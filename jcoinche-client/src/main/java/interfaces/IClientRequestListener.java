package interfaces;

import models.CoincheRequest;

/**
 * Notify The ClientController that there's a new package to treat
 */
public interface IClientRequestListener {
    /**
     * Notify the ClientController that the client have been disconnected from the server
     */
    void onClientDisconnected();

    /**
     * Notify the ClientController that the client is connected to the server
     */
    void onPacket100Received();

    /**
     * Notify the ClientController that the client is logged into the server
     * @param coincheRequest
     */
    void onPacket101Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that the client create a new room
     * @param coincheRequest
     */
    void onPacket102Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that the client is affected to a room
     * @param coincheRequest
     */
    void onPacket103Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that the client have successfully leave a room
     * @param coincheRequest
     */
    void onPacket104Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that the client have successfully received the lobby
     * @param coincheRequest
     */
    void onPacket105Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that the client have successfully received the content of a room
     * @param coincheRequest
     */
    void onPacket106Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that the client have successfully received his hand
     * @param coincheRequest
     */
    void onPacket107Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that it's the client's bid turn
     * @param coincheRequest
     */
    void onPacker108Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that the game phase begin
     * @param coincheRequest
     */
    void onPacker109Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that it's the client's game turn
     * @param coincheRequest
     */
    void onPacker110Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that the client must be waiting for the defendant team
     */
    void onPacker111Received();

    /**
     * Notify the ClientController that the client can coinche the actual bid
     */
    void onPacker112Received();

    /**
     * Notify the ClientController that a new client join the actual room
     * @param coincheRequest
     */
    void onPacket500Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that a client leave the actual room
     * @param coincheRequest
     */
    void onPacket501Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that a client is ready to play
     * @param coincheRequest
     */
    void onPacket502Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that the client must still waiting for players to start the game
     * @param coincheRequest
     */
    void onPacket503Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that the game phase begin
     */
    void onPacket504Received();

    /**
     * Notify the ClientController that a bid has been placed by a player
     * @param coincheRequest
     */
    void onPacket505Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that the client must wait because it's the bid turn of another player
     * @param coincheRequest
     */
    void onPacket506Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that everybody have checked on a bid phase
     */
    void onPacket507Received();

    /**
     * Notify the ClientController that the bid phase is ending
     * @param coincheRequest
     */
    void onPacket508Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that the client must wait because it's the game turn of another player
     * @param coincheRequest
     */
    void onPacket509Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that a card has been placed on the play mat
     * @param coincheRequest
     */
    void onPacket510Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that it's the end of a game round
     * @param coincheRequest
     */
    void onPacket511Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that it's the end of a game set
     * @param coincheRequest
     */
    void onPacket512Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that it's the end of the game
     * @param coincheRequest
     */
    void onPacket513Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that the actual bid is / is not coinched
     * @param coincheRequest
     */
    void onPacket514Received(CoincheRequest coincheRequest);

    /**
     * Notify the ClientController that the client's nick is already taken
     */
    void onPacket600Received();

    /**
     * Notify the ClientController that the room's name is already taken
     */
    void onPacket601Received();

    /**
     * Notify the ClientController that the room he wants to join is unreachable
     */
    void onPacket602Received();

    /**
     * Notify the ClientController that the room he wants to show is unreachable
     */
    void onPacket603Received();

    /**
     * Notify the ClientController that there is not enough player on the game anymore
     */
    void onPacket604Received();
}
