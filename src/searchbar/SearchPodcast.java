package searchbar;

import lombok.Getter;
import musicplayer.AudioCollection;

import java.util.ArrayList;

import database.Podcast;

public final class SearchPodcast extends Search {
    @Getter
    private ArrayList<Podcast> results;

    public void setResults(final ArrayList<Podcast> results) {
        this.results = results;
    }

    /**
     * Function that finds podcasts with given filters.
     * @param podcasts  collection where to search for
     * @param myfilters specified criteria for podcasts.
     */
    public void searchTrack(final ArrayList<Podcast> podcasts, final Filters myfilters) {
        results = new ArrayList<>();
        for (Podcast podcast : podcasts) {
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

    @Override
    public AudioCollection getSelect(final int selectIndex) {
        if (selectIndex >= results.size()) {
            return null;
        }
        return results.get(selectIndex);
    }
}
