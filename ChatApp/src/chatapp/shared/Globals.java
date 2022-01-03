package chatapp.shared;

public class Globals {

    public final static String PUBLIC_GROUP_NAME = "All";
    public static int PORT = 22866;
    public static String IP = "127.0.0.1";
    public final static String ALLOWED_CHARACTERS = "^[\\w\\d_]{3,14}$";
    public static boolean TESTING = false;

    public SystemHelper systemHelper;
    public Listeners listeners = new Listeners();

}
