package recommendations;

import java.util.ArrayList;
import java.util.Comparator;

import database.MyDatabase;
import database.Playlist;
import database.Song;
import musicplayer.AudioCollection;
import pages.HomePage;
import users.UserArtist;
import users.UserNormal;

public final class FansPlaylistConcrete implements RecommendationStrategy {
    @Override
    public String createRecommendation(final UserNormal user) {
        final int nrFans = 5;
        Song currentSong = (Song) user.getMusicPlayer().getPlayback().getCurrTrack();

        UserArtist artist = (UserArtist) MyDatabase
                .getInstance()
                .findMyUser(currentSong.getArtist());

        ArrayList<UserNormal> topFans = artist.getTopFans();
        ArrayList<Song> songsForPlaylist = new ArrayList<Song>();

        for (int iter = 0; iter < topFans.size() && iter < nrFans; ++iter) {
            UserNormal fan = topFans.get(iter);
            ArrayList<Song> likedSongs = fan.getMusicPlayer().getLikedSongs();

            likedSongs.sort(new Comparator<Song>() {
                @Override
                public int compare(final Song song1, final Song song2) {
                    return song2.getNrLikes() - song1.getNrLikes();
                }
            });

            for (int iter2 = 0; iter2 < likedSongs.size() && iter2 < nrFans; ++iter2) {
                songsForPlaylist.add(likedSongs.get(iter2));
            }
        }

        if (songsForPlaylist.size() == 0) {
            return "No new recommendations were found";
        }

        Playlist newPlaylist = new Playlist(currentSong.getArtist()
                + " Fan Club recommendations", user.getUsername());

        newPlaylist.setType(AudioCollection.AudioType.PLAYLIST);
        for (Song song : songsForPlaylist) {
            newPlaylist.getSongs().add(song.dupSong());
        }

        ((HomePage) user.getPages().get(0)).getRecommendations().add(newPlaylist);
        return "The recommendations for user "
                + user.getUsername()
                + " have been updated successfully.";
    }

}
