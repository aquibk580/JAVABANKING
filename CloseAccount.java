import java.io.Console;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

public class CloseAccount {

    public void clean() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static String ac_no;
    Scanner in = new Scanner(System.in);
    ColorSet color = new ColorSet();

    // accountRemove Function Definition
    // Passing args as AccountNo, DB connection
    public void accountRemove(String AccountNo, Connection connection) {
        try {
            // Preparing SQL Query
            String query = "delete from userAccount where account_no = ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, AccountNo);

            // Executing Prepared Query
            statement.execute();
            System.out.println(color.cyan + " Account Deleted ! " + color.reset);

        } catch (SQLException e) {
            // If an error occur Then
            System.out.println("Something went wrong ->" + e.getMessage());

        }

    }

    // -----------------------------------------------------------------------------------------------------------
    // This is main Account Delete Function
    public void deleteAccount() {

        try {
            // Creating Database Connection
            Connection connection = DriverManager.getConnection(DbConfig.JDBC_URL, DbConfig.USERNAME, DbConfig.PASSWORD);

            // Preparing SQL Query -1 ------ (To validate account_no and pin exist in Table)
            String query = "select account_no, name , pin from userAccount where account_no=? and pin=? ";
            PreparedStatement statement = connection.prepareStatement(query);
            clean();

            System.out.println("\n\n\t\t*** | Account Details | *** ");

            // for checking account_no ...
            while (true) {
                System.out.print("\n\n\tEnter your Account No  ->");
                String userAccountNo = in.next();
                ac_no = userAccountNo;

                // Check if the length is exactly 12
                if (userAccountNo.length() != 12) {

                    // If not 12 characters long, display appropriate error message
                    System.out.println(color.red + "\n( Note: Account Number must contain exactly 12 characters! )\n"
                            + color.reset);
                } else {
                    // If it's 12 characters long, check if it's numeric
                    String regex = "^\\d{12}$";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(userAccountNo);

                    if (matcher.matches()) {
                        // If it's 12 digits and numeric, set the parameter for SQL query
                        statement.setString(1, userAccountNo);
                        break; // Exit the loop
                    } else {
                        // If it contains non-numeric characters
                        System.out.println(color.red
                                + "\n( Note: Account Number must contain only numeric characters! )\n" + color.reset);
                    }
                }
            }

            // for checking pin ....
            while (true) {
                clean();
                System.out.println(color.green + "\n\n\tAccount No : " + ac_no + color.reset);
                Console console = System.console();
                char[] passwordArray = console.readPassword("Enter your password: ");
                String userPin = new String(passwordArray);

                // System.out.print("\n\tEnter your Unique Pin -> " + color.purple);
                // String userPin = in.next();
                System.out.println(color.reset);

                // Ensure it contains only 4 digits
                String regex = "^\\d{4}$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(userPin);
                // If 4 Digits
                if (matcher.matches()) {

                    // Hashing object created
                    Hashing hash = new Hashing();
                    String hashedPIN = hash.hashPassword(userPin);

                    // Giving 2st parameter to SQL Query -1 ------
                    statement.setString(2, hashedPIN);
                    break;

                } else {
                    // If not 4 digits
                    System.out.println(
                            color.red + "\n(Note : Account Pin should must contain 4 digits only ! )\n" + color.reset);
                }
            }
            // Executing Prepared SQL Query -1 ------
            ResultSet resultSet = statement.executeQuery();
            // -----------------------------------------------------------------------------------------------------------

            if (resultSet.next()) {
                String account_no = resultSet.getString(1);
                // String userPin = resultSet.getString(2);
                clean();
                System.out.println(color.cyan + "\n\t\tWelcome ," + resultSet.getString(2) + color.reset + "\n");

                OUTER: while (true) {
                    System.out.print("Are You sure want to delete ? \t [ Y(Yes) / N(No) ] :- ");
                    String opt = in.next();
                    opt = opt.toLowerCase();
                    switch (opt) {
                        case "y", "yes" -> {
                            System.out.print(color.green + "\nTell us Why are You Closing Account? \n>" + color.cyan);
                            String review = in.next() + color.reset;
                            clean();
                            if (!review.isEmpty()) {
                                System.out.println(color.yellow + "Thanks For Reviewing !!" + color.reset);
                            }
                            CloseAccount closeAccount = new CloseAccount();
                            // Function Calling ----------------------
                            closeAccount.accountRemove(account_no, connection);
                            break OUTER;
                        }
                        case "n", "no" -> {
                            Main start = new Main();
                            start.Home();
                            break OUTER;
                        }
                        default -> System.out.println("Invalid Input !!");
                    }
                }
            } else {
                System.out.println(color.red + "\tAccount Not Found or Pin is Incorrect !" + color.reset);
                System.out.print(color.cyan + "\n  Retry? [Y(yes) / N(no) - " + color.reset);
                String exit = in.next();
                // if (exit.contains(equalsIgnoreCase("y")));
                if ("y".equals(exit.toLowerCase()) || "yes".equals(exit.toLowerCase())) {
                    deleteAccount();
                    clean();
                    in.nextByte();
                } else {
                    Main home = new Main();
                    home.Home();
                }
            }
            statement.close();
            connection.close();

        } catch (SQLException e) {
            // System.out.println("Something went wrong ! Please Try Again...." +
            // e.getMessage());
            System.out.println("Server Down ! Please Try Again....");
        }

    }

}
