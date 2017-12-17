package com.rx.ben.jcoincheapp.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.rx.ben.jcoincheapp.models.Card;
import com.rx.ben.jcoincheapp.models.CoincheRequest;
import com.rx.ben.jcoincheapp.models.Contract;
import com.rx.ben.jcoincheapp.models.Lobby;
import com.rx.ben.jcoincheapp.models.Player;
import com.rx.ben.jcoincheapp.models.Room;
import com.rx.ben.jcoincheapp.models.Team;

import java.util.ArrayList;

public class KryoUtils {
    static public void registerUtilClasses(Kryo kryo) {
        kryo.register(CoincheRequest.class);
        kryo.register(Player.class);
        kryo.register(Contract.class);
        kryo.register(Contract.AssetType.class);
        kryo.register(Room.class);
        kryo.register(Lobby.class);
        kryo.register(ArrayList.class);
        kryo.register(Card.class);
        kryo.register(Card.ColorType.class);
        kryo.register(Card.ValueType.class);
        kryo.register(Team.class);
    }
}
