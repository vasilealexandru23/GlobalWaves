package users;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import database.MyDatabase;
import database.Playlist;
import database.Song;
import lombok.Getter;
import musicplayer.AudioCollection.AudioType;
import pages.HomePage;
import pages.LikedContentPage;
import pages.UserPage;
import searchbar.Search;
import searchbar.SearchArtist;
import searchbar.SearchHost;
import musicplayer.AudioCollection;
import musicplayer.MusicPlayer;

public final class UserNormal extends UserTypes {
    @Getter
    private MusicPlayer musicPlayer;

    @Getter
    private boolean online;

    private ArrayList<UserPage> pages;

    private int indexPage;
    @Getter
    private Search lastSearch;
    @Getter
    private Integer lastSelect;

    @Getter
    private UserPage selectedPage;

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
    }

    public void setOnline(final boolean online) {
        this.online = online;
    }

    public void setIndexPage(final int indexPage) {
        this.indexPage = indexPage;
    }

    /**
     * Function that change the current page.
     * @param nextPage The page to be changed to.
     * @return The status of the command.
     */
    public String changePage(final String nextPage) {
        if (nextPage.equals("Home")) {
            indexPage = 0;
            selectedPage = null;
            return getUsername() + " accessed Home successfully.";
        } else if (nextPage.equals("LikedContent")) {
            indexPage = 1;
            selectedPage = null;
            return getUsername() + " accessed LikedContent successfully.";
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
        return pages.get(indexPage).printPage();
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
}
