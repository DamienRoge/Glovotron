package com.rossuyt.glovotron;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT16;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT32;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8;

/**
 * Created by Luong-Thi-Bien BOSSUYT on 24/04/2017.
 */

public class DeviceControllerActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothManager mBluetoothManager;

    private BluetoothDevice myBluetoothLEDevice;
    private BluetoothDevice bluetoothArduinoDevice;

    private Boolean mConnected;
    private BluetoothGatt myBluetoothGatt;

    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private OutputStream outputStream;
    private InputStream inputStream;
    private String arduinoAddress = "00:14:03:06:0B:37"; //Adresse mac du module bluetooth de l'arduino (robot)

    private Handler handler = new Handler();

    private Handler handlerXYZ = new Handler();

    private Runnable readBgcXYZ;

    private Runnable sendDatas;

    private String name, address;

    private int STILL = 0;
    private int FORWARD = 1;
    private int LEFT = 2;
    private int RIGHT = 3;

    private BluetoothGattCharacteristic bgcXYZ;

    private int accelX = 10;
    private int accelY = 0;
    private int accelZ = 10;

    private TextView myTextView ;
    private TextView myTextViewRaw ;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_controller);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Bundle extras = getIntent().getExtras();

        myTextView = (TextView) findViewById(R.id.tvAction);
        myTextViewRaw = (TextView) findViewById(R.id.tvRaw);

        myTextView.setText("Robot non connecté");




        name = "BLE";
        //Adresse en dur du module bluetooth du gant
        address = "E3:9D:52:83:DE:1D";


        myBluetoothLEDevice = mBluetoothAdapter.getRemoteDevice(address);

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        if(!arduinoAddress.equals("")) {
            showToast(myBluetoothLEDevice.getName() + " -- " + myBluetoothLEDevice.getAddress());

            Log.i("===>", "LANCEMENT");

            myBluetoothGatt = myBluetoothLEDevice.connectGatt(getApplicationContext(), false, mGattCallback);


            bluetoothArduinoDevice = mBluetoothAdapter.getRemoteDevice(arduinoAddress);

            try {

                bluetoothSocket = createBluetoothSocket(bluetoothArduinoDevice);

                bluetoothSocket.connect();

                outputStream = bluetoothSocket.getOutputStream();
                inputStream = bluetoothSocket.getInputStream();

                int value = computeValues(accelX,accelY,accelZ);

                sendDatas();


            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            showToast("Adresse arduino invalide");
        }

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, uuid);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return device.createRfcommSocketToServiceRecord(uuid);
    }

    private void sendDatas() {
        sendDatas = new Runnable() {
            @Override
            public void run() {
                try {
                    int foo = computeValues(accelX,accelY,accelZ); //cette fonction transforme les 3 valeurs récuperées du gant en commande moteur

                    String action = "";

                    if(foo == 0) {
                        action = "STILL";
                    } else if (foo == 1) {
                        action = "FORWARD";
                    } else if (foo == 2) {
                        action = "LEFT";
                    } else {
                        action = "RIGHT";
                    }

                    myTextView.setText(action);

                    Log.i("coucou", "SEND TO ROBOT : "+foo);
                        outputStream.write(foo);

                } catch (IOException e) {
                    e.printStackTrace();
                    myTextView.setText("Robot perdu");

                }

                handler.postDelayed(sendDatas, 500);
            }
        };

        handler.post(sendDatas);
    }

    private int computeValues(int x, int y, int z) {
        Log.i("--------", "x : "+x);
        Log.i("--------", "y : "+y);
        Log.i("--------", "z : "+z);


        if((x > 26 && x < 32) && (y > 16 && y < 24) && (z > 16 && z < 24)) {
            System.out.println(FORWARD);
            return FORWARD;
        } else if ((x > 16 && x < 24) && (y > 16 && y < 24) && (z > 26 && z < 34)) {
            System.out.println(LEFT);
            return LEFT;
        } else if ((x > 16 && x < 24) && (y > 16 && y < 24) && (z > 6 && z < 14)) {
            System.out.println(RIGHT);
            return RIGHT;
        } else {
            System.out.println(STILL);
            return STILL;
        }
    }

    private void readBgcXYZ() {
        Log.i("-------", "readBgcX");
        readBgcXYZ = new Runnable() {

            @Override
            public void run() {

                myBluetoothGatt.readCharacteristic(bgcXYZ);

                handlerXYZ.postDelayed(readBgcXYZ, 500);
            }
        };

        handlerXYZ.post(readBgcXYZ);
    }

    @Override
    protected void onPause() {
        handler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                /*Log.i("coucou", "READ CHAR");
                Log.i("coucou", "UUID : "+characteristic.getUuid().toString());
                Log.i("coucou", "type : "+ characteristic.getWriteType());
*/


                int val = characteristic.getIntValue(FORMAT_UINT32,0);
                accelZ = val%100;
                accelY = (val/100)%100;
                accelX = (val/10000)%100;

                Log.i("===========>", ""+accelX);
                Log.i("===========>", ""+accelY);
                Log.i("===========>", ""+accelZ);

                Log.i("===========>", "COMPUTED : "+computeValues(accelX,accelY,accelZ));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myTextViewRaw.setText("X : "+accelX + " Y : "+accelY +" Z : "+accelZ);
                    }
                });


                Log.i("------>", myTextView.getText().toString());



            } else {
                Log.i(name, "ERROR READ CHAR");

            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {

                Log.i(name, "Connected to GATT server.");
                Log.i(name, "Attempting to start service discovery:" +
                        gatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(name, "Disconnected from GATT server.");
                gatt.connect();
            }

        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(name, "SERVICES DISCOVERED");
                List<BluetoothGattService> servicesList = gatt.getServices();

                for (int i = 0; i < servicesList.size(); i++) {
                    Log.i(name, servicesList.get(i).getUuid().toString());
                    List<BluetoothGattCharacteristic> characteristicsList = servicesList.get(i).getCharacteristics();
                    for (int j = 0; j < characteristicsList.size(); j++) {

                        Log.i(name, "   " + characteristicsList.get(j).getUuid().toString());

                        //UUID de la characteristique à récuperer
                        if ("00000066-0000-1000-8000-00805f9b34fb".equals(characteristicsList.get(j).getUuid().toString())) {

                            bgcXYZ = characteristicsList.get(j);
                            readBgcXYZ();
                            Log.i("----------------", "getService");
                        }

                    }
                }

            } else {
                Log.w(name, "onServicesDiscovered received: " + status);
            }
        }
    };

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
