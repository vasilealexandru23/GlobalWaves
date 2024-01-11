package commands.Statistics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.Command;
import commands.CommandRunner;
import database.MyDatabase;
import musicplayer.MusicPlayer;
import users.UserTypes.UserType;

public final class WrappedCommand extends Command implements CommandRunner {
    public WrappedCommand(final String command,
            final String username, final Integer timestamp) {
        super(command, username, timestamp);
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();
        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());
        ObjectNode stats = database.getStatistics(getUsername());
        if (stats == null) {
            if (database.findMyUser(getUsername()).getUserType() == UserType.USER) {
                output.put("message", "No data to show for user " + getUsername() + ".");
            } else if (database.findMyUser(getUsername()).getUserType() == UserType.ARTIST) {
                output.put("message", "No data to show for artist " + getUsername() + ".");
            }
        } else {
            output.put("result", database.getStatistics(getUsername()));
        }
        return output;
    }
}
