import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CodeParser {
	
	
	
	public static void execute(String instruction) throws Exception {
		String[] parts = instruction.split(" ");
        String command = parts[0];
        String variable = parts[1];
        String value = parts.length > 2 ? parts[2] : null;
        String value2 = parts.length > 3 ? parts[3] : null;
        
        
        switch (command) {
            case "print":
                OS.print(variable);
                break;
            case "assign":
                if (value.equals("readFile")) {
                   OS.assign(variable, value2, "readFile");
                } else {
                    OS.assign(variable, value, "");
                }
                break;
            case "writeFile":
            	OS.writeFile(variable, value);
                break;
            case "readFile":
            	OS.readFile(variable,false);
                break;
            case "printFromTo":
            	OS.printFromTo(variable, value);
                break;
            case "semWait":
            	OS.semWait(variable);
                break;
            case "semSignal":
            	OS.semSignal(variable);
                break;
            default:
                System.out.println("Invalid instruction: " + instruction);
                break;
        }
        
    }

	
	

}
