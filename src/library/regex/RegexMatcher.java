package library.regex;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegexMatcher {
  private static final String EMAIL = "[a-z_\\-\\.0-9]+[a-z0-9]+@[a-z0-9]+\\.[a-z]{3}";
  private static final String USERNAME = "[a-z]+[a-z0-9_\\-\\.]*[a-z0-9]+$";

  private static final Pattern pattern_email = 
                                Pattern.compile(EMAIL);
  private static final Pattern pattern_username = 
                                Pattern.compile(USERNAME);

  private static Matcher matcher  = null;

  public RegexMatcher() {
    // System.out.println("RegexMatcher constructor invoked");
  }
  public boolean checkEmail(String email) {
    matcher = pattern_email.matcher(email);
    return matcher.matches();
  }

  public boolean checkUsername(String username) {
    matcher = pattern_username.matcher(username);
    return matcher.matches();
  }
}
