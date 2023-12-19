package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import database.MyDatabase;
import musicplayer.MusicPlayer;
import musicplayer.AudioCollection.AudioType;
import users.UserArtist;
import users.UserHost;
import users.UserNormal;
import searchbar.SearchArtist;
import searchbar.SearchHost;

public final class SelectCommand extends Command implements CommandRunner {
    private Integer itemNumber;

    public SelectCommand(final String command, final String username,
            final Integer timestamp, final Integer itemNumber) {
        super(command, username, timestamp);
        this.itemNumber = itemNumber - 1;
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        /* Garanteed that we will always have a normal user. */
        UserNormal user = ((UserNormal) database.findMyUser(getUsername()));

        if (user == null) {
            output.put("message", "User not found.");
            return output;
        }

        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());

        /* Check if user is online. */
        if (!user.isOnline()) {
            output.put("message", getUsername() + " is offline.");
        } else {
            output.put("message", selectTrack(user));
        }

        return output;
    }

    /**
     * Function that selects a track for a user by last search.
     * @param user              the user that selects the track
     * @return                  status of command
     */
    private String selectTrack(final UserNormal user) {
        String noSEARCH = "Please conduct a search before making a selection.";
        String badID = "The selected ID is too high.";
        String successSELECT = "Successfully selected ";

        if (user.getLastSearch() == null) {
            return noSEARCH;
        }

        /* Check for selection of host or artist. */
        switch (user.getLastSearch().getType()) {
            case "artist" -> {
                UserArtist artist = ((SearchArtist) user.getLastSearch()).getArtist(itemNumber);
                if (artist == null) {
                    return badID;
                }

                user.setSelectedPage(artist.getArtistPage());
                user.setLastSelect(itemNumber);

                return "Successfully selected " + artist.getUsername() + "'s page.";
            } case "host" -> {
                UserHost host = ((SearchHost) user.getLastSearch()).getHost(itemNumber);
                if (host == null) {
                    return badID;
                }

                user.setSelectedPage(host.getHostPage());
                user.setLastSelect(itemNumber);

                return "Successfully selected " + host.getUsername() + "'s page.";
            } default -> {

            }
        }

        String track = null;
        user.getMusicPlayer().setSelectedTrack(null);

        user.getMusicPlayer().setSelectedTrack(user.getLastSearch().getSelect(itemNumber));

        if (user.getMusicPlayer().getSelectedTrack() == null) {
            return badID;
        }

        if (user.getLastSearch().getType().equals("song")) {
            user.getMusicPlayer().getSelectedTrack().setType(AudioType.SONG);
        } else if (user.getLastSearch().getType().equals("podcast")) {
            user.getMusicPlayer().getSelectedTrack().setType(AudioType.PODCAST);
        } else if (user.getLastSearch().getType().equals("playlist")) {
            user.getMusicPlayer().getSelectedTrack().setType(AudioType.PLAYLIST);
        } else if (user.getLastSearch().getType().equals("album")) {
            user.getMusicPlayer().getSelectedTrack().setType(AudioType.ALBUM);
        } else {
            return "Error: Unknown type.";
        }

        track = user.getMusicPlayer().getSelectedTrack().getName();

        user.setLastSelect(itemNumber);
        return successSELECT + track + ".";
    }
}
