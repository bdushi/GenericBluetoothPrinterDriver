package al.bruno.generic.bluetooth.printer.driver;

import android.bluetooth.BluetoothSocket;

public interface Listener {
    void onStartConnecting();
    void onConnectionCancelled();
    void onConnectionSuccess(BluetoothSocket bluetoothSocket);
    void onConnectionFailed(String error);
    void onDisconnected();
    void onConnectionClosed();
}
