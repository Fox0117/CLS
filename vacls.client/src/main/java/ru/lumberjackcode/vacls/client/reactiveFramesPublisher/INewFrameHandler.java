package ru.lumberjackcode.vacls.client.reactiveFramesPublisher;

import org.opencv.core.Mat;

public interface INewFrameHandler {
    void OnNewFrame(Mat frame);
}
