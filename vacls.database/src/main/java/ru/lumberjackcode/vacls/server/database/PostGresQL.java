package ru.lumberjackcode.vacls.server.database;

import org.apache.log4j.Logger;

import java.sql.*;

import ru.lumberjackcode.vacls.recognizer.LinearDistanceComporator;
import java.util.LinkedList;
import java.util.List;

public class PostGresQL {
    static private final Logger logger = Logger.getLogger(PostGresQL.class);
    private int dbPort;
    private String url;
    private String login;
    private String password;
    private String dbName;

    public PostGresQL(int dbPort, String login, String password, String dbName) {
        this.dbPort = dbPort;
        this.login = login;
        this.password = password;
        this.dbName = dbName;
        this.url = "jdbc:postgresql://localhost:" + dbPort + "/" + dbName;
    }

    public boolean findFace(double[] faceVector) {
        logger.info("Searching face vector in database...");

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, login, password);
            logger.info("Database connection established...");

            try {
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery("SELECT * FROM REGISTERED_USERS");

                logger.info("Processing result from database...");
                LinearDistanceComporator comporator = new LinearDistanceComporator(1);
                while (result.next()) {
                    Double[] faceVectorFromDB = (Double[])(result.getArray("face_vector").getArray());
                    double[] primfaceVectorFromDB = new double[faceVectorFromDB.length];
                    for (int i = 0; i < faceVectorFromDB.length; primfaceVectorFromDB[i] = faceVectorFromDB[i], ++i);
                    if (comporator.compare(faceVector, primfaceVectorFromDB) == 0)
                        return true;
                }
            }
            finally {
                connection.close();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return false;
    }

    public void storeFace(double[] faceVector) {
        Double[] objectFaceVector = new Double[faceVector.length];
        for (int i = 0; i < faceVector.length; objectFaceVector[i] = faceVector[i], ++i);
        logger.info("Storing face vector in database...");
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, login, password);
            logger.info("Database connection established...");
            try {
                //Insert new faceVector into database
                Array preparedFaceVector = connection.createArrayOf("float8", objectFaceVector);
                String sql = "INSERT INTO REGISTERED_USERS (face_vector) VALUES (?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setArray(1, preparedFaceVector);

                logger.info("Uploading face vector to database...");
                preparedStatement.executeUpdate();
            }
            finally {
                connection.close();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }



    public static void main(String[] argv) {
        PostGresQL connection = new PostGresQL(6000, "vacls", "vaclsdbpassword", "vacls");
        double[] foo = {0.4, 0.666, 0.7564};
        connection.storeFace(foo);
        System.out.println(connection.findFace(foo));

    }


}
