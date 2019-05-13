package server.database;

import client.models.*;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DatabaseHandler extends Config {
    private Connection dbConnection;

    public DatabaseHandler () throws SQLException, ClassNotFoundException {
        this.dbConnection = getDbConnection();
    }

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {
        String connectionString =
                "jdbc:mysql://" + dbHost + ":" + dbPort +"/" + dbName + "?" + "useUnicode = true & useJDBCCompliantTimezoneShift = true & useLegacyDatetimeCode = false & serverTimezone = UTC";

        Class.forName("com.mysql.cj.jdbc.Driver");
        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);
        return dbConnection;
    }

    public User authorize(User userCreds) throws SQLException {
        ResultSet resultSet;
        String userName = userCreds.getUserName();
        String password = userCreds.getPassword();
        String select =
                "SELECT * FROM " + Constants.USER_TABLE + " WHERE userName = ?";

        PreparedStatement prSt = dbConnection.prepareStatement(select);
        prSt.setString(1, userName);
        resultSet = prSt.executeQuery();

        while (resultSet.next()) {
            if (resultSet.getString("password").equals(password)) {
             return new User(
                     resultSet.getInt("id"),
                     resultSet.getString("userName"),
                     resultSet.getString("password"),
                     resultSet.getInt("role")
             );
            }
        }
        return null;
    }

    public boolean registration(User user) throws SQLException {
        String insert = "INSERT INTO " + Constants.USER_TABLE
                + "(" + Constants.USER_LOGIN + "," + Constants.USER_PASSWORD + ")"
                + "VALUES(?,?)";

        User userInDb = authorize(user);
        if (userInDb == null) {
            PreparedStatement prSt = dbConnection.prepareStatement(insert);
            prSt.setString(1, user.getUserName());
            prSt.setString(2, user.getPassword());
            prSt.executeUpdate();
            return true;
        }
        return false;
    }

    public MedicalDirection[] getAllMedicalDirections() throws SQLException {
        ResultSet resultSet;
        String select =  "SELECT * FROM " + Constants.MEDICAL_DIRECTIONS_TABLE;

        PreparedStatement prSt = dbConnection.prepareStatement(select);
        resultSet = prSt.executeQuery();
        ArrayList<MedicalDirection> medicalDirections = new ArrayList<>();
        while (resultSet.next()) {
            medicalDirections.add(new MedicalDirection(
                    resultSet.getInt("id"),
                    resultSet.getString("title")
            ));
        }
        return medicalDirections.toArray(new MedicalDirection[medicalDirections.size()]);
    }

    public MedicalService[] getMedicalServicesByDirection(MedicalDirection medicalDirection) throws SQLException {
        ResultSet resultSet;
        String select = "SELECT * FROM " + Constants.MEDICAL_SERVICES_TABLE + " WHERE medicalservices.directionId = ?";
        PreparedStatement prSt = dbConnection.prepareStatement(select);
        prSt.setInt(1, medicalDirection.getId());
        resultSet = prSt.executeQuery();
        ArrayList<MedicalService> medicalServices = new ArrayList<>();
        while (resultSet.next()) {
            medicalServices.add(new MedicalService(
                    resultSet.getInt("id"),
                    resultSet.getString("title"),
                    resultSet.getFloat("price")
            ));
        }
        return medicalServices.toArray(new MedicalService[medicalServices.size()]);
    }

    public MedicalServiceDate[] getDatesByService(MedicalService medicalService) throws SQLException {
        ResultSet resultSet;
        String select = "SELECT * FROM " + Constants.MEDICAL_SERVICES_DATE_TABLE
                + " WHERE medicalservicesdate.serviceId = ?"
                + " AND medicalservicesdate.isReserved = false ";
        PreparedStatement prSt = dbConnection.prepareStatement(select);
        prSt.setInt(1, medicalService.getId());
        resultSet = prSt.executeQuery();
        ArrayList<MedicalServiceDate> medicalServicesDates = new ArrayList<>();
        while (resultSet.next()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
            medicalServicesDates.add(new MedicalServiceDate(
                    resultSet.getInt("id"),
                    dateFormat.format(resultSet.getTimestamp("date")),
                    resultSet.getBoolean("isReserved")
            ));
        }
        return medicalServicesDates.toArray(new MedicalServiceDate[medicalServicesDates.size()]);
    }

    public void addService(MedicalServiceAction medicalServiceAction) throws SQLException {
        String insert = "INSERT INTO " + Constants.USER_SERVICES
                + "(userId, directionId, serviceId, dateId)"
                + "VALUES(?,?,?,?)";

        String update = "UPDATE " + Constants.MEDICAL_SERVICES_DATE_TABLE
                + " SET isReserved = true "
                + " WHERE id = ?";

        PreparedStatement prSt = dbConnection.prepareStatement(insert);
        PreparedStatement prSt2 = dbConnection.prepareStatement(update);
            prSt.setInt(1, medicalServiceAction.getCurrentUser().getId());
            prSt.setInt(2, medicalServiceAction.getMedicalDirection().getId());
            prSt.setInt(3, medicalServiceAction.getMedicalService().getId());
            prSt.setInt(4, medicalServiceAction.getMedicalServiceDate().getId());
            prSt2.setInt(1, medicalServiceAction.getMedicalServiceDate().getId());
            prSt.executeUpdate();
            prSt2.executeUpdate();
    }

    public MedicalServiceAction[] getAllUsersServices(User user) throws SQLException {
        ResultSet resultSet;
        String select = "select " +
                "medicalservices.title AS serviceTitle, " +
                "medicaldirections.title AS directionTitle, " +
                "medicalservices.price, " +
                "medicalservicesdate.date, " +
                "userservices.id, " +
                "medicalservicesdate.id AS dateId " +
                "from userservices " +
                "Join medicaldirections ON userservices.directionId = medicaldirections.id " +
                "Join medicalservicesdate ON userservices.dateId = medicalservicesdate.id " +
                "Join medicalservices on userservices.serviceId = medicalservices.id " +
                "where userId = ? ";

        PreparedStatement prSt = dbConnection.prepareStatement(select);
        prSt.setInt(1, user.getId());
        resultSet = prSt.executeQuery();
        ArrayList<MedicalServiceAction> medicalServiceActions = new ArrayList<>();
        while (resultSet.next()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
            MedicalDirection medicalDirection = new MedicalDirection();
            medicalDirection.setTitle(resultSet.getString("directionTitle"));

            MedicalService medicalService = new MedicalService();
            medicalService.setId(resultSet.getInt("id"));
            medicalService.setTitle(resultSet.getString("serviceTitle"));
            medicalService.setPrice(resultSet.getFloat("price"));

            MedicalServiceDate medicalServiceDate = new MedicalServiceDate();
            medicalServiceDate.setId(resultSet.getInt("dateId"));
            medicalServiceDate.setDate(dateFormat.format(resultSet.getTimestamp("date")));

            MedicalServiceAction medicalServiceAction = new MedicalServiceAction();
            medicalServiceAction.setMedicalService(medicalService);
            medicalServiceAction.setMedicalServiceDate(medicalServiceDate);
            medicalServiceAction.setMedicalDirection(medicalDirection);
            medicalServiceActions.add(medicalServiceAction);
        }
        return medicalServiceActions.toArray(new MedicalServiceAction[medicalServiceActions.size()]);
    }

    public void declineMedicalService(MedicalServiceAction medicalServiceAction, boolean isNeedToRevertTime) throws SQLException {
        String delete = "DELETE FROM " + Constants.USER_SERVICES +
                " WHERE id = ? ";

        PreparedStatement prSt = dbConnection.prepareStatement(delete);
        prSt.setInt(1, medicalServiceAction.getMedicalService().getId());
        prSt.executeUpdate();
        if (isNeedToRevertTime) {
            String update = "UPDATE " + Constants.MEDICAL_SERVICES_DATE_TABLE
                    + " SET isReserved = false "
                    + " WHERE id = ?";

            PreparedStatement prSt2 = dbConnection.prepareStatement(update);
            prSt2.setInt(1, medicalServiceAction.getMedicalServiceDate().getId());
            prSt2.executeUpdate();
        }
    }

    public MedicalDirection addDirection(MedicalDirection medicalDirection) {
        String insert =  "INSERT INTO " + Constants.MEDICAL_DIRECTIONS_TABLE
                + "(title)"
                + "VALUES(?)";

        try {
            PreparedStatement prSt = dbConnection.prepareStatement(insert);
            prSt.setString(1, medicalDirection.getTitle());
            prSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return medicalDirection;
    }

}
