package musicplayer;

import lombok.Getter;

public class AudioCollection {
    public enum AudioType {
        SONG,
        PODCAST,
        PLAYLIST
    }

    @Getter
    private AudioType type;

    /**
     * Function that sets the type of the track.
     * @param type      type of the track
     */
    public void setType(final AudioType type) {
        this.type = type;
    }

    /**
     * Function that checks for a running playback if
     * some type of track (song, podcast, playlist) is runnig.
     * @param playback      current playback
     */
    public void checkTrack(final Playback playback) {
    }

    /**
     * Function that returns the name of the track.
     * @return
     */
    public String getName() {
        return "";
    }

    /**
     * Function that returns the current playing track's name.
     * @param playback      current playback
     * @return              name of the track
     */
    public String getTrack(final Playback playback) {
        return "";
    }

    /**
     * Function that returns the time remained of a track.
     * @param playback      current playback
     * @return              time remained of the track
     */
    public int getTimeRemained(final Playback playback) {
        return 0;
    }

    /**
     * Function that plays the next track.
     * @param playback      current playback
     */
    public void nextTrack(final Playback playback) {
    }

    /**
     * Function that plays the previous track.
     * @param playback      current playback
     */
    public void prevTrack(final Playback playback) {
    }
}
