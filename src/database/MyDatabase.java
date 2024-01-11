package database;

import lombok.Getter;
import musicplayer.AudioCollection;
import musicplayer.MusicPlayer;
import musicplayer.Playback;
import recommendations.FansPlaylistConcrete;
import recommendations.RandomPlaylistConcrete;
import recommendations.RandomSongConcrete;
import recommendations.RecommendationStrategy;
import users.UserArtist;
import users.UserHost;
import users.UserNormal;
import users.UserTypes;

import java.util.ArrayList;
import java.util.Comparator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

@Getter
public final class MyDatabase {
    private static LibraryInput library;

    private static MyDatabase instanceDb = null;

    private final ArrayList<UserTypes> allUsersCreated = new ArrayList<UserTypes>();

    private final ArrayList<Playlist> allPlaylistsCreated = new ArrayList<Playlist>();

    private final ArrayList<Podcast> allPodcastsCreated = new ArrayList<Podcast>();

    private final ArrayList<Song> allSongsCreated = new ArrayList<Song>();

    private final ArrayList<Album> allAlbumsCreated = new ArrayList<Album>();

    private final ArrayList<Song> deletedSongs = new ArrayList<Song>();

    private void initDataBase() {
        /* Add all podcasts with their episodes. */
        for (PodcastInput podcast : library.getPodcasts()) {
            Podcast newPodcast = new Podcast(podcast.getName(), podcast.getOwner());

            ArrayList<Episode> episodes = new ArrayList<Episode>();
            for (EpisodeInput episode : podcast.getEpisodes()) {
                Episode newEpisode = new Episode(episode.getName(), episode.getDuration(),
                        episode.getDescription(), podcast.getOwner());
                episodes.add(newEpisode);
            }
            newPodcast.setEpisodes(episodes);
            allPodcastsCreated.add(newPodcast);
        }

        /* Add all users. */
        for (UserInput user : library.getUsers()) {
            UserNormal normalUser = new UserNormal(user.getUsername(),
                    user.getAge(), user.getCity());
            normalUser.getMusicPlayer().setPodcasts(dupPodcasts());
            allUsersCreated.add(normalUser);
        }

        /* Add all songs. */
        for (SongInput song : library.getSongs()) {
            Song newSong = new Song(song);
            allSongsCreated.add(newSong);
        }
    }

    private MyDatabase() {
        initDataBase();
    }

    public static void setLibrary(final LibraryInput library) {
        MyDatabase.library = library;
    }

    /**
     * Function that returns the instance of the database.
     * @return              the instance of the database
     */
    public static MyDatabase getInstance() {
        if (instanceDb == null) {
            instanceDb = new MyDatabase();
        }
        return instanceDb;
    }

    /**
     * Function that clears the DataBase and reinitializes it.
     */
    public void clearDataBase() {
        allUsersCreated.clear();
        allPlaylistsCreated.clear();
        allPodcastsCreated.clear();
        allSongsCreated.clear();
        allAlbumsCreated.clear();

        initDataBase();
    }

    /**
     * Function that duplicates a podcast.
     * @param podcast podcast to be duplicated
     * @return the cloned podcast
     */
    public Podcast dupPodcast(final Podcast podcast) {
        Podcast newPodcast = new Podcast(podcast.getName(), podcast.getOwner());
        newPodcast.setEpisodes(new ArrayList<Episode>());
        for (Episode episode : podcast.getEpisodes()) {
            Episode newEpisode = new Episode(episode.getName(), episode.getDuration(),
                    episode.getDescription(), podcast.getOwner());
            newPodcast.getEpisodes().add(newEpisode);
        }

        return newPodcast;
    }

    /**
     * Function that duplicates an array of podcasts.
     * @return          the cloned array of podcasts
     */
    public ArrayList<Podcast> dupPodcasts() {
        ArrayList<Podcast> dupPodcasts = new ArrayList<>();
        for (Podcast podcast : allPodcastsCreated) {
            dupPodcasts.add(dupPodcast(podcast));
        }

        return dupPodcasts;
    }

    /**
     * Function that finds a user by username.
     * @param username  username of the user we want to find
     * @return          the user that matches
     */
    public UserTypes findMyUser(final String username) {
        for (UserTypes user : allUsersCreated) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Function that adds a new user to database.
     * @param username      username of the user
     * @param age           age of the user
     * @param city          city of the user
     * @param type          type of the user
     * @return              return the status of the command
     */
    public String addUser(final String username, final int age,
            final String city, final String type) {
        /* Check if username already exists. */
        if (findMyUser(username) != null) {
            return "The username " + username + " is already taken.";
        }

        UserTypes newUser = null;
        if (type.equals("user")) {
            newUser = new UserNormal(username, age, city);
            ((UserNormal) newUser).getMusicPlayer().setPodcasts(dupPodcasts());
        } else if (type.equals("artist")) {
            newUser = new UserArtist(username, age, city);
        } else if (type.equals("host")) {
            newUser = new UserHost(username, age, city);
        }

        allUsersCreated.add(newUser);

        return "The username " + username + " has been added successfully.";
    }

    /**
     * Function that removes a user from database.
     * @param username username of the user
     * @return return the status of the command
     */
    public String removeUser(final String username) {
        /* Check if username exists. */
        UserTypes toFind = findMyUser(username);

        if (toFind == null) {
            return "The username " + username + " doesn't exist.";
        }

        return toFind.removeUser(this);
    }

    /**
     * Function that changes the visibility of a playlist.
     * @param username              username of the user
     * @param playlistID            id of the playlist
     * @return                      status of the command
     */
    public String switchVisibility(final String username, final Integer playlistID) {
        /* Get the guaranteed normal user with the given username. */
        UserNormal user = ((UserNormal) findMyUser(username));

        /* Check if user is online. */
        if (!user.isOnline()) {
            return username + " is offline.";
        }

        return user.getMusicPlayer().switchVisibility(playlistID);
    }

    /**
     * Function that switches the connection status of a user.
     * @param username username of the user we want to change status
     * @return return the status of the command
     */
    public String switchConnectionStatus(final String username) {
        /* Get the user with the given username. */
        UserTypes toFind = findMyUser(username);

        if (toFind == null) {
            return "The username " + username + " doesn't exist.";
        }

        if (toFind.getUserType() != UserTypes.UserType.USER) {
            return username + " is not a normal user.";
        }

        ((UserNormal) toFind).switchConnectionStatus();

        return username + " has changed status successfully.";
    }

    /**
     * Function that returns all users.
     * @return          return all users
     */
    public ArrayNode getAllUsers() {
        ArrayNode output = new ObjectMapper().createArrayNode();

        /* First add normal users. */
        for (UserTypes user : allUsersCreated) {
            if (user.getUserType() == UserTypes.UserType.USER) {
                output.add(user.getUsername());
            }
        }

        /* Then, add artists. */
        for (UserTypes user : allUsersCreated) {
            if (user.getUserType() == UserTypes.UserType.ARTIST) {
                output.add(user.getUsername());
            }
        }

        /* Finally, add hosts. */
        for (UserTypes user : allUsersCreated) {
            if (user.getUserType() == UserTypes.UserType.HOST) {
                output.add(user.getUsername());
            }
        }

        return output;
    }

    /**
     * Function that adds a song to a playlist.
     * @param username              username of the user
     * @param playlistID            id of the playlist
     * @return                      status of the command
     */
    public String addRemoveInPlaylist(final String username, final Integer playlistID) {
        /* Get the guaranteed noraml user with the given username. */
        String noLOAD = "Please load a source before adding"
                + " to or removing from the playlist.";
        String noPLAYLIST = "The specified playlist does not exist.";
        String noSONG = "The loaded source is not a song.";
        String isOFFLINE = username + " is offline.";

        UserNormal user = ((UserNormal) findMyUser(username));

        if (user == null) {
            return "User not found.";
        }

        /* Check if user is online. */
        if (!user.isOnline()) {
            return isOFFLINE;
        }

        MusicPlayer player = user.getMusicPlayer();

        /* Check for load. */
        if (player.getPlayback() == null || player.getPlayback().getCurrTrack() == null) {
            return noLOAD;
        }

        /* Check for playlist. */
        if (player.getPlaylists().size() < playlistID) {
            return noPLAYLIST;
        }

        /* Check if song is loaded. */
        if (player.getPlayback().getCurrTrack().getType() != AudioCollection.AudioType.SONG
            && player.getPlayback().getCurrTrack().getType() != AudioCollection.AudioType.ALBUM) {
            return noSONG;
        }

        return user.getMusicPlayer().addRemoveInPlaylist(playlistID);
    }

    /**
     * Function that return the top 5 rated songs.
     * @return          return the top 5 rated songs
     */
    public ArrayNode getTop5Songs() {
        ArrayNode output = new ObjectMapper().createArrayNode();
        final int maxTop = 5;
        /* Create an aux vector of with songs. */
        ArrayList<Song> auxSongs = new ArrayList<>();

        auxSongs.addAll(allSongsCreated);

        /* Sort the arraylist of songs by the # of likes. */
        auxSongs.sort(new Comparator<Song>() {
            @Override
            public int compare(final Song stSong, final Song ndSong) {
                return ndSong.getNrLikes() - stSong.getNrLikes();
            }
        });

        for (int iter = 0; iter < maxTop; ++iter) {
            output.add(auxSongs.get(iter).getName());
        }

        return output;
    }

    /**
     * Function that returns the top 5 rated playlists.
     * @return               the top 5 rated playlists
     */
    public ArrayNode getTop5Playlists() {
        ArrayNode output = new ObjectMapper().createArrayNode();
        final int top5 = 5;

        ArrayList<Playlist> publicPlaylists = new ArrayList<>();
        for (Playlist playlist : allPlaylistsCreated) {
            if (playlist.isVisibility()) {
                publicPlaylists.add(playlist);
            }
        }

        /* Sort the list of public playlists by followers. */
        publicPlaylists.sort(new Comparator<Playlist>() {
            @Override
            public int compare(final Playlist playlist1, final Playlist playlist2) {
                return playlist2.getFollowers() - playlist1.getFollowers();
            }
        });

        for (Playlist playlist : publicPlaylists) {
            output.add(playlist.getName());
            if (output.size() == top5) {
                break;
            }
        }

        return output;
    }

    /**
     * Function that returns the online normal users.
     * @return return the online normal users
     */
    public ArrayNode getOnlineUsers() {
        ArrayNode output = new ObjectMapper().createArrayNode();

        for (UserTypes user : allUsersCreated) {
            if (user.getUserType() == UserTypes.UserType.USER) {
                if (((UserNormal) user).isOnline()) {
                    output.add(user.getUsername());
                }
            }
        }

        return output;
    }

    /**
     * Function that adds a new event to an artist.
     * @param username          username of the artist
     * @param date              date of the event
     * @param name              name of the event
     * @param description       description of the event
     * @return                  status of the command
     */
    public String addEvent(final String username, final String date,
            final String name, final String description) {
        /* Get the user. */
        UserTypes toFind = findMyUser(username);
        if (toFind == null) {
            return "The username " + username + " doesn't exist.";
        }

        /* Check if the user is an artist. */
        if (toFind.getUserType() != UserTypes.UserType.ARTIST) {
            return username + " is not an artist.";
        }

        return ((UserArtist) toFind).addEvent(name, date, description);
    }

    /**
     * Function that removes an event from an artist.
     * @param username          username of the artist
     * @param nameEvent         name of the event
     * @return                  status of the command
     */
    public String removeEvent(final String username, final String nameEvent) {
        UserTypes toFind = findMyUser(username);
        if (toFind == null) {
            return "The username " + username + " doesn't exist.";
        }

        /* Check if the user is an artist. */
        if (toFind.getUserType() != UserTypes.UserType.ARTIST) {
            return username + " is not an artist.";
        }

        return ((UserArtist) toFind).removeEvent(nameEvent);
    }

    /**
     * Function that adds a new merch to an artist.
     * @param username          username of the artist
     * @param price             price of the merch
     * @param nameMerch         name of the merch
     * @param description       description of the merch
     * @return                  status of the command
     */
    public String addMerch(final String username, final Integer price,
            final String nameMerch, final String description) {
        /* Get the user. */
        UserTypes toFind = findMyUser(username);
        if (toFind == null) {
            return "The username " + username + " doesn't exist.";
        }

        /* Check if the user is an artist. */
        if (toFind.getUserType() != UserTypes.UserType.ARTIST) {
            return username + " is not an artist.";
        }

        return ((UserArtist) toFind).addMerch(nameMerch, description, price);
    }

    /**
     * Function that adds an album to an artist.
     * @param username              username of the artist
     * @param name                  name of the album
     * @param relaseYear            release year of the album
     * @param description           description of the album
     * @param songs                 songs of the album
     * @return                      status of the command
     */
    public String addAlbum(final String username, final String name,
            final Integer relaseYear, final String description, final ArrayList<Song> songs) {

        /* Check if user exists. */
        UserTypes toFind = findMyUser(username);

        if (toFind == null) {
            return "The username " + username + " doesn't exist.";
        }

        /* Check if the user is an artist. */
        if (toFind.getUserType() != UserTypes.UserType.ARTIST) {
            return username + " is not an artist.";
        }

        /* Check if the artist has already an album with the same name. */
        if (((UserArtist) toFind).checkSameAlbum(name)) {
            return username + " has another album with the same name.";
        }

        /* Check if in this album all songs are unique. */
        for (Song song : songs) {
            for (Song song2 : songs) {
                if (song != song2) {
                    if (song.getName().equals(song2.getName())) {
                        return username + " has the same song at least twice in this album.";
                    }
                }
            }
        }

        Album newAlbum = new Album(name, username, relaseYear, description, songs);
        ((UserArtist) toFind).addAlbum(newAlbum);
        allAlbumsCreated.add(newAlbum);

        /* Add the music from artist in database. */
        for (Song song : songs) {
            allSongsCreated.add(song);
        }

        return username + " has added new album successfully.";
    }

    /**
     * Function that adds a new song to database.
     * @param username          username of the artist
     * @param name              name of the song
     * @param episodes          episodes of the song
     * @return                  status of the command
     */
    public String addPodcast(final String username, final String name,
            final ArrayList<Episode> episodes) {

        /* Check if user exists. */
        UserTypes toFind = findMyUser(username);

        if (toFind == null) {
            return "The username " + username + " doesn't exist.";
        }

        /* Check if the user is not a host. */
        if (toFind.getUserType() != UserTypes.UserType.HOST) {
            return username + " is not a host.";
        }

        /* Check if the host has already a podcast with the same name. */
        for (Podcast podcast : ((UserHost) toFind).getPodcasts()) {
            if (podcast.getName().equals(name)) {
                return username + " has another podcast with the same name.";
            }
        }

        /* Check if in this podcast all episodes are unique. */
        for (Episode episode : episodes) {
            for (Episode episode2 : episodes) {
                if (episode != episode2) {
                    if (episode.getName().equals(episode2.getName())) {
                        return username + " has the same episode in this podcast.";
                    }
                }
            }
        }

        Podcast newPodcast = new Podcast(name, username, episodes);
        ((UserHost) toFind).getPodcasts().add(newPodcast);
        allPodcastsCreated.add(newPodcast);

        for (UserTypes user : allUsersCreated) {
            if (user.getUserType() == UserTypes.UserType.USER) {
                UserNormal normalUser = (UserNormal) user;
                /* Add in musicplayer the new podcast. */
                normalUser.getMusicPlayer().getPodcasts().add(dupPodcast(newPodcast));
            }
        }

        return username + " has added new podcast successfully.";
    }

    /**
     * Function that removes a podcast from database.
     * @param username          username of the host
     * @param name              name of the podcast
     * @return                  status of the command
     */
    public String removePodcast(final String username, final String name) {
        /* Check if user exists. */
        UserTypes toFind = findMyUser(username);

        if (toFind == null) {
            return "The username " + username + " doesn't exist.";
        }

        /* Check if the user is not a host. */
        if (toFind.getUserType() != UserTypes.UserType.HOST) {
            return username + " is not a host.";
        }

        /* Check if the host has already a podcast with the same name. */
        Podcast toDelete = null;
        for (Podcast podcast : ((UserHost) toFind).getPodcasts()) {
            if (podcast.getName().equals(name)) {
                toDelete = podcast;
            }
        }

        if (toDelete == null) {
            return username + " doesn't have a podcast with the given name.";
        }

        /* Check if other normal users interact with podcast. */
        for (UserTypes user : allUsersCreated) {
            if (user.getUserType() == UserTypes.UserType.USER) {
                UserNormal normalUser = (UserNormal) user;
                if (normalUser.getMusicPlayer().getPlayback() == null) {
                    continue;
                }

                AudioCollection currTrack =
                        normalUser.getMusicPlayer().getPlayback().getCurrTrack();

                if (currTrack == null) {
                    continue;
                }

                if (currTrack.getType() == AudioCollection.AudioType.PODCAST) {
                    Podcast currPodcast = (Podcast) currTrack;
                    if (currPodcast.getOwner().equals(toFind.getUsername())) {
                        return username + " can't delete this podcast.";
                    }
                }
            }
        }

        /* Remove podcast from database. */
        allPodcastsCreated.remove(toDelete);
        ((UserHost) toFind).getPodcasts().remove(toDelete);

        /* Remove podcast from musicplayer of normal users. */
        for (UserTypes user : allUsersCreated) {
            if (user.getUserType() == UserTypes.UserType.USER) {
                UserNormal normalUser = (UserNormal) user;
                ArrayList<Podcast> toDeletePodcasts = new ArrayList<Podcast>();
                for (Podcast podcast : normalUser.getMusicPlayer().getPodcasts()) {
                    if (podcast.getName().equals(toDelete.getName())) {
                        toDeletePodcasts.add(podcast);
                    }
                }
                normalUser.getMusicPlayer().getPodcasts().removeAll(toDeletePodcasts);
            }
        }

        return username + " deleted the podcast successfully.";
    }

    /**
     * Function that adds a new announcement to a host.
     * @param username          username of the host
     * @param message           message of the announcement
     * @param description       description of the announcement
     * @return                  status of the command
     */
    public String addAnnouncement(final String username,
            final String message, final String description) {
        /* Check if user exists. */
        UserTypes toFind = findMyUser(username);

        if (toFind == null) {
            return "The username " + username + " doesn't exist.";
        }

        /* Check if the user is not a host. */
        if (toFind.getUserType() != UserTypes.UserType.HOST) {
            return username + " is not a host.";
        }

        /* Check if the host has another announcement with the same name. */
        for (UserHost.Announcement announcement : ((UserHost) toFind).getAnnouncements()) {
            if (announcement.getNameAnnouncement().equals(message)) {
                return username + " has already added an announcement with this name.";
            }
        }

        ((UserHost) toFind).addAnnouncement(message, description);

        return username + " has successfully added new announcement.";
    }

    /**
     * Function that removes an announcement from a host.
     * @param username          username of the host
     * @param name              name of the announcement
     * @return                  status of the command
     */
    public String removeAnnouncement(final String username, final String name) {
        /* Check if user exists. */
        UserTypes toFind = findMyUser(username);

        if (toFind == null) {
            return "The username " + username + " doesn't exist.";
        }

        /* Check if the user is not a host. */
        if (toFind.getUserType() != UserTypes.UserType.HOST) {
            return username + " is not a host.";
        }

        /* Find the announcement and remove it. */
        for (UserHost.Announcement announcement : ((UserHost) toFind).getAnnouncements()) {
            if (announcement.getNameAnnouncement().equals(name)) {
                ((UserHost) toFind).getAnnouncements().remove(announcement);
                return username + " has successfully deleted the announcement.";
            }
        }

        /* Failed to find an announcement with the given name. */
        return username + " has no announcement with the given name.";
    }

    /**
     * Function that returns all albums of an artist.
     *
     * @param username username of the artist's albums
     * @param output   output array
     */
    public ArrayNode showAlbums(final String username) {
        ArrayNode output = new ObjectMapper().createArrayNode();
        /* Here, we will always have a username of an artist. */
        UserArtist artist = ((UserArtist) UserTypes.findMyUser(allUsersCreated, username));
        artist.showAlbums(output);
        return output;
    }

    /**
     * Function that returns all podcasts of a host.
     * @param username
     * @return
     */
    public ArrayNode showPodcasts(final String username) {
        ArrayNode output = new ObjectMapper().createArrayNode();
        /* Here, we will always have a username of a host. */
        UserHost host = ((UserHost) UserTypes.findMyUser(allUsersCreated, username));
        host.showPodcasts(output);
        return output;
    }

    /**
     * Function that returns the playlist of a user.
     * @param username          username of the user
     * @return                  the playlist of the user
     */
    public ArrayNode showPlaylists(final String username) {
        ArrayNode output = new ObjectMapper().createArrayNode();
        /* Here, we will always have a username of a host. */
        UserNormal user = ((UserNormal) UserTypes.findMyUser(allUsersCreated, username));
        user.showPlaylists(output);
        return output;
    }

    /**
     * Function that returns the liked songs of a user.
     * @param username          username of the user
     * @return                  the liked songs of the user
     */
    public ArrayNode showPreferredSongs(final String username) {
        ArrayNode output = new ObjectMapper().createArrayNode();
        /* Here, we will always have a username of a host. */
        UserNormal user = ((UserNormal) UserTypes.findMyUser(allUsersCreated, username));
        user.showPreferredSongs(output);
        return output;
    }

    /**
     * Function that prints the current page of a user.
     * @param username          username of the user
     * @return                  the user printPage function
     */
    public String printCurrentPage(final String username) {
        for (UserTypes user : allUsersCreated) {
            if (user.getUsername().equals(username)) {
                return user.printCurrentPage();
            }
        }
        return null;
    }

    /**
     * Function that removes an album from database.
     * @param username          username of the artist
     * @param name              name of the album
     * @return                  status of the command
     */
    public String removeAlbum(final String username, final String name) {
        /* Check if user exists. */
        UserTypes toFind = findMyUser(username);
        if (toFind == null) {
            return "The username " + username + " doesn't exist.";
        }

        /* Check if user is an artist. */
        if (toFind.getUserType() != UserTypes.UserType.ARTIST) {
            return username + " is not an artist.";
        }

        /* Get the album with given name. */
        Album albumToFind = null;
        for (Album album : ((UserArtist) toFind).getAlbums()) {
            if (album.getName().equals(name)) {
                albumToFind = album;
            }
        }

        /* Check if artist doesn't have an album with given name. */
        if (albumToFind == null) {
            return username + " doesn't have an album with the given name.";
        }

        /* Check if other normal users interact with album. */
        for (UserTypes user : allUsersCreated) {
            if (user.getUserType() == UserTypes.UserType.USER) {
                UserNormal normalUser = (UserNormal) user;
                if (normalUser.getMusicPlayer().getPlayback() == null) {
                    continue;
                }

                AudioCollection currTrack =
                    normalUser.getMusicPlayer().getPlayback().getCurrTrack();

                if (currTrack == null) {
                    continue;
                }

                if (currTrack.getType() == AudioCollection.AudioType.SONG) {
                    Song currSong = (Song) currTrack;
                    if (currSong.getAlbum().equals(name)) {
                        return username + " can't delete this album.";
                    }
                }

                for (Playlist playlist : normalUser.getMusicPlayer().getPlaylists()) {
                    for (Song song : playlist.getSongs()) {
                        if (song.getOwner() == albumToFind.getName()) {
                            return username + " can't delete this album.";
                        }
                    }
                }
            }
        }

        /* Remove songs from album from database. */
        for (Song song : albumToFind.getSongs()) {
            deletedSongs.add(song);
            allSongsCreated.remove(song);
        }

        /* Remove album from database. */
        allAlbumsCreated.remove(albumToFind);
        ((UserArtist) toFind).getAlbums().remove(albumToFind);

        return username + " deleted the album successfully.";
    }

    /**
     * Function that changes the page of a normal user.
     * @param username          username of the user
     * @param nextPage          next page of the user
     * @return                  status of the command
     */
    public String changePage(final String username, final String nextPage) {
        /* Guaranteed that we have a normal user. */
        UserNormal user = (UserNormal) findMyUser(username);

        return user.changePage(nextPage);
    }

    /**
     * Function that returns the top 5 albums.
     * @return          return the top 5 albums
     */
    public ArrayNode getTop5Albums() {
        final Integer maxResult = 5;
        ArrayNode output = new ObjectMapper().createArrayNode();

        ArrayList<Album> allAlbums = new ArrayList<Album>();
        for (Album album : allAlbumsCreated) {
            allAlbums.add(album);
        }

        /* Sort albums by number of likes of songs. */
        allAlbums.sort(new Comparator<Album>() {
            @Override
            public int compare(final Album album1, final Album album2) {
                int nrLikes1 = (int) album1.getSongs().stream().mapToInt(Song::getNrLikes).sum();
                int nrLikes2 = (int) album2.getSongs().stream().mapToInt(Song::getNrLikes).sum();

                if (nrLikes1 == nrLikes2) {
                    return album1.getName().compareTo(album2.getName());
                }

                return nrLikes2 - nrLikes1;
            }
        });

        allAlbums.stream()
            .map(Album::getName)
            .limit(maxResult)
            .forEach(output::add);

        return output;
    }

    /**
     * Function that returns the top 5 artists.
     * @return          return the top 5 artists
     */
    public ArrayNode getTop5Artists() {
        final Integer maxResult = 5;
        ArrayNode output = new ObjectMapper().createArrayNode();

        /* Extract all artists. */
        ArrayList<UserArtist> allArtists = new ArrayList<UserArtist>();
        for (UserTypes user : allUsersCreated) {
            if (user.getUserType() == UserTypes.UserType.ARTIST) {
                allArtists.add((UserArtist) user);
            }
        }

        /* Sort artists by number of likes of songs. */
        allArtists.sort(new Comparator<UserArtist>() {
            @Override
            public int compare(final UserArtist artist1, final UserArtist artist2) {
                int nrLikes1 = (int) artist1.getAlbums().stream()
                    .mapToInt(album -> album.getSongs().stream()
                    .mapToInt(Song::getNrLikes).sum()).sum();
                int nrLikes2 = (int) artist2.getAlbums().stream()
                    .mapToInt(album -> album.getSongs().stream()
                    .mapToInt(Song::getNrLikes).sum()).sum();

                return nrLikes2 - nrLikes1;
            }
        });

        allArtists.stream()
            .map(UserArtist::getUsername)
            .limit(maxResult)
            .forEach(output::add);

        return output;
    }

    /**
     * Function that return all plays of a song.
     * @param song      song to be searched
     * @return          number of plays
     */
    public int getAllPlays(final Song song) {
        int plays = 0;
        for (Song iterSong : allSongsCreated) {
            if (iterSong.getName().equals(song.getName())
                && iterSong.getArtist().equals(song.getArtist())) {
                plays += iterSong.getPlays();
            }
        }

        for (Song iterSong : deletedSongs) {
            if (iterSong.getName().equals(song.getName())
                && iterSong.getArtist().equals(song.getArtist())) {
                plays += iterSong.getPlays();
            }
        }
        return plays;
    }

    /**
     * Function that updates the state of all users.
     */
    private void updateAllUsersStatus() {
        for (UserTypes user : allUsersCreated) {
            if (user.getUserType() == UserTypes.UserType.USER) {
                UserNormal normalUser = (UserNormal) user;
                if (normalUser.getMusicPlayer().getPlayback() == null) {
                    continue;
                }
                normalUser.getMusicPlayer().getPlayback().checkPlayback();
            }
        }
    }

    /**
     * Returns the statistics of a user.
     * @param username      username of the user
     * @return              the statistics of the user
     */
    public ObjectNode getStatistics(final String username) {
        /* For all users, update their state of player. */
        updateAllUsersStatus();
        return findMyUser(username).getStatistics();
    }

    /**
     * Function that buys a merch for a user.
     * @param username          username of the user
     * @param merchName         name of the merch
     * @return                  status of the command
     */
    public String buyMerch(final String username, final String merchName) {
        UserNormal toFind = (UserNormal) findMyUser(username);

        if (toFind == null) {
            return "The username " + username + " doesn't exist.";
        }

        return toFind.buyMerch(merchName);
    }

    /**
     * Function that changes user's account to premium.
     * @param username          username of the user
     * @return                  status of the command
     */
    public String buyPremium(final String username) {
        UserNormal toFind = (UserNormal) findMyUser(username);
        if (toFind == null) {
            return "The username " + username + " doesn't exist.";
        }

        if (toFind.isPremium()) {
            return username + " is already a premium user.";
        }

        if (toFind.getMusicPlayer().getPlayback() != null) {
            toFind.getMusicPlayer().getPlayback().checkPlayback();
        }

        toFind.setPremium(true);

        return username + " bought the subscription successfully.";
    }

    /**
     * Function that cancels user's premium account.
     * @param username          username of the user
     * @return                  status of the command
     */
    public String cancelPremium(final String username) {
        UserNormal toFind = (UserNormal) findMyUser(username);
        if (toFind == null) {
            return "The username " + username + " doesn't exist.";
        }

        if (!toFind.isPremium()) {
            return username + " is not a premium user.";
        }

        if (toFind.getMusicPlayer().getPlayback() != null) {
            toFind.getMusicPlayer().getPlayback().checkPlayback();
        }

        toFind.setPremium(false);

        /* Pay every artist listened. */
        toFind.payPremium();

        /* Clear premium history. */
        for (Song song : toFind.getHistorySongsPremium()) {
            toFind.getAllSongsPlayed().add(song);
        }
        toFind.getHistorySongsPremium().clear();

        return username + " cancelled the subscription successfully.";
    }

    /**
     * Function that returns all merches bought by a user.
     * @param username          username of the user
     * @return                  all merches bought by a user
     */
    public ArrayNode seeMerch(final String username) {
        UserNormal toFind = (UserNormal) findMyUser(username);
        return toFind.seeMerch();
    }

    /**
     * Function that distributes the money to artists.
     */
    private void distributeMoney() {
        for (UserTypes user : allUsersCreated) {
            if (user.getUserType() == UserTypes.UserType.USER) {
                UserNormal myUser = (UserNormal) user;
                if (myUser.isPremium()) {
                    myUser.payPremium();
                }
                myUser.payFreeAccount();
            }
        }
    }

    /**
     * Function that returns the statistics of all artists.
     * @return                  the statistics of all artists
     */
    public ObjectNode statsArtist() {
        ObjectNode output = new ObjectMapper().createObjectNode();

        updateAllUsersStatus();
        distributeMoney();

        ArrayList<UserArtist> allArtists = new ArrayList<>();
        for (UserTypes user : allUsersCreated) {
            if (user.getUserType() == UserTypes.UserType.ARTIST) {
                if (((UserArtist) user).getAllPlays() != 0
                    || ((UserArtist) user).getMerchRevenue() != 0) {
                    allArtists.add((UserArtist) user);
                }
            }
        }

        allArtists.sort(new Comparator<UserArtist>() {
            @Override
            public int compare(final UserArtist artist1, final UserArtist artist2) {
                if (artist1.getMerchRevenue() + artist1.getSongRevenue()
                    == artist2.getMerchRevenue() + artist2.getSongRevenue()) {
                    return artist1.getUsername().compareTo(artist2.getUsername());
                }
                return Double.compare(artist2.getMerchRevenue() + artist2.getSongRevenue(),
                        artist1.getMerchRevenue() + artist1.getSongRevenue());
            }
        });

        for (int iter = 0; iter < allArtists.size(); ++iter) {
            ObjectNode newArtistInfo = new ObjectMapper().createObjectNode();
            UserArtist artist = allArtists.get(iter);
            newArtistInfo.put("merchRevenue", artist.getMerchRevenue());
            newArtistInfo.put("songRevenue", artist.getSongRevenue());
            newArtistInfo.put("ranking", iter + 1);

            /* Get the song with most plays. */
            String mostPlayedSong = null;
            for (String song : artist.getSongsRevenue().keySet()) {
                if (mostPlayedSong == null) {
                    mostPlayedSong = song;
                } else {
                    if (artist.getSongsRevenue().get(song)
                            > artist.getSongsRevenue().get(mostPlayedSong)) {
                        mostPlayedSong = song;
                    } else if (artist.getSongsRevenue().get(song)
                            .equals(artist.getSongsRevenue().get(mostPlayedSong))) {
                        if (mostPlayedSong.compareTo(song) > 0) {
                            mostPlayedSong = song;
                        }
                    }
                }
            }

            if (mostPlayedSong == null || allArtists.get(iter).getSongRevenue() == 0) {
                newArtistInfo.put("mostProfitableSong", "N/A");
            } else {
                newArtistInfo.put("mostProfitableSong", mostPlayedSong);
            }
            output.put(allArtists.get(iter).getUsername(), newArtistInfo);
        }

        return output;
    }

    /**
     * Funciton that returns the notifications of a user.
     * @param username          username of the user
     * @return                  the notifications of the user
     */
    public ArrayNode getNotifications(final String username) {
        UserNormal toFind = (UserNormal) findMyUser(username);
        return toFind.getNotifications();
    }

    /**
     * Function that returns the user with a given playback.
     * @param playback          playback of the user
     * @return                  the user with the given playback
     */
    public UserNormal getUserWithPlayback(final Playback playback) {
        for (UserTypes user : allUsersCreated) {
            if (user.getUserType() == UserTypes.UserType.USER) {
                UserNormal normalUser = (UserNormal) user;
                if (normalUser.getMusicPlayer().getPlayback() == playback) {
                    return normalUser;
                }
            }
        }
        return null;
    }

    /**
     * Function that performs the
     * subscribe request from a user.
     * @param username      username of the user
     * @return              status of the command
     */
    public String subscribe(final String username) {
        UserNormal toFind = (UserNormal) findMyUser(username);
        if (toFind == null) {
            return "The username " + username + " doesn't exist.";
        }

        if (toFind.getSelectedPage() == null) {
            return "To subscribe you need to be on the page of an artist or host.";
        }

        return toFind.getSelectedPage().subscribe(toFind);
    }

    /**
     * Context for strategy pattern used for recommendations.
     * @param username
     * @param recommendationType
     * @return
     */
    public String updateRecommendations(final String username, final String recommendationType) {
        UserNormal user = (UserNormal) findMyUser(username);
        Playback playback = user.getMusicPlayer().getPlayback();

        /* Get the current state of playback. */
        playback.checkPlayback();

        RecommendationStrategy recommendation = null;

        switch (recommendationType) {
            case "fans_playlist" -> {
                recommendation = new FansPlaylistConcrete();
                break;
            } case "random_song" -> {
                recommendation = new RandomSongConcrete();
                break;
            } case "random_playlist" -> {
                recommendation = new RandomPlaylistConcrete();
                break;
            } default -> System.out.println("Invalid recommendation type.");
        }

        return recommendation.createRecommendation(user);
    }

    /**
     * Function that changes the current page of a user.
     * @param username          username of the user
     * @return                  status of the command
     */
    public String previousPage(final String username) {
        UserNormal user = (UserNormal) findMyUser(username);
        return user.previousPage();
    }

    /**
     * Function that changes the current page of a user.
     * @param username          username of the user
     * @return                  status of the command
     */
    public String nextPage(final String username) {
        UserNormal user = (UserNormal) findMyUser(username);
        return user.nextPage();
    }

    /**
     * Function that adds an ad to a user playback.
     * @param username          username of the user
     * @param price             price of the ad
     * @return                  status of the command
     */
    public String adBreak(final String username, final int price) {
        UserNormal user = (UserNormal) findMyUser(username);
        if (user == null) {
            return "The username " + username + " doesn't exist.";
        }

        Playback playback = user.getMusicPlayer().getPlayback();

        if (playback == null || playback.getCurrTrack() == null) {
            return username + " is not playing any music.";
        }

        return playback.adBreak(user, price);
    }
}
