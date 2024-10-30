package org.example;

import javax.security.sasl.SaslClient;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.util.Scanner;

public class CommandHandler {
    private Path currentDir;

    public CommandHandler(){
        this.currentDir = Paths.get(System.getProperty("user.dir"));
    }

    public Path getCurrentDir() {
        return currentDir;
    }
    public void handleCommands(String command, String[] args){
        if (command.equalsIgnoreCase("help")&&args.length==0)
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
        else if (args.length>0 && (args[0].equals(">"))){
            write(args,command);

        }
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
        else if (command.equalsIgnoreCase("mv")&& args.length>=2)
            try {
                mv(args[0], args[1]);
            } catch (IOException e) {
                System.out.println("Error moving file: " + e.getMessage());
            }
        else if (command.equalsIgnoreCase("rmdir")) {
            rmdir(args);
        } else if (command.equalsIgnoreCase("cat")) {
            cat(args);
        }
        else
            System.out.println(" Wrong Command. Type 'help' for the list of commands.");
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
        System.out.println(">      - Redirects the output of the first command to be written to a file.");
        System.out.println(">>     - Redirects the output of the first command to be append to a file.");
        System.out.println("exit   - Exits the CLI");

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

    public void write(String[] args,String command){
        Path filePath = currentDir.resolve(args[1]); //  file path
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out; // Save original System.out
        System.setOut(new PrintStream(buffer)); // Redirect System.out to buffer
        
        if (command.equals("ls")) {
            ls(new String[0]);  }
        else if (command.equals("help")){
            help();
        }    
        else if(command.equals("pwd")){
            System.out.print(pwd());
        }

        // Restore original System.out to console to continue the program
        System.setOut(originalOut);

        // Write captured output to the specified file
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write(buffer.toString());
            System.out.println("Output written to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
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
