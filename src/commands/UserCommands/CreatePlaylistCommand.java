package commands.UserCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.Command;
import commands.CommandRunner;
import database.MyDatabase;
import database.Playlist;
import musicplayer.MusicPlayer;
import users.UserNormal;

public final class CreatePlaylistCommand extends Command implements CommandRunner {
    private String playlistName;

    public CreatePlaylistCommand(final String command, final String username,
            final Integer timestamp, final String playlistName) {
        super(command, username, timestamp);
        this.playlistName = playlistName;
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        /* Garanteed that we will always have a normal user. */
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
            output.put("message", createPlaylist(user, database));
        }

        return output;
    }

    /**
     * Function that creates a playlist for a user.
     * @param user              the user that creates the playlist
     * @param database          the database of the application
     * @return                  status of command
     */
    private String createPlaylist(final UserNormal user, final MyDatabase database) {
        String alreadyCREATED = "A playlist with the same name already exists.";
        String successCREATED = "Playlist created successfully.";

        /* Check for name. */
        if (Playlist.sameName(database.getAllPlaylistsCreated(), playlistName)) {
            return alreadyCREATED;
        }

        /* Create new playlist. */
        Playlist newPlaylist = new Playlist(playlistName, getUsername());
        database.getAllPlaylistsCreated().add(newPlaylist);
        user.getMusicPlayer().addPlaylist(newPlaylist);

        return successCREATED;
    }
}
