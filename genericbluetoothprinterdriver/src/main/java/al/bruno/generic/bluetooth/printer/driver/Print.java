package al.bruno.generic.bluetooth.printer.driver;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import al.bruno.generic.bluetooth.printer.driver.observer.Observer;
import al.bruno.generic.bluetooth.printer.driver.observer.Subject;

public class Print extends Thread implements Subject {

    private final String TAG = "P25";

    private byte[] data;
    private boolean clear;
    private int printModel;
    private BluetoothSocket bluetoothSocket;
    private transient List<Observer> registry = new ArrayList<>();

    public Print(byte[] data, boolean clear, int printModel, BluetoothSocket bluetoothSocket) {
        this.data = data;
        this.clear = clear;
        this.printModel = printModel;
        this.bluetoothSocket = bluetoothSocket;
    }
    @Override
    public void run() {
        super.run();
        try(OutputStream outputStream = bluetoothSocket.getOutputStream()) {
            if (printModel == 3)
                outputStream.write(data);
            else {
                for (byte mData : data) {
                    outputStream.write(mData);
                    if (clear)
                        outputStream.flush();
                }
            }
        } catch (IOException io) {
            notifyObserver(io.getMessage());
        } finally {
            notifyObserver("FINISH");
        }
    }

    @Override
    public void registerObserver(Observer o) {
        registry.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        if(registry.indexOf(o) >= 0)
            registry.remove(o);
    }

    @Override
    public void notifyObserver(String messages) {
        for (Observer observer : registry){
            observer.update(messages);
        }
    }
}
