package ru.lumberjackcode.vacls.client.CV;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class FaceFinder {

    private static CascadeClassifier faceCascade;

    static{
        File cascadeFile = new File("haarcascade_frontalface_default.xml");
        if(cascadeFile.exists() && cascadeFile.isFile() && cascadeFile.canRead()){
            faceCascade = new CascadeClassifier(cascadeFile.getAbsolutePath());
        } else {
            throw new IllegalArgumentException("File not found");
        }
    }

    public static List<Rect> findFaces(Mat frame){
        MatOfRect foundFaces = new MatOfRect();
        faceCascade.detectMultiScale(frame, foundFaces, 1.1, 10, 0,
                new Size(50, 50), new Size(400, 400));
        return foundFaces.toList();
    }

}
