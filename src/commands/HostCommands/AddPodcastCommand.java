package commands.HostCommands;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.Command;
import commands.CommandRunner;
import database.Episode;
import database.MyDatabase;
import musicplayer.MusicPlayer;

public final class AddPodcastCommand extends Command implements CommandRunner {
    private String namePodcast;
    private ArrayList<Episode> episodes;

    public AddPodcastCommand(final String command, final String username, final Integer timestamp,
            final String namePodcast, final ArrayList<Episode> episodes) {
        super(command, username, timestamp);
        this.namePodcast = namePodcast;
        this.episodes = episodes;
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());

        output.put("message", database.addPodcast(getUsername(), namePodcast, episodes));
        return output;
    }
}
