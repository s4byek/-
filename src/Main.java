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
        getAllPatients(connection);
        getAllTests(connection);
        getAllDoctors(connection);
//
//        searchPatientByLastName(connection,"Зубенко",true);
//        searchDoctorByID(connection, 8,true);
//        searchTestById(connection, 1, true);
//
//        searcAllTestsForPatient(connection, "Шаурма");

//        addPatient(connection, "Дмитрий", "Белинский", "м",   "89950191488");
//        deletePatient(connection, 6);
//        updatePatient(connection,7,"Дмитрий", "Белинский", "м",   "+79950191488");
//        patientDateTest(connection);

    }

    // Метод получения всех пациентов
    public static void getAllPatients(Connection connection) throws SQLException {
        String query = "SELECT * FROM patients";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                System.out.println("id: " + resultSet.getInt("id") +
                        ", first_name: " + resultSet.getString("first_name") + " " + resultSet.getString("last_name"));
            }
        }
    }

    public static void getAllTests(Connection connection) throws SQLException {
        String query = "SELECT * FROM tests";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                System.out.println("|" + resultSet.getInt("test_id") + "|" + resultSet.getString("test_name") + "|"
                        + resultSet.getDouble("price"));
            }
        }
    }

    public static void getAllDoctors(Connection connection) throws SQLException {
        String query = "SELECT * FROM doctors";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                System.out.println("doctor_id: " + resultSet.getInt("doctor_id") +
                        " |first_name: " + resultSet.getString("first_name") +
                        " |last_name: " + resultSet.getString("last_name") +
                        " |specialization: " + resultSet.getString("specialization") +
                        " |contact_number: " + resultSet.getString("contact_number"));
            }
        }
    }

    public static int searchPatientByLastName(Connection connection, String lastName, boolean flag) throws SQLException {
        String query = "SELECT * FROM patients WHERE last_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, lastName);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    if (flag) {
                        System.out.println("id: " + resultSet.getInt("id") +
                                ", first_name: " + resultSet.getString("first_name") +
                                ", last_name: " + resultSet.getString("last_name") +
                                ", gender: " + resultSet.getString("gender") +
                                ", contact_number: " + resultSet.getString("contact_number"));
                    } else {
                        return resultSet.getInt("id");
                    }
                }
            }
            return 0;
        }
    }

    public static String searchDoctorByID(Connection connection, int doctorId, boolean flag) throws SQLException {
        String query = "SELECT * FROM doctors WHERE doctor_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, doctorId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                if (flag) {
                    System.out.println("doctor_id: " + resultSet.getInt("doctor_id") +
                            " |first_name: " + resultSet.getString("first_name") +
                            " |last_name: " + resultSet.getString("last_name") +
                            " |specialization: " + resultSet.getString("specialization") +
                            " |contact_number: " + resultSet.getString("contact_number"));
                }
            } else {
                System.out.println("Doctor with ID " + doctorId + " not found");
            }
            return resultSet.getString("first_name") + " " + resultSet.getString("last_name");
        }
    }

    public static String searchTestById(Connection connection, int testId, boolean flag) throws SQLException {
        String query = "SELECT * FROM tests WHERE test_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, testId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                if (flag) {
                    System.out.println(resultSet.getInt("test_id") +
                            "|" + resultSet.getString("test_name") +
                            "|" + resultSet.getDouble("price"));
                }
            } else {
                System.out.println("Test with ID " + testId + " not found");
            }
            return resultSet.getString("test_name");
        }
    }

    public static void searcAllTestsForPatient(Connection connection, String lastName) throws SQLException {
        int patinetid = searchPatientByLastName(connection, lastName, false);
        String query = "SELECT * FROM test_results WHERE patient_id= " + patinetid;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String doc = searchDoctorByID(connection, resultSet.getInt("doctor_id"), false);
                String test = searchTestById(connection, resultSet.getInt("test_id"), false);
                System.out.println("id: " + resultSet.getInt("id") +
                        ", test: " + test +
                        ", doctor: " + doc +
                        ", test_date: " + resultSet.getDate("test_date") +
                        ", result_value: " + resultSet.getString("result_value"));
            }
        }
    }

    public static void addPatient(Connection connection, String firstName, String lastName, String gender, String contactNumber) throws SQLException {
        String query = "INSERT INTO patients (first_name, last_name, gender, contact_number) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, gender);
            BigDecimal contactNumberBigDecimal = new BigDecimal(contactNumber);
            statement.setBigDecimal(4, contactNumberBigDecimal);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Patient successfully added");
            }
        }
    }

    public static void deletePatient(Connection connection, int patientId) throws SQLException {
        String query = "DELETE FROM patients WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, patientId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Patient successfully deleted");
            } else {
                System.out.println("Patient with the specified ID not found");
            }
        }
    }

    public static void updatePatient(Connection connection, int patientId, String firstName, String lastName, String gender, String contactNumber)
            throws SQLException {
        String query = "UPDATE patients SET first_name = ?, last_name = ?, gender = ?, contact_number = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, gender);
            BigDecimal contactNumberBigDecimal = new BigDecimal(contactNumber);
            statement.setBigDecimal(4, contactNumberBigDecimal);
            statement.setInt(5, patientId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Patient information successfully updated");
            } else {
                System.out.println("Patient with the specified ID not found");
            }
        }
    }

    public static void patientDateTest(Connection connection) {
        String query = "SELECT p.id, p.first_name, p.last_name, r.test_date, r.result_value " +
                "FROM patient p " +
                "JOIN test_results r ON p.id = r.patient_id";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                Date testDate = resultSet.getDate("test_date");
                String resultValue = resultSet.getString("result_value");
                System.out.println("Patient: " + firstName + " " + lastName +
                        ", Test date: " + testDate +
                        ", Result: " + resultValue);
            }
        } catch (SQLException e) {
            e.getMessage();
        }
    }
}
