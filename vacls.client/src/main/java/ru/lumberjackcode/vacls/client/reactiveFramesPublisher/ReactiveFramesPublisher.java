package ru.lumberjackcode.vacls.client.reactiveFramesPublisher;

import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("all")

public class ReactiveFramesPublisher implements Runnable{

    private final static Logger log = Logger.getLogger(ReactiveFramesPublisher.class);

    private VideoCapture videoSource;

    private List<INewFrameHandler> newFrameHandlers = new LinkedList<>();

    private Size frameSize;

    /**
     * Creates new reactive frames publisher
     * @param videoSourceId camera id (ex. /dev/video0 -> 0)
     * @param frameWidth width of the frame
     * @param frameHeight height of the frame
     */
    public ReactiveFramesPublisher(int videoSourceId, int frameWidth, int frameHeight) throws Exception{

        videoSource = new VideoCapture(videoSourceId);

        if ( !videoSource.isOpened() ){
            IOException toThrow = new IOException("Can't open video source with id: " + videoSourceId);
            log.error("Can't open video source with id: " + videoSourceId, toThrow);
            throw toThrow;
        }

        if(frameHeight > 0 && frameWidth >0){
            this.frameSize = new Size(frameWidth, frameHeight);
        } else {
            IllegalArgumentException toThrow =
                    new IllegalArgumentException("Frame heigth and frame size must be greater than 0");
            log.error("Frame heigth and frame size must be greater than 0", toThrow);
            throw toThrow;
        }

        log.info("Video source " + videoSourceId + " opened with resolution " + frameWidth + "x" + frameHeight);

    }

    /**
     * Creates new reactive frames publisher with event handlers list
     * @param videoSourceId camera id (ex. /dev/video0 -> 0)
     * @param frameWidth width of the frame
     * @param frameHeight height of the frame
     * @param newFrameHandlers event handlers list
     */
    public ReactiveFramesPublisher(int videoSourceId, int frameWidth, int frameHeight, List<INewFrameHandler> newFrameHandlers) throws Exception{
        this(videoSourceId, frameWidth, frameHeight);
        this.newFrameHandlers = newFrameHandlers;
    }

    @Override
    public void run() {

        while (!Thread.interrupted()){
            Mat newFrame = new Mat(), newFrameResized = new Mat();



            if( !videoSource.read(newFrame) ){
                RuntimeException toThrow = new RuntimeException("Failed to read next frame from camera");
                log.error("Failed to read next frame from camera",
                        toThrow);
                throw toThrow;
            }


            Imgproc.resize(newFrame, newFrameResized, frameSize);

            log.debug("Got new frame, start processing handlers");

            for(INewFrameHandler frameHandler : newFrameHandlers){

                log.debug("Start processing event handler " + frameHandler.getClass().getCanonicalName() );

                try{
                    frameHandler.OnNewFrame(newFrameResized);
                } catch (Exception exception){
                    log.error("Exception in event handler "
                            + frameHandler.getClass().getCanonicalName()
                            + " " +  exception.getMessage());
                }

                log.debug("Event hndler processed: " + frameHandler.getClass().getCanonicalName() );

            }

            log.debug("Handlers processing finished");

            Thread.yield();
        }
        log.error("Thread with camera listener was interrupted", new InterruptedException());
    }





    /**
     * Returns new frame event handlers list
     * @return new frame event handlers list
     */
    public List<INewFrameHandler> getNewFrameHandlers() {
        return newFrameHandlers;
    }

    /**
     * Sets new frame event handlers list
     * @param newFrameHandlers new frame event handlers list
     */
    public void setNewFrameHandlers(List<INewFrameHandler> newFrameHandlers) {
        this.newFrameHandlers = newFrameHandlers;
    }

    /**
     * Returns frame width in pixels
     * @return frame width in pixels
     */
    public int getFrameWidth(){
        return (int) frameSize.width;
    }

    /**
     * Returns frame height in pixels
     * @return frame height in pixels
     */
    public int getFrameHeight(){
        return (int) frameSize.height;
    }

    /**
     * Sets frame width in pixels
     * @param frameWidth frame width in pixels
     */
    public void setFrameWidth(int frameWidth){
        frameSize.width = frameWidth;
    }

    /**
     * Sets frame height in pixels
     * @param frameHeight frame height in pixels
     */
    public void setFrameHeight(int frameHeight){
        frameSize.height = frameHeight;
    }

    /**
     * Sets frame size in pixels
     * @param frameSize frame size in pixels
     */
    public void setFrameSize(Size frameSize) {
        this.frameSize = frameSize;
    }

    /**
     *
     * @return
     */
    public Size getFrameSize() {
        return frameSize;
    }
}
