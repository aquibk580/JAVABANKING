import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class FetchAllData {

    // ANSI color codes for colored text output
    public static class Color {
        public static final String RESET = "\u001B[0m";
        public static final String YELLOW = "\u001B[33m";
        public static final String RED = "\u001B[31m";  // Red color for error message
    }

    // Method to establish connection to the MySQL database using DbConfig
    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DbConfig.JDBC_URL, DbConfig.USERNAME, DbConfig.PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Database connection failed! " + e.getMessage());
        }
        return connection;
    }

    // Method to hash the entered pin using SHA-256
    public static String hashPin(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pin.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();  // Return hashed pin
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to verify the pin of the account (without parameters)
    public static boolean verifyPin() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your account number: ");
        long accountNo = scanner.nextLong();

        System.out.print("Enter your account PIN: ");
        String enteredPin = scanner.next();

        String query = "SELECT pin FROM userAccount WHERE account_no = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set the account number in the query
            statement.setLong(1, accountNo);
            ResultSet resultSet = statement.executeQuery();

            // Check if account exists and pin matches
            if (resultSet.next()) {
                String storedHashedPin = resultSet.getString("pin");
                String hashedEnteredPin = hashPin(enteredPin);  // Hash the entered pin

                if (storedHashedPin.equals(hashedEnteredPin)) {
                    return fetchAccountData(accountNo);  // Call the account fetching method
                } else {
                    System.out.println(Color.RED + "Incorrect PIN! Please try again." + Color.RESET);
                    return false;  // PIN doesn't match
                }
            } else {
                System.out.println(Color.RED + "\n( Note: Account number not found! )\n" + Color.RESET);
                return false;  // Account not found
            }

        } catch (SQLException e) {
            System.out.println("Error while verifying pin! " + e.getMessage());
            return false;  // In case of an error
        }
    }

    // Method to fetch and display account details (takes accountNo as input)
    public static boolean fetchAccountData(long accountNo) {
        String query = "SELECT * FROM userAccount WHERE account_no = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, accountNo);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                System.out.println(Color.RED + "\n( Note: Account number not found! )\n" + Color.RESET);
                return false;  // Account not found
            }

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String phone = resultSet.getString("phone");
                String dob = resultSet.getString("dob");
                String gender = resultSet.getString("gender");
                String accType = resultSet.getString("acc_type");
                double balance = resultSet.getDouble("balance");

                System.out.println("\n*** | Account Details | ***");
                System.out.println("Account No   : " + accountNo);
                System.out.println("Name         : " + name);
                System.out.println("Mobile No.   : " + phone);
                System.out.println("Date of Birth: " + dob);
                System.out.println("Gender       : " + gender);
                System.out.println("Account Type : " + accType);
                System.out.println("Balance      : " + balance);
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Error while fetching data! " + e.getMessage());
            return false;
        }
    }

    // Main method to run the program without parameters
    public static void main(String[] args) {
        verifyPin();  // Simply call verifyPin(), it will handle PIN verification and account data fetching
    }
}
