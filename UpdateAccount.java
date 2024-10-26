import java.sql.*;
import java.util.*;
import java.util.regex.*;

public class UpdateAccount {
  public void clean() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
}

  Scanner in = new Scanner(System.in);
  ColorSet color = new ColorSet();

  // update name ...
  public void updateName(Long accountNo, Connection connection) {
    try {
      String query = "update userAccount set name = ? where account_no = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      // System.out.print("\t\tEnter New Name -> ");
      // statement.setString(1, in.nextLine());
      // statement.setLong(2, accountNo);
      while (true) {
        
        System.out.print("\t\tEnter New Name ->  ");
        String name = in.nextLine();
        String regex = "^[A-Za-z\\s']+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) {
          statement.setString(1, name);
          statement.setLong(2, accountNo);
          break;
        } else {
          System.out
              .println(color.red + "\n( Note : Special & Numberical Characters not allowing.. ! )\n" + color.reset);
        }
      }
      statement.execute();
      System.out.println(color.green + "Account Holder Name Updated.." + color.reset);
      // statement.close();
      // connection.close();
    } catch (Exception e) {
      System.out.println(
          color.red + "Something went wrong while updating name ! Please Try Again...." + e.getMessage() + color.reset);
    }
  }

  // update phone_no
  public void updatePhone_no(Long accountNo, Connection connection) {
    try {
      String query = "update userAccount set phone = ? where account_no = ?";
      PreparedStatement statement = connection.prepareStatement(query);

      while (true) {
        
        System.out.print("\t\tEnter New Mobile No -> ");
        String phone_no = in.next();
        String regex = "^\\d{10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phone_no);

        if (matcher.matches()) {
          statement.setString(1, phone_no);
          statement.setLong(2, accountNo);
          break;
        } else {
          System.out.println(color.red + " \n( Note : Invalid phone number try again ! )\n" + color.reset);
        }
      }

      statement.execute();
      System.out.println(color.green + " User Mobile No. Updated.." + color.reset);
      // statement.close();
      // connection.close();
    } catch (Exception e) {
      System.out.println(color.red + "Something went wrong while updating phone ! Please Try Again...." + e.getMessage()
          + color.reset);
    }
  }
  // -----------------------------------------------------------------------------------------------------------------

  // update dob
  public void updateDOB(Long accountNo, Connection connection) {
    try {
      String query = "update userAccount set dob =str_to_date(?,'%d/%m/%Y') where account_no = ?";
      PreparedStatement statement = connection.prepareStatement(query);

      while (true) {
       
        System.out
            .print(
                color.yellow + "\t(Format [DD/MM/YYYY]) \n " + color.reset + "\t\tEnter New Date of Birth (DOB) -> ");
        String dob = in.next();
        String regex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(\\d{4})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(dob);

        if (matcher.matches()) {
          statement.setString(1, dob);
          statement.setLong(2, accountNo);

          break;
        } else {
          System.out.println(color.red + "\n( Note :Please enter Date of Birth in proper format ! )\n" + color.reset);
        }
      }

      statement.execute();
      System.out.println(color.green + "User Date of Birth Updated.." + color.reset);

    } catch (Exception e) {
      System.out.println(
          color.red + "Something went wrong while updating name ! Please Try Again...." + e.getMessage() + color.reset);
    }
  }
  // -----------------------------------------------------------------------------------------------------------------

  // update pin
  public void updatePin(Long accountNo, Connection connection) {
    try {
      String query = "update userAccount set pin = ? where account_no = ?";
      PreparedStatement statement = connection.prepareStatement(query);

      while (true) {
       
        System.out.println(color.cyan
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
          statement.setString(1, hashedPIN);
          statement.setLong(2, accountNo);
          break;

        } else {
          System.out.println(color.red + "\n( Note : Please Follow the instruction properly ! )\n" + color.reset);
        }
      }

      statement.execute();
      System.out.println(color.green + "Account Pin Updated.." + color.reset);
      // statement.close();
      // connection.close();
    } catch (Exception e) {
      System.out.println(
          color.red + "Something went wrong while updating name ! Please Try Again...." + e.getMessage() + color.reset);
    }
  }
  // -----------------------------------------------------------------------------------------------------------------

  // update account_type
  public void updateAcc_Type(Long accountNo, Connection connection) {
    try {
      String query = "update userAccount set acc_type = ? where account_no = ?";
      PreparedStatement statement = connection.prepareStatement(query);

      while (true) {
        
        System.out.print(color.yellow + "\t( C - Current Account / S - Saving Account ) " + color.reset
            + "\n\t\tEnter New Account Type ->  ");
        String acc_type = in.next();
        acc_type = acc_type.toLowerCase();

        if (acc_type.equals("c") || acc_type.equals("current")) {
          statement.setString(1, "current");
          statement.setLong(2, accountNo);
          break;

        } else if (acc_type.equals("s") || acc_type.equals("saving")) {
          statement.setString(1, "saving");
          statement.setLong(2, accountNo);
          break;

        } else {
          System.out.println(color.red + "( Note : Please enter account type as specified ! )" + color.reset);
        }
      }

      statement.execute();
      System.out.println(color.green + "Account Type Updated" + color.reset);
      // statement.close();
      // connection.close();
    } catch (Exception e) {
      System.out.println(
          color.red + "Something went wrong while updating name ! Please Try Again...." + e.getMessage() + color.reset);
    }
  }
  // ---------------------------------------------------------------------------------------------------------------

  public void updateUser() {
    // name ,phone , dob , pin , acc_type .... updating

    try {
      clean();
      Connection connection = DriverManager.getConnection(DbConfig.JDBC_URL, DbConfig.USERNAME, DbConfig.PASSWORD);
      String query = "select account_no, name , pin from userAccount where account_no=? and pin=? ";
      PreparedStatement statement = connection.prepareStatement(query);
      System.out.println("\n\t\t*** | Account Details | *** ");

      // for checking account_no ...
      while (true) {
      
        System.out.print("\n\tEnter your Account No  ->");
        String userAccountNo = in.next();
        String regex = "^\\d{12}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userAccountNo);

        if (matcher.matches()) {
          statement.setString(1, userAccountNo);
          break;

        } else {
          System.out
              .println(color.red + "\n( Note : Account Number should must contain 12 digits only ! )\n" + color.reset);
        }
      }

      // for checking pin ....
      while (true) {
        System.out.print("\n\tEnter your Unique Pin  ->  ");
        String userPin = in.next();

        String regex = "^\\d{4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userPin);
        if (matcher.matches()) {
          // Hashing object created
          Hashing hash = new Hashing();
          String hashedPIN = hash.hashPassword(userPin);
          statement.setString(2, hashedPIN);
          break;

        } else {
          System.out.println(color.red + "\n(Note : Account Pin should must contain 4 digits only ! )\n" + color.reset);
        }
      }

      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
         clean();
        long account_no = resultSet.getLong(1);
        System.out.println(color.cyan + "\n\n Welcome , Dear " + resultSet.getString(2) + " ! " + color.reset);
        while (true) {
           
          System.out.println(color.cyan + "\n\t  * | Update Details --" + color.reset);
          System.out.print(color.purple
              + "\n\t1. Update Account Holder Name \n\t2. Update Mobile No \n\t3. Update DOB \n\t4. Update Pin \n\t5. Update Account Type \n\t6. <- Back To Home  \n Option ->"
              + color.reset);
          int opt = in.nextInt();
          // creating object for UpdateAccount Class;
          UpdateAccount updateAccount = new UpdateAccount();
          switch (opt) {

            case 1:
              updateAccount.updateName(account_no, connection);
              break;

            case 2:
              updateAccount.updatePhone_no(account_no, connection);
              break;

            case 3:
              updateAccount.updateDOB(account_no, connection);
              break;

            case 4:
              updateAccount.updatePin(account_no, connection);
              break;

            case 5:
              updateAccount.updateAcc_Type(account_no, connection);
              break;

            case 6:
            clean();
              Main home = new Main();
              home.Home();
              
              break;

            default:
              System.out.println(color.red + " Please select valid options !\n\n" + color.reset);

          }
        }
      } else {
        System.out.println(color.red + " \t\t Account Not Found or Pin is Incorrect !" + color.reset);
        Main home = new Main();
        home.Home();
      }

      statement.close();
      connection.close();

    } catch (Exception e) {
      System.out.println("Something went wrong ! Please Try Again...." + e.getMessage());
    }

  }
}
