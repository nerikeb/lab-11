package org.example.lab_11;

public interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);

    void notifyObservers();
}
