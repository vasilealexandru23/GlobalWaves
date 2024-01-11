package commands.HostCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.Command;
import commands.CommandRunner;
import database.MyDatabase;
import musicplayer.MusicPlayer;

public final class RemoveAnnouncementCommand extends Command implements CommandRunner {
    private String name;

    public RemoveAnnouncementCommand(final String command, final String username,
            final Integer timestamp, final String name) {
        super(command, username, timestamp);
        this.name = name;
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());

        output.put("message", database.removeAnnouncement(getUsername(), name));
        return output;
    }
}
