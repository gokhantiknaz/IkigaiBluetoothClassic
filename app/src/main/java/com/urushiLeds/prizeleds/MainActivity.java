package com.urushiLeds.prizeleds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.urushiLeds.prizeleds.Class.LocalDataManager;
import com.urushiLeds.prizeleds.Fragment.Fragment1;
import com.urushiLeds.prizeleds.Fragment.Fragment2;
import com.urushiLeds.prizeleds.Fragment.Fragment3;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.urushi.prizeleds.R;
import com.urushiLeds.prizeleds.Fragment.Fragment4;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    //   private ArrayList<Models> modelsArrayList = new ArrayList<>();

    BottomNavigationView bottomNavigationView;

    private FloatingActionButton fab_bottom;
    private static final String TAG = "MainActivity";
    private Fragment fragmentTemp;
    private TextView tv_status;
    private String device_id;
    private byte[] txData = new byte[109];
    private int trial = 0, trial_ack = 0;
    private boolean isTxFull = false;
    BluetoothAdapter bluetoothAdapter;
    SendReceive sendReceive;
    ArrayList<String> bleList = new ArrayList<>();
    ArrayList<String> sendList = new ArrayList<>();
    private InputStream inputStream;
    private OutputStream outputStream;

    static final String DATA_ACK = "S";

    static final int STATE_CONNECTED = 1;
    static final int STATE_CONNECTION_FAILED = 2;
    static final int STATE_MESSAGE_RECEIVED = 3;
    static final int STATE_MESSAGE_NEXTCONNECTION_WAIT = 4;
    static final int STATE_MESSAGE_ACK_WAIT = 5;
    static final int STATE_MESSAGE_WRONG_ACK_RECEIVED = 6;

    private String tempMsg = null, hour, minute;

    private static final UUID ESP32_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice device;
    private BluetoothSocket socket;

    ClientClass clientClass;
    ProgressDialog progress;

    LocalDataManager localDataManager = new LocalDataManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }

        //    modelsArrayList.add(new Models("CUSTOM","Channel 1","Channel 2","Channel 3","Channel 4","Channel 5","Channel 6",6));
        //    modelsArrayList.add(new Models("F-MAJOR","Cool White","Wide Spectrum",null,null,null,null,2));
        //    modelsArrayList.add(new Models("S-MAJOR","Deep Blue","Aqua Sun",null,null,null,null,2));
        //     modelsArrayList.add(new Models("F-MAX","Cool White","Full Spectrum","Reddish White","Blueish White",null,null,4));
        //   modelsArrayList.add(new Models("S-MAX","Deep Blue","Aqua Sun","Magenta","Sky Blue",null,null,4));

        localDataManager.setSharedPreference(getApplicationContext(), "test_model", "false");

        if (findViewById(R.id.frame) != null) {
            if (savedInstanceState != null) {
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.frame, new Fragment4()).commit();
        }

        progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait");

        // Gelen device id ile bluetooth bağlantısını kur.
        if (bleList.size() > 0) {
            device_id = bleList.get(0);
            clientClass = new ClientClass(device_id);
            clientClass.start();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        fragmentTemp = new Fragment2();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragmentTemp, "" + fragmentTemp).commit();
                        break;
                    case R.id.action_back:
                        if (socket.isConnected()) {
                            closeBluetooth();
                        }
                        startActivity(new Intent(MainActivity.this, BluetoothScanActivity.class));
                        finish();
                        break;
                    case R.id.action_test:
                        fragmentTemp = new Fragment3();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragmentTemp, "" + fragmentTemp).commit();
                        break;
                    case R.id.action_home:
                        if (findViewById(R.id.frame) != null) {
                            getSupportFragmentManager().beginTransaction().add(R.id.frame, new Fragment1()).commit();
                        }
//                    case R.id.color_picker:
//                        if (findViewById(R.id.frame) != null) {
//                            getSupportFragmentManager().beginTransaction().add(R.id.frame, new ColorPicker()).commit();
//                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        bluetoothAdapter.disable();
    }

    public void init() {

        checkPermissions(MainActivity.this, this);

        tv_status = findViewById(R.id.tv_status);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        fab_bottom = findViewById(R.id.fab_bottom);
        bottomNavigationView.setBackground(null);
        bleList = getIntent().getStringArrayListExtra("bleDevicesList");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    public static void checkPermissions(Activity activity, Context context){
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_PRIVILEGED,
        };

        if(!hasPermissions(context, PERMISSIONS)){
            ActivityCompat.requestPermissions( activity, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                fragmentTemp = new Fragment2();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragmentTemp).commit();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case STATE_CONNECTED:
                    tv_status.setText("Connected ... ");
                    tv_status.setTextColor(Color.GREEN);
                    Log.e(TAG, "v");
                    if (bleList.size() > sendList.size()) {
                        if (socket.isConnected() && isTxFull) {
                            sendReceive.write(txData);
                            sendList.remove(device.getAddress());
                            Log.e(TAG, "All settings send to all devices");
                            Message message = Message.obtain();
                            message.what = STATE_MESSAGE_ACK_WAIT;
                            handler.sendMessage(message);
                        }
                    } else
                        fab_bottom.setEnabled(true);
                    progress.dismiss();
                    break;
                case STATE_CONNECTION_FAILED:
                    tv_status.setText("Connection Error ... ");
                    tv_status.setTextColor(Color.RED);
                    Log.e(TAG, "Bağlantı Hatası");
                    progress.dismiss();
                    if (socket.isConnected()) {
                        closeBluetooth();
                    }
                    if (sendList.size() < bleList.size()) {
                        Message message = Message.obtain();
                        message.what = STATE_MESSAGE_NEXTCONNECTION_WAIT;
                        handler.sendMessage(message);
                    }
                    break;
                case STATE_MESSAGE_RECEIVED:
                    tempMsg = getMessage(msg);
                    timerHandler.removeCallbacks(timerRunnable);
                    trial = 0;
                    if (tempMsg.equals(DATA_ACK)) {
                        Log.e(TAG, "Mesaj Doğru Alındı");
                        trial_ack = 0;

                        Message message = Message.obtain();
                        message.what = STATE_MESSAGE_NEXTCONNECTION_WAIT;
                        handler.sendMessage(message);

                    } else {
                        Log.e(TAG, "Yanlış doğrulama kodu alındı");
                        trial_ack++;
                        Message message = Message.obtain();
                        message.what = STATE_MESSAGE_WRONG_ACK_RECEIVED;
                        handler.sendMessage(message);
                    }
                    break;
                case STATE_MESSAGE_NEXTCONNECTION_WAIT:
                    String test_model = localDataManager.getSharedPreference(getApplicationContext(), "test_model", "false");
                    boolean isSent = sendList.size() > 0;
                    if (isSent) {
                        Log.e(TAG, "Diğer cihaza bağlanıyor ...");
                        tv_status.setText("Connecting...");
                        tv_status.setTextColor(getResources().getColor(R.color.accent));
                        progress =
                                ProgressDialog.show(MainActivity.this, "Connecting to other leds..", "PLease Wait");
                        // Bluetooth bağlantısını kes.
                        if (socket.isConnected()) {
                            closeBluetooth();
                        }
                        Log.e(TAG, device.getAddress());
                        device_id = sendList.get(0);
                        clientClass = new ClientClass(device_id);
                        clientClass.start();
                        Log.e(TAG, device.getAddress());
                    } else if (socket.isConnected()) {
                        Log.e(TAG, "Tüm cihazlara veriler gönderildi.");
                        Toast.makeText(getApplicationContext(), "Settings send to all devices", Toast.LENGTH_LONG).show();
                        fab_bottom.setEnabled(true);
                        tv_status.setText("Connected");
                        tv_status.setTextColor(Color.GREEN);
                    }
                    break;
                case STATE_MESSAGE_ACK_WAIT:
                    Log.e(TAG, "Doğrulama kodu bekleniyor ...");
                    tv_status.setText("Waiting for verification");
                    tv_status.setTextColor(getResources().getColor(R.color.accent));
                    trial++;
                    // 30.sn ACK gelmesini bekle
                    timerHandler.postDelayed(timerRunnable, 30000);
                    break;
                case STATE_MESSAGE_WRONG_ACK_RECEIVED:
                    Log.e(TAG, "Yanlış Doğrulama kodu alındı.");
                    //todo yanlış ACK geldiğinde burası yapılacak !!!
                    if (trial_ack < 3) {
                        sendReceive.write(txData);
                        Message message = Message.obtain();
                        message.what = STATE_MESSAGE_ACK_WAIT;
                        handler.sendMessage(message);
                    } else {
                        trial_ack = 0;
                        Message message = Message.obtain();
                        message.what = STATE_MESSAGE_NEXTCONNECTION_WAIT;
                        handler.sendMessage(message);
                    }
                    break;
            }
            return false;
        }
    });

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (trial < 3) {
                try {
                    sendReceive.write(txData);
                    Message message = Message.obtain();
                    message.what = STATE_MESSAGE_ACK_WAIT;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            } else {
                trial = 0;
                if (socket.isConnected()) {
                    closeBluetooth();
                }
                if (sendList.size() > 0) {
                    try {
                        device_id = sendList.get(0);
                        clientClass = new ClientClass(device_id);
                        clientClass.start();
                        sendReceive.write(txData);
                        Message message = Message.obtain();
                        message.what = STATE_MESSAGE_ACK_WAIT;
                        handler.sendMessage(message);

                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }
                //timerHandler.removeCallbacks(timerRunnable);
            }
        }
    };

    public void fab_bottom(View view) {
        // anlık saat ve dakika bilgisini al
        getDateTime();

        sendList = (ArrayList<String>) bleList.clone();

        fab_bottom.setEnabled(false);
        if (!socket.isConnected()) {
            startActivity(new Intent(MainActivity.this, BluetoothScanActivity.class));
            finish();
        }

        String model = localDataManager.getSharedPreference(getApplicationContext(), "model", "");
        String test_model = localDataManager.getSharedPreference(getApplicationContext(), "test_model", "false");
        Log.e(TAG, "fab_bottom: hour" + hour);
        Log.e(TAG, "fab_bottom: minute" + minute);
        txData[54] = Byte.parseByte(hour);
        txData[55] = Byte.parseByte(minute);
        if (test_model.equals("test")) {
            txData[0] = 0x65;
            String longManuel = localDataManager.getSharedPreference(getApplicationContext(), "longManuel", "false");

            txData[1] = 0x06;
            txData[2] = 0xA;

            String test_f1 = localDataManager.getSharedPreference(getApplicationContext(), "testf1", "0");
            String test_f2 = localDataManager.getSharedPreference(getApplicationContext(), "testf2", "0");
            String test_f3 = localDataManager.getSharedPreference(getApplicationContext(), "testf3", "0");
            String test_f4 = localDataManager.getSharedPreference(getApplicationContext(), "testf4", "0");
            String test_f5 = localDataManager.getSharedPreference(getApplicationContext(), "testf5", "0");
            String test_f6 = localDataManager.getSharedPreference(getApplicationContext(), "testf6", "0");
            String test_f7 = localDataManager.getSharedPreference(getApplicationContext(), "testf7", "0");
            String test_f8 = localDataManager.getSharedPreference(getApplicationContext(), "testf8", "0");

            txData[3] = (byte) Integer.parseInt(test_f1);
            txData[4] = (byte) Integer.parseInt(test_f2);
            txData[5] = (byte) Integer.parseInt(test_f3);
            txData[6] = (byte) Integer.parseInt(test_f4);

            txData[7] = (byte) Integer.parseInt(test_f5);
            txData[8] = (byte) Integer.parseInt(test_f6);
            //txData_Test[9] = (byte) Integer.parseInt(test_f7);
            //txData_Test[10] = (byte) Integer.parseInt(test_f8);

            // saatleri boşalt
            for (int i = 9; i < 80; i++) {
                txData[i] = 0x00;
            }
            txData[82] = 0x66;
        } else {
            if (model.equals("RGBW")) {
                txData[0] = 0x65;
                txData[1] = 0x01;
                txData[2] = 0x01;

                String fmax_cw_gd_f = "0";
                String fmax_cw_gd_h = localDataManager.getSharedPreference(getApplicationContext(), "RGBWREDgdh", "07");
                String fmax_cw_gd_m = localDataManager.getSharedPreference(getApplicationContext(), "RGBWREDgdm", "00");
                txData[3] = (byte) Integer.parseInt(fmax_cw_gd_f);
                txData[4] = (byte) Integer.parseInt(fmax_cw_gd_h);
                txData[5] = (byte) Integer.parseInt(fmax_cw_gd_m);
                String fmax_cw_g_f = localDataManager.getSharedPreference(getApplicationContext(), "RGBWREDf2", "0");
                String fmax_cw_g_h = localDataManager.getSharedPreference(getApplicationContext(), "RGBWREDgh", "12");
                String fmax_cw_g_m = localDataManager.getSharedPreference(getApplicationContext(), "RGBWREDgm", "00");
                txData[6] = (byte) Integer.parseInt(fmax_cw_g_f);
                txData[7] = (byte) Integer.parseInt(fmax_cw_g_h);
                txData[8] = (byte) Integer.parseInt(fmax_cw_g_m);
                String fmax_cw_gb_f = localDataManager.getSharedPreference(getApplicationContext(), "RGBWREDf3", "0");
                String fmax_cw_gb_h = localDataManager.getSharedPreference(getApplicationContext(), "RGBWREDgbh", "17");
                String fmax_cw_gb_m = localDataManager.getSharedPreference(getApplicationContext(), "RGBWREDgbm", "00");
                txData[9] = (byte) Integer.parseInt(fmax_cw_gb_f);
                txData[10] = (byte) Integer.parseInt(fmax_cw_gb_h);
                txData[11] = (byte) Integer.parseInt(fmax_cw_gb_m);
                String fmax_cw_a_f = "0";
                String fmax_cw_a_h = localDataManager.getSharedPreference(getApplicationContext(), "RGBWREDah", "22");
                String fmax_cw_a_m = localDataManager.getSharedPreference(getApplicationContext(), "RGBWREDam", "00");
                txData[12] = (byte) Integer.parseInt(fmax_cw_a_f);
                txData[13] = (byte) Integer.parseInt(fmax_cw_a_h);
                txData[14] = (byte) Integer.parseInt(fmax_cw_a_m);

                txData[15] = 0x02;
                String fmax_fs_gd_f = "0";
                String fmax_fs_gd_h = localDataManager.getSharedPreference(getApplicationContext(), "RGBWGREENgdh", "07");
                String fmax_fs_gd_m = localDataManager.getSharedPreference(getApplicationContext(), "RGBWGREENgdm", "00");
                txData[16] = (byte) Integer.parseInt(fmax_fs_gd_f);
                txData[17] = (byte) Integer.parseInt(fmax_fs_gd_h);
                txData[18] = (byte) Integer.parseInt(fmax_fs_gd_m);
                String fmax_fs_g_f = localDataManager.getSharedPreference(getApplicationContext(), "RGBWGREENf2", "0");
                String fmax_fs_g_h = localDataManager.getSharedPreference(getApplicationContext(), "RGBWGREENgh", "12");
                String fmax_fs_g_m = localDataManager.getSharedPreference(getApplicationContext(), "RGBWGREENgm", "00");
                txData[19] = (byte) Integer.parseInt(fmax_fs_g_f);
                txData[20] = (byte) Integer.parseInt(fmax_fs_g_h);
                txData[21] = (byte) Integer.parseInt(fmax_fs_g_m);
                String fmax_fs_gb_f = localDataManager.getSharedPreference(getApplicationContext(), "RGBWGREENf3", "0");
                String fmax_fs_gb_h = localDataManager.getSharedPreference(getApplicationContext(), "RGBWGREENgbh", "17");
                String fmax_fs_gb_m = localDataManager.getSharedPreference(getApplicationContext(), "RGBWGREENgbm", "00");
                txData[22] = (byte) Integer.parseInt(fmax_fs_gb_f);
                txData[23] = (byte) Integer.parseInt(fmax_fs_gb_h);
                txData[24] = (byte) Integer.parseInt(fmax_fs_gb_m);
                String fmax_fs_a_f = "0";
                String fmax_fs_a_h = localDataManager.getSharedPreference(getApplicationContext(), "RGBWGREENah", "22");
                String fmax_fs_a_m = localDataManager.getSharedPreference(getApplicationContext(), "RGBWGREENam", "00");
                txData[25] = (byte) Integer.parseInt(fmax_fs_a_f);
                txData[26] = (byte) Integer.parseInt(fmax_fs_a_h);
                txData[27] = (byte) Integer.parseInt(fmax_fs_a_m);

                txData[28] = 0x03;
                String fmax_rw_gd_f = "0";
                String fmax_rw_gd_h = localDataManager.getSharedPreference(getApplicationContext(), "RGBWBLUEgdh", "07");
                String fmax_rw_gd_m = localDataManager.getSharedPreference(getApplicationContext(), "RGBWBLUEgdm", "00");
                txData[29] = (byte) Integer.parseInt(fmax_rw_gd_f);
                txData[30] = (byte) Integer.parseInt(fmax_rw_gd_h);
                txData[31] = (byte) Integer.parseInt(fmax_rw_gd_m);
                String fmax_rw_g_f = localDataManager.getSharedPreference(getApplicationContext(), "RGBWBLUEf2", "0");
                String fmax_rw_g_h = localDataManager.getSharedPreference(getApplicationContext(), "RGBWBLUEgh", "12");
                String fmax_rw_g_m = localDataManager.getSharedPreference(getApplicationContext(), "RGBWBLUEgm", "00");
                txData[32] = (byte) Integer.parseInt(fmax_rw_g_f);
                txData[33] = (byte) Integer.parseInt(fmax_rw_g_h);
                txData[34] = (byte) Integer.parseInt(fmax_rw_g_m);
                String fmax_rw_gb_f = localDataManager.getSharedPreference(getApplicationContext(), "RGBWBLUEf3", "0");
                String fmax_rw_gb_h = localDataManager.getSharedPreference(getApplicationContext(), "RGBWBLUEgbh", "17");
                String fmax_rw_gb_m = localDataManager.getSharedPreference(getApplicationContext(), "RGBWBLUEgbm", "00");
                txData[35] = (byte) Integer.parseInt(fmax_rw_gb_f);
                txData[36] = (byte) Integer.parseInt(fmax_rw_gb_h);
                txData[37] = (byte) Integer.parseInt(fmax_rw_gb_m);
                String fmax_rw_a_f = "0";
                String fmax_rw_a_h = localDataManager.getSharedPreference(getApplicationContext(), "RGBWBLUEah", "22");
                String fmax_rw_a_m = localDataManager.getSharedPreference(getApplicationContext(), "RGBWBLUEam", "00");
                txData[38] = (byte) Integer.parseInt(fmax_rw_a_f);
                txData[39] = (byte) Integer.parseInt(fmax_rw_a_h);
                txData[40] = (byte) Integer.parseInt(fmax_rw_a_m);

                txData[41] = 0x04;
                String fmax_bw_gd_f = "0";
                String fmax_bw_gd_h = localDataManager.getSharedPreference(getApplicationContext(), "RGBWHITEgdh", "07");
                String fmax_bw_gd_m = localDataManager.getSharedPreference(getApplicationContext(), "RGBWHITEgdm", "00");
                txData[42] = (byte) Integer.parseInt(fmax_bw_gd_f);
                txData[43] = (byte) Integer.parseInt(fmax_bw_gd_h);
                txData[44] = (byte) Integer.parseInt(fmax_bw_gd_m);
                String fmax_bw_g_f = localDataManager.getSharedPreference(getApplicationContext(), "RGBWHITEf2", "0");
                String fmax_bw_g_h = localDataManager.getSharedPreference(getApplicationContext(), "RGBWHITEgh", "12");
                String fmax_bw_g_m = localDataManager.getSharedPreference(getApplicationContext(), "RGBWHITEgm", "00");
                txData[45] = (byte) Integer.parseInt(fmax_bw_g_f);
                txData[46] = (byte) Integer.parseInt(fmax_bw_g_h);
                txData[47] = (byte) Integer.parseInt(fmax_bw_g_m);
                String fmax_bw_gb_f = localDataManager.getSharedPreference(getApplicationContext(), "RGBWHITEf3", "0");
                String fmax_bw_gb_h = localDataManager.getSharedPreference(getApplicationContext(), "RGBWHITEgbh", "17");
                String fmax_bw_gb_m = localDataManager.getSharedPreference(getApplicationContext(), "RGBWHITEgbm", "00");
                txData[48] = (byte) Integer.parseInt(fmax_bw_gb_f);
                txData[49] = (byte) Integer.parseInt(fmax_bw_gb_h);
                txData[50] = (byte) Integer.parseInt(fmax_bw_gb_m);
                String fmax_bw_a_f = "0";
                String fmax_bw_a_h = localDataManager.getSharedPreference(getApplicationContext(), "RGBWHITEah", "22");
                String fmax_bw_a_m = localDataManager.getSharedPreference(getApplicationContext(), "RGBWHITEam", "00");
                txData[51] = (byte) Integer.parseInt(fmax_bw_a_f);
                txData[52] = (byte) Integer.parseInt(fmax_bw_a_h);
                txData[53] = (byte) Integer.parseInt(fmax_bw_a_m);

                txData[56] = 0x66;

            } else if (model.equals("SPECT+")) {
                txData[0] = 0x65;
                txData[1] = 0x02;
                txData[2] = 0x01;

                String fmax_cw_gd_f = "0";
                String fmax_cw_gd_h = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+5000Kgdh", "07");
                String fmax_cw_gd_m = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+5000Kgdm", "00");
                txData[3] = (byte) Integer.parseInt(fmax_cw_gd_f);
                txData[4] = (byte) Integer.parseInt(fmax_cw_gd_h);
                txData[5] = (byte) Integer.parseInt(fmax_cw_gd_m);
                String fmax_cw_g_f = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+5000Kf2", "0");
                String fmax_cw_g_h = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+5000Kgh", "12");
                String fmax_cw_g_m = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+5000Kgm", "00");
                txData[6] = (byte) Integer.parseInt(fmax_cw_g_f);
                txData[7] = (byte) Integer.parseInt(fmax_cw_g_h);
                txData[8] = (byte) Integer.parseInt(fmax_cw_g_m);
                String fmax_cw_gb_f = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+5000Kf3", "0");
                String fmax_cw_gb_h = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+5000Kgbh", "17");
                String fmax_cw_gb_m = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+5000Kgbm", "00");
                txData[9] = (byte) Integer.parseInt(fmax_cw_gb_f);
                txData[10] = (byte) Integer.parseInt(fmax_cw_gb_h);
                txData[11] = (byte) Integer.parseInt(fmax_cw_gb_m);
                String fmax_cw_a_f = "0";
                String fmax_cw_a_h = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+5000Kah", "22");
                String fmax_cw_a_m = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+5000Kam", "00");
                txData[12] = (byte) Integer.parseInt(fmax_cw_a_f);
                txData[13] = (byte) Integer.parseInt(fmax_cw_a_h);
                txData[14] = (byte) Integer.parseInt(fmax_cw_a_m);

                txData[15] = 0x02;
                String fmax_fs_gd_f = "0";
                String fmax_fs_gd_h = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+6500Kgdh", "07");
                String fmax_fs_gd_m = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+6500Kgdm", "00");
                txData[16] = (byte) Integer.parseInt(fmax_fs_gd_f);
                txData[17] = (byte) Integer.parseInt(fmax_fs_gd_h);
                txData[18] = (byte) Integer.parseInt(fmax_fs_gd_m);
                String fmax_fs_g_f = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+6500Kf2", "0");
                String fmax_fs_g_h = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+6500Kgh", "12");
                String fmax_fs_g_m = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+6500Kgm", "00");
                txData[19] = (byte) Integer.parseInt(fmax_fs_g_f);
                txData[20] = (byte) Integer.parseInt(fmax_fs_g_h);
                txData[21] = (byte) Integer.parseInt(fmax_fs_g_m);
                String fmax_fs_gb_f = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+6500Kf3", "0");
                String fmax_fs_gb_h = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+6500Kgbh", "17");
                String fmax_fs_gb_m = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+6500Kgbm", "00");
                txData[22] = (byte) Integer.parseInt(fmax_fs_gb_f);
                txData[23] = (byte) Integer.parseInt(fmax_fs_gb_h);
                txData[24] = (byte) Integer.parseInt(fmax_fs_gb_m);
                String fmax_fs_a_f = "0";
                String fmax_fs_a_h = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+6500Kah", "22");
                String fmax_fs_a_m = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+6500Kam", "00");
                txData[25] = (byte) Integer.parseInt(fmax_fs_a_f);
                txData[26] = (byte) Integer.parseInt(fmax_fs_a_h);
                txData[27] = (byte) Integer.parseInt(fmax_fs_a_m);

                txData[28] = 0x03;
                String fmax_rw_gd_f = "0";
                String fmax_rw_gd_h = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+9000Kgdh", "07");
                String fmax_rw_gd_m = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+9000Kgdm", "00");
                txData[29] = (byte) Integer.parseInt(fmax_rw_gd_f);
                txData[30] = (byte) Integer.parseInt(fmax_rw_gd_h);
                txData[31] = (byte) Integer.parseInt(fmax_rw_gd_m);
                String fmax_rw_g_f = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+9000Kf2", "0");
                String fmax_rw_g_h = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+9000Kgh", "12");
                String fmax_rw_g_m = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+9000Kgm", "00");
                txData[32] = (byte) Integer.parseInt(fmax_rw_g_f);
                txData[33] = (byte) Integer.parseInt(fmax_rw_g_h);
                txData[34] = (byte) Integer.parseInt(fmax_rw_g_m);
                String fmax_rw_gb_f = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+9000Kf3", "0");
                String fmax_rw_gb_h = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+9000Kgbh", "17");
                String fmax_rw_gb_m = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+9000Kgbm", "00");
                txData[35] = (byte) Integer.parseInt(fmax_rw_gb_f);
                txData[36] = (byte) Integer.parseInt(fmax_rw_gb_h);
                txData[37] = (byte) Integer.parseInt(fmax_rw_gb_m);
                String fmax_rw_a_f = "0";
                String fmax_rw_a_h = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+9000Kah", "22");
                String fmax_rw_a_m = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+9000Kam", "00");
                txData[38] = (byte) Integer.parseInt(fmax_rw_a_f);
                txData[39] = (byte) Integer.parseInt(fmax_rw_a_h);
                txData[40] = (byte) Integer.parseInt(fmax_rw_a_m);

                txData[41] = 0x04;
                String fmax_bw_gd_f = "0";
                String fmax_bw_gd_h = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+MAGENTAgdh", "07");
                String fmax_bw_gd_m = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+MAGENTAgdm", "00");
                txData[42] = (byte) Integer.parseInt(fmax_bw_gd_f);
                txData[43] = (byte) Integer.parseInt(fmax_bw_gd_h);
                txData[44] = (byte) Integer.parseInt(fmax_bw_gd_m);
                String fmax_bw_g_f = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+MAGENTAf2", "0");
                String fmax_bw_g_h = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+MAGENTAgh", "12");
                String fmax_bw_g_m = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+MAGENTAgm", "00");
                txData[45] = (byte) Integer.parseInt(fmax_bw_g_f);
                txData[46] = (byte) Integer.parseInt(fmax_bw_g_h);
                txData[47] = (byte) Integer.parseInt(fmax_bw_g_m);
                String fmax_bw_gb_f = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+MAGENTAf3", "0");
                String fmax_bw_gb_h = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+MAGENTAgbh", "17");
                String fmax_bw_gb_m = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+MAGENTAgbm", "00");
                txData[48] = (byte) Integer.parseInt(fmax_bw_gb_f);
                txData[49] = (byte) Integer.parseInt(fmax_bw_gb_h);
                txData[50] = (byte) Integer.parseInt(fmax_bw_gb_m);
                String fmax_bw_a_f = "0";
                String fmax_bw_a_h = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+MAGENTAah", "22");
                String fmax_bw_a_m = localDataManager.getSharedPreference(getApplicationContext(), "SPECTRUM+MAGENTAam", "00");
                txData[51] = (byte) Integer.parseInt(fmax_bw_a_f);
                txData[52] = (byte) Integer.parseInt(fmax_bw_a_h);
                txData[53] = (byte) Integer.parseInt(fmax_bw_a_m);

                txData[56] = 0x66;
            } else if (model.equals("WIDE SPECT")) {
                txData[0] = 0x65;
                txData[1] = 0x03;
                txData[2] = 0x01;

                String fmax_cw_gd_f = "0";
                String fmax_cw_gd_h = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT REDDISH WHITEgdh", "07");
                String fmax_cw_gd_m = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT REDDISH WHITEgdm", "00");
                txData[3] = (byte) Integer.parseInt(fmax_cw_gd_f);
                txData[4] = (byte) Integer.parseInt(fmax_cw_gd_h);
                txData[5] = (byte) Integer.parseInt(fmax_cw_gd_m);
                String fmax_cw_g_f = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT REDDISH WHITEf2", "0");
                String fmax_cw_g_h = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT REDDISH WHITEgh", "12");
                String fmax_cw_g_m = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT REDDISH WHITEgm", "00");
                txData[6] = (byte) Integer.parseInt(fmax_cw_g_f);
                txData[7] = (byte) Integer.parseInt(fmax_cw_g_h);
                txData[8] = (byte) Integer.parseInt(fmax_cw_g_m);
                String fmax_cw_gb_f = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT REDDISH WHITEf3", "0");
                String fmax_cw_gb_h = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT REDDISH WHITEgbh", "17");
                String fmax_cw_gb_m = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT REDDISH WHITEgbm", "00");
                txData[9] = (byte) Integer.parseInt(fmax_cw_gb_f);
                txData[10] = (byte) Integer.parseInt(fmax_cw_gb_h);
                txData[11] = (byte) Integer.parseInt(fmax_cw_gb_m);
                String fmax_cw_a_f = "0";
                String fmax_cw_a_h = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT REDDISH WHITEah", "22");
                String fmax_cw_a_m = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT REDDISH WHITEam", "00");
                txData[12] = (byte) Integer.parseInt(fmax_cw_a_f);
                txData[13] = (byte) Integer.parseInt(fmax_cw_a_h);
                txData[14] = (byte) Integer.parseInt(fmax_cw_a_m);

                txData[15] = 0x02;
                String fmax_fs_gd_f = "0";
                String fmax_fs_gd_h = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT GREENISH WHITEgdh", "07");
                String fmax_fs_gd_m = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT GREENISH WHITEgdm", "00");
                txData[16] = (byte) Integer.parseInt(fmax_fs_gd_f);
                txData[17] = (byte) Integer.parseInt(fmax_fs_gd_h);
                txData[18] = (byte) Integer.parseInt(fmax_fs_gd_m);
                String fmax_fs_g_f = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT GREENISH WHITEf2", "0");
                String fmax_fs_g_h = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT GREENISH WHITEgh", "12");
                String fmax_fs_g_m = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT GREENISH WHITEgm", "00");
                txData[19] = (byte) Integer.parseInt(fmax_fs_g_f);
                txData[20] = (byte) Integer.parseInt(fmax_fs_g_h);
                txData[21] = (byte) Integer.parseInt(fmax_fs_g_m);
                String fmax_fs_gb_f = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT GREENISH WHITEf3", "0");
                String fmax_fs_gb_h = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT GREENISH WHITEgbh", "17");
                String fmax_fs_gb_m = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT GREENISH WHITEgbm", "00");
                txData[22] = (byte) Integer.parseInt(fmax_fs_gb_f);
                txData[23] = (byte) Integer.parseInt(fmax_fs_gb_h);
                txData[24] = (byte) Integer.parseInt(fmax_fs_gb_m);
                String fmax_fs_a_f = "0";
                String fmax_fs_a_h = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT GREENISH WHITEah", "22");
                String fmax_fs_a_m = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT GREENISH WHITEam", "00");
                txData[25] = (byte) Integer.parseInt(fmax_fs_a_f);
                txData[26] = (byte) Integer.parseInt(fmax_fs_a_h);
                txData[27] = (byte) Integer.parseInt(fmax_fs_a_m);

                txData[28] = 0x03;
                String fmax_rw_gd_f = "0";
                String fmax_rw_gd_h = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT BLUEISH WHITEgdh", "07");
                String fmax_rw_gd_m = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT BLUEISH WHITEgdm", "00");
                txData[29] = (byte) Integer.parseInt(fmax_rw_gd_f);
                txData[30] = (byte) Integer.parseInt(fmax_rw_gd_h);
                txData[31] = (byte) Integer.parseInt(fmax_rw_gd_m);
                String fmax_rw_g_f = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT BLUEISH WHITEf2", "0");
                String fmax_rw_g_h = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT BLUEISH WHITEgh", "12");
                String fmax_rw_g_m = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT BLUEISH WHITEgm", "00");
                txData[32] = (byte) Integer.parseInt(fmax_rw_g_f);
                txData[33] = (byte) Integer.parseInt(fmax_rw_g_h);
                txData[34] = (byte) Integer.parseInt(fmax_rw_g_m);
                String fmax_rw_gb_f = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT BLUEISH WHITEf3", "0");
                String fmax_rw_gb_h = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT BLUEISH WHITEgbh", "17");
                String fmax_rw_gb_m = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT BLUEISH WHITEgbm", "00");
                txData[35] = (byte) Integer.parseInt(fmax_rw_gb_f);
                txData[36] = (byte) Integer.parseInt(fmax_rw_gb_h);
                txData[37] = (byte) Integer.parseInt(fmax_rw_gb_m);
                String fmax_rw_a_f = "0";
                String fmax_rw_a_h = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT BLUEISH WHITEah", "22");
                String fmax_rw_a_m = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT BLUEISH WHITEam", "00");
                txData[38] = (byte) Integer.parseInt(fmax_rw_a_f);
                txData[39] = (byte) Integer.parseInt(fmax_rw_a_h);
                txData[40] = (byte) Integer.parseInt(fmax_rw_a_m);

                txData[41] = 0x04;
                String fmax_bw_gd_f = "0";
                String fmax_bw_gd_h = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT SUNLIKE WHITEgdh", "07");
                String fmax_bw_gd_m = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT SUNLIKE WHITEgdm", "00");
                txData[42] = (byte) Integer.parseInt(fmax_bw_gd_f);
                txData[43] = (byte) Integer.parseInt(fmax_bw_gd_h);
                txData[44] = (byte) Integer.parseInt(fmax_bw_gd_m);
                String fmax_bw_g_f = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT SUNLIKE WHITEf2", "0");
                String fmax_bw_g_h = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT SUNLIKE WHITEgh", "12");
                String fmax_bw_g_m = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT SUNLIKE WHITEgm", "00");
                txData[45] = (byte) Integer.parseInt(fmax_bw_g_f);
                txData[46] = (byte) Integer.parseInt(fmax_bw_g_h);
                txData[47] = (byte) Integer.parseInt(fmax_bw_g_m);
                String fmax_bw_gb_f = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT SUNLIKE WHITEf3", "0");
                String fmax_bw_gb_h = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT SUNLIKE WHITEgbh", "17");
                String fmax_bw_gb_m = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT SUNLIKE WHITEgbm", "00");
                txData[48] = (byte) Integer.parseInt(fmax_bw_gb_f);
                txData[49] = (byte) Integer.parseInt(fmax_bw_gb_h);
                txData[50] = (byte) Integer.parseInt(fmax_bw_gb_m);
                String fmax_bw_a_f = "0";
                String fmax_bw_a_h = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT SUNLIKE WHITEah", "22");
                String fmax_bw_a_m = localDataManager.getSharedPreference(getApplicationContext(), "WIDE SPECT SUNLIKE WHITEam", "00");
                txData[51] = (byte) Integer.parseInt(fmax_bw_a_f);
                txData[52] = (byte) Integer.parseInt(fmax_bw_a_h);
                txData[53] = (byte) Integer.parseInt(fmax_bw_a_m);

                txData[56] = 0x66;
            } else if (model.equals("UV+")) {
                txData[0] = 0x65;
                txData[1] = 0x04;
                txData[2] = 0x01;
                String smax_db_gd_f = "0";
                String smax_db_gd_h = localDataManager.getSharedPreference(getApplicationContext(), "UV+5000Kgdh", "07");
                String smax_db_gd_m = localDataManager.getSharedPreference(getApplicationContext(), "UV+5000Kgdm", "00");
                txData[3] = (byte) Integer.parseInt(smax_db_gd_f);
                txData[4] = (byte) Integer.parseInt(smax_db_gd_h);
                txData[5] = (byte) Integer.parseInt(smax_db_gd_m);
                String smax_db_g_f = localDataManager.getSharedPreference(getApplicationContext(), "UV+5000Kf2", "0");
                String smax_db_g_h = localDataManager.getSharedPreference(getApplicationContext(), "UV+5000Kgh", "12");
                String smax_db_g_m = localDataManager.getSharedPreference(getApplicationContext(), "UV+5000Kgm", "00");
                txData[6] = (byte) Integer.parseInt(smax_db_g_f);
                txData[7] = (byte) Integer.parseInt(smax_db_g_h);
                txData[8] = (byte) Integer.parseInt(smax_db_g_m);
                String smax_db_gb_f = localDataManager.getSharedPreference(getApplicationContext(), "UV+5000Kf3", "0");
                String smax_db_gb_h = localDataManager.getSharedPreference(getApplicationContext(), "UV+5000Kgbh", "17");
                String smax_db_gb_m = localDataManager.getSharedPreference(getApplicationContext(), "UV+5000Kgbm", "00");
                txData[9] = (byte) Integer.parseInt(smax_db_gb_f);
                txData[10] = (byte) Integer.parseInt(smax_db_gb_h);
                txData[11] = (byte) Integer.parseInt(smax_db_gb_m);
                String smax_db_a_f = "0";
                String smax_db_a_h = localDataManager.getSharedPreference(getApplicationContext(), "UV+5000Kah", "22");
                String smax_db_a_m = localDataManager.getSharedPreference(getApplicationContext(), "UV+5000Kam", "00");
                txData[12] = (byte) Integer.parseInt(smax_db_a_f);
                txData[13] = (byte) Integer.parseInt(smax_db_a_h);
                txData[14] = (byte) Integer.parseInt(smax_db_a_m);
                txData[15] = 0x02;
                String smax_as_gd_f = "0";
                String smax_as_gd_h = localDataManager.getSharedPreference(getApplicationContext(), "UV+6500Kgdh", "07");
                String smax_as_gd_m = localDataManager.getSharedPreference(getApplicationContext(), "UV+6500Kgdm", "00");
                txData[16] = (byte) Integer.parseInt(smax_as_gd_f);
                txData[17] = (byte) Integer.parseInt(smax_as_gd_h);
                txData[18] = (byte) Integer.parseInt(smax_as_gd_m);
                String smax_as_g_f = localDataManager.getSharedPreference(getApplicationContext(), "UV+6500Kf2", "0");
                String smax_as_g_h = localDataManager.getSharedPreference(getApplicationContext(), "UV+6500Kgh", "12");
                String smax_as_g_m = localDataManager.getSharedPreference(getApplicationContext(), "UV+6500Kgm", "00");
                txData[19] = (byte) Integer.parseInt(smax_as_g_f);
                txData[20] = (byte) Integer.parseInt(smax_as_g_h);
                txData[21] = (byte) Integer.parseInt(smax_as_g_m);
                String smax_as_gb_f = localDataManager.getSharedPreference(getApplicationContext(), "UV+6500Kf3", "0");
                String smax_as_gb_h = localDataManager.getSharedPreference(getApplicationContext(), "UV+6500Kgbh", "17");
                String smax_as_gb_m = localDataManager.getSharedPreference(getApplicationContext(), "UV+6500Kgbm", "00");
                txData[22] = (byte) Integer.parseInt(smax_as_gb_f);
                txData[23] = (byte) Integer.parseInt(smax_as_gb_h);
                txData[24] = (byte) Integer.parseInt(smax_as_gb_m);
                String smax_as_a_f = "0";
                String smax_as_a_h = localDataManager.getSharedPreference(getApplicationContext(), "UV+6500Kah", "22");
                String smax_as_a_m = localDataManager.getSharedPreference(getApplicationContext(), "UV+6500Kam", "00");
                txData[25] = (byte) Integer.parseInt(smax_as_a_f);
                txData[26] = (byte) Integer.parseInt(smax_as_a_h);
                txData[27] = (byte) Integer.parseInt(smax_as_a_m);
                txData[28] = 0x03;
                String smax_m_gd_f = "0";
                String smax_m_gd_h = localDataManager.getSharedPreference(getApplicationContext(), "UV+9000Kgdh", "07");
                String smax_m_gd_m = localDataManager.getSharedPreference(getApplicationContext(), "UV+9000Kgdm", "00");
                txData[29] = (byte) Integer.parseInt(smax_m_gd_f);
                txData[30] = (byte) Integer.parseInt(smax_m_gd_h);
                txData[31] = (byte) Integer.parseInt(smax_m_gd_m);
                String smax_m_g_f = localDataManager.getSharedPreference(getApplicationContext(), "UV+9000Kf2", "0");
                String smax_m_g_h = localDataManager.getSharedPreference(getApplicationContext(), "UV+9000Kgh", "12");
                String smax_m_g_m = localDataManager.getSharedPreference(getApplicationContext(), "UV+9000Kgm", "00");
                txData[32] = (byte) Integer.parseInt(smax_m_g_f);
                txData[33] = (byte) Integer.parseInt(smax_m_g_h);
                txData[34] = (byte) Integer.parseInt(smax_m_g_m);
                String smax_m_gb_f = localDataManager.getSharedPreference(getApplicationContext(), "UV+9000Kf3", "0");
                String smax_m_gb_h = localDataManager.getSharedPreference(getApplicationContext(), "UV+9000Kgbh", "17");
                String smax_m_gb_m = localDataManager.getSharedPreference(getApplicationContext(), "UV+9000Kgbm", "00");
                txData[35] = (byte) Integer.parseInt(smax_m_gb_f);
                txData[36] = (byte) Integer.parseInt(smax_m_gb_h);
                txData[37] = (byte) Integer.parseInt(smax_m_gb_m);
                String smax_m_a_f = "0";
                String smax_m_a_h = localDataManager.getSharedPreference(getApplicationContext(), "UV+9000Kah", "22");
                String smax_m_a_m = localDataManager.getSharedPreference(getApplicationContext(), "UV+9000Kam", "00");
                txData[38] = (byte) Integer.parseInt(smax_m_a_f);
                txData[39] = (byte) Integer.parseInt(smax_m_a_h);
                txData[40] = (byte) Integer.parseInt(smax_m_a_m);
                txData[41] = 0x04;
                String smax_sb_gd_f = "0";
                String smax_sb_gd_h = localDataManager.getSharedPreference(getApplicationContext(), "UV+UV PLUSgdh", "07");
                String smax_sb_gd_m = localDataManager.getSharedPreference(getApplicationContext(), "UV+UV PLUSgdm", "00");
                txData[42] = (byte) Integer.parseInt(smax_sb_gd_f);
                txData[43] = (byte) Integer.parseInt(smax_sb_gd_h);
                txData[44] = (byte) Integer.parseInt(smax_sb_gd_m);
                String smax_sb_g_f = localDataManager.getSharedPreference(getApplicationContext(), "UV+UV PLUSf2", "0");
                String smax_sb_g_h = localDataManager.getSharedPreference(getApplicationContext(), "UV+UV PLUSgh", "12");
                String smax_sb_g_m = localDataManager.getSharedPreference(getApplicationContext(), "UV+UV PLUSgm", "00");
                txData[45] = (byte) Integer.parseInt(smax_sb_g_f);
                txData[46] = (byte) Integer.parseInt(smax_sb_g_h);
                txData[47] = (byte) Integer.parseInt(smax_sb_g_m);
                String smax_sb_gb_f = localDataManager.getSharedPreference(getApplicationContext(), "UV+UV PLUSf3", "0");
                String smax_sb_gb_h = localDataManager.getSharedPreference(getApplicationContext(), "UV+UV PLUSgbh", "17");
                String smax_sb_gb_m = localDataManager.getSharedPreference(getApplicationContext(), "UV+UV PLUSgbm", "00");
                txData[48] = (byte) Integer.parseInt(smax_sb_gb_f);
                txData[49] = (byte) Integer.parseInt(smax_sb_gb_h);
                txData[50] = (byte) Integer.parseInt(smax_sb_gb_m);
                String smax_sb_a_f = "0";
                String smax_sb_a_h = localDataManager.getSharedPreference(getApplicationContext(), "UV+UV PLUSah", "22");
                String smax_sb_a_m = localDataManager.getSharedPreference(getApplicationContext(), "UV+UV PLUSam", "00");
                txData[51] = (byte) Integer.parseInt(smax_sb_a_f);
                txData[52] = (byte) Integer.parseInt(smax_sb_a_h);
                txData[53] = (byte) Integer.parseInt(smax_sb_a_m);
                txData[56] = 0x66;

            } else {
                //custom /////////////////////////////////////////////

                txData[0] = 0x65; //101
                txData[1] = 0x01; //1

                txData[2] = 0x01; //1. kanal
                String ch1brightness1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 1" + "f1", "0");
                String ch1brightness2 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 1" + "f2", "0");
                String ch1brightness3 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 1" + "f3", "0");
                String ch1brightness4 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 1" + "f4", "0");

                String mgdh1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 1" + "gdh", "7");
                String mgdm1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 1" + "gdm", "0");
                String mgh1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 1" + "gh", "12");
                String mgm1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 1" + "gm", "0");
                String mgbh1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 1" + "gbh", "17");
                String mgbm1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 1" + "gbm", "0");
                String mah1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 1" + "ah", "22");
                String mam1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 1" + "am", "0");

                txData[3] = (byte) Integer.parseInt(ch1brightness1);
                txData[4] = (byte) Integer.parseInt(mgdh1);
                txData[5] = (byte) Integer.parseInt(mgdm1);


                txData[6] = (byte) Integer.parseInt(ch1brightness2);
                txData[7] = (byte) Integer.parseInt(mgh1);
                txData[8] = (byte) Integer.parseInt(mgm1);


                txData[9] = (byte) Integer.parseInt(ch1brightness3);
                txData[10] = (byte) Integer.parseInt(mgbh1);
                txData[11] = (byte) Integer.parseInt(mgbm1);


                txData[12] = (byte) Integer.parseInt(ch1brightness4);
                txData[13] = (byte) Integer.parseInt(mah1);
                txData[14] = (byte) Integer.parseInt(mam1);

                txData[15] = 0x02; //2. kanal
                String ch2brightness1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 2" + "f1", "0");
                String ch2brightness2 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 2" + "f2", "0");
                String ch2brightness3 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 2" + "f3", "0");
                String ch2brightness4 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 2" + "f4", "0");

                String mgdh2 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 2" + "gdh", "7");
                String mgdm2 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 2" + "gdm", "0");
                String mgh2 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 2" + "gh", "12");
                String mgm2 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 2" + "gm", "0");
                String mgbh2 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 2" + "gbh", "17");
                String mgbm2 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 2" + "gbm", "0");
                String mah2 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 2" + "ah", "22");
                String mam2 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 2" + "am", "0");

                txData[16] = (byte) Integer.parseInt(ch2brightness1);
                txData[17] = (byte) Integer.parseInt(mgdh2);
                txData[18] = (byte) Integer.parseInt(mgdm2);


                txData[19] = (byte) Integer.parseInt(ch2brightness2);
                txData[20] = (byte) Integer.parseInt(mgh2);
                txData[21] = (byte) Integer.parseInt(mgm2);


                txData[22] = (byte) Integer.parseInt(ch2brightness3);
                txData[23] = (byte) Integer.parseInt(mgbh2);
                txData[24] = (byte) Integer.parseInt(mgbm2);


                txData[25] = (byte) Integer.parseInt(ch2brightness4);
                txData[26] = (byte) Integer.parseInt(mah2);
                txData[27] = (byte) Integer.parseInt(mam2);


                txData[28] = 0x03; //3. kanal
                String ch3brightness1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 3" + "f1", "0");
                String ch3brightness2 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 3" + "f2", "0");
                String ch3brightness3 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 3" + "f3", "0");
                String ch3brightness4 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 3" + "f4", "0");


                String mgdh3 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 3" + "gdh", "7");
                String mgdm3 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 3" + "gdm", "0");
                String mgh3 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 3" + "gh", "12");
                String mgm3 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 3" + "gm", "0");
                String mgbh3 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 3" + "gbh", "17");
                String mgbm3 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 3" + "gbm", "0");
                String mah3 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 3" + "ah", "22");
                String mam3 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 3" + "am", "0");


                txData[29] = (byte) Integer.parseInt(ch3brightness1);
                txData[30] = (byte) Integer.parseInt(mgdh3);
                txData[31] = (byte) Integer.parseInt(mgdm3);

                txData[32] = (byte) Integer.parseInt(ch3brightness2);
                txData[33] = (byte) Integer.parseInt(mgh3);
                txData[34] = (byte) Integer.parseInt(mgm3);

                txData[35] = (byte) Integer.parseInt(ch3brightness3);
                txData[36] = (byte) Integer.parseInt(mgbh3);
                txData[37] = (byte) Integer.parseInt(mgbm3);

                txData[38] = (byte) Integer.parseInt(ch3brightness4);
                txData[39] = (byte) Integer.parseInt(mah3);
                txData[40] = (byte) Integer.parseInt(mam3);


                txData[41] = 0x04; //4. kanal
                String ch4brightness1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 4" + "f1", "0");
                String ch4brightness2 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 4" + "f2", "0");
                String ch4brightness3 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 4" + "f3", "0");
                String ch4brightness4 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 4" + "f4", "0");


                String mgdh4 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 4" + "gdh", "7");
                String mgdm4 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 4" + "gdm", "0");
                String mgh4 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 4" + "gh", "12");
                String mgm4 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 4" + "gm", "0");
                String mgbh4 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 4" + "gbh", "17");
                String mgbm4 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 4" + "gbm", "0");
                String mah4 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 4" + "ah", "22");
                String mam4 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 4" + "am", "0");


                txData[42] = (byte) Integer.parseInt(ch4brightness1);
                txData[43] = (byte) Integer.parseInt(mgdh4);
                txData[44] = (byte) Integer.parseInt(mgdm4);


                txData[45] = (byte) Integer.parseInt(ch4brightness2);
                txData[46] = (byte) Integer.parseInt(mgh4);
                txData[47] = (byte) Integer.parseInt(mgm4);


                txData[48] = (byte) Integer.parseInt(ch4brightness3);
                txData[49] = (byte) Integer.parseInt(mgbh4);
                txData[50] = (byte) Integer.parseInt(mgbm4);


                txData[51] = (byte) Integer.parseInt(ch4brightness4);
                txData[52] = (byte) Integer.parseInt(mah4);
                txData[53] = (byte) Integer.parseInt(mam4);


                txData[54] = 0x05; //5. kanal

                String ch5brightness1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 5" + "f1", "0");
                String ch5brightness2 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 5" + "f2", "0");
                String ch5brightness3 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 5" + "f3", "0");
                String ch5brightness4 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 5" + "f4", "0");

                String mgdh5 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 5" + "gdh", "7");
                String mgdm5 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 5" + "gdm", "0");
                String mgh5 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 5" + "gh", "12");
                String mgm5 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 5" + "gm", "0");
                String mgbh5 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 5" + "gbh", "17");
                String mgbm5 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 5" + "gbm", "0");
                String mah5 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 5" + "ah", "22");
                String mam5 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 5" + "am", "0");


                txData[55] = (byte) Integer.parseInt(ch5brightness1);
                txData[56] = (byte) Integer.parseInt(mgdh5);
                txData[57] = (byte) Integer.parseInt(mgdm5);


                txData[58] = (byte) Integer.parseInt(ch5brightness2);
                txData[59] = (byte) Integer.parseInt(mgh5);
                txData[60] = (byte) Integer.parseInt(mgm5);


                txData[61] = (byte) Integer.parseInt(ch5brightness3);
                txData[62] = (byte) Integer.parseInt(mgbh5);
                txData[63] = (byte) Integer.parseInt(mgbm5);


                txData[64] = (byte) Integer.parseInt(ch5brightness4);
                txData[65] = (byte) Integer.parseInt(mah5);
                txData[66] = (byte) Integer.parseInt(mam5);

                txData[67] = 0x06; //6. kanal
                String ch6brightness1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 6" + "f1", "0");
                String ch6brightness2 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 6" + "f2", "0");
                String ch6brightness3 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 6" + "f3", "0");
                String ch6brightness4 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 6" + "f4", "0");


                String mgdh6 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 6" + "gdh", "7");
                String mgdm6 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 6" + "gdm", "0");
                String mgh6 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 6" + "gh", "12");
                String mgm6 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 6" + "gm", "0");
                String mgbh6 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 6" + "gbh", "17");
                String mgbm6 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 6" + "gbm", "0");
                String mah6 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 6" + "ah", "22");
                String mam6 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel 6" + "am", "0");


                txData[68] = (byte) Integer.parseInt(ch6brightness1);
                txData[69] = (byte) Integer.parseInt(mgdh6);
                txData[70] = (byte) Integer.parseInt(mgdm6);


                txData[71] = (byte) Integer.parseInt(ch6brightness2);
                txData[72] = (byte) Integer.parseInt(mgh6);
                txData[73] = (byte) Integer.parseInt(mgm6);


                txData[74] = (byte) Integer.parseInt(ch6brightness3);
                txData[75] = (byte) Integer.parseInt(mgbh6);
                txData[76] = (byte) Integer.parseInt(mgbm6);

                txData[77] = (byte) Integer.parseInt(ch6brightness4);
                txData[78] = (byte) Integer.parseInt(mah6);
                txData[79] = (byte) Integer.parseInt(mam6);

                 /*
                txData[80] = 0x07; //7. kanal
                String ch7brightness1  = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 7"+"f1","0");
                String ch7brightness2 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 7"+"f2","0");
                String ch7brightness3 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 7"+"f3","0");
                String ch7brightness4 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 7"+"f4","0");

                String mgdh7 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 7"+"gdh","7");
                String mgdm7 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 7"+"gdm","0");
                String mgh7  = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 7"+"gh","12");
                String mgm7  = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 7"+"gm","0");
                String mgbh7 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 7"+"gbh","17");
                String mgbm7 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 7"+"gbm","0");
                String mah7 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 7"+"ah","22");
                String mam7 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 7"+"am","0");

                txData[81] = (byte) Integer.parseInt(ch7brightness1);
                txData[82] = (byte) Integer.parseInt(mgdh7);
                txData[83] = (byte) Integer.parseInt(mgdm7);

                txData[84] = (byte) Integer.parseInt(ch7brightness2);
                txData[85] = (byte) Integer.parseInt(mgh7);
                txData[86] = (byte) Integer.parseInt(mgm7);

                txData[87] = (byte) Integer.parseInt(ch7brightness3);
                txData[88] = (byte) Integer.parseInt(mgbh7);
                txData[89] = (byte) Integer.parseInt(mgbm7);

                txData[90] = (byte) Integer.parseInt(ch7brightness4);
                txData[91] = (byte) Integer.parseInt(mah7);
                txData[92] = (byte) Integer.parseInt(mam7);


                txData[93] = 0x08; //8. kanal
                String ch8brightness1  = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 8"+"f1","0");
                String ch8brightness2 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 8"+"f2","0");
                String ch8brightness3 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 8"+"f3","0");
                String ch8brightness4 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 8"+"f4","0");


                String mgdh8 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 8"+"gdh","7");
                String mgdm8 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 8"+"gdm","0");
                String mgh8  = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 8"+"gh","12");
                String mgm8  = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 8"+"gm","0");
                String mgbh8 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 8"+"gbh","17");
                String mgbm8 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 8"+"gbm","0");
                String mah8 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 8"+"ah","22");
                String mam8 = localDataManager.getSharedPreference(getApplicationContext(),model+"Channel 8"+"am","0");

                txData[94] = (byte) Integer.parseInt(ch8brightness1);
                txData[95] = (byte) Integer.parseInt(mgdh8);
                txData[96] = (byte) Integer.parseInt(mgdm8);

                txData[97] = (byte) Integer.parseInt(ch8brightness2);
                txData[98] = (byte) Integer.parseInt(mgh8);
                txData[99] = (byte) Integer.parseInt(mgm8);

                txData[100] = (byte) Integer.parseInt(ch8brightness3);
                txData[101] = (byte) Integer.parseInt(mgbh8);
                txData[102] = (byte) Integer.parseInt(mgbm8);

                txData[103] = (byte) Integer.parseInt(ch8brightness4);
                txData[104] = (byte) Integer.parseInt(mah8);
                txData[105] = (byte) Integer.parseInt(mam8);

                 */

                txData[80] = Byte.parseByte(hour);
                txData[81] = Byte.parseByte(minute);
                txData[82] = 0x66; //stop
            }
        }

        // Datalar gönderiliyor
        for (int i = 0; i < txData.length; i++) {
            Log.e(TAG, "tx data " + i + ". data = " + txData[i]);
        }
        try {
            isTxFull = true;
            sendReceive.write(txData);
            sendList.remove(device.getAddress());
            Log.e(TAG, "Veriler gönderildi.");
            Message message = Message.obtain();
            message.what = STATE_MESSAGE_ACK_WAIT;
            handler.sendMessage(message);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void btn_closeConnection(View view) {
        closeBluetooth();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.oneMinute:
                if (checked)
                    localDataManager.setSharedPreference(view.getContext(), "longManuel", "false");
                break;
            case R.id.tenMinutes:
                if (checked)
                    localDataManager.setSharedPreference(view.getContext(), "longManuel", "true");
                break;
        }
    }

    private class ClientClass extends Thread {

        public ClientClass(String device_id) {
            device = bluetoothAdapter.getRemoteDevice(device_id);
            try {
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }
                socket = device.createRfcommSocketToServiceRecord(ESP32_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                // bağlantı kurulu ise önce kapat
                if (socket.isConnected()) {
                    closeBluetooth();
                }
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                socket.connect();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
                Message message = Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);

            } catch (IOException e) {
                e.printStackTrace();

                Message message = Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread{
        private final BluetoothSocket bluetoothSocket;

        public SendReceive(BluetoothSocket socket){
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;

        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            while (true){
                try {
                        bytes =  inputStream.read(buffer);
                        handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void closeBluetooth(){
        if (inputStream != null){
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (outputStream != null){
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        tv_status.setText("Connection closing...");
        tv_status.setTextColor(Color.CYAN);
        Log.e(TAG,"Bluetooth soket kapatıldı");
    }

    private String getMessage(Message msg){
        byte[] readBuffer = (byte[]) msg.obj;
        return new String(readBuffer,0,msg.arg1);
    }

    private void getDateTime(){
        // getDateTime: 10-05
        SimpleDateFormat sdf = new SimpleDateFormat("HH-mm");
        String currentTime = sdf.format(new Date());
        hour = currentTime.substring(0,2);
        minute = currentTime.substring(3,5);
        Log.e(TAG, "getDateTime: hour : "+hour+" minute : "+minute);
    }
}
