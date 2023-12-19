package fileio.input;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

public final class LibraryInput {
    private static String libraryPATH;
    private static LibraryInput instanceLb = null;

    @Getter
    private ArrayList<SongInput> songs;
    private ArrayList<PodcastInput> podcasts;
    private ArrayList<UserInput> users;

    public LibraryInput() {
    }

    public static void setLibraryPath(final String path) {
        libraryPATH = path;
    }

    /**
     * Function that reads the library from the input file (SINGLETON PATTERN).
     * @return the library
     * @throws StreamReadException
     * @throws DatabindException
     * @throws IOException
     */
    public static LibraryInput getInstance()
            throws StreamReadException, DatabindException, IOException {
        if (instanceLb == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            instanceLb = objectMapper.readValue(new File(libraryPATH),
            LibraryInput.class);
        }
        return instanceLb;
    }

    public void setSongs(final ArrayList<SongInput> songs) {
        this.songs = songs;
    }

    public ArrayList<PodcastInput> getPodcasts() {
        return podcasts;
    }

    public void setPodcasts(final ArrayList<PodcastInput> podcasts) {
        this.podcasts = podcasts;
    }

    public ArrayList<UserInput> getUsers() {
        return users;
    }

    public void setUsers(final ArrayList<UserInput> users) {
        this.users = users;
    }
}
