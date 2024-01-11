package database;

import users.UserTypes;
import users.UserTypes.UserType;
import lombok.Getter;
import musicplayer.AudioCollection;
import musicplayer.Playback;
import users.UserNormal;

@Getter
public final class Episode extends AudioCollection {
    private String name;
    private String owner;
    private Integer duration;
    private String description;
    private int timeWatched;
    private int plays;

    public Episode() {
    }

    public Episode(final String name, final Integer duration,
        final String description, final String owner) {
        this.name = name;
        this.duration = duration;
        this.description = description;
        this.owner = owner;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setTimeWatched(final int time) {
        this.timeWatched = time;
    }

    public void setPlays(final int plays) {
        this.plays = plays;
    }

    /**
     * Function that gets all the plays of an episode.
     * @return          total plays of the episode
     */
    public int getAllPlays() {
        int totalPlays = 0;
        for (UserTypes user : MyDatabase.getInstance().getAllUsersCreated()) {
            if (user.getUserType() == UserType.USER) {
                for (Podcast podcast : ((UserNormal) user).getMusicPlayer().getPodcasts()) {
                    for (Episode episode : podcast.getEpisodes()) {
                        if (episode.getName().equals(this.name)) {
                            totalPlays += episode.getPlays();
                        }
                    }
                }
            }
        }
        return totalPlays;
    }

    @Override
    public String getTrack(final Playback playback) {
        return name;
    }

    @Override
    public int getTimeRemained(final Playback playback) {
        return duration - playback.getTimeWatched();
    }

    @Override
    public void nextTrack(final Playback playback) {

    }

    @Override
    public void prevTrack(final Playback playback) {

    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public void updatePlays(final Playback playback) {
        this.plays++;
    }
}
