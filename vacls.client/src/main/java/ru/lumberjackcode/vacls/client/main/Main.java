package ru.lumberjackcode.vacls.client.main;


import org.apache.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import ru.lumberjackcode.vacls.client.applicationparams.VaclsClientParams;
import ru.lumberjackcode.vacls.client.applicationparams.VideoParams;
import ru.lumberjackcode.vacls.client.reactiveFramesPublisher.ReactiveFramesPublisher;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

public class Main {

    private final static Logger log = Logger.getLogger(Main.class);

    private static Unmarshaller paramsUnmarshaller;

    private static VaclsClientParams applicationParams;

    static {
        try {
            JAXBContext context = JAXBContext.newInstance(VaclsClientParams.class);
            paramsUnmarshaller = context.createUnmarshaller();
        } catch (JAXBException e){
            log.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws Exception{
        log.info("Starting Vacls client app");


        if(args.length != 1){
            log.error("App takes 1 param: params.xml path");
            System.exit(1);
        }

        JAXBElement<VaclsClientParams> root = paramsUnmarshaller.unmarshal(new StreamSource(new File(args[0])), VaclsClientParams.class);

        VaclsClientParams params = root.getValue();

        try{
            System.load( new File( params.getOpenCVPath() ).getAbsolutePath() );
        }catch (Exception ex){
            log.error("Bad OpenCV lib path" ,ex);
        }

        VideoParams videoParams = params.getVideoParams();

        ReactiveFramesPublisher reactiveFramesPublisher = new ReactiveFramesPublisher(
                Integer.parseInt( videoParams.getCapturingDevice() ),
                videoParams.getFrameWidth(),
                videoParams.getFrameHeight()
        );

        Thread mainLogicThread = new Thread(reactiveFramesPublisher);
        mainLogicThread.start();

        log.info("Main OK");
    }
}
