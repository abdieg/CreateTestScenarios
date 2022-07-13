import java.util.Scanner;

public class MainMenu {
    public static String current_dir;

    public static void main(String[] args) {
        int args_size = args.length;
        String graphml_source = null;
        current_dir = System.getProperty("user.dir");

        if (args_size == 0) {
            MainMenu console = new MainMenu();
            console = MainMenu.mainMenu(console);
        }
        else if (args_size == 1) {
            System.out.println("Parameter: " + args[0]);
            graphml_source = args[0];
            GenerateScenarios.generateScenariosInConsole(graphml_source);
        }
        else if (args_size == 2) {
            System.out.println("Parameter 0: " + args[0]);
            System.out.println("Parameter 1: " + args[1]);
            graphml_source = args[0];
            GenerateScenarios.generateScenariosInConsole(graphml_source, Integer.valueOf(args[1]));
        }
        else {
            System.out.println("ERROR! THIS PROGRAM REQUIRES SPECIFIC PARAMETERS. PERHAPS YOUR ABSOLUTE FILEPATH IS NOT IN QUOTES");
            System.out.println("Example: \"C:\\Users\\file.graphml\"");
        }
    }

    public static MainMenu mainMenu(MainMenu console) {
        Scanner sc = new Scanner(System.in);
        String choice;
    }
}
