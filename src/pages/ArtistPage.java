package pages;

import java.util.ArrayList;

import database.Album;
import lombok.Getter;

public final class ArtistPage extends UserPage {
    public final class Event {
        @Getter
        private String name;
        @Getter
        private String date;
        @Getter
        private String description;

        public Event(final String name, final String date, final String description) {
            this.name = name;
            this.date = date;
            this.description = description;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public void setDate(final String date) {
            this.date = date;
        }

        public void setDescription(final String description) {
            this.description = description;
        }
    }

    public final class Merch {
        @Getter
        private String name;
        @Getter
        private Integer price;
        @Getter
        private String description;

        public Merch(final String name, final String description, final Integer price) {
            this.name = name;
            this.description = description;
            this.price = price;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public void setPrice(final Integer price) {
            this.price = price;
        }

        public void setDescription(final String description) {
            this.description = description;
        }
    }

    @Getter
    private ArrayList<Event> events = new ArrayList<>();
    @Getter
    private ArrayList<Merch> merchs = new ArrayList<>();
    @Getter
    private ArrayList<Album> albums;

    public ArtistPage(final ArrayList<Album> albums) {
        this.albums = albums;
    }

    @Override
    public String printCurrentPage() {
        String result = "Albums:\n\t[";

        /* Add albums. */
        boolean firstAlbum = true;
        for (Album album : albums) {
            if (firstAlbum) {
                result += album.getName();
                firstAlbum = false;
            } else {
                result += ", " + album.getName();
            }
        }

        result += "]\n\nMerch:\n\t[";

        /* Add merchs. */
        boolean firstMerch = true;
        for (Merch merch : merchs) {
            if (firstMerch) {
                result += merch.getName();
                firstMerch = false;
                result += " - " + merch.getPrice() + ":\n\t" + merch.getDescription();
            } else {
                result += ", " + merch.getName();
                result += " - " + merch.getPrice() + ":\n\t" + merch.getDescription();
            }
        }

        result += "]\n\nEvents:\n\t[";

        /* Add events. */
        boolean firstEvent = true;
        for (Event event : events) {
            if (firstEvent) {
                result += event.getName();
                firstEvent = false;
                result += " - " + event.getDate() + ":\n\t" + event.getDescription();
            } else {
                result += ", " + event.getName();
                result += " - " + event.getDate() + ":\n\t" + event.getDescription();
            }
        }

        result += "]";
        return result;
    }
}
