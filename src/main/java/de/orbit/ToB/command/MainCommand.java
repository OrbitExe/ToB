package de.orbit.ToB.command;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import de.orbit.ToB.ToB;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainCommand implements Command {

    public MainCommand() {}

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        return CommandResult.success();
    }

    @Override
    public String[] commands() {
        return new String[] { "tob" };
    }

    @Override
    public CommandSpec getCommandSpec() {

        List<Command> subCommands = new ArrayList<>();

        //--- Load all sub-commands
        try {
            ClassPath classPath = ClassPath.from(this.getClass().getClassLoader());
            ImmutableSet<ClassPath.ClassInfo> commands = classPath.getTopLevelClasses("de.SweetCode.ToB.command.commands");
            commands.forEach(e -> {
                Class<?> clazz = e.load();
                try {
                    subCommands.add((Command) clazz.newInstance());
                } catch (Exception ex) {
                    ToB.getLogger().error(String.format("Command - Failed to load the %s.", clazz.getSimpleName()));
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        CommandSpec.Builder builder = CommandSpec.builder()
                            .description(Text.of("ToB Awesome"))
                            .permission("tob.help")
                            .executor(this);

        subCommands.forEach(e -> builder.child(e.getCommandSpec(), e.commands()));

        return builder.build();
    }
}
