package de.orbit.ToB.listener;

import de.orbit.ToB.ToB;
import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.ArenaManager;
import de.orbit.ToB.arena.ArenaPlayer;
import de.orbit.ToB.arena.ArenaSignEntry;
import de.orbit.ToB.arena.team.TeamType;
import de.orbit.ToB.arena.team.TeamTypes;
import de.orbit.ToB.classes.GameClass;
import de.orbit.ToB.classes.GameClasses;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class SignListener  {

    /**
     * This method is going to handle all entity interactions with the SECONDARY input (right-mouse button), if the player
     * is in the arena. The setup mechanism has a separated listener.
     */
    @Listener
    public void onClickArenaSign(InteractBlockEvent.Secondary.MainHand event) {

        Optional<ArenaPlayer> cause = ToB.get(ArenaManager.class).getPlayer(
            event.getCause().first(Player.class).get()
        );

        // if the player is currently in no arena: we don't care
        if(!(cause.isPresent())) {
            return;
        }

        ArenaPlayer player = cause.get();
        Arena arena = player.getArena();
        BlockState target = event.getTargetBlock().getState();

        // if we got neither a WALL_SIGN or STANDING_SIGN we are not interested
        if(!(target.getType() == BlockTypes.WALL_SIGN) && !(target.getType() == BlockTypes.STANDING_SIGN)) {
            return;
        }


        TileEntity entity = event.getTargetBlock().getLocation().get().getTileEntity().get();
        SignData signData = entity.get(SignData.class).get();

        // Extracting data from sign
        ListValue<Text> text = signData.lines();

        TeamType teamType   = TeamTypes.toTeam(text.get(0).getColor()).get();
        String identifier   = text.get(1).toPlain();
        String value        = text.get(2).toPlain();

        //--- We have to handle multiple cases
        //--- 1.: if somebody clicks on a class sign
        if(arena.existsSign(ArenaSignEntry.SignType.CLASS, entity.getLocation())) {

            GameClass gameClass = GameClasses.toClass(value).get();
            gameClass.apply(player);

        }

    }

}
