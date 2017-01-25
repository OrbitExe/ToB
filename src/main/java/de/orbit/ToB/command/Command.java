package de.orbit.ToB.command;

import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public interface Command extends CommandExecutor {

    public String[] commands();

    public CommandSpec getCommandSpec();

}
