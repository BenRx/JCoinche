package com.rx.ben.jcoincheapp.kryo;

import android.util.Log;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.rx.ben.jcoincheapp.models.CoincheRequest;
import com.rx.ben.jcoincheapp.interfaces.IClientRequestListener;

public class KryoClientController {

    private Client mClient;
    private Listener mPacketListener;
    private Connection mConnection;
    private IClientRequestListener mClientRequestListener;

    public KryoClientController(IClientRequestListener clientRequestListener) {
        mClient = new Client();
        mClientRequestListener = clientRequestListener;
        mPacketListener = new Listener() {

            public void connected(Connection connection) {
                mClientRequestListener.onClientConnected();
            }

            public void disconnected(Connection connection) {
                mClientRequestListener.onClientDisconnected();
            }

            public void received (Connection connection, Object object) {
                mConnection = connection;
                if (object instanceof CoincheRequest) {
                    CoincheRequest coincheRequest = (CoincheRequest) object;
                    Log.d("KryoClientController", "INFO: Server request received " + coincheRequest.mRequestId);
                    switch (coincheRequest.mRequestId) {
                        case 101:
                            mClientRequestListener.onPacket101Received(coincheRequest);
                            break;
                        case 600:
                            mClientRequestListener.onPacket600Received();
                        default:
                            Log.e("KryoClientController", "INFO: Unknown server request received");
                            break;
                    }
                }
            }
        };
    }

    public void initPacketListener() {
        mClient.addListener(mPacketListener);
    }

    public void removePacketListener() {
        mClient.removeListener(mPacketListener);
    }

    public void sendRequest(Connection connection, CoincheRequest coincheRequest) {
        connection.sendTCP(coincheRequest);
    }

    public Kryo getKryo() {
        return mClient.getKryo();
    }

    public Client getClient() {
        return mClient;
    }

    public Connection getConnection() {
        return mConnection;
    }
}
