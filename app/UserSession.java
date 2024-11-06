public class UserSession {
    private static UserSession instance;
    private final String username;

    private UserSession(String username) {
        this.username = username;
    }

    public static void init(String username) {
        if (instance == null) {
            instance = new UserSession(username);
        }
    }

    public static UserSession getInstance() {
        if (instance == null) {
            throw new IllegalStateException("UserSession not initialized. Call init() after login.");
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }
}
