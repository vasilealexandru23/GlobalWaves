package musicplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import fileio.input.SongInput;
import lombok.Getter;
import mydata.EpisodeData;
import mydata.PodcastData;

public final class Playback {
    @Getter
    private SongInput currSong;

    @Getter
    private PodcastData currPodcast;
    @Getter
    private EpisodeData currEpisode;
    @Getter
    private int indexEpisode;

    @Getter
    private Playlist currPlaylist;
    @Getter
    private int indexSong;
    @Getter
    private int indexSongShuffled;

    @Getter
    private int timeWatched;
    @Getter
    private boolean playPause;
    @Getter
    private int repeat;
    @Getter
    private boolean shuffle;
    @Getter
    private int lastInteract;

    private ArrayList<SongInput> shuffledPlaylist = null;

    public Playback() {
    }

    public void setCurrSong(final SongInput song) {
        this.currSong = song;
    }

    /**
     * Sets the current podcast and restore data from previous watch.
     * @param currPodcast
     */
    public void setCurrPodcast(final PodcastData currPodcast) {
        this.currPodcast = currPodcast;

        /* Restore indexEpisode. */
        this.indexEpisode = currPodcast.getIndexEpisode();

        /* Restore timeWatched. */
        this.timeWatched = currPodcast.getEpisodes().get(indexEpisode).getTimeWatched();

        /* Restore currEpisode. */
        this.currEpisode = currPodcast.getEpisodes().get(this.indexEpisode);
    }

    public void setCurrEpisode(final EpisodeData episode) {
        this.currEpisode = episode;
    }

    public void setIndexEpisode(final int index) {
        this.indexEpisode = index;
    }

    public void setIndexSongShuffled(final int indexSongShuffled) {
        this.indexSongShuffled = indexSongShuffled;
    }

    /**
     * Sets the current playlist and restore index of current song.
     * @param playlist
     */
    public void setCurrPlaylist(final Playlist playlist) {
        this.currPlaylist = playlist;
        this.indexSong = 0;
    }

    public void setIndexSong(final int indexSong) {
        this.indexSong = indexSong;
    }

    public void setTimeWatched(final int timeWatched) {
        this.timeWatched = timeWatched;
    }

    public void setPlayPause(final boolean playPause) {
        this.playPause = playPause;
    }

    /**
     * Function that sets current track on play
     * or pause and updates the last time interacted.
     * @param currTime
     */
    public void updatePlayPause(final int currTime) {
        this.lastInteract = currTime;
        this.playPause = !this.playPause;
    }

    /**
     * Function that sets repeat status and checks player status.
     * @param repeat        repeat mode to update
     */
    public void setRepeat(final int repeat) {
        checkCurrentEpisode();
        checkCurrentSong();
        checkSong();
        this.repeat = repeat;
    }

    public void setShuffle(final boolean shuffle) {
        this.shuffle = shuffle;
    }

    public void setLastInteracted(final int lastCmd) {
        this.lastInteract = lastCmd;
    }

    /**
     * This function returns repeat status.
     * @return repeat status.
     */
    public String getRepeatstatus() {
        if (this.repeat == 0) {
            return "No Repeat";
        } else if (this.repeat == 1) {
            if (currPlaylist != null) {
                return "Repeat All";
            }
            return "Repeat Once";
        } else {
            if (currPlaylist != null) {
                return "Repeat Current Song";
            }
            return "Repeat Infinite";
        }
    }

    /**
     * Function that update podcast data.
     */
    public void updatePodcastData() {
        this.currPodcast.setCurrEpisode(this.currEpisode);
        this.currPodcast.setIndexEpisode(this.indexEpisode);
        this.currPodcast.getCurrEpisode().setTimeWatched(this.timeWatched);
    }

    /**
     * Function that update the
     * current episode to the next one.
     */
    public void goToNextEpisode() {
        this.indexEpisode++;
        if (this.indexEpisode == currPodcast.getEpisodes().size()) {
            if (this.repeat != 0) {
                this.indexEpisode = 0;
            }
        }
        if (this.indexEpisode < currPodcast.getEpisodes().size()) {
            this.currEpisode = this.currPodcast.getEpisodes().get(indexEpisode);
        } else {
            this.currEpisode = null;
            this.playPause = false;
        }
    }

    /**
     * Function that gets a podcast to next episode.
     */
    public void nextEpisodeinPodcast() {
        if (this.currEpisode == null) {
            return;
        }

        /* Play next episode. */
        goToNextEpisode();

        /* Make update in player's podcast. */
        this.timeWatched = 0;
        updatePodcastData();
        this.lastInteract = MusicPlayer.getTimestamp();
    }

    /**
     * Function that checks the current episode in timeline.
     */
    public void checkCurrentEpisode() {
        if (this.currEpisode == null) {
            return;
        }
        if (this.indexEpisode == currPodcast.getEpisodes().size()) {
            return;
        }
        if (this.playPause) {
            this.timeWatched += MusicPlayer.getTimestamp() - this.lastInteract;
            while (this.timeWatched > currEpisode.getDuration()) {
                this.timeWatched -= currEpisode.getDuration();
                goToNextEpisode();
            }
            updatePodcastData();
            this.lastInteract = MusicPlayer.getTimestamp();
        }
    }

    /**
     * Function that checks if a song is running.
     * @return
     */
    public boolean checkSong() {
        if (currSong == null) {
            return false;
        }
        if (this.playPause) {
            this.timeWatched += MusicPlayer.getTimestamp() - this.lastInteract;
            this.lastInteract = MusicPlayer.getTimestamp();
            while (this.repeat != 0 && this.timeWatched > currSong.getDuration()) {
                if (this.repeat != 0) {
                    this.timeWatched -= currSong.getDuration();
                    if (this.repeat == 1) {
                        this.repeat = 0;
                    }
                }
            }
            if (this.timeWatched > currSong.getDuration()) {
                this.currSong = null;
                this.playPause = false;
                return false;
            }
        }
        return true;
    }

    /**
     * Function that changes the current index in playlist.
     */
    public void goToNextSong() {
        if (this.shuffle) {
            if (this.repeat != 2) {
                /* Go to next song. */
                this.indexSongShuffled++;
            }
            if (this.repeat == 1) {
                this.indexSongShuffled %= currPlaylist.getSongs().size();
            }
            indexSong = indexSongShuffled;
        } else {
            if (this.repeat != 2) {
                this.indexSong++;
            }
            if (this.repeat == 1) {
                this.indexSong %= currPlaylist.getSongs().size();
            }
        }
    }

    /**
     * Function that changes the current index in playlist.
     */
    public void goToPrevSong() {
        if (this.shuffle) {
            /* Go to prev song. */
            if (this.repeat != 2) {
                this.indexSongShuffled--;
            }

            if (this.indexSongShuffled < 0) {
                this.indexSongShuffled = 0;
            }

            if (this.repeat == 1) {
                this.indexSongShuffled %= currPlaylist.getSongs().size();
            }
            indexSong = indexSongShuffled;
        } else {
            if (this.repeat != 2) {
                this.indexSong--;
            }

            if (this.indexSong < 0) {
                this.indexSong = 0;
            }

            if (this.repeat == 1) {
                this.indexSong %= currPlaylist.getSongs().size();
            }
        }
    }

    /**
     * Function that sets the next song in playlist.
     */
    public void nextSonginPlaylist() {
        if (currPlaylist == null) {
            return;
        }
        if (this.indexSong == currPlaylist.getSongs().size()) {
            return;
        }

        goToNextSong();
        if (!this.shuffle) {
            /* Check if we have no song running. */
            if (this.indexSong == currPlaylist.getSongs().size()) {
                this.playPause = false;
                this.currPlaylist = null;
            }
        } else {
            /* Check if we have no song running. */
            if (this.indexSongShuffled == currPlaylist.getSongs().size()) {
                this.currPlaylist = null;
                this.playPause = false;
                this.shuffle = false;
            }
        }
        this.lastInteract = MusicPlayer.getTimestamp();
        this.timeWatched = 0;
    }

    /**
     * Function that sets the current song to previous one.
     */
    public void prevSonginPlaylist() {
        checkCurrentSong();
        if (this.indexSong == currPlaylist.getSongs().size()) {
            return;
        }
        if (this.timeWatched > 0) {
            this.timeWatched = 0;
        } else {
            goToPrevSong();
        }
        this.lastInteract = MusicPlayer.getTimestamp();
    }

    /**
     * Function that checks current playing song in playlist.
     */
    public void checkCurrentSong() {
        if (this.currPlaylist == null) {
            return;
        }
        if (this.indexSong == currPlaylist.getSongs().size()) {
            return;
        }
        if (this.playPause) {
            this.timeWatched += MusicPlayer.getTimestamp() - this.lastInteract;
            if (!this.shuffle) {
                while (this.timeWatched > currPlaylist.getSongs().get(indexSong).getDuration()) {
                    this.timeWatched -= currPlaylist.getSongs().get(indexSong).getDuration();
                    goToNextSong();
                    /* Check if we have no song running. */
                    if (this.indexSong == currPlaylist.getSongs().size()) {
                        this.playPause = false;
                        this.currPlaylist = null;
                        break;
                    }
                }
            } else {
                while (this.timeWatched > shuffledPlaylist.get(indexSongShuffled).getDuration()) {
                    this.timeWatched -= shuffledPlaylist.get(indexSongShuffled).getDuration();
                    goToNextSong();
                    /* Check if we have no song running. */
                    if (this.indexSongShuffled == currPlaylist.getSongs().size()) {
                        this.currPlaylist = null;
                        this.playPause = false;
                        this.shuffle = false;
                        break;
                    }
                }
            }
            this.lastInteract = MusicPlayer.getTimestamp();
        }
    }

    /**
     * Function that gets the current track's name.
     * @return      the track's name as a string
     */
    public String getTrack() {
        int currTime = MusicPlayer.getTimestamp();
        if (this.currSong != null) {
            if (!checkSong()) {
                return "";
            }
            return this.currSong.getName();
        } else if (this.currPodcast != null) {
            checkCurrentEpisode();
            return this.currEpisode.getName();
        } else if (this.currPlaylist != null) {
            checkCurrentSong();
            if (this.currPlaylist == null || this.indexSong == currPlaylist.getSongs().size()) {
                return "";
            }
            if (!this.shuffle) {
                return currPlaylist.getSongs().get(indexSong).getName();
            } else {
                return shuffledPlaylist.get(indexSongShuffled).getName();
            }
        }
        return "";
    }

    /**
     * Function that computes the time remained of a track.
     * @return      the time remained as integer
     */
    public int getTimeRemained() {
        int currTime = MusicPlayer.getTimestamp();
        if (this.currSong != null) {
            if (!checkSong()) {
                playPause = false;
                return 0;
            }

            return this.currSong.getDuration() - this.timeWatched;
        } else if (this.currPodcast != null) {
            checkCurrentEpisode();
            return this.currEpisode.getDuration() - this.timeWatched;
        }

        checkCurrentSong();
        if (this.currPlaylist == null || indexSong == currPlaylist.getSongs().size()) {
            return 0;
        }

        if (!this.shuffle) {
            return this.currPlaylist.getSongs().get(indexSong).getDuration() - this.timeWatched;
        } else {
            return this.shuffledPlaylist.get(indexSongShuffled).getDuration() - this.timeWatched;
        }
    }

    /**
     * Function that returns current playing song in a playlist.
     * @return          the current playing song
     */
    public SongInput currPlayingSong() {
        if (currPlaylist == null || indexSong == currPlaylist.getSongs().size()) {
            return null;
        }

        if (!shuffle) {
            return currPlaylist.getSongs().get(indexSong);
        } else {
            return shuffledPlaylist.get(indexSongShuffled);
        }
    }

    /**
     * Function that generated the shuffled playlist.
     * @param seed          to generate random shuffle
     */
    public void generateShuffle(final int seed) {
        if (!shuffle) {
            shuffledPlaylist = null;
        } else {
            shuffledPlaylist = new ArrayList<>();

            for (SongInput song : currPlaylist.getSongs()) {
                shuffledPlaylist.add(song);
            }

            Collections.shuffle(shuffledPlaylist, new Random(seed));

            /* Get the index of currnet playing song in shuffled playlist. */
            for (int iter = 0; iter < shuffledPlaylist.size(); ++iter) {
                if (shuffledPlaylist.get(iter) == currPlaylist.getSongs().get(indexSong)) {
                    indexSongShuffled = iter;
                }
            }
        }
    }

    /**
     * Find the index of the current playing song in unshuffled array.
     */
    public void restoreIndex() {
        for (int iter = 0; iter < shuffledPlaylist.size(); ++iter) {
            if (shuffledPlaylist.get(indexSongShuffled) == currPlaylist.getSongs().get(iter)) {
                indexSong = iter;
            }
        }
    }

    /**
     * Function that skips 90 seconds forward current episode.
     * @return      the status of command
     */
    public String forward() {
        String successFORWARD = "Skipped forward successfully.";
        final int forwardTime = 90;
        if (getTimeRemained() < forwardTime) {
            goToNextEpisode();
            updatePodcastData();
        } else {
            this.timeWatched += forwardTime;
        }
        this.currPodcast.getCurrEpisode().setTimeWatched(this.timeWatched);
        this.lastInteract = MusicPlayer.getTimestamp();
        return successFORWARD;
    }

    /**
     * Function that rewound an episode with 90 seconds back
     * @return      command status success.
     */
    public String backward() {
        String successBACKWARD = "Rewound successfully.";
        final int backwardTime = 90;
        checkCurrentEpisode();
        if (this.timeWatched >= backwardTime) {
            this.timeWatched -= backwardTime;
        } else {
            this.timeWatched = 0;
        }
        this.currPodcast.getCurrEpisode().setTimeWatched(this.timeWatched);
        this.lastInteract = MusicPlayer.getTimestamp();
        return successBACKWARD;
    }

    /**
     * Functions that checks if something is on playback.
     * @return      if something is runnning on playback
     */
    public boolean checkPlayback() {
        checkSong();
        checkCurrentSong();
        checkCurrentEpisode();

        if (currEpisode == null && currSong == null && currPlaylist == null) {
            return false;
        }
        return true;
    }

    /**
     * Function that changes shuffle status.
     * @param seed
     * @return
     */
    public String changeShuffle(final int seed) {
        String outACTIVATED = "Shuffle function activated successfully.";
        String outDEZACTIVATED = "Shuffle function deactivated successfully.";

        if (this.shuffle) {
            this.shuffle = false;
            restoreIndex();
            return outDEZACTIVATED;
        } else {
            this.shuffle = true;
            generateShuffle(seed);
            return outACTIVATED;
        }
    }

    /**
     * Function that changes repeat status
     * @return status of command
     */
    public String repeat() {
        String noREPEAT = "Repeat mode changed to no repeat.";
        String allREPEAT = "Repeat mode changed to repeat all.";
        String currREPEAT = "Repeat mode changed to repeat current song.";
        String onceREPEAT = "Repeat mode changed to repeat once.";
        String infREPEAT = "Repeat mode changed to repeat infinite.";
        final int maxREPEAT = 3;

        repeat = (repeat + 1) % maxREPEAT;

        if (this.currPlaylist != null) {
            if (repeat == 1) {
                return allREPEAT;
            } else if (repeat == 2) {
                return currREPEAT;
            } else {
                return noREPEAT;
            }
        } else {
            if (repeat == 1) {
                return onceREPEAT;
            } else if (repeat == 2) {
                return infREPEAT;
            } else {
                return noREPEAT;
            }
        }
    }

    /**
     * Function that sets playPause status.
     * @return status of current playback.
     */
    public String playPause() {
        String playbackRESUMED = "Playback resumed successfully.";
        String playbackPAUSED = "Playback paused successfully.";

        updatePlayPause(MusicPlayer.getTimestamp());

        if (this.isPlayPause()) {
            return playbackRESUMED;
        }

        return playbackPAUSED;
    }
}
