package users;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import database.Album;
import database.MyDatabase;
import database.Playlist;
import database.Song;
import lombok.Getter;
import musicplayer.AudioCollection;
import pages.ArtistPage;
import notifications.Notification;

public final class UserArtist extends UserTypes {
    @Getter
    private ArrayList<Album> albums;
    @Getter
    private ArtistPage artistPage;
    @Getter
    private double merchRevenue;

    @Getter
    private ArrayList<UserNormal> subscribers = new ArrayList<UserNormal>();

    @Getter
    private Map<String, Double> songsRevenue = new HashMap<String, Double>();

    public UserArtist(final String username, final int age, final String city) {
        super(username, age, city);
        setUserType(UserType.ARTIST);
        albums = new ArrayList<Album>();
        this.artistPage = new ArtistPage(this, albums);
    }

    /**
     * Function that send a notification to all subscribers.
     * @param notification          notification to be sent
     */
    public void notifySubscribers(final Notification notification) {
        subscribers.forEach(user -> user.updateNotifications(notification));
    }

    /**
     * Function that checks if the artist has an album with the same name.
     * @param albumName name of the album
     * @return true if the artist has an album with the same name
     */
    public boolean checkSameAlbum(final String albumName) {
        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Function that adds a new album to the
     * artist's albums and notifies all subscribers.
     * @param album         album to be added
     */
    public void addAlbum(final Album album) {
        albums.add(album);
        notifySubscribers(new Notification("New Album",
                "New Album from " + getUsername() + "."));
    }

    /**
     * Function that puts in output the artist's albums.
     * @param output
     */
    public void showAlbums(final ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();
        for (Album album : albums) {
            /* Add name. */
            ObjectNode newResult = objectMapper.createObjectNode();
            newResult.put("name", album.getName());

            /* Add songs. */
            ArrayNode songOut = objectMapper.createArrayNode();
            for (Song song : album.getSongs()) {
                songOut.add(song.getName());
            }

            /* Add to ouput. */
            newResult.put("songs", songOut);
            output.add(newResult);
        }
    }

    /**
     * Function that checks if a song exists in the artist's albums.
     * @param songName name of the song
     * @return true if the song exists
     */
    public boolean checkSongExists(final String songName) {
        for (Album album : albums) {
            for (Song song : album.getSongs()) {
                if (song.getName().equals(songName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Function that creates a new event and adds it to the artist's page.
     * @param name        name of event
     * @param date        date of event
     * @param description description of event
     */
    public String addEvent(final String name, final String date, final String description) {
        /* Check if artist already has an event with the same name. */
        if (checkSameEvent(name)) {
            return getUsername() + " has an event with the same name.";
        }

        /* Check if date has format dd-MM-yyyy. */
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date);
        } catch (ParseException e) {
            return "Event for " + getUsername() + " does not have a valid date.";
        }

        ArtistPage.Event event = artistPage.new Event(name, date, description);
        artistPage.getEvents().add(event);

        notifySubscribers(new Notification("New Event",
                "New Event from " + getUsername() + "."));

        return getUsername() + " has added new event successfully.";
    }

    /**
     * Function that removes an event from the artist's page.
     * @param eventName name of event
     * @return status of command
     */
    public String removeEvent(final String eventName) {
        /* Remove event. */
        for (ArtistPage.Event event : artistPage.getEvents()) {
            if (event.getName().equals(eventName)) {
                artistPage.getEvents().remove(event);
                return getUsername() + " deleted the event successfully.";
            }
        }

        return getUsername() + " doesn't have an event with the given name.";
    }

    /**
     * Function that creates a new merch and adds it to the artist's page.
     * @param name        name of merch
     * @param description description of merch
     * @param price       price of merch
     * @return status of command
     */
    public String addMerch(final String name, final String description, final Integer price) {
        /* Check if artist already has an album with the same name. */
        if (checkSameMerch(name)) {
            return getUsername() + " has merchandise with the same name.";
        }

        /* Check if price is valid. */
        if (price < 0) {
            return "Price for merchandise can not be negative.";
        }

        ArtistPage.Merch merch = artistPage.new Merch(name, description, price);
        artistPage.getMerchs().add(merch);

        notifySubscribers(new Notification("New Merchandise",
                "New Merchandise from " + getUsername() + "."));

        return getUsername() + " has added new merchandise successfully.";
    }

    /**
     * Function that checks if an artist has an event with the same name.
     * @param eventName name of the event
     * @return true if the artist has an event with the same name
     */
    public boolean checkSameEvent(final String eventName) {
        for (ArtistPage.Event event : artistPage.getEvents()) {
            if (event.getName().equals(eventName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Function that checks if an artist has a merch with the same name.
     * @param merchName name of the merch
     * @return true if the artist has a merch with the same name
     */
    public boolean checkSameMerch(final String merchName) {
        for (ArtistPage.Merch merch : artistPage.getMerchs()) {
            if (merch.getName().equals(merchName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String printCurrentPage() {
        return artistPage.printPage();
    }

    @Override
    protected boolean canBeRemoved(final MyDatabase database) {
        /* Check if other normal users interact with artist. */
        for (UserTypes user : database.getAllUsersCreated()) {
            /* Check if current user is normal. */
            if (user.getUserType() != UserType.USER) {
                continue;
            }

            UserNormal myUser = (UserNormal) user;

            /* Check if he selected the page. */
            if (myUser.getSelectedPage() == artistPage) {
                return false;
            }

            /* Check if his musicPlayer interact with album. */
            if (myUser.getMusicPlayer().getPlayback() == null) {
                continue;
            }

            myUser.getMusicPlayer().getPlayback().checkPlayback();
            AudioCollection currTrack = myUser.getMusicPlayer().getPlayback().getCurrTrack();

            if (currTrack == null) {
                continue;
            }

            /* Check if the current playing track is an artist's album. */
            if (currTrack.getType() == AudioCollection.AudioType.ALBUM) {
                Album currAlbum = (Album) currTrack;
                if (checkSameAlbum(currAlbum.getName())) {
                    return false;
                }
            }

            /*
             * Check if the current playing track
             * is a song and is owned by arist.
             */
            if (currTrack.getType() == AudioCollection.AudioType.SONG) {
                Song currSong = (Song) currTrack;
                if (currSong.getOwner().equals(this.getUsername())) {
                    return false;
                }
            }

            /*
             * Check if the current playing track
             * is playlist and has a song owned by artist.
             */
            if (currTrack.getType() == AudioCollection.AudioType.PLAYLIST) {
                Playlist currPlaylist = (Playlist) currTrack;
                for (Song song : currPlaylist.getSongs()) {
                    if (song.getOwner().equals(this.getUsername())) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public String removeUser(final MyDatabase database) {
        /* Check if we can remove the artist. */
        if (!canBeRemoved(database)) {
            return getUsername() + " can't be deleted.";
        }

        /* Iterate over users and unlike a song from album. */
        for (UserTypes user : database.getAllUsersCreated()) {
            /* Check if current user is normal. */
            if (user.getUserType() != UserType.USER) {
                continue;
            }

            UserNormal myUser = (UserNormal) user;
            if (myUser.getMusicPlayer().getLikedSongs() == null) {
                continue;
            }

            ArrayList<Song> toDelete = new ArrayList<Song>();
            for (Song song : myUser.getMusicPlayer().getLikedSongs()) {
                if (song.getOwner().equals(this.getUsername())) {
                    toDelete.add(song);
                }
            }

            myUser.getMusicPlayer().getLikedSongs().removeAll(toDelete);
        }

        /* Erase everything from database. */
        for (Album album : albums) {
            for (Song song : album.getSongs()) {
                database.getAllSongsCreated().remove(song);
            }
            database.getAllAlbumsCreated().remove(album);
        }

        /* Remove from database the user. */
        database.getAllUsersCreated().remove(this);

        return getUsername() + " was successfully deleted.";
    }

    /**
     * Function that returns the number
     * of plays of an artist by a normal user.
     * @param user
     * @return
     */
    public int getAllPlays(final UserNormal user) {
        int plays = 0;
        for (Song song : user.getHistorySongs()) {
            if (song.getArtist().equals(this.getUsername())) {
                plays++;
            }
        }
        for (Song song : user.getHistorySongsPremium()) {
            if (song.getArtist().equals(this.getUsername())) {
                plays++;
            }
        }
        for (Song song : user.getAllSongsPlayed()) {
            if (song.getArtist().equals(this.getUsername())) {
                plays++;
            }
        }
        return plays;
    }

    /**
     * Function that returns the number
     * of plays of an artist by all normal users.
     * @return
     */
    public int getAllPlays() {
        int plays = 0;
        for (Song song : MyDatabase.getInstance().getAllSongsCreated()) {
            if (song.getArtist().equals(this.getUsername())) {
                plays += song.getPlays();
            }
        }
        return plays;
    }

    /**
     * Function that returns top fans of this artist.
     * @return              top fans
     */
    public ArrayList<UserNormal> getTopFans() {
        ArrayList<UserNormal> sortedFans = new ArrayList<UserNormal>();
        for (UserTypes user : MyDatabase.getInstance().getAllUsersCreated()) {
            if (user.getUserType() == UserType.USER
                && ((UserNormal) user).getPlaysArtist(this) != 0) {
                sortedFans.add((UserNormal) user);
            }
        }

        UserArtist currArtist = this;

        sortedFans.sort(new Comparator<UserNormal>() {
            @Override
            public int compare(final UserNormal user1, final UserNormal user2) {

                if (user1.getPlaysArtist(currArtist) == user2.getPlaysArtist(currArtist)) {
                    return user1.getUsername().compareTo(user2.getUsername());
                }

                return user2.getPlaysArtist(currArtist) - user1.getPlaysArtist(currArtist);
            }
        });

        return sortedFans;
    }

    /**
     * Function that returns top albums of this artist.
     * @return              top albums
     */
    private ArrayList<Album> topAlbums() {
        ArrayList<Album> sortedAlbums = new ArrayList<Album>();
        for (Album album : albums) {
            if (!sortedAlbums.contains(album)) {
                if (album.getPlays() != 0) {
                    sortedAlbums.add(album);
                }
            }
        }

        sortedAlbums.sort(new Comparator<Album>() {
            @Override
            public int compare(final Album album1, final Album album2) {
                if (album1.getPlays() == album2.getPlays()) {
                    return album1.getName().compareTo(album2.getName());
                }

                return album2.getPlays() - album1.getPlays();
            }
        });

        return sortedAlbums;
    }

    /**
     * Function that returns sorted songs of this artist.
     * @return              sorted songs
     */
    private ArrayList<Song> sortedSongs() {
        ArrayList<Song> sortedSongs = new ArrayList<Song>() {
            @Override
            public boolean contains(final Object o) {
                Song song = (Song) o;
                for (Song song2 : this) {
                    if (song2.getName().equals(song.getName())) {
                        return true;
                    }
                }
                return false;
            }
        };
        for (Song song : MyDatabase.getInstance().getAllSongsCreated()) {
            if (!sortedSongs.contains(song)
                && song.getArtist().equals(this.getUsername())
                && MyDatabase.getInstance().getAllPlays(song) != 0) {
                sortedSongs.add(song);
            }
        }

        sortedSongs.sort(new Comparator<Song>() {
            @Override
            public int compare(final Song song1, final Song song2) {
                int plays1 = MyDatabase.getInstance().getAllPlays(song1);
                int plays2 = MyDatabase.getInstance().getAllPlays(song2);

                if (plays1 == plays2) {
                    return song1.getName().compareTo(song2.getName());
                }

                return plays2 - plays1;
            }
        });

        return sortedSongs;
    }

    @Override
    public ObjectNode getStatistics() {
        ArrayList<String> topFans = new ArrayList<String>();
        ObjectNode stats = new ObjectMapper().createObjectNode();
        ObjectNode topAlbums = new ObjectMapper().createObjectNode();
        ObjectNode topSongs = new ObjectMapper().createObjectNode();
        Integer listeners = 0;
        final int top5 = 5;

        topAlbums().stream().limit(top5)
                .forEach(album -> topAlbums.put(album.getName(), album.getPlays()));

        sortedSongs().stream().limit(top5)
                .forEach(song -> topSongs.put(song.getName(),
                        MyDatabase.getInstance().getAllPlays(song)));

        getTopFans().stream().limit(top5)
                .forEach(user -> topFans.add(user.getUsername()));

        for (UserTypes user : MyDatabase.getInstance().getAllUsersCreated()) {
            if (user.getUserType() == UserType.USER) {
                UserNormal myUser = (UserNormal) user;
                if (myUser.getPlaysArtist(this) != 0) {
                    listeners++;
                }
            }
        }

        if (topAlbums.isEmpty()
            && topSongs.isEmpty()
            && topFans.isEmpty()) {
            return null;
        }

        stats.put("topAlbums", topAlbums);
        stats.put("topSongs", topSongs);
        stats.put("topFans", new ObjectMapper().valueToTree(topFans));
        stats.put("listeners", listeners);

        return stats;
    }

    /**
     * Function that adds all the revenue from songs.
     * @return              revenue from songs
     */
    public double getSongRevenue() {
        final double hundred = 100.0;
        double songRevenue = 0;
        /* Iterate over map and all contribution. */
        for (Map.Entry<String, Double> entry : songsRevenue.entrySet()) {
            songRevenue += entry.getValue();
        }
        return Math.round(songRevenue * hundred) / hundred;
    }

    /**
     * Function that gets the number
     * of plays of a song by premium users.
     * @param song          song to be checked
     * @return              number of plays
     */
    public int playsByPremiumUsers(final Song song) {
        int plays = 0;
        for (UserTypes user : MyDatabase.getInstance().getAllUsersCreated()) {
            if (user.getUserType() == UserType.USER) {
                UserNormal myUser = (UserNormal) user;
                for (Song song2 : myUser.getHistorySongsPremium()) {
                    if (song2.getName().equals(song.getName())) {
                        plays++;
                    }
                }
            }
        }
        return plays;
    }

    /**
     * Function that sells a merch to a user.
     * @param buyer         user that buys the merch
     * @param merch         merch to be bought
     */
    public void newPurchase(final UserNormal buyer, final ArtistPage.Merch merch) {
        /* Add revenue. */
        this.merchRevenue += merch.getPrice();

        /* Add to buyer the purchase. */
        buyer.addMerch(merch);
    }
}
