package sdis.spotify.common;

public class ServerMessages {
    public static final String SERVER_WAITING = "----Server waiting for Client----";
    public static final String WELCOME_MESSAGE = "Welcome to \n" +
            "░██████╗██████╗░░█████╗░████████╗██╗███████╗██╗░░░██╗\n" +
            "██╔════╝██╔══██╗██╔══██╗╚══██╔══╝██║██╔════╝╚██╗░██╔╝\n" +
            "╚█████╗░██████╔╝██║░░██║░░░██║░░░██║█████╗░░░╚████╔╝░\n" +
            "░╚═══██╗██╔═══╝░██║░░██║░░░██║░░░██║██╔══╝░░░░╚██╔╝░░\n" +
            "██████╔╝██║░░░░░╚█████╔╝░░░██║░░░██║██║░░░░░░░░██║░░░\n" +
            "╚═════╝░╚═╝░░░░░░╚════╝░░░░╚═╝░░░╚═╝╚═╝░░░░░░░░╚═╝░░░";
    public static final String USER_LOGGED_SUCCESSFULLY = "User successfully logged in";
    public static final String LOGIN_ERROR = "Err 401 ~ Credentials DO NOT MATCH. Try again";
    public static final String LOGIN_REQUIRED = "Err 403 ~ User login is required for this action";
    public static final String MAX_CONNECTIONS_REACHED_ERROR = "Err 503 ~ Max Number of connections reached.";
    public static final String MAX_LOGIN_ATTEMPTS_REACHED_ERROR = "Err 429 ~ Max Number of login attempts reached.";
}