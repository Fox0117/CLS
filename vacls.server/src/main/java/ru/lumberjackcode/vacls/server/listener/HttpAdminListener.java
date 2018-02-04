package ru.lumberjackcode.vacls.server.listener;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import ru.lumberjackcode.vacls.server.HttpServer;
import ru.lumberjackcode.vacls.server.authentication.FaceAuthenticatior;
import ru.lumberjackcode.vacls.transfere.*;

import com.sun.net.httpserver.*;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpAdminListener {
    private final static Logger logger = Logger.getLogger(HttpServer.class);
    private int portAdmin;
    private com.sun.net.httpserver.HttpServer server;

    public HttpAdminListener(int portAdmin){
        this.portAdmin = portAdmin;
        logger.info("HttpAdminListener binding on port: " + Integer.toString(this.portAdmin));
        try {
            server = com.sun.net.httpserver.HttpServer.create();
            server.bind(new InetSocketAddress(portAdmin), 0);
        }
        catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return;
        }

        server.createContext("/", new EchoHandler());
        server.createContext("/entries/", new EntiresHandler());
        server.createContext("/js/", new JsHandler());
        //context.setAuthenticator(new Auth());
        server.setExecutor(null);
    }

    public void start() {
        logger.info("HttpAdminListener starting on port: " + Integer.toString(this.portAdmin));
        try {
            server.start();
        }
        catch (Exception ex) {
            logger.error("Admin server was interrupted");
            logger.error(ex.getMessage(), ex);
        }
    }

    public void stop() {
        logger.info("HttpAdminListener stops\n\n");
        try {
            server.stop(1);
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public static class EchoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange){
            try {
                //Online status check
                if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                    try {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream output = exchange.getResponseBody();
                        output.write("<h1>Admin server is online</h1>".getBytes());
                        output.flush();
                        output.close();
                    }
                    catch (IOException ex) {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
                        logger.error(ex.getMessage(), ex);
                    }
                }
                //Different types of request
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
                    exchange.close();
                    return;
                }
            }
            catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                return;
            }
        }
    }

    public static class EntiresHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange){
            try {
                //Online status check
                if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                    try {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream output = exchange.getResponseBody();
                        output.write("<h1>Entries handler is online</h1>".getBytes());
                        output.flush();
                        output.close();
                    }
                    catch (IOException ex) {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
                        logger.error(ex.getMessage(), ex);
                    }
                }
                else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                    Headers headers = exchange.getRequestHeaders();
                    if (!headers.containsKey("startDate") || !headers.containsKey("endDate")) {
                        try {
                            //TODO Get entries range

                            //Now return default value
                            AdminResponse.EntriesRange entriesRange = new AdminResponse.EntriesRange();
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                            OutputStream output = exchange.getResponseBody();
                            output.write(entriesRange.getUtf8Json());
                            output.flush();
                            output.close();
                        }
                        catch (IOException ex) {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
                            logger.error(ex.getMessage(), ex);
                        }
                    }
                    else {
                        LocalDateTime startDate=null, endDate=null;
                        //Get header values
                        try {
                            startDate = LocalDateTime.parse(headers.get("startDate").get(0), AdminResponse.getDateTimeFormatter());
                            endDate = LocalDateTime.parse(headers.get("endDate").get(0), AdminResponse.getDateTimeFormatter());
                        }
                        catch (Exception ex) {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                            exchange.close();
                            logger.error(ex.getMessage(), ex);
                            return;
                        }

                        try {
                            //TODO Process entries

                            //Now return default value
                            AdminResponse.Entries entry = new AdminResponse.Entries();
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                            OutputStream output = exchange.getResponseBody();
                            output.write(entry.getUtf8Json());
                            output.flush();
                            output.close();
                        }
                        catch (Exception ex) {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
                            logger.error(ex.getMessage(), ex);
                        }
                    }
                }
                //Different types of request
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
                    exchange.close();
                    return;
                }
            }
            catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                return;
            }
        }
    }

    public static class JsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange){
            try {
                //Online status check
                if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                    try {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream output = exchange.getResponseBody();
                        output.write("<h1>JavaScript handler is online</h1>".getBytes());
                        output.flush();
                        output.close();
                    }
                    catch (IOException ex) {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
                        logger.error(ex.getMessage(), ex);
                    }
                }
                /*else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                    //TODO POST request processing
                }*/
                //Different types of request
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
                    exchange.close();
                    return;
                }
            }
            catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                return;
            }
        }
    }
    /*static class Auth extends Authenticator {
        @Override
        public Result authenticate(HttpExchange httpExchange) {
            if ("/forbidden".equals(httpExchange.getRequestURI().toString()))
                return new Failure(403);
            else
                return new Success(new HttpPrincipal("login", "password"));
        }
    }*/
}
