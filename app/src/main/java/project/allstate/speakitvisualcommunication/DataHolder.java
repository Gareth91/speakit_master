package project.allstate.speakitvisualcommunication;

/**
 * Class holds the login name
 * Created by Gareth on 22/08/2017.
 */
public class DataHolder {

    /**
     * The login name
     */
    private String login;

    /**
     * Get the login name
     * @return
     */
    public String getLogin() {
        return login;
    }

    /**
     * Set the login name
     * @param login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Static instance of the class
     */
    private static final DataHolder holder = new DataHolder();

    /**
     * Get the static instance
     * @return
     */
    public static DataHolder getInstance() {
        return holder;
    }
}
