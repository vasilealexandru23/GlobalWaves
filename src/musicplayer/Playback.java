package musicplayer;

import java.util.ArrayList;

import database.Album;
import database.Episode;
import database.MyDatabase;
import database.Playlist;
import database.Podcast;
import database.Song;
import lombok.Getter;
import users.UserNormal;

@Getter
public final class Playback {

    private int indexSong;
    private int indexSongShuffled;

    private int timeWatched;
    private boolean playPause;
    private int repeat;
    private boolean shuffle;
    private int lastInteract;
    private boolean stopped;

    /* Queue of the running player. */

    @Getter
    private int indexInQueue;
    @Getter
    private ArrayList<Song> queueSongs = new ArrayList<Song>();
    @Getter
    private ArrayList<Episode> queueEpisodes = new ArrayList<Episode>();

    public Playback() {
    }

    /**
     * Function that gets and checks the current track.
     * @return             the current track
     */
    public AudioCollection getCurrTrack() {
        checkPlayback();
        if (queueSongs.size() != 0) {
            if (indexInQueue >= queueSongs.size()) {
                return null;
            }
            return queueSongs.get(indexInQueue);
        } else if (queueEpisodes.size() != 0) {
            if (indexInQueue >= queueEpisodes.size()) {
                return null;
            }
            return queueEpisodes.get(indexInQueue);
        }
        return null;
    }

    /**
     * Function that sets the current track.
     * @param currTrack         the track to be set
     */
    public void setCurrTrack(final AudioCollection currTrack) {
        if (currTrack == null) {
            return;
        }
        if (currTrack.getType() == AudioCollection.AudioType.SONG) {
            queueSongs.add((Song) currTrack);
            indexInQueue = 0;
        } else if (currTrack.getType() == AudioCollection.AudioType.PODCAST) {
            timeWatched = ((Podcast) currTrack).getCurrEpisode().getTimeWatched();
            for (Episode episode : ((Podcast) currTrack).getEpisodes()) {
                queueEpisodes.add(episode);
            }
            indexInQueue = ((Podcast) currTrack).getIndexEpisode();
        } else if (currTrack.getType() == AudioCollection.AudioType.ALBUM) {
            for (Song song : ((Album) currTrack).getSongs()) {
                queueSongs.add(song);
            }
            indexInQueue = 0;
        } else if (currTrack.getType() == AudioCollection.AudioType.PLAYLIST) {
            for (Song song : ((Playlist) currTrack).getSongs()) {
                queueSongs.add(song);
            }
            indexInQueue = 0;
        }
        currTrack.updatePlays(this);
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
        this.repeat = repeat;
    }

    public void setShuffle(final boolean shuffle) {
        this.shuffle = shuffle;
    }

    public void setLastInteracted(final int lastCmd) {
        this.lastInteract = lastCmd;
    }

    public void setIndexInQueue(final int indexInQueue) {
        this.indexInQueue = indexInQueue;
    }

    /**
     * This function returns repeat status.
     * @return repeat status.
     */
    public String getRepeatstatus() {
        if (this.repeat == 0) {
            return "No Repeat";
        } else if (this.repeat == 1) {
            if (getCurrTrack().getType() == AudioCollection.AudioType.PLAYLIST) {
                return "Repeat All";
            }
            return "Repeat Once";
        } else {
            if (getCurrTrack().getType() == AudioCollection.AudioType.PLAYLIST) {
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
        if (getCurrTrack() != null) {
            return getCurrTrack().getTrack(this);
        }
        return "";
    }

    /**
     * Function that computes the time remained of a track.
     * @return      the time remained as integer
     */
    public int getTimeRemained() {
        if (getCurrTrack() != null) {
            return getCurrTrack().getTimeRemained(this);
        }
        return 0;
    }

    /**
     * Functions that updates the state of playback.
     */
    public void checkPlayback() {
        if (queueSongs.size() != 0) {
            if (indexInQueue >= queueSongs.size()) {
                return;
            }
            if (isPlayPause() && !isStopped()) {
                timeWatched += MusicPlayer.getTimestamp() - lastInteract;
                while (timeWatched >= queueSongs.get(indexInQueue).getDuration()) {
                    timeWatched -= queueSongs.get(indexInQueue).getDuration();
                    indexInQueue++;
                    if (indexInQueue == queueSongs.size()) {
                        setPlayPause(false);
                        break;
                    }
                    queueSongs.get(indexInQueue).updatePlays(this);
                }
            }
        } else if (queueEpisodes.size() != 0) {
            if (indexInQueue >= queueEpisodes.size()) {
                return;
            }
            if (isPlayPause() && !isStopped()) {
                timeWatched += MusicPlayer.getTimestamp() - lastInteract;
                while (timeWatched >= queueEpisodes.get(indexInQueue).getDuration()) {
                    timeWatched -= queueEpisodes.get(indexInQueue).getDuration();
                    indexInQueue++;
                    if (indexInQueue == queueEpisodes.size()) {
                        setPlayPause(false);
                        break;
                    }
                    queueEpisodes.get(indexInQueue).updatePlays(this);
                }
                if (indexInQueue < queueEpisodes.size()) {
                    queueEpisodes.get(indexInQueue).setTimeWatched(timeWatched);
                }
            }
        }
        lastInteract = MusicPlayer.getTimestamp();
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
            if (getCurrTrack().getType() == AudioCollection.AudioType.PLAYLIST) {
                ((Playlist) getCurrTrack()).restoreIndex(this);
            } else {
                ((Album) getCurrTrack()).restoreIndex(this);
            }
            return outDEZACTIVATED;
        } else {
            this.shuffle = true;
            if (getCurrTrack().getType() == AudioCollection.AudioType.PLAYLIST) {
                ((Playlist) getCurrTrack()).generateShuffle(this, seed);
            } else {
                ((Album) getCurrTrack()).generateShuffle(this, seed);
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

        if (this.getCurrTrack().getType() == AudioCollection.AudioType.PLAYLIST) {
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

    /**
     * Function that adds an ad to the queue.
     * @param user          user that adds the ad
     * @param price         price of the ad
     * @return              status of the command
     */
    public String adBreak(final UserNormal user, final int price) {
        /* Update the playback state. */
        checkPlayback();
        /* Insert the ad into queue. */
        Song newAd = MyDatabase.getInstance().getAllSongsCreated().get(0).dupSong();
        newAd.setPrice(price);
        queueSongs.add(indexInQueue + 1, newAd);
        return "Ad inserted successfully.";
    }
}
