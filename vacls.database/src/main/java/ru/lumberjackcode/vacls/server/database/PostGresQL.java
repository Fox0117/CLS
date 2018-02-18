package ru.lumberjackcode.vacls.server.database;

import org.apache.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ru.lumberjackcode.vacls.transfere.AdminResponse;

import ru.lumberjackcode.vacls.recognizer.LinearDistanceComporator;

@SuppressWarnings("all")

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

    public int findFace(double[] faceVector) {
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
                        return result.getInt("id");
                }
            }
            finally {
                connection.close();
            }
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return 0;
    }

    public void storeNewFace(double[] faceVector) {
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
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void storeRegisteredFace(int registered_user_id) {
        logger.info("Storing registered face in database...");
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, login, password);
            logger.info("Database connection established...");
            try {
                //Insert registered user id into database
                String sql = "INSERT INTO IDENTIFIED_USERS (registered_user_id) VALUES (?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, registered_user_id);

                logger.info("Uploading registered face id to database...");
                preparedStatement.executeUpdate();
            }
            finally {
                connection.close();
            }
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public AdminResponse.EntriesRange getEntriesRange() {
        logger.info("Getting identified faces range...");
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, login, password);
            logger.info("Database connection established...");
            try {
                //Get entries range from database
                Statement statement = connection.createStatement();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.");
                ResultSet minRange = statement.executeQuery("SELECT min(identified_at) FROM IDENTIFIED_USERS");
                minRange.next();
                LocalDateTime minDateTime = minRange.getTimestamp("min").toLocalDateTime();
                ResultSet maxRange = statement.executeQuery("SELECT max(identified_at) FROM IDENTIFIED_USERS");
                maxRange.next();
                LocalDateTime maxDateTime = maxRange.getTimestamp("max").toLocalDateTime();

                return new AdminResponse.EntriesRange("", minDateTime, maxDateTime);
            }
            finally {
                connection.close();
            }
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return new AdminResponse.EntriesRange();
    }

    public AdminResponse.Entries getEntries(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Getting identified faces...");
        AdminResponse.Entries entries = new AdminResponse.Entries();
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, login, password);
            logger.info("Database connection established...");
            try {
                //Get entries from database
                String sql = "SELECT * FROM IDENTIFIED_USERS WHERE IDENTIFIED_AT BETWEEN (?) AND (?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setTimestamp(1, Timestamp.valueOf(startDate));
                preparedStatement.setTimestamp(2, Timestamp.valueOf(endDate));
                ResultSet result = preparedStatement.executeQuery();

                logger.info("Processing range from database....");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                while (result.next()) {
                    LocalDateTime dateTime = result.getTimestamp("identified_at").toLocalDateTime();
                    entries.add(new AdminResponse.Entry(Integer.toString(result.getInt("registered_user_id")), dateTime));
                }
            }
            finally {
                connection.close();
            }
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return entries;
    }
}
