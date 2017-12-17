import controllers.ClientController;
import managers.LobbyManager;
import models.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class ClientTest {
    @Test
    public void testClientLogging() {
        LobbyManager lobbyManager = new LobbyManager();
        CoincheRequest cr = new CoincheRequest();
        cr.mRequestId = 101;
        cr.mPacket = lobbyManager.getLobby();

        ClientController clientController = new ClientController();
        clientController.setNickName("Toto");
        clientController.getClientRequestListener().onPacket101Received(cr);

        Assert.assertEquals(true, clientController.isLogged());
    }

    @Test
    public void testClientLoggingRoom() {
        Room room = new Room("AmazingRoom");
        CoincheRequest cr = new CoincheRequest();
        cr.mRequestId = 103;
        cr.mPacket = room;

        ClientController clientController = new ClientController();
        clientController.setNickName("Ben");
        clientController.getClientRequestListener().onPacket103Received(cr);

        Assert.assertEquals(true, clientController.isInARoom());
    }

    @Test
    public void testClientIsInAGame() {
        Team team = new Team(42);
        CoincheRequest cr = new CoincheRequest();
        cr.mRequestId = 513;
        cr.mPacket = team;

        ClientController clientController = new ClientController();
        clientController.setNickName("Ben");
        clientController.getClientRequestListener().onPacket504Received();

        Assert.assertEquals(true, clientController.isInAGame());

        clientController.getClientRequestListener().onPacket513Received(cr);

        Assert.assertEquals(false, clientController.isInAGame());
    }

    @Test
    public void testClientBidPhase() {
        ArrayList<Contract> bidList = new ArrayList<>();
        CoincheRequest cr = new CoincheRequest();
        cr.mRequestId = 108;
        cr.mPacket = bidList;

        ClientController clientController = new ClientController();
        clientController.setNickName("Ben");
        clientController.getClientRequestListener().onPacker108Received(cr);

        Assert.assertEquals(true, clientController.isInABidPhase());

        clientController.getClientCommandListener().onBidCommand(Contract.AssetType.TEST, 0);

        Assert.assertEquals(false, clientController.isInABidPhase());

    }

    @Test
    public void testClientWrongValueBidPhase() {
        ArrayList<Contract> bidList = new ArrayList<>();
        CoincheRequest cr = new CoincheRequest();
        cr.mRequestId = 108;
        cr.mPacket = bidList;

        ClientController clientController = new ClientController();
        clientController.setNickName("John");
        clientController.getClientRequestListener().onPacker108Received(cr);

        Assert.assertEquals(true, clientController.isInABidPhase());

        clientController.getClientCommandListener().onBidCommand(Contract.AssetType.SPADE, 30);

        Assert.assertEquals(true, clientController.isInABidPhase());
    }

    @Test
    public void testClientGamePhase() {
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(Card.ColorType.SPADE, Card.ValueType.ACE));
        hand.add(new Card(Card.ColorType.HEART, Card.ValueType.ACE));
        hand.add(new Card(Card.ColorType.DIAMOND, Card.ValueType.ACE));
        hand.add(new Card(Card.ColorType.CLUB, Card.ValueType.ACE));
        CoincheRequest cr = new CoincheRequest();
        cr.mRequestId = 110;
        cr.mPacket = hand;

        ClientController clientController = new ClientController();
        clientController.setNickName("John");
        clientController.getClientRequestListener().onPacker110Received(cr);

        Assert.assertEquals(true, clientController.isInAGamePhase());
    }

    @Test
    public void testClientWrongCardGamePhase() {
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(Card.ColorType.SPADE, Card.ValueType.ACE));
        hand.add(new Card(Card.ColorType.HEART, Card.ValueType.ACE));
        hand.add(new Card(Card.ColorType.DIAMOND, Card.ValueType.ACE));
        hand.add(new Card(Card.ColorType.CLUB, Card.ValueType.ACE));
        CoincheRequest cr = new CoincheRequest();
        cr.mRequestId = 110;
        cr.mPacket = hand;

        ClientController clientController = new ClientController();
        clientController.setNickName("John");
        clientController.getClientRequestListener().onPacker110Received(cr);

        clientController.getClientCommandListener().onPlayCommand(Card.ColorType.SPADE, Card.ValueType.EIGHT);

        Assert.assertEquals(true, clientController.isInAGamePhase());
    }
}