package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RequestHandler implements HttpHandler {

  private CommandHandler handler;
  public static void main(String[] args) throws IOException {
    Server server = new Server(3650);
  }
  
  public RequestHandler(){
    handler = new CommandHandler();
  }

  public void handle(HttpExchange t) throws IOException {
    InputStream is = t.getRequestBody();

    this.readRequest(is);
    String response = "This is the response";
    t.sendResponseHeaders(200, response.length());
    OutputStream os = t.getResponseBody();
    os.write(response.getBytes());
    os.close();

  }

  private int readRequest(InputStream in) {
    Scanner s = new Scanner(in, "UTF-8");
    s.useDelimiter("&");

    String command = this.parseCommand(s);
    this.proccessCommand(command, s);

    s.close();
    return 0;

  }

  private int proccessCommand(String command, Scanner s) {
    switch (command) {
    case "login":
      login(s);
    }
    return 0;

  }

  private int login(Scanner s) {
    String user = s.next().split("=")[1];
    String pass = s.next().split("=")[1];
    System.out.println("User: " + user + "  Pass: " + pass);
    handler.gitLogin(user, pass);
    return 0;
  }

  private String parseCommand(Scanner s) {
    String cmd = s.next();
    String[] nv = cmd.split("=");

    cmd = "ERROR";

    if (nv.length == 2 && nv[0].equals("cmd")) {
      cmd = nv[1];
    }

    return cmd;
  }

}
