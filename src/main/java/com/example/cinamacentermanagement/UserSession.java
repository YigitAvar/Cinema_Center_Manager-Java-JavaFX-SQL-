package com.example.cinamacentermanagement;

/**
 * Manages the user session for the cinema center management system.
 * This class stores the username of the currently logged-in user.
 */
public class UserSession {

    private static String loggedInUsername;

    /**
     * Sets the username of the currently logged-in user.
     *
     * @param username the username of the logged-in user
     */
    public static void setLoggedInUsername(String username) {
        loggedInUsername = username;
    }

    /**
     * Returns the username of the currently logged-in user.
     *
     * @return the username of the logged-in user
     */
    public static String getLoggedInUsername() {
        return loggedInUsername;
    }

    /**
     * Clears the current user session.
     * This method sets the logged-in username to null.
     */
    public static void clearSession() {
        loggedInUsername = null;
    }
}
