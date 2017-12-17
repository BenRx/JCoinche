package kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import models.*;
import controllers.*;

/**
 * Control the Kryo library part of the server
 */
public class KryoServerController {
    private Server mServer;
    private Listener mPacketListener;
    private ServerController mServerController;

    public KryoServerController(ServerController serverController) {
        mServer = new Server();
        mServerController = serverController;
        mPacketListener = new Listener() {

            public void connected(Connection connection) {
                mServerController.getServerRequestListener().onClientConnexion(connection);
            }

            public void disconnected (Connection connection) {
                mServerController.getServerRequestListener().onClientDisconnection(connection);
            }

            public void received (Connection connection, Object object) {
                if (object instanceof CoincheRequest) {
                    CoincheRequest coincheRequest = (CoincheRequest) object;
                    System.out.println("controllers : request " + coincheRequest.mRequestId + " received");
                    switch (coincheRequest.mRequestId) {
                        case 200:
                            mServerController.getServerRequestListener().onPacket200Received(connection, coincheRequest);
                            break;
                        case 201:
                            mServerController.getServerRequestListener().onPacket201Received(connection, coincheRequest);
                            break;
                        case 202:
                            mServerController.getServerRequestListener().onPacket202Received(connection, coincheRequest);
                            break;
                        case 203:
                            mServerController.getServerRequestListener().onPacket203Received(connection);
                            break;
                        case 204:
                            mServerController.getServerRequestListener().onPacket204Received(connection);
                            break;
                        case 205:
                            mServerController.getServerRequestListener().onPacket205Received(connection, coincheRequest);
                            break;
                        case 206:
                            mServerController.getServerRequestListener().onPacket206Received(connection, coincheRequest);
                            break;
                        case 207:
                            mServerController.getServerRequestListener().onPacket207Received(connection, coincheRequest);
                            break;
                        case 208:
                            mServerController.getServerRequestListener().onPacket208Received(connection);
                            break;
                        case 209:
                            mServerController.getServerRequestListener().onPacket209Received(connection, coincheRequest);
                            break;
                        case 210:
                            mServerController.getServerRequestListener().onPacket210Received(connection, coincheRequest);
                            break;
                        default:
                            System.out.println("controllers : Unknown request received");
                            break;
                    }
                }
            }
        };
    }

    /**
     * Init the KryoServerController packet listener
     */
    public void initPacketListener() {
        mServer.addListener(mPacketListener);
    }

    /**
     * Remove the KryoServerController packet listener
     */
    public void removePacketListener() {
        mServer.removeListener(mPacketListener);
    }

    /**
     * Send the packet 'coincheRequest' to the client pointed by the object 'connection'
     */
    public void sendRequest(Connection connection, CoincheRequest coincheRequest) {
        connection.sendTCP(coincheRequest);
    }

    /**
     * Return every open connections with the actual instance of the KryoServerController
     * @return
     */
    public Connection[] getConnections() {
        return mServer.getConnections();
    }

    /**
     * Return the Kryo element of the KryoServerController
     * @return
     */
    public Kryo getKryo() {
        return mServer.getKryo();
    }

    /**
     * Return the Server element of the KryoServerController
     * @return
     */
    public Server getServer() {
        return mServer;
    }

    public Listener getPacketListener() {
        return mPacketListener;
    }
}
