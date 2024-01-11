package commands.MusicPlayerCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.Command;
import commands.CommandRunner;
import database.MyDatabase;
import musicplayer.MusicPlayer;
import musicplayer.Playback;
import users.UserNormal;

public final class LoadCommand extends Command implements CommandRunner {
    public LoadCommand(final String command, final String username,
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
            output.put("message", loadTrack(user));
        }

        return output;
    }

    /**
     * Function that loads a track for a user.
     * @param user          the user that loads the track
     * @return              status of command
     */
    private String loadTrack(final UserNormal user) {
        String successLOAD = "Playback loaded successfully.";
        String noSELECT = "Please select a source before attempting to load.";

        /* Check for select. */
        if (user.getLastSelect() == -1) {
            return noSELECT;
        }

        /* Get the musicplayer and playback of user. */
        MusicPlayer musicPlayer = user.getMusicPlayer();
        if (musicPlayer.getPlayback() != null) {
            musicPlayer.getPlayback().checkPlayback();
        }
        musicPlayer.initNewPlayback();
        Playback playback = musicPlayer.getPlayback();

        playback.setCurrTrack(musicPlayer.getSelectedTrack());

        user.setLastSelect(-1);
        user.setLastSearch(null);

        return successLOAD;
    }
}
