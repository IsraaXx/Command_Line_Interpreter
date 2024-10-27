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

}
