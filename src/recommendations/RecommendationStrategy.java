package recommendations;

import users.UserNormal;

public interface RecommendationStrategy {
    /**
     * Function that creates a recommendation for a user.
     * @param user      the user for which the recommendation is created
     * @return          a string containing the result of the recommendation
     */
    String createRecommendation(UserNormal user);
}
