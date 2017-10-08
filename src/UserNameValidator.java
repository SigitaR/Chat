import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sigute on 10/4/2017.
 */

//taken from https://www.mkyong.com/regular-expressions/how-to-validate-username-with-regular-expression/

public class UserNameValidator {
    public static Pattern pattern;
    private Matcher matcher;
    private Vector usernames = new Vector();

    private static final String USERNAME_PATTERN = "^[A-Za-z0-9_-]{1,12}$";

    public UserNameValidator(){

        pattern = Pattern.compile(USERNAME_PATTERN);
    }

    public boolean validate(final String username){

        matcher = pattern.matcher(username);
        return matcher.matches();

    }

    //This one does not work :)

    public boolean checkDuplicates(final String username) {

        Server s = new Server();
        usernames = s.getUsernames();
        usernames.add(username);

        if (validate(username)) {
            for (int i = 0; i < usernames.size() - 1; i++) {
                for (int j = (i + 1); j < usernames.size(); j++) {
                    if (usernames.elementAt(i).toString().equalsIgnoreCase(
                            usernames.elementAt(j).toString())) {
                        System.out.println("User name exists.");
                        usernames.remove(username);
                        break;
                    }

                }

            }
            return false;
        } else {
            return true;
        }
    }
    }
