package ru.lumberjackcode.vacls.transfere;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.opencv.core.Mat;


import java.nio.charset.Charset;
import java.time.Clock;
import java.util.Base64;

public class ClientRequest {

    private Frame64[] frames64;

    private String id;

    private String token;

    private long time;

    public ClientRequest(Mat[] frames, String id, String token){
        frames64 = new Frame64[frames.length];

        for(int i = 0; i < frames.length; ++i) {

            int frameWidth = frames[i].cols();
            int frameHeight = frames[i].rows();
            int frameType = frames[i].type();

            byte[] frameBuffer = new byte[(int) (frames[i].total() * frames[i].elemSize())];
            frames[i].get(0, 0, frameBuffer);
            String frame64 = Base64.getEncoder().encodeToString(frameBuffer);
            frames64[i] = new Frame64(frame64, frameWidth, frameHeight, frameType);

        }

        this.id = id;
        this.token = token;
        this.time = Clock.systemUTC().millis();

    }

    public byte[] getUtf8Json(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this).getBytes(Charset.forName("UTF-8"));
    }

    public static ClientRequest fromUtf8Json(byte[] json){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(new String(json, Charset.forName("UTF-8")), ClientRequest.class);
    }

    public Mat[] getFrame(){
        Mat[] frames = new Mat[frames64.length];
        for(int i = 0; i < frames64.length; ++i){
            Mat frame = new Mat(frames64[i].getFrameHeight(), frames64[i].getFrameWidth(), frames64[i].getFrameType());
            byte[] frameBuffer = Base64.getDecoder().decode(frames64[i].getFrame64());
            frame.put(0,0, frameBuffer);
            frames[i] = frame;
        }
        return frames;
    }

    public String getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public long getTime() {
        return time;
    }


}
