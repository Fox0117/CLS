package ru.lumberjackcode.vacls.server.listener;

import org.apache.log4j.Logger;
import ru.lumberjackcode.vacls.server.HttpServer;
import ru.lumberjackcode.vacls.server.authentication.FaceAuthenticatior;
//import ru.lumberjackcode.vacls.transfere.*;

import java.io.InputStream;
import java.util.*;
import com.sun.net.httpserver.*;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpClientListener {
    private final static Logger logger = Logger.getLogger(HttpServer.class);
    private int portClient;
    private String openCVPath;
    private com.sun.net.httpserver.HttpServer server;
    private ExecutorService httpThreadPool;

    public HttpClientListener(int portClient, int maxThreadPoolNumber, String openCVPath){
        this.portClient = portClient;
        this.openCVPath = openCVPath;
        logger.info("HttpClientListener binding on port: " + Integer.toString(this.portClient));
        try {
            server = com.sun.net.httpserver.HttpServer.create();
            server.bind(new InetSocketAddress(portClient), 0);
        }
        catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return;
        }

        HttpContext context = server.createContext("/", new EchoHandler());
        //context.setAuthenticator(new Auth());
        httpThreadPool = Executors.newFixedThreadPool(maxThreadPoolNumber);
        server.setExecutor(httpThreadPool);
    }

    public void start() {
        logger.info("HttpClientListener starting on port: " + Integer.toString(this.portClient));
        try {
            server.start();
        }
        catch (Exception ex) {
            logger.error("Server was interrupted");
            logger.error(ex.getMessage(), ex);
        }
    }

    public void stop() {
        logger.info("HttpClientListener stops\n\n");
        try {
            server.stop(1);
            httpThreadPool.shutdownNow();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public static class EchoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange){
            logger.info("Connection established");
            try {
                if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                    logger.info("Processing POST request from client");

                    //Get ClientRequest from JSON
                    InputStream input = exchange.getRequestBody();
                    byte[] jSonInputData = new byte[input.available()];
                    input.read(jSonInputData);
                    //ClientRequest clientReq = ClientRequest.fromUtf8Json(jSonInputData);

                    //Process ClientRequest
                    FaceAuthenticatior faceAuth = new FaceAuthenticatior();
                    //faceAuth.Authentificate(clientReq);

                    //Send response to client
                    //ClientResponse clientResp = new ClientResponse(true, faceAuth.getMessage(), 0);
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream output = exchange.getResponseBody();
                    //output.write(clientResp.getUtf8Json());
                    output.write(faceAuth.getMessage().getBytes());
                    output.flush();
                    output.close();
                    logger.info("Response on POST request was sent to client");
                }
                else if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream output = exchange.getResponseBody();
                    output.write("<h1>Server is online</h1>".getBytes());
                    output.flush();
                    output.close();
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
                }

            }
            catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
                return;
            }
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
