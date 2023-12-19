package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import database.MyDatabase;
import musicplayer.MusicPlayer;

public final class ShowPodcastCommand extends Command implements CommandRunner {
    public ShowPodcastCommand(final String command, final String username,
            final Integer timestamp) {
        super(command, username, timestamp);
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());

        output.put("result", database.showPodcasts(getUsername()));
        return output;
    }
}
