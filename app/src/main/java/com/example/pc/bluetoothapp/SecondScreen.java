package com.example.pc.bluetoothapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.io.PrintWriter;
/**
 * Created by PC on 2015.05.17.
 */
public class SecondScreen extends Activity {
    //kintamieji, aparsyt visus, tag nebutinas, mConnected ir mChatService
    String tag = "debugging";
    String s = null;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;
    private String mConnectedDeviceName = null;
    private ConnectedThread connected = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    ConnectThread connect = null;
    //handleris, aprasyt ka daro
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch(msg.what){
                case SUCCESS_CONNECT:
                    // DO something
                    connected = new ConnectedThread((BluetoothSocket)msg.obj);
                    connected.start();
                    Toast.makeText(getApplicationContext(), "CONNECT", Toast.LENGTH_LONG).show();
                    //String s = "successfully connected";
                   // connected.write(s.getBytes());
                  //  Button bt1 = (Button)findViewById(R.id.button);
                  //  Button bt2 = (Button)findViewById(R.id.button2);
                    Switch sw = (Switch)findViewById(R.id.switch2);
                    //    bt1.setEnabled(true);
                    //    bt2.setEnabled(true);
                        sw.setEnabled(true);


                    Log.i(tag, "connected");
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[])msg.obj;
                    String string = new String(readBuf);
                    Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
                    s=string;
                    final TextView tv = (TextView)findViewById(R.id.textView2);
                    tv.setText(string);
                    break;
            }
        }
    };
    //sukurimo funkcija, sukuria 2 textview,switcha, mygtuka, aprasyt kintamus kurie nera pilki
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout1);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final TextView tv2 = (TextView)findViewById(R.id.textView3);
        final TextView tv = (TextView)findViewById(R.id.textView2);
        final Switch sw = (Switch)findViewById(R.id.switch2);
        tv2.setText(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
      //  Button bt1 = (Button)findViewById(R.id.button);
       // Button bt2 = (Button)findViewById(R.id.button2);
        Button bt3 = (Button)findViewById(R.id.button3);

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.charging);
        final MediaPlayer mp2 = MediaPlayer.create(this, R.raw.not_charging);
        final RelativeLayout rl = (RelativeLayout)findViewById(R.id.relative_layout);

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);//svarbu kviecia devicelist activity, source android api
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
               // final TextView tv2 = (TextView)findViewById(R.id.textView3);
              //  tv2.setText(serverIntent.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS));
               // BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(serverIntent.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS));
               // Attempt to connect to the device
               //  mChatService.connect(device, true);
            }
        });
        final ImageView img = (ImageView)findViewById(R.id.imageView);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    img.setVisibility(View.VISIBLE);
                    tv.setTextColor(Color.GREEN);
                    rl.setBackgroundResource(R.drawable.green);
                    // sendMessage("Start");
                    String s1 = "on";
                    connected.write(s1);
                    img.setImageResource(R.drawable.g);
                  //  mp.start();
                }
                if(!isChecked){
                    //  tv.setText("Not Charging");
                    tv.setTextColor(Color.RED);
                    rl.setBackgroundResource(R.drawable.red);
                    // sendMessage("Stop");
                    String s = "off";
                    connected.write(s);
                    img.setVisibility(View.VISIBLE);
                    img.setImageResource(R.drawable.c);
                  // mp2.start();
                }
            }
        });
    }
    //realizuota onResume, nesvarb
    @Override
    protected void onResume() {
        super.onResume();
        final TextView tv2 = (TextView)findViewById(R.id.textView3);
    }

    //realizuota on activity result, svarbu,cia grazina device kuriuos randa devicelist activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final TextView tv2 = (TextView)findViewById(R.id.textView3);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    tv2.setText(data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS));
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    connect = new ConnectThread(device);
                    connect.start();
                 //   connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    tv2.setText(data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS));
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    connect = new ConnectThread(device);
                    connect.start();
                   // connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                   // setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                  //  Log.d(TAG, "BT not enabled");
                  //  Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                         //   Toast.LENGTH_SHORT).show();
                //    getActivity().finish();
                }
        }
    }
//prisijungimo threadas, source android develeper guide, esme prijungia prie kito device, sukuria socketa

    private class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;
            Log.i(tag, "construct");
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.i(tag, "get socket failed");

            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();
            Log.i(tag, "connect - run");
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                Log.i(tag, "connect - succeeded");
            } catch (IOException connectException) {	Log.i(tag, "connect failed");
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)

            mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
        }



        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
//threadas reikalingas kai gaunamas jau socketas ir jau prijungta, apdoroja iseinancius ir ijeinancius duomenis, source android developer guide, connectivity,bluetooth
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer;  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    buffer = new byte[1024];
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();

                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        public void write(String s) {
            try {
                PrintWriter out = new PrintWriter(mmSocket.getOutputStream(),true);
                out.println(s);
                //  mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

}
