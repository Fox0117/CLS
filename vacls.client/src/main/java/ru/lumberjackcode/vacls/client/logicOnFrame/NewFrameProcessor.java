package ru.lumberjackcode.vacls.client.logicOnFrame;

import com.sun.jndi.toolkit.url.Uri;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.omg.PortableInterceptor.ClientRequestInfo;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import ru.lumberjackcode.vacls.client.CV.FaceFinder;
import ru.lumberjackcode.vacls.client.applicationparams.VaclsClientParams;
import ru.lumberjackcode.vacls.client.gui.Annauncer;
import ru.lumberjackcode.vacls.client.reactiveFramesPublisher.INewFrameHandler;
import ru.lumberjackcode.vacls.client.utils.Imshow;
import ru.lumberjackcode.vacls.transfere.ClientRequest;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

public class NewFrameProcessor implements INewFrameHandler{
    private final static Logger log = Logger.getLogger(NewFrameProcessor.class);

    private URI serverURI;

    private int serverTimeout;

    private static FaceFinder faceFinder;

    private String id;

    private Imshow visualiser = new Imshow("Faces", 640, 480);

    private Annauncer annauncer;

    public NewFrameProcessor(VaclsClientParams vaclsClientParams) throws Exception{

        this.serverURI = new URIBuilder()
                .setScheme("http")
                .setHost(vaclsClientParams.getServerParams().getServerURL())
                .setPort(vaclsClientParams.getServerParams().getPort())
                .build();

        serverTimeout = vaclsClientParams.getServerParams().getServerTimeout();

        annauncer = new Annauncer();

        this.id = vaclsClientParams.getID();

    }

    @Override
    public void OnNewFrame(Mat frame) {
        List<Rect> foundFaces = FaceFinder.findFaces(frame);




        Mat[] facesArray = new Mat[foundFaces.size()];

        for (int i = 0; i < foundFaces.size(); i++) {
            facesArray[i] = new Mat(frame, foundFaces.get(i));
        }

        if(log.isDebugEnabled()){
            visualise(frame.clone(), foundFaces);
        }


        if(foundFaces.size() > 0){
            ClientRequest request = new ClientRequest(facesArray, id, "2286914881337" );

            annauncer.showMessage(new String(request.getUtf8Json(), Charset.forName("UTF-8")));
            synchronized (annauncer){
                try {
                    annauncer.wait();
                } catch (InterruptedException ex){
                    log.error(ex);
                }
            }
        }




    }

    private void visualise(Mat frame, List<Rect> faces){
        for (Rect face : faces){
            Imgproc.rectangle(frame, face.tl(), face.br(), new Scalar(0, 255,0));
        }
        visualiser.showImage(frame);
    }
}
