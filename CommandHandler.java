
import javax.security.sasl.SaslClient;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.List;


public class CommandHandler {
    private Path currentDir;

    public CommandHandler(){
        this.currentDir = Paths.get(System.getProperty("user.dir"));
    }

    public Path getCurrentDir() {
        return currentDir;
    }
    public void handleCommands(String command, String[] args){
         boolean overwriteToFile = false;
        File targetFile = null;
        // Check if the second-to-last argument is '>'
        if (args.length >= 2 && args[args.length - 2].equals(">")) {
            overwriteToFile = true;
            targetFile = new File(args[args.length - 1]);
            // Create the file if it does not exist
            try {
                if (targetFile.createNewFile()) {
                    System.out.println("File created: " + targetFile.getName());
                }
            } catch (IOException e) {
                System.out.println("Error creating file: " + e.getMessage());
                return;
            }
            // Remove the last two elements (operator and filename) from args
            args = java.util.Arrays.copyOf(args, args.length - 2);
        }
        // Save the original System.out stream to restore later
        PrintStream originalOutt = System.out;
        PrintStream fileOutt = null;
        
        if (overwriteToFile) {
            try {
                // Open the file to overwrite the file content
                fileOutt = new PrintStream(new FileOutputStream(targetFile, false)); // `false` for overwrite mode
                System.setOut(fileOutt);  // Redirect System.out to the file
            } catch (FileNotFoundException e) {
                System.out.println("Error: Cannot write to file " + targetFile.getName());
                return;
            }
        }

        
        //check if the second last argument is >> 
        if (args.length >= 2 && args[args.length - 2].equals(">>")) {
            appendToFile(command,args);
            return;
        }
      
       else if (command.equalsIgnoreCase("help")&&args.length==0)
            help();
        else if (command.equalsIgnoreCase("echo"))
            echo(args);
        else if (command.equalsIgnoreCase("cd")&& args.length==0)
            cd();
        else if (command.equalsIgnoreCase("cd")&& args.length>=1)
            cd(args[0]);
        else if (command.equalsIgnoreCase("rm")){
            if(args.length==0)
                System.out.println("Please specify a file name");
            rm(args); }
        else if (command.equalsIgnoreCase("mkdir")){
            if(args.length==0)
                System.out.println("Please Enter Directory Name to make");
            mkdir(args);}   
        else if (command.equalsIgnoreCase("pwd"))
            pwd();
        else if (command.equalsIgnoreCase("ls") && args.length > 0 && args[0].equalsIgnoreCase("-r"))
             ls_r(args); // Use the specified path or default to current directory

        else if (command.equalsIgnoreCase("ls") && args.length > 0 && args[0].equalsIgnoreCase("-a")) {
            ls_a(); // Implement this method to list all files, including hidden ones
        }
        else if (command.equalsIgnoreCase("ls"))
            ls(args);
        else if (command.equalsIgnoreCase("touch")) {
            if (args.length == 0) {
                System.out.println("Please specify a file name.");
            } else {
                touch(args);
            }
        }
       else if (command.equalsIgnoreCase("mv"))
                mv(args);
        else if (command.equalsIgnoreCase("rmdir")) {
            rmdir(args);
        } else if (command.equalsIgnoreCase("cat")) {
            cat(args);
        }else if (command.equalsIgnoreCase("grep")) { 
            grep(args);
        }
        else
            System.out.println(" Wrong Command. Type 'help' for the list of commands.");

          if (fileOutt != null) {
                fileOutt.close();
                System.setOut(originalOutt);
                System.out.println("Output written to " + targetFile.getAbsolutePath());
            }
        
      
    }
    public void appendToFile(String command, String[] args) {
    File appendFile = new File(args[args.length - 1]); // The file name is the last argument in args
    if (!appendFile.exists()) { 
        System.out.println("The specified file '" + appendFile.getName() + "' does not exist."); 
        return; 
    }
    // Remove ">>" and filename from args for the command execution
    String[] commandArgs = java.util.Arrays.copyOf(args, args.length - 2); 
    
    // Redirect the command output to a temporary stream for capturing
    PrintStream originalOut = System.out; // Save the original System.out to restore later
    ByteArrayOutputStream tmp = new ByteArrayOutputStream(); // Create a temporary byte stream to hold output
    PrintStream tempOut = new PrintStream(tmp); 
    
    System.setOut(tempOut); 
    
    try {
        // Execute the command and capture its output to tempOut
        handleCommands(command, commandArgs);
        tempOut.flush(); // Ensure all output is written to the ByteArrayOutputStream

        // Now append the captured output from tmp to the specified file
        try (PrintStream fileOut = new PrintStream(new FileOutputStream(appendFile, true))) { 
            fileOut.print(tmp.toString()); 
        } catch (FileNotFoundException e) {
            System.out.println("Error: Cannot write to file " + appendFile.getName());
        }
    } finally {

        System.setOut(originalOut); 
        System.out.println("Output appended to " + appendFile.getName()); 
        tempOut.close(); // Close the temporary PrintStream to free resources
    }
}
    public void help(){

        System.out.println("Available commands:");
        System.out.println("help   - Displays available commands");
        System.out.println("pwd    - Show current directory");
        System.out.println("cd     - Change directory");
        System.out.println("ls     - List files in current directory");
        System.out.println("ls -a  - List all files, including hidden ones");
        System.out.println("ls -r  - List files in reverse order");
        System.out.println("mkdir  - Create a directory");
        System.out.println("rmdir  - Remove a directory");
        System.out.println("touch  - Create a file");
        System.out.println("mv     - Move or rename a file");
        System.out.println("rm     - Remove a file");
        System.out.println("cat    - Display file contents");
        System.out.println("echo   - Echoes the input text");
        System.out.println("grep   - Search for a specific pattern in a file."); //ex: grep hello test1.txt -> output line with hello word if found in the file test1.txt
        System.out.println(">      - Redirects the output of the first command to be written to a file.");
        System.out.println(">>     - Redirects the output of the first command to be append to a file.");
        System.out.println("|      - Pipes output of one command to another command as input.");
        System.out.println("exit   - Exits the CLI");

    }
    public void grep(String[] args) {
        // Check if the correct number of arguments is provided
        if (args.length < 2) {
            System.out.println("Usage: grep <pattern> <file>");
            return;
        }

        // Trim the input arguments to avoid leading/trailing whitespace issues
        String pattern = args[0].trim();
        String fileName = args[1].trim();

        try {
            // Read all lines from the specified file
            List<String> lines = Files.readAllLines(Paths.get(fileName));
            boolean found = false; // Flag to check if any match is found

            // Iterate through each line in the file
            for (String line : lines) {
                // Check if the line contains the specified pattern
                if (line.contains(pattern)) {
                    System.out.println(line);
                    found = true; // Set flag to true if a match is found
                }
            }

            // If no lines were found, indicate that
            if (!found) {
                System.out.println("No lines found containing: " + pattern);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
    public void echo(String[] args) {
        if (args.length == 0) {
            System.out.println("No input provided for echo.");
        } else {
            System.out.println(String.join(" ", args));
        }
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
    public void rm(String[] arg){
        for(String path: arg){
            Path filePath = currentDir.resolve(path);
            System.out.println("Attempting to delete: " + filePath);
            try {
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    System.out.println("File deleted successfully: " + path);
                } else {
                    System.out.println("File not found : " + path);
                }
            } catch (IOException e) {
                System.out.println("Error deleting file: " + e.getMessage());
            }
        }
    }
    public void mkdir(String [] args){
        for (String path : args) {
            Path newDirPath = currentDir.resolve(path);
            File directory = newDirPath.toFile();
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

   public String pwd() {
       return ("Current working directory: " + currentDir);
    }
    public void ls(String[] args) {
        File dir;
        if (args.length == 0)
            dir = currentDir.toFile();
            // Store the directory/file in a File object
        else
            dir = new File(args[0]);
        // Check if the path exists
        if (!dir.exists()) {
            System.out.println("This directory doesn't exist.");
        } else if (!dir.isDirectory()) {
            System.out.println("This is not a directory.");
        } else if (!dir.canRead()) {
            System.out.println("this directory can't be read.");
        } else {
            // Get the list of files and directories
            String[] dirList = dir.list();

            // Check if the directory is empty
            if (dirList == null || dirList.length == 0) {
                System.out.println("This directory is empty.");
            } else {
                // Iterate through the list and print non-hidden files
                for (String file : dirList) {
                    File fileName = new File(dir, file);
                    if (!fileName.isHidden() && !fileName.getName().startsWith(".")) {
                        System.out.println(file);
                    }
                }
            }
        }
    }

  public void ls_r(String []args)//take specific arg or by default get the content of current directory
    {
        File Dir;

        if (args.length <= 1) {
            // Use current directory if no specific path is provided
            Dir = currentDir.toFile();
        } else {
            // Use the provided directory path
            Dir = new File(args[1]);
        }
        if (!Dir.exists()) {
            System.out.println("This directory does not exist");
        }
         else if(!Dir.isDirectory())
        {
            System.out.println("This argument is not a directory, please provide a valid directory");
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
    public void ls_a() {
        File dir = currentDir.toFile(); // get the current directory as a File object

        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("This directory does not exist or is not a directory.");
            return;
        }

        String[] files = dir.list(); // list all files in the directory
        if (files == null || files.length == 0) {
            System.out.println("The directory is empty.");
        } else {
            System.out.println("All files (including hidden):");
            for (String fileName : files) {
                File file = new File(dir, fileName);
                if (file.isHidden() || fileName.startsWith(".")) {
                    System.out.print("[Hidden] ");
                }
                System.out.println(fileName);
            }
        }
    }
    
    public void mv(String[] args) {   /*case1 multiple src to destination
                                        case2 src file /directory to destination
                                        case3 src file to destination file (rename) */
        if (args.length < 2) {
            System.out.println("Not enough arguments.please provide <source(s)> <destination>");
            return;
        }

        String destinationPath = args[args.length - 1];//take the last element in args to be destination
        File destination = new File(destinationPath);

        // Check if multiple sources are provided
        if (args.length > 2) {
            if (!destination.isDirectory()) {
                System.out.println("When moving multiple files, the destination must be a directory.");
                return;
            }
            if (!destination.exists()) {
                System.out.println("Destination directory does not exist.");
                return;
            }
            // Move each source file to the destination directory
            for (int i = 0; i < args.length - 1; i++) {
                File source = new File(args[i]);
                mvSingleFileToDirectory(source, destination);
            }
        } else {
            // Single source: rename or move the file or directory
            File source = new File(args[0]);
            if (destination.exists() && destination.isDirectory()) {
                moveOrRenameSingle(source, destination);
            } else {
                if (!destination.exists()) {
                    System.out.println("Destination does not exist.");
                    return;
                }
                // If the destination is not a directory, it's treated as a rename operation
                moveOrRenameSingle(source, destination);
            }
        }
    }
    // method to move or rename a single file or directory <file,file>
    private void moveOrRenameSingle(File source, File destination) {
        if (!source.exists()) {
            System.out.println("Source file or directory does not exist.");
            return;
        }

        // If the destination exists as a directory, move the source inside it
        if (destination.isDirectory()) {
            mvSingleFileToDirectory(source, destination);
        } else {
            // If destination is not a directory, rename or overwrite
            try {
                Files.move(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                //the old file will be replaced due to will be deleted and replaced with the file being moved.
                System.out.println("Moved/Renamed: " + source + " to " + destination); //rename src file to dest and save the content of srcfile and delete destination 
            } catch (IOException e) {
                System.out.println("Error: Unable to move or rename file " + source + " to " + destination + " - " + e.getMessage());
            }
        }
    }
    //  method to move a single file to a directory
    private void mvSingleFileToDirectory(File source, File destinationDir) {
        if (!source.exists()) {
            System.out.println("Source file or directory does not exist.");
            return;
        }

        File target = new File(destinationDir, source.getName());
        try {
            Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Moved: " + source + " to " + target);
        } catch (IOException e) {
            System.out.println("Error: Unable to move file " + source + " to " + target + " - " + e.getMessage());
        }
    }
    
    public void touch(String[] args) {
        if (args.length == 0) {
            System.out.println("Please specify a file name.");
            return;
        }
        for (String fileName : args) {
            Path filePath = currentDir.resolve(fileName);
            try {
                if (Files.exists(filePath)) {
                    // Update last modified time
                    Files.setLastModifiedTime(filePath, java.nio.file.attribute.FileTime.fromMillis(System.currentTimeMillis()));
                    System.out.println("File '" + fileName + "' timestamp updated.");
                } else {
                    // Create a new file
                    Files.createFile(filePath);
                    System.out.println("File '" + fileName + "' created.");
                }
            } catch (IOException e) {
                System.out.println("Error creating file: " + e.getMessage());
            }
        }
    }
    public void rmdir(String[] args){
        if (args.length == 0)
            System.out.println("Specify an empty directory to be removed.");
        else{
            for (String str: args){
                File dir = new File(str);
                if (!dir.exists())
                    System.out.println("Failed to remove "+str+" :Directory doesn't exist.");
                else if (!dir.isDirectory()) {
                    System.out.println("Failed to remove "+str+" :Not a directory.");
                } else{
                    String[] dirList = dir.list();
                    if (dirList != null && dirList.length > 0) {
                        System.out.println("Failed to remove '" + str + ": Directory not empty");
                        return;
                    }

                    // Attempt to remove the directory
                    if (dir.delete()) {
                        System.out.println("Directory '" + str + ": removed successfully.");
                    } else {
                        System.out.println("Failed to remove '" + str + ": Permission denied");
                    }
                }
            }
        }
    }
    public void cat(String[] args){
        if (args.length == 0){
            System.out.println("Enter the text you want to display.");
            Scanner input = new Scanner(System.in);
            String scannedInput = input.nextLine();
            System.out.println(scannedInput);
        }
        else{
            for (String fileName: args){
                File file = new File(fileName);
                if (!file.exists()){
                    System.out.println("Failed to concatenate "+fileName+": File doesn't exist.");
                }
                else if (!file.canRead()){
                    System.out.println("Failed to concatenate "+fileName+": Permission denied.");
                } else if (!file.isFile()) {
                    System.out.println("Failed to concatenate "+fileName+": is not a file.");
                } else if (file.length() == 0){
                    System.out.println("Failed to concatenate "+fileName+": File is empty.");
                }
                try{
                    Scanner fileReader = new Scanner(file);
                    while (fileReader.hasNextLine()){
                        String line = fileReader.nextLine();
                        System.out.println(line);
                    }
                    fileReader.close();
                } catch (FileNotFoundException e) {
                    System.out.println("Failed to concatenate "+fileName+": An error occurred.");
                }
            }
        }
    }
}
