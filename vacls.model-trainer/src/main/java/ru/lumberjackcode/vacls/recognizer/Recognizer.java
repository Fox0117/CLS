package ru.lumberjackcode.vacls.recognizer;

import org.apache.log4j.Logger;
import org.opencv.core.*;
import org.opencv.face.FisherFaceRecognizer;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;

public class Recognizer{

    private final static Logger log = Logger.getLogger(Recognizer.class);

    private FisherFaceRecognizer fisherFaceRecognizer = FisherFaceRecognizer.create();

    private Size trainedSize;

    public Recognizer(String modelPath, Size preTrainedImSize){
        File preTrainedModelFile = new File(modelPath);
        if(preTrainedModelFile.exists() && preTrainedModelFile.isFile() && preTrainedModelFile.canRead()){
            try{
                fisherFaceRecognizer.read(preTrainedModelFile.getAbsolutePath());
            }catch (CvException ex){
                log.error("Internal OpenCV error", ex);
            }
        }else {
            IOException ioException = new IOException("Can't find pre trained model in " + modelPath);
            log.error(ioException.getMessage(), ioException);
        }

        this.trainedSize = preTrainedImSize;
    }

    public Recognizer(String modelPath){
        this(modelPath, new Size(92, 112));
    }

    double[] getVector(Mat face){

        Mat resizedFace = new Mat(), readyToRecognitionMat = new Mat(), result = new Mat();

        Imgproc.resize(face, resizedFace, trainedSize);

        if(face.type() != CvType.CV_8UC1){
            Imgproc.cvtColor(resizedFace, readyToRecognitionMat, Imgproc.COLOR_BGR2GRAY);
        }
        else {
            readyToRecognitionMat = resizedFace;
        }

        Core.PCAProject(readyToRecognitionMat, fisherFaceRecognizer.getMean(), fisherFaceRecognizer.getEigenVectors(), result);

        double[] toReturn = new double[(int) result.total() * result.channels()];

        result.get(0,0, toReturn);

        return toReturn;
    }
}
