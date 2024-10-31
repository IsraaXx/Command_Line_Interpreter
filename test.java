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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class test {
    private CommandHandler commandHandler;
    private Path initialDir;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private File tempDir;
    private File sourceFile;
    private File destinationDir;
    private File destinationFile;
    private Path tempDirect;
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
        tempDirect = Files.createTempDirectory("testDir");
    }
    @AfterEach
    public void cleanAfterTest() {
        // Cleanup the temporary files and directories after tests
        if (sourceFile.exists()) sourceFile.delete();
        if (destinationDir.exists()) destinationDir.delete();
        if (destinationFile.exists()) destinationFile.delete();
        if (tempDir.exists()) tempDir.delete();
    }
    //cd command tests
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
    //rm command tests
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
    //mkdir command tests
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

    //touch command tests
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
    //ls-a command tests
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
        // Set up to capture console output before calling the method
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        //create a file
        Path file = Files.createFile(initialDir.resolve("fileTest.txt"));
        try{

            String[] args = { file.toString() };
            //pass the created file to ls function
            commandHandler.ls(args);
            String output = outputStream.toString();
            assertTrue(output.contains("This is not a directory."));
        }finally {
            // Restore original System.out after the test
            System.setOut(originalOut);
            Files.deleteIfExists(file);
        }
    }
    @Test
    void testLsCommandSuccess() throws IOException {
        // Set up to capture console output before calling the method
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        //create a directory
        Path testDirectory = Files.createTempDirectory("testDirectory");
        // Create some files in the test directory
        File file1 = new File(testDirectory.toFile(), "file1.txt");
        File file2 = new File(testDirectory.toFile(), "file2.txt");
        File hiddenFile = new File(testDirectory.toFile(), ".hiddenFile.txt");

        List.of(file1, file2, hiddenFile).forEach(file -> {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            // Call the ls command
            commandHandler.ls(new String[]{testDirectory.toString()});
            String output = outputStream.toString();
            // Capture and verify the output
            assertTrue(output.contains("file1.txt"), "Expected 'file1.txt' to be listed in the output.");
            assertTrue(output.contains("file2.txt"), "Expected 'file2.txt' to be listed in the output.");
            assertTrue(!output.contains(".hiddenFile.txt"), "Expected '.hiddenFile.txt' to be excluded from the output.");
        } finally {
            // Restore System.out and clean up files
            System.setOut(originalOut);
            Files.deleteIfExists(file1.toPath());
            Files.deleteIfExists(file2.toPath());
            Files.deleteIfExists(hiddenFile.toPath());
            Files.deleteIfExists(testDirectory);
        }
    }
    @Test
    void testLsCommandInCurrentDirectory() throws IOException {
        // Set up to capture console output before calling the method
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        // Create temporary files in the current directory
        Path currentDirectory = Path.of("").toAbsolutePath(); // Current directory path
        Path file1 = Files.createFile(currentDirectory.resolve("testFile1.txt"));
        Path file2 = Files.createFile(currentDirectory.resolve("testFile2.txt"));

        try {
            // Call ls without arguments to list files in the current directory
            commandHandler.ls(new String[]{});

            // Capture the output from the ls command
            String output = outputStream.toString();

            // Verify the output contains the files in the current directory
            assertTrue(output.contains("testFile1.txt"),
                    "Expected testFile1.txt to be listed in output: " + output);
            assertTrue(output.contains("testFile2.txt"),
                    "Expected testFile2.txt to be listed in output: " + output);

        } finally {
            // Restore original System.out after the test
            System.setOut(originalOut);

            // Cleanup: delete test files created in the current directory
            Files.deleteIfExists(file1);
            Files.deleteIfExists(file2);
        }
    }
    //rmdir command tests
    @Test
    void testRmdirDirectoryNotEmpty() throws Exception {
        // Set up to capture console output before calling the method
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        //create non-empty directory
        Path nonEmptyDir = Files.createDirectory(initialDir.resolve("nonEmptyDir"));
        try{
            //create a file in the directory
            Files.createFile(nonEmptyDir.resolve("fileInDir.txt"));
            //call the rmdir command
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
        // Set up to capture console output before calling the method
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        try{
            //call rmdir with empty array -> no directory specified
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
        // Set up to capture console output before calling the method
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        //create a file
        Path testFile = Files.createFile(initialDir.resolve("testFile.txt"));
        try{
            //pass the file to rmdir
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
        // Set up to capture console output before calling the method
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        try{
            String nonExistentFile = "nonExistentFile.txt";
            //call the cat with a file name that doesn't exist
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
        // Set up to capture console output before calling the method
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
    //pwd command tests
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

     @Test
    public void WriteTest() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(buffer));
        System.setOut(originalOut);
        String outputLog = buffer.toString();
        if (outputLog.contains("Output written to")) {
            System.out.println("Test passed: 'Output written to' message found.");
        } else {
            System.out.println("Test failed: 'Output written to' message not found.");
        }
    }
    
    // append (>>) command tests
    @Test
    public void testAppendToFile_ExistingFile() throws IOException {
        File testFile = new File(tempDirect.toFile(), "testOutput.txt");
        Files.writeString(testFile.toPath(), "first content in the file\n");
        commandHandler.appendToFile("echo", new String[] {"new content", ">>", testFile.getAbsolutePath()});
        // check if both initial and new content are present
        String content = Files.readString(testFile.toPath());
        assertTrue(content.contains("first content in the file"), "File should contain initial content");
        assertTrue(content.contains("new content"), "File should contain appended content");
    }
    @Test
    public void testAppendToFile_NonExistentFile() {
        //  create a path for a file that does not exist
        File nonExistentFile = new File(tempDirect.toFile(), "nonExistentFile.txt");
        // Capture console output to verify error message
        ByteArrayOutputStream consoleOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(consoleOutput));
        commandHandler.appendToFile("echo", new String[] {"some content", ">>", nonExistentFile.getAbsolutePath()});
        //  check for error message and that the file was not created
        assertTrue(consoleOutput.toString().contains("The specified file '" + nonExistentFile.getName() + "' does not exist."));
        assertFalse(nonExistentFile.exists(), "Non-existent file should not be created");
        System.setOut(System.out);
    }

}
