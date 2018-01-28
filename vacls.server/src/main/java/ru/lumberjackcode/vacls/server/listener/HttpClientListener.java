package ru.lumberjackcode.vacls.server.listener;

import org.apache.log4j.Logger;
import ru.lumberjackcode.vacls.server.HttpServer;
import java.util.*;
import com.sun.net.httpserver.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpClientListener {
    private final static Logger logger = Logger.getLogger(HttpServer.class);
    private int portClient;
    private int maxThreadPoolNumber;
    private String openCVPath;
    private Queue<Thread> queuedThreads;
    private ArrayList<Thread> activeThreads;

    public HttpClientListener(int portClient, int maxThreadPoolNumber, String openCVPath){
        this.portClient = portClient;
        this.maxThreadPoolNumber = maxThreadPoolNumber;
        this.openCVPath = openCVPath;
        logger.info("HttpClientListener starting on port: " + Integer.toString(portClient));
        com.sun.net.httpserver.HttpServer server;
        try {
            server = com.sun.net.httpserver.HttpServer.create();
            server.bind(new InetSocketAddress(portClient), 0);
        }
        catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return;
        }

        HttpContext context = server.createContext("/", new EchoHandler());
        context.setAuthenticator(new Auth());
        server.setExecutor(Executors.newFixedThreadPool(maxThreadPoolNumber));
        logger.info("HttpClientListener started");
        server.start();
    }

    static class EchoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder builder = new StringBuilder();

            builder.append("<h1>URI: ").append(exchange.getRequestURI()).append("</h1>");

            Headers headers = exchange.getRequestHeaders();
            for (String header : headers.keySet()) {
                builder.append("<p>").append(header).append("=")
                        .append(headers.getFirst(header)).append("</p>");
            }

            byte[] bytes = builder.toString().getBytes();
            exchange.sendResponseHeaders(200, bytes.length);

            OutputStream output = exchange.getResponseBody();
            output.write(bytes);
            output.close();
        }
    }

    static class Auth extends Authenticator {
        @Override
        public Result authenticate(HttpExchange httpExchange) {
            if ("/forbidden".equals(httpExchange.getRequestURI().toString()))
                return new Failure(403);
            else
                return new Success(new HttpPrincipal("login", "password"));
        }
    }
}
