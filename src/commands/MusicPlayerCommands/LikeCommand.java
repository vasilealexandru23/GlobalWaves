package commands.MusicPlayerCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.Command;
import commands.CommandRunner;
import database.MyDatabase;
import musicplayer.AudioCollection;
import musicplayer.MusicPlayer;
import users.UserNormal;

public final class LikeCommand extends Command implements CommandRunner {

    public LikeCommand(final String command, final String username,
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

        if (user == null) {
            output.put("message", "User not found.");
            return output;
        }

        /* Check if user is online. */
        if (!user.isOnline()) {
            output.put("message", getUsername() + " is offline.");
        } else {
            output.put("message", likeSong(user));
        }

        return output;
    }

    /**
     * Function that likes a song for a user.
     * @param user        the user that likes the song
     * @return            status of command
     */
    private String likeSong(final UserNormal user) {
        String noLOAD = "Please load a source before liking or unliking.";
        String noSONG = "Loaded source is not a song.";

        MusicPlayer player = user.getMusicPlayer();
        player.getPlayback().checkPlayback();

        /* Check by load. */
        if (player.getPlayback().getCurrTrack() == null) {
            return noLOAD;
        }

        /* Check by song. */
        if (player.getSelectedTrack().getType() == AudioCollection.AudioType.PODCAST) {
            return noSONG;
        }

        /* Like or unlike. */
        return player.likeSong();
    }
}
