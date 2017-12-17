import managers.DeckManager;
import managers.LobbyManager;
import models.Player;
import models.Room;
import models.Team;
import org.junit.Assert;
import org.junit.Test;

public class ServerTest {
    @Test
    public void testServerAddPlayerInRoomTest() {
        Player player0 = new Player(0, "Fifi");
        Player player1 = new Player(1, "Ben");
        Player player2 = new Player(2, "Lily");
        Player player3 = new Player(3, "Mimi");

        LobbyManager lobbyManager = new LobbyManager();

        lobbyManager.getLobby().mPlayers.add(player0);
        lobbyManager.getLobby().mPlayers.add(player1);
        lobbyManager.getLobby().mPlayers.add(player2);
        lobbyManager.getLobby().mPlayers.add(player3);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player0);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player1);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player2);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player3);

        int playerNbr = 0;
        for (Team team : lobbyManager.getRoom("A").mTeams) {
            for (Player player : team.mPlayers) {
                playerNbr++;
            }
        }
        Assert.assertEquals(4, playerNbr);

        lobbyManager.getRoomsManager().removePlayerFromRoomAccordingToPlayerID(2);

        playerNbr = 0;
        for (Team team : lobbyManager.getRoom("A").mTeams) {
            for (Player player : team.mPlayers) {
                playerNbr++;
            }
        }
        Assert.assertEquals(3, playerNbr);
    }

    @Test
    public void testServerRoomAvailableTest() {
        LobbyManager lobbyManager = new LobbyManager();
        Assert.assertEquals(true, lobbyManager.isThisRoomAvailable("A"));
        Assert.assertEquals(false, lobbyManager.isThisRoomAvailable("Z"));

        Room room = new Room("Z");

        lobbyManager.getLobby().mRooms.add(room);
        Assert.assertEquals(true, lobbyManager.isThisRoomAvailable("Z"));
    }

    @Test
    public void testServerIsPlayerIsInRoom() {
        Player player0 = new Player(0, "Fifi");
        Player player1 = new Player(1, "Ben");
        Player player2 = new Player(2, "Lily");
        Player player3 = new Player(3, "Mimi");

        LobbyManager lobbyManager = new LobbyManager();

        lobbyManager.getLobby().mPlayers.add(player0);
        lobbyManager.getLobby().mPlayers.add(player1);
        lobbyManager.getLobby().mPlayers.add(player2);
        lobbyManager.getLobby().mPlayers.add(player3);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player0);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player1);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player2);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player3);

        Assert.assertEquals(true, lobbyManager.isThisPlayerIsInThisRoom(player0, lobbyManager.getRoom("A")));
        Assert.assertEquals(false, lobbyManager.isThisPlayerIsInThisRoom(player0, lobbyManager.getRoom("B")));
    }

    @Test
    public void testServerGetPlayersReadyInRoom() {
        Player player0 = new Player(0, "Fifi");
        Player player1 = new Player(1, "Ben");
        Player player2 = new Player(2, "Lily");
        Player player3 = new Player(3, "Mimi");

        player0.mIsReadyToPlay = true;
        player1.mIsReadyToPlay = true;

        LobbyManager lobbyManager = new LobbyManager();

        lobbyManager.getLobby().mPlayers.add(player0);
        lobbyManager.getLobby().mPlayers.add(player1);
        lobbyManager.getLobby().mPlayers.add(player2);
        lobbyManager.getLobby().mPlayers.add(player3);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player0);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player1);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player2);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player3);

        Assert.assertEquals(new Integer(2), lobbyManager.getPlayersNbrReadyInRoom(lobbyManager.getRoom("A")));
    }

    @Test
    public void testServerGetRoomAccordingToPlayerId() {
        Player player0 = new Player(0, "Fifi");
        Player player1 = new Player(1, "Ben");
        Player player2 = new Player(2, "Lily");
        Player player3 = new Player(3, "Mimi");

        LobbyManager lobbyManager = new LobbyManager();

        lobbyManager.getLobby().mPlayers.add(player0);
        lobbyManager.getLobby().mPlayers.add(player1);
        lobbyManager.getLobby().mPlayers.add(player2);
        lobbyManager.getLobby().mPlayers.add(player3);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player0);
        lobbyManager.getRoomsManager().addPlayerInRoom("B", player1);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player2);
        lobbyManager.getRoomsManager().addPlayerInRoom("B", player3);

        Room room1 = lobbyManager.getRoomAccordingToPlayerId(0);
        Room room2 = lobbyManager.getRoomAccordingToPlayerId(42);
        Room room3 = lobbyManager.getRoomAccordingToPlayerId(1);

        Assert.assertEquals("A", room1.mName);
        Assert.assertEquals(null, room2);
        Assert.assertEquals("B", room3.mName);
    }

    @Test
    public void testServerPlayerNameTaken() {
        Player player0 = new Player(0, "Fifi");
        Player player1 = new Player(1, "Ben");
        Player player2 = new Player(2, "Lily");
        Player player3 = new Player(3, "Mimi");

        LobbyManager lobbyManager = new LobbyManager();

        lobbyManager.getLobby().mPlayers.add(player0);
        lobbyManager.getLobby().mPlayers.add(player1);
        lobbyManager.getLobby().mPlayers.add(player2);
        lobbyManager.getLobby().mPlayers.add(player3);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player0);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player1);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player2);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player3);

        Assert.assertEquals(true, lobbyManager.isPlayerNickIsTaken("Ben"));
        Assert.assertEquals(false, lobbyManager.isPlayerNickIsTaken("Zed"));
    }

    @Test
    public void testServerGetPlayerAccordingToID() {
        Player player0 = new Player(0, "Fifi");
        Player player1 = new Player(1, "Ben");
        Player player2 = new Player(2, "Lily");
        Player player3 = new Player(3, "Mimi");

        LobbyManager lobbyManager = new LobbyManager();

        lobbyManager.getLobby().mPlayers.add(player0);
        lobbyManager.getLobby().mPlayers.add(player1);
        lobbyManager.getLobby().mPlayers.add(player2);
        lobbyManager.getLobby().mPlayers.add(player3);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player0);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player1);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player2);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player3);

        Player pl1 = lobbyManager.getPlayerAccordingToID(2);
        Player pl2= lobbyManager.getPlayerAccordingToID(42);

        Assert.assertEquals("Lily", pl1.mName);
        Assert.assertEquals(null, pl2);
    }

    @Test
    public void testServerDeckManager() {
        Player player0 = new Player(0, "Fifi");
        Player player1 = new Player(1, "Ben");
        Player player2 = new Player(2, "Lily");
        Player player3 = new Player(3, "Mimi");

        LobbyManager lobbyManager = new LobbyManager();

        lobbyManager.getLobby().mPlayers.add(player0);
        lobbyManager.getLobby().mPlayers.add(player1);
        lobbyManager.getLobby().mPlayers.add(player2);
        lobbyManager.getLobby().mPlayers.add(player3);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player0);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player1);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player2);
        lobbyManager.getRoomsManager().addPlayerInRoom("A", player3);

        DeckManager dm = new DeckManager();
        Assert.assertEquals(32, dm.mDeck.size());

        for (Team tm : lobbyManager.getRoom("A").mTeams) {
            for (Player pl : tm.mPlayers) {
                pl.mHand = dm.createHand();
                Assert.assertEquals(8, pl.mHand.size());
            }
        }
    }
}
