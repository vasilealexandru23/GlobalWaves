package main;

import checker.Checker;
import checker.CheckerConstants;
import fileio.input.LibraryInput;
import musicplayer.MusicPlayer;
import musicplayer.Playback;
import mydata.InputData;
import mydata.Playlist;
import mydata.Podcast;
import mydata.Song;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your
 * implentation.
 */
public final class Main {
    static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";

    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePathInput  for input file
     * @param filePathOutput for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePathInput,
            final String filePathOutput)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LibraryInput library = objectMapper.readValue(new File(LIBRARY_PATH),
                LibraryInput.class);
        ArrayNode outputs = objectMapper.createArrayNode();

        /* Read commands and transfer input classes in data classes. */
        InputData[] commands = objectMapper.readValue(new File("input/" + filePathInput),
                InputData[].class);
        ArrayList<Podcast> inputPodcasts = Podcast.initPodcastData(library.getPodcasts());
        ArrayList<Song> inputSongs = Song.initSongsData(library.getSongs());
        /* Create for every user it's player. */
        ArrayList<MusicPlayer> players = new ArrayList<>();
        MusicPlayer.initPlayers(players, library, inputPodcasts);
        /* Create array to keep all created playlists. */
        ArrayList<Playlist> createdPlaylists = new ArrayList<>();
        /* Iterate over commands. */
        for (InputData command : commands) {
            /* Get the player && playback for the coresponding user && check it's status. */
            MusicPlayer currplayer = MusicPlayer.findMyPlayer(players, command.getUsername());
            Playback currPlayback = null;
            if (currplayer != null) {
                currPlayback = currplayer.getPlayback();
            }

            /* Update timeline. */
            MusicPlayer.setTimestamp(command.getTimestamp());

            /* Prepare output. */
            ObjectNode outputcmd = objectMapper.createObjectNode();
            String outMessage = null;
            ArrayNode outResults = null;
            command.ouputCommand(outputcmd);

            switch (command.getCommand()) {
                case "search" -> {
                    ArrayNode statusSearch = objectMapper.createArrayNode();
                    outputcmd.put("message", command.searchTrack(currplayer, createdPlaylists,
                            inputPodcasts, inputSongs, statusSearch));
                    outputcmd.put("results", statusSearch);
                }
                case "select" -> {
                    outMessage = currplayer.selectTrack(command.getSelectcmd().getItemNumber() - 1);
                }
                case "load" -> {
                    if (InputData.checkLoad(currplayer, outputcmd)) {
                        outMessage = currplayer.loadItem();
                    }
                }
                case "status" -> {
                    ObjectNode outstatus = objectMapper.createObjectNode();
                    InputData.checkStatus(currplayer, outstatus);
                    outputcmd.put("stats", outstatus);
                }
                case "playPause" -> {
                    currplayer.checkStatus();
                    if (InputData.checkPlayPause(currplayer, outputcmd)) {
                        outMessage = currplayer.getPlayback().playPause();
                    }
                }
                case "createPlaylist" -> {
                    String playlistName = command.getPlaylistName();
                    Playlist.createPlaylist(createdPlaylists,
                            playlistName, currplayer, outputcmd);
                }
                case "addRemoveInPlaylist" -> {
                    int playlistid = command.getPlaylistId();
                    if (InputData.checkAddRemove(currplayer, playlistid, outputcmd)) {
                        Playlist getplaylist = currplayer.getPlaylists().get(playlistid - 1);
                        Song getsong = (Song) currplayer.getSelectedTrack();
                        outMessage = Playlist.addRemoveInPlaylist(getplaylist, getsong);
                    }
                }
                case "like" -> {
                    if (InputData.checkLike(currplayer, outputcmd)) {
                        outMessage = currplayer.likeSong();
                    }
                }
                case "showPlaylists" -> {
                    ArrayNode playlists = objectMapper.createArrayNode();
                    Playlist.showPlaylists(currplayer.getPlaylists(), playlists);
                    outputcmd.put("result", playlists);
                }
                case "showPreferredSongs" -> {
                    ArrayNode outresults = objectMapper.createArrayNode();
                    for (String song : currplayer.likedSongNames()) {
                        outresults.add(song);
                    }
                    outputcmd.put("result", outresults);
                }
                case "follow" -> {
                    if (InputData.checkFollow(currplayer, outputcmd)) {
                        Playlist playlist = (Playlist) currplayer.getSelectedTrack();
                        outMessage = playlist.followPlaylist(currplayer);
                    }
                }
                case "getTop5Songs" -> {
                    outResults = objectMapper.createArrayNode();
                    MusicPlayer.getTop5Songs(inputSongs, players, outResults);
                }
                case "getTop5Playlists" -> {
                    outResults = objectMapper.createArrayNode();
                    Playlist.getTop5Playlists(createdPlaylists, outResults);
                }
                case "repeat" -> {
                    currplayer.checkStatus();
                    if (InputData.checkRepeat(currplayer, outputcmd)) {
                        outMessage = currPlayback.repeat();
                    }
                }
                case "shuffle" -> {
                    currplayer.checkStatus();
                    if (InputData.checkShuffle(currplayer, outputcmd)) {
                        outMessage = currPlayback.changeShuffle(command.getSeed());
                    }
                }
                case "forward" -> {
                    if (InputData.checkForBackWard(currplayer, outputcmd)) {
                        outMessage = ((Podcast) currPlayback.getCurrTrack()).forward(currPlayback);
                    }
                }
                case "backward" -> {
                    if (InputData.checkForBackWard(currplayer, outputcmd)) {
                        outMessage = ((Podcast) currPlayback.getCurrTrack()).backward(currPlayback);
                    }
                }
                case "next" -> {
                    outMessage = currplayer.nextTrack();
                }
                case "prev" -> {
                    outMessage = currplayer.prevTrack();
                }
                case "switchVisibility" -> {
                    outMessage = currplayer.switchVisibility(
                            command.getPlaylistId());
                }
                default -> {
                }
            }
            if (outMessage != null) {
                outputcmd.put("message", outMessage);
            }
            if (outResults != null) {
                outputcmd.put("result", outResults);
            }
            outputs.add(outputcmd);
        }
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), outputs);
    }
}
