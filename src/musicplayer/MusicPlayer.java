package musicplayer;

import lombok.Getter;
import musicplayer.AudioCollection.AudioType;
import users.UserNormal;

import java.util.ArrayList;
import database.Album;

import database.Playlist;
import database.Podcast;
import database.Song;

@Getter
public final class MusicPlayer {
    private UserNormal user;
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

    public MusicPlayer(final UserNormal user) {
        this.user = user;
        this.user.setLastSearch(null);
        this.user.setLastSelect(-1);
        MusicPlayer.timestamp = 0;
    }

    public void setUser(final UserNormal user) {
        this.user = user;
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
            if (player.user.getUsername().equals(username)) {
                player.checkStatus();
                return player;
            }
        }

        return null;
    }

    /**
     * Function that unloads a track from player.
     */
    public void unloadTrack() {
        if (this.getPlayback().getCurrTrack() != null) {
            this.initNewPlayback();
            this.getPlayback().setPlayPause(false);
        }
    }

    /**
     * Function that selects a track by last search.
     * @param mySelect      the index in search result
     * @return              command status
     */
    public String select(final int mySelect) {
        String noSEARCH = "Please conduct a search before making a selection.";
        String badID = "The selected ID is too high.";
        String successSELECT = "Successfully selected ";

        /* Check for search. */
        if (this.user.getLastSearch() == null) {
            return noSEARCH;
        }

        String track = null;
        this.selectedTrack = null;

        selectedTrack = this.user.getLastSearch().getSelect(mySelect);

        if (selectedTrack == null) {
            return badID;
        }

        track = selectedTrack.getName();

        user.setLastSelect(mySelect);
        return successSELECT + track + ".";
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
        if (playback != null) {
            playback.checkPlayback();
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
            currSong = (Song) (this.getPlayback().getCurrTrack());
        } else if (selectedTrack.getType() == AudioType.PLAYLIST) {
            currSong = ((Playlist) (this.getPlayback().getCurrTrack())).currPlayingSong(playback);
        } else {
            currSong = ((Album) (this.getPlayback().getCurrTrack())).currPlayingSong(playback);
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
     * Function that adds a song to a playlist.
     * @param playlistID        playlist ID to find the playlist
     * @return                  status of command
     */
    public String addRemoveInPlaylist(final Integer playlistID) {
        Playlist getplaylist = playlists.get(playlistID - 1);
        Song getSong;
        if (playback.getCurrTrack().getType() == AudioCollection.AudioType.ALBUM) {
            getSong = ((Album) playback.getCurrTrack()).currPlayingSong(playback);
        } else {
            getSong = (Song) playback.getCurrTrack();
        }
        return Playlist.addRemoveInPlaylist(getplaylist, getSong);
    }

    /**
     * Function that loads into the player.
     * @return      the success message
     */
    public String loadItem() {
        String successLOAD = "Playback loaded successfully.";
        this.initNewPlayback();

        this.getPlayback().setCurrTrack(selectedTrack);
        if (selectedTrack.getType() == AudioType.PODCAST) {
            /* Restore data. */
            playback.setTimeWatched(((Podcast) selectedTrack).getCurrEpisode().getTimeWatched());
        } else {
            /* Restore data. */
            playback.setIndexSong(0);
        }

        this.user.setLastSelect(-1);
        this.user.setLastSearch(null);

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
        if (playback.getCurrTrack() == null) {
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

        playback.checkPlayback();

        /* Check for load. */
        if (playback.getCurrTrack() == null) {
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
}
