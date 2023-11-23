package searchbar;

import lombok.Getter;
import mydata.PodcastData;

import java.util.ArrayList;

public final class SearchPodcast extends SearchCommand {
    @Getter
    private ArrayList<PodcastData> results;

    /* Maximum search size. */
    private final int maxSearch = 5;

    public void setResults(final ArrayList<PodcastData> results) {
        this.results = results;
    }

    /**
     * Function that finds podcasts with given filters.
     * @param podcasts  collection where to search for
     * @param myfilters specified criteria for podcasts.
     */
    public void searchPodcasts(final ArrayList<PodcastData> podcasts, final Filters myfilters) {
        results = new ArrayList<>();

        for (PodcastData podcast : podcasts) {
            boolean podcastWorks = podcast.getName() == null
                    || myfilters.getName() == null
                    || podcast.getName().startsWith(myfilters.getName());

            if (podcast.getOwner() != null && myfilters.getOwner() != null
                    && !podcast.getOwner().equals(myfilters.getOwner())) {
                podcastWorks = false;
            }

            if (podcastWorks) {
                results.add(podcast);
                if (results.size() == maxSearch) {
                    break;
                }
            }
        }
    }
}