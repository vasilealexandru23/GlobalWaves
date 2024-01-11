package recommendations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import database.Playlist;
import database.Song;
import musicplayer.AudioCollection;
import pages.HomePage;
import users.UserNormal;

public final class RandomPlaylistConcrete implements RecommendationStrategy {
    @Override
    public String createRecommendation(final UserNormal user) {
        /* Get top 3 genres. */
        ArrayList<String> topGenres = new ArrayList<>();
        HashMap<String, Integer> genres = new HashMap<>();
        ArrayList<Song> allSongs = new ArrayList<>();

        /* Add songs from liked songs. */
        for (Song song : user.getMusicPlayer().getLikedSongs()) {
            if (!allSongs.contains(song)) {
                allSongs.add(song);
            }
            if (genres.containsKey(song.getGenre())) {
                genres.put(song.getGenre(), genres.get(song.getGenre()) + 1);
            } else {
                genres.put(song.getGenre(), 1);
                topGenres.add(song.getGenre());
            }
        }

        /* Add songs from created playlists. */
        for (Playlist playlist : user.getMusicPlayer().getPlaylists()) {
            for (Song song : playlist.getSongs()) {
                if (!allSongs.contains(song)) {
                    allSongs.add(song);
                }
                if (genres.containsKey(song.getGenre())) {
                    genres.put(song.getGenre(), genres.get(song.getGenre()) + 1);
                } else {
                    genres.put(song.getGenre(), 1);
                    topGenres.add(song.getGenre());
                }
            }
        }

        /* Add songs from followed playlists. */
        for (Playlist playlist : user.getMusicPlayer().getFollowedPlaylists()) {
            for (Song song : playlist.getSongs()) {
                if (!allSongs.contains(song)) {
                    allSongs.add(song);
                }
                if (genres.containsKey(song.getGenre())) {
                    genres.put(song.getGenre(), genres.get(song.getGenre()) + 1);
                } else {
                    genres.put(song.getGenre(), 1);
                    topGenres.add(song.getGenre());
                }
            }
        }

        /* Sort genres. */
        topGenres.sort(new Comparator<String>() {
            @Override
            public int compare(final String genre1, final String genre2) {
                return genres.get(genre2) - genres.get(genre1);
            }
        });

        final int top3GENRES = 3;
        final int songsINST = 5;
        final int songsINND = 3;
        final int songsINRD = 2;
        ArrayList<Song> songsForPlaylist = new ArrayList<>();
        for (int iter = 0; iter < top3GENRES && iter < topGenres.size(); ++iter) {
            String genre = topGenres.get(iter);
            ArrayList<Song> songsByGenre = new ArrayList<>();
            int songs = 0;
            if (iter == 0) {
                songs = songsINST;
            } else if (iter == 1) {
                songs = songsINND;
            } else {
                songs = songsINRD;
            }
            for (Song song : allSongs) {
                if (song.getGenre().equals(genre)) {
                    songsByGenre.add(song);
                }
            }

            songsByGenre.sort(new Comparator<Song>() {
                @Override
                public int compare(final Song song1, final Song song2) {
                    return song2.getNrLikes() - song1.getNrLikes();
                }
            });

            for (int iter2 = 0; iter2 < songs && iter2 < songsByGenre.size(); ++iter2) {
                songsForPlaylist.add(songsByGenre.get(iter2).dupSong());
            }
        }

        if (songsForPlaylist.size() == 0) {
            return "No new recommendations were found";
        }

        Playlist newPlaylist = new Playlist(user.getUsername()
                + "'s recommendations", user.getUsername());
        newPlaylist.getSongs().addAll(songsForPlaylist);
        newPlaylist.setType(AudioCollection.AudioType.PLAYLIST);
        ((HomePage) user.getPages().get(0)).getRecommendations().add(newPlaylist);

        return "The recommendations for user "
                + user.getUsername()
                + " have been updated successfully.";
    }
}
