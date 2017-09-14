package server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class Server {

  HttpServer server;
  
  public Server(int port) throws IOException{
    server = HttpServer.create(new InetSocketAddress(port), 0);
    server.createContext("/server", new RequestHandler());
    server.start();
  }
  
}
