package ru.lumberjackcode.vacls.server.database;

import org.apache.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import ru.lumberjackcode.vacls.transfere.AdminResponse;

import ru.lumberjackcode.vacls.recognizer.LinearDistanceComporator;

@SuppressWarnings("all")

public class PostGresQL {
    static private final Logger logger = Logger.getLogger(PostGresQL.class);
    private static boolean isStandartParamsSet = false;
    private static double precision;
    private static int standartDbPort;
    private static String standartDbLogin;
    private static String standartDbPassword;
    private static String standartDbName;
    private int dbPort;
    private String url;
    private String login;
    private String password;
    private String dbName;

    public static void setStandartParams(int dbPort, String login, String password, String dbName, double precision) {
        standartDbPort = dbPort;
        standartDbLogin = login;
        standartDbPassword = password;
        standartDbName = dbName;
        isStandartParamsSet = true;
        PostGresQL.precision = precision;
    }

    public PostGresQL(int dbPort, String login, String password, String dbName, double precision) {
        logger.info("Creating connection object...");
        this.dbPort = dbPort;
        this.login = login;
        this.password = password;
        this.dbName = dbName;
        PostGresQL.precision = precision;
        this.url = "jdbc:postgresql://localhost:" + dbPort + "/" + dbName;
    }

    public PostGresQL() {
        this(standartDbPort, standartDbLogin, standartDbPassword, standartDbName, precision);

        if (!isStandartParamsSet)
            throw new IllegalArgumentException("Standart params do not set for PostGresQl");
    }

    public ArrayList<Integer> findFace(double[] faceVector) {
        logger.info("Searching face vector in database...");
        ArrayList<Integer> faceParams = new ArrayList<>();
        faceParams.add(0);
        faceParams.add(0);

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, login, password);
            logger.info("Database connection established...");

            try {
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery("SELECT * FROM REGISTERED_USERS");

                logger.info("Processing result from database...");
                LinearDistanceComporator comporator = new LinearDistanceComporator(precision);
                while (result.next()) {
                    Double[] faceVectorFromDB = (Double[])(result.getArray("face_vector").getArray());
                    double[] primfaceVectorFromDB = new double[faceVectorFromDB.length];
                    for (int i = 0; i < faceVectorFromDB.length; primfaceVectorFromDB[i] = faceVectorFromDB[i], ++i);
                    if (comporator.compare(faceVector, primfaceVectorFromDB) == 0) {
                        storeRegisteredFace(result.getInt("id"));
                        faceParams.set(0, result.getInt("id"));
                        faceParams.set(1, result.getInt("total_amount"));
                        return faceParams;
                    }
                }

                storeNewFace(faceVector);
            }
            finally {
                connection.close();
            }
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return faceParams;
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

    public void storeRegisteredFace(int registeredUserId) {
        logger.info("Storing registered face in database...");
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, login, password);
            logger.info("Database connection established...");
            try {
                //Insert registered user id into database
                String sql = "INSERT INTO IDENTIFIED_USERS (registered_user_id) VALUES (?); " +
                        "UPDATE REGISTERED_USERS SET TOTAL_AMOUNT = TOTAL_AMOUNT + 1 WHERE ID = (?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, registeredUserId);
                preparedStatement.setInt(2, registeredUserId);

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
        logger.info("Getting entries range...");
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
                Timestamp minTimestamp = minRange.getTimestamp("min");
                LocalDateTime minDateTime = LocalDateTime.of(1, 1, 1, 0, 0, 0);
                LocalDateTime maxDateTime = LocalDateTime.of(1, 1, 1, 0, 0, 0);
                if (minTimestamp != null)
                    minDateTime = minTimestamp.toLocalDateTime();
                ResultSet maxRange = statement.executeQuery("SELECT max(identified_at) FROM IDENTIFIED_USERS");
                maxRange.next();
                Timestamp maxTimestamp = maxRange.getTimestamp("max");
                if (minTimestamp != null)
                    maxDateTime = maxTimestamp.toLocalDateTime();

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
        logger.info("Getting entries...");
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

                logger.info("Processing entries from database....");
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

    public AdminResponse.JSParametrs GetAmountOfVisits(int registeredUserId) {
        logger.info("Getting amount of visits...");
        AdminResponse.JSParametrs resultAmount = new AdminResponse.JSParametrs();
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, login, password);
            logger.info("Database connection established...");
            try {
                String sql = "SELECT TOTAL_AMOUNT FROM REGISTERED_USERS WHERE ID = (?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, registeredUserId);
                ResultSet result = preparedStatement.executeQuery();
                result.next();
                resultAmount.total_visits = result.getInt(1);

                //Get year amount from database
                LocalDateTime startDate = LocalDateTime.of(LocalDateTime.now().getYear(), Month.JANUARY, 1, 0, 0, 0);
                LocalDateTime endDate = LocalDateTime.now();
                sql = "SELECT COUNT(*) FROM IDENTIFIED_USERS WHERE IDENTIFIED_AT BETWEEN (?) AND (?) AND REGISTERED_USER_ID = (?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setTimestamp(1, Timestamp.valueOf(startDate));
                preparedStatement.setTimestamp(2, Timestamp.valueOf(endDate));
                preparedStatement.setInt(3, registeredUserId);
                result = preparedStatement.executeQuery();
                result.next();
                resultAmount.last_year_visits = result.getInt(1);

                //Get month amount from database
                startDate = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), 1, 0, 0, 0);
                endDate = LocalDateTime.now();
                preparedStatement.setTimestamp(1, Timestamp.valueOf(startDate));
                preparedStatement.setTimestamp(2, Timestamp.valueOf(endDate));
                result = preparedStatement.executeQuery();
                result.next();
                resultAmount.last_month_visits = result.getInt(1);

                //Get week amount from database
                startDate = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
                        LocalDateTime.now().getDayOfMonth() - LocalDateTime.now().getDayOfWeek().getValue() + 1, 0, 0, 0);
                endDate = LocalDateTime.now();
                preparedStatement.setTimestamp(1, Timestamp.valueOf(startDate));
                preparedStatement.setTimestamp(2, Timestamp.valueOf(endDate));
                result = preparedStatement.executeQuery();
                result.next();
                resultAmount.last_week_visits = result.getInt(1);

                //Get day amount from database
                startDate = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
                        LocalDateTime.now().getDayOfMonth(), 0, 0, 0);
                endDate = LocalDateTime.now();
                preparedStatement.setTimestamp(1, Timestamp.valueOf(startDate));
                preparedStatement.setTimestamp(2, Timestamp.valueOf(endDate));
                result = preparedStatement.executeQuery();
                result.next();
                resultAmount.last_day_visits = result.getInt(1);

                return resultAmount;
            }
            finally {
                connection.close();
            }
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return resultAmount;
    }
}
