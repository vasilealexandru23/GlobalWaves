package musicplayer;

import database.Album;
import database.Episode;
import database.Playlist;
import database.Podcast;
import lombok.Getter;

@Getter
public final class Playback {
    private AudioCollection currTrack;

    private int indexSong;
    private int indexSongShuffled;

    private int timeWatched;
    private boolean playPause;
    private int repeat;
    private boolean shuffle;
    private int lastInteract;
    private boolean stopped;

    public Playback() {
    }

    /**
     * Function that gets and checks the current track.
     * @return             the current track
     */
    public AudioCollection getCurrTrack() {
        if (currTrack != null) {
            currTrack.checkTrack(this);
        }
        return currTrack;
    }

    public void setCurrTrack(final AudioCollection currTrack) {
        this.currTrack = currTrack;
    }

    public void setIndexSong(final int indexSong) {
        this.indexSong = indexSong;
    }

    public void setIndexSongShuffled(final int indexSongShuffled) {
        this.indexSongShuffled = indexSongShuffled;
    }

    public void setTimeWatched(final int timeWatched) {
        this.timeWatched = timeWatched;
    }

    public void setPlayPause(final boolean playPause) {
        this.playPause = playPause;
    }

    /**
     * Function that gets the current episode running for a podcast.
     * @return
     */
    public Episode getCurrEpisode() {
        return ((Podcast) currTrack).getCurrEpisode();
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
        if (currTrack != null) {
            currTrack.checkTrack(this);
        }
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
            if (currTrack.getType() == AudioCollection.AudioType.PLAYLIST) {
                return "Repeat All";
            }
            return "Repeat Once";
        } else {
            if (currTrack.getType() == AudioCollection.AudioType.PLAYLIST) {
                return "Repeat Current Song";
            }
            return "Repeat Infinite";
        }
    }

    /**
     * Function that gets the current track's name.
     * @return      the track's name as a string
     */
    public String getTrack() {
        if (currTrack != null) {
            return currTrack.getTrack(this);
        }
        return "";
    }

    /**
     * Function that computes the time remained of a track.
     * @return      the time remained as integer
     */
    public int getTimeRemained() {
        if (currTrack != null) {
            return currTrack.getTimeRemained(this);
        }
        return 0;
    }

    /**
     * Functions that checks if something is on playback.
     * @return      if something is runnning on playback
     */
    public boolean checkPlayback() {
        if (currTrack != null) {
            currTrack.checkTrack(this);
            return true;
        }

        return false;
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
            if (currTrack.getType() == AudioCollection.AudioType.PLAYLIST) {
                ((Playlist) currTrack).restoreIndex(this);
            } else {
                ((Album) currTrack).restoreIndex(this);
            }
            return outDEZACTIVATED;
        } else {
            this.shuffle = true;
            if (currTrack.getType() == AudioCollection.AudioType.PLAYLIST) {
                ((Playlist) currTrack).generateShuffle(this, seed);
            } else {
                ((Album) currTrack).generateShuffle(this, seed);
            }

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

        if (this.currTrack.getType() == AudioCollection.AudioType.PLAYLIST) {
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

    /**
     * Function that puts the current playback on stop.
     */
    public void stopPlayback() {
        checkPlayback();
        stopped = true;
    }

    /**
     * Function that starts the current playback.
     */
    public void startPlayback() {
        lastInteract = MusicPlayer.getTimestamp();
        stopped = false;
    }
}
