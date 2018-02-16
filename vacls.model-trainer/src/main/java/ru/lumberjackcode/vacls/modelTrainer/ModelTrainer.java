package ru.lumberjackcode.vacls.modelTrainer;


import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.face.Face;
import org.opencv.face.FisherFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import ru.lumberjackcode.vacls.client.utils.Imshow;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class ModelTrainer {
    public static void main(String[] params){
        System.load(new File("libs/OpenCV/libopencv_java340.so").getAbsolutePath());
        FisherFaceRecognizer fisherFaceRecognizer = FisherFaceRecognizer.create();

        List<Integer> labels = new LinkedList<>();
        List<Mat> frames = new LinkedList<>();

        int nextLabel = 0;

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("vacls.model-trainer/trainingData"))) {
            for(Path path : directoryStream){
                if(Files.isDirectory(path)){
                    for(Path file : Files.newDirectoryStream(path)){
                        labels.add(nextLabel);

                        Mat next = Imgcodecs.imread(file.toAbsolutePath().toString());
                        Mat nextGray = new Mat();
                        Imgproc.cvtColor(next, nextGray, 6);
                        frames.add(nextGray);
                    }
                    nextLabel++;
                }
            }
        }catch (IOException ex){
            System.err.println("Bad struct" + ex.getMessage());
        }

        Mat labelsMat = new Mat(1, labels.size(), CvType.CV_32S);
        for(int i = 0; i < labels.size(); ++i){
            labelsMat.put(0, i, new int[]{labels.get(i)});
        }

        fisherFaceRecognizer.train(frames, labelsMat);
        fisherFaceRecognizer.write("test_write.dat");
        System.out.print("OK");
    }
}
