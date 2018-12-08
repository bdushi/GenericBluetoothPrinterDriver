package al.bruno.generic.bluetooth.printer.driver.observer;

/**
 * Created by 1sd on 3/15/18.
 */

public interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObserver(String messages);
}
