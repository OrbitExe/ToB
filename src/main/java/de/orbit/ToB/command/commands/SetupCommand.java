package de.orbit.ToB.command.commands;

import de.orbit.ToB.MessageHandler;
import de.orbit.ToB.ToB;
import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.ArenaManager;
import de.orbit.ToB.arena.ArenaPlayer;
import de.orbit.ToB.arena.ArenaSignEntry;
import de.orbit.ToB.arena.team.TeamType;
import de.orbit.ToB.arena.team.TeamTypes;
import de.orbit.ToB.arena.validator.ArenaValidator;
import de.orbit.ToB.arena.validator.RuleState;
import de.orbit.ToB.classes.GameClass;
import de.orbit.ToB.classes.GameClasses;
import de.orbit.ToB.command.Command;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetupCommand implements Command {

    private final static Pattern PATTERN_TOWER_DIMENSION = Pattern.compile("^([0-9]+)(x)([0-9]+)$");

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
                        GenericArguments.choices(Text.of("action"), new HashMap<String, String>() {{
                            this.put("create", "create");
                            this.put("sign", "sign");
                            this.put("button", "button");
                            this.put("plate", "plate");
                            this.put("spawn", "spawn");
                            this.put("max-players", "max-players");
                            this.put("tower-dimension", "tower-dimension");
                            this.put("bound", "bound");
                            this.put("finish", "finish");

                            this.put("load", "load");
                            this.put("a", "a");
                            this.put("b", "b");
                        }}, true),
                        GenericArguments.optional(
                            GenericArguments.integer(Text.of("id"))
                        ),
                        GenericArguments.optional(
                            GenericArguments.string(Text.of("value"))
                        )
                    )
                )
            .build();
    }


    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {

        String action = commandContext.<String>getOne("action").get();

        Optional<Integer> id = commandContext.getOne("id");
        Optional<String> value = commandContext.getOne("value");

        ArenaManager arenaManager = ToB.get(ArenaManager.class);
        MessageHandler messageHandler = ToB.get(MessageHandler.class);

        Optional<Arena> arenaOptional = id.isPresent() ? arenaManager.get(id.get()) : Optional.empty();

        //@TODO Check that we actually have a player here
        Player player = (Player) commandSource;

        //--- Handling all cases
        switch (action.toLowerCase()) {

            case "a":
                arenaManager.get(0).get().createBackup(false);
                break;

            case "b":
                arenaManager.get(0).get().restoreBackup();
                break;

            case "load":
                arenaManager.unserialize();
                break;

            case "create": {

                int recommendedId = arenaManager.getRecommendedId();

                Optional<String> success = arenaManager.add(new Arena(recommendedId));
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
                        "Successfully created the arena with the id %d.",
                        true,
                        recommendedId
                    );
                }

            }
            break;

            case "max-players": {

                if(!(this.isArenaPresent(player, id, arenaOptional))) {
                    return CommandResult.success();
                }

                //--- if the value for the amount of players exists
                if(!(value.isPresent())) {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                        "Please provide as value the max amount of players which can join the arena."
                    );
                    return CommandResult.success();
                }

                Arena arena = arenaOptional.get();
                int amount = Integer.parseInt(value.get());

                //--- Check if the amount of players is dividable by 2, because we wanna have a even player distribution
                // across both teams in the bast case.
                if(amount % 2 == 0 && amount >= ToB.ARENA_MIN_PLAYER) {
                    arena.setMaxPlayers(amount);
                    messageHandler.send(
                        player,
                        MessageHandler.Level.SUCCESS,
                        "Set the max amount of players for arena %d to %d.",
                        arena.getIdentifier(),
                        amount
                    );
                    CommandResult.success();
                } else {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                        "The max amount of players must be dividable by 2 to ensure a even player distribution and it" +
                            " must be greater or equals to 4."
                    );
                    return CommandResult.success();
                }

            }
            break;

            /*
             * Setting signs.
             */
            case "sign": {

                if(!(this.isArenaPresent(player, id, arenaOptional))) {
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

                                Optional<GameClass> gameClass = GameClasses.toClass(signValue);

                                if(!(gameClass.isPresent())) {
                                    messageHandler.send(
                                        player,
                                        MessageHandler.Level.ERROR,
                                        "The game class %s does not exist.",
                                        signValue
                                    );
                                    return CommandResult.success();
                                }

                                arena.addSign(ArenaSignEntry.SignType.CLASS, teamType.get(), tileEntity, gameClass.get());
                                arena.updateSigns();

                                messageHandler.send(
                                    player,
                                    MessageHandler.Level.SUCCESS,
                                    "Successfully added the %s class sign.",
                                    gameClass.get().displayName()
                                );

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
                            arena.addSign(ArenaSignEntry.SignType.LOBBY, null, tileEntity, null);
                            arena.updateSigns();

                            messageHandler.send(
                                player,
                                MessageHandler.Level.SUCCESS,
                                "Successfully added the arena lobby sign."
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

            /*
              Setting the button points in the arena.
             */
            case "button": {

                if(!(this.isArenaPresent(player, id, arenaOptional))) {
                    return CommandResult.success();
                }

                Arena arena = arenaOptional.get();

                //--- if the value for the team does not exist
                if(!(value.isPresent()) || !TeamTypes.toTeam(value.get()).isPresent()) {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                        "Please provide the name of the team you wanna set the button of: %s or %s.",
                        TeamTypes.RED.displayName(),
                        TeamTypes.BLUE.displayName()
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
                    Direction direction = hit.getExtent().getBlock(hit.getBlockPosition()).get(Keys.DIRECTION).get();
                    conditions = hit.getExtent().getBlockType(
                            hit.getLocation().add(direction.getOpposite().asOffset()).getBlockPosition()
                    ) == BlockTypes.EMERALD_BLOCK;
                }

                //--- Looking at a BUTTON & the button is attached to a EMERALD block
                if(conditions) {

                    TeamType teamType = TeamTypes.toTeam(value.get()).get();

                    arena.setButtonPoint(
                        teamType,
                        blockRay.get().getLocation()
                    );

                    messageHandler.send(
                        player,
                        MessageHandler.Level.SUCCESS,
                        "Successfully set the win button of the %s team.",
                        teamType.displayName()
                    );

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

            /*
              Setting the plates of the arena.
             */
            case "plate": {

                if(!(this.isArenaPresent(player, id, arenaOptional))) {
                    return CommandResult.success();
                }

                Arena arena = arenaOptional.get();
                Location<World> location = player.getLocation();
                BlockState pressurePlate = location.getBlock();

                //--- Check if the player set the amount of players
                if(arena.getMaxPlayers() <= 0) {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                        "Please set the max amount of players for this arena before adding plates to it."
                    );
                    return CommandResult.success();
                }

                //--- Checking if it is a STONE_PRESSURE_PLATE
                if(!(pressurePlate.getType() == BlockTypes.STONE_PRESSURE_PLATE)) {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                        "Please go on a stone pressure plate."
                    );
                    return CommandResult.success();
                }

                BlockState blockState = player.getLocation().add(0, -1, 0).getBlock();

                //--- Checking if the block BELOW the pressure plate is a GOLD_BLOCK
                if(!(blockState.getType() == BlockTypes.GOLD_BLOCK)) {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                        "The pressure plate must be placed on a gold block."
                    );
                    return CommandResult.success();
                }

                //--- if the value for the spawn does not exist
                if(!(value.isPresent()) || !(TeamTypes.toTeam(value.get()).isPresent())) {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                        "Please provide the team name you wanna add a pressure plate for: %s or %s.",
                        TeamTypes.RED.displayName(),
                        TeamTypes.BLUE.displayName()
                    );
                    return CommandResult.success();
                }

                //--- Setting
                TeamType teamType = TeamTypes.toTeam(value.get()).get();

                int count = arena.getPlates(teamType).size();

                //--- Check if we already have enough plates for this team.
                // We wanna have (n / 2) - 1 plates per team. All standing on the plates except one guy pressing the hutton
                // in the best case.
                if(count == (arena.getMaxPlayers() / 2 - 1)) {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                        "The team has already %d plate(s). You cannot add anymore for team %s.",
                        count,
                        teamType.displayName()
                    );
                    return CommandResult.success();
                }

                arena.addPlate(player.getLocation(), teamType);

                messageHandler.send(
                    player,
                    MessageHandler.Level.SUCCESS,
                    "You have successfully added the plate for team %s. You have to add %d more to complete the setup for this team.",
                    teamType.displayName(),
                    ((arena.getMaxPlayers() / 2 - 1) - (count + 1))
                );

            }
            break;

            /*
              Setting spawn points in the arena.
             */
            case "spawn": {

                if(!(this.isArenaPresent(player, id, arenaOptional))) {
                    return CommandResult.success();
                }

                //--- if the value for the spawn does not exist
                if(
                    !(value.isPresent()) ||
                    (
                        !(value.get().equalsIgnoreCase(TeamTypes.RED.displayName())) &&
                        !(value.get().equalsIgnoreCase(TeamTypes.BLUE.displayName())) &&
                        !(value.get().equalsIgnoreCase("lobby"))
                    )
                ) {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                        "Please provide the team name you wanna set the spawn of: %s, %s or lobby.",
                        TeamTypes.RED.displayName(),
                        TeamTypes.BLUE.displayName()
                    );
                    return CommandResult.success();
                }

                Arena arena = arenaOptional.get();

                if(TeamTypes.RED.displayName().equalsIgnoreCase(value.get())) {
                    arena.setSpawnPoint(TeamTypes.RED, player.getLocation());
                    messageHandler.send(
                            player,
                            MessageHandler.Level.SUCCESS,
                            "Successfully set the spawn of the %s team for arena %d at your location.",
                            TeamTypes.RED.displayName(),
                            arena.getIdentifier()
                    );
                } else if(TeamTypes.BLUE.displayName().equalsIgnoreCase(value.get())) {
                    arena.setSpawnPoint(TeamTypes.BLUE, player.getLocation());
                    messageHandler.send(
                            player,
                            MessageHandler.Level.SUCCESS,
                            "Successfully set the spawn of the %s team for arena %d at your location.",
                            TeamTypes.BLUE.displayName(),
                            arena.getIdentifier()
                    );
                } else if(value.get().equalsIgnoreCase("lobby")) {
                    arena.setLobbyPoint(player.getLocation());
                    messageHandler.send(
                            player,
                            MessageHandler.Level.SUCCESS,
                            "Successfully set the lobby point of the arena for arena %d at your location.",
                            arena.getIdentifier()
                    );
                } else {
                    throw new NotImplementedException("Not implemented yet.");
                }

            }
            break;

            /*
               FINISH
             */
            case "finish": {

                if(!(this.isArenaPresent(player, id, arenaOptional))) {
                    return CommandResult.success();
                }

                ArenaValidator validator = new ArenaValidator(arenaOptional.get());
                List<ArenaValidator.ArenaValidatorEntry> results = validator.validate();

                Text.Builder builder = Text.builder();

                builder.append(
                    Text.builder(
                        String.format("Status Report - Arena %d", arenaOptional.get().getIdentifier()))
                        .append(Text.NEW_LINE)
                    .build()
                );

                results.forEach(e -> {

                    Text.Builder tmp = Text.builder();
                    RuleState ruleState = e.getRuleState();

                    //--- Prefix
                    tmp.append(
                        Text.builder("[" + ruleState.c() + "] ")
                            .color(ruleState.color())
                            .onHover(
                                TextActions.showText(
                                    Text.builder(StringUtils.capitalize(ruleState.name().toLowerCase()))
                                        .color(ruleState.color())
                                    .build()
                                )
                            )
                        .build()
                    );

                    //--- Description & Message
                    tmp.append(
                        Text.builder().append(e.displayName())
                            .onClick(
                                TextActions.executeCallback(
                                    p -> messageHandler.send(player, e.getDescription().toBuilder().color(e.getRuleState().color()).build())
                                )
                            )
                            .onHover(TextActions.showText(Text.builder("Click Me").style(TextStyles.ITALIC).build()))
                        .build()
                    );


                    tmp.append(Text.NEW_LINE);

                    builder.append(tmp.build());

                });

                builder.append(
                    Text.builder("_______________________").append(Text.NEW_LINE).build()
                );

                //--- Conclusion
                TextColor conclusionColor = TextColors.GREEN;

                long warning = validator.count(RuleState.ACCEPTABLE);
                long error = validator.count(RuleState.ERROR);

                if(error > 0) {
                    conclusionColor = TextColors.DARK_RED;
                } else if(warning > 0) {
                    conclusionColor = TextColors.YELLOW;
                }

                builder.append(
                    Text.builder(
                        String.format(
                            "%.2f%% of the setup completed.",
                            ((validator.count(RuleState.ACCEPTABLE) + validator.count(RuleState.FULFILLED)) / (double) validator.count(null)) * 100
                        )
                    )
                    .color(conclusionColor)
                    .build()
                );

                messageHandler.send(player, builder.build());
            }
            break;

            /*
               Setting the tower dimension of an arena.
             */
            case "tower-dimension": {

                if(!(this.isArenaPresent(player, id, arenaOptional))) {
                    return CommandResult.success();
                }

                //--- if the value for the dimension exists & is valid
                Matcher matcher;
                if(
                    !(value.isPresent()) ||
                    !((matcher = SetupCommand.PATTERN_TOWER_DIMENSION.matcher(value.get())).matches())
                ) {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                            "Please provide a valid tower dimension the format is: nxm. n and m are numeric value defining" +
                                "width and height of the 2D area.",
                            TeamTypes.RED.displayName(),
                            TeamTypes.BLUE.displayName()
                    );
                    return CommandResult.success();
                }

                Arena arena = arenaOptional.get();

                Dimension dimension = new Dimension(
                    Integer.valueOf(matcher.group(1)),
                    Integer.valueOf(matcher.group(3))
                );

                arena.setTowerDimension(dimension);

                messageHandler.send(
                    player,
                    MessageHandler.Level.SUCCESS,
                        "You have successfully set the tower dimension to %dx%d for arena %d.",
                        (int) dimension.getWidth(),
                        (int) dimension.getHeight(),
                        arena.getIdentifier()
                );

            }
            break;

            case "bound": {

                if(!(this.isArenaPresent(player, id, arenaOptional))) {
                    return CommandResult.success();
                }

                //--- if the value for the min or max position exists
                if(
                    !(value.isPresent()) ||
                    (
                        !(value.get().equalsIgnoreCase("min")) &&
                        !(value.get().equalsIgnoreCase("max"))
                    )
                ) {
                    messageHandler.send(
                        player,
                        MessageHandler.Level.ERROR,
                            "Please provide the name of the bound you wanna set: min or max as value."
                    );
                    return CommandResult.success();
                }

                Arena arena = arenaOptional.get();

                String boundName = value.get().toLowerCase();
                Location<World> location = player.getLocation();

                if(boundName.equalsIgnoreCase("min")) {
                    arena.setBoundaries(
                        arena.getAreaMax(),
                        location
                    );
                } else {
                    arena.setBoundaries(
                        location,
                        arena.getAreaMin()
                    );
                }

                messageHandler.send(
                    player,
                    MessageHandler.Level.SUCCESS,
                        "Successfully set the %s bound for the arena %d at your location.",
                        boundName,
                        arena.getIdentifier()
                );

            }
            break;

        }

        return CommandResult.success();
    }

    private boolean isArenaPresent(Player player, Optional<Integer> id, Optional<Arena> arenaOptional) {

        MessageHandler messageHandler = ToB.get(MessageHandler.class);

        //--- Check if ID provided
        if(!(id.isPresent())) {
            messageHandler.send(
                    player,
                    MessageHandler.Level.ERROR,
                    "Please provide a valid arena id."
            );
            return false;
        }

        //--- Check if the arena exists
        if(!(arenaOptional.isPresent())) {
            messageHandler.send(
                    player,
                    MessageHandler.Level.ERROR,
                    "An arena with the id %d does not exists.",
                    id.get()
            );
            return false;
        }

        return true;

    }

}
