package pages;

import database.Playlist;
import database.Song;
import musicplayer.MusicPlayer;

public final class LikedContentPage extends UserPage {
    private MusicPlayer musicPlayer;

    public LikedContentPage(final MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }

    @Override
    public String printCurrentPage() {
        String result = "";
        result += "Liked songs:\n\t[";

        /* Add liked songs. */
        boolean firstSong = true;
        for (Song song : musicPlayer.getLikedSongs()) {
            if (firstSong) {
                result += song.getName() + " - " + song.getArtist();
                firstSong = false;
            } else {
                result += ", " + song.getName() + " - " + song.getArtist();
            }
        }

        result += "]\n\nFollowed playlists:\n\t[";

        /* Add followed playlists. */
        boolean firstPlaylist = true;
        for (Playlist playlist : musicPlayer.getFollowedPlaylists()) {
            if (firstPlaylist) {
                result += playlist.getName() + " - " + playlist.getOwner();
                firstPlaylist = false;
            } else {
                result += ", " + playlist.getName() + " - " + playlist.getOwner();
            }
        }

        result += "]";

        return result;
    }
}
