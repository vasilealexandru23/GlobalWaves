package notifications;

public interface Observator {
    /**
     * Function that updates the
     * notifications bar with a new notification.
     * @param notification
     */
    void updateNotifications(Notification notification);
}
