import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// import com.mysql.cj.protocol.Resultset;

public class CreateAccount {

  public void clean() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

  Scanner in = new Scanner(System.in);
  ColorSet color = new ColorSet();

  public void createUser() {
    try {
      Connection connection = DriverManager.getConnection(DbConfig.JDBC_URL, DbConfig.USERNAME, DbConfig.PASSWORD);
      String query = "insert into userAccount (name,phone,dob,gender,pin,acc_type) values(?,?,str_to_date(?,'%d/%m/%Y'),?,?,?);";
      PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

      // taking user name ....
      while (true) {
        clean();
        System.out.println("\n\t\t*** | -- Account Holder Details -- | *** \n\tPersonal Details -- ");
        System.out.print("\t\tAccount Holder Name ->  ");
        String name = in.nextLine();
        String regex = "^[A-Za-z\\s']+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) {
          statement.setString(1, name);
          break;
        } else {
          System.out
              .println(color.red + "\n( Note : Special & Numberical Characters not allowing.. ! )\n" + color.reset);
        }
      }

      // Taking phone no...
      while (true) {
        System.out.print("\t\tMobile No. -> ");
        String phone_no = in.next();
        String regex = "^\\d{10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phone_no);

        if (matcher.matches()) {
          statement.setString(2, phone_no);
          break;
        } else {
          System.out.println(color.yellow + " \n( Note : Invalid phone number try again ! )\n" + color.reset);
        }
      }

      // taking dob ...
      while (true) {
        System.out.print(color.yellow + "\t(Format [DD/MM/YYYY]) \n " + color.reset + "\t\tDate of Birth (DOB) -> ");
        String dob = in.next();
        String regex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(\\d{4})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(dob);

        if (matcher.matches()) {
          statement.setString(3, dob);
          break;
        } else {
          System.out
              .println(color.red + "\n( Note :Please enter Date of Birth in proper format ! )\n" + color.reset);
        }
      }

      // taking gender ...
      while (true) {
        System.out
            .print(color.yellow + "\t( M - Male | F - Female | T - other)  " + color.reset + " \n\t\tGender ->  ");
        String gender = in.next();
        gender = gender.toLowerCase();
        if (gender.equals("m") || gender.equals("male")) {
          statement.setString(4, "male");
          break;
        } else if (gender.equals("f") || gender.equals("female")) {
          statement.setString(4, "female");
          break;
        } else if (gender.equals("t") || gender.equals("trans") || gender.equals("o") || gender.equals("other")) {
          statement.setString(4, "other");
          break;
        } else {
          System.out.println(color.red + "\n( Note : Please enter proper gender ! )\n" + color.reset);
        }
      }

      // taking pin ... creating pin
      while (true) {
        System.out.println(color.yellow
            + "\t( Note : For your security, never share your PIN or confidential information with anyone.)"
            + color.reset);
        System.out.print(color.green + "\t\tCreate your UNIQUE PIN ( 4 Digits ) \n -> " + color.reset);
        String userPIN = in.next();
        String regex = "^\\d{4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userPIN);
        if (matcher.matches()) {
          // Hashing object created
          Hashing hash = new Hashing();
          String hashedPIN = hash.hashPassword(userPIN);
          statement.setString(5, hashedPIN);
          break;

        } else {
          System.out.println(color.red + "\n( Note : Please Follow the instruction properly ! )\n" + color.reset);
        }
      }

      // taking account type ....
      while (true) {
        System.out.print(
            color.yellow + "\t( C - Current Account / S - Saving Account ) " + color.reset + "\n\t\tAccount Type ->  ");
        String acc_type = in.next();
        acc_type = acc_type.toLowerCase();

        if (acc_type.equals("c") || acc_type.equals("current")) {
          statement.setString(6, "current");
          break;

        } else if (acc_type.equals("s") || acc_type.equals("saving")) {
          statement.setString(6, "saving");
          break;

        } else {
          System.out.println(color.yellow + "( Note : Please enter account type as specified ! )" + color.reset);
        }
      }

      // get generated key for (auto incremental account no)...
      int rowsAffected = statement.executeUpdate();

      if (rowsAffected > 0) {
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
          long accountNumber = generatedKeys.getLong(1); // The first column will have the auto-incremented ID
          System.out.println(color.green + "\n\tAccount created successfully.\n Account number: " + color.cyan
              + accountNumber + color.reset);
        }
      } else {
        System.out.println("Account creation failed.");
      }

      // asking for comfirmation ....
      while (true) {
        System.out.println("\n\t  </> Do you wants to create more accounts ? \n\t[ Y(Yes) / N(No)] -> " + color.reset);
        String asking = in.next();
        asking = asking.toLowerCase();

        if (asking.equals("y") || asking.equals("yes")) {
          CreateAccount obj = new CreateAccount();
          obj.createUser();
          break;
        } else if (asking.equals("n") || asking.equals("no")) {
          clean();
          Main home = new Main();
          home.Home();
          break;
        } else {
          System.out.println(color.red + "( Note : Please Select Yes or No only !)" + color.reset);
        }
      }
      statement.close();
      connection.close();
    } catch (SQLException e) {
      System.out.println(color.red + "Something Went Wrong!\n" + e.getMessage() + color.reset);
      e.printStackTrace();
    }
  }
}
