package musicplayer;

import lombok.Getter;
import mydata.EpisodeData;
import mydata.PodcastData;
import searchbar.SearchCommand;
import searchbar.SearchPlaylist;
import searchbar.SearchPodcast;
import searchbar.SearchSong;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ArrayNode;

import fileio.input.LibraryInput;
import fileio.input.SongInput;
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
    private Playlist selectedPlaylist;
    @Getter
    private SongInput selectedSong;
    @Getter
    private PodcastData selectedPodcast;
    @Getter
    private static int timestamp;
    @Getter
    private Playback playback;
    @Getter
    private ArrayList<Playlist> playlists = new ArrayList<>();
    @Getter
    private ArrayList<SongInput> likedSongs = new ArrayList<>();
    @Getter
    private ArrayList<PodcastData> podcasts = new ArrayList<>();
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

    public void setSelectedPlaylist(final Playlist selectedPlaylist) {
        this.selectedPlaylist = selectedPlaylist;
    }

    public void setSelectedSong(final SongInput selectedSong) {
        this.selectedSong = selectedSong;
    }

    public void setSelectedPodcast(final PodcastData selectedPodcast) {
        this.selectedPodcast = selectedPodcast;
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

    public void setLikedSongs(final ArrayList<SongInput> likedSongs) {
        this.likedSongs = likedSongs;
    }

    public void setPodcasts(final ArrayList<PodcastData> podcasts) {
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
        this.selectedSong = null;
        this.selectedPodcast = null;
        this.selectedPlaylist = null;
        this.lastSelect = -1;
    }

    /**
     * Function that unloads a track from player.
     */
    public void unloadTrack() {
        if (this.isLoaded()) {
            this.getPlayback().checkCurrentEpisode();
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
            this.selectedSong = ((SearchSong) lastSearch).getResults().get(mySelect);
            track = selectedSong.getName();
        } else if (this.lastSearch.getType().equals("podcast")) {
            if (((SearchPodcast) lastSearch).getResults().size() <= mySelect) {
                return badID;
            }
            this.selectedPodcast = ((SearchPodcast) lastSearch).getResults().get(mySelect);
            track = selectedPodcast.getName();
        } else {
            if (((SearchPlaylist) lastSearch).getResults().size() <= mySelect) {
                return badID;
            }
            this.selectedPlaylist = ((SearchPlaylist) lastSearch).getResults().get(mySelect);
            track = selectedPlaylist.getName();
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
            final LibraryInput library, final ArrayList<PodcastData> podcasts) {

        for (UserInput user : library.getUsers()) {
            MusicPlayer newplayer = new MusicPlayer(user.getUsername());
            for (PodcastData podcast : podcasts) {
                /* Set current episode to first one. */
                podcast.setCurrEpisode(podcast.getEpisodes().get(0));
                podcast.setIndexEpisode(0);

                for (EpisodeData episode : podcast.getEpisodes()) {
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
    public boolean islikedSong(final SongInput song) {
        return likedSongs.contains(song);
    }

    /**
     * Adds a given song to liked songs collection.
     * @param song      given song to add
     */
    public void likeSong(final SongInput song) {
        likedSongs.add(song);
    }

    /**
     * Remove a given song from liked songs colletction.
     * @param song      given song to remove
     */
    public void unlikeSong(final SongInput song) {
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
        for (SongInput song : likedSongs) {
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

        SongInput currSong;

        if (this.getSelectedSong() != null) {
            currSong = this.getSelectedSong();
        } else {
            currSong = this.getPlayback().currPlayingSong();
        }
        if (this.islikedSong(currSong)) {
            this.unlikeSong(currSong);
            return okUNLKED;
        } else {
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
        if (this.selectedSong != null) {
            this.getPlayback().setCurrSong(selectedSong);
        } else if (this.selectedPodcast != null) {
            this.getPlayback().setCurrPodcast(selectedPodcast);
        } else {
            this.getPlayback().setCurrPlaylist(selectedPlaylist);
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

        if (this.getPlayback().getCurrSong() != null) {
            this.getPlayback().setCurrSong(null);
            this.getPlayback().setPlayPause(false);
        } else if (this.getPlayback().getCurrPodcast() != null) {
            this.getPlayback().nextEpisodeinPodcast();
            if (this.getPlayback().getCurrEpisode() == null) {
                return noLOAD;
            } else {
                return successNEXT + this.getPlayback().getCurrEpisode().getName()
                        + ".";
            }
        } else {
            this.getPlayback().nextSonginPlaylist();
            if (this.getPlayback().currPlayingSong() == null) {
                return noLOAD;
            } else {
                this.getPlayback().setPlayPause(true);
                return successNEXT + this.getPlayback().currPlayingSong().getName()
                        + ".";
            }
        }

        return null;
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

        if (this.getPlayback().getCurrSong() != null) {
            this.getPlayback().setCurrSong(null);
            this.getPlayback().setPlayPause(false);
        } else if (this.getPlayback().getCurrPodcast() != null) {
            this.getPlayback().prevSonginPlaylist();
        } else {
            this.getPlayback().prevSonginPlaylist();
            if (this.getPlayback().currPlayingSong() == null) {
                return noLOAD;
            } else {
                this.getPlayback().setPlayPause(true);
                return successPREV + this.getPlayback().currPlayingSong().getName()
                        + ".";
            }
        }
        return null;
    }

    /**
     * Function that gets top 5 songs by number of likes.
     * @param library       where to get our songs
     * @param players       all user's players
     * @param outResults    interact with output
     */
    public static void getTop5Songs(final LibraryInput library,
            final ArrayList<MusicPlayer> players,
            final ArrayNode outResults) {
        /* create a vector of songs. */
        Map<SongInput, Integer> nrlikes = new HashMap<>();

        /* create a sorted arraylist. */
        ArrayList<SongInput> auxSongs = new ArrayList<>();

        final int maxTop = 5;

        /* Iterate over all songs and compute # of likes. */
        for (SongInput song : library.getSongs()) {
            Integer likes = 0;
            auxSongs.add(song);
            for (MusicPlayer player : players) {
                if (player.getLikedSongs().contains(song)) {
                    likes++;
                }
            }
            nrlikes.put(song, likes);
        }

        /* Sorting by likes comparator. */
        Comparator<SongInput> songInputComparator = new Comparator<SongInput>() {
            @Override
            public int compare(final SongInput stSong, final SongInput ndSong) {
                return nrlikes.get(ndSong) - nrlikes.get(stSong);
            }
        };

        /* Sort the arraylist of songs. */
        auxSongs.sort(songInputComparator);

        for (int iter = 0; iter < maxTop; ++iter) {
            outResults.add(auxSongs.get(iter).getName());
        }
    }
}
