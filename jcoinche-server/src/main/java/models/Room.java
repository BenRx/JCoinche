package models;

import java.util.ArrayList;

public class Room {
    public String mName;
    public ArrayList<Team> mTeams;
    public boolean mPartieStarted;

    public Room() {
        mTeams = new ArrayList<>();
        mTeams.add(new Team(0));
        mTeams.add(new Team(1));
    }

    public Room(String name) {
        mName = name;
        mTeams = new ArrayList<>();
        mTeams.add(new Team(0));
        mTeams.add(new Team(1));
    }
}
