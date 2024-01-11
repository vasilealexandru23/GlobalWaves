package users;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import database.Album;
import database.Episode;
import database.MyDatabase;
import database.Playlist;
import database.Podcast;
import database.Song;
import lombok.Getter;
import musicplayer.AudioCollection.AudioType;
import pages.ArtistPage;
import pages.HomePage;
import pages.LikedContentPage;
import pages.UserPage;
import searchbar.Search;
import searchbar.SearchArtist;
import searchbar.SearchHost;
import musicplayer.AudioCollection;
import musicplayer.MusicPlayer;
import notifications.Notification;
import notifications.Observator;

public final class UserNormal extends UserTypes implements Observator {
    @Getter
    private MusicPlayer musicPlayer;
    @Getter
    private boolean online;

    @Getter
    private ArrayList<UserPage> pages;

    private int indexPage;
    @Getter
    private Search lastSearch;
    @Getter
    private Integer lastSelect;
    @Getter
    private UserPage selectedPage;

    private ArrayList<Notification> notifications = new ArrayList<Notification>();
    private ArrayList<ArtistPage.Merch> myMerch = new ArrayList<>();
    @Getter
    private boolean premium;
    @Getter
    private ArrayList<Song> historySongs = new ArrayList<Song>();
    @Getter
    private ArrayList<Song> historySongsPremium = new ArrayList<Song>();
    @Getter
    private ArrayList<Song> allSongsPlayed = new ArrayList<Song>();
    @Getter
    private ArrayList<UserPage> historyPages = new ArrayList<UserPage>();

    private final double maxCREDIT = 1000000.0;

    public void setLastSearch(final Search lastSearch) {
        this.lastSearch = lastSearch;
    }

    public void setLastSelect(final int lastSelect) {
        this.lastSelect = lastSelect;
    }

    public void setSelectedPage(final UserPage selectedPage) {
        this.selectedPage = selectedPage;
    }

    /**
     * Function that selects a track by last search.
     * @param mySelect the index in search result
     * @return command status
     */
    public String select(final int mySelect) {
        String noSEARCH = "Please conduct a search before making a selection.";
        String badID = "The selected ID is too high.";
        String successSELECT = "Successfully selected ";
        /* Check for search. */
        if (lastSearch == null) {
            return noSEARCH;
        }

        if (lastSearch.getType().equals("artist")) {
            UserArtist artist = ((SearchArtist) lastSearch).getArtist(mySelect);
            if (artist == null) {
                return badID;
            }

            selectedPage = artist.getArtistPage();
            lastSelect = mySelect;
            return "Successfully selected " + artist.getUsername() + "'s page.";
        }

        if (lastSearch.getType().equals("host")) {
            UserHost host = ((SearchHost) lastSearch).getHost(mySelect);
            if (host == null) {
                return badID;
            }

            selectedPage = host.getHostPage();
            lastSelect = mySelect;
            return "Successfully selected " + host.getUsername() + "'s page.";
        }

        String track = null;
        this.musicPlayer.setSelectedTrack(null);

        this.musicPlayer.setSelectedTrack(lastSearch.getSelect(mySelect));

        if (this.musicPlayer.getSelectedTrack() == null) {
            return badID;
        }

        track = this.musicPlayer.getSelectedTrack().getName();

        lastSelect = mySelect;
        return successSELECT + track + ".";
    }

    public UserNormal(final String username, final int age, final String city) {
        super(username, age, city);
        setUserType(UserType.USER);
        this.online = true;
        musicPlayer = new MusicPlayer(this);
        pages = new ArrayList<UserPage>();

        /* Add Home page. */
        pages.add(new HomePage(musicPlayer));

        /* Add LikedContent page. */
        pages.add(new LikedContentPage(musicPlayer));
        this.premium = false;
    }

    public void setOnline(final boolean online) {
        this.online = online;
    }

    public void setIndexPage(final int indexPage) {
        this.indexPage = indexPage;
    }

    public void setPremium(final boolean premium) {
        this.premium = premium;
    }

    /**
     * Function that change the current page.
     * @param nextPage The page to be changed to.
     * @return The status of the command.
     */
    public String changePage(final String nextPage) {
        if (nextPage.equals("Home")) {
            selectedPage = null;
            historyPages.add(pages.get(0));
            indexPage = historyPages.size() - 1;
            return getUsername() + " accessed Home successfully.";
        } else if (nextPage.equals("LikedContent")) {
            selectedPage = null;
            historyPages.add(pages.get(1));
            indexPage = historyPages.size() - 1;
            return getUsername() + " accessed LikedContent successfully.";
        }

        if (musicPlayer.getPlayback().getCurrTrack() == null) {
            return getUsername() + " is trying to access a non-existent page.";
        }

        if (nextPage.equals("Artist")) {
            Song currentSong = (Song) musicPlayer.getPlayback().getCurrTrack();
            UserArtist artist = (UserArtist) MyDatabase
                    .getInstance()
                    .findMyUser(currentSong.getArtist());
            historyPages.add(artist.getArtistPage());
            indexPage = historyPages.size() - 1;
            return getUsername() + " accessed Artist successfully.";
        } else if (nextPage.equals("Host")) {
            Episode currentEpisode = (Episode) musicPlayer.getPlayback().getCurrTrack();
            UserHost host = (UserHost) MyDatabase
                    .getInstance()
                    .findMyUser(currentEpisode.getOwner());
            historyPages.add(host.getHostPage());
            indexPage = historyPages.size() - 1;
            return getUsername() + " accessed Host successfully.";
        }

        return getUsername() + " is trying to access a non-existent page.";
    }

    /**
     * Function that puts in output the preferred songs.
     * @param output the output object
     */
    public void showPreferredSongs(final ArrayNode output) {
        for (Song song : musicPlayer.getLikedSongs()) {
            output.add(song.getName());
        }
    }

    /**
     * Function that puts in output the owned playlists.
     * @param output the output object
     */
    public void showPlaylists(final ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();
        for (Playlist playlist : musicPlayer.getPlaylists()) {
            ObjectNode showPlaylist = objectMapper.createObjectNode();
            ArrayList<String> songNames = playlist.getSongNames();
            ArrayNode songsOut = objectMapper.createArrayNode();

            for (String song : songNames) {
                songsOut.add(song);
            }

            showPlaylist.put("name", playlist.getName());
            showPlaylist.put("songs", songsOut);
            showPlaylist.put("visibility", playlist.checkVisibility());
            showPlaylist.put("followers", playlist.getFollowers());

            output.add(showPlaylist);
        }
    }

    /**
     * Function that changes the current user's status.
     */
    public void switchConnectionStatus() {
        this.online = !this.online;
        if (!this.online) {
            if (musicPlayer.getPlayback() != null) {
                musicPlayer.getPlayback().stopPlayback();
            }
        } else {
            if (musicPlayer.getPlayback() != null) {
                musicPlayer.getPlayback().startPlayback();
            }
        }
    }

    @Override
    public String printCurrentPage() {
        if (!isOnline()) {
            return getUsername() + " is offline.";
        }
        if (selectedPage != null) {
            return selectedPage.printPage();
        }
        if (historyPages.size() == 0) {
            return pages.get(0).printPage();
        }
        return historyPages.get(indexPage).printPage();
    }

    @Override
    public boolean canBeRemoved(final MyDatabase database) {
        /* Check if other users interact with user's things. */
        for (UserTypes user : database.getAllUsersCreated()) {
            if (user.getUserType() != UserTypes.UserType.USER) {
                continue;
            }

            UserNormal myUser = (UserNormal) user;
            if (myUser == this) {
                continue;
            }

            if (myUser.getMusicPlayer().getPlayback() == null) {
                continue;
            }

            myUser.getMusicPlayer().getPlayback().checkPlayback();
            AudioCollection currTrack = myUser.getMusicPlayer().getPlayback().getCurrTrack();
            if (currTrack == null) {
                continue;
            }

            if (currTrack.getType().equals(AudioType.PLAYLIST)) {
                Playlist currPlaylist = (Playlist) currTrack;
                if (currPlaylist.getOwner().equals(this.getUsername())) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String removeUser(final MyDatabase database) {
        if (!canBeRemoved(database)) {
            return getUsername() + " can't be deleted.";
        }
        for (UserTypes user : database.getAllUsersCreated()) {
            if (user.getUserType() != UserType.USER) {
                continue;
            }

            UserNormal myUser = (UserNormal) user;

            if (myUser.getMusicPlayer().getPlayback() == null) {
                continue;
            }

            /*
             * For every playlist, iterate over all normal users and erase from followed
             * playlist.
             */
            if (myUser.getMusicPlayer().getFollowedPlaylists() != null) {
                ArrayList<Playlist> toDelete = new ArrayList<>();
                for (Playlist playlist : myUser.getMusicPlayer().getFollowedPlaylists()) {
                    /*
                     * From this user's followed playlists remove the ones who have owner toDelete
                     * user.
                     */
                    if (playlist.getOwner().equals(this.getUsername())) {
                        toDelete.add(playlist);
                    }
                }
                myUser.getMusicPlayer().getFollowedPlaylists().removeAll(toDelete);
            }
        }

        for (Playlist playlist : this.musicPlayer.getPlaylists()) {
            database.getAllPlaylistsCreated().remove(playlist);
        }

        /* For every liked song by user, decrement the number of likes. */
        for (Song song : this.getMusicPlayer().getLikedSongs()) {
            song.setNrLikes(song.getNrLikes() - 1);
        }

        /* For every followed playlist by user, decrement the number of followers. */
        for (Playlist playlist : this.getMusicPlayer().getFollowedPlaylists()) {
            playlist.setFollowers(playlist.getFollowers() - 1);
        }

        database.getAllUsersCreated().remove(this);

        return getUsername() + " was successfully deleted.";
    }

    /**
     * Function that adds a song to the history.
     * @param song          the song
     */
    public void addToHistory(final Song song) {
        if (premium) {
            historySongsPremium.add(song);
        } else {
            historySongs.add(song);
        }
    }

    /**
     * Function that computes the number of
     * plays by the current user of an artist's song.
     * @param               artist
     * @return              number of plays
     */
    public int getPlaysArtist(final UserArtist artist) {
        int plays = 0;
        for (Song song : historySongs) {
            if (song.getOwner().equals(artist.getUsername())) {
                plays++;
            }
        }
        for (Song song : historySongsPremium) {
            if (song.getOwner().equals(artist.getUsername())) {
                plays++;
            }
        }
        for (Song song : allSongsPlayed) {
            if (song.getOwner().equals(artist.getUsername())) {
                plays++;
            }
        }
        return plays;
    }

    /**
     * Function that computes the number of
     * plays by the current user of a host's episode.
     * @param host          the host
     * @return              number of plays
     */
    public int getPlaysHost(final UserHost host) {
        int plays = 0;
        for (Podcast podcast : this.getMusicPlayer().getPodcasts()) {
            for (Episode episode : podcast.getEpisodes()) {
                plays += episode.getPlays();
            }
        }
        return plays;
    }

    /**
     * Function that pays the artists as a premium account.
     */
    public void payPremium() {
        for (UserTypes user : MyDatabase.getInstance().getAllUsersCreated()) {
            if (user.getUserType() == UserTypes.UserType.ARTIST) {
                UserArtist artist = (UserArtist) user;
                for (Song song : this.historySongsPremium) {
                    if (!song.getArtist().equals(artist.getUsername())) {
                        continue;
                    }
                    if (artist.getSongsRevenue().containsKey(song.getName())) {
                        artist.getSongsRevenue().put(song.getName(),
                                artist.getSongsRevenue().get(song.getName())
                                        + maxCREDIT / (this.historySongsPremium.size()));
                    } else {
                        artist.getSongsRevenue().put(song.getName(),
                            maxCREDIT / (this.historySongsPremium.size()));
                    }
                }
            }
        }
    }

    /**
     * Function that pays the artists as a free account.
     */
    public void payFreeAccount() {
        ArrayList<Song> songsBetweenAds = new ArrayList<>();
        for (int iter = 0; iter < this.historySongs.size(); ++iter) {
            Song song = this.historySongs.get(iter);
            if (song.getName().equals("Ad Break")) {
                for (UserTypes userArtist : MyDatabase.getInstance().getAllUsersCreated()) {
                    if (userArtist.getUserType() == UserTypes.UserType.ARTIST) {
                        UserArtist myArtist = (UserArtist) userArtist;

                        for (Song songBetweenAds : songsBetweenAds) {
                            if (!songBetweenAds.getArtist().equals(myArtist.getUsername())) {
                                continue;
                            }
                            if (myArtist.getSongsRevenue().containsKey(songBetweenAds.getName())) {
                                myArtist.getSongsRevenue().put(songBetweenAds.getName(),
                                        myArtist.getSongsRevenue().get(songBetweenAds.getName())
                                        + (song.getPrice() * 1.0) / songsBetweenAds.size());
                            } else {
                                myArtist.getSongsRevenue().put(songBetweenAds.getName(),
                                        (song.getPrice() * 1.0) / songsBetweenAds.size());
                            }
                        }
                    }
                }
                songsBetweenAds.clear();
            } else {
                songsBetweenAds.add(song);
            }
        }
    }

    /**
     * Function that gets top listened artists.
     * @return          top listened artists
     */
    private ArrayList<UserArtist> topArtists() {
        ArrayList<UserArtist> artists = new ArrayList<>();
        for (UserTypes user : MyDatabase.getInstance().getAllUsersCreated()) {
            if (user.getUserType() == UserType.ARTIST
                && ((UserArtist) user).getAllPlays(this) != 0) {
                artists.add((UserArtist) user);
            }
        }

        artists.sort((artist1, artist2) -> {
            int plays1 = getPlaysArtist(artist1);
            int plays2 = getPlaysArtist(artist2);
            if (plays1 == plays2) {
                return artist1.getUsername().compareTo(artist2.getUsername());
            }
            return plays2 - plays1;
        });

        return artists;
    }

    /**
     * Functin that computes the top songs.
     * @param allHistory        all the history
     * @return                  the top songs
     */
    private ArrayList<Song> topSongs(final ArrayList<Song> allHistory) {
        ArrayList<Song> topSongs = new ArrayList<Song>() {
            @Override
            public boolean contains(final Object o) {
                Song song = (Song) o;
                for (Song s : this) {
                    if (s.getName().equals(song.getName())) {
                        return true;
                    }
                }
                return false;
            }
        };

        for (Song song : allHistory) {
            if (!topSongs.contains(song) && !song.getName().equals("Ad Break")) {
                topSongs.add(song);
            }
        }

        topSongs.sort((song1, song2) -> {
            int plays1 = (int) allHistory.stream().filter(
                song -> song1.getName().equals(song.getName())).count();
            int plays2 = (int) allHistory.stream().filter(
                song -> song2.getName().equals(song.getName())).count();

            if (plays1 == plays2) {
                return song1.getName().compareTo(song2.getName());
            }
            return plays2 - plays1;
        });

        return topSongs;
    }

    private ArrayList<String> topGenres(final ArrayList<Song> allHistory) {
        ArrayList<String> genres = new ArrayList<>();
        for (Song song: allHistory) {
            if (!genres.contains(song.getGenre())
                && !song.getName().equals("Ad Break")) {
                genres.add(song.getGenre());
            }
        }

        genres.sort((genre1, genre2) -> {
            int plays1 = (int) allHistory.stream().filter(
                song -> song.getGenre().equals(genre1)).count();
            int plays2 = (int) allHistory.stream().filter(
                song -> song.getGenre().equals(genre2)).count();
            if (plays1 == plays2) {
                return genre1.compareTo(genre2);
            }

            return plays2 - plays1;
        });

        return genres;
    }

    /**
     * Function that computes the top listened albums.
     * @return                  the top albums
     */
    private ArrayList<Album> topAlbums() {
        ArrayList<Album> albums = new ArrayList<>();
        for (Album album : MyDatabase.getInstance().getAllAlbumsCreated()) {
            boolean found = false;
            for (Album album1 : albums) {
                if (album1.getName().equals(album.getName())) {
                    found = true;
                    break;
                }
            }
            if (!found && album.getPlays(this) != 0) {
                albums.add(album);
            }
        }

        albums.sort((album1, album2) -> {
            int plays1 = album1.getPlays(this);
            int plays2 = album2.getPlays(this);
            if (plays1 == plays2) {
                return album1.getName().compareTo(album2.getName());
            }
            return plays2 - plays1;
        });
        return albums;
    }

    /**
     * Function that computes the top listened episodes.
     * @return                  the top episodes
     */
    private ArrayList<Episode> topEpisodes() {
        ArrayList<Episode> episodes = new ArrayList<Episode>();

        for (Podcast podcast : this.getMusicPlayer().getPodcasts()) {
            for (Episode episode : podcast.getEpisodes()) {
                if (episode.getPlays() != 0) {
                    episodes.add(episode);
                }
            }
        }

        episodes.sort((episode1, episode2) -> {
            int plays1 = episode1.getPlays();
            int plays2 = episode2.getPlays();

            if (plays1 == plays2) {
                return episode1.getName().compareTo(episode2.getName());
            }
            return plays2 - plays1;
        });

        return episodes;
    }

    @Override
    public ObjectNode getStatistics() {
        ObjectNode stats = new ObjectMapper().createObjectNode();
        ObjectNode topArtists = new ObjectMapper().createObjectNode();
        ObjectNode topAlbums = new ObjectMapper().createObjectNode();
        ObjectNode topSongs = new ObjectMapper().createObjectNode();
        ObjectNode topGenres = new ObjectMapper().createObjectNode();
        ObjectNode topPodcasts = new ObjectMapper().createObjectNode();

        final int maxTOP = 5;

        /* Get all history in an array. */
        ArrayList<Song> allHistory = new ArrayList<>();
        allHistory.addAll(this.getHistorySongs());
        allHistory.addAll(this.getHistorySongsPremium());
        allHistory.addAll(this.getAllSongsPlayed());

        /* Get top artists. */
        topArtists().stream().limit(maxTOP)
                .forEach(artist -> topArtists.put(artist.getUsername(), getPlaysArtist(artist)));

        /* Get top songs. */
        topSongs(allHistory).stream().limit(maxTOP)
                .forEach(song -> topSongs.put(song.getName(), (int) allHistory.stream().filter(
                    song1 -> song1.getName().equals(song.getName())).count()));

        /* Get top genres. */
        topGenres(allHistory).stream().limit(maxTOP)
                .forEach(genre -> topGenres.put(genre, (int) allHistory.stream().filter(
                    song -> song.getGenre().equals(genre)).count()));

        /* Get top albums. */
        topAlbums().stream().limit(maxTOP)
                .forEach(album -> topAlbums.put(album.getName(), album.getPlays(this)));

        /* Get top episodes. */
        topEpisodes().stream().limit(maxTOP)
                .forEach(episode -> topPodcasts.put(episode.getName(), episode.getPlays()));

        if (topArtists.isEmpty()
            && topAlbums.isEmpty()
            && topSongs.isEmpty()
            && topGenres.isEmpty()
            && topPodcasts.isEmpty()) {
                return null;
        }

        stats.put("topArtists", topArtists);
        stats.put("topGenres", topGenres);
        stats.put("topSongs", topSongs);
        stats.put("topAlbums", topAlbums);
        stats.put("topEpisodes", topPodcasts);
        return stats;
    }

    /**
     * Function that buys a merch from selected page.
     * @param merchName         the merch name
     * @return                  the status of the command
     */
    public String buyMerch(final String merchName) {
        if (selectedPage == null) {
            return "You must select a page first.";
        }

        return selectedPage.buyMerch(this, merchName);
    }

    /**
     * Function that adds a merch in the user's list.
     * @param merch             the merch
     */
    public void addMerch(final ArtistPage.Merch merch) {
        myMerch.add(merch);
    }

    /**
     * Function that shows all the merch bought.
     * @return                 all merches bought
     */
    public ArrayNode seeMerch() {
        ArrayNode output = new ObjectMapper().createArrayNode();

        for (ArtistPage.Merch merch : myMerch) {
            output.add(merch.getName());
        }

        return output;
    }

    /**
     * Function that shows
     * the notifications of the user.
     * @return          notifications
     */
    public ArrayNode getNotifications() {
        ArrayNode output = new ObjectMapper().createArrayNode();

        for (Notification notification : notifications) {
            ObjectNode status = new ObjectMapper().createObjectNode();
            status.put("name", notification.getName());
            status.put("description", notification.getDescription());
            output.add(status);
        }

        notifications.clear();

        return output;
    }

    /**
     * Function that changes the
     * current page to the previous one.
     * @return          navigation status
     */
    public String previousPage() {
        if (indexPage == 0) {
            return "There are no pages left to go back.";
        }
        indexPage--;
        return "The user " + getUsername() + " has navigated successfully to the previous page.";
    }

    /**
     * Function that changes the
     * current page to the next one.
     * @return          navigation status
     */
    public String nextPage() {
        if (indexPage == historyPages.size() - 1) {
            return "There are no pages left to go forward.";
        }
        indexPage++;
        return "The user " + getUsername() + " has navigated successfully to the next page.";
    }

    @Override
    public void updateNotifications(final Notification notification) {
        notifications.add(notification);
    }
}
