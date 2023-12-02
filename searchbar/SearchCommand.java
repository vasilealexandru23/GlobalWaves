package searchbar;

import java.util.ArrayList;

import mydata.Playlist;
import mydata.Podcast;
import mydata.Song;

public class SearchCommand {
    private String type;
    private Filters filters;

    public SearchCommand() {
    }

    public final String getType() {
        return type;
    }

    public final void setType(final String type) {
        this.type = type;
    }

    public final Filters getFilters() {
        return filters;
    }

    public final void setFilters(final Filters filters) {
        this.filters = filters;
    }

    /**
     * Function that search for a track with particular filters.
     * @param who                   user's name who request search
     * @param createdPlaylists      all createdPlaylists
     * @param songs                 all songs from library
     * @param podcasts              all podcasts from library
     * @param myfilters             filters requested
     * @return                      search result
     */
    public SearchCommand search(final String who,
        final ArrayList<Playlist> createdPlaylists,
        final ArrayList<Song> songs,
        final ArrayList<Podcast> podcasts,
        final Filters myfilters) {
        switch (this.type) {
            case "song" -> {
                SearchSong ssong = new SearchSong();
                ssong.setType(type);
                /* Compute songs. */
                ssong.searchSongs(songs, myfilters);
                return ssong;
            }
            case "podcast" -> {
                SearchPodcast ppodcast = new SearchPodcast();
                ppodcast.setType(type);
                /* Compute podcasts. */
                ppodcast.searchPodcasts(podcasts, myfilters);
                return ppodcast;
            }
            case "playlist" -> {
                SearchPlaylist pplaylist = new SearchPlaylist();
                pplaylist.setType(type);
                /* Compute playlists. */
                pplaylist.searchPlaylist(who, createdPlaylists,
                        myfilters);
                return pplaylist;
            }
            default -> {
                return null;
            }
        }
    }
}
