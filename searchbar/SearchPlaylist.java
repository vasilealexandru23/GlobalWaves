package searchbar;

import lombok.Getter;
import musicplayer.Playlist;

import java.util.ArrayList;

public final class SearchPlaylist extends SearchCommand {
    @Getter
    private ArrayList<Playlist> results;

    private final int maxSearch = 5;

    public void setResults(final ArrayList<Playlist> results) {
        this.results = results;
    }

    /**
     * @param who
     * @param createdPlaylists
     * @param myfilters
     */
    public void searchPlaylist(final String who,
            final ArrayList<Playlist> createdPlaylists,
            final Filters myfilters) {

        results = new ArrayList<>();

        /* Check public playlists. */
        for (Playlist playlist : createdPlaylists) {
            /* Check if this playlist is not visibly by who. */
            if (!playlist.isVisibility() && !playlist.getOwner().equals(who)) {
                continue;
            }

            boolean playlistworks = myfilters.getName() == null
                    || playlist.getName().startsWith(myfilters.getName());
            /* Check by name. */

            /* Check by owner. */
            if (myfilters.getOwner() != null
                    && !playlist.getOwner().equals(myfilters.getOwner())) {
                playlistworks = false;
            }

            if (playlistworks) {
                results.add(playlist);
                if (results.size() == maxSearch) {
                    break;
                }
            }

        }
    }
}
