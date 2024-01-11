package pages;

import java.util.ArrayList;

import database.Playlist;
import database.Song;
import lombok.Getter;
import musicplayer.AudioCollection;
import musicplayer.MusicPlayer;
import users.UserNormal;

public final class HomePage extends UserPage {
    private MusicPlayer musicPlayer;

    @Getter
    private ArrayList<AudioCollection> recommendations = new ArrayList<AudioCollection>();

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

        /* Add song recommendations. */
        result += "\n\nSong recommendations:\n\t[";

        boolean firstSong = true;
        for (AudioCollection audio : recommendations) {
            if (audio.getType() == AudioCollection.AudioType.SONG) {
                Song song = (Song) audio;
                if (firstSong) {
                    result += song.getName();
                    firstSong = false;
                } else {
                    result += ", " + song.getName();
                }
            }
        }

        result += "]";

        /* Add playlists recommendations. */
        result += "\n\nPlaylists recommendations:\n\t[";

        boolean firstPlaylistRecommendation = true;
        for (AudioCollection audio : recommendations) {
            if (audio.getType() == AudioCollection.AudioType.PLAYLIST) {
                Playlist playlist = (Playlist) audio;
                if (firstPlaylistRecommendation) {
                    result += playlist.getName();
                    firstPlaylistRecommendation = false;
                } else {
                    result += ", " + playlist.getName();
                }
            }
        }

        result += "]";

        return result;
    }

    @Override
    public String subscribe(final UserNormal user) {
        return "You can subscribe only to artists and hosts.";
    }
}
