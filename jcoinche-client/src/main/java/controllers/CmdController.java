package controllers;

import models.Card;
import models.Contract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Parse the standard input to redirect commands to the ClientController
 */
public class CmdController {
    private ClientController mClientController;
    private BufferedReader mBufferReader;
    private String[] mInputs;

    /**
     * Init the CmdController by creating a standard input buffer reader
     * @param clientController
     */
    public CmdController(ClientController clientController) {
        mClientController = clientController;
        mBufferReader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Get the actual line of the standard input and parse it
     */
    public void getCmd() {
        try {
            String input = mBufferReader.readLine();
            mInputs = input.split(" ");
        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
            System.exit(84);
        }

        if (mInputs.length > 0) {
            if (basicCmds() == 1) {
                return;
            }
            if (!mClientController.isConnected()) {
                if (mInputs[0].equals("/connect")) {
                    String host = "localhost";
                    int port = 0;
                    if (mInputs.length == 2) {
                        host = mInputs[1];
                        port = 0;
                    } else if (mInputs.length == 3) {
                        host = mInputs[1];
                        try {
                            port = Integer.parseInt(mInputs[2]);
                        } catch (Exception ignored) {
                            port = 0;
                        }
                    }
                    mClientController.getClientCommandListener().onConnectCommand(host, port);
                    return;
                }
            } else {
                if (mInputs[0].equals("/disconnect")) {
                    mClientController.getClientCommandListener().onDisconnectCommand();
                    return;
                }
                if (!mClientController.isLogged()) {
                    mClientController.getClientCommandListener().onNickNameEntered(mInputs[0]);
                    return;
                } else {
                    if (!mClientController.isInARoom()) {
                       if (lobbyCmds() == 1) {
                           return;
                       }
                    } else {
                        if (!mClientController.isInAGame()) {
                            if (roomCmds() == 1) {
                                return;
                            }
                        } else {
                            if (mClientController.isInABidPhase()) {
                                if (bidCmds() == 1) {
                                    return;
                                }
                            } else if (mClientController.isInACoinchePhase()) {
                                if (coincheCmds() == 1) {
                                    return;
                                }
                            } else if (mClientController.isInAGamePhase()) {
                                if (gameCmds() == 1) {
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("INFO: unknown command");
        }
    }

    private int gameCmds() {
        if (mInputs[0].equals("/play") && mInputs.length == 3) {
            String color = mInputs[1];
            String value = mInputs[2];
            Card.ColorType colorType = isAValidCardColorType(color);
            Card.ValueType valueType = isAValidCardValue(value);
            if (colorType != null) {
                if (valueType != null) {
                    mClientController.getClientCommandListener().onPlayCommand(colorType, valueType);
                    return 1;
                } else {
                    System.out.println("INFO: Invalid value");
                    return 1;
                }
            } else {
                System.out.println("INFO: Invalid color");
                return 1;
            }
        }
        return 0;
    }

    private int coincheCmds() {
        if (mInputs[0].equals("/coinche") && mInputs.length == 2) {
            Boolean response = isAValidResponse(mInputs[1]);
            if (response == null) {
                System.out.println("INFO: Invalid response");
                return 1;
            } else {
                mClientController.getClientCommandListener().onCoincheCommand(response);
                return 1;
            }
        }
        return 0;
    }

    private int bidCmds() {
        if (mInputs[0].equals("/bid") && mInputs.length >= 2 && mInputs.length < 4) {
            if (mInputs.length == 3) {
                String asset = mInputs[1];
                int bidVal;
                try {
                    bidVal = Integer.parseInt(mInputs[2]);
                } catch (Exception ignored) {
                    bidVal = 0;
                }
                Contract.AssetType assetType = isAValidAsset(asset);
                if (assetType != null && assetType != Contract.AssetType.CHECK) {
                    mClientController.getClientCommandListener().onBidCommand(assetType, bidVal);
                    return 1;
                } else {
                    System.out.println("INFO: Invalid asset type");
                    return 1;
                }
            } else {
                String asset = mInputs[1];
                Contract.AssetType assetType = isAValidAsset(asset);
                if (assetType != null && assetType == Contract.AssetType.CHECK) {
                    mClientController.getClientCommandListener().onBidCommand(assetType, 0);
                    return 1;
                } else {
                    System.out.println("INFO: Invalid asset type");
                    return 1;
                }
            }
        }
        return 0;
    }

    private int basicCmds() {
        if (mInputs[0].equals("/quit")) {
            mClientController.getClientCommandListener().onQuitCommand();
            return 1;
        }
        if (mInputs[0].equals("/help")) {
            mClientController.getClientCommandListener().onHelpCommand();
            return 1;
        }
        return 0;
    }

    private int lobbyCmds() {
        if (mInputs[0].equals("/create") && mInputs.length == 2) {
            String roomName = mInputs[1];
            mClientController.getClientCommandListener().onCreateRoomCommand(roomName);
            return 1;
        } else if (mInputs[0].equals("/join") && mInputs.length == 2) {
            String roomName = mInputs[1];
            mClientController.getClientCommandListener().onJoinRoomCommand(roomName);
            return 1;
        } else if (mInputs[0].equals("/list")) {
            mClientController.getClientCommandListener().onListRoomCommand();
            return 1;
        } else if (mInputs[0].equals("/displayRoom") && mInputs.length == 2) {
            String roomName = mInputs[1];
            mClientController.getClientCommandListener().onDisplayRoomCommand(roomName);
            return 1;
        }
        return 0;
    }

    private int roomCmds() {
        if (mInputs[0].equals("/leave")) {
            mClientController.getClientCommandListener().onLeaveRoomCommand();
            return 1;
        } else if (mInputs[0].equals("/ready")) {
            mClientController.getClientCommandListener().onReadyToPlayCommand();
            return 1;
        }
        return 0;
    }

    private Boolean isAValidResponse(String response) {
        if (response.equals("YES")) {
            return true;
        } else if (response.equals("NO")) {
            return false;
        } else {
            return null;
        }
    }

    private Contract.AssetType isAValidAsset(String asset) {
        switch (asset) {
            case "SPADE":
                return Contract.AssetType.SPADE;
            case "HEART":
                return Contract.AssetType.HEART;
            case "DIAMOND":
                return Contract.AssetType.DIAMOND;
            case "CLUB":
                return Contract.AssetType.CLUB;
            case "ALL_ASSET":
                return Contract.AssetType.ALL_ASSET;
            case "WITHOUT_ASSET":
                return Contract.AssetType.WITHOUT_ASSET;
            case "CHECK":
                return Contract.AssetType.CHECK;
            default:
                return null;
        }
    }

    private Card.ColorType isAValidCardColorType(String type) {
        switch (type) {
            case "SPADE":
                return Card.ColorType.SPADE;
            case "HEART":
                return Card.ColorType.HEART;
            case "DIAMOND":
                return Card.ColorType.DIAMOND;
            case "CLUB":
                return Card.ColorType.CLUB;
            default:
                return null;
        }
    }

    private Card.ValueType isAValidCardValue(String value) {
        switch (value) {
            case "SEVEN":
                return Card.ValueType.SEVEN;
            case "EIGHT":
                return Card.ValueType.EIGHT;
            case "NINE":
                return Card.ValueType.NINE;
            case "TEN":
                return Card.ValueType.TEN;
            case "JACK":
                return Card.ValueType.JACK;
            case "QUEEN":
                return Card.ValueType.QUEEN;
            case "KING":
                return Card.ValueType.KING;
            case "ACE":
                return Card.ValueType.ACE;
        }
        return null;
    }
}
