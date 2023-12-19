package commands;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import database.MyDatabase;
import database.Song;
import musicplayer.MusicPlayer;

public final class AddAlbumCommand extends Command implements CommandRunner {
    private String albumName;
    private Integer releaseYear;
    private String description;
    private ArrayList<Song> songs;

    public AddAlbumCommand(final String command, final String username,
            final Integer timestamp, final String albumName,
            final Integer releaseYear, final String description,
            final ArrayList<Song> songs) {
        super(command, username, timestamp);
        this.albumName = albumName;
        this.releaseYear = releaseYear;
        this.description = description;
        this.songs = songs;
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());

        output.put("message", database.addAlbum(getUsername(),
                albumName, releaseYear, description, songs));
        return output;
    }
}
