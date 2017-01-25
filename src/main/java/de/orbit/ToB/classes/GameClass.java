package de.orbit.ToB.classes;

import de.orbit.ToB.arena.ArenaPlayer;

public interface GameClass {

    /**
     * Gives a display name which can be used e.g.: in chats as a human-readable version.
     *
     * @return
     */
    String displayName();

    /**
     * The factor is a value between [0; 1) and describes how many players can join relatively this class per team. The
     * max amount of all defaultFactors is 1 and must be exactly hit.
     *
     * Example:
     *  n = 10 players/arena
     *  defaultFactor = 0.2
     *
     *  The defaultFactor describes that 20% of the players per team can join this class.
     *
     * @return
     */
    double defaultFactor();

    /**
     * This method will be called when a player selects & can supply this class. This class is reponsible for clearing
     * its inventory, giving magical effects, items and so on and on.
     *
     * @param player
     */
    void apply(ArenaPlayer player);

}
