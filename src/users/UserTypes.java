package users;

import lombok.Getter;
import java.util.ArrayList;

import database.MyDatabase;

@Getter
public abstract class UserTypes {
    public enum UserType {
        USER, ARTIST, HOST
    }

    private String username;
    private int age;
    private String city;
    private UserType userType;

    public UserTypes() {
    }

    public UserTypes(final String username, final int age, final String city) {
        this.username = username;
        this.age = age;
        this.city = city;
    }

    public final void setUsername(final String username) {
        this.username = username;
    }

    public final void setAge(final int age) {
        this.age = age;
    }

    public final void setCity(final String city) {
        this.city = city;
    }

    public final void setUserType(final UserType userType) {
        this.userType = userType;
    }

    /**
     * Function that returns the user with the given username.
     * @param allUsersCreated all users from database
     * @param username        username of the user we want to find
     * @return return the user with the given username
     */
    public static UserTypes findMyUser(final ArrayList<UserTypes> allUsersCreated,
            final String username) {
        for (UserTypes user : allUsersCreated) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Function that checks if the user can be removed.
     * @param database database
     * @return true if the user can be removed
     */
    protected abstract boolean canBeRemoved(MyDatabase database);

    /**
     * Function that returns the content of user's page.
     * @return return the content of user's page
     */
    public abstract String printCurrentPage();

    /**
     * Function that removes the user from database.
     * @param database database
     * @return status of command
     */
    public abstract String removeUser(MyDatabase database);
}
