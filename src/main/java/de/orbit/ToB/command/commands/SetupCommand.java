package de.orbit.ToB.command.commands;

import com.flowpowered.math.vector.Vector3d;
import de.orbit.ToB.MessageHandler;
import de.orbit.ToB.ToB;
import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.ArenaManager;
import de.orbit.ToB.arena.team.TeamType;
import de.orbit.ToB.arena.team.TeamTypes;
import de.orbit.ToB.command.Command;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Optional;

public class SetupCommand implements Command {

    public SetupCommand() {}

    @Override
    public String[] commands() {
        return new String[] { "setup" };
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .description(Text.of("ToB Setup"))
                .permission("tob.setup")
                .executor(this)
                .arguments(
                    GenericArguments.seq(
                        GenericArguments.integer(Text.of("id")),
                        GenericArguments.choices(Text.of("action"), new HashMap<String, String>() {{
                            this.put("create", "create");
                            this.put("sign", "sign");
                            this.put("button", "button");
                            this.put("plate", "plate");
                            this.put("spawn", "spawn");
                        }}, true),
                        GenericArguments.optional(
                            GenericArguments.string(Text.of("value"))
                        )
                    )
                )
                .build();
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {

        int id = commandContext.<Integer>getOne("id").get();
        String action = commandContext.<String>getOne("action").get();
        Optional<String> value = commandContext.getOne("value");

        ArenaManager arenaManager = ToB.get(ArenaManager.class);
        MessageHandler messageHandler = ToB.get(MessageHandler.class);

        Optional<Arena> arenaOptional = arenaManager.get(id);

        Player player = (Player) commandSource;

        //--- Handling all cases
        switch (action.toLowerCase()) {

            case "create": {

                if(arenaOptional.isPresent()) {
                    messageHandler.send(
                            player,
                            MessageHandler.Level.ERROR,
                            "An arena with the id %d already exists. Please choose another available id. Recommended ID: %d",
                            id, arenaManager.getRecommendedId()
                    );
                    return CommandResult.success();
                }

                if(!(value.isPresent())) {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                        "Please provide the max amount of players allowed in this arena."
                    );
                    return CommandResult.success();
                }

                int maxPlayer = Integer.parseInt(value.get());
                Optional<String> success = arenaManager.add(new Arena(id, maxPlayer), false);
                if(success.isPresent()) {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                        success.get()
                    );
                } else {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.SUCCESS,
                        "Successfully created the arena with the id %d and a max-player amount of %d.",
                        id,
                        maxPlayer
                    );
                }

            }
            break;

            case "sign": {

                //--- Check if the arena exists
                if(!(arenaOptional.isPresent())) {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                        "An arena with the id %d does not exists.",
                        id
                    );
                    return CommandResult.success();
                }

                Optional<BlockRayHit<World>> blockRay = BlockRay.from(player)
                    .skipFilter(e -> {
                        BlockType blockType = e.getExtent().getBlockType(e.getBlockPosition());
                        return (blockType == BlockTypes.WALL_SIGN || blockType == BlockTypes.STANDING_SIGN);
                    })
                    .stopFilter(e -> e.getExtent().getBlockType(e.getBlockPosition()) != BlockTypes.AIR)
                    .distanceLimit(3)
                    .build()
                    .end();

                //--- Check if we even got a block
                if(blockRay.isPresent()) {

                    Arena arena = arenaOptional.get();

                    Sign tileEntity = (Sign) blockRay.get().getExtent().getTileEntity(blockRay.get().getBlockPosition()).get();
                    SignData signData = tileEntity.get(SignData.class).get();

                    Optional<TeamType> teamType = TeamTypes.toTeam(signData.get(0).get().toPlain());
                    String identifier = signData.get(1).get().toPlain();
                    String signValue = signData.get(2).get().toPlain();

                    switch (identifier.toLowerCase()) {

                        case "class": {

                            if(teamType.isPresent()) {

                                arena.addSign(Arena.SignType.CLASS, teamType.get(), tileEntity);
                                arena.updateSigns();

                            } else {
                                messageHandler.send(
                                    player,
                                    MessageHandler.Level.ERROR,
                                    "Please provide the team this signs belongs to. It can be: %s. You have to place the value" +
                                        "of your choice on the first line of the sign.",
                                    StringUtils.join(TeamTypes.values(), ", ")
                                );
                                return CommandResult.success();
                            }

                        }
                        break;

                        case "lobby": {

                            //--- Add & Update sign and send success message
                            arena.addSign(Arena.SignType.LOBBY, null, tileEntity);
                            arena.updateSigns();

                            messageHandler.send(
                                player,
                                MessageHandler.Level.SUCCESS,
                                "Successfully the arena lobby sign."
                            );

                        }
                        break;

                        default: {
                            messageHandler.send(
                                player,
                                MessageHandler.Level.ERROR,
                                "%s is an unknown sign identifier.",
                                (identifier.isEmpty() ? "<empty>" : identifier)
                            );
                        }
                        break;

                    }


                } else {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                        "Please make sure that you you are looking at the sign with the cross in the middle of the screen. " +
                            "You should also ensure that the distance between you and the sign is equals or less than 3 blocks."
                    );
                }

            }
            break;

            /**
             * Setting the button points in the arena.
             */
            case "button": {

                //--- Check if the arena exists
                if(!(arenaOptional.isPresent())) {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                        "An arena with the id %d does not exists.",
                        id
                    );
                    return CommandResult.success();
                }

                Arena arena = arenaOptional.get();

                //--- if the value for the team does not exist
                if(
                    !(value.isPresent()) ||
                        (
                            !(value.get().equalsIgnoreCase("red")) &&
                            !(value.get().equalsIgnoreCase("blue"))
                        )
                ) {
                    messageHandler.send(
                            player,
                            MessageHandler.Level.ERROR,
                            "Please provide the name of the team you wanna set the button of: red or blue."
                    );
                    return CommandResult.success();
                }

                //--- get the block the player is looking at
                Optional<BlockRayHit<World>> blockRay = BlockRay.from(player)
                        .skipFilter(e -> {
                            BlockType blockType = e.getExtent().getBlockType(e.getBlockPosition());
                            return (blockType == BlockTypes.STONE_BUTTON || blockType == BlockTypes.WOODEN_BUTTON);
                        })
                        .stopFilter(e -> e.getExtent().getBlockType(e.getBlockPosition()) != BlockTypes.AIR)
                        .distanceLimit(3)
                        .build()
                        .end();

                boolean conditions = blockRay.isPresent();

                //--- validate if the button is also attached to EMERALD_BLOCK
                if(conditions) {
                    BlockRayHit<World> hit = blockRay.get();
                    Vector3d face = hit.getDirection().negate();
                    conditions = hit.getExtent().getBlockType(hit.getLocation().sub(face).getBlockPosition()) == BlockTypes.EMERALD_BLOCK;
                }

                if(conditions) {



                } else {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                        "Please make sure that you you are looking at the button with the cross in the middle of the screen. " +
                            "The block the button is attached to has to consist of emerald. " +
                            "You should also ensure that the distance between you and the sign is equals or less than 3 blocks."
                    );
                }

            }
            break;

            case "plate": {

            }

            /**
             * Setting spawn points in the arena.
             */
            case "spawn": {

                //--- Check if the arena exists
                if(!(arenaOptional.isPresent())) {
                    messageHandler.send(
                            player,
                            MessageHandler.Level.ERROR,
                            "An arena with the id %d does not exists.",
                            id
                    );
                    return CommandResult.success();
                }

                //--- if the value for the spawn does not exist
                if(
                    !(value.isPresent()) ||
                    (
                        !(value.get().equalsIgnoreCase("red")) &&
                        !(value.get().equalsIgnoreCase("blue")) &&
                        !(value.get().equalsIgnoreCase("lobby"))
                    )
                ) {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                        "Please provide the position name you wanna set the spawn of: red, blue or lobby."
                    );
                    return CommandResult.success();
                }

                Arena arena = arenaOptional.get();

                switch (value.get().toLowerCase()) {

                    case "red":
                        arena.setSpawnPoint(TeamTypes.RED, player.getLocation());
                        messageHandler.send(
                            player,
                            MessageHandler.Level.SUCCESS,
                            "Successfully set the spawn of the red team for arena %s at your location.",
                            TeamTypes.RED.displayName()
                        );
                        break;

                    case "blue":
                        arena.setSpawnPoint(TeamTypes.BLUE, player.getLocation());
                        messageHandler.send(
                            player,
                            MessageHandler.Level.SUCCESS,
                            "Successfully set the spawn of the red team for arena %s at your location.",
                            TeamTypes.BLUE.displayName()
                        );
                        break;

                    case "lobby":
                        arena.setLobbyPoint(player.getLocation());
                        messageHandler.send(
                                player,
                                MessageHandler.Level.SUCCESS,
                                "Successfully set the lobby point of the arena for arena %s at your location.",
                                TeamTypes.BLUE.displayName()
                        );
                        break;

                }

            }

            break;

        }

        return CommandResult.success();
    }
}
