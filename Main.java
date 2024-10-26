import java.util.*;

public class Main {
  ColorSet color = new ColorSet();
  Scanner in = new Scanner(System.in);

  public void Home() {
 
    System.out.println("\n\n\t\t\t *** || ATM Simulation System || ***\n");
    while (true) {
      System.out.println(color.green + "\n\tSelect Options From Here :" + color.reset);
      System.out
          .print("\t 1. Create New Account  \n\t 2. Update Existing Account \n\t 3. Delete Existing Account \n\t 4. Account detailes \n\t 5. ATM \n\t 6. Exit Application \n\t Options -> " + color.reset);
      int opt = in.nextInt();
      switch (opt) {
        case 1:
          CreateAccount createAccount = new CreateAccount();
          createAccount.createUser();
          break;
        case 2:
          UpdateAccount updateAccount = new UpdateAccount();
          updateAccount.updateUser();
          break;
        case 3:
          CloseAccount closeAccount = new CloseAccount();
          closeAccount.deleteAccount();
          break;
        case 4:
        FetchAllData fetchdata =  new FetchAllData();
        fetchdata.verifyPin();
        break;
        case 5:
          Atm Atm = new Atm();
          Atm.atm();
          break;
        case 6:
          System.out.println(" Exiting ...");
          System.exit(0);

        default:
          Home();
          break;
      }
    }
  }

  public static void main(String[] args) {
    Main Start = new Main();
    Start.Home();
  }
}
