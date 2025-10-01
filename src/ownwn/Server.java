package ownwn;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

public class Server {
    private static final String PACKAGE_NAME = "ownwn";


    private final Map<String, HttpMethod> methods;

    public Server() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);

        new FirstHandler();
        methods = AnnotationFinder.getAllAnnotatedMethods(PACKAGE_NAME);

        server.createContext("/").setHandler(exchange -> {
            try {
                handle(exchange);
            } catch (IOException _) {}
        });

        server.start();
    }

    @Anno("/")
    private void handle(HttpExchange exchange) throws IOException {

        HttpMethod method = methods.get(exchange.getRequestURI().getPath());
        System.out.println(exchange.getRequestURI().getPath());
        System.out.println(method);
        System.out.println("\n\n\n");

        exchange.sendResponseHeaders(200, "hello world".length());
        exchange.getResponseBody().write("hello world".getBytes());
        exchange.getResponseBody().close();
    }
}