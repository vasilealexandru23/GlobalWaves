package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import database.MyDatabase;
import musicplayer.MusicPlayer;

public final class ChangePageCommand extends Command implements CommandRunner {
    private String nextPage;

    public ChangePageCommand(final String command, final String username, final Integer timestamp,
            final String nextPage) {
        super(command, username, timestamp);
        this.nextPage = nextPage;
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());

        output.put("message", database.changePage(getUsername(), nextPage));
        return output;
    }
}
