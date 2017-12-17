package interfaces;

import com.esotericsoftware.kryonet.Connection;
import models.CoincheRequest;

/**
 * Notify The ServerController that there's a new package to treat
 */
public interface IServerRequestListener {
    /**
     * Notify the ServerController that a new client is connected
     * @param c
     */
    void onClientConnexion(Connection c);

    /**
     * Notify the ServerController that a new client is connected
     * @param c
     */
    void onClientDisconnection(Connection c);

    /**
     * Notify the ServerController that a client c want to use a specified nickname
     * @param c
     * @param coincheRequest
     */
    void onPacket200Received(Connection c, CoincheRequest coincheRequest);

    /**
     * Notify the ServerController that a client c want to create a room
     * @param c
     * @param coincheRequest
     */
    void onPacket201Received(Connection c, CoincheRequest coincheRequest);

    /**
     * Notify the ServerController that a client c want to join a room
     * @param c
     * @param coincheRequest
     */
    void onPacket202Received(Connection c, CoincheRequest coincheRequest);

    /**
     * Notify the ServerController that a client c want to leave a room
     * @param c
     */
    void onPacket203Received(Connection c);

    /**
     * Notify the ServerController that a client c want to see the lobby
     * @param c
     */
    void onPacket204Received(Connection c);

    /**
     * Notify the ServerController that a client c want to see a room content
     * @param c
     * @param coincheRequest
     */
    void onPacket205Received(Connection c, CoincheRequest coincheRequest);

    /**
     * Notify the ServerController that a client c is ready to play
     * @param c
     * @param coincheRequest
     */
    void onPacket206Received(Connection c, CoincheRequest coincheRequest);

    /**
     * Notify the ServerController that a client c want to place a new bid
     * @param c
     * @param coincheRequest
     */
    void onPacket207Received(Connection c, CoincheRequest coincheRequest);

    /**
     * Notify the ServerController that a client c is ready for the game phase
     * @param c
     */
    void onPacket208Received(Connection c);

    /**
     * Notify the ServerController that a client c want to add a card on the play mat
     * @param c
     * @param coincheRequest
     */
    void onPacket209Received(Connection c, CoincheRequest coincheRequest);

    /**
     * Notify the ServerController that a client c want to change the coinche status of the actual bid
     * @param c
     * @param coincheRequest
     */
    void onPacket210Received(Connection c, CoincheRequest coincheRequest);
}
