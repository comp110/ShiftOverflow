package server;

import comp110.Credentials;

public class CommandHandler {
  public static SController controller = new SController();

  public void gitLogin(String user, String pass) {
    Credentials credentials = new Credentials(user, pass);
    controller.uiUsernamePasswordCallback(credentials);
  }
  
  
}
