package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import database.MyDatabase;
import musicplayer.MusicPlayer;

public final class RemoveEventCommand extends Command implements CommandRunner {
    private String nameEvent;

    public RemoveEventCommand(final String command, final String username,
            final Integer timestamp, final String nameEvent) {
        super(command, username, timestamp);
        this.nameEvent = nameEvent;
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());

        output.put("message", database.removeEvent(getUsername(), nameEvent));
        return output;
    }
}
