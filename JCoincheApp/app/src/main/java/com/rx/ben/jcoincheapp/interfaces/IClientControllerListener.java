package com.rx.ben.jcoincheapp.interfaces;

import com.rx.ben.jcoincheapp.models.CoincheRequest;

public interface IClientControllerListener {
    void onConnectionError();
    void onClientConnected();
    void onClientDisconnected();
    void onClientLogged(CoincheRequest coincheRequest);
    void onNickAlreadyUsedError();
}
