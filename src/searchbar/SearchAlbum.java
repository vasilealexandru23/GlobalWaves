package searchbar;

import java.util.ArrayList;

import database.Album;
import lombok.Getter;
import musicplayer.AudioCollection;

public final class SearchAlbum extends Search {
    @Getter
    private ArrayList<Album> results;

    public void setResults(final ArrayList<Album> results) {
        this.results = results;
    }

    /**
     * Function that finds albums with given filters.
     * @param albums            collection where to search for
     * @param myfilters         specified criteria for albums.
     */
    public void searchTrack(final ArrayList<Album> albums, final Filters myfilters) {
        results = new ArrayList<>();

        for (Album album : albums) {
            /* Check name of album. */
            boolean goalalbum = album.getName() == null || myfilters.getName() == null
                    || album.getName().startsWith(myfilters.getName());

            /* Check album by artist. */
            if (album.getOwner() != null && myfilters.getOwner() != null
                && !album.getOwner().equals(myfilters.getOwner())) {
                goalalbum = false;
            }

            /* Check album by description. */
            if (album.getDescription() != null && myfilters.getDescription() != null
                && !album.getDescription().contains(myfilters.getDescription())) {
                goalalbum = false;
            }

            if (goalalbum) {
                results.add(album);
                if (results.size() == maxSearch) {
                    break;
                }
            }
        }
    }

    @Override
    public AudioCollection getSelect(final int selectIndex)  {
        if (selectIndex >= results.size()) {
            return null;
        }

        return results.get(selectIndex);
    }
}
