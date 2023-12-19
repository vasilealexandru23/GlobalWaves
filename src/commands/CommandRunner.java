package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;

import database.MyDatabase;

/* Command pattern. */
public interface CommandRunner {
    /**
     * Execute the command.
     * @param database      The database.
     * @return              The command output.
     */
    ObjectNode execute(MyDatabase database);
}
