package musicplayer;

import lombok.Getter;

@Getter
public abstract class AudioCollection {
    public enum AudioType {
        SONG,
        PODCAST,
        PLAYLIST,
        ALBUM
    }

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
     * some type of track (song, podcast, playlist) is running.
     * @param playback      current playback
     */
    public abstract void checkTrack(Playback playback);

    /**
     * Function that returns the name of the track.
     * @return        name of the track
     */
    public abstract String getName();

    /**
     * Function that returns the current playing track's name.
     * @param playback      current playback
     * @return              name of the track
     */
    public abstract String getTrack(Playback playback);

    /**
     * Function that returns the time remained of a track.
     * @param playback      current playback
     * @return              time remained of the track
     */
    public abstract int getTimeRemained(Playback playback);

    /**
     * Function that plays the next track.
     * @param playback      current playback
     */
    public abstract void nextTrack(Playback playback);

    /**
     * Function that plays the previous track.
     * @param playback      current playback
     */
    public abstract void prevTrack(Playback playback);

    /**
     * Function that returns the owner of the track.
     * @return
     */
    public abstract String getOwner();
}
