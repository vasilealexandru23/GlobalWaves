package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import database.MyDatabase;
import musicplayer.MusicPlayer;

public final class RemovePodcastCommand extends Command implements CommandRunner {
    private String namePodcast;

    public RemovePodcastCommand(final String command,
            final String username, final Integer timestamp, final String namePodcast) {
        super(command, username, timestamp);
        this.namePodcast = namePodcast;
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());

        output.put("message", database.removePodcast(getUsername(), namePodcast));
        return output;
    }
}
