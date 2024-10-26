import java.sql.*;
import java.util.Scanner;

public class transactionhistory {
    ColorSet color = new ColorSet();

    // Method to search for transaction history by account number and print details
    public void printTransactionHistory(String loggedInAccNo) {
        Scanner scanner = new Scanner(System.in);

        // Get account number from user
        System.out.print("Enter Account Number: ");
        String accNo = scanner.nextLine();

        // Validate if the entered account number matches the logged-in account number
        if (!accNo.equals(loggedInAccNo)) {
            System.out.println(color.red + "Access Denied! You can only view your own transaction history." + color.reset);
            return; // Exit if the account numbers do not match
        }

        // SQL query to fetch transaction history by account number
        String query = "SELECT accNo, withdraw, deposit, transaction_date, transaction_time FROM transactionHistory WHERE accNo = ?";

        Connection connection = null;
        try {
            // Establish the database connection using DbConfig
            connection = DriverManager.getConnection(DbConfig.JDBC_URL, DbConfig.USERNAME, DbConfig.PASSWORD);

            // Prepare the statement
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, accNo);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Check if results are found
            if (!resultSet.isBeforeFirst()) {  // No results found
                System.out.println("No transaction history found for account number: " + accNo);
            } else {
                // Print the transaction history details
                System.out.println("\nTransaction History for Account No: " + accNo);
                System.out.println("--------------------------------------------------");

                boolean hasTransactions = false; // Track if any valid transactions are printed

                while (resultSet.next()) {
                    double withdraw = resultSet.getDouble("withdraw");
                    double deposit = resultSet.getDouble("deposit");
                    Date transactionDate = resultSet.getDate("transaction_date");
                    Time transactionTime = resultSet.getTime("transaction_time");

                    // Check if the transaction has a valid withdraw or deposit value
                    boolean hasValidTransaction = (withdraw != 0 || deposit != 0); // Check for valid transactions

                    // Print only if there is a valid transaction
                    if (hasValidTransaction) {
                        hasTransactions = true; // Mark that we have at least one valid transaction
                        System.out.println("Transaction Details:");
                        if (withdraw != 0) {
                            System.out.println("Withdraw: " + withdraw);
                        } 
                        if (deposit != 0) {
                            System.out.println("Deposit: " + deposit);
                        }
                        System.out.println("Date: " + transactionDate);
                        System.out.println("Time: " + transactionTime);
                        System.out.println("--------------------------------------------------");
                    }
                }

                if (!hasTransactions) {
                    System.out.println("No valid transactions found for account number: " + accNo);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the connection
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Main method to run the program
    public static void main(String[] args) {
        transactionhistory transactionHistory = new transactionhistory();
        transactionHistory.printTransactionHistory("12345"); // Replace with the logged-in account number
    }
}
