package commands.ArtistCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.Command;
import commands.CommandRunner;
import database.MyDatabase;
import musicplayer.MusicPlayer;

public final class AddEventCommand extends Command implements CommandRunner {
    private String date;
    private String nameEvent;
    private String description;

    public AddEventCommand(final String command, final String username,
            final Integer timestamp, final String date, final String nameEvent,
            final String description) {
        super(command, username, timestamp);
        this.date = date;
        this.nameEvent = nameEvent;
        this.description = description;
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());

        output.put("message", database.addEvent(getUsername(), date, nameEvent, description));
        return output;
    }
}
