package chatapp.shared;

import java.util.HexFormat;

public class Globals {

    public static String publicGroupName = "All";
    public static boolean testing = false;
    public static int port = 22866;
    public static byte[] pepper = HexFormat.of().parseHex("5fc6a31698e6306b656b7840f258a915");
    public Listeners listeners = new Listeners();

}
