package main;

import checker.Checker;
import checker.CheckerConstants;
import commands.CommandRunner;
import database.MyDatabase;
import fileio.input.CommandInput;
import fileio.input.LibraryInput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        /* Read the library. */
        LibraryInput.setLibraryPath(LIBRARY_PATH);
        LibraryInput library = LibraryInput.getInstance();


        /* Read commands and transfer input classes in data classes. */
        CommandInput[] commands = objectMapper.readValue(new File("input/" + filePathInput),
                CommandInput[].class);

        /* Create the database. */
        MyDatabase.setLibrary(library);
        MyDatabase database = MyDatabase.getInstance();

        /* Construct my specific commands. */
        for (CommandInput command : commands) {
            command.constructCommand();
        }

        /* Create output. */
        ArrayNode outputs = objectMapper.createArrayNode();

        /* Iterate over all commands and execute them. */
        for (CommandRunner command : CommandInput.getCommands()) {
            outputs.add(command.execute(database));
        }

        /* Clear everything. */
        CommandInput.getCommands().clear();
        database.clearDataBase();

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), outputs);
    }
}
