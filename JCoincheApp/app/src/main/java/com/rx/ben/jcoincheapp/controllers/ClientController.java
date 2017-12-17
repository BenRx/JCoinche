package com.rx.ben.jcoincheapp.controllers;

import android.util.Log;

import com.esotericsoftware.kryonet.Connection;
import com.rx.ben.jcoincheapp.interfaces.IClientControllerListener;
import com.rx.ben.jcoincheapp.interfaces.IClientRequestListener;
import com.rx.ben.jcoincheapp.kryo.KryoClientController;
import com.rx.ben.jcoincheapp.kryo.KryoUtils;
import com.rx.ben.jcoincheapp.models.CoincheRequest;

import java.io.IOException;

public class ClientController {

    private KryoClientController mKryoClientController;
    private String mHost;
    private int mPort;

    private String mNickName;

    private IClientControllerListener mClientControllerListener;
    private IClientRequestListener mClientRequestListener = new IClientRequestListener() {

        @Override
        public void onClientConnected() {
            mClientControllerListener.onClientConnected();
        }

        @Override
        public void onClientDisconnected() {
            mClientControllerListener.onClientDisconnected();
        }

        @Override
        public void onPacket101Received(CoincheRequest coincheRequest) {
            mClientControllerListener.onClientLogged(coincheRequest);
        }

        @Override
        public void onPacket600Received() {
            mClientControllerListener.onNickAlreadyUsedError();
        }
    };

    public ClientController(IClientControllerListener clientControllerListener, String host, int port) {
        mClientControllerListener = clientControllerListener;
        mHost = host;
        mPort = port;
        mKryoClientController = new KryoClientController(mClientRequestListener);
        KryoUtils.registerUtilClasses(mKryoClientController.getKryo());
    }

    public void start() {
        mKryoClientController.initPacketListener();
        Runnable connexionRunnable = new Runnable() {
            @Override
            public void run() {
                mKryoClientController.getClient().start();
                Log.d("ClientController", "Attempting connection to " + mHost + ":" + mPort);
                try {
                    mKryoClientController.getClient().connect(5000, mHost, mPort);
                } catch (IOException e) {
                    Log.e("ClientController", "Unable to join the foreign host : " + e.getMessage());
                    mClientControllerListener.onConnectionError();
                }
            }
        };
        Thread thread = new Thread(connexionRunnable);
        thread.start();
    }

    public void stop() {
        mKryoClientController.removePacketListener();
        mKryoClientController.getClient().stop();
    }

    public void sendRequest(final Connection connection, final int requestCode, final Object packet) {
        Runnable requestRunnable = new Runnable() {
            @Override
            public void run() {
                CoincheRequest coincheRequest = new CoincheRequest();
                coincheRequest.mRequestId = requestCode;
                coincheRequest.mPacket = packet;
                mKryoClientController.sendRequest(connection, coincheRequest);
            }
        };
        Thread thread = new Thread(requestRunnable);
        thread.start();
    }

    public KryoClientController getKryoClientController() {
        return mKryoClientController;
    }

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String nick) {
        mNickName = nick;
    }
}
