package users;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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

public final class UserArtist extends UserTypes {
    @Getter
    private ArrayList<Album> albums;
    @Getter
    private ArtistPage artistPage;

    public UserArtist(final String username, final int age, final String city) {
        super(username, age, city);
        setUserType(UserType.ARTIST);
        albums = new ArrayList<Album>();
        this.artistPage = new ArtistPage(albums);
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
}
