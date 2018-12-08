package al.bruno.generic.bluetooth.printer.driver;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.io.IOException;

public class GenericDriver implements ConnectionListener{

    private final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private Listener listener;
    private ConnectionTask connectTask;

    private BluetoothSocket bluetoothSocket;

    public GenericDriver(Listener listener) {
        this.listener = listener;
    }

    public void connect(BluetoothDevice bluetoothDevice) throws Exception {
        if(isConnecting())
            throw  new Exception("Connection in Progress");
        if(isConnected())
            throw new Exception("Socket already connected");
        (connectTask = new ConnectionTask(this, bluetoothDevice)).execute();
    }

    @Override
    public void onStartConnecting() {
        listener.onStartConnecting();
    }

    @Override
    public void onConnectionListener(Connect connect) {
        if((bluetoothSocket = connect.getBluetoothSocket()) != null) {
            listener.onConnectionSuccess(bluetoothSocket);
        } else {
            listener.onConnectionFailed("Socket is null");
        }

    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public boolean isConnecting() {
        return connectTask!= null && connectTask.getStatus() == AsyncTask.Status.RUNNING;
    }

    public boolean isConnected() {
        return bluetoothSocket != null && bluetoothSocket.isConnected();
    }

    public void disconnect() throws Exception {
        if(bluetoothSocket == null)
            throw new Exception("Socket is not connected");
        try {
            bluetoothSocket.close();
            bluetoothSocket = null;
            listener.onDisconnected();
        }
        catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void cancel() throws Exception
    {
        if (connectTask.getStatus() == AsyncTask.Status.RUNNING) {
            connectTask.cancel(true);
            listener.onConnectionCancelled();
        } else {
            throw new Exception("No connection is in progress");
        }
    }
}
