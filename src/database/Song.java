package database;

import java.util.ArrayList;

import fileio.input.SongInput;
import musicplayer.AudioCollection;
import musicplayer.Playback;
import users.UserNormal;

public final class Song extends AudioCollection {
    private String name;
    private Integer duration;
    private String album;
    private ArrayList<String> tags;
    private String lyrics;
    private String genre;
    private Integer releaseYear;
    private String artist;

    /* Field for add. */
    private int price;

    public int getPrice() {
        return price;
    }

    public void setPrice(final int price) {
        this.price = price;
    }

    private int nrLikes;

    public Song() {
    }

    public Song(final SongInput song) {
        this.name = song.getName();
        this.duration = song.getDuration();
        this.album = song.getAlbum();
        this.tags = song.getTags();
        this.lyrics = song.getLyrics();
        this.genre = song.getGenre();
        this.releaseYear = song.getReleaseYear();
        this.artist = song.getArtist();
        this.nrLikes = 0;
        this.price = 0;
        setType(AudioType.SONG);
    }

    /**
     * Function that duplicates a song.
     * @return          return the new song
     */
    public Song dupSong() {
        Song newSong = new Song();
        newSong.name = this.name;
        newSong.duration = this.duration;
        newSong.album = this.album;
        newSong.tags = this.tags;
        newSong.lyrics = this.lyrics;
        newSong.genre = this.genre;
        newSong.releaseYear = this.releaseYear;
        newSong.artist = this.artist;
        newSong.nrLikes = this.nrLikes;
        setType(AudioType.SONG);

        return newSong;
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

    public String getAlbum() {
        return album;
    }

    public void setAlbum(final String album) {
        this.album = album;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(final ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(final String lyrics) {
        this.lyrics = lyrics;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(final String genre) {
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(final int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(final String artist) {
        this.artist = artist;
    }

    public String getOwner() {
        return artist;
    }

    public int getNrLikes() {
        return nrLikes;
    }

    public void setNrLikes(final int nrLikes) {
        this.nrLikes = nrLikes;
    }

    /**
     * Function that returns data from library.getSongs() in another structure.
     * @param inputSongs        songs from library
     * @return                  return the new array with data
     */
    public static ArrayList<Song> initSongsData(final ArrayList<SongInput> inputSongs) {
        ArrayList<Song> mySongs = new ArrayList<>();
        for (SongInput song : inputSongs) {
            Song newSong = new Song(song);
            mySongs.add(newSong);
        }

        return mySongs;
    }

    @Override
    public int getTimeRemained(final Playback playback) {
        return duration - playback.getTimeWatched();
    }

    @Override
    public String getTrack(final Playback playback) {
        return name;
    }

    @Override
    public void nextTrack(final Playback playback) {
        playback.setPlayPause(false);
        playback.setCurrTrack(null);
    }

    @Override
    public void prevTrack(final Playback playback) {
        playback.setPlayPause(false);
        playback.setCurrTrack(null);
    }

    @Override
    public void updatePlays(final Playback playback) {
        UserNormal user = MyDatabase.getInstance().getUserWithPlayback(playback);
        user.addToHistory(this);
        this.plays++;
    }
}
