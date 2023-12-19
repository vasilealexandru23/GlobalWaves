package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import database.MyDatabase;
import musicplayer.MusicPlayer;

public final class AddRemoveInPlaylistCommand extends Command implements CommandRunner {
    private Integer playlistID;

    public AddRemoveInPlaylistCommand(final String command, final String username,
            final Integer timestamp, final Integer playlistID) {
        super(command, username, timestamp);
        this.playlistID = playlistID;
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());

        output.put("message", database.addRemoveInPlaylist(getUsername(), playlistID));
        return output;
    }
}
