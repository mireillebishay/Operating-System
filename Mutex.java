import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

public class Mutex {

  private boolean locked; // Whether the mutex is currently locked or not
  private Queue<MyProcess> blockedQ;
  public Mutex() {
    locked = false; // Initially unlocked
    blockedQ = new ArrayDeque<>();
  }

  // Acquire the lock on the mutex, blocking if it's already locked
  public static void semWait(Mutex m) throws Exception {
	  OS os = OS.getInstance(2);
	  MyProcess currentProcess = os.currentProcess;
    if(m.isLocked()) {
    	m.getBlockedQ().add(currentProcess);
    	os.blockedQueue.add(currentProcess); //addCurrentMyProcess, and general Queue
    	os.leftCyclesToSwap = 0;
    	currentProcess.getPcb().setProcessState(ProcessState.BLOCKEDMEMORY);
//    	currentProcess = null;
    	
    }
    else {
    	m.setLocked(true); // Lock the mutex
    	}
  }

  public void setLocked(boolean locked) {
	this.locked = locked;
}

// Release the lock on the mutex
  public static void semSignal(Mutex m) throws Exception {
	  MyProcess p;
	  OS os = OS.getInstance(2);
	  if(!m.getBlockedQ().isEmpty()) {
		 p = m.getBlockedQ().remove(); 
		 os.blockedQueue.remove(p);
		 os.readyQueue.add(p);
		 if(p.getPcb().getProcessState().equals(ProcessState.BLOCKEDDISK))
			 p.getPcb().setProcessState(ProcessState.READYONDISK);
		 else
			 p.getPcb().setProcessState(ProcessState.READYONMEMORY);
		 }//Remove from general queue
	  
	  else 
		  m.setLocked(false);
	}

	public Queue<MyProcess> getBlockedQ() {
		return blockedQ;
	}
	
	public boolean isLocked() {
		return locked;
	}

}
