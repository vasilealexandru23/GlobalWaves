package database;

import java.util.ArrayList;

import musicplayer.AudioCollection;
import musicplayer.MusicPlayer;
import musicplayer.Playback;

public final class Podcast extends AudioCollection {
    private String name;
    private String owner;
    private ArrayList<Episode> episodes;
    private int indexEpisode;

    public Podcast(final String name, final String owner) {
        this.name = name;
        this.owner = owner;
    }

    public Podcast(final String name, final String owner, final ArrayList<Episode> episodes) {
        this.name = name;
        this.owner = owner;
        this.episodes = episodes;
        for (Episode episode : episodes) {
            episode.setOwner(owner);
        }
        indexEpisode = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(final ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }

    /**
     * Function that returns the current playing episode.
     * @return
     */
    public Episode getCurrEpisode() {
        if (indexEpisode == episodes.size()) {
            return null;
        }

        return episodes.get(indexEpisode);
    }

    public int getIndexEpisode() {
        return indexEpisode;
    }

    public void setIndexEpisode(final int index) {
        this.indexEpisode = index;
    }

    /**
     * Function that updates the podcast data.
     * @param playback      current playback
     */
    public void updatePodcastData(final Playback playback) {
        this.getCurrEpisode().setTimeWatched(playback.getTimeWatched());
    }

    @Override
    public void nextTrack(final Playback playback) {
        this.goToNextEpisode(playback);

        playback.setTimeWatched(0);
        updatePodcastData(playback);

        playback.setLastInteracted(MusicPlayer.getTimestamp());
    }

    @Override
    public void prevTrack(final Playback playback) {
    }

    /**
     * Function that updates the current episode to the next one.
     * @param playback      current playback
     */
    public void goToNextEpisode(final Playback playback) {
        this.indexEpisode++;
        if (this.indexEpisode == episodes.size()) {
            if (playback.getRepeat() != 0) {
                indexEpisode = 0;
            }
        }
        if (indexEpisode >= episodes.size()) {
            playback.setCurrTrack(null);
            playback.setPlayPause(false);
        }
    }

    @Override
    public int getTimeRemained(final Playback playback) {
        return playback.getQueueEpisodes().get(playback.getIndexInQueue()).getDuration()
                - playback.getTimeWatched();
    }

    /**
     * Function that gets the current track.
     * @param playback
     * @return
     */
    public String getTrack(final Playback playback) {
        /* Request update from playback. */
        if (playback.getCurrTrack() == null) {
            return "";
        }
        return playback.getQueueEpisodes().get(playback.getIndexInQueue()).getName();
    }

     /**
      * Function that skips 90 seconds forward current episode.
      * @param playback     the playback
      * @return             the status of command
      */
    public String forward(final Playback playback) {
        String successFORWARD = "Skipped forward successfully.";
        final int forwardTime = 90;
        if (getTimeRemained(playback) < forwardTime) {
            goToNextEpisode(playback);
            updatePodcastData(playback);
        } else {
            playback.setTimeWatched(playback.getTimeWatched() + forwardTime);
        }
        updatePodcastData(playback);
        playback.setLastInteracted(MusicPlayer.getTimestamp());
        return successFORWARD;
    }

    /**
     * Function that rewound an episode with 90 seconds back.
     * @param playback      the playback
     * @return              the status of command
     */
    public String backward(final Playback playback) {
        String successBACKWARD = "Rewound successfully.";
        final int backwardTime = 90;
        if (playback.getTimeWatched() >= backwardTime) {
            playback.setTimeWatched(playback.getTimeWatched() - backwardTime);
        } else {
            playback.setTimeWatched(0);
        }
        updatePodcastData(playback);
        playback.setLastInteracted(MusicPlayer.getTimestamp());
        return successBACKWARD;
    }

    @Override
    public void updatePlays(final Playback playback) {
        getCurrEpisode().updatePlays(playback);
    }
}
