import java.util.Scanner;

public class CLI {
    private CommandHandler commandHandler;

    public CLI() {
        commandHandler = new CommandHandler();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Command Line Interpreter! Type 'help' to see available commands.");

        while (true) {
//            System.out.print("> ");
            System.out.print(commandHandler.getCurrentDir());
            System.out.print("> ");
            String input = scanner.nextLine();

            //handle exit case
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting!");
                break;
            }
            //parse input to get command and arguments
            Parser commandParser = new Parser();
            commandParser.parse(input);

            //process and execute the command
            commandHandler.handleCommands(commandParser.getCommandName(), commandParser.getArgs());
        }
        scanner.close();
    }
}
