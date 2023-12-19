package fileio.input;

import java.util.ArrayList;

import commands.AddAlbumCommand;
import commands.AddAnnouncementCommand;
import commands.AddEventCommand;
import commands.AddMerchCommand;
import commands.AddPodcastCommand;
import commands.AddRemoveInPlaylistCommand;
import commands.AddUserCommand;
import commands.BackwardCommand;
import commands.ChangePageCommand;
import commands.CommandRunner;
import commands.CreatePlaylistCommand;
import commands.DeleteUserCommand;
import commands.FollowCommand;
import commands.ForwardCommand;
import commands.GetAllUsersCommand;
import commands.GetOnlineUsersCommand;
import commands.GetTop5AlbumsCommand;
import commands.GetTop5ArtistsCommand;
import commands.GetTop5PlaylistsCommand;
import commands.GetTop5SongsCommand;
import commands.LikeCommand;
import commands.LoadCommand;
import commands.NextCommand;
import commands.PlayPauseCommand;
import commands.PrevCommand;
import commands.PrintCurrentPageCommand;
import commands.RemoveAlbumCommand;
import commands.RemoveAnnouncementCommand;
import commands.RemoveEventCommand;
import commands.RemovePodcastCommand;
import commands.RepeatCommand;
import commands.SearchCommand;
import commands.SelectCommand;
import commands.ShowAlbumsCommand;
import commands.ShowPlaylistsCommand;
import commands.ShowPodcastCommand;
import commands.ShowPreferredSongs;
import commands.ShuffleCommand;
import commands.StatusCommand;
import commands.SwitchConnectionStatusCommand;
import commands.SwitchVisibilityCommand;
import database.Episode;
import database.Song;
import lombok.Getter;
import searchbar.Filters;
import searchbar.SearchArtist;
import searchbar.SearchHost;
import searchbar.Search;
import searchbar.SearchPlaylist;
import searchbar.SearchPodcast;
import searchbar.SearchSong;
import searchbar.SearchAlbum;

public final class CommandInput {
    @Getter
    private String command;

    @Getter
    private static ArrayList<CommandRunner> commands = new ArrayList<>();

    /* For MusicPlayer. */
    @Getter
    private Integer itemNumber;
    @Getter
    private Search searchcmd = null;
    @Getter
    private int timestamp;
    private Filters filters;

    /* For users. */
    @Getter
    private String username;
    private int age;
    private String city;
    private String type;

    /* For playlists. */
    @Getter
    private String playlistName;
    @Getter
    private int playlistId;
    @Getter
    private int seed;

    /* For album. */
    private String name;
    private String date;
    private Integer price;
    private Integer releaseYear;
    private String description;
    private ArrayList<Song> songs = new ArrayList<Song>();
    private ArrayList<Episode> episodes = new ArrayList<Episode>();

    /* For pagination. */
    @Getter
    private String nextPage;

    public void setName(final String name) {
        this.name = name;
    }

    public void setReleaseYear(final Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setEpisodes(final ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }

    public void setNextPage(final String nextPage) {
        this.nextPage = nextPage;
    }

    public CommandInput() {
    }

    public void setSearchcmd(final Search searchcmd) {
        this.searchcmd = searchcmd;
    }

    /**
     * Function that constructs the commands.
     */
    public void constructCommand() {
        switch (command) {
            case "search" -> {
                commands.add(new SearchCommand(command, username, searchcmd,
                        timestamp, type, filters));
            } case "select" -> {
                commands.add(new SelectCommand(command, username, timestamp, itemNumber));
            } case "load" -> {
                commands.add(new LoadCommand(command, username, timestamp));
            } case "status" -> {
                commands.add(new StatusCommand(command, username, timestamp));
            } case "playPause" -> {
                commands.add(new PlayPauseCommand(command, username, timestamp));
            } case "createPlaylist" -> {
                commands.add(new CreatePlaylistCommand(command, username, timestamp, playlistName));
            } case "addRemoveInPlaylist" -> {
                commands.add(new AddRemoveInPlaylistCommand(command, username,
                    timestamp, playlistId));
            } case "like" -> {
                commands.add(new LikeCommand(command, username, timestamp));
            } case "showPlaylists" -> {
                commands.add(new ShowPlaylistsCommand(command, username, timestamp));
            } case "showPreferredSongs" -> {
                commands.add(new ShowPreferredSongs(command, username, timestamp));
            } case "follow" -> {
                commands.add(new FollowCommand(command, username, timestamp));
            } case "getTop5Songs" -> {
                commands.add(new GetTop5SongsCommand(command, username, timestamp));
            } case "getTop5Playlists" -> {
                commands.add(new GetTop5PlaylistsCommand(command, username, timestamp));
            } case "repeat" -> {
                commands.add(new RepeatCommand(command, username, timestamp));
            } case "shuffle" -> {
                commands.add(new ShuffleCommand(command, username, timestamp, seed));
            } case "forward" -> {
                commands.add(new ForwardCommand(command, username, timestamp));
            } case "backward" -> {
                commands.add(new BackwardCommand(command, username, timestamp));
            } case "next" -> {
                commands.add(new NextCommand(command, username, timestamp));
            } case "prev" -> {
                commands.add(new PrevCommand(command, username, timestamp));
            } case "switchVisibility" -> {
                commands.add(new SwitchVisibilityCommand(command, username, timestamp, playlistId));
            } case "addUser" -> {
                commands.add(new AddUserCommand(command, username, timestamp, age, city, type));
            } case "deleteUser" -> {
                commands.add(new DeleteUserCommand(command, username, timestamp));
            } case "switchConnectionStatus" -> {
                commands.add(new SwitchConnectionStatusCommand(command, username, timestamp));
            } case "addAlbum" -> {
                commands.add(new AddAlbumCommand(command, username, timestamp, name,
                        releaseYear, description, songs));
            } case "removeAlbum" -> {
                commands.add(new RemoveAlbumCommand(command, username, timestamp, name));
            } case "showAlbums" -> {
                commands.add(new ShowAlbumsCommand(command, username, timestamp));
            } case "printCurrentPage" -> {
                commands.add(new PrintCurrentPageCommand(command, username, timestamp));
            } case "addEvent" -> {
                commands.add(new AddEventCommand(command, username, timestamp,
                        date, name, description));
            } case "removeEvent" -> {
                commands.add(new RemoveEventCommand(command, username, timestamp, name));
            } case "addMerch" -> {
                commands.add(new AddMerchCommand(command, username, timestamp,
                        price, name, description));
            } case "addPodcast" -> {
                commands.add(new AddPodcastCommand(command, username, timestamp, name, episodes));
            } case "removePodcast" -> {
                commands.add(new RemovePodcastCommand(command, username, timestamp, name));
            } case "showPodcasts" -> {
                commands.add(new ShowPodcastCommand(command, username, timestamp));
            } case "addAnnouncement" -> {
                commands.add(new AddAnnouncementCommand(command, username, timestamp,
                        name, description));
            } case "removeAnnouncement" -> {
                commands.add(new RemoveAnnouncementCommand(command, username, timestamp, name));
            } case "changePage" -> {
                commands.add(new ChangePageCommand(command, username, timestamp, nextPage));
            } case "getTop5Albums" -> {
                commands.add(new GetTop5AlbumsCommand(command, username, timestamp));
            } case "getTop5Artists" -> {
                commands.add(new GetTop5ArtistsCommand(command, username, timestamp));
            } case "getAllUsers" -> {
                commands.add(new GetAllUsersCommand(command, username, timestamp));
            } case "getOnlineUsers" -> {
                commands.add(new GetOnlineUsersCommand(command, username, timestamp));
            } default -> {
                System.out.println("Command not found.");
            }
        }
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public void setPrice(final Integer price) {
        this.price = price;
    }

    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Function that sets the select parameter.
     * @param itemNumber
     */
    public void setItemNumber(final int itemNumber) {
        this.itemNumber = itemNumber;
    }

    /**
     * Function that sets the type of search.
     * @param type
     */
    public void setType(final String type) {
        if (type.equals("song")) {
            if (searchcmd == null) {
                this.searchcmd = new SearchSong();
            }
        } else if (type.equals("podcast")) {
            if (searchcmd == null) {
                this.searchcmd = new SearchPodcast();
            }
        } else if (type.equals("playlist")) {
            if (searchcmd == null) {
                this.searchcmd = new SearchPlaylist();
            }
        } else if (type.equals("artist")) {
            if (searchcmd == null) {
                this.searchcmd = new SearchArtist();
            }
        } else if (type.equals("album")) {
            if (searchcmd == null) {
                this.searchcmd = new SearchAlbum();
            }
        } else if (type.equals("host")) {
            if (searchcmd == null) {
                this.searchcmd = new SearchHost();
            }
        }

        this.type = type;
    }

    /**
     * Function that sets filters.
     * @param filters
     */
    public void setFilters(final Filters filters) {
        if (searchcmd == null) {
            this.searchcmd = new Search();
        }
        this.searchcmd.setFilters(filters);
        this.filters = filters;
    }

    public void setPlaylistName(final String playlistName) {
        this.playlistName = playlistName;
    }

    public void setPlaylistId(final int playlistId) {
        this.playlistId = playlistId;
    }

    public void setSeed(final int seed) {
        this.seed = seed;
    }

}
