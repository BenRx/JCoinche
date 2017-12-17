package com.rx.ben.jcoincheapp.models;

import java.util.ArrayList;

public class Player {
    public int mConnectionId;
    public String mName;
    public ArrayList<Card> mHand;
    public boolean mIsReadyToPlay;

    // Empty constructor for serialization
    public Player() {
        mHand = new ArrayList<>();
    }

    public Player(int coId, String id) {
        mConnectionId = coId;
        mName = id;
        mHand = new ArrayList<>();
    }
}