package controllers;

import com.esotericsoftware.kryonet.Connection;
import interfaces.IGameRequestListener;
import interfaces.IServerRequestListener;
import kryo.KryoServerController;
import kryo.KryoUtils;
import managers.LobbyManager;
import models.*;

import java.util.ArrayList;

/**
 * The core of the server
 * Handle packets from the KryoServerController and redirect it to the associated sub-controllers
 */
public class ServerController {

    private KryoServerController mKryoServerController;
    private LobbyManager mLobbyManager;
    private ArrayList<GameController> mGameControllers;

    private IGameRequestListener mGameRequestListener = new IGameRequestListener() {
        @Override
        public void onPlayerHandAttribution(Player player) {
            System.out.println("INFO: Hand attribution in room " + mLobbyManager.getRoomAccordingToPlayerId(player.mConnectionId).mName);
            System.out.println("- - - - - - - - - - - - - - -");
            sendRequestToPlayer(player, 107, player.mHand);
        }

        @Override
        public void onPlayerBidTurn(Player player, ArrayList<Contract> bidList) {
            System.out.println("INFO: new Bid turn in room " + mLobbyManager.getRoomAccordingToPlayerId(player.mConnectionId).mName);
            System.out.println("- - - - - - - - - - - - - - -");
            sendRequestToClients(mLobbyManager.getPlayersAccordingToRoomNameExceptCaller(player.mConnectionId, mLobbyManager.getRoomAccordingToPlayerId(player.mConnectionId).mName), 506, player.mName);
            sendRequestToPlayer(player, 108, bidList);
        }

        @Override
        public void onNewBidPhaseStarted(int connectionId) {
            System.out.println("INFO: Every players have checked in room " + mLobbyManager.getRoomAccordingToPlayerId(connectionId).mName);
            System.out.println("New bid phase. Redrawing hands ...");
            System.out.println("- - - - - - - - - - - - - - -");
            sendRequestToClients(mLobbyManager.getPlayersAccordingToRoomName(mLobbyManager.getRoomAccordingToPlayerId(connectionId).mName), 507, null);
            GameController gc = getGameControllerAccordingToPlayerID(connectionId);
            if (gc != null) {
                gc.start();
            }
        }

        @Override
        public void onBidPhaseEnd(Team attackerTeam, Team defendantTeam, Room room) {
            System.out.println("INFO: Ending bid phase in room " + room.mName);
            System.out.println("- - - - - - - - - - - - - - -");
            sendRequestToClients(mLobbyManager.getPlayersAccordingToRoomName(room.mName), 508, attackerTeam);
            for (Player player : attackerTeam.mPlayers) {
                sendRequestToPlayer(player, 111, null);
            }
            sendRequestToPlayer(defendantTeam.mPlayers.get(0), 112, null);
            sendRequestToPlayer(defendantTeam.mPlayers.get(1), 111, null);
        }

        @Override
        public void onPreGamePhase(Player player) {
            System.out.println("INFO: Starting game phase in room " + mLobbyManager.getRoomAccordingToPlayerId(player.mConnectionId).mName);
            System.out.println("- - - - - - - - - - - - - - -");
            sendRequestToPlayer(player, 109, player.mHand);
        }

        @Override
        public void onPlayerGameTurn(Player player, ArrayList<Card> playMat) {
            System.out.println("INFO: new game turn in room " + mLobbyManager.getRoomAccordingToPlayerId(player.mConnectionId).mName);
            System.out.println("- - - - - - - - - - - - - - -");
            sendRequestToClients(mLobbyManager.getPlayersAccordingToRoomNameExceptCaller(player.mConnectionId, mLobbyManager.getRoomAccordingToPlayerId(player.mConnectionId).mName), 509, player.mName);
            sendRequestToPlayer(player, 110, playMat);
        }

        @Override
        public void onEndGameSet(Room room) {
            System.out.println("INFO: End of game set in room " + room.mName);
            System.out.println("- - - - - - - - - - - - - - -");
            sendRequestToClients(mLobbyManager.getPlayersAccordingToRoomName(room.mName), 511, room.mTeams);
        }

        @Override
        public void onGameScoreDisplay(Room room) {
            System.out.println("INFO: End of game round in room " + room.mName + " displaying score");
            System.out.println("- - - - - - - - - - - - - - -");
            sendRequestToClients(mLobbyManager.getPlayersAccordingToRoomName(room.mName), 512, room.mTeams);
        }

        @Override
        public void onGameEnd(Team winnerTeam, Room room) {
            System.out.println("INFO: End of game in room " + room.mName + " displaying score and closing game");
            System.out.println("- - - - - - - - - - - - - - -");
            sendRequestToClients(mLobbyManager.getPlayersAccordingToRoomName(room.mName), 513, winnerTeam);
            room.mPartieStarted = false;
            for (GameController gc : mGameControllers) {
                if (gc.getRoom().mName.equals(room.mName)) {
                    gc.stop();
                    mGameControllers.remove(gc);
                    break;
                }
            }
        }
    };

    private IServerRequestListener mServerRequestListener = new IServerRequestListener() {
        @Override
        public void onClientConnexion(Connection c) {
            CoincheRequest coincheRequest = new CoincheRequest();
            coincheRequest.mRequestId = 100;
            sendRequest(c, 100, null);
        }

        @Override
        public void onClientDisconnection(Connection c) {
            System.out.println("INFO: Client disconnected");
            Room room = mLobbyManager.removePlayerFromRoomAccordingToPlayerID(c.getID());
            Player player = mLobbyManager.getPlayerAccordingToID(c.getID());
            if (room != null && player != null) {
                sendRequestToClients(mLobbyManager.getPlayersAccordingToRoomNameExceptCaller(c.getID(), room.mName), 501, player.mName);
                System.out.println("INFO: " + player.mName + " is leaving the room " + room.mName);
                mLobbyManager.getLobby().mPlayers.remove(player);

                if (mLobbyManager.isThisRoomIsInAGame(room)) {
                    System.out.println("INFO: The room " + room.mName + " was in a game. Closing this game and notifying clients...");
                    room.mPartieStarted = false;
                    for (GameController gc : mGameControllers) {
                        if (gc.getRoom().mName.equals(room.mName)) {
                            gc.stop();
                            sendRequestToClients(mLobbyManager.getPlayersAccordingToRoomName(room.mName), 604, null);
                            mGameControllers.remove(gc);
                            break;
                        }
                    }

                }
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket200Received(Connection c, CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof String) {
                String nick = (String) coincheRequest.mPacket;

                if (mLobbyManager.isPlayerNickIsTaken(nick)) {
                    sendRequest(c, 600, null);
                } else {
                    Player player = new Player(c.getID(), nick);
                    mLobbyManager.getLobby().mPlayers.add(player);
                    sendRequest(c, 101, mLobbyManager.getLobby());

                    System.out.println("INFO: New client connected. Name " + nick);
                    System.out.println("- - - - - - - - - - - - - - -");
                }
            }
        }

        @Override
        public void onPacket201Received(Connection c, CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof String) {
                String roomName = (String) coincheRequest.mPacket;

                if (mLobbyManager.isRoomNameIsTaken(roomName)) {
                    System.out.println("INFO: " + roomName + " cannot be created");
                    sendRequest(c, 601, null);
                } else {
                    Room room = new Room(roomName);
                    mLobbyManager.getLobby().mRooms.add(room);
                    sendRequest(c, 102, mLobbyManager.getLobby());
                    System.out.println("INFO: New room created");
                    System.out.println("Rooms available :");
                    for (Room r : mLobbyManager.getLobby().mRooms) {
                        System.out.println(r.mName);
                    }
                    System.out.println("- - - - - - - - - - - - - - -");
                }
            }
        }

        @Override
        public void onPacket202Received(Connection c, CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof String) {
                String roomName = (String) coincheRequest.mPacket;

                if (!mLobbyManager.isThisRoomAvailable(roomName)) {
                    System.out.println("INFO: " + roomName + " cannot be joined");
                    sendRequest(c, 602, roomName);
                } else {
                    Player player = mLobbyManager.getPlayerAccordingToID(c.getID());
                    mLobbyManager.addPlayerInRoom(roomName, mLobbyManager.getPlayerAccordingToID(player.mConnectionId));

                    sendRequestToClients(mLobbyManager.getPlayersAccordingToRoomNameExceptCaller(c.getID(), roomName), 500, player.mName);
                    sendRequest(c, 103, mLobbyManager.getRoom(roomName));

                    System.out.println("INFO: " + player.mName + " joined the " + roomName + " room");
                    System.out.println("- - - - - - - - - - - - - - -");
                }
            }
        }

        @Override
        public void onPacket203Received(Connection c) {
            Room room = mLobbyManager.removePlayerFromRoomAccordingToPlayerID(c.getID());
            Player player = mLobbyManager.getPlayerAccordingToID(c.getID());
            if (room != null && player != null) {
                sendRequestToClients(mLobbyManager.getPlayersAccordingToRoomNameExceptCaller(c.getID(), room.mName), 501, player.mName);
                sendRequest(c, 104, room.mName);

                System.out.println("INFO: Player " + player.mName + " is leaving " + room.mName);
                System.out.println("Players remaining in " + room.mName);
                for (Team team : room.mTeams) {
                    System.out.println("Team " + team.mId + ":");
                    for (Player p : team.mPlayers) {
                        System.out.println(p.mName);
                    }
                }
                System.out.println("- - - - - - - - - - - - - - -");
            }
        }

        @Override
        public void onPacket204Received(Connection c) {
            sendRequest(c, 105, mLobbyManager.getLobby());
        }

        @Override
        public void onPacket205Received(Connection c, CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof String) {
                String roomName = (String) coincheRequest.mPacket;
                if (!mLobbyManager.isThisRoomAvailable(roomName)) {
                    sendRequest(c, 603, roomName);
                } else {
                    Room room = mLobbyManager.getRoom(roomName);
                    sendRequest(c, 106, room);
                }
            }
        }

        @Override
        public void onPacket206Received(Connection c, CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof String) {
                String nickName = (String) coincheRequest.mPacket;
                Room curPlayerRoom = mLobbyManager.getRoomAccordingToPlayerId(c.getID());
                if (curPlayerRoom != null) {
                    mLobbyManager.setPlayerReadyToPlay(c.getID(), curPlayerRoom);
                    sendRequestToClients(mLobbyManager.getPlayersAccordingToRoomNameExceptCaller(c.getID(), curPlayerRoom.mName), 502, nickName);
                    System.out.println("INFO: Player " + nickName + " is ready to play");
                    if (mLobbyManager.isRoomIsReady(curPlayerRoom)) {
                        System.out.println("INFO: All players in the room are ready. Starting the game...");
                        sendRequestToClients(mLobbyManager.getPlayersAccordingToRoomName(curPlayerRoom.mName), 504, null);

                        curPlayerRoom.mPartieStarted = true;
                        GameController gameController = new GameController(mGameRequestListener, curPlayerRoom);
                        gameController.start();
                        mGameControllers.add(gameController);
                    } else {
                        System.out.println("INFO: All players in the room are not ready yet.");
                        sendRequestToClients(mLobbyManager.getPlayersAccordingToRoomName(curPlayerRoom.mName), 503, mLobbyManager.getPlayersNbrReadyInRoom(curPlayerRoom));
                    }
                }
            }
        }

        @Override
        public void onPacket207Received(Connection c, CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof Contract) {
                Contract contract = (Contract) coincheRequest.mPacket;
                Player player = mLobbyManager.getPlayerAccordingToID(c.getID());
                contract.mOwnerName = player.mName;
                GameController gc = getGameControllerAccordingToPlayerID(c.getID());
                if (gc != null) {
                    System.out.println("INFO: Player " + player.mName + " is placing a bid in room " + mLobbyManager.getRoomAccordingToPlayerId(player.mConnectionId).mName);
                    sendRequestToClients(mLobbyManager.getPlayersAccordingToRoomNameExceptCaller(c.getID(), mLobbyManager.getRoomAccordingToPlayerId(c.getID()).mName), 505, contract);
                    gc.placeTeamBid(player, contract);
                }
            }
        }

        @Override
        public void onPacket208Received(Connection c) {
            GameController gc = getGameControllerAccordingToPlayerID(c.getID());
            if (gc != null) {
                System.out.println("INFO: New player ready for the game phase in room " + mLobbyManager.getRoomAccordingToPlayerId(c.getID()).mName);
                gc.addClientReadyForGamePhase();
            }
        }

        @Override
        public void onPacket209Received(Connection c, CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof Card) {
                Card card = (Card) coincheRequest.mPacket;
                Player player = mLobbyManager.getPlayerAccordingToID(c.getID());
                card.mOwnerName = player.mName;
                GameController gc = getGameControllerAccordingToPlayerID(c.getID());
                if (gc != null) {
                    System.out.println("INFO: Player " + player.mName + " is placing a card in room " + mLobbyManager.getRoomAccordingToPlayerId(player.mConnectionId).mName);
                    sendRequestToClients(mLobbyManager.getPlayersAccordingToRoomNameExceptCaller(c.getID(), mLobbyManager.getRoomAccordingToPlayerId(c.getID()).mName), 510, card);
                    gc.playCard(card, player);
                }
            }
        }

        @Override
        public void onPacket210Received(Connection c, CoincheRequest coincheRequest) {
            if (coincheRequest.mPacket instanceof Boolean) {
                Boolean isCoinched = (Boolean) coincheRequest.mPacket;
                Player player = mLobbyManager.getPlayerAccordingToID(c.getID());
                GameController gc = getGameControllerAccordingToPlayerID(c.getID());
                if (isCoinched && gc != null) {
                    System.out.println("INFO: Player " + player.mName + " is coinching the contract in room " + mLobbyManager.getRoomAccordingToPlayerId(player.mConnectionId).mName);
                    gc.coincheCurContract();
                }
                sendRequestToClients(mLobbyManager.getPlayersAccordingToRoomName(mLobbyManager.getRoomAccordingToPlayerId(c.getID()).mName), 514, isCoinched);
            }
        }
    };

    /**
     * Initialize the ServerController by creating KryoServerController and the server's lobby
     */
    public ServerController() {
        mKryoServerController = new KryoServerController(this);
        KryoUtils.registerUtilClasses(mKryoServerController.getKryo());
        mLobbyManager = new LobbyManager();
        mGameControllers = new ArrayList<>();
    }

    /**
     * Start the ServerController by starting the connection on $(SERVER_IP):0xC3f4
     * Init the packet listener of the KryoServerController
     */
    public void start() {
        mKryoServerController.getServer().start();
        try {
            mKryoServerController.getServer().bind(0xC4f3);
        } catch (Exception e) {
            System.out.println("Error : " + e);
            System.exit(84);
        }
        mKryoServerController.initPacketListener();
    }

    /**
     * Stop the ServerController by removing the packet listener of the KryoServerController and stop the connection
     */
    public void stop() {
        mKryoServerController.removePacketListener();
        mKryoServerController.getServer().stop();
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
        mKryoServerController.sendRequest(connection, coincheRequest);
    }

    /**
     * Send a packet to all of the client in the list 'players' with the specified request code and content
     *
     * @param players
     * @param requestCode
     * @param packet
     */
    public void sendRequestToClients(ArrayList<Player> players, int requestCode, Object packet) {
        CoincheRequest coincheRequest = new CoincheRequest();
        coincheRequest.mRequestId = requestCode;
        coincheRequest.mPacket = packet;
        for (Connection connection : mKryoServerController.getConnections()) {
            for (Player player : players) {
                if (player.mConnectionId == connection.getID()) {
                    mKryoServerController.sendRequest(connection, coincheRequest);
                }
            }
        }
    }

    /**
     * Send a packet to the specified player with the specified request code and content
     *
     * @param player
     * @param requestCode
     * @param packet
     */
    public void sendRequestToPlayer(Player player, int requestCode, Object packet) {
        CoincheRequest coincheRequest = new CoincheRequest();
        coincheRequest.mRequestId = requestCode;
        coincheRequest.mPacket = packet;
        for (Connection connection : mKryoServerController.getConnections()) {
            if (player.mConnectionId == connection.getID()) {
                mKryoServerController.sendRequest(connection, coincheRequest);
            }
        }
    }

    private GameController getGameControllerAccordingToPlayerID(int id) {
        for (GameController gc : mGameControllers) {
            Player player = mLobbyManager.getPlayerAccordingToID(id);
            if (mLobbyManager.isThisPlayerIsInThisRoom(player, gc.getRoom())) {
                return gc;
            }
        }
        return null;
    }

    public IServerRequestListener getServerRequestListener() {
        return mServerRequestListener;
    }
}
