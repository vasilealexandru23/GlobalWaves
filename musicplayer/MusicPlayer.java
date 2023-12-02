package musicplayer;

import lombok.Getter;
import musicplayer.AudioCollection.AudioType;
import mydata.Episode;
import mydata.Playlist;
import mydata.Podcast;
import mydata.Song;
import searchbar.SearchCommand;
import searchbar.SearchPlaylist;
import searchbar.SearchPodcast;
import searchbar.SearchSong;

import java.util.ArrayList;
import java.util.Comparator;

import com.fasterxml.jackson.databind.node.ArrayNode;

import fileio.input.LibraryInput;
import fileio.input.UserInput;

public final class MusicPlayer {
    @Getter
    private String username;
    @Getter
    private SearchCommand lastSearch;
    @Getter
    private int lastSelect;
    @Getter
    private boolean loaded;

    @Getter
    private AudioCollection selectedTrack;

    @Getter
    private static int timestamp;
    @Getter
    private Playback playback;
    @Getter
    private ArrayList<Playlist> playlists = new ArrayList<>();
    @Getter
    private ArrayList<Song> likedSongs = new ArrayList<>();
    @Getter
    private ArrayList<Podcast> podcasts = new ArrayList<>();
    @Getter
    private ArrayList<Playlist> followedPlaylists = new ArrayList<>();

    public MusicPlayer(final String username) {
        this.username = username;
        this.lastSearch = null;
        this.lastSelect = -1;
        this.loaded = false;
        MusicPlayer.timestamp = 0;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setLastSearch(final SearchCommand lastSearch) {
        this.lastSearch = lastSearch;
    }

    public void setLastSelect(final int lastSelect) {
        this.lastSelect = lastSelect;
    }

    public void setLoaded(final boolean loaded) {
        this.loaded = loaded;
    }

    public void setSelectedTrack(final AudioCollection selectedTrack) {
        this.selectedTrack = selectedTrack;
    }

    public static void setTimestamp(final int timestamp) {
        MusicPlayer.timestamp = timestamp;
    }

    public void setPlayback(final Playback playback) {
        this.playback = playback;
    }

    public void setPlaylists(final ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    public void setLikedSongs(final ArrayList<Song> likedSongs) {
        this.likedSongs = likedSongs;
    }

    public void setPodcasts(final ArrayList<Podcast> podcasts) {
        this.podcasts = podcasts;
    }

    public void setFollowedPlaylists(final ArrayList<Playlist> followedPlaylists) {
        this.followedPlaylists = followedPlaylists;
    }

    /**
     * Function that finds for a username, it's player.
     * @param players           all players created.
     * @param username          given username
     * @return                  the coresponding player
     */
    public static MusicPlayer findMyPlayer(final ArrayList<MusicPlayer> players,
            final String username) {
        for (MusicPlayer player : players) {
            if (player.getUsername().equals(username)) {
                player.checkStatus();
                return player;
            }
        }

        return null;
    }

    /*
     * Function that sets selections on null.
     */
    private void clearSelection() {
        this.selectedTrack = null;
        this.lastSelect = -1;
    }

    /**
     * Function that unloads a track from player.
     */
    public void unloadTrack() {
        if (this.isLoaded()) {
            if (this.getPlayback().getCurrTrack().getType() == AudioCollection.AudioType.PODCAST) {
                ((Podcast) this.getPlayback().getCurrTrack()).checkTrack(this.getPlayback());
            }
            this.initNewPlayback();
            this.getPlayback().setPlayPause(false);
            this.setLoaded(false);
        }
    }

    /**
     * Function that selects a track by last search.
     * @param mySelect      the index in search result
     * @return              command status
     */
    public String selectTrack(final int mySelect) {
        String noSEARCH = "Please conduct a search before making a selection.";
        String badID = "The selected ID is too high.";
        String successSELECT = "Successfully selected ";
        /* Check for search. */
        if (this.lastSearch == null) {
            return noSEARCH;
        }

        this.clearSelection();

        String track = null;
        if (this.lastSearch.getType().equals("song")) {
            if (((SearchSong) lastSearch).getResults().size() <= mySelect) {
                return badID;
            }

            this.selectedTrack = ((SearchSong) lastSearch).getResults().get(mySelect);
            selectedTrack.setType(AudioCollection.AudioType.SONG);
            track = selectedTrack.getName();
        } else if (this.lastSearch.getType().equals("podcast")) {
            if (((SearchPodcast) lastSearch).getResults().size() <= mySelect) {
                return badID;
            }

            this.selectedTrack = ((SearchPodcast) lastSearch).getResults().get(mySelect);
            selectedTrack.setType(AudioCollection.AudioType.PODCAST);
            track = selectedTrack.getName();
        } else {
            if (((SearchPlaylist) lastSearch).getResults().size() <= mySelect) {
                return badID;
            }

            this.selectedTrack = ((SearchPlaylist) lastSearch).getResults().get(mySelect);
            selectedTrack.setType(AudioCollection.AudioType.PLAYLIST);
            track = selectedTrack.getName();
        }

        this.lastSelect = mySelect;
        return successSELECT + track + ".";
    }

    /**
     * Function that creates for each user a music player.
     * @param players list to keep all the music players.
     * @param library data to initialize music players.
     */
    public static void initPlayers(final ArrayList<MusicPlayer> players,
            final LibraryInput library, final ArrayList<Podcast> podcasts) {

        for (UserInput user : library.getUsers()) {
            MusicPlayer newplayer = new MusicPlayer(user.getUsername());
            for (Podcast podcast : podcasts) {
                /* Set current episode to first one. */
                podcast.setIndexEpisode(0);

                for (Episode episode : podcast.getEpisodes()) {
                    /* Set time watched to 0. */
                    episode.setTimeWatched(0);
                }

                newplayer.podcasts.add(podcast);
            }
            players.add(newplayer);
        }
    }

    /**
     * This function creates a new playback and sets its params.
     */
    public void initNewPlayback() {
        this.playback = new Playback();
        this.playback.setTimeWatched(0);
        this.playback.setPlayPause(true);
        this.playback.setLastInteracted(timestamp);
    }

    /**
     * Adds a new playlist to the user's list of playlists.
     * @param playlist      given playlist to add
     */
    public void addPlaylist(final Playlist playlist) {
        this.playlists.add(playlist);
    }

    /**
     * Check if given song is liked by user.
     * @param song      given song
     * @return          return if song is liked
     */
    public boolean islikedSong(final Song song) {
        return likedSongs.contains(song);
    }

    /**
     * Adds a given song to liked songs collection.
     * @param song      given song to add
     */
    public void likeSong(final Song song) {
        likedSongs.add(song);
    }

    /**
     * Remove a given song from liked songs colletction.
     * @param song      given song to remove
     */
    public void unlikeSong(final Song song) {
        likedSongs.remove(song);
    }

    /**
     * Function that checks if we have audiofile loaded.
     */
    public void checkStatus() {
        if (playback != null && !playback.checkPlayback()) {
            loaded = false;
        }
    }

    /**
     * Returns the names of the liked songs.
     * @return      ArrayList<String>
     */
    public ArrayList<String> likedSongNames() {
        ArrayList<String> likedSongNames = new ArrayList<>();
        for (Song song : likedSongs) {
            likedSongNames.add(song.getName());
        }

        return likedSongNames;
    }

    /**
     * Function that mark a song as liked or unliked.
     * @return      status of command
     */
    public String likeSong() {
        String okLIKED = "Like registered successfully.";
        String okUNLKED = "Unlike registered successfully.";

        Song currSong;

        if (selectedTrack.getType() == AudioType.SONG) {
            currSong = (Song) selectedTrack;
        } else {
            currSong = ((Playlist) (this.getPlayback().getCurrTrack())).currPlayingSong(playback);
        }
        if (this.islikedSong(currSong)) {
            currSong.setNrLikes(currSong.getNrLikes() - 1);
            this.unlikeSong(currSong);
            return okUNLKED;
        } else {
            currSong.setNrLikes(currSong.getNrLikes() + 1);
            this.likeSong(currSong);
            return okLIKED;
        }
    }

    /**
     * Function that loads into the player.
     * @return      the success message
     */
    public String loadItem() {
        String successLOAD = "Playback loaded successfully.";
        this.loaded = true;
        this.initNewPlayback();

        this.getPlayback().setCurrTrack(selectedTrack);
        if (selectedTrack.getType() == AudioType.PODCAST) {
            /* Restore data. */
            playback.setTimeWatched(((Podcast) selectedTrack).getCurrEpisode().getTimeWatched());
        } else {
            /* Restore data. */
            playback.setIndexSong(0);
        }

        this.lastSelect = -1;
        this.lastSearch = null;

        return successLOAD;
    }

    /**
     * Function that switch visibility for a given playlist (ID).
     * @param playlistID        playlist ID to find the playlist
     * @return                  status of command
     */
    public String switchVisibility(final int playlistID) {
        final String badID = "The specified playlist ID is too high.";
        final String toPRIVATE = "Visibility status updated successfully to private.";
        final String toPUBLIC = "Visibility status updated successfully to public.";
        if (playlistID > this.playlists.size()) {
            return badID;
        }

        Playlist myplaylist = this.playlists.get(playlistID - 1);
        myplaylist.setVisibility(!myplaylist.isVisibility());

        if (!myplaylist.isVisibility()) {
            return toPRIVATE;
        } else {
            return toPUBLIC;
        }
    }

    /**
     * Function that goes to next track.
     * @return      command status
     */
    public String nextTrack() {
        String noLOAD = "Please load a source before skipping to the next track.";
        String successNEXT = "Skipped to next track successfully. The current track is ";

        /* Check for load. */
        if (!this.isLoaded()) {
            return noLOAD;
        }

        /* Play the next track. */
        getPlayback().getCurrTrack().nextTrack(playback);

        /* Check if their is something still running. */
        if (getPlayback().getCurrTrack() == null) {
            return noLOAD;
        } else {
            this.getPlayback().setPlayPause(true);
            return successNEXT + this.getPlayback().getCurrTrack().getTrack(playback) + ".";
        }

    }

    /**
     * Function that goes to previous track.
     * @return      command status
     */
    public String prevTrack() {
        String noLOAD = "Please load a source before returning to the previous track.";
        String successPREV = "Returned to previous track successfully. The current track is ";

        /* Check for load. */
        if (!this.isLoaded()) {
            return noLOAD;
        }

        /* Play previous track. */
        getPlayback().getCurrTrack().prevTrack(playback);

        /* Check if their is something still running. */
        if (getPlayback().getCurrTrack() == null) {
            return noLOAD;
        } else {
            this.getPlayback().setPlayPause(true);
            return successPREV + this.getPlayback().getCurrTrack().getTrack(playback) + ".";
        }
    }

    /**
     * Function that gets top 5 songs by number of likes.
     * @param songs         where to get our songs
     * @param players       all user's players
     * @param outResults    interact with output
     */
    public static void getTop5Songs(final ArrayList<Song> songs,
            final ArrayList<MusicPlayer> players,
            final ArrayNode outResults) {
        final int maxTop = 5;
        /* Create an aux vector of with songs. */
        ArrayList<Song> auxSongs = new ArrayList<>();

        /* Iterate over all songs and add the to our array. */
        for (Song song : songs) {
            auxSongs.add(song);
        }

        /* Sorting by likes comparator. */
        Comparator<Song> songInputComparator = new Comparator<Song>() {
            @Override
            public int compare(final Song stSong, final Song ndSong) {
                return ndSong.getNrLikes() - stSong.getNrLikes();
            }
        };

        /* Sort the arraylist of songs. */
        auxSongs.sort(songInputComparator);

        for (int iter = 0; iter < maxTop; ++iter) {
            outResults.add(auxSongs.get(iter).getName());
        }
    }
}
