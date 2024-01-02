
public class PCB {
	private int processID;
	private ProcessState processState;
	private int PC;
	private int upperBound;
	private int lowerBound;
	private int instructionsEnd;
	
	public PCB (int processID, int lowerBound,int upperBound,ProcessState processState) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.processID = processID;
		this.processState = processState;
		this.PC = 0;
	}
	public int getProcessID() {
		return processID;
	}
	public void setProcessID(int processID) {
		this.processID = processID;
	}
	
	public int getPC() {
		return PC;
	}
	public void setPC(int pC) throws Exception {
		PC = pC;
		OS os = OS.getInstance(2);
		Memory memory = os.memory;
		for(int i = 0;i < 7;i+=6) {
			if(memory.read(new int [] {1,i})!= null && (Integer) memory.read(new int [] {1,i}) == this.processID) {
				memory.write(i+1, pC, "Process" + processID +"PC");
				break;
				}
		}
	}
	public int getUpperBound() {
		return upperBound;
	}
	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}
	public int getLowerBound() {
		return lowerBound;
	}
	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}
	public ProcessState getProcessState() {
		return processState;
	}
	public void setProcessState(ProcessState processState) throws Exception {		
		this.processState = processState;
		OS os = OS.getInstance(2);
		Memory memory = os.memory;
		for(int i = 0;i < 7;i+=6) {
			if(memory.read(new int [] {1,i}) != null && (Integer) memory.read(new int [] {1,i}) == this.processID) {
				memory.write(i+2, processState, "Process" + getProcessID() +"STATE");
				break;
				}
		}
	}
	public int getInstructionsEnd() {
		return instructionsEnd;
	}
	public void setInstructionsEnd(int instructionsEnd) {
		this.instructionsEnd = instructionsEnd;
	}
	

}
