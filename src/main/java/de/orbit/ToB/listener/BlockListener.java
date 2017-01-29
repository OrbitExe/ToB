package de.orbit.ToB.listener;

import de.orbit.ToB.MessageHandler;
import de.orbit.ToB.ToB;
import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.ArenaManager;
import de.orbit.ToB.arena.ArenaPlateEntry;
import de.orbit.ToB.arena.ArenaSignEntry;
import de.orbit.ToB.arena.team.TeamType;
import de.orbit.ToB.arena.team.TeamTypes;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import java.util.Optional;
import java.util.stream.Collectors;

public class BlockListener {

    @Listener
    public void onGameBlockBreak(ChangeBlockEvent.Break event) {

        ArenaManager arenaManager = ToB.get(ArenaManager.class);
        MessageHandler messageHandler = ToB.get(MessageHandler.class);

        Optional<Player> playerOptional = event.getCause().get("Owner", Player.class);

        event.getTransactions().forEach(e -> {

            BlockSnapshot snapshot = e.getOriginal();
            BlockState blockState = snapshot.getState();

            Optional<Arena> arenaOptional = arenaManager.get(snapshot.getLocation().get());

            //--- Check for permissions, so on and so on
            if(!(arenaOptional.isPresent())) {
                return;
            }

            //--- What we all do if a player tries to do this
            if(playerOptional.isPresent()) {
                Arena arena = arenaOptional.get();
                Player player = playerOptional.get();

                //@TODO Keep track of permission
                if(!(player.hasPermission("tob.area.modify"))) {
                    event.setCancelled(true);
                    return;
                }

                //--- If a sign from an arena gets destroyed, we have to notify the arena and remove it.
                if (blockState.getType() == BlockTypes.WALL_SIGN || blockState.getType() == BlockTypes.STANDING_SIGN) {

                    arena.getSigns().stream()
                        .filter(sign -> !(sign == null))
                        .filter(
                            sign -> sign.getSign().getLocation().equals(snapshot.getLocation().get())
                        )
                        .collect(Collectors.toList())
                        .iterator().forEachRemaining(sign -> {
                            sign.remove();
                            messageHandler.send(
                                player,
                                MessageHandler.Level.SUCCESS,
                                "You successfully removed a game related sign from the arena."
                            );
                        });

                } else if (blockState.getType() == BlockTypes.STONE_PRESSURE_PLATE) {

                    arena.getPlates().stream()
                        .filter(plate -> !(plate == null))
                        .filter(
                            plate -> plate.getLocation().equals(snapshot.getLocation().get())
                        )
                        .collect(Collectors.toList())
                        .iterator().forEachRemaining(plate -> {
                            plate.remove();
                            messageHandler.send(
                                player,
                                MessageHandler.Level.SUCCESS,
                                "You successfully removed a game related pressure plate from the arena."
                            );
                        });

                } else if (blockState.getType() == BlockTypes.STONE_BUTTON) {

                    Location<World> red = arena.getButton(TeamTypes.RED);
                    Location<World> blue = arena.getButton(TeamTypes.BLUE);

                    if (red.equals(snapshot.getLocation().get())) {
                        arena.setButtonPoint(TeamTypes.RED, null);
                        messageHandler.send(
                            player,
                            MessageHandler.Level.SUCCESS,
                            "You successfully removed the %s team win button from the arena.",
                                TeamTypes.RED.displayName()
                        );
                    } else if (blue.equals(snapshot.getLocation().get())) {
                        arena.setButtonPoint(TeamTypes.BLUE, null);
                        messageHandler.send(
                            player,
                            MessageHandler.Level.SUCCESS,
                            "You successfully removed the %s team win button from the arena.",
                            TeamTypes.BLUE.displayName()
                        );
                    }

                }

            }

        });


    }

    @Listener
    public void onExplode(ExplosionEvent.Pre event) {

        ArenaManager arenaManager = ToB.get(ArenaManager.class);
        Optional<Arena> arenaOptional = arenaManager.get(event.getExplosion().getLocation());

        arenaOptional.ifPresent(e -> {

            Explosion original = event.getExplosion();
            event.setExplosion(
                Explosion.builder()
                    .canCauseFire(false)
                    .location(original.getLocation())
                    .radius(original.getRadius())
                    .shouldBreakBlocks(true)
                    .shouldDamageEntities(true) //@TODO not sure yet...
                    .shouldPlaySmoke(true)
                .build()
            );

        });

    }

}
