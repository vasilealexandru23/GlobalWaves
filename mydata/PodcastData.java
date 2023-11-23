package mydata;

import java.util.ArrayList;

import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;

public final class PodcastData {
    private String name;
    private String owner;
    private ArrayList<EpisodeData> episodes;
    private EpisodeData currEpisode;
    private int indexEpisode;

    public PodcastData(final String name, final String owner) {
        this.name = name;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public ArrayList<EpisodeData> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(final ArrayList<EpisodeData> episodes) {
        this.episodes = episodes;
    }

    public EpisodeData getCurrEpisode() {
        return currEpisode;
    }

    public void setCurrEpisode(final EpisodeData episode) {
        this.currEpisode = episode;
    }

    public int getIndexEpisode() {
        return indexEpisode;
    }

    public void setIndexEpisode(final int index) {
        this.indexEpisode = index;
    }

    /**
     * Function that returns data from library.getPodcasts() in another structure.
     * @param myPodcasts        podcasts from library
     * @return                  return the new array with data
     */
    public static ArrayList<PodcastData> initPodcastData(final ArrayList<PodcastInput> myPodcasts) {
        ArrayList<PodcastData> inputPodcasts = new ArrayList<>();

        for (PodcastInput podcast : myPodcasts) {
            PodcastData newPodcast = new PodcastData(podcast.getName(),
                    podcast.getOwner());

            ArrayList<EpisodeData> episodes = new ArrayList<>();
            for (EpisodeInput episode : podcast.getEpisodes()) {
                EpisodeData newEpisode = new EpisodeData(episode.getName(),
                        episode.getDuration(), episode.getDescription());
                episodes.add(newEpisode);
            }

            newPodcast.setEpisodes(episodes);
            inputPodcasts.add(newPodcast);
        }
        return inputPodcasts;
    }
}
