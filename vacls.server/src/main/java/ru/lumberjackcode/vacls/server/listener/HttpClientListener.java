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
            logger.error("HttpClientListener was interrupted");
            logger.error(ex.getMessage(), ex);
        }
    }

    public void stop() {
        logger.info("HttpClientListener stops");
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
            try {
                //Online status check
                if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                    try {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream output = exchange.getResponseBody();
                        output.write("<h1>Client server is online</h1>".getBytes());
                        output.flush();
                        output.close();
                    }
                    catch (IOException ex) {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
                        logger.error(ex.getMessage(), ex);
                    }
                }
                //Main request
                else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                    logger.info("Processing POST request from client");
                    InputStream input;
                    byte[] jSonInputData;
                    ClientRequest clientReq;
                    FaceAuthenticatior faceAuth;

                    //Get data from request;
                    try {
                        input = exchange.getRequestBody();
                        jSonInputData = IOUtils.toByteArray(input);
                        clientReq = ClientRequest.fromUtf8Json(jSonInputData);
                    }
                    catch (Exception ex) {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                        exchange.close();
                        logger.error(ex.getMessage(), ex);
                        return;
                    }

                    try {
                        //Process ClientRequest
                        faceAuth = new FaceAuthenticatior();
                        ClientResponse clientResp = faceAuth.Authentificate(clientReq);
                        //Send response to client
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream output = exchange.getResponseBody();
                        output.write(clientResp.getUtf8Json());
                        output.flush();
                        output.close();
                    }
                    catch (Exception ex) {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
                        exchange.close();
                        logger.error(ex.getMessage(), ex);
                        return;
                    }
                    logger.info("POST request from client processed");
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
