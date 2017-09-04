package project.allstate.speakitvisualcommunication;

import org.junit.Test;
import org.junit.Before;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Ashley on 30/08/2017.
 */
public class UserTest {

    String loginName;

    @Before
    public void setUp() throws Exception {
        loginName = "Admin";
    }

    /**
     *
     */
    @Test
    public void testUserDefaultConstructor() {
        User user = new User();
        assertNotNull(user);
    }

    /**
     *
     */
    @Test
    public void testUserConstructorWithArguments() throws Exception {
        User user = new User(loginName);
        assertNotNull(user);

        assertEquals(user, loginName);

    }

    @Test
    public void getLoginName() throws Exception {
        User user = new User();
        user.setLoginName(loginName);
        assertEquals(user, user.getLoginName());

    }

    @Test
    public void setLoginName() throws Exception {
        User user = new User();
        user.setLoginName(loginName);
        assertEquals(user, user.getLoginName());

    }

}