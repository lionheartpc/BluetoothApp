package com.example.pc.bluetoothapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_ENABLE_BT = 3;
    //sukurimo funkcija, inicijuoja mygtuka, adapteri, papraso kad adapteris butu ijungtas,matomas, jei bt ijungtas pereinama i SecondScreen activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//adapterio kintamas
        ImageButton ibt1 = (ImageButton)findViewById(R.id.imageButton); //mygtukas
        ibt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                //mygtuko listener
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivity(discoverableIntent);}
                if (mBluetoothAdapter.isEnabled()){
                Intent intent1;
                intent1 = new Intent(getApplicationContext(),SecondScreen.class);
                startActivity(intent1);}
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
