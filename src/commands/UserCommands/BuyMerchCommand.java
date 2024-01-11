package commands.UserCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.Command;
import commands.CommandRunner;
import database.MyDatabase;
import musicplayer.MusicPlayer;

public final class BuyMerchCommand extends Command implements CommandRunner {
    private String merchName;

    public BuyMerchCommand(final String command,
            final String username, final Integer timestamp,
            final String merchName) {
        super(command, username, timestamp);
        this.merchName = merchName;
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());

        output.put("message", database.buyMerch(getUsername(), merchName));
        return output;
    }
}
