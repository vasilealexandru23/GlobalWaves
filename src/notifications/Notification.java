package notifications;

import lombok.Getter;

public final class Notification {
    @Getter
    private String name;
    @Getter
    private String description;

    public Notification(final String name, final String description) {
        this.name = name;
        this.description = description;
    }
}
