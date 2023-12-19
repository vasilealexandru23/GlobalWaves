package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;

@Getter
public class Command {
    private String command;
    private String username;
    private Integer timestamp;

    /**
     * Output the command's general data to the output object.
     * @param output            The output object.
     */
    public void outputCommand(final ObjectNode output) {
        output.put("command", getCommand());
        if (username != null) {
            output.put("user", getUsername());
        }
        output.put("timestamp", timestamp);
    }

    public Command(final String command, final String username, final Integer timestamp) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
    }
}
