public class CommandHandler {
    public CommandHandler(){}
    public void handleCommands(String command, String[] args){
        if (command.equalsIgnoreCase("help"))
            help();
        else if (command.equalsIgnoreCase("exit"))
            exit();
    }
    public void help(){
        System.out.println("Available commands:");
        System.out.println("help   - Displays available commands");
        System.out.println("echo   - Echoes the input text");
        System.out.println("exit   - Exits the CLI");
    }
    public void exit(){}
}
