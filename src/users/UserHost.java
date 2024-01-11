package users;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import database.Episode;
import database.MyDatabase;
import database.Podcast;
import lombok.Getter;
import musicplayer.AudioCollection;
import pages.HostPage;

public final class UserHost extends UserTypes {
    public UserHost(final String username, final int age, final String city) {
        super(username, age, city);
        setUserType(UserType.HOST);
    }

    public final class Announcement {
        @Getter
        private String nameAnnouncement;
        @Getter
        private String description;

        Announcement(final String nameAnnouncement, final String description) {
            this.nameAnnouncement = nameAnnouncement;
            this.description = description;
        }

        public void setMessage(final String nameAnnouncement) {
            this.nameAnnouncement = nameAnnouncement;
        }

        public void setDescription(final String description) {
            this.description = description;
        }
    }

    @Getter
    private ArrayList<Announcement> announcements = new ArrayList<>();
    @Getter
    private ArrayList<Podcast> podcasts = new ArrayList<>();
    @Getter
    private HostPage hostPage = new HostPage(this, podcasts, announcements);
    @Getter
    private ArrayList<UserNormal> subscribers = new ArrayList<UserNormal>();

    public void setAnnouncements(final ArrayList<Announcement> announcements) {
        this.announcements = announcements;
    }

    /**
     * Function that creates a new announcement and adds to the host's page.
     */
    public void addAnnouncement(final String name, final String description) {
        announcements.add(new Announcement(name, description));
    }

    /**
     * Funnction that puts in ouput the host's podcasts.
     */
    public void showPodcasts(final ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        for (Podcast podcast : podcasts) {
            /* Add name. */
            ObjectNode newResult = objectMapper.createObjectNode();
            newResult.put("name", podcast.getName());

            /* Add episodes. */
            ArrayNode episodeOut = objectMapper.createArrayNode();
            for (Episode episode : podcast.getEpisodes()) {
                episodeOut.add(episode.getName());
            }

            /* Add to ouput. */
            newResult.put("episodes", episodeOut);
            output.add(newResult);
        }
    }

    @Override
    public String printCurrentPage() {
        return hostPage.printPage();
    }

    @Override
    protected boolean canBeRemoved(final MyDatabase database) {
        /* Check if other users interact with host's podcasts. */
        for (UserTypes user : database.getAllUsersCreated()) {
            if (user.getUserType() == UserType.USER) {

                UserNormal myUser = ((UserNormal) user);
                if (myUser.getSelectedPage() != null) {
                    if (myUser.getSelectedPage() == this.hostPage) {
                        return false;
                    }
                }

                if (myUser.getMusicPlayer().getPlayback() == null) {
                    continue;
                }

                myUser.getMusicPlayer().getPlayback().checkPlayback();
                AudioCollection currTrack = myUser.getMusicPlayer().getPlayback().getCurrTrack();
                if (currTrack == null) {
                    continue;
                }

                if (currTrack.getType() == AudioCollection.AudioType.PODCAST) {
                    Podcast currPodcast = (Podcast) currTrack;
                    if (currPodcast.getOwner().equals(this.getUsername())) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public String removeUser(final MyDatabase database) {
        if (!canBeRemoved(database)) {
            return getUsername() + " can't be deleted.";
        }

        for (UserTypes user : database.getAllUsersCreated()) {
            if (user.getUserType() == UserType.USER) {

                UserNormal myUser = ((UserNormal) user);
                if (myUser.getSelectedPage() != null) {
                    if (myUser.getSelectedPage() == this.hostPage) {
                        myUser.setSelectedPage(null);
                    }
                }
            }
        }

        for (Podcast podcast : podcasts) {
            database.getAllPodcastsCreated().remove(podcast);
        }

        database.getAllUsersCreated().remove(this);

        return getUsername() + " was successfully deleted.";
    }

    /**
     * Funciton that gets top listened episodes.
     * @return          top listened episodes
     */
    private ArrayList<Episode> topEpisodes() {
        ArrayList<Episode> allEpisodes = new ArrayList<>();
        for (Podcast podcast : podcasts) {
            allEpisodes.addAll(podcast.getEpisodes());
        }
        for (Podcast podcast : MyDatabase.getInstance().getAllPodcastsCreated()) {
            if (podcast.getOwner().equals(this.getUsername())) {
                for (Episode episode : podcast.getEpisodes()) {
                    if (episode.getAllPlays() != 0) {
                        allEpisodes.add(episode);
                    }
                }
            }
        }

        allEpisodes.sort((episode1, episode2) -> {
            if (episode1.getAllPlays() == episode2.getAllPlays()) {
                return episode1.getName().compareTo(episode2.getName());
            }

            return episode2.getAllPlays() - episode1.getAllPlays();
        });

        return allEpisodes;
    }

    @Override
    public ObjectNode getStatistics() {
        ObjectNode stats = new ObjectMapper().createObjectNode();
        ObjectNode topEpisodes = new ObjectMapper().createObjectNode();
        final int top5 = 5;

        topEpisodes().stream().limit(top5)
            .forEach(episode -> topEpisodes.put(episode.getName(), episode.getAllPlays()));

        int listeners = 0;
        for (UserTypes user : MyDatabase.getInstance().getAllUsersCreated()) {
            if (user.getUserType() == UserType.USER) {
                UserNormal myUser = (UserNormal) user;
                if (myUser.getPlaysHost(this) != 0) {
                    listeners++;
                }
            }
        }

        stats.put("topEpisodes", topEpisodes);
        stats.put("listeners", listeners);

        return stats;
    }
}
