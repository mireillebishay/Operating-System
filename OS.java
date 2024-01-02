import java.util.*;
import java.io.*;

public class OS {
	private static OS instance; 
	Scanner sc;
	Memory memory;
	Queue <MyProcess> readyQueue;
	Queue <MyProcess> blockedQueue;
	Mutex file;
	Mutex output;
	Mutex input;
	MyProcess currentProcess;
	int timeSlice;
	int processesFinished = 0;
	int clockCycles = 0;
	int leftCyclesToSwap;
	int p1Arrival;
	int p2Arrival;
	int p3Arrival;
	HardDisk disk = new HardDisk();
	
	
	private OS (int timeSlice, int p1,int p2,int p3) throws Exception {
		sc = new Scanner(System.in);
		this.timeSlice = timeSlice;
	    file = new Mutex();
		output = new Mutex();
		input = new Mutex();
		readyQueue =  new ArrayDeque <MyProcess>();
		blockedQueue =  new ArrayDeque <MyProcess>();
		memory = new Memory();
		memory.allocate(0, 11);
		leftCyclesToSwap = timeSlice;
		this.p1Arrival = p1;
		this.p2Arrival = p2;
		this.p3Arrival = p3;
		System.out.println("timeSlice: " + timeSlice);
		System.out.println("process 1 will arrive at: " + p1);
		System.out.println("process 2 will arrive at: " + p2);
		System.out.println("process 3 will arrive at: " + p3);
		
		
	}
	public static OS getInstance(int timeSlice) throws Exception {
//		System.out.println(instance);
//		if (instance == null) instance = new OS(timeSlice);
		return instance;
		}
	public static OS getInstance(int timeSlice,int p1,int p2,int p3) throws Exception {
//		System.out.println(instance);
		if (instance == null) instance = new OS(timeSlice,p1,p2,p3);
		return instance;
		}

	
	public void createProcess(String path, boolean creation, MyProcess peek) {
		//create new process:
		//1.ReadFile
		//2.allocate memory and swap if there is no space
		//3.Store instructions,PCB in memory
		//4.put in readyQ
		try {
			MyProcess toBeSwapped = null;
			ArrayList <String> instructions = new ArrayList<String>();
			instructions = readFile(path);
			MyProcess newProcess = null;
			if(creation)
				newProcess = new MyProcess(-1, -1, ProcessState.NEW);
			else {
				newProcess = peek;
				newProcess.feedPCBFromDisk();
				}
			int u,l = 0;
			if(memory.isFree(12, 25)) {
				memory.allocate(12, 25);
				l = 12;
				u = 25;
				
				}
			else if(memory.isFree(26,39)) {
				memory.allocate(26, 39);
				l = 26;u=39;
			}
			else {
				 toBeSwapped = null;
//				if(clockCycles==4)
//				memory.printMemory();
				if(blockedQueue.peek() != null) {
					
					toBeSwapped = blockedQueue.element();
					toBeSwapped.getPcb().setProcessState(ProcessState.BLOCKEDDISK);
				}
				else  if (readyQueue.size() > 0){
					Object [] x= readyQueue.toArray();
					toBeSwapped = (MyProcess) x[x.length-1]; //is this the last element?
					toBeSwapped.getPcb().setProcessState(ProcessState.READYONDISK);
				}
				Hashtable <String,String>  vars =  new Hashtable<String, String>();
				for(int i = toBeSwapped.getPcb().getInstructionsEnd() +1; i< toBeSwapped.getPcb().getUpperBound();i++) {
					if((String) memory.read(new int [] {0,i}) != null)
						vars.put( (String) memory.read(new int [] {0,i})  , (String) memory.read(new int [] {1,i}));
				}
				Hashtable <String,String>  PCB =  new Hashtable<String, String>();
				for(int i = 0;i < 7;i+=6) {
					if (( toBeSwapped != null && memory.read(new int [] {1,i}) != null &&(Integer) memory.read(new int [] {1,i}) == toBeSwapped.getPcb().getProcessID() )) {
						PCB.put( ((String) memory.read(new int [] {0,i})).substring(8) ,  memory.read(new int [] {1,i}).toString());
						PCB.put( ((String) memory.read(new int [] {0,i+1})).substring(8) ,  memory.read(new int [] {1,i+1}).toString());
						PCB.put( ((String) memory.read(new int [] {0,i+2})).substring(8) ,  memory.read(new int [] {1,i+2}).toString());
						PCB.put( ((String) memory.read(new int [] {0,i+3})).substring(8) ,  memory.read(new int [] {1,i+3}).toString());
						PCB.put( ((String) memory.read(new int [] {0,i+4})).substring(8) ,  memory.read(new int [] {1,i+4}).toString());
						PCB.put( ((String) memory.read(new int [] {0,i+5})).substring(8) ,  memory.read(new int [] {1,i+5}).toString());
					}}
				disk.SSD.put(toBeSwapped.getPcb().getProcessID(),vars);
				disk.SSDPCB.put("PCB" + toBeSwapped.getPcb().getProcessID(), PCB);
//				System.out.println(disk.SSD + "]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
				disk.writeSSDToFile();
					
				memory.deallocate(toBeSwapped.getPcb().getLowerBound(), toBeSwapped.getPcb().getUpperBound());
				memory.allocate(toBeSwapped.getPcb().getLowerBound(), toBeSwapped.getPcb().getUpperBound());
				
				l = toBeSwapped.getPcb().getLowerBound();
				u = toBeSwapped.getPcb().getUpperBound();
			}
				newProcess.getPcb().setLowerBound(l);
				newProcess.getPcb().setUpperBound(u);
				if(creation) {
				readyQueue.add(newProcess);
				newProcess.getPcb().setProcessState(ProcessState.READYONMEMORY);
				
				}
				else
					newProcess.getPcb().setProcessState(ProcessState.RUNNING);
				
				int c = 0;
				int i ;
				for(i = newProcess.getPcb().getLowerBound(); c<instructions.size();i++) {
					memory.write(i, instructions.get(c) , "instruction" + (c+1));
					c++;
				}
				newProcess.getPcb().setInstructionsEnd(i-1);
				for(i = 0;i < 7;i+=6) {
					if (memory.read( new int [] {1,i}) == null || memory.isValid[i] == false || ( toBeSwapped != null && (Integer) memory.read(new int [] {1,i}) == toBeSwapped.getPcb().getProcessID() )) {
						memory.deallocate(i, i+5);
						memory.write(i, newProcess.getPcb().getProcessID(), "Process" + newProcess.getPcb().getProcessID() +"ID");
						memory.write(i+1, newProcess.getPcb().getPC(), "Process" + newProcess.getPcb().getProcessID() +"PC");
						memory.write(i+2, newProcess.getPcb().getProcessState(), "Process" + newProcess.getPcb().getProcessID() +"STATE");
						memory.write(i+3, newProcess.getPcb().getLowerBound(), "Process" + newProcess.getPcb().getProcessID()+ "LOWERBOUND");
						memory.write(i+4, newProcess.getPcb().getUpperBound(), "Process" + newProcess.getPcb().getProcessID() + "UPPERBOUND");
						memory.write(i+5, newProcess.getPcb().getInstructionsEnd(), "Process" + newProcess.getPcb().getProcessID() +" INSTUCTIONSEND");
						break;
						}
				}
				
				disk.readSSDFromFile();
				Hashtable <String,String> varsData = disk.SSD.get(newProcess.getPcb().getProcessID());
				if(varsData != null) {
				 int j = newProcess.getPcb().getInstructionsEnd()+1;
				 for(String s: varsData.keySet()) {
					 memory.write(j, varsData.get(s), s);
					 j++;
				 }
				}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() throws Exception {
		System.out.println("cycle " + clockCycles);
		// NEW -> UNBLOCKED -> RUNNING
		//1. Create new Processes
		
		//Processes arrive in this order: Process 1 arrives at time 0, Process 2 arrives at
				//time 1, and Process 3 arrives at time 4
		
		if(clockCycles == p1Arrival) {
			
			createProcess("src/Program_1.txt",true,null);
			}
		if(clockCycles == p2Arrival)
			createProcess("src/Program_2.txt",true,null );
		if(clockCycles == p3Arrival) {
//			System.out.println("creatingggggggggg");
			createProcess("src/Program_3.txt",true,null);}
		if(currentProcess == null) {
			if(readyQueue.peek() != null)
				currentProcess = readyQueue.remove(); 
			}
//		memory.printMemory();
//		System.out.println("current: " + currentProcess);
		//Execute next instruction of the currentProcess
		if(currentProcess != null) {
		String instruction = (String) memory.read(new int [] {1, currentProcess.getPcb().getPC() + currentProcess.getPcb().getLowerBound() });
		System.out.println( "current instruction: "+instruction);
		System.out.println( "PC: " +currentProcess.getPcb().getPC());
		System.out.println( "PID: " +currentProcess.getPcb().getProcessID());
//		currentProcess.getPcb().setPC(currentProcess.getPcb().getPC()+1);
		
			this.leftCyclesToSwap--;
			CodeParser.execute(instruction);
		}
		else {
			System.out.println("No Process executing now");
			leftCyclesToSwap = 0;
		}
		
		
		//check if a process reaches the end of the instructions after each cycle, then its done and remove its content from memory and increment the processesFinished
//		
		if(currentProcess != null) {
			if(currentProcess.getPcb().getInstructionsEnd() <=  currentProcess.getPcb().getPC() + currentProcess.getPcb().getLowerBound()-1) {
				currentProcess.getPcb().setProcessState(ProcessState.ZOMBIE);
				memory.deallocate(currentProcess.getPcb().getLowerBound(), currentProcess.getPcb().getUpperBound());
				for(int i = 0;i < 7;i+=6) {
					if(  memory.read(new int [] {1,i}) != null && (Integer) memory.read(new int [] {1,i}) == currentProcess.getPcb().getProcessID()) {
						memory.deallocate(i, i+5);
					}
				}
				processesFinished++;
				leftCyclesToSwap = 0;
			}
		}
		//if processesFinished == 3 then we are done
		if(processesFinished == 3) {
			System.out.println();
			System.out.println("Ready Queue: " + readyQueue);
			System.out.println("Blocked Queue" + blockedQueue);
			System.out.println("File Blocked Queue" + file.getBlockedQ());
			System.out.println("Input Blocked Queue" + this.input.getBlockedQ());
			System.out.println("Output Blocked Queue" + this.output.getBlockedQ());
			return;
		}
		
		//after the time slice, swap processes(if the chosen is on disk, then load its data again) , hane3mel eh fil vars??
		if(leftCyclesToSwap <= 0) {
			leftCyclesToSwap = timeSlice;
			if( currentProcess != null && !currentProcess.getPcb().getProcessState().equals(ProcessState.ZOMBIE) && !currentProcess.getPcb().getProcessState().equals(ProcessState.BLOCKEDMEMORY)) {
				readyQueue.add(currentProcess);
				currentProcess.getPcb().setProcessState(ProcessState.READYONMEMORY);
			}
			
			
			MyProcess toBeCurrent = readyQueue.peek() != null ? readyQueue.remove() :currentProcess != null && !currentProcess.getPcb().getProcessState().equals(ProcessState.ZOMBIE) ? currentProcess:null;
			
			if(toBeCurrent != null) {
				if( toBeCurrent.getPcb().getProcessState() == ProcessState.READYONDISK) {
					System.out.println("swapping");
//					memory.printMemory();
					createProcess("src/Program_" + toBeCurrent.getPcb().getProcessID() +".txt", false, toBeCurrent);
//					memory.printMemory();
					
				}
			}
			currentProcess = toBeCurrent;
			if(currentProcess != null)
				toBeCurrent.getPcb().setProcessState(ProcessState.RUNNING);
			
		}
//		if(clockCycles == 13)
//			memory.printMemory();
		System.out.println();
		System.out.println("After Execution of this instruction: ");
		System.out.println("Ready Queue: " + readyQueue);
		System.out.println("Blocked Queue" + blockedQueue);
		System.out.println("File Blocked Queue" + file.getBlockedQ());
		System.out.println("Input Blocked Queue" + this.input.getBlockedQ());
		System.out.println("Output Blocked Queue" + this.output.getBlockedQ());
		System.out.println();
		System.out.println("----------------");
		memory.printMemory();
		System.out.println("----------------");
		clockCycles++;
		
		run();	
	}
	
	private ArrayList <String> readFile(String path) throws Exception {
			FileReader fr = new FileReader(path);
			BufferedReader br = new BufferedReader(fr);
			ArrayList <String> res = new ArrayList<String>(); 
			String s = br.readLine();
			while(s != null) {
				res.add(s);
				s = br.readLine();	
			}
			br.close();
			return res;	
		
	}
	
	// system calls: 
	public static void semSignal(String resource) throws Exception {
		OS os = OS.getInstance(2);
		// TODO Auto-generated method stub
		if(resource.equals("userInput"))
			Mutex.semSignal(os.input);
		else if(resource.equals("userOutput"))
			Mutex.semSignal(os.output);
		else if(resource.equals("file"))
			Mutex.semSignal(os.file);
		MyProcess currentProcess = os.currentProcess;
	    currentProcess.getPcb().setPC(currentProcess.getPcb().getPC()+1);
		
		//get the corresponding mutex from the OS and call on it the method in the mutex class
		
	}

	public static void semWait(String resource) throws Exception {
		// TODO Auto-generated method stub
		OS os = OS.getInstance(2);
//		System.out.println("here brooooooooooooooooo");
		
		//get the corresponding mutex from the OS and call on it the method in the mutex class
		if(resource.equals("userInput"))
			Mutex.semWait(os.input);
		else if(resource.equals("userOutput"))
			Mutex.semWait(os.output);
		else if(resource.equals("file"))
			Mutex.semWait(os.file);
		MyProcess currentProcess = os.currentProcess;
	    currentProcess.getPcb().setPC(currentProcess.getPcb().getPC()+1);
		//if blocked , reset time to 0
		
	}

	public static void printFromTo(String variable, String variable2) throws Exception {
        // TODO Auto-generated method stub
        OS os = OS.getInstance(2);
        MyProcess currentProcess = os.currentProcess;
        //get 2 var values and parse to ints
//        System.out.println("(((((((((((((((((((((((((((((((((");
        int instructionEnd = currentProcess.getPcb().getInstructionsEnd();
        int upperBound = currentProcess.getPcb().getUpperBound();
        int a = Integer.parseInt( getVariableValue(instructionEnd, upperBound, variable));
        int b = Integer.parseInt(getVariableValue(instructionEnd, upperBound, variable2));

        // print all values in between
        if (a < b) {
            for (; a <= b; a++) {
                System.out.print(a +" ");
            }
        } else {
            for (; b <= a; b++) {
                System.out.print(b + " ");
            }
        }
        System.out.println();
//        MyProcess currentProcess = os.currentProcess;
	    currentProcess.getPcb().setPC(currentProcess.getPcb().getPC()+1);

    }
	
	public static void assign(String variable, String value, String type) throws Exception {
        OS os = OS.getInstance(2);
        Object variableValue = null;
//        System.out.println(type);
        if (type.equals("readFile") && os.currentProcess.tempFile == null) {
//        	System.out.println("here---------------------------------------");
             variableValue = new String(readFile(value,true));
             os.currentProcess.tempFile = (String) variableValue;
             return;
        } else if(type.equals("") && os.currentProcess.tempFile == null) {
            Scanner scanner = os.sc; 
            System.out.println("Please enter a value: ");
            variableValue = new String(scanner.nextLine());
            os.currentProcess.tempFile = (String) variableValue;
            return;
//            scanner.close();
        }
        else {
        	variableValue =  os.currentProcess.tempFile;
        	os.currentProcess.tempFile = null;
        	}

        MyProcess currentProcess = os.currentProcess;
        int instructionEnd = currentProcess.getPcb().getInstructionsEnd();
        int upperBound = currentProcess.getPcb().getUpperBound();

        for (; instructionEnd < upperBound; instructionEnd++) {
            if (os.memory.read(new int[]{1, instructionEnd}) == null ||  !os.memory.isValid[instructionEnd]) {
                os.memory.write(instructionEnd, variableValue, variable);
                break;
                }
        }
//        MyProcess currentProcess = os.currentProcess;
	    currentProcess.getPcb().setPC(currentProcess.getPcb().getPC()+1);
	}

	public static String readFile(String variable,boolean compoundInsturction) throws Exception {
		// TODO Auto-generated method stub
		OS os = OS.getInstance(2);
		
		//1. get fileName from the var in memory
		String fileName = getVariableValue(os.currentProcess.getPcb().getInstructionsEnd(), os.currentProcess.getPcb().getUpperBound(), variable);
		FileReader fr = new FileReader("src/"+fileName);
		BufferedReader br = new BufferedReader(fr);
		String res = "";
		String s = br.readLine();
		while(s != null) {
			res += s;
			s = br.readLine();	
		}
		br.close();
		MyProcess currentProcess = os.currentProcess;
		if(!compoundInsturction) 
			currentProcess.getPcb().setPC(currentProcess.getPcb().getPC()+1);	
		return res;	
		
		//2. readFile and return it
		
		
	}

	public static void writeFile(String fileVariableName, String variableNameToBeWritten) throws Exception {
		// TODO Auto-generated method stub
		OS os = OS.getInstance(2);
		//1.GET variable data
		String s = getVariableValue(os.currentProcess.getPcb().getInstructionsEnd(), os.currentProcess.getPcb().getUpperBound(), variableNameToBeWritten);
		
		String fileName = getVariableValue(os.currentProcess.getPcb().getInstructionsEnd(), os.currentProcess.getPcb().getUpperBound(), fileVariableName);
		File file = new File("src/"+fileName);
		file.createNewFile();
		BufferedWriter writer2 = new BufferedWriter(new FileWriter("src/"+fileName));
		writer2.write(s);
	    writer2.close();
	    MyProcess currentProcess = os.currentProcess;
	    currentProcess.getPcb().setPC(currentProcess.getPcb().getPC()+1);
	    
	}

	public static void print(String variable) throws Exception {
        //1. get variable value of the current Process
        OS os = OS.getInstance(2);
        MyProcess currentProcess = os.currentProcess;
        int instructionEnd = currentProcess.getPcb().getInstructionsEnd();
        int upperBound = currentProcess.getPcb().getUpperBound();
        System.out.println(getVariableValue(instructionEnd, upperBound, variable));
//        MyProcess currentProcess = os.currentProcess;
	    currentProcess.getPcb().setPC(currentProcess.getPcb().getPC()+1);
    }
	
	public static String getVariableValue(int instructionsEnd, int upperBound, String varName) throws Exception {
	        OS os = OS.getInstance(2);
	        String varNameInMemory;
	    for(;instructionsEnd<upperBound; instructionsEnd++ ){
	        varNameInMemory = (String) os.memory.read( new int [] {0,instructionsEnd});
	        if (varNameInMemory.equals(varName) && os.memory.isValid[instructionsEnd]){
	            return (String) os.memory.read( new int [] {1,instructionsEnd});
	        }
	    }
	    return null;
	    }
	
	//
	public static void main(String[] args) {
		try {
		OS os = getInstance(2,0,1,4);
		os.run();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
}
