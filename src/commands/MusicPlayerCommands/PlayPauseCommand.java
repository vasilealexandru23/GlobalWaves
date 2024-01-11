package commands.MusicPlayerCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.Command;
import commands.CommandRunner;
import database.MyDatabase;
import musicplayer.MusicPlayer;
import users.UserNormal;

public final class PlayPauseCommand extends Command implements CommandRunner {
    public PlayPauseCommand(final String command, final String username,
            final Integer timestamp) {
        super(command, username, timestamp);
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        /* Guaranteed that we will always have a normal user. */
        UserNormal user = ((UserNormal) database.findMyUser(getUsername()));

        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());

         /* Check if user is online. */
         if (!user.isOnline()) {
            output.put("message", getUsername() + " is offline.");
        } else {
            output.put("message", playPause(user));
        }

        return output;
    }

    /**
     * Function that plays or pauses the playback.
     * @param user          the user that request to play or pause
     * @return              status of command
     */
    private String playPause(final UserNormal user) {
        String noLOAD = "Please load a source before attempting"
                + " to pause or resume playback.";

        /* Get the musicplayer and check it's status. */
        MusicPlayer player = user.getMusicPlayer();
        player.checkStatus();

        if (player.getPlayback().getCurrTrack() == null) {
            return noLOAD;
        }

        return player.getPlayback().playPause();
    }
}
