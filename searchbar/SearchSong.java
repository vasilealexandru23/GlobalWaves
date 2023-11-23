package searchbar;

import java.util.ArrayList;

import fileio.input.SongInput;
import lombok.Getter;

public final class SearchSong extends SearchCommand {
    @Getter
    private ArrayList<SongInput> results;

    private final int maxSearch = 5;

    public void setResults(final ArrayList<SongInput> results) {
        this.results = results;
    }

    /**
     * Function that search for songs with specific filters
     * @param songs             where to search songs
     * @param myfilters         given filters by user
     */
    public void searchSongs(final ArrayList<SongInput> songs, final Filters myfilters) {
        results = new ArrayList<>();

        for (SongInput song : songs) {
            /* Check name of song. */
            boolean goalsong = song.getName() == null || myfilters.getName() == null
                    || song.getName().startsWith(myfilters.getName());

            /* Check album of song. */
            if (song.getAlbum() != null && myfilters.getAlbum() != null
                && !song.getAlbum().equals(myfilters.getAlbum())) {
                goalsong = false;
            }

            /* Check by tags. */
            if (song.getTags() != null && myfilters.getTags() != null) {
                ArrayList<String> songtags = song.getTags();
                ArrayList<String> reqtags = myfilters.getTags();
                for (String tag : reqtags) {
                    if (!songtags.contains(tag)) {
                        goalsong = false;
                        break;
                    }
                }
            }

            /* Check by lyrics. */
            if (song.getLyrics() != null && myfilters.getLyrics() != null
                && !song.getLyrics().toLowerCase().contains(myfilters.getLyrics().toLowerCase())) {
                goalsong = false;
            }

            /* Check by genre. */
            if (song.getGenre() != null && myfilters.getGenre() != null
                && !song.getGenre().equalsIgnoreCase(myfilters.getGenre())) {
                goalsong = false;
            }

            /* Check by release year. */
            if (song.getReleaseYear() != 0 && myfilters.getReleaseYear() != null) {
                String reqrelease = myfilters.getReleaseYear();
                int datarelease = Integer.parseInt(
                        String.valueOf(reqrelease.toCharArray(),
                        1, reqrelease.length() - 1));
                if (reqrelease.charAt(0) == '<'
                    && song.getReleaseYear() >= datarelease) {
                    goalsong = false;
                }
                if (reqrelease.charAt(0) == '>'
                    && song.getReleaseYear() <= datarelease) {
                    goalsong = false;
                }
            }

            /* Check by artist. */
            if (song.getArtist() != null && myfilters.getArtist() != null
                && !song.getArtist().equals(myfilters.getArtist())) {
                goalsong = false;
            }

            if (goalsong) {
                results.add(song);
                if (results.size() == maxSearch) {
                    break;
                }
            }

        }
    }
}
