package ru.lumberjackcode.vacls.transfere;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.Charset;

public class ClientResponse implements IResponse{
    private String message;

    private int error;

    private boolean needToBeShown;

    public ClientResponse(boolean needToBeShown, String message, int error){
        this.needToBeShown = needToBeShown;
        this.message = message;
        this.error = error;
    }

    public static ClientResponse fromUtf8Json(byte[] json){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(new String(json, Charset.forName("UTF-8")), ClientResponse.class);
    }

    public String getMessage() {
        return message;
    }

    public int getError() {
        return error;
    }

    public boolean isNeedToBeShown() {
        return needToBeShown;
    }
}
