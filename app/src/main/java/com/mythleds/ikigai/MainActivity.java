package com.mythleds.ikigai;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mythleds.ikigai.Class.LocalDataManager;
import com.mythleds.ikigai.Fragment.Fragment1;
import com.mythleds.ikigai.Fragment.Fragment2;
import com.mythleds.ikigai.Fragment.Fragment3;
import com.mythleds.ikigai.Fragment.Fragment4;

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
    private int i = 0;
    private final byte[] txData = new byte[109];
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

    private String hour;
    private String minute;

    private static final UUID ESP32_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice device;
    private BluetoothSocket socket;

    ClientClass clientClass;
    ProgressDialog progress;

    LocalDataManager localDataManager = new LocalDataManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            startActivity(new Intent(MainActivity.this, BluetoothScanActivity.class));
                        }
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


        tv_status = findViewById(R.id.tv_status);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        fab_bottom = findViewById(R.id.fab_bottom);
        bottomNavigationView.setBackground(null);
        bleList = getIntent().getStringArrayListExtra("bleDevicesList");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
                    if (i > 0) {
                        if (socket.isConnected() && isTxFull) {
                            sendReceive.write(txData);
                            sendList.remove(device.getAddress());
                            Log.e(TAG, "All settings send to all devices");
                            Message message = Message.obtain();
                            message.what = STATE_MESSAGE_ACK_WAIT;
                            handler.sendMessage(message);
                        }
                    } else {
                        fab_bottom.setEnabled(true);
                    }
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
                    String tempMsg = getMessage(msg);
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
                    i++;
                    String test_model = localDataManager.getSharedPreference(getApplicationContext(), "test_model", "false");
                    boolean isSent = sendList.size() > 0;

                    if (test_model.equals("test")) {
                        isSent = i < bleList.size();
                    }
                    if (isSent) {
                        Log.e(TAG, "Diğer cihaza bağlanıyor ...");
                        tv_status.setText("Connecting...");
                        tv_status.setTextColor(getResources().getColor(R.color.accent));
                        progress =
                                ProgressDialog.show(MainActivity.this, "Connecting to other leds..", "Please Wait");
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
        i = 0;
        sendList = (ArrayList<String>) bleList.clone();

        fab_bottom.setEnabled(false);
        if (!socket.isConnected()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                startActivity(new Intent(MainActivity.this, BluetoothScanActivity.class));
            }
            finish();
        }

        String model = localDataManager.getSharedPreference(getApplicationContext(), "model", "");
        String test_model = localDataManager.getSharedPreference(getApplicationContext(), "test_model", "false");
        Log.e(TAG, "fab_bottom: hour" + hour);
        Log.e(TAG, "fab_bottom: minute" + minute);
        txData[54] = Byte.parseByte(hour);
        txData[55] = Byte.parseByte(minute);
        //stop
        if (test_model.equals("test")) {
            String longManuel = localDataManager.getSharedPreference(getApplicationContext(), "longManuel", "false");

            txData[0] = 0x65;
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
        } else {
            String totalIntensity = localDataManager.getSharedPreference(getApplicationContext(), model + "total", "100");
            //total * kanal / 100
            Integer total = Integer.parseInt(totalIntensity);

            txData[0] = 0x65; //101
            txData[1] = 0x01; //1

            int byteSira = 1;
            for (int i = 1; i < 7; i++) {
                txData[++byteSira] = 0x01; //1. kanal   // 2

                String ch1brightness1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel " + i + "f1", "0");
                String ch1brightness2 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel " + i + "f2", "0");
                String ch1brightness3 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel " + i + "f3", "0");
                String ch1brightness4 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel " + i + "f4", "0");

                Integer newBright21 = (total * Integer.parseInt(ch1brightness2) / 100);
                Integer newBright31 = (total * Integer.parseInt(ch1brightness3) / 100);

                String mgdh1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel " + i + "gdh", "7");
                String mgdm1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel " + i + "gdm", "0");
                String mgh1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel " + i + "gh", "12");
                String mgm1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel " + i + "gm", "0");
                String mgbh1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel " + i + "gbh", "17");
                String mgbm1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel " + i + "gbm", "0");
                String mah1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel " + i + "ah", "22");
                String mam1 = localDataManager.getSharedPreference(getApplicationContext(), model + "Channel " + i + "am", "0");

                txData[++byteSira] = (byte) Integer.parseInt(ch1brightness1); //3
                txData[++byteSira] = (byte) Integer.parseInt(mgdh1);
                txData[++byteSira] = (byte) Integer.parseInt(mgdm1);
                txData[++byteSira] = (byte) (newBright21 & 0xff);

                txData[++byteSira] = (byte) Integer.parseInt(mgh1);
                txData[++byteSira] = (byte) Integer.parseInt(mgm1);
                txData[++byteSira] = (byte) (newBright31 & 0xff);

                txData[++byteSira] = (byte) Integer.parseInt(mgbh1);
                txData[++byteSira] = (byte) Integer.parseInt(mgbm1);
                txData[++byteSira] = (byte) Integer.parseInt(ch1brightness4);
                txData[++byteSira] = (byte) Integer.parseInt(mah1);
                txData[++byteSira] = (byte) Integer.parseInt(mam1);  // 14

            }

            txData[80] = Byte.parseByte(hour);
            txData[81] = Byte.parseByte(minute);
        }
        txData[82] = 0x66;
        // Datalar gönderiliyor
        for (int i = 0; i < txData.length; i++) {
            Log.e(TAG, "tx data " + i + ". data = " + txData[i]);
        }
        try {
            isTxFull = true;
            sendReceive.write(txData);
            sendList.remove(device.getAddress());
            if (!test_model.equals("test"))
                i++;
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
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

            } catch (IOException e) {
                e.printStackTrace();

                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;

        public SendReceive(BluetoothSocket socket) {
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

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void closeBluetooth() {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        tv_status.setText("Connection closing...");
        tv_status.setTextColor(Color.CYAN);
        Log.e(TAG, "Bluetooth soket kapatıldı");
    }

    private String getMessage(Message msg) {
        byte[] readBuffer = (byte[]) msg.obj;
        return new String(readBuffer, 0, msg.arg1);
    }

    private void getDateTime() {
        // getDateTime: 10-05
        SimpleDateFormat sdf = new SimpleDateFormat("HH-mm");
        String currentTime = sdf.format(new Date());
        hour = currentTime.substring(0, 2);
        minute = currentTime.substring(3, 5);
        Log.e(TAG, "getDateTime: hour : " + hour + " minute : " + minute);
    }
}
