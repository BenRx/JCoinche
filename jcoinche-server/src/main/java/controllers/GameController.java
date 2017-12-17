package controllers;

import interfaces.IGameRequestListener;
import managers.DeckManager;
import models.*;

import java.util.ArrayList;

/**
 * Control the bid and the game phase of the program
 */
public class GameController {

    private final static int MIN_END_GAME_POINTS = 701;

    private Room mRoom;
    private DeckManager mDeckManager;
    private IGameRequestListener mGameRequestListener;

    private ArrayList<Contract> mBidList;
    private ArrayList<Card> mPlayMat;

    private Contract mCurContract;

    private int mClientsOnGamePhase;


    /**
     * Allow the ClientController to listen on game events and init its properties
     *
     * @param gameRequestListener
     * @param room
     */
    public GameController(IGameRequestListener gameRequestListener, Room room) {
        mGameRequestListener = gameRequestListener;
        mRoom = room;
        mBidList = new ArrayList<>();
        mPlayMat = new ArrayList<>();
    }

    /**
     * Start the GameController by generating player's hands.
     * Notify the first player that it's his turn to play
     */
    public void start() {
        mClientsOnGamePhase = 0;
        mDeckManager = new DeckManager();
        for (Team team : mRoom.mTeams) {
            for (Player player : team.mPlayers) {
                player.mHand = mDeckManager.createHand();
                mGameRequestListener.onPlayerHandAttribution(player);
            }
        }
        mGameRequestListener.onPlayerBidTurn(getPlayerAccordingToTurn(mBidList.size()), mBidList);
    }

    /**
     * Stop the GameController and reset its properties
     */
    public void stop() {
        resetRound();
    }

    /**
     * Add a player ready for the game phase in the GameController's room
     */
    public void addClientReadyForGamePhase() {
        if (mClientsOnGamePhase == 3) {
            for (Team team : mRoom.mTeams) {
                for (Player player : team.mPlayers) {
                    mGameRequestListener.onPreGamePhase(player);
                }
            }
            mClientsOnGamePhase = 0;
            Player pl = getPlayerAccordingToTurn(mPlayMat.size());
            if (pl != null) {
                mGameRequestListener.onPlayerGameTurn(pl, pl.mHand);
            }
        } else {
            mClientsOnGamePhase++;
        }
    }

    /**
     * Allow a player to play a card. This function will call the next turn or the end of the game phase by calculating scores
     *
     * @param cardPlayed
     * @param player
     */
    public void playCard(Card cardPlayed, Player player) {
        mPlayMat.add(cardPlayed);
        if (mPlayMat.size() == 4) {
            mDeckManager.removeCardFromPlayerHand(player, cardPlayed);
            processWinnerTeamScore(getSetWinnerTeamId());
            mPlayMat.clear();
            mGameRequestListener.onEndGameSet(mRoom);
            if (getSetTurn() != 8) {
                Player pl = getPlayerAccordingToTurn(mPlayMat.size());
                if (pl != null) {
                    mGameRequestListener.onPlayerGameTurn(pl, pl.mHand);
                }
            } else {
                processGameRoundPoints();
                mGameRequestListener.onGameScoreDisplay(mRoom);
                if (isATeamFinishTheGame()) {
                    mGameRequestListener.onGameEnd(getTheWinnerTeam(), mRoom);
                } else {
                    mGameRequestListener.onPlayerBidTurn(getPlayerAccordingToTurn(mBidList.size()), mBidList);
                }
                resetRound();
            }
        } else {
            mDeckManager.removeCardFromPlayerHand(player, cardPlayed);
            Player pl = getPlayerAccordingToTurn(mPlayMat.size());
            if (pl != null) {
                mGameRequestListener.onPlayerGameTurn(pl, pl.mHand);
            }
        }
    }

    /**
     * Set the game bid and the attacker / defendant team
     *
     * @param player
     * @param contract
     */
    public void placeTeamBid(Player player, Contract contract) {
        for (Team team : mRoom.mTeams) {
            for (Player pl : team.mPlayers) {
                if (player.mConnectionId == pl.mConnectionId) {
                    team.mContract = contract;
                    mBidList.add(contract);
                    if (mBidList.size() == 4) {
                        if (isBidIsChecked()) {
                            mBidList.clear();
                            mGameRequestListener.onNewBidPhaseStarted(player.mConnectionId);
                        } else {
                            mCurContract = getBiggestContract();
                            Team attacker = getAttackerTeam(mCurContract);
                            if (attacker != null) {
                                Team defendant = getDefendantTeam(attacker.mId);
                                attacker.mContract = mCurContract;
                                mGameRequestListener.onBidPhaseEnd(attacker, defendant, mRoom);
                                mBidList.clear();
                            }
                        }
                    } else {
                        mGameRequestListener.onPlayerBidTurn(getPlayerAccordingToTurn(mBidList.size()), mBidList);
                    }
                }
            }
        }
    }

    /**
     * Allow the defendant team to coinche the current contract
     */
    public void coincheCurContract() {
        mCurContract.mIsCoinched = true;
        Team attacker = getAttackerTeam(mCurContract);
        if (attacker != null) {
            attacker.mContract = mCurContract;
        }
    }

    /**
     * Return the current GameController's room
     *
     * @return
     */
    public Room getRoom() {
        return mRoom;
    }

    private void resetRound() {
        for (Team team : mRoom.mTeams) {
            team.isWonTheSet = false;
            team.isAttackerTeam = false;
            team.mContract = new Contract();
        }
        mBidList.clear();
        mPlayMat.clear();
        mDeckManager = new DeckManager();
        for (Team team : mRoom.mTeams) {
            for (Player player : team.mPlayers) {
                player.mHand = mDeckManager.createHand();
                mGameRequestListener.onPlayerHandAttribution(player);
            }
        }
    }

    private Team getTheWinnerTeam() {
        for (Team team : mRoom.mTeams) {
            if (team.mScore >= MIN_END_GAME_POINTS) {
                return team;
            }
        }
        return null;
    }

    private boolean isATeamFinishTheGame() {
        for (Team team : mRoom.mTeams) {
            if (team.mScore >= MIN_END_GAME_POINTS) {
                return true;
            }
        }
        return false;
    }

    private void processGameRoundPoints() {
        boolean attackerWon = false;
        for (Team team : mRoom.mTeams) {
            if (team.isAttackerTeam) {
                if (team.mScore >= mCurContract.mValue) {
                    team.mScore += mCurContract.mValue;
                    team.isWonTheSet = true;
                    attackerWon = true;
                    break;
                }
            }
        }
        for (Team team : mRoom.mTeams) {
            if (!team.isAttackerTeam && !attackerWon) {
                if (mCurContract.mIsCoinched) {
                    team.mScore += 2 * (mCurContract.mValue + 162);
                } else {
                    team.mScore += mCurContract.mValue + 162;
                }
                team.isWonTheSet = true;
                break;
            }
        }
    }

    private int getSetTurn() {
        return 8 - mRoom.mTeams.get(0).mPlayers.get(0).mHand.size();
    }

    private int getSetWinnerTeamId() {
        Card card = getBestSetCard();
        if (card != null) {
            for (Team team : mRoom.mTeams) {
                for (Player player : team.mPlayers) {
                    if (player.mName.equals(card.mOwnerName)) {
                        return team.mId;
                    }
                }
            }
        }
        return -1;
    }

    private Card getBestSetCard() {
        Card bestCard = new Card();
        for (Card card : mPlayMat) {
            if (bestCard.mCardValue == null) {
                bestCard = card;
            } else if (getCardValueAccordingToTheAsset(card) > getCardValueAccordingToTheAsset(bestCard)) {
                bestCard = card;
            }
        }
        return bestCard;
    }

    private void processWinnerTeamScore(int teamId) {
        int score = 0;
        for (Card card : mPlayMat) {
            score += getCardValueAccordingToTheAsset(card);
        }
        for (Team team : mRoom.mTeams) {
            if (team.mId == teamId) {
                team.mScore += score;
            }
        }
    }

    private int getCardValueAccordingToTheAsset(Card card) {
        if (isThisCardIsAsset(card)) {
            switch (card.mCardValue) {
                case JACK:
                    return 20;
                case NINE:
                    return 14;
                case ACE:
                    return 11;
                case TEN:
                    return 10;
                case KING:
                    return 4;
                case QUEEN:
                    return 3;
                case EIGHT:
                    return 0;
                case SEVEN:
                    return 0;
            }
        } else {
            switch (card.mCardValue) {
                case ACE:
                    return 11;
                case TEN:
                    return 10;
                case KING:
                    return 4;
                case QUEEN:
                    return 3;
                case JACK:
                    return 2;
                case NINE:
                    return 0;
                case EIGHT:
                    return 0;
                case SEVEN:
                    return 0;
            }
        }
        return 0;
    }

    private boolean isThisCardIsAsset(Card card) {
        switch (mCurContract.mAsset) {
            case HEART:
                return card.mColorType == Card.ColorType.HEART;
            case CLUB:
                return card.mColorType == Card.ColorType.CLUB;
            case SPADE:
                return card.mColorType == Card.ColorType.SPADE;
            case DIAMOND:
                return card.mColorType == Card.ColorType.DIAMOND;
            case ALL_ASSET:
                return true;
            case WITHOUT_ASSET:
                return false;
            default:
                return false;
        }
    }

    private Team getDefendantTeam(int attackerTeamId) {
        if (attackerTeamId == 0) {
            return mRoom.mTeams.get(1);
        } else {
            return mRoom.mTeams.get(0);
        }
    }

    private Team getAttackerTeam(Contract biggestContract) {
        for (Team team : mRoom.mTeams) {
            for (Player player : team.mPlayers) {
                if (player.mName.equals(biggestContract.mOwnerName)) {
                    team.isAttackerTeam = true;
                    return team;
                }
            }
        }
        return null;
    }

    private Contract getBiggestContract() {
        Contract refContract = new Contract();
        refContract.mValue = 0;
        for (Contract contract : mBidList) {
            if (contract.mValue > refContract.mValue) {
                refContract = contract;
            }
        }
        return refContract;
    }

    private boolean isBidIsChecked() {
        for (Contract contract : mBidList) {
            if (contract.mAsset != Contract.AssetType.CHECK) {
                return false;
            }
        }
        return true;
    }

    private Player getPlayerAccordingToTurn(int turn) {
        switch (turn) {
            case 0:
                return mRoom.mTeams.get(0).mPlayers.get(0);
            case 1:
                return mRoom.mTeams.get(1).mPlayers.get(0);
            case 2:
                return mRoom.mTeams.get(0).mPlayers.get(1);
            case 3:
                return mRoom.mTeams.get(1).mPlayers.get(1);
        }
        return null;
    }
}
