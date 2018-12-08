package al.bruno.genericprinterdemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import al.bruno.genericprinterdemo.databinding.DeviceSingleItemBinding;

public class DeviceListActivity extends AppCompatActivity implements RecyclerViewOnClick {
    private BluetoothAdapter bluetoothAdapter;
    private TextView noDevicesFounded;
    private CustomAdapter<BluetoothDevice, DeviceSingleItemBinding> adapter;
    private RecyclerView pairedDevicesList;
    private ProgressBar searchingDevices;
    private BluetoothDevice bluetoothDevice;

    private final int REQUEST_ENABLE_BT = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);
        pairedDevicesList = findViewById(R.id.paired_devices_list);
        searchingDevices = findViewById(R.id.searching_devices);
        pairedDevicesList.setLayoutManager(new LinearLayoutManager(this));
        pairedDevicesList.setItemAnimator(new DefaultItemAnimator());
        pairedDevicesList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        noDevicesFounded = findViewById(R.id.no_devices_founded);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, intentFilter);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startDiscovery();
    }

    @Override
    public void onItemClick(View view, int position) {
        searchingDevices.setVisibility(View.GONE);
        bluetoothAdapter.cancelDiscovery();
        bluetoothDevice = adapter.getItem(position);
        if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            setResult(Activity.RESULT_OK, new Intent().putExtra("devices", bluetoothDevice));
            finish();
        } else {
            createBond(bluetoothDevice);
            if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                setResult(Activity.RESULT_OK, new Intent().putExtra("devices", bluetoothDevice));
                finish();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.refresh:
                if(bluetoothAdapter.isEnabled())
                    bluetoothAdapter.startDiscovery();
                else {
                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onLongItemClick(View view, int position) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED) {
            if (REQUEST_ENABLE_BT == requestCode)
                if(resultCode == Activity.RESULT_OK)
                    bluetoothAdapter.startDiscovery();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mReceiver);
            unregisterReceiver(boundReceiver);
        } catch (IllegalArgumentException e){
            Log.i("SBD", e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        List<BluetoothDevice> devices = new ArrayList<>();
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                searchingDevices.setVisibility(View.VISIBLE);
                noDevicesFounded.setVisibility(View.GONE);
                pairedDevicesList.setVisibility(View.VISIBLE);
                updateList(devices);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (!devices.isEmpty()) {
                    searchingDevices.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                } else {
                    searchingDevices.setVisibility(View.GONE);
                    noDevicesFounded.setVisibility(View.VISIBLE);
                    pairedDevicesList.setVisibility(View.GONE);
                }
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(devices.indexOf(device) == -1) {
                    devices.add(device);
                } else {
                    devices.set(devices.indexOf(device), device);
                }
                adapter.notifyDataSetChanged();
            }
        }
    };

    private final BroadcastReceiver boundReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != null) {
                if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                    int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                    if (state > 0) {
                        if (state == BluetoothDevice.BOND_BONDING) {
                            Log.i("SBD", "BOND_BONDING...");
                            //bonding process is still working
                            //essentially this means that the Confirmation Dialog is still visible
                        } else if (state == BluetoothDevice.BOND_BONDED) {
                            setResults();
                            searchingDevices.setVisibility(View.GONE);
                        } else if (state == BluetoothDevice.BOND_NONE) {
                            Log.i("SBD", "BOND_BONDING...");
                            //bonding process failed
                            //which also means that the user pressed CANCEL on the Dialog
                            //we can finally call the method
                        }
                    } else {
                        Log.i("SBD", "NO_BONDING_DEVICES...");
                    }
                }
            } else {
                Log.i("SBD", "NO_BONDING_DEVICES...");
            }
        }
        /*if (state < 0) {
                    //we should never get here
                } else if (state == BluetoothDevice.BOND_BONDING) {
                    //bonding process is still working
                    //essentially this means that the Confirmation Dialog is still visible
                } else if (state == BluetoothDevice.BOND_BONDED) {
                    DeviceListActivity.this.setResult();
                    searchingDevices.setVisibility(View.GONE);
                } else if (state == BluetoothDevice.BOND_NONE) {
                    //bonding process failed
                    //which also means that the user pressed CANCEL on the Dialog
                    //we can finally call the method
                }*/
    };

    public void updateList(List<BluetoothDevice> devices) {
        Log.i("SBD", String.valueOf(devices.size()));
        adapter = new CustomAdapter<>(DeviceListActivity.this, devices, R.layout.device_single_item, DeviceSingleItemBinding::setDevice, this);
        pairedDevicesList.setAdapter(adapter);
    }

    private void createBond(BluetoothDevice device) {
        try {
            searchingDevices.setVisibility(View.VISIBLE);
            Class<?> cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class<?>[] par = {};

            Method method = cl.getMethod("createBond", par);
            method.invoke(device);

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(boundReceiver, filter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(boundReceiver, filter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unPairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setResults() {
        setResult(Activity.RESULT_OK, new Intent().putExtra("devices", bluetoothDevice));
        finish();
    }
}