package recommendations;

import java.util.ArrayList;
import java.util.Random;

import database.MyDatabase;
import database.Song;
import musicplayer.AudioCollection;
import musicplayer.Playback;
import pages.HomePage;
import users.UserNormal;

public final class RandomSongConcrete implements RecommendationStrategy {
    @Override
    public String createRecommendation(final UserNormal user) {
        final int limit = 30;

        Playback playback = user.getMusicPlayer().getPlayback();
        Song currentSong = (Song) playback.getCurrTrack();

        if (playback.getTimeWatched() >= limit) {
            String genre = currentSong.getGenre();
            ArrayList<Song> songsByGenre = new ArrayList<>();

            /* Add all songs with specific genre. */
            for (Song song : MyDatabase.getInstance().getAllSongsCreated()) {
                if (song.getGenre().equals(genre)) {
                    songsByGenre.add(song);
                }
            }

            /* Get random song with seed equals to timeWatched. */
            Random random = new Random(playback.getTimeWatched());
            int randomIndex = random.nextInt(songsByGenre.size());
            Song randomSong = songsByGenre.get(randomIndex).dupSong();
            randomSong.setType(AudioCollection.AudioType.SONG);
            ((HomePage) user.getPages().get(0)).getRecommendations().add(randomSong);

            return "The recommendations for user "
                    + user.getUsername()
                    + " have been updated successfully.";
        }
        return "No new recommendations were found";
    }
}
