package pages;

import java.util.ArrayList;

import database.Playlist;
import database.Song;
import musicplayer.MusicPlayer;

public final class HomePage extends UserPage {
    private MusicPlayer musicPlayer;

    public HomePage(final MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }

    @Override
    public String printCurrentPage() {
           String result = "";
        result += "Liked songs:\n\t[";

        /* Get top 5 songs liked. */
        ArrayList<Song> topSongs = new ArrayList<>();
        topSongs.addAll(musicPlayer.getLikedSongs());

        /* Sort songs by likes. */
        topSongs.sort((Song song1, Song song2) -> song2.getNrLikes() - song1.getNrLikes());

        /* Add liked songs. */
        int songCounter = 0;
        for (Song song : topSongs) {
            if (songCounter == maxResult) {
                break;
            }
            if (songCounter == 0) {
                result += song.getName();
            } else {
                result += ", " + song.getName();
            }
            songCounter++;
        }

        result += "]\n\nFollowed playlists:\n\t[";

        /* Add followed playlists. */
        boolean firstPlaylist = true;
        for (Playlist playlist : musicPlayer.getFollowedPlaylists()) {
            if (firstPlaylist) {
                result += playlist.getName();
                firstPlaylist = false;
            } else {
                result += ", " + playlist.getName();
            }
        }

        result += "]";

        return result;
    }
}
