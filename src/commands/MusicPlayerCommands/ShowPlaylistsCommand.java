package commands.MusicPlayerCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.Command;
import commands.CommandRunner;
import database.MyDatabase;
import musicplayer.MusicPlayer;

public final class ShowPlaylistsCommand extends Command implements CommandRunner {
    public ShowPlaylistsCommand(final String command, final String username,
            final Integer timestamp) {
        super(command, username, timestamp);
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());

        output.put("result", database.showPlaylists(getUsername()));
        return output;
    }
}
