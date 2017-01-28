package de.orbit.ToB.listener;

import de.orbit.ToB.ToB;
import de.orbit.ToB.arena.ArenaManager;
import de.orbit.ToB.arena.ArenaSignEntry;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

public class BlockListener {

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {

        ArenaManager arenaManager = ToB.get(ArenaManager.class);

        //@TODO We should create areas from min & max locations in arena, to validate if the block is even in one arena
        // because if not then we can just ignore this event.

        event.getTransactions().forEach(e -> {

            BlockSnapshot snapshot = e.getOriginal();
            BlockState blockState = snapshot.getState();

            //--- If a sign from an arena gets destroyed, we have to notify the arena and remove it.
            if(blockState.getType() == BlockTypes.WALL_SIGN || blockState.getType() == BlockTypes.STANDING_SIGN) {

                arenaManager.getArenas().forEach(arena -> {

                    arena.getSigns().stream()
                        .filter(
                            sign -> sign.getSign().getLocation().getBlockPosition().equals(snapshot.getLocation().get().getBlockPosition())
                        )
                        .forEach(ArenaSignEntry::remove);

                });

            }

        });

    }

}
