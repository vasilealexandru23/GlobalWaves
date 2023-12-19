package database;

public final class Episode {
    private String name;
    private Integer duration;
    private String description;
    private int timeWatched;

    public Episode() {
    }

    public Episode(final String name, final Integer duration, final String description) {
        this.name = name;
        this.duration = duration;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public int getTimeWatched() {
        return timeWatched;
    }

    public void setTimeWatched(final int time) {
        this.timeWatched = time;
    }
}
