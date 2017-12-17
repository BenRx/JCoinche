package kryo;

import controllers.ClientController;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import models.*;

/**
 * Control the Kryo library part of the client
 */
public class KryoClientController {

    private ClientController mClientController;
    private Client mClient;
    private Listener mPacketListener;
    private Connection mConnection;

    public KryoClientController(ClientController clientController) {
        mClient = new Client();
        mClientController = clientController;
        mPacketListener = new Listener() {

            public void disconnected (Connection connection) {
                mClientController.getClientRequestListener().onClientDisconnected();
            }

            public void received (Connection connection, Object object) {
                mConnection = connection;
                if (object instanceof CoincheRequest) {
                    CoincheRequest coincheRequest = (CoincheRequest) object;
                    switch (coincheRequest.mRequestId) {
                        case 100:
                            mClientController.getClientRequestListener().onPacket100Received();
                            break;
                        case 101:
                            mClientController.getClientRequestListener().onPacket101Received(coincheRequest);
                            break;
                        case 102:
                            mClientController.getClientRequestListener().onPacket102Received(coincheRequest);
                            break;
                        case 103:
                            mClientController.getClientRequestListener().onPacket103Received(coincheRequest);
                            break;
                        case 104:
                            mClientController.getClientRequestListener().onPacket104Received(coincheRequest);
                            break;
                        case 105:
                            mClientController.getClientRequestListener().onPacket105Received(coincheRequest);
                            break;
                        case 106:
                            mClientController.getClientRequestListener().onPacket106Received(coincheRequest);
                            break;
                        case 107:
                            mClientController.getClientRequestListener().onPacket107Received(coincheRequest);
                            break;
                        case 108:
                            mClientController.getClientRequestListener().onPacker108Received(coincheRequest);
                            break;
                        case 109:
                            mClientController.getClientRequestListener().onPacker109Received(coincheRequest);
                            break;
                        case 110:
                            mClientController.getClientRequestListener().onPacker110Received(coincheRequest);
                            break;
                        case 111:
                            mClientController.getClientRequestListener().onPacker111Received();
                            break;
                        case 112:
                            mClientController.getClientRequestListener().onPacker112Received();
                            break;
                        case 500:
                            mClientController.getClientRequestListener().onPacket500Received(coincheRequest);
                            break;
                        case 501:
                            mClientController.getClientRequestListener().onPacket501Received(coincheRequest);
                            break;
                        case 502:
                            mClientController.getClientRequestListener().onPacket502Received(coincheRequest);
                            break;
                        case 503:
                            mClientController.getClientRequestListener().onPacket503Received(coincheRequest);
                            break;
                        case 504:
                            mClientController.getClientRequestListener().onPacket504Received();
                            break;
                        case 505:
                            mClientController.getClientRequestListener().onPacket505Received(coincheRequest);
                            break;
                        case 506:
                            mClientController.getClientRequestListener().onPacket506Received(coincheRequest);
                            break;
                        case 507:
                            mClientController.getClientRequestListener().onPacket507Received();
                            break;
                        case 508:
                            mClientController.getClientRequestListener().onPacket508Received(coincheRequest);
                            break;
                        case 509:
                            mClientController.getClientRequestListener().onPacket509Received(coincheRequest);
                            break;
                        case 510:
                            mClientController.getClientRequestListener().onPacket510Received(coincheRequest);
                            break;
                        case 511:
                            mClientController.getClientRequestListener().onPacket511Received(coincheRequest);
                            break;
                        case 512:
                            mClientController.getClientRequestListener().onPacket512Received(coincheRequest);
                            break;
                        case 513:
                            mClientController.getClientRequestListener().onPacket513Received(coincheRequest);
                            break;
                        case 514:
                            mClientController.getClientRequestListener().onPacket514Received(coincheRequest);
                            break;
                        case 600:
                            mClientController.getClientRequestListener().onPacket600Received();
                            break;
                        case 601:
                            mClientController.getClientRequestListener().onPacket601Received();
                            break;
                        case 602:
                            mClientController.getClientRequestListener().onPacket602Received();
                            break;
                        case 603:
                            mClientController.getClientRequestListener().onPacket603Received();
                            break;
                        case 604:
                            mClientController.getClientRequestListener().onPacket604Received();
                            break;
                        default:
                            System.out.println("INFO: Unknown server request received");
                            break;
                    }
                }
            }
        };
    }

    /**
     * Init the KryoClientController packet listener
     */
    public void initPacketListener() {
        mClient.addListener(mPacketListener);
    }

    /**
     * Remove the KryoClientController packet listener
     */
    public void removePacketListener() {
        mClient.removeListener(mPacketListener);
    }

    /**
     * Send the packet 'coincheRequest' to the server pointed by the object 'connection'
     */
    public void sendRequest(Connection connection, CoincheRequest coincheRequest) {
        connection.sendTCP(coincheRequest);
    }

    /**
     * Return the Kryo element of the KryoClientController
     * @return
     */
    public Kryo getKryo() {
        return mClient.getKryo();
    }

    /**
     * Return the Client element of the KryoClientController
     * @return
     */
    public Client getClient() {
        return mClient;
    }

    /**
     * Return the actual server connection
     * @return
     */
    public Connection getConnection() {
        return mConnection;
    }
}
