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
    protected int plays;

    /**
     * Function that sets the type of the track.
     * @param type      type of the track
     */
    public void setType(final AudioType type) {
        this.type = type;
    }

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
     * @return          owner of the track
     */
    public abstract String getOwner();

    /**
     * Function that returns the number of plays of the track.
     * @return          number of plays of the track
     */
    public int getPlays() {
        return plays;
    }

    /**
     * Function that updates the number of plays of the track.
     */
    public abstract void updatePlays(Playback playback);
}
