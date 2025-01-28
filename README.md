# Command Line Interpreter with JUnit Testing

## **Overview**
This project implements a basic Command Line Interpreter (CLI) in Java, mimicking the functionality of a Unix/Linux shell. It supports system commands like `pwd`, `cd`, `ls`, and more, as well as internal commands such as `exit` and `help`. Additionally, the project includes JUnit test cases to ensure the reliability of the implemented commands.

---

## **Features**
1. **Command Execution:**
   - Supported commands: `pwd`, `cd`, `ls`, `ls -a`, `ls -r`, `mkdir`, `rmdir`, `touch`, `mv`, `rm`, `cat`, `>`, `>>`, `|`
   - Handles both valid and invalid inputs gracefully.

2. **Internal Commands:**
   - `exit`: Terminates the CLI.
   - `help`: Displays a list of available commands and their usage.

3. **JUnit Testing:**
   - Automated test cases to verify the functionality of each command.
   - Ensures code reliability and consistency across updates.
   - Tests for edge cases, such as invalid paths, improper parameters, and other errors.

---

## **Technologies Used**
- **Programming Language:** Java  
- **Testing Framework:** JUnit  
- **Development Tools:** IntelliJ IDEA, Eclipse, or any Java IDE  
- **Version Control System:** Git and GitHub  

---

## **Key Concepts**
1. **Command Parsing and Execution:**
   - The CLI interprets user input, identifies valid commands, and executes them using Java-based logic.
   - Implements commands like `mv` with multi-functional capabilities (e.g., moving and renaming files).

2. **Error Handling:**
   - Displays meaningful error messages for invalid commands, parameters, or paths without crashing.

3. **Automated Testing with JUnit:**
   - Utilizes JUnit annotations like `@Test`, `@Before`, and `@After` to structure test cases.
   - Employs assertions (`assertEquals`, `assertTrue`, etc.) to verify outcomes against expected behavior.

4. **Object-Oriented Programming:**
   - Modular design for command functionalities and test cases.
   - Uses Java's standard libraries and predefined classes for file and directory management.

---
