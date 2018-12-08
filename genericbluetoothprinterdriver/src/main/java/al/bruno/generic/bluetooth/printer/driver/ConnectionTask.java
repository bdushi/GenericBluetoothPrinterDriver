package al.bruno.generic.bluetooth.printer.driver;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.UUID;

public class ConnectionTask extends AsyncTask<Void, Void, Connect> {

    private final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private ConnectionListener connectionListener;
    private BluetoothDevice bluetoothDevice;
    ConnectionTask(ConnectionListener connectionListener, BluetoothDevice bluetoothDevice) {
        this.connectionListener = connectionListener;
        this.bluetoothDevice = bluetoothDevice;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        connectionListener.onStartConnecting();
    }

    @Override
    protected Connect doInBackground(Void... voids) {
        try {
            BluetoothSocket bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
            bluetoothSocket.connect();
            return new Connect(bluetoothSocket, "Success", true);
        } catch (IOException ex){
            return new Connect(ex.getMessage(), false);
        }
    }

    @Override
    protected void onPostExecute(Connect connect) {
        super.onPostExecute(connect);
        connectionListener.onConnectionListener(connect);
    }
}
