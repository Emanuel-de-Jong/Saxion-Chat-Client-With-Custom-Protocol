package chatapp.tests;

import chatapp.shared.Globals;
import org.junit.Test;

import static org.junit.Assert.*;

public class LogInTests {

    @Test
    public void regexTester() {
        assertTrue("userName_12".matches(Globals.ALLOWED_CHARACTERS));
        assertTrue("asdassa24__".matches(Globals.ALLOWED_CHARACTERS));
        assertTrue("asfdjio__33".matches(Globals.ALLOWED_CHARACTERS));
        assertFalse("useérName_12".matches(Globals.ALLOWED_CHARACTERS));
        assertFalse("us$erName_12".matches(Globals.ALLOWED_CHARACTERS));
    }

}
