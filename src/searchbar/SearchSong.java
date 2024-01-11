package searchbar;

import java.util.ArrayList;

import database.Song;
import lombok.Getter;
import musicplayer.AudioCollection;

public final class SearchSong extends Search {
    @Getter
    private ArrayList<Song> results;

    public void setResults(final ArrayList<Song> results) {
        this.results = results;
    }

    /**
     * Function that search for songs with specific filters
     * @param songs             where to search songs
     * @param myfilters         given filters by user
     */
    public void searchTrack(final ArrayList<Song> songs, final Filters myfilters) {
        results = new ArrayList<>();

        for (Song song : songs) {
            /* Check name of song. */
            boolean goalsong = song.getName() == null || myfilters.getName() == null
                    || song.getName().toLowerCase().startsWith((myfilters.getName().toLowerCase()));

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

    /**
     * Function that returns a song from the results.
     * @param selectIndex        the index of the song
     * @return                   the song as super class
     */
    public AudioCollection getSelect(final int selectIndex) {
        if (selectIndex >= results.size()) {
            return null;
        }

        return results.get(selectIndex);
    }
}
