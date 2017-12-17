package models;

import java.util.ArrayList;

public class Team {
    public int mId;
    public int mScore;
    public ArrayList<Player> mPlayers;
    public Contract mContract;
    public boolean isAttackerTeam;
    public boolean isWonTheSet;

    public Team() {
        mPlayers = new ArrayList<>();
        mScore = 0;
        isAttackerTeam = false;
        isWonTheSet = false;
    }

    public Team(int id) {
        mPlayers = new ArrayList<>();
        mId = id;
        mScore = 0;
        isAttackerTeam = false;
        isWonTheSet = false;
    }
}
