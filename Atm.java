import java.sql.*;
import java.util.*;

public class Atm {
    ColorSet color = new ColorSet();
    Scanner in = new Scanner(System.in);

    public void atm() {
        try {
           Connection connection = DriverManager.getConnection(DbConfig.JDBC_URL, DbConfig.USERNAME, DbConfig.PASSWORD);


            // Prompt for account number first
            System.out.println("\n\t\t * | ATM Access | * ");
            PreparedStatement accCheckStmt = connection.prepareStatement(
                "SELECT account_no FROM userAccount WHERE account_no = ?");
            PreparedStatement insertuseracc = connection.prepareStatement(
                "INSERT INTO transactionHistory (accNo) VALUES (?)"
            );

            // Input account number
            System.out.print("\tEnter Account Number: ");
            String accNo = in.next();
            accCheckStmt.setString(1, accNo);
            ResultSet accCheckResult = accCheckStmt.executeQuery();

            // Check if the account number exists
            if (!accCheckResult.next()) {
                System.out.println(color.red + "\n\tInvalid Account Number! Access Denied." + color.reset);
                return;
            } else {
                System.out.println(color.green + "\n\tAccount number found, proceed with authentication!" + color.reset);
                
                // Insert the account number into transactionHistory
                insertuseracc.setString(1, accNo);
                insertuseracc.executeUpdate(); // Correct method for INSERT statements
            }

            // Proceed with account type and PIN after account number verification
            PreparedStatement statement = connection.prepareStatement(
                "SELECT account_no, acc_type, pin, balance FROM userAccount WHERE account_no = ? AND acc_type = ? AND pin = ?");

            statement.setString(1, accNo); // Set account number for further query

            // Input account type
            String accType;
            while (true) {
                System.out.print(color.yellow + "\tAccount Type (C for Current / S for Saving): " + color.reset);
                accType = in.next().toLowerCase();
                if (accType.equals("c")) {
                    statement.setString(2, "current");
                    break;
                } else if (accType.equals("s")) {
                    statement.setString(2, "saving");
                    break;
                } else {
                    System.out.println(color.red + "\tInvalid Account Type. Please enter 'C' or 'S'." + color.reset);
                }
            }

            // Input PIN
            System.out.print("\tEnter PIN: ");
            String enteredPin = in.next();
            Hashing hashing = new Hashing();
            String hashedPin = hashing.hashPassword(enteredPin);
            statement.setString(3, hashedPin);

            // Execute query to verify account type and PIN
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                System.out.println(color.green + "\n\tAccess Granted!" + color.reset);
                long accountNo = resultSet.getLong("account_no");
                double balance = resultSet.getDouble("balance");

                // Menu for options
                while (true) {
                    System.out.println("\n\tSelect Option:");
                    System.out.println("\t1. Deposit");
                    System.out.println("\t2. Withdraw");
                    System.out.println("\t3. Check Balance");
                    System.out.println("\t4. Change PIN");
                    System.out.println("\t5. Transaction History");
                    System.out.println("\t6. Exit");

                    System.out.print("\tOption -> ");
                    int option = in.nextInt();

                    switch (option) {
                        case 1:
                            balance = deposit(accountNo, connection);
                            break;
                        case 2:
                            balance = withdraw(accountNo, connection, balance);
                            break;
                        case 3:
                            System.out.println(color.green + "\n\tYour Current Balance: " + balance + color.reset);
                            break;
                        case 4:
                            changePin(accountNo, connection);
                            break;
                        case 5:
                            transactionhistory trans = new transactionhistory();
                            trans.printTransactionHistory(accNo);
                            break;
                        case 6:
                            System.out.println("\n\tThank you for using our ATM. Goodbye!");
                            connection.close();
                            return;
                        default:
                            System.out.println(color.red + "\tInvalid option, try again." + color.reset);
                    }
                }
            } else {
                System.out.println(color.red + "\n\tAccess Denied! Invalid Account Type or PIN." + color.reset);
            }

            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(color.red + "Error: " + e.getMessage() + color.reset);
        }
    }

    // Deposit Method
    public double deposit(long accountNo, Connection connection) {
        try {
            System.out.print("\tEnter deposit amount: ");
            double depositAmount = in.nextDouble();

            // Update the user's balance
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE userAccount SET balance = balance + ? WHERE account_no = ?"
            );

            // Insert the deposit into transactionHistory
            PreparedStatement insertDep = connection.prepareStatement(
                "INSERT INTO transactionHistory (deposit, accNo, transaction_date, transaction_time) VALUES (?, ?, CURRENT_DATE, CURRENT_TIME)"
            );

            // Set the deposit amount and account number for updating balance
            statement.setDouble(1, depositAmount);
            statement.setLong(2, accountNo);

            // Set the deposit amount and account number for transaction history
            insertDep.setDouble(1, depositAmount);
            insertDep.setLong(2, accountNo);

            // Execute the update for both statements
            statement.executeUpdate();  // This updates the balance in userAccount
            insertDep.executeUpdate();   // This inserts the record into transactionHistory

            // Fetch updated balance
            PreparedStatement balanceStatement = connection.prepareStatement("SELECT balance FROM userAccount WHERE account_no = ?");
            balanceStatement.setLong(1, accountNo);
            ResultSet resultSet = balanceStatement.executeQuery();

            double newBalance = 0;
            if (resultSet.next()) {
                newBalance = resultSet.getDouble("balance");
                System.out.println(color.green + "\n\tDeposit Successful! New Balance: " + newBalance + color.reset);
            }

            balanceStatement.close();
            statement.close();
            return newBalance; // Return the new balance
        } catch (SQLException e) {
            System.out.println(color.red + "Error: " + e.getMessage() + color.reset);
            return 0;
        }
    }

// Withdraw Method
public double withdraw(long accountNo, Connection connection, double currentBalance) {
    try {
        System.out.print("\tEnter withdrawal amount: ");
        double withdrawAmount = in.nextDouble();

        // Fetch current balance
        PreparedStatement balanceStatement = connection.prepareStatement("SELECT balance FROM userAccount WHERE account_no = ?");
        balanceStatement.setLong(1, accountNo);
        ResultSet resultSet = balanceStatement.executeQuery();

        double balance = 0;
        if (resultSet.next()) {
            balance = resultSet.getDouble("balance");
        }

        if (withdrawAmount > balance) {
            System.out.println(color.red + "\n\tInsufficient funds!" + color.reset);
            return balance; // Return the current balance if not enough funds
        }

        double newBalance = balance - withdrawAmount;

        // Update the user's balance
        PreparedStatement updateStatement = connection.prepareStatement("UPDATE userAccount SET balance = ? WHERE account_no = ?");
        updateStatement.setDouble(1, newBalance);
        updateStatement.setLong(2, accountNo);
        updateStatement.executeUpdate();

        // Insert the withdrawal into transactionHistory
        PreparedStatement insertWith = connection.prepareStatement(
            "INSERT INTO transactionHistory (withdraw, accNo, transaction_date, transaction_time) VALUES (?, ?, CURRENT_DATE, CURRENT_TIME)"
        );

        // Set the withdrawal amount and account number for transaction history
        insertWith.setDouble(1, withdrawAmount);
        insertWith.setLong(2, accountNo);
        insertWith.executeUpdate(); // Insert the withdrawal record

        // Fetch updated balance after withdrawal
        balanceStatement.setLong(1, accountNo);
        resultSet = balanceStatement.executeQuery();
        if (resultSet.next()) {
            newBalance = resultSet.getDouble("balance");
            System.out.println(color.green + "\n\tWithdrawal Successful! New Balance: " + newBalance + color.reset);
        }

        balanceStatement.close();
        updateStatement.close();
        insertWith.close();
        return newBalance; // Return the new balance
    } catch (SQLException e) {
        System.out.println(color.red + "Error: " + e.getMessage() + color.reset);
        return currentBalance;
    }
}


    // Change PIN Method
    public void changePin(long accountNo, Connection connection) {
        try {
            System.out.print("\tEnter new PIN: ");
            String newPin = in.next();
            String hashedNewPin = new Hashing().hashPassword(newPin);

            PreparedStatement statement = connection.prepareStatement("UPDATE userAccount SET pin = ? WHERE account_no = ?");
            statement.setString(1, hashedNewPin);
            statement.setLong(2, accountNo);
            statement.executeUpdate();

            System.out.println(color.green + "\n\tPIN changed successfully!" + color.reset);
            statement.close();
        } catch (SQLException e) {
            System.out.println(color.red + "Error: " + e.getMessage() + color.reset);
        }
    }
}
