package database;

import lombok.Getter;
import musicplayer.AudioCollection;
import musicplayer.MusicPlayer;
import musicplayer.Playback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class Playlist extends AudioCollection {
    @Getter
    private ArrayList<Song> songs = new ArrayList<>();
    @Getter
    private ArrayList<Song> shuffledPlaylist;
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

    public void setSongs(final ArrayList<Song> songs) {
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
    public boolean songExists(final Song song) {
        return songs.contains(song);
    }

    /**
     * Remove given song from playlist.
     * @param song given song to remove
     */
    public void songRemove(final Song song) {
        songs.remove(song);
    }

    /**
     * Add given song to playlist.
     * @param song given song to add
     */
    public void songAdd(final Song song) {
        this.songs.add(song);
    }

    /**
     * Returns a list of song names in given playlist.
     * @return an ArrayList of strings.
     */
    public ArrayList<String> getSongNames() {
        ArrayList<String> songNames = new ArrayList<>();

        for (Song song : songs) {
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
    public String checkVisibility() {
        if (isVisibility()) {
            return "public";
        } else {
            return "private";
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
        Playlist newPlaylist = new Playlist(name, player.getUser().getUsername());
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
            final Song song) {
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

    /**
     * Function that changes the current
     * index in playlist to the next one.
     * @param playback
     */
    public void goToNextSong(final Playback playback) {
        if (playback.getIndexSong() == this.getSongs().size()) {
            return;
        }

        if (playback.isShuffle()) {
            if (playback.getRepeat() != 2) {
                /* Go to next song. */
                playback.setIndexSongShuffled(playback.getIndexSongShuffled() + 1);
            }

            if (playback.getRepeat() == 1) {
                /* Repeat the same song. */
                playback.setIndexSongShuffled(playback.getIndexSongShuffled() % getSongs().size());
            }
            playback.setIndexSong(playback.getIndexSongShuffled());
        } else {
            if (playback.getRepeat() != 2) {
                /* Go to next song. */
                playback.setIndexSong(playback.getIndexSong() + 1);
            }

            if (playback.getRepeat() == 1) {
                /* Repeat the same song. */
                playback.setIndexSong(playback.getIndexSong() % getSongs().size());
            }
        }
    }

    /**
     * Function that changes the current
     * index in playlist to the previous one.
     * @param playback
     */
    public void goToPrevSong(final Playback playback) {
        if (playback.getIndexSong() == this.getSongs().size()) {
            return;
        }

        if (playback.isShuffle()) {
            /* Go to prev song. */

            playback.setIndexSongShuffled(playback.getIndexSongShuffled() - 1);

            if (playback.getIndexSongShuffled() < 0) {
                playback.setIndexSongShuffled(0);
            }

            if (playback.getRepeat() == 1) {
                playback.setIndexSongShuffled(playback.getIndexSongShuffled() % getSongs().size());
            }
            playback.setIndexSong(playback.getIndexSongShuffled());
        } else {
            if (playback.getRepeat() != 2) {
                playback.setIndexSong(playback.getIndexSong() - 1);
            }

            if (playback.getIndexSong() < 0) {
                playback.setIndexSong(0);
            }

            if (playback.getRepeat() == 1) {
                playback.setIndexSong(playback.getIndexSong() % getSongs().size());
            }
        }
    }

    /**
     * Function that checks current playing song in playlist.
     */
    public void checkCurrentSong(final Playback playback) {
        if (playback.getIndexSong() == this.getSongs().size()) {
            return;
        }
        if (playback.isPlayPause() && !playback.isStopped()) {
            Integer updateTimeWatched = playback.getTimeWatched()
                    + MusicPlayer.getTimestamp() - playback.getLastInteract();
            playback.setTimeWatched(updateTimeWatched);
            if (!playback.isShuffle()) {
                while (playback.getTimeWatched()
                        >= this.getSongs().get(playback.getIndexSong()).getDuration()) {
                    updateTimeWatched = playback.getTimeWatched()
                            - this.getSongs().get(playback.getIndexSong()).getDuration();
                    playback.setTimeWatched(updateTimeWatched);
                    this.goToNextSong(playback);
                    /* Check if we have no song running. */
                    if (playback.getIndexSong() == this.getSongs().size()) {
                        playback.setPlayPause(false);
                        playback.setCurrTrack(null);
                        break;
                    }
                }
            } else {
                while (playback.getTimeWatched()
                        >= shuffledPlaylist.get(playback.getIndexSongShuffled()).getDuration()) {
                    updateTimeWatched = playback.getTimeWatched()
                            - shuffledPlaylist.get(playback.getIndexSongShuffled()).getDuration();
                    playback.setTimeWatched(updateTimeWatched);
                    this.goToNextSong(playback);
                    /* Check if we have no song running. */
                    if (playback.getIndexSongShuffled() == this.getSongs().size()) {
                        playback.setPlayPause(false);
                        playback.setShuffle(false);
                        playback.setCurrTrack(null);
                        break;
                    }
                }
            }
            playback.setLastInteracted(MusicPlayer.getTimestamp());
        }
    }

    /**
     * Function that generated the shuffled playlist.
     * @param seed          to generate random shuffle
     */
    public void generateShuffle(final Playback playback, final int seed) {
        if (!playback.isShuffle()) {
            shuffledPlaylist = null;
        } else {
            shuffledPlaylist = new ArrayList<>();

            for (Song song : this.getSongs()) {
                shuffledPlaylist.add(song);
            }

            Collections.shuffle(shuffledPlaylist, new Random(seed));

            /* Get the index of currnet playing song in shuffled playlist. */
            for (int iter = 0; iter < shuffledPlaylist.size(); ++iter) {
                if (shuffledPlaylist.get(iter) == this.getSongs().get(playback.getIndexSong())) {
                    playback.setIndexSongShuffled(iter);
                }
            }
        }
    }

    /**
     * Find the index of the current playing song in unshuffled array.
     * @param playback      current playback
     */
    public void restoreIndex(final Playback playback) {
        for (int iter = 0; iter < shuffledPlaylist.size(); ++iter) {
            if (shuffledPlaylist.get(playback.getIndexSongShuffled()) == this.songs.get(iter)) {
                playback.setIndexSong(iter);
            }
        }
    }

    @Override
    public int getTimeRemained(final Playback playback) {
        this.checkCurrentSong(playback);
        if (playback.getIndexSong() == this.songs.size()) {
            return 0;
        }

        if (!playback.isShuffle()) {
            return this.songs.get(playback.getIndexSong()).getDuration()
                    - playback.getTimeWatched();
        } else {
            return shuffledPlaylist.get(playback.getIndexSongShuffled()).getDuration()
                    - playback.getTimeWatched();
        }
    }

    /**
     * Function that returns current playing song in a playlist.
     * @param playback      current playback
     * @return              current playing song
     */
    public Song currPlayingSong(final Playback playback) {
        if (playback.getIndexSong() == this.songs.size()) {
            return null;
        }

        if (!playback.isShuffle()) {
            return this.getSongs().get(playback.getIndexSong());
        } else {
            return shuffledPlaylist.get(playback.getIndexSongShuffled());
        }
    }

    @Override
    public String getTrack(final Playback playback) {
        checkCurrentSong(playback);
        if (playback.getCurrTrack() == null) {
            return "";
        }

        if (playback.isShuffle()) {
            return shuffledPlaylist.get(playback.getIndexSongShuffled()).getName();
        } else {
            if (songs.size() == 0) {
                return "";
            }
            return songs.get(playback.getIndexSong()).getName();
        }
    }

    @Override
    public void nextTrack(final Playback playback) {
        checkCurrentSong(playback);
        goToNextSong(playback);

        if (playback.isShuffle()) {
            if (playback.getIndexSongShuffled() == this.getSongs().size()) {
                playback.setPlayPause(false);
                playback.setShuffle(false);
                playback.setCurrTrack(null);
            }
        } else {
            if (playback.getIndexSong() == this.getSongs().size()) {
                playback.setPlayPause(false);
                playback.setCurrTrack(null);
            }
        }

        playback.setLastInteracted(MusicPlayer.getTimestamp());
        playback.setTimeWatched(0);
    }

    @Override
    public void prevTrack(final Playback playback) {
        checkCurrentSong(playback);

        if (playback.getTimeWatched() > 0) {
            playback.setTimeWatched(0);
        } else {
            goToPrevSong(playback);
    }

        playback.setLastInteracted(MusicPlayer.getTimestamp());
    }

    @Override
    public void updatePlays(final Playback playback) {
        if (playback.getIndexSong() == this.getSongs().size()) {
            return;
        }

        currPlayingSong(playback).updatePlays(playback);
    }
}
