package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

public class test {
    private CommandHandler commandHandler;
    private Path initialDir;
private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private File tempDir;
    private File sourceFile;
    private File destinationDir;
    private File destinationFile;
    @BeforeEach
    void setUp()  throws IOException {
        commandHandler = new CommandHandler();
        initialDir = commandHandler.getCurrentDir();
        System.setOut(new PrintStream(outputStreamCaptor)); // Capture console output
        tempDir = Files.createTempDirectory("testDir").toFile();// Create a temporary directory for testing
        tempDir.deleteOnExit(); // Ensure it gets deleted after tests
        sourceFile = new File(tempDir, "source.txt");
        Files.write(sourceFile.toPath(), "This is a test file.".getBytes());
        destinationDir = new File(tempDir, "destinationDir");// Create destination directory
        destinationDir.mkdir();
        destinationFile = new File(tempDir, "destination.txt");
    }
    @AfterEach
    public void cleanAfterTest() {
        // Cleanup the temporary files and directories after tests
        if (sourceFile.exists()) sourceFile.delete();
        if (destinationDir.exists()) destinationDir.delete();
        if (destinationFile.exists()) destinationFile.delete();
        if (tempDir.exists()) tempDir.delete();
    }
    @Test
    void testCdToHomeDirectory() {
        commandHandler.cd();                  // No arguments, should go to the home directory
        Path homeDir = Paths.get(System.getProperty("user.home"));
        assertEquals(homeDir, commandHandler.getCurrentDir(), "cd with no arguments should change to the home directory.");
    }
    @Test
    void testCdTopreviousDirectory() {
        commandHandler.cd("..");
        Path parentDir = initialDir.getParent();
        assertEquals(parentDir, commandHandler.getCurrentDir(), "cd .. should change to the previous directory.");
    }
    @Test
    void testCdToSpecificPath() {
        Path targetPath = initialDir.resolve("targetsrc");
        if (Files.exists(targetPath)) {                // Make sure the target exists before testing
            commandHandler.cd("targetsrc");
            assertEquals(targetPath, commandHandler.getCurrentDir(), "cd to a specific path should change to that path.");
        } else {
            System.out.println("Directory 'targetsrc' does not exist in the current path.");
        }
    }

    @Test
    void testRmSingleFile() throws IOException{
        // Create a temporary file in the current directory
        Path tempFile = Files.createTempFile(commandHandler.getCurrentDir(), "testFile", ".txt");
        assertTrue(Files.exists(tempFile), "Temporary file should exist before deletion");

        //Delete file using rm command
        commandHandler.rm(new String[]{tempFile.getFileName().toString()});

        // Check that file no longer exists
        assertFalse(Files.exists(tempFile), "Temporary file should be deleted");
    }

    @Test
    void testRmMultipleFiles() throws IOException { // Try on many files Ex 2 files name as arguments
        Path tempFile1 = Files.createTempFile(commandHandler.getCurrentDir(), "testFile1", ".txt");
        Path tempFile2 = Files.createTempFile(commandHandler.getCurrentDir(), "testFile2", ".txt");
        assertTrue(Files.exists(tempFile1), "First temporary file should exist before deletion");
        assertTrue(Files.exists(tempFile2), "Second temporary file should exist before deletion");

        commandHandler.rm(new String[]{tempFile1.getFileName().toString(), tempFile2.getFileName().toString()});

        assertFalse(Files.exists(tempFile1), "First temporary file should be deleted");
        assertFalse(Files.exists(tempFile2), "Second temporary file should be deleted");
    }

    @Test
    void testRmNonExistentFile() {
        String nonExistentFileName = "nonExistentFile.txt";
        //Path nonExistentFile = commandHandler.getCurrentDir().resolve(nonExistentFileName);

        commandHandler.rm(new String[]{nonExistentFileName});
        System.out.println("Attempt to delete the non-existent file!");
    }

    @Test
    void testMkdirCommand() throws IOException{
        String [] arg = {"testDir1","test2\\New"};
        commandHandler.mkdir(arg);
        File directory = new File("testDir1");
        File directory2 = new File("test2\\New");
        assertTrue((directory.exists()&&directory2.exists())&&(directory.isDirectory()&&directory2.isDirectory()),"New Directories created");

    }

    @Test
    void testMkdirCommandExisting() throws IOException{
        File directory = new File("testDir");
        directory.mkdir();
        String [] arg = {"testDir"};
        commandHandler.mkdir(arg);
        assertTrue(directory.exists()&&directory.isDirectory(),"Directory Is already Exists");
    }
    
  @Test
  void testWriteCommandWithLs() throws IOException {
    String outputFileName = "output.txt";
    Path outputPath = initialDir.resolve(outputFileName);

    commandHandler.write(new String[] {"", outputFileName}, "ls");

    assertTrue(Files.exists(outputPath), "Output file should be created");

    // Check contents of output file
    String content = Files.readString(outputPath);
    assertTrue(content.contains("sleep.txt"), "Output should contain 'sleep.txt'");
    assertTrue(content.contains("pls.txt"), "Output should contain 'pls.txt'"); }


    @Test
    void testWriteCommandWithpwd() throws IOException {
        String outputFileName = "output.txt";
        Path outputPath = initialDir.resolve(outputFileName);
    
        commandHandler.write(new String[] {"", outputFileName}, "pwd");
    
        assertTrue(Files.exists(outputPath), "Output file should be created");
    
        // Check contents of output file
        String content = Files.readString(outputPath);
        assertTrue(content.contains("Current working directory: C:\\Users\\user\\OS_Assignment"), "Output should contain Current working directory");}
    

    @Test
    void testTouchCreateNewFile() throws IOException {
        String newFileName = "newTestFile.txt";
        Path newFilePath = initialDir.resolve(newFileName);
        Files.deleteIfExists(newFilePath);
        // Create a new file using the touch command
        commandHandler.touch(new String[]{newFileName});
        // Check that the file was created
        assertTrue(Files.exists(newFilePath), "touch should create a new file if it doesn't exist");
        Files.deleteIfExists(newFilePath);
    }

    @Test
    void testTouchUpdateTimestamp() throws IOException {
        // Define the file name and create it beforehand
        String fileName = "existingTestFile.txt";
        Path filePath = initialDir.resolve(fileName);
        Files.deleteIfExists(filePath);
        Files.createFile(filePath);
        long initialTimestamp = Files.getLastModifiedTime(filePath).toMillis();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("The test was interrupted unexpectedly.");
        }

        // Call touch on the existing file to update its timestamp
        commandHandler.touch(new String[]{fileName});
        long updatedTimestamp = Files.getLastModifiedTime(filePath).toMillis();

        // Check that the timestamp was updated
        assertTrue(updatedTimestamp > initialTimestamp, "touch should update the file's timestamp if it already exists");

        Files.deleteIfExists(filePath);
    }
    @Test
    void testLsAListsHiddenFiles() throws IOException { //This test checks whether the ls -a command lists hidden files correctly.
        // Create a hidden file
        String hiddenFileName = ".hiddenTestFile.txt";
        Path hiddenFilePath = initialDir.resolve(hiddenFileName);
        Files.deleteIfExists(hiddenFilePath);
        Files.createFile(hiddenFilePath);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        commandHandler.ls_a();

        // Check if the output includes the hidden file
        String output = outputStream.toString();
        assertTrue(output.contains(hiddenFileName), "ls -a should list hidden files");
        System.setOut(System.out);
        Files.deleteIfExists(hiddenFilePath);
    }

    @Test
    void testLsAListsAllFiles() throws IOException { // This test checks whether the ls command (without options) lists all files in the directory, including regular files and hidden files
        // Create a visible file
        String visibleFileName = "visibleTestFile.txt";
        Path visibleFilePath = initialDir.resolve(visibleFileName);
        Files.deleteIfExists(visibleFilePath);
        Files.createFile(visibleFilePath);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        commandHandler.ls_a();

        // Check if the output includes the visible file
        String output = outputStream.toString();
        assertTrue(output.contains(visibleFileName), "ls -a should list visible files");

        System.setOut(System.out);
        Files.deleteIfExists(visibleFilePath);
    }

    //ls command tests
    @Test
    void testLsDirectoryDoesNotExist() {
        // Set up to capture console output before calling the method
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            String[] args = {initialDir.resolve("nonexistentDir").toString()};
            commandHandler.ls(args);

            // Capture the output after calling ls method
            String output = outputStream.toString();
            assertTrue(output.contains("This directory doesn't exist."));
        } finally {
            // Restore original System.out after the test
            System.setOut(originalOut);
        }
    }
    @Test
    void testLsNotADirectory() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        Path file = Files.createFile(initialDir.resolve("fileTest.txt"));
        try{

            String[] args = { file.toString() };
            commandHandler.ls(args);
            String output = outputStream.toString();
            assertTrue(output.contains("This is not a directory."));
        }finally {
            // Restore original System.out after the test
            System.setOut(originalOut);
            Files.deleteIfExists(file);
        }
    }

    //rmdir command tests
    @Test
    void testRmdirDirectoryNotEmpty() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        Path nonEmptyDir = Files.createDirectory(initialDir.resolve("nonEmptyDir"));
        try{
            Files.createFile(nonEmptyDir.resolve("fileInDir.txt"));
            commandHandler.rmdir(new String[]{nonEmptyDir.toString()});
            String output = outputStream.toString();
            assertTrue(output.contains("Failed to remove '" + nonEmptyDir.toString() + ": Directory not empty"));
        }finally {
            // Restore original System.out after the test
            System.setOut(originalOut);
            Files.deleteIfExists(nonEmptyDir.resolve("fileInDir.txt"));
            Files.deleteIfExists(nonEmptyDir);
        }
    }
    @Test
    void testRmdirNoDirectorySpecified() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        try{
            commandHandler.rmdir(new String[]{});
            String output = outputStream.toString();
            assertTrue(output.contains("Specify an empty directory to be removed."));
        }finally {
            // Restore original System.out after the test
            System.setOut(originalOut);
        }
    }
    @Test
    void testRmdirNotADirectory() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        Path testFile = Files.createFile(initialDir.resolve("testFile.txt"));
        try{
            commandHandler.rmdir(new String[]{testFile.toString()});
            String output = outputStream.toString();
            assertTrue(output.contains("Failed to remove " + testFile.toString() + " :Not a directory."));
        }finally {
            // Restore original System.out after the test
            System.setOut(originalOut);
            Files.deleteIfExists(testFile);
        }
    }

    //cat command tests
    @Test
    void testCatFileDoesNotExist() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        try{
            String nonExistentFile = "nonExistentFile.txt";
            commandHandler.cat(new String[]{nonExistentFile});
            String output = outputStream.toString();
            assertTrue(output.contains("Failed to concatenate " + nonExistentFile + ": File doesn't exist."));
        }finally {
            // Restore original System.out after the test
            System.setOut(originalOut);
        }
    }
    @Test
    void testCatValidFile() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        // Create a valid file with content
        File validFile = new File("validFile.txt");
        String content = "This is a valid file.\nIt has multiple lines.\n";
        Files.writeString(validFile.toPath(), content);

        try {
            commandHandler.cat(new String[]{validFile.getAbsolutePath()});
            String output = outputStream.toString();
            assertTrue(output.contains("This is a valid file."), "Expected content not found in output.");
            assertTrue(output.contains("It has multiple lines."), "Expected content not found in output.");
        } finally {
            System.setOut(originalOut);
            validFile.delete(); // Cleanup
        }
    }
     @Test
    void testPwdCommand() {
        String expectedDirectory = System.getProperty("user.dir");
        assertEquals(("Current working directory: "+expectedDirectory), commandHandler.pwd());
    }
    //ls -r command tests
    @Test
void testLs_rCurrentDirectory() {
    
    String[] args = {"-r"};
    commandHandler.ls_r(args);
    
    // Capture output and trim whitespace for consistent comparison
    String output = outputStreamCaptor.toString().trim();
    
    // Check if the output contains expected content
    assertTrue(output.contains("Directory contents in reverse order:"), 
               "Expected directory listing message in console output.");
}
    @Test
        void testLs_rWithSpecificDirectory() throws IOException {
           
            Path tempDir = Files.createTempDirectory("testDir");
            Files.createFile(tempDir.resolve("file1.txt"));
            Files.createFile(tempDir.resolve("file2.txt"));
            Files.createFile(tempDir.resolve("file3.txt"));
    
            String[] args = {"-r", tempDir.toString()};
            commandHandler.ls_r(args);
    
            // Check if the output contains the file names in reverse order
            String output = outputStreamCaptor.toString();
            assertTrue(output.contains("file3.txt"));
            assertTrue(output.contains("file2.txt"));
            assertTrue(output.contains("file1.txt"));
    
            // Cleanup temporary files
            Files.deleteIfExists(tempDir.resolve("file1.txt"));
            Files.deleteIfExists(tempDir.resolve("file2.txt"));
            Files.deleteIfExists(tempDir.resolve("file3.txt"));
            Files.deleteIfExists(tempDir);
        }
    
    @Test
         void testLs_rWithNonExistentDirectory() {
            String[] args = {"-r", "nonExistentDir"};
            commandHandler.ls_r(args);
    
            
            assertEquals("This directory does not exist", outputStreamCaptor.toString().trim());
        }
    //mv command tests
    @Test
     void testMoveFileToDirectory() { //test for move filesrc to dirdestination
        String[] args = {sourceFile.getAbsolutePath(), destinationDir.getAbsolutePath()};
        commandHandler.mv(args);
        
        // Verify that the source file no longer exists
        assertFalse(sourceFile.exists(), "Source file should not exist after moving");

        // Verify that the file exists in the destination directory
        File movedFile = new File(destinationDir, sourceFile.getName());
        assertTrue(movedFile.exists(), "File should exist in the destination directory");
    }

    @Test
    void testRenameFile() { //test for rename filesrc to filedestination
        String[] args = {sourceFile.getAbsolutePath(), destinationFile.getAbsolutePath()};
        commandHandler.mv(args);
        
        // Verify that the source file no longer exists
        assertFalse(destinationFile.exists(), "Source file should not exist after renaming");
    
        // Verify that the file has been renamed
        assertTrue(sourceFile.exists(), "File should exist with the new name");
    }


}
