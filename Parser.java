import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.Scanner;
public class Parser {
        String commandName;
        String[] args;
        //This method will divide the input into commandName and args
        //where "input" is the string command entered by the user
        public String getCommandName(){
            return commandName;
        };
        public String[] getArgs(){
            return args;
        };

        public boolean parse(String input){
            if(input.isEmpty()){
                commandName="";
                args = new String[0];
                return false;
            }

            String[] commInput = input.split("\\s+");   //regex to Split a String by Whitespaces
            commandName = commInput[0];
            if(commInput.length>1){
                args = new String[commInput.length - 1];
                System.arraycopy(commInput, 1, args, 0, commInput.length-1);
            }
            else {
                args = new String[0];   }

            return true;    

        }
}
