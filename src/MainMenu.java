import java.util.*;

public class MainMenu {
    public static String current_dir;

    public static void main(String[] args) {
        int args_size = args.length;
        String graphml_source;
        current_dir = System.getProperty("user.dir");

        if (args_size == 0) {
            MainMenu console = new MainMenu();
            console = MainMenu.mainMenu(console);
        } else if (args_size == 1) {
            System.out.println("Parameter: " + args[0]);
            graphml_source = args[0];
            GenerateScenarios.generateScenariosInConsole(graphml_source);
        } else if (args_size == 2) {
            System.out.println("Parameter 0: " + args[0]);
            System.out.println("Parameter 1: " + args[1]);
            graphml_source = args[0];
            GenerateScenarios.generateScenariosInConsole(graphml_source, Integer.parseInt(args[1]));
        } else {
            System.out.println("ERROR! THIS PROGRAM REQUIRES SPECIFIC PARAMETERS. PERHAPS YOUR ABSOLUTE FILEPATH IS NOT IN QUOTES");
            System.out.println("Example: \"C:\\Users\\file.graphml\"");
        }
    }

    public static MainMenu mainMenu(MainMenu console) {
        Scanner sc = new Scanner(System.in);
        String choice;

        do {
            System.out.println("-------------------------------------------- MENU --------------------------------------------");
            System.out.println("GRAPHML test cases converter");
            System.out.println("https://www.yworks.com/yed-live");
            System.out.println("Extracts all possible paths to create test case scenarios");
            System.out.println("----------------------------------------------------------------------------------------------");
            System.out.println("1. Generate all test cases for all GRAPHML files in this folder");
            System.out.println("2. Generate all test scenarios for a specific GRAPHML file in this folder");
            System.out.println("3. Generate test scenarios for a specific GRAPHML file in this folder providing the final node");
            System.out.println("X. Exit program");
            System.out.println("----------------------------------------------------------------------------------------------");
            System.out.print("Selection>> ");

            choice = sc.next();

            if (choice.equalsIgnoreCase("1"))
                return MainMenu.subMenu1(console);
            else if (choice.equalsIgnoreCase("2"))
                return MainMenu.subMenu2(console);
            else if (choice.equalsIgnoreCase("3"))
                return MainMenu.subMenu3(console);
            else if (choice.equalsIgnoreCase("x"))
                return console;
            else {
                System.out.println("\nINVALID SELECTION. TRY AGAIN\n");
                return MainMenu.mainMenu(console);
            }
        } while (choice.equalsIgnoreCase("x"));
    }

    public static MainMenu subMenu1(MainMenu console) {
        Scanner sc = new Scanner(System.in);
        String choice;

        do {
            System.out.println("------------------------------------------ SUB MENU ------------------------------------------");
            System.out.println("Generate all test cases for all GRAPHML files in this folder?");
            System.out.println("Current folder: " + current_dir);
            System.out.println("----------------------------------------------------------------------------------------------");
            System.out.println("1. Yes");
            System.out.println("2. No");
            System.out.println("X. Exit program");
            System.out.println("----------------------------------------------------------------------------------------------");
            System.out.print("Selection>> ");

            choice = sc.next();

            if (choice.equalsIgnoreCase("1")) {
                GenerateScenarios.generateScenariosInTextFile(current_dir);
                System.out.println("\n>>>>PROCESS COMPLETE!!!\n");
                return MainMenu.mainMenu(console);
            } else if (choice.equalsIgnoreCase("2"))
                return MainMenu.mainMenu(console);
            else if (choice.equalsIgnoreCase("x"))
                return console;
            else {
                System.out.println("\nINVALID SELECTION. TRY AGAIN\n");
                return MainMenu.subMenu1(console);
            }
        } while (choice.equalsIgnoreCase("x"));
    }

    public static MainMenu subMenu2(MainMenu console) {
        Scanner sc = new Scanner(System.in);
        String choice;

        do {
            System.out.println("------------------------------------------ SUB MENU ------------------------------------------");
            System.out.println("Generate all test scenarios for a specific GRAPHML file in this folder");
            System.out.println("Current folder: " + current_dir);
            System.out.println("Pick a file to begin");
            System.out.println("----------------------------------------------------------------------------------------------");
            Map<Integer, String> number_file = new HashMap<Integer, String>();
            number_file = GenerateScenarios.showGraphmlFilesInCurrentFolder(current_dir);
            for (int i = 0; i < number_file.size(); i++) {
                System.out.println(i + ". " + number_file.get(i));
            }
            System.out.println("B. Back");
            System.out.println("X. Exit program");
            System.out.println("----------------------------------------------------------------------------------------------");
            System.out.print("Selection>> ");

            choice = sc.next();

            if (choice.equalsIgnoreCase("B"))
                return MainMenu.mainMenu(console);
            else if (choice.equalsIgnoreCase("x"))
                return console;
            else {
                String mapValue = number_file.get(Integer.valueOf(choice));
                if (mapValue != null) {
                    GenerateScenarios.generateScenariosInTextFile(current_dir, mapValue);
                    System.out.println("\n>>>>PROCESS COMPLETE!!!\n");
                    return MainMenu.mainMenu(console);
                } else {
                    System.out.println("\nINVALID SELECTION. TRY AGAIN\n");
                    return MainMenu.subMenu2(console);
                }
            }
        } while (choice.equalsIgnoreCase("x"));
    }

    public static MainMenu subMenu3(MainMenu console) {
        Scanner sc = new Scanner(System.in);
        String choice;

        do {
            System.out.println("------------------------------------------ SUB MENU ------------------------------------------");
            System.out.println("Generate test scenarios for a specific GRAPHML file in this folder providing final node");
            System.out.println("Current folder: " + current_dir);
            System.out.println("Pick a file to begin");
            System.out.println("----------------------------------------------------------------------------------------------");
            Map<Integer, String> number_file = new HashMap<Integer, String>();
            number_file = GenerateScenarios.showGraphmlFilesInCurrentFolder(current_dir);
            for (int i = 0; i < number_file.size(); i++) {
                System.out.println(i + ". " + number_file.get(i));
            }
            System.out.println("B. Back");
            System.out.println("X. Exit program");
            System.out.println("----------------------------------------------------------------------------------------------");
            System.out.print("Selection>> ");

            choice = sc.next();

            if (choice.equalsIgnoreCase("B"))
                return MainMenu.mainMenu(console);
            else if (choice.equalsIgnoreCase("x"))
                return console;
            else {
                String mapValue = number_file.get(Integer.valueOf(choice));
                if (mapValue != null) {
                    return MainMenu.subSubMenu3(console, number_file.get(Integer.valueOf(choice)).toString());
                } else {
                    System.out.println("\nINVALID SELECTION. TRY AGAIN\n");
                    return MainMenu.subMenu3(console);
                }
            }
        } while (choice.equalsIgnoreCase("x"));
    }

    public static MainMenu subSubMenu3(MainMenu console, String name) {
        Scanner sc = new Scanner(System.in);
        String choice;

        do {
            System.out.println("---------------------------------------- SUB SUB MENU ----------------------------------------");
            System.out.println("GRAPHML file: " + name);
            System.out.println("The GRAPHML file selected has the following ending nodes. Pick one final node");
            System.out.println("----------------------------------------------------------------------------------------------");
            Map<Integer, String> temp_fn = new HashMap<Integer, String>(GenerateScenarios.getFinalNodes(current_dir + "/" + name));
            List<Integer> menu_temp_fn_key = new ArrayList<Integer>();
            List<String> menu_temp_fn_value = new ArrayList<String>();
            for (int key : temp_fn.keySet()) {
                menu_temp_fn_key.add(key);
            }
            for (String val : temp_fn.values()) {
                menu_temp_fn_value.add(val);
            }
            for (int i = 0; i < menu_temp_fn_key.size(); i++) {
                System.out.println(i + ". " + "Test outcome = " + menu_temp_fn_value.get(i) + " ... {Key = " + menu_temp_fn_key.get(i) + "}");
            }
            System.out.println("B. Back");
            System.out.println("X. Exit program");
            System.out.println("----------------------------------------------------------------------------------------------");
            System.out.print("Selection>> ");

            choice = sc.next();

            if (choice.equalsIgnoreCase("B"))
                return MainMenu.subMenu3(console);
            else if (choice.equalsIgnoreCase("x"))
                return console;
            else {
                try {
                    int keySelected = menu_temp_fn_key.get(Integer.parseInt(choice));
                    GenerateScenarios.generateScenariosInTextFile(current_dir, name, keySelected);
                    System.out.println("\n>>>>PROCESS COMPLETE!!!\n");
                    return MainMenu.subSubMenu3(console, name);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("\nINVALID SELECTION! TRY AGAIN\n");
                    return MainMenu.subSubMenu3(console, name);
                }
            }
        } while (choice.equalsIgnoreCase("x"));
    }
}