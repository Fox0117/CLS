package ru.lumberjackcode.vacls.client.logicOnFrame;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
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
import ru.lumberjackcode.vacls.transfere.ClientResponse;




import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.*;

public class NewFrameProcessor implements INewFrameHandler, AutoCloseable{
    private final static Logger log = Logger.getLogger(NewFrameProcessor.class);

    private URI serverURI;

    private int serverTimeout;

    private static FaceFinder faceFinder;

    private String id;

    private Imshow visualiser = new Imshow("Faces", 640, 480);

    private final Annauncer annauncer;

    private CloseableHttpAsyncClient asyncClient = HttpAsyncClients.createDefault();

    public NewFrameProcessor(VaclsClientParams vaclsClientParams) throws Exception{

        this.serverURI = new URIBuilder()
                .setScheme("http")
                .setHost(vaclsClientParams.getServerParams().getServerURL())
                .setPort(vaclsClientParams.getServerParams().getPort())
                .build();

        serverTimeout = vaclsClientParams.getServerParams().getServerTimeout();

        annauncer = new Annauncer();

        this.id = vaclsClientParams.getID();

        asyncClient.start();
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

            log.debug("Got faces, starting request");

            ClientRequest request = new ClientRequest(facesArray, id, "2286914881337" );

            HttpPost httpPost = new HttpPost(serverURI);
            httpPost.setEntity(new ByteArrayEntity(request.getUtf8Json()));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            Future<HttpResponse> httpResponseFuture = asyncClient.execute(httpPost, null);

            HttpResponse httpResponse;

            try {

                httpResponse = httpResponseFuture.get(serverTimeout, TimeUnit.MILLISECONDS);

            }catch (TimeoutException | ExecutionException | InterruptedException exception ){
                log.warn("Getting response exception from " + serverURI.toASCIIString(), exception);
                httpResponseFuture.cancel(false);
                return;
            }

            if(httpResponse.getStatusLine().getStatusCode() != 200){
                log.warn("HTTP ERROR : "
                        + httpResponse.getStatusLine().getStatusCode()
                        + httpResponse.getStatusLine().getReasonPhrase());
                return;
            }

            byte[] serverResponseBuffer;

            try{
                 serverResponseBuffer = EntityUtils.toByteArray(httpResponse.getEntity());
            } catch (IOException exception){
                log.warn("Entity reading failed" ,exception);
                return;
            }

            ClientResponse clientResponse;

            try {
                clientResponse = ClientResponse.fromUtf8Json(serverResponseBuffer);
            } catch (Exception exception){
                log.warn("Entity parsing failed" , exception);
                return;
            }

            if(clientResponse.getError() != 0){
                log.warn("Got not 0 Error from server: " + clientResponse.getError());
                return;
            }

            if(! clientResponse.isNeedToBeShown()){
                log.debug("No need in showing");
                return;
            }

            annauncer.showMessage(clientResponse.getMessage());

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

    @Override
    public void close() throws IOException {
        asyncClient.close();
    }
}
