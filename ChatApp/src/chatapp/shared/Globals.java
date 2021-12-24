package chatapp.shared;

public class Globals {

    public final static String PUBLIC_GROUP_NAME = "All";
    public final static int PORT = 22866;
    public final static String ALLOWED_CHARACTERS = "^[\\w\\d_]+$";
    public static boolean TESTING = false;

    public Listeners listeners = new Listeners();

}
