package managers;

import models.Lobby;
import models.Player;
import models.Room;
import models.Team;

import java.util.ArrayList;

/**
 * Manage the lobby of the server
 */
public class LobbyManager {
    private Lobby mLobby;
    private RoomsManager mRoomsManager;

    /**
     * Init the server's lobby by creating the roomsManager of this lobby and by creating 3 rooms by default : A, B and C
     */
    public LobbyManager() {
        mLobby = new Lobby();

        mRoomsManager = new RoomsManager(mLobby.mRooms);
        Room room = new Room("A");
        mLobby.mRooms.add(room);
        Room room2 = new Room("B");
        mLobby.mRooms.add(room2);
        Room room3 = new Room("C");
        mLobby.mRooms.add(room3);
    }

    /**
     * Add the specified player into the room with the specified room name
     * @param roomName
     * @param player
     */
    public void addPlayerInRoom(String roomName, Player player) {
        mRoomsManager.addPlayerInRoom(roomName, player);
    }

    /**
     * Remove the player with the specified id from his actual room
     * @param id
     * @return
     */
    public Room removePlayerFromRoomAccordingToPlayerID(int id) {
        return mRoomsManager.removePlayerFromRoomAccordingToPlayerID(id);
    }

    /**
     * Return true if all of the players in the room are ready to play
     * @param room
     * @return
     */
    public boolean isRoomIsReady(Room room) {
        return mRoomsManager.isRoomIsReady(room);
    }

    /**
     * Return true if the specified room name is already taken
     * @param roomName
     * @return
     */
    public boolean isRoomNameIsTaken(String roomName) {
        return mRoomsManager.isRoomNameIsTaken(roomName);
    }

    /**
     * Return true if the room with the specified room name exist and is available
     * @param roomName
     * @return
     */
    public boolean isThisRoomAvailable(String roomName) {
       return mRoomsManager.isThisRoomAvailable(roomName);
    }

    /**
     * Return true if the specified player is in the specified room
     * @param player
     * @param room
     * @return
     */
    public boolean isThisPlayerIsInThisRoom(Player player, Room room) {
        return mRoomsManager.isThisPlayerIsInThisRoom(player, room);
    }

    /**
     * Return true if the specified room is actually in a game phase
     * @param room
     * @return
     */
    public boolean isThisRoomIsInAGame(Room room) {
        return mRoomsManager.isThisRoomIsInAGame(room);
    }

    /**
     * Return the number of player ready in the specified room
     * @param room
     * @return
     */
    public Integer getPlayersNbrReadyInRoom(Room room) {
        return mRoomsManager.getPlayersNbrReadyInRoom(room);
    }

    /**
     * Return the room witch contain the player with the specified player id or null if the specified player does not exist
     * @param playerId
     * @return
     */
    public Room getRoomAccordingToPlayerId(int playerId) {
        return mRoomsManager.getRoomAccordingToPlayerId(playerId);
    }

    public Room getRoom(String roomName) {
        return mRoomsManager.getRoom(roomName);
    }

    public RoomsManager getRoomsManager() {
        return mRoomsManager;
    }

    /**
     * Set the player with the specified player id ready in the specified room
     * @param playerId
     * @param room
     */
    public void setPlayerReadyToPlay(int playerId, Room room) {
        for (Team team : room.mTeams) {
            for (Player player : team.mPlayers) {
                if (player.mConnectionId == playerId) {
                    player.mIsReadyToPlay = true;
                }
            }
        }
    }

    /**
     * Return true if the specified nick name is already taken
     * @param nick
     * @return
     */
    public boolean isPlayerNickIsTaken(String nick) {
        for (Player player : mLobby.mPlayers) {
            if (player.mName.equals(nick)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return all of the players present in the room with the specified room name except the player with the specified id
     * @param callerId
     * @param roomName
     * @return
     */
    public ArrayList<Player> getPlayersAccordingToRoomNameExceptCaller(int callerId, String roomName) {
        Room room = getRoom(roomName);
        ArrayList<Player> players = new ArrayList<>();
        if (room != null) {
            for (Team team : room.mTeams) {
                for (Player player : team.mPlayers) {
                    if (player.mConnectionId != callerId) {
                        players.add(player);
                    }
                }
            }
        }
        return players;
    }

    /**
     * Return all of the players present in the room with the specified room name
     * @param roomName
     * @return
     */
    public ArrayList<Player> getPlayersAccordingToRoomName(String roomName) {
        Room room = getRoom(roomName);
        ArrayList<Player> players = new ArrayList<>();
        if (room != null) {
            for (Team team : room.mTeams) {
                players.addAll(team.mPlayers);
            }
        }
        return players;
    }

    /**
     * Return the player with the specified id or null if it does not exist
     * @param id
     * @return
     */
    public Player getPlayerAccordingToID(int id) {
        for (Player player : mLobby.mPlayers) {
            if (player.mConnectionId == id) {
                return player;
            }
        }
        return null;
    }

    public Lobby getLobby() {
        return mLobby;
    }
}
