package com.mythleds.ikigai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mythleds.ikigai.Adapter.DeviceAdapter;
import com.mythleds.ikigai.Class.Ble_devices;
import com.mythleds.ikigai.Interface.CallBackDevice;


import java.util.ArrayList;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.S)
public class BluetoothScanActivity extends AppCompatActivity {

    private int ble_request_en;
    private BluetoothAdapter bluetoothAdapter;
    private Intent intent_ble;
    private ImageButton btn_scan;
    private RecyclerView mRecylerView;
    private DeviceAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CallBackDevice callBackDevice;
    ArrayList arrayList_bleDevices = new ArrayList<>();
    // seçilen BTT modüller
    ArrayList bleDeviceList = new ArrayList();
    int a = 0;
    View view;

    public void init() {
        btn_scan = findViewById(R.id.btn_scan);
        mRecylerView = findViewById(R.id.rv_ble);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        intent_ble = new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE);
        ble_request_en = 1;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setItemViewCacheSize(50);
        mRecylerView.setLayoutManager(linearLayoutManager);
        mAdapter = new DeviceAdapter(arrayList_bleDevices, callBackDevice);

        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth desteklenmiyor ... ", Toast.LENGTH_LONG).show();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                startActivityForResult(intent_ble, ble_request_en);
            }
        }

        // cihaz seçildiğinde buraya dallanacak buradan bir sonraki aktiviteye seçilen cihazın device_name ve device_id si gönderilecek
        mAdapter.setCallback(new CallBackDevice() {
            @Override
            public void listenerMethod(String device_name, String device_id, boolean action, int pos) {
                if (action == true) {
                    if (!device_id.isEmpty()) {
                        bleDeviceList.add(device_id);
                    }
                    //btn_scan.setText("Bağlan");
                    Log.e("pos", "" + device_id + " " + pos);

                } else if (action == false) {
                    //bleDeviceList.clear();
                    //btn_scan(view);
/*                    bleDeviceList.set(pos,"null");
                    btn_scan.setText("Bağlan");
                    Log.e("pos",""+pos);*/

                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_scan);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(BluetoothScanActivity.this, Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(BluetoothScanActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;

            }
        } else {
            if (ContextCompat.checkSelfPermission(BluetoothScanActivity.this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(BluetoothScanActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 2);
                return;
            }
        }



        init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ble_request_en) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Bluetooth aktif", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Bluetooth aktif değil", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void btn_scan(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Bluetooth izinleri verilmelidir ", Toast.LENGTH_LONG).show();
                return;
            }
        }

        try {
            if (bleDeviceList.isEmpty() && !bluetoothAdapter.getAddress().isEmpty()) {
                arrayList_bleDevices.clear();

                Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();

                for (BluetoothDevice bluetoothDevice : bt) {
                    if (bluetoothDevice.getName().contains("IKIGAI") || bluetoothDevice.getName().contains("ikigai")) {
                        arrayList_bleDevices.add(new Ble_devices(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                    }
                }
                mRecylerView.setAdapter(mAdapter);
            } else {

                for (int i = 0; i < bleDeviceList.size(); i++) {
                    Log.e("deviceList", bleDeviceList.get(i).toString() + "pos : " + i);
                }
                Intent intent = new Intent(BluetoothScanActivity.this, MainActivity.class);
                intent.putStringArrayListExtra("bleDevicesList", bleDeviceList);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error While searching devices...", Toast.LENGTH_LONG).show();

        }


    }
}