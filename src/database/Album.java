package database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import lombok.Getter;
import musicplayer.AudioCollection;
import musicplayer.MusicPlayer;
import musicplayer.Playback;
import users.UserNormal;

public final class Album extends AudioCollection {
    private String name;
    @Getter
    private String owner;
    @Getter
    private Integer releaseYear;
    @Getter
    private String description;
    @Getter
    private ArrayList<Song> songs;
    @Getter
    private ArrayList<Song> shuffledPlaylist;

    public Album(final String name, final String owner, final Integer releaseYear,
            final String description, final ArrayList<Song> songs) {
        this.name = name;
        this.owner = owner;
        this.releaseYear = releaseYear;
        this.description = description;
        this.songs = songs;
    }

    @Override
    public int getTimeRemained(final Playback playback) {
        if (playback.getIndexInQueue() == playback.getQueueSongs().size()) {
            return 0;
        }

        if (!playback.isShuffle()) {
            return currPlayingSong(playback).getDuration() - playback.getTimeWatched();
        } else {
            return shuffledPlaylist.get(playback.getIndexSongShuffled()).getDuration()
                    - playback.getTimeWatched();
        }
    }

    /**
     * Function that changes the current
     * index in playlist to the next one.
     * @param playback
     */
    public void goToNextSong(final Playback playback) {
        if (playback.getIndexSong() == playback.getQueueSongs().size()) {
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
                playback.setIndexInQueue(playback.getIndexInQueue() + 1);
            }

            if (playback.getRepeat() == 1) {
                /* Repeat the same song. */
                playback.setIndexInQueue(playback.getIndexInQueue()
                        % playback.getQueueSongs().size());
            }
        }

    }

    @Override
    public void nextTrack(final Playback playback) {
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
            if (playback.getRepeat() != 2) {
                playback.setIndexSongShuffled(playback.getIndexSongShuffled() - 1);
            }

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

    @Override
    public void prevTrack(final Playback playback) {

        if (playback.getTimeWatched() > 0) {
            playback.setTimeWatched(0);
        } else {
            goToPrevSong(playback);
        }

        playback.setLastInteracted(MusicPlayer.getTimestamp());
    }

    public String getName() {
        return name;
    }

    @Override
    public String getTrack(final Playback playback) {
        if (playback.getCurrTrack() == null) {
            return "";
        }

        if (playback.isShuffle()) {
            return shuffledPlaylist.get(playback.getIndexSongShuffled()).getName();
        } else {
            return currPlayingSong(playback).getName();
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

   /**
     * Function that returns current playing song in a playlist.
     * @param playback      current playback
     * @return              current playing song
     */
    public Song currPlayingSong(final Playback playback) {
        if (playback.getIndexSong() == playback.getQueueSongs().size()) {
            return null;
        }

        if (!playback.isShuffle()) {
            return playback.getQueueSongs().get(playback.getIndexInQueue());
        } else {
            return shuffledPlaylist.get(playback.getIndexSongShuffled());
        }
    }

    @Override
    public void updatePlays(final Playback playback) {
        if (playback.getIndexSong() == playback.getQueueSongs().size()) {
            return;
        }

        currPlayingSong(playback).updatePlays(playback);
    }

    @Override
    public int getPlays() {
        int totalPlays = 0;
        for (Song song : MyDatabase.getInstance().getAllSongsCreated()) {
            if (song.getArtist().equals(owner) && song.getAlbum().equals(name)) {
                totalPlays += song.getPlays();
            }
        }
        for (Song song : MyDatabase.getInstance().getDeletedSongs()) {
            if (song.getArtist().equals(owner) && song.getAlbum().equals(name)) {
                totalPlays += song.getPlays();
            }
        }
        return totalPlays;
    }

    /**
     * Function that computes the number of plays
     * of an album for a specific user.
     * @param user
     * @return
     */
    public int getPlays(final UserNormal user) {
        int plays = 0;
        for (Song song : user.getHistorySongs()) {
            if (song.getAlbum().equals(name)) {
                plays++;
            }
        }
        for (Song song : user.getHistorySongsPremium()) {
            if (song.getAlbum().equals(name)) {
                plays++;
            }
        }
        for (Song song : user.getAllSongsPlayed()) {
            if (song.getAlbum().equals(name)) {
                plays++;
            }
        }
        return plays;
    }
}
