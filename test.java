import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

public class test {
    private CommandHandler commandHandler;
    private Path initialDir;

    @BeforeEach
    void setUp() {
        commandHandler = new CommandHandler();
        initialDir = commandHandler.getCurrentDir(); 
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

        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outputStream));
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

        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outputStream));
        commandHandler.ls_a();

        // Check if the output includes the visible file
        String output = outputStream.toString();
        assertTrue(output.contains(visibleFileName), "ls -a should list visible files");

        System.setOut(System.out);
        Files.deleteIfExists(visibleFilePath);
    }
    

}
