package managers;

import models.Player;
import models.Room;
import models.Team;

import java.util.ArrayList;

/**
 * Manage lobby's rooms
 */
public class RoomsManager {

    private ArrayList<Room> mRooms;

    public RoomsManager(ArrayList<Room> rooms) {
        mRooms = rooms;
    }


    /**
     * Add the specified player in the room with the specified roomName
     * @param roomName
     * @param player
     */
    public void addPlayerInRoom(String roomName, Player player) {
        Room room = getRoom(roomName);
        if (room != null && player != null) {
            addPlayerInTeam(room, player);
        }
    }

    /**
     * Remove the player with the specified id from his actual room
     * @param id
     * @return
     */
    public Room removePlayerFromRoomAccordingToPlayerID(int id) {
        for (Room room : mRooms) {
            for (Team team : room.mTeams) {
                for (Player player : team.mPlayers) {
                    if (player.mConnectionId == id) {
                        team.mPlayers.remove(player);
                        return room;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Return true if all of the players in the room are ready to play
     * @param room
     * @return
     */
    public boolean isRoomIsReady(Room room) {
        int playersReadyCount = 0;
        for (Team team : room.mTeams) {
            for (Player player : team.mPlayers) {
                if (player.mIsReadyToPlay) {
                    playersReadyCount++;
                } else {
                    return false;
                }
            }
        }
        return playersReadyCount == 4;
    }

    /**
     * Return true if the specified room name is already taken
     * @param roomName
     * @return
     */
    public boolean isRoomNameIsTaken(String roomName) {
        return getRoom(roomName) != null;
    }

    /**
     * Return true if the room with the specified room name exist and is available
     * @param roomName
     * @return
     */
    public boolean isThisRoomAvailable(String roomName) {
        for (Room room : mRooms) {
            if (room.mName.equals(roomName)) {
                if (room.mTeams.get(0).mPlayers.size() < 2 || room.mTeams.get(1).mPlayers.size() < 2) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return true if the specified player is in the specified room
     * @param player
     * @param room
     * @return
     */
    public boolean isThisPlayerIsInThisRoom(Player player, Room room) {
        if (isRoomNameIsTaken(room.mName)) {
            for (Team team : room.mTeams) {
                for (Player pl : team.mPlayers) {
                    if (pl.mConnectionId == player.mConnectionId) {
                        return true;
                    }
                }
            }
        } else {
            return false;
        }
        return false;
    }

    /**
     * Return true if the specified room is actually in a game phase
     * @param room
     * @return
     */
    public boolean isThisRoomIsInAGame(Room room) {
        return room.mPartieStarted;
    }

    /**
     * Return the number of player ready in the specified room
     * @param room
     * @return
     */
    public Integer getPlayersNbrReadyInRoom(Room room) {
        Integer playersReadyCount = 0;
        for (Team team : room.mTeams) {
            for (Player player : team.mPlayers) {
                if (player.mIsReadyToPlay) {
                    playersReadyCount++;
                }
            }
        }
        return playersReadyCount;
    }

    /**
     * Return the room witch contain the player with the specified player id or null if the specified player does not exist
     * @param playerId
     * @return
     */
    public Room getRoomAccordingToPlayerId(int playerId) {
        for (Room room : mRooms) {
            for (Team team : room.mTeams) {
                for (Player player : team.mPlayers) {
                    if (player.mConnectionId == playerId) {
                        return room;
                    }
                }
            }
        }
        return null;
    }

    public Room getRoom(String roomName) {
        for (Room room : mRooms) {
            if (room.mName.equals(roomName)) {
                return room;
            }
        }
        return null;
    }

    private void addPlayerInTeam(Room room, Player player) {
        if (room.mTeams.get(0).mPlayers.size() < 2) {
            room.mTeams.get(0).mPlayers.add(player);
        } else {
            room.mTeams.get(1).mPlayers.add(player);
        }
    }
}
