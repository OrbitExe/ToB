package de.orbit.ToB;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.Map;

public class MessageHandler implements Component {

    private final static Text PREFIX = Text.builder()
                .color(TextColors.GOLD)
                .append(Text.of("["))
                .append(Text.builder().color(TextColors.AQUA).append(Text.of("ToB")).build())
                .append(Text.of("]"))
            .build();

    private final static Text SPACE = Text.of(" ");

    public MessageHandler() {}

    /**
     * <p>
     *    Sends a message to the provided the channel. The level decides what color the message will be. The last two
     *    arguments will be passed to {@link String#format(String, Object...)} to easily provide support for formatting.
     * </p>
     *
     * @param player
     * @param level
     * @param format
     * @param arguments
     */
    public void send(Player player, Level level, String format, Object... arguments) {
        player.sendMessage(
            Text.builder()
                .append(PREFIX)
                .append(SPACE)
                .color(level.color())
                .append(Text.of(
                    String.format(format, arguments)
                ))
            .build()
        );
    }

    public void send(Player player, Text text) {
        player.sendMessage(
            Text.builder()
                .append(PREFIX)
                .append(SPACE)
                .append(text)
            .build()
        );
    }

    public void send(Player player, Level level, String text) {
        this.send(player, level, text, false, new Object[]{});
    }

    public void send(Player player, String text) {
        this.send(player, Level.INFO, text);
    }

    @Override
    public void setup() {}

    @Override
    public void shutdown() {}

    public enum Level {

        ERROR(TextColors.RED),
        SUCCESS(TextColors.GREEN),
        INFO(TextColors.WHITE);

        private TextColor color;

        Level(TextColor color) {
            this.color = color;
        }

        public TextColor color() {
            return this.color;
        }
    }

}
