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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.Scanner;

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
    static String contentFolder = "public/";

    public static void main(String[] args) throws IOException {
        if (args.length >= 3) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
            contentFolder = args[2];
        }
        InetSocketAddress i = new InetSocketAddress(ip, port); //localhost - 127.0.0.1
        HttpServer server = HttpServer.create(i, 0);
        server.createContext("/welcome", new WelcomeHandler());
        server.createContext("/headers", new HeadersHandler());
        server.createContext("/pages/", new PagesHandler(contentFolder));
        server.createContext("/parameters", new ParametersHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Started the server, listening on:");
        System.out.println("port: " + port);
        System.out.println("ip: " + ip);
        System.out.println("pages: " + contentFolder);
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

    static class HeadersHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
//            String response = "Welcome to my first http-server";
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>\n");
            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append("<title>My Headers</title>\n");
            sb.append("<meta charset='UTF-8'>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("<table border = \"1\">");
            sb.append("<tr>");
            sb.append("<th>Header</th>");
            sb.append("<th>Value</th>");
            sb.append("</tr>");
            for (String key : he.getRequestHeaders().keySet()) {
                sb.append("<tr>");
                sb.append("<td>" + key + "</td>");
                sb.append("<td>" + he.getRequestHeaders().get(key) + "</td>");
                sb.append("</tr>");
            }
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

    static class PagesHandler implements HttpHandler {

        String contentFolder;

        private PagesHandler(String contentFolder) {
            this.contentFolder = contentFolder;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            System.out.println(he.getRequestURI().toString());
            String str = he.getRequestURI().toString();
            String substring = str.substring(7);
            File file = new File(contentFolder + substring);
            byte[] bytesToSend = new byte[(int) file.length()];
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytesToSend, 0, bytesToSend.length);
            } catch (IOException ie) {
                ie.printStackTrace();
            }
            Headers h = he.getResponseHeaders();
            int index = substring.indexOf(".");
            String extension = substring.substring(index + 1);
            System.out.println("Extension string: " + extension);
            h.add("Content-Type", "" + extension);
            he.sendResponseHeaders(200, bytesToSend.length);
            try (OutputStream os = he.getResponseBody()) {
                os.write(bytesToSend, 0, bytesToSend.length);
            }
        }
    }

    static class ParametersHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>\n");
            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append("<title>Parameters</title>\n");
            sb.append("<meta charset='UTF-8'>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("<h2>Parameters:</h2>\n");
            sb.append("<p>Method is: " + he.getRequestMethod());
            sb.append("<p>Get-Parameters: " + he.getRequestURI().getQuery());
            Scanner scan = new Scanner(he.getRequestBody());
            while (scan.hasNext()) {
                sb.append("Request body, with Post-parameters: " + scan.nextLine());
                sb.append("</br>");
            }
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
