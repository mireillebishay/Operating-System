import java.util.Hashtable;

public class MyProcess {
	private static int processCount =1;
	private PCB pcb;
	String tempFile = null;
	public MyProcess(int lower, int upper, ProcessState s) {
		int PID = processCount++;
		pcb = new PCB(PID, processCount, upper, s);
	}
	public static int getProcessCount() {
		return processCount;
	}
	public static void setProcessCount(int processCount) {
		MyProcess.processCount = processCount;
	}
	public PCB getPcb() throws Exception {
		
		feedPCBFromMemory();
		return pcb;
	}
	public String toString() {
		return this.pcb.getProcessID() +"";
	}
	
	public void feedPCBFromMemory() throws Exception{
		OS os = OS.getInstance(2);
		Memory memory = os.memory;
		for(int i = 0;i < 7;i+=6) {
			if(  memory.read(new int [] {1,i}) != null && (Integer) memory.read(new int [] {1,i}) == this.pcb.getProcessID()) {
				boolean isNull = false;
				for(int j = i; j<=i+5;j++) {
					if(memory.read(new int [] {1,j}) == null || !((String) memory.read(new int [] {0,j})).substring(7, 8).equals(this.pcb.getProcessID() +"")) {
						isNull = true;
						break;
					}
				}
			if(isNull ==false) {
//				System.out.println("here(&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
			 pcb.setProcessID((Integer) memory.read(new int [] {1,i}));
			
			 pcb.setPC  ((Integer) memory.read(new int [] {1,i+1}));
			 pcb.setProcessState(((ProcessState) memory.read(new int [] {1,i+2})));
			 pcb.setLowerBound((Integer) memory.read(new int [] {1,i+3}));
			 pcb.setUpperBound((Integer) memory.read(new int [] {1,i+4}));
			 pcb.setInstructionsEnd((Integer) memory.read(new int [] {1,i+5}));
			 break;
			}
			}
		}
	}
	public void setPcb(PCB pcb) {
		this.pcb = pcb;
	}

	public void feedPCBFromDisk() throws Exception {
	    OS os = OS.getInstance(2);
	    os.disk.readSSDFromFile();
	    Hashtable<String, Hashtable<String, String>> SSDPCB = os.disk.SSDPCB;

	   Hashtable<String, String> pcbData = SSDPCB.get("PCB" + this.getPcb().getProcessID());
	       

	        String processIDStr = pcbData.get("processID".toUpperCase());
	        if (processIDStr != null) {
	            int processID = Integer.parseInt(processIDStr);
	            pcb.setProcessID(processID);
	        }

	        String processStateStr = pcbData.get("processState".toUpperCase());
	        if (processStateStr != null) {
	            ProcessState processState = ProcessState.valueOf(processStateStr);
	            pcb.setProcessState(processState);
	        }

	        String PCStr = pcbData.get("PC".toUpperCase());
//	        System.out.println("{{{{{{{}}}}}}}}}}}}}}}");
//	        System.out.println(PCStr);
	        if (PCStr != null) {
	            int PC = Integer.parseInt(PCStr);
	            pcb.setPC(PC);
	        }

	        String upperBoundStr = pcbData.get("upperBound".toUpperCase());
	        if (upperBoundStr != null) {
	            int upperBound = Integer.parseInt(upperBoundStr);
	            pcb.setUpperBound(upperBound);
	        }

	        String lowerBoundStr = pcbData.get("lowerBound".toUpperCase());
	        if (lowerBoundStr != null) {
	            int lowerBound = Integer.parseInt(lowerBoundStr);
	            pcb.setLowerBound(lowerBound);
	        }

	        String instructionsEndStr = pcbData.get("instructionsEnd".toUpperCase());
	        if (instructionsEndStr != null) {
	            int instructionsEnd = Integer.parseInt(instructionsEndStr);
	            pcb.setInstructionsEnd(instructionsEnd);
	        }

	        // Do something with the populated PCB object
	        // For example, add it to a list or perform further operations
	        // ...
	}

}
