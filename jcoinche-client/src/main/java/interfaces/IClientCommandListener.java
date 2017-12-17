package interfaces;

import models.Card;
import models.Contract;

/**
 * Notify The ClientController that there's a new command to treat
 */
public interface IClientCommandListener {
    void onQuitCommand();
    void onNickNameEntered(String nick);
    void onHelpCommand();
    void onConnectCommand(String host, int port);
    void onDisconnectCommand();
    void onCreateRoomCommand(String roomName);
    void onJoinRoomCommand(String roomName);
    void onLeaveRoomCommand();
    void onListRoomCommand();
    void onDisplayRoomCommand(String roomName);
    void onReadyToPlayCommand();
    void onBidCommand(Contract.AssetType asset, int value);
    void onCoincheCommand(Boolean isCoinched);
    void onPlayCommand(Card.ColorType colorType, Card.ValueType valueType);
}
