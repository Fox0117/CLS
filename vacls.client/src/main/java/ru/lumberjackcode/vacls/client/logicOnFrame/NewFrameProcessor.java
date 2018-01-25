package ru.lumberjackcode.vacls.client.logicOnFrame;

import com.sun.jndi.toolkit.url.Uri;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import ru.lumberjackcode.vacls.client.CV.FaceFinder;
import ru.lumberjackcode.vacls.client.applicationparams.VaclsClientParams;
import ru.lumberjackcode.vacls.client.reactiveFramesPublisher.INewFrameHandler;
import ru.lumberjackcode.vacls.client.utils.Imshow;

import java.net.URI;
import java.util.List;

public class NewFrameProcessor implements INewFrameHandler{
    private final static Logger log = Logger.getLogger(NewFrameProcessor.class);

    private URI serverURI;

    private int serverTimeout;

    private static FaceFinder faceFinder;

    private Imshow visualiser = new Imshow("Faces", 640, 480);

    public NewFrameProcessor(VaclsClientParams vaclsClientParams) throws Exception{

        this.serverURI = new URIBuilder()
                .setScheme("http")
                .setHost(vaclsClientParams.getServerParams().getServerURL())
                .setPort(vaclsClientParams.getServerParams().getPort())
                .build();

        serverTimeout = vaclsClientParams.getServerParams().getServerTimeout();

    }

    @Override
    public void OnNewFrame(Mat frame) {

        List<Rect> foundFaces = FaceFinder.findFaces(frame);

        if(log.isDebugEnabled()){
            visualise(frame, foundFaces);
        }
    }

    private void visualise(Mat frame, List<Rect> faces){
        for (Rect face : faces){
            Imgproc.rectangle(frame, face.tl(), face.br(), new Scalar(0, 255,0));
        }
        visualiser.showImage(frame);
    }
}
