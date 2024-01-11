package commands.MusicPlayerCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.Command;
import commands.CommandRunner;
import database.MyDatabase;
import musicplayer.MusicPlayer;
import musicplayer.Playback;
import users.UserNormal;
import pages.HomePage;

public final class LoadRecommendationsCommand extends Command implements CommandRunner {
    public LoadRecommendationsCommand(final String command, final String username,
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
        /* Check for recommendations. */
        HomePage homePage = (HomePage) user.getPages().get(0);
        if (homePage.getRecommendations().size() == 0) {
            return "No recommendations available.";
        }

        /* Get the musicplayer and playback of user. */
        MusicPlayer musicPlayer = user.getMusicPlayer();
        if (musicPlayer.getPlayback() != null) {
            musicPlayer.getPlayback().checkPlayback();
        }
        musicPlayer.initNewPlayback();
        Playback playback = musicPlayer.getPlayback();

        playback.setCurrTrack(homePage.getRecommendations().get(
                homePage.getRecommendations().size() - 1));
        return "Playback loaded successfully.";
    }
}
