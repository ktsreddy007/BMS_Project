package com.hemanth.embeddedrf;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SensorsActivity extends AppCompatActivity {
    BluetoothManager bluetoothManager;
    private SimpleBluetoothDeviceInterface deviceInterface;
    TextView textView;

   private String name,mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        textView=findViewById(R.id.textbox);

        Intent intent=getIntent();
        name=intent.getStringExtra("name");
        mac=intent.getStringExtra("mac");
        Toast.makeText(getApplicationContext(), mac, Toast.LENGTH_SHORT).show();
        Button button=findViewById(R.id.connect);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectDevice(mac);
            }
        });


         bluetoothManager = BluetoothManager.getInstance();
        if (bluetoothManager == null) {
            // Bluetooth unavailable on this device :( tell the user
            Toast.makeText(getApplicationContext(), "Bluetooth not available.", Toast.LENGTH_LONG).show(); // Replace context with your context instance.
            finish();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothManager.closeDevice(mac);
                Intent i=new Intent(SensorsActivity.this,Display.class);
                i.putExtra("mac",mac);
                startActivity(i);
            }
        });
    }
    private void connectDevice(String mac) {
        bluetoothManager.openSerialDevice(mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnected, this::onError);
    }

    private void onConnected(BluetoothSerialDevice connectedDevice) {
        // You are now connected to this device!
        // Here you may want to retain an instance to your device:
        deviceInterface = connectedDevice.toSimpleDeviceInterface();

        // Listen to bluetooth events
        deviceInterface.setListeners(this::onMessageReceived, this::onMessageSent, this::onError);

        // Let's send a message:
        deviceInterface.sendMessage("Hello world!");
    }

    private void onMessageSent(String message) {
        // We sent a message! Handle it here.
        Toast.makeText(getApplicationContext(), "Sent a message! Message was: " + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.
    }

    private void onMessageReceived(String message) {
        // We received a message! Handle it here.
        List<String> list;
        StringBuilder stringBuilder=new StringBuilder();
        list=new Dataparser().parseData(message);
        for (int i=0;i<list.size();i++){
            if (i==0){
            stringBuilder.append(list.get(i).toString());
            stringBuilder.append(" ");}else {
                stringBuilder.append(",");
                stringBuilder.append(list.get(i).toString());
                stringBuilder.append(" ");
            }

        }
        textView.append(stringBuilder.toString()+"\n");
        textView.append("\n");

    }

    private void onError(Throwable error) {
        // Handle the error
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
