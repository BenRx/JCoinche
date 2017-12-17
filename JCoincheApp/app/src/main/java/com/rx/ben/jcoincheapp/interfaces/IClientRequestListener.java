package com.rx.ben.jcoincheapp.interfaces;

import com.rx.ben.jcoincheapp.models.CoincheRequest;

public interface IClientRequestListener {
    void onClientConnected();
    void onClientDisconnected();
    void onPacket101Received(CoincheRequest coincheRequest);
    void onPacket600Received();
}
