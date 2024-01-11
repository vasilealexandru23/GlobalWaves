package fileio.input;

import java.util.ArrayList;


import commands.*;
import commands.ArtistCommands.AddAlbumCommand;
import commands.ArtistCommands.AddEventCommand;
import commands.ArtistCommands.RemoveAlbumCommand;
import commands.ArtistCommands.RemoveEventCommand;
import commands.ArtistCommands.ShowAlbumsCommand;
import commands.DatabaseCommands.AddUserCommand;
import commands.DatabaseCommands.DeleteUserCommand;
import commands.DatabaseCommands.GetAllUsersCommand;
import commands.DatabaseCommands.GetOnlineUsersCommand;
import commands.HostCommands.AddAnnouncementCommand;
import commands.HostCommands.AddMerchCommand;
import commands.HostCommands.AddPodcastCommand;
import commands.HostCommands.RemoveAnnouncementCommand;
import commands.HostCommands.RemovePodcastCommand;
import commands.HostCommands.ShowPodcastCommand;
import commands.MusicPlayerCommands.AdBreakCommand;
import commands.MusicPlayerCommands.AddRemoveInPlaylistCommand;
import commands.MusicPlayerCommands.BackwardCommand;
import commands.MusicPlayerCommands.ForwardCommand;
import commands.MusicPlayerCommands.LikeCommand;
import commands.MusicPlayerCommands.LoadCommand;
import commands.MusicPlayerCommands.LoadRecommendationsCommand;
import commands.MusicPlayerCommands.NextCommand;
import commands.MusicPlayerCommands.PlayPauseCommand;
import commands.MusicPlayerCommands.PrevCommand;
import commands.MusicPlayerCommands.RepeatCommand;
import commands.MusicPlayerCommands.SearchCommand;
import commands.MusicPlayerCommands.SelectCommand;
import commands.MusicPlayerCommands.ShowPlaylistsCommand;
import commands.MusicPlayerCommands.ShuffleCommand;
import commands.MusicPlayerCommands.StatusCommand;
import commands.MusicPlayerCommands.SwitchVisibilityCommand;
import commands.Statistics.GetTop5AlbumsCommand;
import commands.Statistics.GetTop5ArtistsCommand;
import commands.Statistics.GetTop5PlaylistsCommand;
import commands.Statistics.GetTop5SongsCommand;
import commands.Statistics.WrappedCommand;
import commands.UserCommands.BuyMerchCommand;
import commands.UserCommands.BuyPremiumCommand;
import commands.UserCommands.CancelPremiumCommand;
import commands.UserCommands.ChangePageCommand;
import commands.UserCommands.CreatePlaylistCommand;
import commands.UserCommands.FollowCommand;
import commands.UserCommands.GetNotifications;
import commands.UserCommands.NextPageCommand;
import commands.UserCommands.PreviousPageCommand;
import commands.UserCommands.PrintCurrentPageCommand;
import commands.UserCommands.SeeMerchCommand;
import commands.UserCommands.ShowPreferredSongs;
import commands.UserCommands.SubscribeCommand;
import commands.UserCommands.SwitchConnectionStatusCommand;
import commands.UserCommands.UpdateRecommendationsCommand;
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

    /* For recommendations. */
    @Getter
    private String recommendationType;

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

    public void setRecommendationType(final String recommendationType) {
        this.recommendationType = recommendationType;
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
            case "search" ->
                commands.add(new SearchCommand(command, username, searchcmd,
                        timestamp, type, filters));
            case "select" ->
                commands.add(new SelectCommand(command, username, timestamp, itemNumber));
            case "load" ->
                commands.add(new LoadCommand(command, username, timestamp));
            case "status" ->
                commands.add(new StatusCommand(command, username, timestamp));
            case "playPause" ->
                commands.add(new PlayPauseCommand(command, username, timestamp));
            case "createPlaylist" ->
                commands.add(new CreatePlaylistCommand(command, username, timestamp, playlistName));
            case "addRemoveInPlaylist" ->
                commands.add(new AddRemoveInPlaylistCommand(command, username,
                    timestamp, playlistId));
            case "like" ->
                commands.add(new LikeCommand(command, username, timestamp));
            case "showPlaylists" ->
                commands.add(new ShowPlaylistsCommand(command, username, timestamp));
            case "showPreferredSongs" ->
                commands.add(new ShowPreferredSongs(command, username, timestamp));
            case "follow" ->
                commands.add(new FollowCommand(command, username, timestamp));
            case "getTop5Songs" ->
                commands.add(new GetTop5SongsCommand(command, username, timestamp));
            case "getTop5Playlists" ->
                commands.add(new GetTop5PlaylistsCommand(command, username, timestamp));
            case "repeat" ->
                commands.add(new RepeatCommand(command, username, timestamp));
            case "shuffle" ->
                commands.add(new ShuffleCommand(command, username, timestamp, seed));
            case "forward" ->
                commands.add(new ForwardCommand(command, username, timestamp));
            case "backward" ->
                commands.add(new BackwardCommand(command, username, timestamp));
            case "next" ->
                commands.add(new NextCommand(command, username, timestamp));
            case "prev" ->
                commands.add(new PrevCommand(command, username, timestamp));
            case "switchVisibility" ->
                commands.add(new SwitchVisibilityCommand(command, username, timestamp, playlistId));
            case "addUser" ->
                commands.add(new AddUserCommand(command, username, timestamp, age, city, type));
            case "deleteUser" ->
                commands.add(new DeleteUserCommand(command, username, timestamp));
            case "switchConnectionStatus" ->
                commands.add(new SwitchConnectionStatusCommand(command, username, timestamp));
            case "addAlbum" ->
                commands.add(new AddAlbumCommand(command, username, timestamp, name,
                        releaseYear, description, songs));
            case "removeAlbum" ->
                commands.add(new RemoveAlbumCommand(command, username, timestamp, name));
            case "showAlbums" ->
                commands.add(new ShowAlbumsCommand(command, username, timestamp));
            case "printCurrentPage" ->
                commands.add(new PrintCurrentPageCommand(command, username, timestamp));
            case "addEvent" ->
                commands.add(new AddEventCommand(command, username, timestamp,
                        date, name, description));
            case "removeEvent" ->
                commands.add(new RemoveEventCommand(command, username, timestamp, name));
            case "addMerch" ->
                commands.add(new AddMerchCommand(command, username, timestamp,
                        price, name, description));
            case "addPodcast" ->
                commands.add(new AddPodcastCommand(command, username, timestamp, name, episodes));
            case "removePodcast" ->
                commands.add(new RemovePodcastCommand(command, username, timestamp, name));
            case "showPodcasts" ->
                commands.add(new ShowPodcastCommand(command, username, timestamp));
            case "addAnnouncement" ->
                commands.add(new AddAnnouncementCommand(command, username, timestamp,
                        name, description));
            case "removeAnnouncement" ->
                commands.add(new RemoveAnnouncementCommand(command, username, timestamp, name));
            case "changePage" ->
                commands.add(new ChangePageCommand(command, username, timestamp, nextPage));
            case "getTop5Albums" ->
                commands.add(new GetTop5AlbumsCommand(command, username, timestamp));
            case "getTop5Artists" ->
                commands.add(new GetTop5ArtistsCommand(command, username, timestamp));
            case "getAllUsers" ->
                commands.add(new GetAllUsersCommand(command, username, timestamp));
            case "getOnlineUsers" ->
                commands.add(new GetOnlineUsersCommand(command, username, timestamp));
            case "wrapped" ->
                commands.add(new WrappedCommand(command, username, timestamp));
            case "buyPremium" ->
                commands.add(new BuyPremiumCommand(command, username, timestamp));
            case "cancelPremium" ->
                commands.add(new CancelPremiumCommand(command, username, timestamp));
            case "getNotifications" ->
                commands.add(new GetNotifications(command, username, timestamp));
            case "subscribe" ->
                commands.add(new SubscribeCommand(command, username, timestamp));
            case "buyMerch" ->
                commands.add(new BuyMerchCommand(command, username, timestamp, name));
            case "seeMerch" ->
                commands.add(new SeeMerchCommand(command, username, timestamp));
            case "updateRecommendations" ->
                commands.add(new UpdateRecommendationsCommand(command, username,
                        timestamp, recommendationType));
            case "previousPage" ->
                commands.add(new PreviousPageCommand(command, username, timestamp));
            case "nextPage" ->
                commands.add(new NextPageCommand(command, username, timestamp));
            case "adBreak" ->
                commands.add(new AdBreakCommand(command, username, timestamp, price));
            case "loadRecommendations" ->
                commands.add(new LoadRecommendationsCommand(command, username, timestamp));
            default -> System.out.println("Command not found.");
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
        switch (type) {
            case "song":
                if (searchcmd == null) {
                    this.searchcmd = new SearchSong();
                }
            case "podcast":
                if (searchcmd == null) {
                    this.searchcmd = new SearchPodcast();
                }
            case "playlist":
                if (searchcmd == null) {
                    this.searchcmd = new SearchPlaylist();
                }
            case "artist":
                if (searchcmd == null) {
                    this.searchcmd = new SearchArtist();
                }
            case "album":
                if (searchcmd == null) {
                    this.searchcmd = new SearchAlbum();
                }
            case "host":
                if (searchcmd == null) {
                    this.searchcmd = new SearchHost();
                }
            default :
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
