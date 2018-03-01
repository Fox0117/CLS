package ru.lumberjackcode.vacls.server.authentication;

import jdk.nashorn.api.scripting.ClassFilter;
import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import ru.lumberjackcode.vacls.transfere.*;
import ru.lumberjackcode.vacls.server.database.PostGresQL;
import ru.lumberjackcode.vacls.recognizer.Recognizer;
import ru.lumberjackcode.vacls.transfere.AdminResponse;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StreamCorruptedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.script.ScriptEngine;
import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import java.util.List;


public class FaceAuthenticatior {
    private static String modelPath;

    private final static Logger logger = Logger.getLogger(FaceAuthenticatior.class);

    public static void setStandartParams(String modelPath) {
        FaceAuthenticatior.modelPath = modelPath;
    }

    public static ClientResponse Authentificate(ClientRequest clientRequest, String clientScriptPath) throws Exception {
        logger.info("Authentificating face...");
        PostGresQL connection = new PostGresQL();
        Mat[] faces = clientRequest.getFrame();
        ArrayList<Integer> faceMaxParams = new ArrayList<>(), faceParams;
        faceMaxParams.add(0);
        faceMaxParams.add(0);
        double[] faceVector;
        logger.info(modelPath);
        for (Mat face : faces) {
            faceVector = (Recognizer.getInstance(modelPath)).getVector(face);
            faceParams = connection.findFace(faceVector);
            if (faceParams.get(0) != 0 && faceParams.get(1) > faceMaxParams.get(1))
                faceMaxParams = faceParams;
        }

        if (faceMaxParams.get(0) <= 0) {
            logger.info("User not found...");
            return new ClientResponse(false, "User not found", 0);
        }

        AdminResponse.JSParametrs jsParametrs = connection.GetAmountOfVisits(faceMaxParams.get(0));
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        ScriptEngine engine = factory.getScriptEngine(new RestrictAllCF());
        engine.eval(new String(Files.readAllBytes(Paths.get(clientScriptPath))));
        Invocable invocable = (Invocable) engine;
        Object result = invocable.invokeFunction("getMessageInternal", new String(jsParametrs.getUtf8Json()));
        return new ClientResponse(true, result.toString(), 0);

    }

    private static class RestrictAllCF implements ClassFilter {
        @Override
        public boolean exposeToScripts(String s) {
            return false;
        }
    }
}
