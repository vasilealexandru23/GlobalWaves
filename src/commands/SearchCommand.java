package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import database.Album;
import database.MyDatabase;
import database.Playlist;
import database.Podcast;
import database.Song;
import musicplayer.MusicPlayer;
import searchbar.Filters;
import searchbar.Search;
import searchbar.SearchAlbum;
import searchbar.SearchArtist;
import searchbar.SearchHost;
import searchbar.SearchPlaylist;
import searchbar.SearchPodcast;
import searchbar.SearchSong;
import users.UserArtist;
import users.UserHost;
import users.UserNormal;

public final class SearchCommand extends Command implements CommandRunner {
    private String searchType;
    private Filters filters;
    private Search searchcmd;

    public SearchCommand(final String command, final String username, final Search searchcmd,
            final Integer timestamp, final String searchType, final Filters filters) {
        super(command, username, timestamp);
        this.searchType = searchType;
        this.filters = filters;
        this.searchcmd = searchcmd;
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        /* Garanteed that we will always have a normal user. */
        UserNormal user = ((UserNormal) database.findMyUser(getUsername()));
        MusicPlayer.setTimestamp(getTimestamp());

        if (user == null) {
            output.put("message", "User not found.");
            return output;
        }

        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());

        /* Check if user is online. */
        if (!user.isOnline()) {
            output.put("message", getUsername() + " is offline.");
            output.put("results", new ObjectMapper().createArrayNode());
        } else {
            searchTrack(user.getMusicPlayer(), database, output);
        }

        return output;
    }

    private void searchTrack(final MusicPlayer player,
            final MyDatabase database, final ObjectNode output) {

        /* Unload track of player. */
        if (player.getPlayback() != null) {
            player.getPlayback().checkPlayback();
            player.unloadTrack();
        }

        ArrayNode outputArray = new ObjectMapper().createArrayNode();

        /* Prepare output. */
        if (searchType.equals("song")) {
            ((SearchSong) searchcmd).searchTrack(database.getAllSongsCreated(), filters);
            player.getUser().setLastSearch(searchcmd);
            player.getUser().getLastSearch().setType("song");

            for (Song song : ((SearchSong) searchcmd).getResults()) {
                outputArray.add(song.getName());
            }

            output.put("message", "Search returned "
                    + ((SearchSong) searchcmd).getResults().size()
                    + " results");
        } else if (searchType.equals("podcast")) {
            ((SearchPodcast) searchcmd).searchTrack(player.getPodcasts(), filters);
            player.getUser().setLastSearch(searchcmd);
            player.getUser().getLastSearch().setType("podcast");

            for (Podcast podcast : ((SearchPodcast) searchcmd).getResults()) {
                outputArray.add(podcast.getName());
            }

            output.put("message", "Search returned "
                    + ((SearchPodcast) searchcmd).getResults().size()
                    + " results");
        } else if (searchType.equals("playlist")) {
            ((SearchPlaylist) searchcmd).searchTrack(getUsername(),
                    database.getAllPlaylistsCreated(), filters);
            player.getUser().setLastSearch(searchcmd);
            player.getUser().getLastSearch().setType("playlist");

            for (Playlist playlist : ((SearchPlaylist) searchcmd).getResults()) {
                outputArray.add(playlist.getName());
            }

            output.put("message", "Search returned "
                    + ((SearchPlaylist) searchcmd).getResults().size()
                    + " results");
        } else if (searchType.equals("artist")) {
            ((SearchArtist) searchcmd).searchArtist(database.getAllUsersCreated(), filters);
            player.getUser().setLastSearch(searchcmd);
            player.getUser().getLastSearch().setType("artist");

            for (UserArtist artist : ((SearchArtist) searchcmd).getResults()) {
                outputArray.add(artist.getUsername());
            }

            output.put("message", "Search returned "
                    + ((SearchArtist) searchcmd).getResults().size()
                    + " results");
        } else if (searchType.equals("host")) {
            ((SearchHost) searchcmd).searchHost(database.getAllUsersCreated(), filters);
            player.getUser().setLastSearch(searchcmd);
            player.getUser().getLastSearch().setType("host");

            for (UserHost host : ((SearchHost) searchcmd).getResults()) {
                outputArray.add(host.getUsername());
            }

            output.put("message", "Search returned "
                    + ((SearchHost) searchcmd).getResults().size()
                    + " results");
        } else {
            ((SearchAlbum) searchcmd).searchTrack(database.getAllAlbumsCreated(), filters);
            player.getUser().setLastSearch(searchcmd);
            player.getUser().getLastSearch().setType("album");

            for (Album album : ((SearchAlbum) searchcmd).getResults()) {
                outputArray.add(album.getName());
            }

            output.put("message", "Search returned "
                    + ((SearchAlbum) searchcmd).getResults().size()
                    + " results");
        }
        output.put("results", outputArray);
    }
}
