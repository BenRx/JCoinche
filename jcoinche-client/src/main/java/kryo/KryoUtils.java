package kryo;

import com.esotericsoftware.kryo.Kryo;
import models.*;

import java.util.ArrayList;

public class KryoUtils {
    /**
     * Allow the kryo registering of every classes serializable of the server
     * @param kryo
     */
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
