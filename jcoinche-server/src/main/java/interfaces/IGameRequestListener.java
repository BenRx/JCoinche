package interfaces;

import models.*;

import java.util.ArrayList;

/**
 * Notify the ServerController that there's new game action to treat
 */
public interface IGameRequestListener {
    void onPlayerHandAttribution(Player player);
    void onPlayerBidTurn(Player player, ArrayList<Contract> bidList);
    void onNewBidPhaseStarted(int connectionId);
    void onBidPhaseEnd(Team attackerTeam, Team defendantTeam, Room room);
    void onPreGamePhase(Player player);
    void onPlayerGameTurn(Player player, ArrayList<Card> playMat);
    void onEndGameSet(Room room);
    void onGameScoreDisplay(Room room);
    void onGameEnd(Team team, Room room);
}
