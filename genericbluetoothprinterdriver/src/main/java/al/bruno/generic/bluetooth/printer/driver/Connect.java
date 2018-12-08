package al.bruno.generic.bluetooth.printer.driver;

import android.bluetooth.BluetoothSocket;

public class Connect {
    private BluetoothSocket bluetoothSocket;
    private String error;
    private boolean connecting;

    Connect(BluetoothSocket bluetoothSocket, String error, boolean connecting) {
        this.bluetoothSocket = bluetoothSocket;
        this.error = error;
        this.connecting = connecting;
    }

    Connect(String error, boolean connecting) {
        this.error = error;
        this.connecting = connecting;
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public String getError() {
        return error;
    }

    public boolean isConnecting() {
        return connecting;
    }
}
