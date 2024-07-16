import java.math.BigDecimal;
import java.sql.*;

public class Main {
    private static final String PROTOCOL = "jdbc:postgresql://";
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String URL_LOCALE_NAME = "localhost/";
    private static final String DATABASE_NAME = "MedLab";
    public static final String USER_NAME = "postgres";
    public static final String DATABASE_PASS = "26112005";
    private static final String URL_REMOTE = "host.docker.internal:5432/";

    public static void main(String[] args) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Connected JDBC Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC Driver is not found. Include it in your library path");
        }
        Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost/MedLab",
                "postgres",
                "26112005"); // открытие соединения с базой данных
        System.out.println("Connected to the database");
//        getAllPatients(connection);
//        getAllTests(connection);
//        getAllDoctors(connection);
//
//        searchPatientByLastName(connection,"Зубенко",true);
//        searchDoctorByID(connection, 8,true);
//        searchTestById(connection, 1, true);

//        searcAllTestsForPatient(connection, "Шаурма");

//        addPatient(connection, "Дмитрий", "Белинский", "м",   "89950191488");
//        deletePatient(connection, 6);
//        updatePatient(connection,7,"Дмитрий", "Белинский", "м",   "+79950191488");
//        patientDateTest(connection);

    }

    // Метод получения всех пациентов
    public static void getAllPatients(Connection connection) throws SQLException {
        String query = "SELECT * FROM Пациент";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("ID") +
                        ", имя: " + resultSet.getString("Имя") + " " + resultSet.getString("Фамилия"));
            }
        }
    }

    public static void getAllTests(Connection connection) throws SQLException {
        String query = "SELECT * FROM Анализы";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                System.out.println("|" + resultSet.getInt("TestID") + "|" + resultSet.getString("TestName") + "|" + resultSet.getDouble("Price"));
            }
        }
    }

    public static void getAllDoctors(Connection connection) throws SQLException {
        String query = "SELECT * FROM Врач";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                System.out.println("DoctorID: " + resultSet.getInt("DoctorID") +
                        " |FirstName: " + resultSet.getString("FirstName") +
                        " |LastName: " + resultSet.getString("LastName") +
                        " |Specialization: " + resultSet.getString("Specialization") +
                        " |ContactNumber: " + resultSet.getString("ContactNumber"));

            }
        }
    }

    //методы поисков
    public static int searchPatientByLastName(Connection connection, String lastName, boolean flag) throws SQLException {
        String query = "SELECT * FROM \"Пациент\" WHERE \"Фамилия\" = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, lastName);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    if (flag) {
                        System.out.println("ID: " + resultSet.getInt("ID") +
                                ", Имя: " + resultSet.getString("Имя") +
                                ", Фамилия: " + resultSet.getString("Фамилия") +
                                ", Пол: " + resultSet.getString("Пол") +
                                ", Контактный номер: " + resultSet.getString("Контактный номер"));
                    } else {
                        return resultSet.getInt("ID");
                    }
                }
            }
            return 0;
        }
    }

    public static String searchDoctorByID(Connection connection, int doctorId, boolean flag) throws SQLException {
        String query = "SELECT * FROM Врач WHERE \"DoctorID\" = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, doctorId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                if (flag) {
                    System.out.println("DoctorID: " + resultSet.getInt("DoctorID") +
                            " |FirstName: " + resultSet.getString("FirstName") +
                            " |LastName: " + resultSet.getString("LastName") +
                            " |Specialization: " + resultSet.getString("Specialization") +
                            " |ContactNumber: " + resultSet.getString("ContactNumber"));
                }
            } else {
                System.out.println("Doctor with ID " + doctorId + " not found");
            }
            return resultSet.getString("FirstName") + " " + resultSet.getString("LastName");
        }
    }

    public static String searchTestById(Connection connection, int testId, boolean flag) throws SQLException {
        String query = "SELECT * FROM Анализы WHERE \"TestID\" = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, testId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                if (flag) {
                    System.out.println(resultSet.getInt("TestID") +
                            "|" + resultSet.getString("TestName") +
                            "|" + resultSet.getDouble("Price"));
                }
            } else {
                System.out.println("Test with ID " + testId + " not found");

            }
            return resultSet.getString("TestName");
        }
    }


    public static void searcAllTestsForPatient(Connection connection, String lastName) throws SQLException {
        int patinetid = searchPatientByLastName(connection, lastName, false);  //для удобства поиска по фамилии
        String query = "SELECT * FROM \"Результаты анализов\" WHERE \"PatientID\"= " + patinetid;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String doc = searchDoctorByID(connection, resultSet.getInt("DoctorID"), false);
                String test = searchTestById(connection, resultSet.getInt("TestID"), false);
                System.out.println("ID: " + resultSet.getInt("ID") +
                        ", Test: " + test +
                        ", Doctor: " + doc +
                        ", TestDate: " + resultSet.getDate("TestDate") +
                        ", ResultValue: " + resultSet.getString("ResultValue"));
            }
        }
    }


    public static void addPatient(Connection connection, String firstName, String lastName, String gender, String contactNumber) throws SQLException {
        String query = "INSERT INTO \"Пациент\" (\"Имя\", \"Фамилия\", \"Пол\", \"Контактный номер\") VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, gender);
            BigDecimal contactNumberBigDecimal = new BigDecimal(contactNumber);
            statement.setBigDecimal(4, contactNumberBigDecimal);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {                                   //возвращает количетсво изменненных строк
                System.out.println("Пациент успешно добавлен");
            }
        }
    }

    public static void deletePatient(Connection connection, int patientId) throws SQLException {
        String query = "DELETE FROM \"Пациент\" WHERE \"ID\" = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, patientId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Пациент успешно удален");
            } else {
                System.out.println("Пациент с указанным ID не найден");
            }
        }
    }

    public static void updatePatient(Connection connection, int patientId, String firstName, String lastName, String gender, String contactNumber) throws SQLException {
        String query = "UPDATE \"Пациент\" SET \"Имя\" = ?, \"Фамилия\" = ?, \"Пол\" = ?, \"Контактный номер\" = ? WHERE \"ID\" = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, gender);
            BigDecimal contactNumberBigDecimal = new BigDecimal(contactNumber);
            statement.setBigDecimal(4, contactNumberBigDecimal);
            statement.setInt(5, patientId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Информация о пациенте успешно обновлена");
            } else {
                System.out.println("Пациент с указанным ID не найден");
            }
        }
    }
    public static void patientDateTest(Connection connection) {
        String query = "SELECT p.\"ID\", p.\"Имя\", p.\"Фамилия\", r.\"TestDate\", r.\"ResultValue\" " +
                "FROM \"Пациент\" p " +
                "JOIN \"Результаты анализов\" r ON p.\"ID\" = r.\"PatientID\"";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String firstName = resultSet.getString("Имя");
                String lastName = resultSet.getString("Фамилия");
                Date testDate = resultSet.getDate("TestDate");
                String resultValue = resultSet.getString("ResultValue");
                System.out.println("Пациент: " + firstName + " " + lastName +
                        ", Дата анализа: " + testDate +
                        ", Результат: " + resultValue);
            }
        } catch (SQLException e) {
            e.getMessage();
        }
    }
}
