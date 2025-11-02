import java.util.ArrayList;
import java.util.Scanner;

public class Sistem {
    private ArrayList<Ticket> tickets = new ArrayList<>();
    private Auth_Service auth = new Auth_Service();
    private Scanner sc = new Scanner(System.in);

    public void Start(){
        int choice;
        while(choice !=6){
            System.out.println("\\n=== Transport ticket sistem ===");
            System.out.println(auth.orLogin() ? "Logged in: " + auth.nowLogged() : "Not logged in");
            System.out.println("1. Registration");
            System.out.println("2. Log in");
            System.out.println("3. Buy ticket");
            System.out.println("4. View tickets");
            System.out.println("5. Log out");
            System.out.println("6. Exit");
            System.out.println("Select an option: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch(choice){
                case 1:
                    registration();
                break;
                case 2:
                    login();
                break;
                case 3:
                    buyticket();
                break;
                case 4:
                    viewtickets();
                break;
                case 5:
                    logout();
                break;
                case 6:
                    System.out.println("Bye!");
                break;
                default:
                    System.out.println("Wrong choice! Try again.");
                break;
            }

            private void registration(){

            }

        }
    }

}
