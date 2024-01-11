package pages;

import users.UserNormal;

/* Template Method design pattern. */
public abstract class UserPage {
    protected final Integer maxResult = 5;

    /**
     * Function that prints the current page
     * and can easily be extended with more functionalities.
     * @return      A string containing the current page content.
     */
    public String printPage() {
        return printCurrentPage();
    }

    /**
     * Function that prints the current page.
     * @return      A string containing the current page content.
     */
    public abstract String printCurrentPage();

    /**
     * Function that buys a merch from the page.
     * The default return is that the merch cannot be bought.
     * For artist pages, this function is overriden.
     * @param       name of the merch
     * @return      A string containing the result of the buy.
     */
    public String buyMerch(final UserNormal buyer, final String nameMerch) {
        return "Cannot buy merch from this page.";
    }

    /**
     * Function that subscribes a user to the page.
     * @param user  The user that subscribes.
     * @return      A string containing the result of the subscribe.
     */
    public abstract String subscribe(UserNormal user);
}
