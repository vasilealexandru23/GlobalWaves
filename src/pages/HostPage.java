package pages;

import java.util.ArrayList;

import database.Episode;
import database.Podcast;
import lombok.Getter;
import users.UserHost.Announcement;

public final class HostPage extends UserPage {
    @Getter
    private ArrayList<Podcast> podcasts;
    @Getter
    private ArrayList<Announcement> announcements = new ArrayList<>();

    public HostPage(final ArrayList<Podcast> podcasts,
        final ArrayList<Announcement> announcements) {
        this.podcasts = podcasts;
        this.announcements = announcements;
    }

    /**
     * Function that prints the current page.
     */
    public String printCurrentPage() {
        String result = "Podcasts:\n\t[";

        /* Add podcasts and episodes. */
        boolean firstPodcast = true;
        for (Podcast podcast : podcasts) {
            if (!firstPodcast) {
                result += "]\n, ";
            }
            result += podcast.getName() + ":\n\t[";
            firstPodcast = false;
            boolean firstEpisode = true;
            for (Episode episode : podcast.getEpisodes()) {
                if (!firstEpisode) {
                    result += ", ";
                }
                result += episode.getName() + " - " + episode.getDescription();
                firstEpisode = false;
            }
        }

        result += "]\n]\n\nAnnouncements:\n\t[";

        /* Add announcements. */
        boolean firstAnnouncement = true;
        for (Announcement announcement : announcements) {
            if (!firstAnnouncement) {
                result += ", ";
            }
            result += announcement.getNameAnnouncement() + ":\n\t" + announcement.getDescription();
            firstAnnouncement = false;
        }

        result += "\n]";

        return result;
    }
}
