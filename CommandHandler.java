import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

public class CommandHandler {
    private Path currentDir; 
    
    public CommandHandler(){
        this.currentDir = Paths.get(System.getProperty("user.dir"));
    }

    public Path getCurrentDir() {
        return currentDir;
    }
    public void handleCommands(String command, String[] args){
        if (command.equalsIgnoreCase("help"))
            help();
        else if (command.equalsIgnoreCase("cd")&& args.length==0)
            cd();
        else if (command.equalsIgnoreCase("cd")&& args.length>=1)
            cd(args[0]);
        else if (command.equalsIgnoreCase("exit"))
            exit();
    }
    public void help(){
        System.out.println("Available commands:");
        System.out.println("help   - Displays available commands");
        System.out.println("echo   - Echoes the input text");
        System.out.println("cd   - Used to change the current directory of the terminal");
        System.out.println("exit   - Exits the CLI");
    }
    public void cd(){                      // go to the home directory
        currentDir = Paths.get(System.getProperty("user.home"));
    }
    public void cd(String arg){        // if args (..) means change current path to previous path 
        if (arg.equals("..")){
        Path previousPath = currentDir.getParent();
        if(previousPath!=null)
            currentDir=previousPath;
        else 
                System.out.println("No previous paths available!");
            
    }
        else {     // argument is a path full or a short one (full or relative)
            Path targetPath = Paths.get(arg);
            if (!targetPath.isAbsolute()) {   // convert the target path if it was relative to absolute same structure as currentDir
                targetPath = currentDir.resolve(targetPath);
            }
            if (Files.exists(targetPath) && Files.isDirectory(targetPath)) {   // Check if the target path exists and is a directory
                currentDir = targetPath;
            } else {
                System.out.println("Directory does not exist: " + targetPath.toString());
            }
        }
    }
    
    public void exit(){}
}
