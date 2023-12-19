package searchbar;

import java.util.ArrayList;

import lombok.Getter;
import users.UserHost;
import users.UserTypes;

public final class SearchHost extends Search {
    @Getter
    private ArrayList<UserHost> results;

    public void setResults(final ArrayList<UserHost> results) {
        this.results = results;
    }

    /**
     * Function that searches for a host.
     * @param allUsersCreated       all users created
     * @param myFilters             the filters applied
     */
    public void searchHost(final ArrayList<UserTypes> allUsersCreated, final Filters myFilters) {
        results = new ArrayList<>();
        for (UserTypes user : allUsersCreated) {
            if (user.getUserType() == UserTypes.UserType.HOST
                    && user.getUsername().startsWith(myFilters.getName())) {
                results.add((UserHost) user);
                if (results.size() == maxSearch) {
                    break;
                }
            }
        }
    }

    /**
     * Function that returns the host at the selected index.
     * @param selectIndex           The index of the host to be returned.
     * @return                      The host at the selected index.
     */
    public UserHost getHost(final int selectIndex) {
        if (selectIndex >= results.size()) {
            return null;
        }
        return results.get(selectIndex);
    }
}
