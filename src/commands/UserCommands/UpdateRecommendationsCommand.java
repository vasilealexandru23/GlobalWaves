package commands.UserCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.Command;
import commands.CommandRunner;
import database.MyDatabase;
import musicplayer.MusicPlayer;

public final class UpdateRecommendationsCommand extends Command implements CommandRunner {
    private String recommendationType;
    public UpdateRecommendationsCommand(final String command, final String username,
            final Integer timestamp, final String recommendationType) {
        super(command, username, timestamp);
        this.recommendationType = recommendationType;
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());

        output.put("message", database.updateRecommendations(getUsername(), recommendationType));

        return output;
    }
}
