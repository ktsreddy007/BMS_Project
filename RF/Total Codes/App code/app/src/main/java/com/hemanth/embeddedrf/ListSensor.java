package com.hemanth.embeddedrf;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ListSensor extends AppCompatActivity {
   List<Sensor> sensors;
   ListView listView;
   SensorAdapter adapter;
    private BluetoothManager bluetoothManager;
    private SimpleBluetoothDeviceInterface deviceInterface;
    String mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sensor);
        listView=findViewById(R.id.sensorlist);
        sensors=new ArrayList<>();
        adapter=new SensorAdapter(this,sensors);
        Intent intent=getIntent();
        listView.setAdapter(adapter);

        mac=intent.getStringExtra("mac");
        bluetoothManager = BluetoothManager.getInstance();

        if (bluetoothManager == null) {
            // Bluetooth unavailable on this device :( tell the user
            Toast.makeText(getApplicationContext(), "Bluetooth not available.", Toast.LENGTH_LONG).show(); // Replace context with your context instance.
            finish();
        }
        connectDevice(mac);
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
        //deviceInterface.sendMessage("Hello world!");
    }

    private void onMessageSent(String message) {
        // We sent a message! Handle it here.
        Toast.makeText(getApplicationContext(), "Sent a message! Message was: " + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.
    }

    private void onMessageReceived(String message) {
        // We received a message! Handle it here.
        sensors.clear();

        List<String> ll;
        ll=new Dataparser().parseData(message);
        int p=0;
        int q=1;
        for(int i=0;i<ll.size()/2;i++){
            sensors.add(new Sensor(ll.get(p),ll.get(q)));
            p=p+2;
            q=q+2;
            adapter.notifyDataSetChanged();
        }


         // Replace context with your context instance.
    }

    private void onError(Throwable error) {
        // Handle the error
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetoothManager.closeDevice(mac);
    }
}
