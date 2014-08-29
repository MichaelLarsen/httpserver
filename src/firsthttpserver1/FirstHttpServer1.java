/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package firsthttpserver1;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

/**
 *
 * @author Michael
 */
public class FirstHttpServer1 {

    /**
     * @param args the command line arguments
     */
    static int port = 8080;
    static String ip = "127.0.0.1";

    public static void main(String[] args) throws IOException {
        if (args.length >= 2) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
        }
        InetSocketAddress i = new InetSocketAddress(ip, port); //localhost - 127.0.0.1
        HttpServer server = HttpServer.create(i, 0);
        server.createContext("/welcome", new WelcomeHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Started the server, listening on:");
        System.out.println("port: " + port);
        System.out.println("ip: " + ip);
    }

    static class WelcomeHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
//            String response = "Welcome to my first http-server";
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>\n");
            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append("<title>My fancy Web Site</title>\n");
            sb.append("<meta charset='UTF-8'>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("<h2>Welcome to my very first home made Web Server :-)</h2>\n");
            sb.append("</body>\n");
            sb.append("</html>\n");
            String response = sb.toString();
            Headers h = he.getResponseHeaders();
            h.add("Content-Type", "text/html");
            he.sendResponseHeaders(200, response.length());
            try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
                pw.print(response);
            };
        }
    }
}
