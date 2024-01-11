package commands.Statistics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.Command;
import commands.CommandRunner;
import database.MyDatabase;

public final class EndProgramCommand extends Command implements CommandRunner {
    public EndProgramCommand(final String command) {
        super(command, null, null);
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();
        output.put("command", "endProgram");
        output.put("result", database.statsArtist());
        return output;
    }
}
