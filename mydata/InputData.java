package mydata;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fileio.input.LibraryInput;
import fileio.input.SongInput;
import musicplayer.MusicPlayer;
import musicplayer.Playlist;
import searchbar.Filters;
import searchbar.SearchCommand;
import searchbar.SearchPlaylist;
import searchbar.SearchPodcast;
import searchbar.SearchSong;
import searchbar.SelectCommand;

public final class InputData {
    /* Select command. */
    private SelectCommand selectcmd = null;

    /* Search command. */
    private SearchCommand searchcmd = null;

    private String command;
    private String username;
    private int timestamp;
    private String playlistName;
    private int playlistId;
    private int seed;

    public InputData() {
    }

    public SelectCommand getSelectcmd() {
        return selectcmd;
    }

    public void setSelectcmd(final SelectCommand selectcmd) {
        this.selectcmd = selectcmd;
    }

    public SearchCommand getSearchcmd() {
        return searchcmd;
    }

    public void setSearchcmd(final SearchCommand searchcmd) {
        this.searchcmd = searchcmd;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Function that sets the select parameter.
     * @param itemNumber
     */
    public void setItemNumber(final int itemNumber) {
        if (selectcmd == null) {
            this.selectcmd = new SelectCommand();
        }
        this.selectcmd.setItemNumber(itemNumber);
    }

    /**
     * Function that sets the type of search.
     * @param type
     */
    public void setType(final String type) {
        if (searchcmd == null) {
            this.searchcmd = new SearchCommand();
        }
        this.searchcmd.setType(type);
    }

    /**
     * Function that sets filters.
     * @param filters
     */
    public void setFilters(final Filters filters) {
        if (searchcmd == null) {
            this.searchcmd = new SearchCommand();
        }
        this.searchcmd.setFilters(filters);
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(final String playlistName) {
        this.playlistName = playlistName;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(final int playlistId) {
        this.playlistId = playlistId;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(final int seed) {
        this.seed = seed;
    }

    /**
     * Function that outputs the command data.
     * @param output how to interact with output.
     */
    public void ouputCommand(final ObjectNode output) {
        output.put("command", command);
        if (username != null) {
            output.put("user", username);
        }
        output.put("timestamp", timestamp);
    }

    /**
     * Function that calls helper function for
     * search and returns it's result and command status.
     * @param player                    current player running
     * @param createdPlaylists          all created playlists
     * @param inputPodcasts             all podcasts from library
     * @param library                   library to search songs
     * @param outResults                interact with ouput
     * @return                          status of command
     */
    public String searchTrack(final MusicPlayer player,
            final ArrayList<Playlist> createdPlaylists,
            final ArrayList<PodcastData> inputPodcasts,
            final LibraryInput library, final ArrayNode outResults) {

        /* Unload track of player. */
        player.unloadTrack();

        /* Compute search. */
        Filters reqFilters = this.searchcmd.getFilters();
        SearchCommand mySearch = this.searchcmd.search(this.username,
                createdPlaylists, library.getSongs(),
                inputPodcasts, reqFilters);

        player.setLastSearch(mySearch);

        /* Prepare output. */
        if (mySearch.getType().equals("song")) {
            for (SongInput song : ((SearchSong) mySearch).getResults()) {
                outResults.add(song.getName());
            }
            return "Search returned "
                    + ((SearchSong) mySearch).getResults().size()
                    + " results";
        } else if (mySearch.getType().equals("podcast")) {
            for (PodcastData podcast : ((SearchPodcast) mySearch).getResults()) {
                outResults.add(podcast.getName());
            }
            return "Search returned "
                    + ((SearchPodcast) mySearch).getResults().size()
                    + " results";
        } else {
            for (Playlist playlist : ((SearchPlaylist) mySearch).getResults()) {
                outResults.add(playlist.getName());
            }
            return "Search returned "
                    + ((SearchPlaylist) mySearch).getResults().size()
                    + " results";
        }
    }

    /**
     * Function that checks if we can load an item.
     * @param player current running player.
     * @param output interact with output
     * @return returns if we can proceed
     */
    public static boolean checkLoad(final MusicPlayer player,
            final ObjectNode output) {
        String noSELECT = "Please select a source before attempting to load.";

        /* Check for select */
        if (player.getLastSelect() == -1) {
            output.put("message", noSELECT);
            return false;
        }

        return true;
    }

    /**
     * Function that checks if we can apply shuffle.
     * @param player current player running for a user
     * @param output how to interact with output
     * @return returns if we can proceed shuffle.
     */
    public static boolean checkShuffle(final MusicPlayer player,
            final ObjectNode output) {
        String noLOAD = "Please load a source before using the shuffle function.";
        String noPLAYLIST = "The loaded source is not a playlist.";

        /* Check for load. */
        if (!player.isLoaded()) {
            output.put("message", noLOAD);
            return false;
        }

        /* Check for playlist. */
        if (player.getPlayback().getCurrPlaylist() == null) {
            output.put("message", noPLAYLIST);
            return false;
        }

        return true;
    }

    /**
     * Function that checks if we can apply for/back ward.
     * @param player current player running for a user
     * @param output hwo to interact with output
     * @return returns if we can proceed for/back ward.
     */
    public static boolean checkForBackWard(final MusicPlayer player,
            final ObjectNode output) {
        String noLOAD = "Please load a source before attempting to forward.";
        String noPODCAST = "The loaded source is not a podcast.";

        /* Check for load. */
        if (!player.isLoaded()) {
            output.put("message", noLOAD);
            return false;
        }

        /* Check for podcast. */
        if (player.getPlayback().getCurrPodcast() == null) {
            output.put("message", noPODCAST);
            return false;
        }

        return true;
    }

    /**
     * Function that checks if we can proceed addRemove command.
     * @param player     current player running
     * @param playlistID the playlist to interact with
     * @param output     interact with output
     * @return return if we can proceed command.
     */
    public static boolean checkAddRemove(final MusicPlayer player,
            final int playlistID, final ObjectNode output) {
        String noLOAD = "Please load a source before adding"
                + " to or removing from the playlist.";
        String noPLAYLIST = "The specified playlist does not exist.";
        String noSONG = "The loaded source is not a song.";

        /* Check for load. */
        if (!player.isLoaded()) {
            output.put("message", noLOAD);
            return false;
        }

        /* Check for playlist. */
        if (player.getPlaylists().size() < playlistID) {
            output.put("message", noPLAYLIST);
            return false;
        }

        /* Check if song is loaded. */
        if (player.getSelectedSong() == null) {
            output.put("message", noSONG);
            return false;
        }

        return true;
    }

    /**
     * Function that checks if we can proceed repeat command.
     * @param player current player running
     * @param output interact with output
     * @return return if we can proceed command.
     */
    public static boolean checkRepeat(final MusicPlayer player,
            final ObjectNode output) {
        String noLOAD = "Please load a source before setting the repeat status.";

        /* Check for load. */
        if (!player.isLoaded()) {
            output.put("message", noLOAD);
            return false;
        }

        return true;

    }

    /**
     * Function that checks if we can proceed follow command.
     * @param player current player running
     * @param output interact with output
     * @return return if we can proceed command
     */
    public static boolean checkFollow(final MusicPlayer player,
            final ObjectNode output) {
        String noSELECT = "Please select a source before following or unfollowing.";
        String noPLAYLIST = "The selected source is not a playlist.";

        /* Check by select. */
        if (player.getLastSelect() == -1) {
            output.put("message", noSELECT);
            return false;
        }

        /* Check by playlist. */
        if (player.getSelectedPlaylist() == null) {
            output.put("message", noPLAYLIST);
            return false;
        }

        return true;
    }

    /**
     * Function that checks if we can proceed like command.
     * @param player current player running
     * @param output interact with output
     * @return if we can proceed the command
     */
    public static boolean checkLike(final MusicPlayer player,
            final ObjectNode output) {
        String noLOAD = "Please load a source before liking or unliking.";
        String noSONG = "Loaded source is not a song.";

        /* Check by load. */
        if (!player.isLoaded()) {
            output.put("message", noLOAD);
            return false;
        }

        /* Check by song. */
        if (player.getSelectedPodcast() != null) {
            output.put("message", noSONG);
            return false;
        }

        return true;
    }

    /**
     * Function that checks if we can procees playPause command.
     * @param player current player running
     * @param output interact with output
     * @return if we can proceed the command
     */
    public static boolean checkPlayPause(final MusicPlayer player,
            final ObjectNode output) {
        String noLOAD = "Please load a source before attempting"
                + " to pause or resume playback.";
        if (!player.isLoaded()) {
            output.put("message", noLOAD);
            return false;
        }
        return true;
    }

    /**
     * Function that checks the status of player.
     * @param player       current player running
     * @param statusOutput interact with output
     */
    public static void checkStatus(final MusicPlayer player,
            final ObjectNode statusOutput) {
        player.checkStatus();
        statusOutput.put("name", player.getPlayback().getTrack());
        statusOutput.put("remainedTime", player.getPlayback().getTimeRemained());
        statusOutput.put("repeat", player.getPlayback().getRepeatstatus());
        statusOutput.put("shuffle", player.getPlayback().isShuffle());
        statusOutput.put("paused", !player.getPlayback().isPlayPause());
    }
}
