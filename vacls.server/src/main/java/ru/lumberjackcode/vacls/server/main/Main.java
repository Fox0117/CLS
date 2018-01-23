package ru.lumberjackcode.vacls.server.main;

import org.apache.log4j.Logger;
import ru.lumberjackcode.vacls.server.applicationparams.VaclsServerParams;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

public class Main {

    private final static Logger logger = Logger.getLogger(Main.class);

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
        logger.info("vacls.server module Start");
        if (args.length != 1) {
            logger.error("module takes only config.xml path for setup");
            System.exit(1);
        }

        try {
            JAXBElement<VaclsServerParams> jaxbElement = configUnmarshaller.unmarshal(new StreamSource(new File(args[0])), VaclsServerParams.class);
            configParams = jaxbElement.getValue();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(1);
        }

        // Loading OpenCV lib
        try{
            System.load(new File( configParams.getSystemParams().getOpenCvPath() ).getAbsolutePath() );
        }catch (Exception ex){
            logger.error("Bad OpenCV lib path" ,ex);
        }

        logger.info("vacls.server module OK");
    }
}
