package pages;

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
}
