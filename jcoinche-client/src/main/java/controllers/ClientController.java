package controllers;

import com.esotericsoftware.kryonet.Connection;
import interfaces.IClientCommandListener;
import interfaces.IClientRequestListener;
import kryo.KryoClientController;
import kryo.KryoUtils;
import models.*;

import java.util.ArrayList;

/**
 * The core of the client
 * Handle packets from the KryoClientController and redirect it to the associated sub-controllers
 */
public class ClientController {

    private KryoClientController mKryoClientController;
    private CmdController mCmdController;
    private boolean mMustRun;
    private boolean mIsLogged;
    private boolean mIsInARoom;
    private boolean mIsInAGame;
    private boolean mIsInABidPhase;
    private boolean mIsInACoinchePhase;
    private boolean mIsInAGamePhase;

    private String mNickName;

    private ArrayList<Contract> mActualBidList;
    private ArrayList<Card> mActualHand;

    private int mPort;
    private String mHost;

    private IClientCommandListener mClientCommandListener = new IClientCommandListener() {

        @Override
        public void onQuitCommand() {
            System.out.println("Bye Bye !");
            System.out.println("- - - - - - - - - - - - - - -");
            stop();
            mMustRun = false;
        }

        @Override
        public void onNickNameEntered(String nick) {
            mNickName = nick;
            sendRequest(mKryoClientController.getConnection(), 200, nick);
        }

        @Override
        public void onHelpCommand() {
            System.out.println("// JCoinche server v0.1 \\\\");
            System.out.println("• Basic commands :");
            System.out.println("/connect [host] [port] - Join a server. If you don't specify a host and port, defaults are localhost and 0xC4F3");
            System.out.println("/disconnect - Quit a server");
            System.out.println("/quit - Quit the program");
            System.out.println("• Lobby commands :");
            System.out.println("/create [roomName] - Create a room");
            System.out.println("/join [roomName] - Join a room");
            System.out.println("/list - List available rooms");
            System.out.println("/displayRoom [roomName] - Display room content");
            System.out.println("• Room commands :");
            System.out.println("/leave - Leave the room");
            System.out.println("/ready - Notify the server that you're ready to play");
            System.out.println("• Bid commands :");
            System.out.println("/bid [TYPE] [BID_VALUE] - Place a bid with the asset TYPE and the value BID_VALUE");
            System.out.println("Type available : SPADE, HEART, DIAMOND, CLUB, ALL_ASSET, WITHOUT_ASSET, CAPOT, CHECK");
            System.out.println("• Play commands :");
            System.out.println("/bid [TYPE] [CARD_VALUE] - Play a card with the color TYPE and the value CARD_VALUE");
            System.out.println("Type available : SPADE, HEART, DIAMOND, CLUB");
            System.out.println("Value available : SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE");
            System.out.println("- - - - - - - - - - - - - - -");
        }

        @Override
        public void onConnectCommand(String host, int port) {
            setServerInfos(host, port);
            start();
        }

        @Override
        public void onDisconnectCommand() {
            System.out.println("INFO: You're now disconnected from the server");
            System.out.println("- - - - - - - - - - - - - - -");
            stop();
        }

        @Override
        public void onCreateRoomCommand(String roomName) {
            sendRequest(mKryoClientController.getConnection(), 201, roomName);
        }

        @Override
        public void onJoinRoomCommand(String roomName) {
            sendRequest(mKryoClientController.getConnection(), 202, roomName);
        }

        @Override
        public void onLeaveRoomCommand() {
            sendRequest(mKryoClientController.getConnection(), 203, null);
        }

        @Override
        public void onListRoomCommand() {
            sendRequest(mKryoClientController.getConnection(), 204, null);
        }

        @Override
        public void onDisplayRoomCommand(String roomName) {
            sendRequest(mKryoClientController.getConnection(), 205, roomName);
        }

        @Override
        public void onReadyToPlayCommand() {
            sendRequest(mKryoClientController.getConnection(), 206, mNickName);
        }

        @Override
        public void onBidCommand(Contract.AssetType asset, int value) {
            Contract contract = new Contract(asset, value);
            if (contract.mAsset == Contract.AssetType.TEST) {
                mIsInABidPhase = false;
                System.out.println("INFO: Contract successfully placed");
                System.out.println("- - - - - - - - - - - - - - -");
                return;
            }
            if (contract.mAsset == Contract.AssetType.CHECK) {
                sendRequest(mKryoClientController.getConnection(), 207, contract);
                mIsInABidPhase = false;
                System.out.println("INFO: Contract successfully placed");
                System.out.println("- - - - - - - - - - - - - - -");
                return;
            }
            if (mActualBidList.size() == 0) {
                if (value >= 80 && value <= 160) {
                    sendRequest(mKryoClientController.getConnection(), 207, contract);
                    mIsInABidPhase = false;
                    System.out.println("INFO: Contract successfully placed");
                    System.out.println("- - - - - - - - - - - - - - -");
                } else {
                    System.out.println("INFO: Wrong bid value");
                }
            } else {
                int refValue = mActualBidList.get(mActualBidList.size() - 1).mValue;
                if (value - 10 >= refValue) {
                    sendRequest(mKryoClientController.getConnection(), 207, contract);
                    mIsInABidPhase = false;
                    System.out.println("INFO: Contract successfully placed");
                    System.out.println("- - - - - - - - - - - - - - -");
                } else {
                    System.out.println("INFO: Wrong bid value");
                }
            }
        }

        @Override
        public void onCoincheCommand(Boolean isCoinched) {
            mIsInACoinchePhase = false;
            sendRequest(mKryoClientController.getConnection(), 210, isCoinched);
        }

        @Override
        public void onPlayCommand(Card.ColorType colorType, Card.ValueType valueType) {
            Card newCard = new Card(colorType, valueType);
            boolean isCardIsPresentInHand = false;
            for (Card card : mActualHand) {
                if (newCard.mCardValue == card.mCardValue && newCard.mColorType == card.mColorType) {
                    isCardIsPresentInHand = true;
                }
            }
            if (!isCardIsPresentInHand) {
                System.out.println("INFO: This card is not in your hand, you cannot play it");
            } else {
                mIsInAGamePhase = false;
                System.out.println("INFO: Card successfully played");
                System.out.println("- - - - - - - - - - - - - - -");
                sendRequest(mKryoClientController.getConnection(), 209, newCard);
            }
        }
    };

    private IClientRequestListener mClientRequestListener = new IClientRequestListener() {

        @Override
        public void onClientDisconnected() {
            System.out.println("INFO: You're now disconnected from the server");
            System.out.println("- - - - - - - - - - - - - - -");
            stop();
        }

        @Override
        public void onPacket100Received() {
            System.out.println("INFO: You're now connected to the server");
            System.out.println("Please, choose a nickname :");
        }

        @Override
        public void onPacket101Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof Lobby) {
                Lobby lobby = (Lobby) coincheRequest.mPacket;
                System.out.println("INFO: Hello " + mNickName + " you're now logged in the server\n");
                System.out.println("Room(s) available :");
                for (Room room : lobby.mRooms) {
                    System.out.println(room.mName);
                }
                System.out.println("- - - - - - - - - - - - - - -");
                System.out.println("Player(s) online :");
                for (Player player : lobby.mPlayers) {
                    System.out.println(player.mName);
                }
                mIsLogged = true;
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket102Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof Lobby) {
                Lobby lobby = (Lobby) coincheRequest.mPacket;
                System.out.println("INFO: Room created\n");
                System.out.println("Room(s) available :");
                for (Room room : lobby.mRooms) {
                    System.out.println(room.mName);
                }
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket103Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof Room) {
                mIsInARoom = true;
                Room room = (Room) coincheRequest.mPacket;
                System.out.println("INFO: You're now connected to the room " + room.mName + "\n");
                System.out.println("Player(s) are :");
                for (Team team : room.mTeams) {
                    System.out.println("Team " + team.mId + ":");
                    for (Player player : team.mPlayers) {
                        System.out.println(player.mName);
                    }
                }
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket104Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof String) {
                String roomName = (String) coincheRequest.mPacket;
                mIsInARoom = false;
                System.out.println("INFO: You are leaving the room " + roomName);
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket105Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof Lobby) {
                Lobby lobby = (Lobby) coincheRequest.mPacket;
                System.out.println("Room(s) available :");
                for (Room room : lobby.mRooms) {
                    System.out.println(room.mName);
                }
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket106Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof Room) {
                Room room = (Room) coincheRequest.mPacket;
                System.out.println("INFO: " + room.mName + " desc :");
                System.out.println("Player(s) are :");
                for (Team team : room.mTeams) {
                    System.out.println("Team " + team.mId + ":");
                    for (Player player : team.mPlayers) {
                        System.out.println(player.mName);
                    }
                }
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket107Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof ArrayList) {
                ArrayList<Card> hand = (ArrayList<Card>) coincheRequest.mPacket;
                System.out.println();
                System.out.println("♠ ♥ ♦ ♣ BID PHASE ♠ ♥ ♦ ♣");
                System.out.println("Your Hand :");
                for (Card card : hand) {
                    System.out.print(card.mColorType.toString() + card.mCardValue.toString() + " ");
                }
                System.out.println();
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }
        @Override
        public void onPacker108Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof ArrayList) {
                ArrayList<Contract> bidList = (ArrayList<Contract>) coincheRequest.mPacket;
                mIsInABidPhase = true;
                mActualBidList = bidList;
                System.out.println();
                System.out.println("It's your turn to make a contract");
                if (!bidList.isEmpty()) {
                    System.out.println("//Actual contracts\\\\");
                    for (Contract contract : bidList) {
                        System.out.println(contract.mOwnerName + " place this bid :");
                        System.out.println("Asset type : " + contract.mAsset);
                        System.out.println("Value : " + contract.mValue);
                    }
                }
                System.out.println();
                System.out.println("use /bid [ASSET] [value] to choose an asset and a value for your contract");
                System.out.println("Ex : /bid HEART 80");
                System.out.println();
                System.out.println("//Assets available\\\\");
                System.out.println("SPADE, HEART, DIAMOND, CLUB, ALL_ASSET, WITHOUT_ASSET, CHECK");
                System.out.println("Value must be 10 more than the biggest contract value");
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacker109Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof ArrayList) {
                ArrayList<Card> hand = (ArrayList<Card>) coincheRequest.mPacket;
                mActualHand = hand;
                System.out.println();
                System.out.println("♠ ♥ ♦ ♣ GAME PHASE ♠ ♥ ♦ ♣");
                System.out.println("Your Hand :");
                for (Card card : hand) {
                    System.out.print(card.mColorType.toString() + card.mCardValue.toString() + " ");
                }
                System.out.println();
                System.out.println("- - - - - - - - - - - - - - -");
                System.out.println();
            }
        }

        @Override
        public void onPacker110Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof ArrayList) {
                ArrayList<Card> hand = (ArrayList<Card>) coincheRequest.mPacket;
                mActualHand = hand;
                mIsInAGamePhase = true;
                System.out.println();
                System.out.println("It's your turn to play");
                System.out.println();
                System.out.println("use /play [COLOR] [value] to choose a card");
                System.out.println("Ex : /play HEART KING");
                System.out.println();
                if (!mActualHand.isEmpty()) {
                    System.out.println("//Your hand\\\\");
                    for (Card card : mActualHand) {
                        System.out.print(card.mColorType.toString() + card.mCardValue.toString() + " ");
                    }
                }
                System.out.println();
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacker111Received() {
            System.out.println();
            System.out.println("INFO: Waiting for the leader of the defendant team ...");
            System.out.println("- - - - - - - - - - - - - - -");
        }

        @Override
        public void onPacker112Received() {
            mIsInACoinchePhase = true;
            System.out.println();
            System.out.println("INFO: Would you coinche the contract ?");
            System.out.println();
            System.out.println("Use the /coinche YES|NO command");
            System.out.println("- - - - - - - - - - - - - - -");
        }

        @Override
        public void onPacket500Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof String) {
                String playerName = (String) coincheRequest.mPacket;
                System.out.println("INFO: " + playerName + " joined this room");
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket501Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof String) {
                String playerName = (String) coincheRequest.mPacket;
                System.out.println("INFO: " + playerName + " leaved this room");
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket502Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof String) {
                String playerName = (String) coincheRequest.mPacket;
                System.out.println("INFO: " + playerName + " is ready to play");
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket503Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof Integer) {
                Integer playerReadyNbr = (Integer) coincheRequest.mPacket;
                int playersNotReady = 4 - playerReadyNbr;
                System.out.println("INFO: The room is not ready yet. Still waiting for " + playersNotReady + "/4 player(s)");
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket504Received() {
            System.out.println("INFO: Players are ready. Starting the game ! ♠ ♥ ♦ ♣");
            mIsInAGame = true;
        }

        @Override
        public void onPacket505Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof Contract) {
                Contract contract = (Contract) coincheRequest.mPacket;
                System.out.println("INFO: " + contract.mOwnerName + " place a contract");
                System.out.println("Contract type : " + contract.mAsset + " - Contract value : " + contract.mValue);
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket506Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof String) {
                String nickName = (String) coincheRequest.mPacket;
                System.out.println("INFO: Bid phase in progress, it’s the turn to " + nickName + " to play, waiting ...");
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket507Received() {
            System.out.println();
            System.out.println("INFO: Everybody have checked. New bid phase. Redrawing hands ...");
            System.out.println("- - - - - - - - - - - - - - -");
            mActualBidList.clear();
        }

        @Override
        public void onPacket508Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof Team) {
                Team team = (Team) coincheRequest.mPacket;
                System.out.println();
                System.out.println("INFO: The team " + team.mId + " set the biggest contract");
                System.out.println("Contract type : " + team.mContract.mAsset + " - Contract value : " + team.mContract.mValue);
                System.out.println("//Attacker team :");
                for (Player atkPl : team.mPlayers) {
                    System.out.println(atkPl.mName);
                }
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket509Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof String) {
                String nickName = (String) coincheRequest.mPacket;
                System.out.println("INFO: Game phase in progress, it’s the turn to " + nickName + " to play, waiting ...");
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket510Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof Card) {
                Card card = (Card) coincheRequest.mPacket;
                System.out.println("INFO: " + card.mOwnerName + " play a card");
                System.out.println("Card color type : " + card.mColorType.toString() + " - card value : " + card.mCardValue.toString());
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket511Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof ArrayList) {
                ArrayList<Team> teams = (ArrayList<Team>) coincheRequest.mPacket;
                System.out.println();
                System.out.println("INFO: End of the set");
                System.out.println("Scores :");
                for (Team team : teams) {
                    System.out.println("Team " + team.mId);
                    System.out.println("Score : " + team.mScore);
                    if (team.mId != 1) {
                        System.out.println();
                    }
                }
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket512Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof ArrayList) {
                ArrayList<Team> teams = (ArrayList<Team>) coincheRequest.mPacket;
                System.out.println();
                System.out.println("♠ ♥ ♦ ♣ End of the round ♠ ♥ ♦ ♣");
                System.out.println();
                for (Team team : teams) {
                    if (team.isWonTheSet) {
                        System.out.println("The team " + team.mId + " win the round");
                    }
                }
                System.out.println("Scores :");
                for (Team team : teams) {
                    System.out.println("Team " + team.mId);
                    System.out.println("Score : " + team.mScore);
                    if (team.mId == 0) {
                        System.out.println();
                    }
                }
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket513Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof Team) {
                Team winnerTeam = (Team) coincheRequest.mPacket;
                System.out.println();
                System.out.println("♠ ♥ ♦ ♣ End of the game ♠ ♥ ♦ ♣");
                System.out.println();
                System.out.println("The team " + winnerTeam.mId + " win the game. Congrats !");
                System.out.println();
                System.out.println("Ending the game, returning to the room ...");
                System.out.println("- - - - - - - - - - - - - - -");
                mActualBidList.clear();
                mActualHand.clear();
                mIsInAGame = false;
                mIsInABidPhase = false;
                mIsInACoinchePhase = false;
                mIsInAGamePhase = false;
            }
        }

        @Override
        public void onPacket514Received(CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof Boolean) {
                Boolean isCoinched = (Boolean) coincheRequest.mPacket;
                if (isCoinched) {
                    System.out.println();
                    System.out.println("INFO: The defendant team choose to coinche the contract");
                    System.out.println("- - - - - - - - - - - - - - -");
                }
                sendRequest(mKryoClientController.getConnection(), 208, null);
            }
        }

        @Override
        public void onPacket600Received() {
            System.out.println("INFO: This nickname is already taken");
            System.out.println("Please, choose another nickname :");
        }

        @Override
        public void onPacket601Received() {
            System.out.println("INFO: This room name is already taken");
            System.out.println("- - - - - - - - - - - - - - -");
        }

        @Override
        public void onPacket602Received() {
            System.out.println("INFO: This room is not reachable");
            System.out.println("- - - - - - - - - - - - - - -");
        }

        @Override
        public void onPacket603Received() {
            System.out.println("INFO: This room is not reachable");
            System.out.println("- - - - - - - - - - - - - - -");
        }

        @Override
        public void onPacket604Received() {
            System.out.println("INFO: To few players in the room. The game must be stopped :(");
            System.out.println("- - - - - - - - - - - - - - -");
            mIsInAGame = false;
            mIsInABidPhase = false;
            mIsInACoinchePhase = false;
            mIsInAGamePhase = false;
        }
    };

    /**
     * Init the ClientController by creating KryoClientController
     */
    public ClientController() {
        System.out.println(".::THE AMAZING JCOINCHE GAME::.");
        System.out.println("INFO: Please use the /help cmd if you don't know what to do");

        mKryoClientController = new KryoClientController(this);
        mCmdController = new CmdController(this);
        mMustRun = true;
        mActualBidList = new ArrayList<>();
        mActualHand = new ArrayList<>();

        KryoUtils.registerUtilClasses(mKryoClientController.getKryo());
    }

    /**
     * Set the host and the port of the client
     * @param host
     * @param port
     */
    public void setServerInfos(String host, int port) {
        mPort = port != 0 ? port : 0xC4F3;
        mHost = host != null ? host : "localhost";
    }

    /**
     * Start the ClientController by launching the connection to host:port
     * Init the KryoClientController packet listener
     */
    public void start() {
        mKryoClientController.getClient().start();
        try {
            System.out.println(mHost + " " + mPort);
            mKryoClientController.getClient().connect(5000, mHost, mPort);
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
            System.exit(84);
        }
        mKryoClientController.initPacketListener();
    }

    /**
     * Stopping the ClientC by removing the packet listener and canceling the actual connexion
     * Clear the state machine, and the potencial hand and bid list of the client
     */
    public void stop() {
        mKryoClientController.removePacketListener();
        mKryoClientController.getClient().stop();
        mActualBidList.clear();
        mActualHand.clear();
        mIsLogged = false;
        mIsInARoom = false;
        mIsInAGame = false;
        mIsInABidPhase = false;
        mIsInACoinchePhase = false;
        mIsInAGamePhase = false;
    }

    /**
     * Send a packet to the client pointed by 'connection' with the specified request code and content
     *
     * @param connection
     * @param requestCode
     * @param packet
     */
    public void sendRequest(Connection connection, int requestCode, Object packet) {
        CoincheRequest coincheRequest = new CoincheRequest();
        coincheRequest.mRequestId = requestCode;
        coincheRequest.mPacket = packet;
        mKryoClientController.sendRequest(connection, coincheRequest);
    }

    /**
     * Return true if the client is actually connected to the server
     * @return
     */
    public boolean isConnected() {
        return mKryoClientController.getClient().isConnected();
    }

    /**
     * Return true if the client is actually logged to the server
     * @return
     */
    public boolean isLogged() {
        return mIsLogged;
    }

    /**
     * Return true if the client is actually in a room
     * @return
     */
    public boolean isInARoom() {
        return mIsInARoom;
    }

    /**
     * Return true if the client is actually in a game
     * @return
     */
    public boolean isInAGame() {
        return mIsInAGame;
    }

    /**
     * Return true if the client is actually in a bid phase
     * @return
     */
    public boolean isInABidPhase() {
        return mIsInABidPhase;
    }

    /**
     * Return true if the client is actually in a coinche phase
     * @return
     */
    public boolean isInACoinchePhase() {
        return mIsInACoinchePhase;
    }

    /**
     * Return true if the client is actually in a game phase
     * @return
     */
    public boolean isInAGamePhase() {
        return mIsInAGamePhase;
    }

    /**
     * Return true if the client still must run
     * @return
     */
    public boolean mustRun() {
        return mMustRun;
    }

    public CmdController getCmdController() {
        return mCmdController;
    }

    public IClientCommandListener getClientCommandListener() {
        return mClientCommandListener;
    }

    public IClientRequestListener getClientRequestListener() {
        return mClientRequestListener;
    }

    public void setNickName(String nick) {
        mNickName = nick;
    }

    public void setActualHand(ArrayList<Card> hand) {
        mActualHand = hand;
    }
}
