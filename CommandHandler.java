import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.io.IOException;
import java.io.File;

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
        else if (command.equalsIgnoreCase("rm"))
            rm(args[0]); 
        else if (command.equalsIgnoreCase("mkdir"))
           mkdir(args);     
        else if (command.equalsIgnoreCase("pwd"))
            pwd(); 
        else if (command.equalsIgnoreCase("ls") && args.length > 0 && args[0].equalsIgnoreCase("-r"))
            if(args.length > 1)
            {
                ls_r(args[1]);
            }else ls_r(System.getProperty("user.dir")); // Use the specified path or default to current directory
        else if (command.equalsIgnoreCase("mv")&& args.length>=2)
        try {
            mv(args[0], args[1]);
        } catch (IOException e) {
            System.out.println("Error moving file: " + e.getMessage());
        }
        else if (command.equalsIgnoreCase("exit"))
            exit();
        else
            System.out.println(" Wrong Command. Type 'help' for the list of commands.");
    }
    public void help(){
        System.out.println("Available commands:");
        System.out.println("help   - Displays available commands");
        System.out.println("echo   - Echoes the input text");
        System.out.println("cd     - Used to change the current directory of the terminal");
        System.out.println("rm     - Removes each given file");
        System.out.println("mkdir   - Creates a directory with each given name ");
        System.out.println("pwd    - Print the working directory");
        System.out.println("ls -r  -Lists the contents (files and directories) of the current directory in reverse order");
        System.out.println("mv     -Moves one or more files/directories to a directory or rename files/directories. ");
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
    public void rm(String arg){
            Path filePath = currentDir.resolve(arg);
            System.out.println("Attempting to delete: " + filePath);
            try {
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    System.out.println("File deleted successfully: " + arg);
                } else {
                    System.out.println("File not found : " + arg);
                }
            } catch (IOException e) {
                System.out.println("Error deleting file: " + e.getMessage());
            }
    }
        public void mkdir(String [] args){
        for (String path : args) {
            File directory = new File(path);
            if (!directory.exists()) {
                boolean success = directory.mkdirs();
                if (success) {
                    System.out.println("Directory created successfully at " + directory.getAbsolutePath());
                } else {
                    System.err.println("Failed to create directory");
                }
            } else {
                System.err.println("Directory already exists");
            }
        }
}
    public void pwd()
    {
        System.out.println("Current working directory: " + currentDir);
    }
    public void ls_r(String arg)
    {
        File Dir = new File(arg);//store current directory in file
        if (!Dir.exists()) {
            System.out.println("This directory does not exist");
        }
        else{
            String [] arr = Dir.list(); //list content of directory that store in file
            if (arr != null && arr.length > 0) {
                int n= arr.length;
                System.out.println("Directory contents in reverse order:");
                for(int i=n-1 ;i>=0 ;i--) //list the content in reverse order
                {
                    System.out.println(arr[i]);
                }
            }
            else {
                System.out.println("The directory is empty ");
            }
 
        }
            
    }
    public File makeAbsolute(String path) {
        Path filePath = Paths.get(path);
        if (!filePath.isAbsolute()) {
            filePath = currentDir.resolve(filePath);
        }
        return filePath.toFile();
    }
    public void mv(String source,String destination) throws IOException
    {
        File src = makeAbsolute(source);
        File Dst = makeAbsolute(destination);
        if(!src.exists())
        {
            System.out.println("This source directory does not exist");
        }
        if (Dst.isFile()) { //in this case we will rename src with destination
           System.out.println("Can't move into file.");
        }
            if (Dst.exists() && Dst.isDirectory()) {// Move the source to the destination directory
            Files.move(src.toPath(), Dst.toPath().resolve(src.getName()), StandardCopyOption.REPLACE_EXISTING);//If src.txt already exists in the destination, it will be replaced due to will be deleted and replaced with the file being moved.
        }
        else { // Destination does not exist, treat it as a rename
            Files.move(src.toPath(), Dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
    
    public void exit(){}
}
