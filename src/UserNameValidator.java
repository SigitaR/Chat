import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sigute on 12/6/2017.
 */
public class UserNameValidator {

    private Pattern pattern;
    private Matcher matcher;

    private static final String USERNAME_PATTERN = "^[A-Za-z0-9_-]{1,12}$";

    public UserNameValidator(){
        pattern = Pattern.compile(USERNAME_PATTERN);
    }

    public boolean validate(final String username){

        matcher = pattern.matcher(username);
        return matcher.matches();

    }

    public boolean unique(final String username) {
        Set<Server.ServerThread> threadList = Server.getThreadList();
        boolean result = false;
        for (Server.ServerThread t : threadList) {
            String name = t.toString();
            if (name.equals(username)) {
                result = false;
            } else {
                result = true;
            }
        }
        return result;
    }

}
