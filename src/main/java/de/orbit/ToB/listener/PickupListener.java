package de.orbit.ToB.listener;

import de.orbit.ToB.ToB;
import de.orbit.ToB.arena.ArenaManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;

import java.util.Optional;

public class PickupListener {

    @Listener
    public void onSandPickup(ChangeInventoryEvent.Pickup event) {

        ArenaManager arenaManager = ToB.get(ArenaManager.class);
        Optional<Player> playerOptional = event.getCause().first(Player.class);


        playerOptional.ifPresent(player -> {

            if(arenaManager.getPlayer(player).isPresent()) {

                event.getTransactions().forEach(e -> {
                    player.getInventory().iterator().forEachRemaining(inv -> {
                    });
                });

            }

        });

    }


}
