package com.hemanth.embeddedrf;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.harrysoft.androidbluetoothserial.BluetoothManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Sensor> items;
    private SensorAdapter adapter;
    private ListView listView;
    private List<BluetoothDevice> pairedDevices;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.devicelist);
        items=new ArrayList<>();
        adapter=new SensorAdapter(this,items);
        listView.setAdapter(adapter);
        BluetoothManager bluetoothManager = BluetoothManager.getInstance();
        if (bluetoothManager == null) {
            // Bluetooth unavailable on this device :( tell the user
            Toast.makeText(getApplicationContext(), "Bluetooth not available.", Toast.LENGTH_LONG).show(); // Replace context with your context instance.
            finish();
        }
        try {
            pairedDevices = bluetoothManager.getPairedDevicesList();
            for (BluetoothDevice device : pairedDevices) {
                items.add(new Sensor(device.getName(), device.getAddress()));
                adapter.notifyDataSetChanged();
            }
        }catch(Exception e){

        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,SensorsActivity.class);
                Sensor s=(Sensor)adapter.getItem(position);
                intent.putExtra("name",s.getName());
                intent.putExtra("mac",s.getValue());
                startActivity(intent);
            }
        });

    }
}
