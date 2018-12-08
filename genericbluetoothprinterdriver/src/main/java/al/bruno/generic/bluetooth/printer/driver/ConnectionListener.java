package al.bruno.generic.bluetooth.printer.driver;

public interface ConnectionListener {
    void onStartConnecting();
    void onConnectionListener(Connect connect);
}
