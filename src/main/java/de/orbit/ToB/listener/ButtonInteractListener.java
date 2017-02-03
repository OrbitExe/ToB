package de.orbit.ToB.listener;

import de.orbit.ToB.MessageHandler;
import de.orbit.ToB.ToB;
import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.ArenaManager;
import de.orbit.ToB.arena.ArenaPlateEntry;
import de.orbit.ToB.arena.ArenaPlayer;
import de.orbit.ToB.arena.team.TeamType;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

public class ButtonInteractListener {

    @Listener
    public void onButtonPush(InteractBlockEvent event, @First Player player) {

        BlockSnapshot blockSnapshot = event.getTargetBlock();

        //--- It should be a stone button
        if(!(blockSnapshot.getState().getType() == BlockTypes.STONE_BUTTON)) {
            return;
        }

        ArenaManager arenaManager = ToB.get(ArenaManager.class);
        Optional<ArenaPlayer> arenaPlayerOptional = arenaManager.getPlayer(player);

        //--- If we aren't in an arena; just ignore it.
        if(!(arenaPlayerOptional.isPresent())) {
            return;
        }

        ArenaPlayer arenaPlayer = arenaPlayerOptional.get();
        Arena arena = arenaPlayer.getArena();
        TeamType team = arenaPlayer.getTeamType();

        //--- Check if it is the correct button
        Location<World> button = arena.getButton(team);

        if(!(button.getBlockPosition().equals(blockSnapshot.getLocation().get().getBlockPosition()))) {
            return;
        }

        List<ArenaPlateEntry> plates = arena.getPlates(team);
        List<ArenaPlayer> teamPlayers = arena.getTeam(team);

        //--- The amount of players required to be on a pressure plate to activate the win button
        int required = (teamPlayers.size() - 1);

        int count = 0;
        for(ArenaPlateEntry e : plates) {
            ToB.get(MessageHandler.class).send(
                    player,
                    String.valueOf(player.getLocation().getBlockPosition().distance(e.getLocation().getBlockPosition()))
            );
            if(
                //--- The plate is powered
                e.getLocation().get(Keys.POWERED).get() &&
                //--- The player is actually on the plate
                teamPlayers.stream().anyMatch(p -> p.getPlayer().getLocation().getBlockPosition().distanceSquared(e.getLocation().getBlockPosition()) <= 2)
            ) {
                count++;
            }
        }

        assert count == required;
        //@TODO Trigger win event

    }

}
