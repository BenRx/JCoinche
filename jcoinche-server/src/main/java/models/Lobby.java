package models;

import java.util.ArrayList;

public class Lobby {
    public ArrayList<Room> mRooms;
    public ArrayList<Player> mPlayers;

    public Lobby() {
        mRooms = new ArrayList<>();
        mPlayers = new ArrayList<>();
    }

}
