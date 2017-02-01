package de.orbit.ToB;

import com.google.inject.Inject;
import de.orbit.ToB.arena.ArenaManager;
import de.orbit.ToB.command.MainCommand;
import de.orbit.ToB.listener.BlockListener;
import de.orbit.ToB.listener.PickupListener;
import de.orbit.ToB.listener.SignListener;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;

@Plugin(
        id = "tob",
        name = "Tower of Babel",
        description = "ToB is a mini-game plugin bringing a Bible story to life.",
        version = "1.0",
        url = "https://github.com/sweetcode",
        authors = { "Jan Krueger" }
)
public class ToB {

    public static final int ARENA_MIN_PLAYER = 4;

    private final static Map<Class, Component> components = new HashMap<>();

    {
        ToB.components.put(Hologram.class, new Hologram());
        ToB.components.put(ArenaManager.class, new ArenaManager());
        ToB.components.put(MessageHandler.class, new MessageHandler());
    }

    private static ToB instance;

    @Inject
    private Logger logger;

    public ToB() {
        ToB.instance = this;
    }

    /**
     * <p>
     *     Gives the logger responsible and supposed to be globally used to log all important information related to the
     *     system.
     * </p>
     *
     * @return
     */
    public static Logger getLogger() {
        return ToB.getInstance().logger;
    }

    public static ToB getInstance() {
        return ToB.instance;
    }

    public static <T extends Component> T get(Class<T> component) {

        if(!(ToB.components.containsKey(component))) {
            throw new IllegalArgumentException(String.format("Component %s does not exist.", component.getSimpleName()));
        }

        return (T) ToB.components.get(component);
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {

        ToB.getLogger().info("Kicking off the initializing process to prepare all necessary components.");
        ToB.components.forEach((k, v) -> {
            ToB.getLogger().info(String.format("Setting up %s component.", v.getClass().getSimpleName()));
            v.setup();
        });

        Sponge.getEventManager().registerListeners(this, new SignListener());
        Sponge.getEventManager().registerListeners(this, new BlockListener());
        Sponge.getEventManager().registerListeners(this, new PickupListener());

        //--- Commands
        MainCommand mainCommand = new MainCommand();
        Sponge.getCommandManager().register(this, mainCommand.getCommandSpec(), mainCommand.commands());

        //--- CW
        Sponge.getCommandManager().register(this, CommandSpec.builder()
            .executor((commandSource, commandContext) -> {

                Player player = (Player) commandSource;
                player.setLocation(Sponge.getServer().getWorld(commandContext.getOne("value").get().toString()).get().getSpawnLocation());

            return CommandResult.success();
        }).arguments(GenericArguments.optional(
                        GenericArguments.string(Text.of("value"))
                )).build(), "cw");

    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        ToB.getLogger().info("We are done here.");
        ToB.components.forEach((k, v) -> {
            v.shutdown();
            ToB.getLogger().info(String.format("Shutdown %s component.", v.getClass().getSimpleName()));
        });
    }

}
