import java.util.Scanner;
import jdk.javadoc.internal.doclets.formats.html.SourceToHTMLConverter;

public class Client {
    public RemoteInterface remoteInterface;

    public Client() {

    }

    public static void main(String[] args) {

        Scanner reader = new Scanner(System.in); // Create a Scanner object

        

        Registry rmiRegistry = getRegistry("localhost");
        RemoteInterface stub = (RemoteInterface) rmiRegistry.lookup(remote_object_name);

        
        boolean leave = false;
        while (!leave) {

            // Print interface
            System.out.println("-------------------------Welcome to the P2P interface-------------------------");
            System.out.println("                                                                              ");
            System.out.println("   What do you want to do?                                                    ");
            System.out.println("       1.Backup a file                                                        ");
            System.out.println("       2.Restore a file                                                       ");
            System.out.println("       3.Delete a file                                                        ");
            System.out.println("       4.Manage local service storage                                         ");
            System.out.println("       5.Retrive local service state information                              ");
            System.out.println("                                                                              ");
            System.out.println("       0.Leave                                                                ");
            System.out.println("                                                                              ");
            System.out.println("------------------------------------------------------------------------------");

            // Get option
            int option = reader.nextLine(); // Read user input

            // Proceed to option
            switch (option) {
            case 1:
                backup();
                break;
            case 2:
                restore();
                break;
            case 3:
                delete();
                break;
            case 4:
                manage();
                break;
            case 5:
                state();
                break;
            case 0:
                leave = true;
            }
        }

    }

    public String backup(){
        Scanner reader = new Scanner(System.in); // Create a Scanner object

        System.out.println("                                                                              ");
        System.out.println("                                                                              ");
        System.out.println("-------------------------------Backing up a file------------------------------");
        System.out.println("   What is the file's path name?");
        String pathname = reader.nextLine();  // Read user input
        String replicationDegree = reader.nextLine();  // Read user input






        backup()
        return 
    }
}
