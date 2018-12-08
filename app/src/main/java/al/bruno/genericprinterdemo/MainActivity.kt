package al.bruno.genericprinterdemo

import al.bruno.generic.bluetooth.printer.driver.GenericDriver
import al.bruno.generic.bluetooth.printer.driver.Listener
import al.bruno.generic.bluetooth.printer.driver.Print
import al.bruno.generic.bluetooth.printer.driver.PrintListener
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Listener, PrintListener, View.OnClickListener {

    private val adapter:BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val connection:GenericDriver = GenericDriver(this);

    private val REQUEST_CONNECT_DEVICE = 1
    private val REQUEST_ENABLE_BT = 2
    private val BLUETOOTH_DEVICES = "BLUETOOTH_DEVICES"

    private val TAG = "P25"

    private var bluetoothDevice: BluetoothDevice? = null
    private var printer: Print? = null

    val data = "             Summary\n" +
            "_________________________________\n" +
            "Expense                     Value\n" +
            "Car                         1 Lek\n" +
            "Party                       1 Lek\n" +
            "Rent                        1 Lek\n" +
            "School                      1 Lek\n" +
            "Shopping                    1 Lek\n" +
            "Transportation              1 Lek\n" +
            "---------------------------------\n" +
            "TOTAL                       6 Lek\n" +
            "\n\n\n\n\n\n"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*if (savedInstanceState != null)
            bluetoothDevice = savedInstanceState.getParcelable(BLUETOOTH_DEVICES)*/

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(bluetoothDevice != null) {
            connect(bluetoothDevice)
        } else {
            if (adapter.isEnabled) {
                startActivityForResult(Intent(this, DeviceListActivity::class.java), REQUEST_CONNECT_DEVICE)
            } else {
                startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != RESULT_CANCELED) {
            when(requestCode){
                REQUEST_CONNECT_DEVICE -> {
                    if (resultCode == Activity.RESULT_OK) {
                        bluetoothDevice = data.getParcelableExtra<BluetoothDevice>("devices")
                        supportActionBar?.subtitle = bluetoothDevice?.name
                        connect(bluetoothDevice)

                    }
                }
                REQUEST_ENABLE_BT ->{
                    if (resultCode == Activity.RESULT_OK) {
                        startActivityForResult(Intent(this, DeviceListActivity::class.java), REQUEST_CONNECT_DEVICE)
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable(BLUETOOTH_DEVICES, bluetoothDevice)
        super.onSaveInstanceState(outState)
    }

    private fun connect(bluetoothDevice: BluetoothDevice?) {
        if (bluetoothDevice == null) {
            Log.i(TAG, "bluetoothDevice null")
        } else {
            if (bluetoothDevice.bondState == BluetoothDevice.BOND_BONDED) {
                try {
                    if (!connection.isConnected)
                        connection.connect(bluetoothDevice)
                    else {
                        print.visibility = VISIBLE
                        print.text = bluetoothDevice.name
                        printer = Print(data.toByteArray(), false, 1, connection.bluetoothSocket);
                        printer?.start()
                        printer?.registerObserver({
                            print.setOnClickListener(this)
                        })
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }

            } else
                startActivityForResult(Intent(this, DeviceListActivity::class.java), REQUEST_CONNECT_DEVICE)
        }
    }

    override fun onStartConnecting() {
        progress.visibility = VISIBLE
    }
    override fun onConnectionCancelled() {
        progress.visibility = GONE
    }

    override fun onConnectionSuccess(bluetoothSocket: BluetoothSocket?) {
        progress.visibility = GONE
        print.visibility = VISIBLE
        print.text = bluetoothDevice?.name
        printer = Print(data.toByteArray(), false, 1, connection.bluetoothSocket);
        printer?.start()
        printer?.registerObserver({
            print.setOnClickListener(this)
        })
    }

    override fun onConnectionFailed(error: String?) {
        Log.i(TAG, error);
        progress.visibility = GONE
    }

    override fun onDisconnected() {
        Log.i(TAG, "Disconnected");
    }

    override fun onConnectionClosed() {
        progress.visibility = GONE
        Log.i(TAG, "ConnectionClosed");
    }

    override fun error(error: String?) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        if(connection.isConnected) {
            printer = Print(data.toByteArray(), false, 1, connection.bluetoothSocket)
            printer?.start()
            printer?.registerObserver({
                print.setOnClickListener(this)
            })
        }
        else
            try {
                connection.connect(bluetoothDevice)
            } catch (e : Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }
}
