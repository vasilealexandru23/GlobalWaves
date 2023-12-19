package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import database.MyDatabase;
import musicplayer.MusicPlayer;

public final class AddUserCommand extends Command implements CommandRunner {
    private Integer age;
    private String city;
    private String type;

    public AddUserCommand(final String command, final String username,
            final Integer timestamp, final Integer age, final String city, final String type) {
        super(command, username, timestamp);
        this.age = age;
        this.city = city;
        this.type = type;
    }

    @Override
    public ObjectNode execute(final MyDatabase database) {
        ObjectNode output = new ObjectMapper().createObjectNode();

        outputCommand(output);
        MusicPlayer.setTimestamp(getTimestamp());

        output.put("message", database.addUser(getUsername(), age, city, type));
        return output;
    }
}
