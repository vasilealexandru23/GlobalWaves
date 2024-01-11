package commands.MusicPlayerCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.Command;
import commands.CommandRunner;
import database.MyDatabase;
import musicplayer.MusicPlayer;

public final class AdBreakCommand extends Command implements CommandRunner {
    private int price;
    public AdBreakCommand(final String command, final String username,
            final Integer timestamp, final int price) {
        super(command, username, timestamp);
        this.price = price;
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());

        output.put("message", database.adBreak(getUsername(), price));

        return output;
    }
}
