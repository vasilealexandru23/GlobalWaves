package musicplayer;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fileio.input.SongInput;

public final class Playlist {
    @Getter
    private ArrayList<SongInput> songs = new ArrayList<>();
    @Getter
    private String name;
    @Getter
    private String owner;
    @Getter
    private int followers;
    @Getter
    private boolean visibility; /* true->public, false->private */

    public Playlist(final String name, final String owner) {
        this.name = name;
        this.owner = owner;
        this.followers = 0;
        this.visibility = true;
    }

    public void setSongs(final ArrayList<SongInput> songs) {
        this.songs = songs;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public void setFollowers(final int followers) {
        this.followers = followers;
    }

    public void setVisibility(final boolean visibility) {
        this.visibility = visibility;
    }

    /**
     * Check if song is in playlist.
     * @param song given song to check
     * @return boolean
     */
    public boolean songExists(final SongInput song) {
        return songs.contains(song);
    }

    /**
     * Remove given song from playlist.
     * @param song given song to remove
     */
    public void songRemove(final SongInput song) {
        songs.remove(song);
    }

    /**
     * Add given song to playlist.
     * @param song given song to add
     */
    public void songAdd(final SongInput song) {
        this.songs.add(song);
    }

    /**
     * Returns a list of song names in given playlist.
     * @return an ArrayList of strings.
     */
    public ArrayList<String> getSongNames() {
        ArrayList<String> songNames = new ArrayList<>();

        for (SongInput song : songs) {
            songNames.add(song.getName());
        }

        return songNames;
    }

    /**
     * Returns true or false if the list of playlists contains the song name.
     * @param playlists a list of playlists
     * @param songname  the name of a song
     * @return boolean
     */
    public static boolean sameName(final ArrayList<Playlist> playlists,
            final String songname) {
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(songname)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Function that prints the state of a playlist.
     * @param playlist given playlist to check
     * @return String ("public" or "private")
     */
    public static String checkVisibility(final Playlist playlist) {
        if (playlist.isVisibility()) {
            return "public";
        } else {
            return "private";
        }
    }

    /**
     * Function that shows all playlists of a user.
     * @param userPlaylists user's playlists as ArrayList
     * @param output        how to interact with output
     */
    public static void showPlaylists(final ArrayList<Playlist> userPlaylists,
            final ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();
        for (Playlist playlist : userPlaylists) {
            ObjectNode showPlaylist = objectMapper.createObjectNode();
            ArrayList<String> songNames = playlist.getSongNames();
            ArrayNode songsOut = objectMapper.createArrayNode();

            for (String song: songNames) {
                songsOut.add(song);
            }

            showPlaylist.put("name", playlist.name);
            showPlaylist.put("songs", songsOut);
            showPlaylist.put("visibility", checkVisibility(playlist));
            showPlaylist.put("followers", playlist.followers);

            output.add(showPlaylist);
        }
    }

    /**
     * Function that gets TOP 5 followed playlists.
     * @param allPlaylists list with all created playlists.
     * @param output       how to interact with output
     */
    public static void getTop5Playlists(final ArrayList<Playlist> allPlaylists,
            final ArrayNode output) {
        final int top5 = 5;
        ArrayList<Playlist> publicPlaylists = new ArrayList<>();

        for (Playlist playlist : allPlaylists) {
            if (playlist.visibility) {
                publicPlaylists.add(playlist);
            }
        }

        Comparator<Playlist> playListComparator = new Comparator<Playlist>() {
            @Override
                public int compare(final Playlist stSong, final Playlist ndSong) {
                    return ndSong.getFollowers() - stSong.getFollowers();
                }
        };

        /* Sort the list of public playlists by followers. */
        publicPlaylists.sort(playListComparator);

        for (Playlist playlist : publicPlaylists) {
            output.add(playlist.name);
            if (output.size() == top5) {
                break;
            }
        }
    }

    /**
     * Function that creates a new playlist for a user.
     * @param createdPlaylists list with all playlists created
     * @param name             name of the playlist
     * @param player           current running player
     * @param output           interact with output
     */
    public static void createPlaylist(final ArrayList<Playlist> createdPlaylists,
            final String name, final MusicPlayer player, final ObjectNode output) {
        String alreadyCREATED = "A playlist with the same name already exists.";
        String successCREATED = "Playlist created successfully.";

        /* Check for name. */
        if (sameName(createdPlaylists, name)) {
            output.put("message", alreadyCREATED);
            return;
        }

        /* Create new playlist. */
        Playlist newPlaylist = new Playlist(name, player.getUsername());
        createdPlaylists.add(newPlaylist);
        player.addPlaylist(newPlaylist);

        output.put("message", successCREATED);
    }

    /**
     * Function that adds or removes a song from playlist.
     * @param playlist playlist where we add or remove song
     * @param song     the song to be added or removed
     * @return status of command
     */
    public static String addRemoveInPlaylist(final Playlist playlist,
            final SongInput song) {
        String successADD = "Successfully added to playlist.";
        String successREMOVE = "Successfully removed from playlist.";

        if (playlist.songExists(song)) {
            playlist.songRemove(song);
            return successREMOVE;
        } else {
            playlist.songAdd(song);
            return successADD;
        }
    }

    /**
     * Function that follows or unfollows a playlist.
     * @param player current player running
     * @return status of command
     */
    public String followPlaylist(final MusicPlayer player) {
        String isOWNED = "You cannot follow or unfollow your own playlist.";
        String successFOLLOWED = "Playlist followed successfully.";
        String successUNFOLLOWED = "Playlist unfollowed successfully.";

        /* Check if playlist if owned by user. */
        if (player.getPlaylists().contains(this)) {
            return isOWNED;
        }

        if (player.getFollowedPlaylists().contains(this)) {
            player.getFollowedPlaylists().remove(this);
            this.followers--;
            return successUNFOLLOWED;
        } else {
            player.getFollowedPlaylists().add(this);
            this.followers++;
            return successFOLLOWED;
        }
    }
}
