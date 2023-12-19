package searchbar;

import musicplayer.AudioCollection;

public class Search {
    private String type;
    private Filters filters;

    /* Maximum search size. */
    protected final int maxSearch = 5;

    public Search() {
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
     * Function that searches for a track or page.
     */
    public void searchTrack() {
    }

    /**
     * Function that selects a track or page from the search results.
     * @param selectIndex           the index of the track or page
     * @return                      the track or page
     */
    public AudioCollection getSelect(final int selectIndex) {
        return null;
    }
}
