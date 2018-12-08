package al.bruno.generic.bluetooth.printer.driver;

public interface PrintListener {
    void finish();
    void error(String error);
}
