package searchbar;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public final class Filters {
    private String name;
    private String owner;
    private String duration;
    private String album;
    private String artist;
    private String lyrics;
    private String genre;
    private String releaseYear;
    private ArrayList<String> tags;

    public Filters() {
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public void setAlbum(final String album) {
        this.album = album;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setReleaseYear(final String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setArtist(final String artist) {
        this.artist = artist;
    }

    public void setTags(final ArrayList<String> tags) {
        this.tags = tags;
    }

    public void setLyrics(final String lyrics) {
        this.lyrics = lyrics;
    }

    public void setGenre(final String genre) {
        this.genre = genre;
    }

    public void setDuration(final String duration) {
        this.duration = duration;
    }
}
