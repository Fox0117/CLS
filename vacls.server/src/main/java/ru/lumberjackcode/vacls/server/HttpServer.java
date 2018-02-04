package ru.lumberjackcode.vacls.server;

import org.apache.log4j.Logger;
import ru.lumberjackcode.vacls.server.applicationparams.VaclsServerParams;
import ru.lumberjackcode.vacls.server.listener.HttpAdminListener;
import ru.lumberjackcode.vacls.server.listener.HttpClientListener;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.Scanner;

@SuppressWarnings("all")

public class HttpServer {
    private final static Logger logger = Logger.getLogger(HttpServer.class);

    private static Unmarshaller configUnmarshaller;

    private static VaclsServerParams configParams;

    static {
        try {
            JAXBContext context = JAXBContext.newInstance(VaclsServerParams.class);
            configUnmarshaller = context.createUnmarshaller();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args){
        logger.info("vacls.server.HttpServer starts");
        if (args.length != 1) {
            logger.error("module takes only config.xml path for setup");
            System.exit(1);
        }

        //Loading server configuration
        try {
            JAXBElement<VaclsServerParams> jaxbElement = configUnmarshaller.unmarshal(new StreamSource(new File(args[0])), VaclsServerParams.class);
            configParams = jaxbElement.getValue();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            System.exit(1);
        }

        // Loading OpenCV lib
        try{
            System.load(new File( configParams.getSystemParams().getOpenCvPath() ).getAbsolutePath() );
        }
        catch (Exception ex) {
            logger.error("Bad OpenCV lib path", ex);
        }

        HttpClientListener clientListener=null;
        HttpAdminListener adminListener=null;
        try {
            clientListener = new HttpClientListener(configParams.getConnectionParams().getPortClient(), configParams.getSystemParams().getMaxThreadPoolNumber(), configParams.getSystemParams().getOpenCvPath());
            adminListener = new HttpAdminListener(configParams.getConnectionParams().getPortAdmin());
            clientListener.start();
            adminListener.start();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            logger.error("Unable create Http listeners");
            if (clientListener != null)
                clientListener.stop();

            if (adminListener != null)
                adminListener.stop();

            logger.info("vacls.server.HttpServer stops\n\n");
            System.exit(1);
        }

        Scanner input = new Scanner(System.in);
        for(; input.hasNext() && !input.next().equals("stop"););

        clientListener.stop();
        adminListener.stop();
        logger.info("vacls.server.HttpServer stops\n\n");
    }
}
