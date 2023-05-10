package com.safra.events;

public class ConnectivityChangedEvent {

    boolean isConnected;

    public ConnectivityChangedEvent(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

}
