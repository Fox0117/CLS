package ru.lumberjackcode.vacls.server.listener;

import org.apache.log4j.Logger;
import ru.lumberjackcode.vacls.server.HttpServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class HttpClientListener {
    private final static Logger logger = Logger.getLogger(HttpServer.class);
    private int portClient;
    private int maxThreadPoolNumber;
    private String openCVPath;
    private Queue<Thread> queuedThreads;
    private ArrayList<Thread> activeThreads;

    public HttpClientListener(int portClient, int maxThreadPoolNumber, String openCVPath) {
        this.portClient = portClient;
        this.maxThreadPoolNumber = maxThreadPoolNumber;
        this.openCVPath = openCVPath;
        queuedThreads = new LinkedList<>();
        activeThreads = new ArrayList<>(maxThreadPoolNumber);

        //Creating server socket on clientPort
        logger.info("HttpClientListener starting on port: " + Integer.toString(portClient));
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portClient);
            logger.info("HttpClientListener started");
        }
        catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

        //Awaiting for requests
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientSession session = new ClientSession(clientSocket);
                queuedThreads.add(new Thread(session));
                for (int i = 0; i < maxThreadPoolNumber && queuedThreads.peek() != null; ++i) {
                    if (activeThreads.get(i) == null || !(activeThreads.get(i).isAlive())) {
                        activeThreads.set(i, queuedThreads.poll());
                        activeThreads.get(i).start();
                    }
                }
            }
            catch (IOException ex) {
                logger.error("Interrupted connection");
                logger.error(ex.getMessage(), ex);
                break;
            }
        }

        //Stopping listener and interrupt running sessions
        logger.info("HttpClientListener stopping");
        for (int i = 0; i < maxThreadPoolNumber; ++i) {
            if (activeThreads.get(i) != null && activeThreads.get(i).isAlive()) {
                activeThreads.get(i).interrupt();
            }
        }
        logger.info("HttpClientListener stopped");
    }

    private static class ClientSession implements Runnable {
        private Socket socket;
        private InputStream input;
        private OutputStream output;

        public ClientSession(Socket socket) throws IOException {
            this.socket = socket;
            this.input = socket.getInputStream();
            this.output = socket.getOutputStream();
        }

        @Override
        public void run() {
            return;
        }


    }
}
