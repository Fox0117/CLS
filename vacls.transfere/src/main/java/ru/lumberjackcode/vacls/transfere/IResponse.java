package ru.lumberjackcode.vacls.transfere;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.Charset;

public interface IResponse {
    default byte[] getUtf8Json(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this).getBytes(Charset.forName("UTF-8"));
    }
}
