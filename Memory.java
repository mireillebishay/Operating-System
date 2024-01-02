public class Memory {
    private Object[][] memory = new Object[2][40];
    private boolean[] isFree = new boolean[40];
    public boolean[] isValid = new boolean[40];
    
    public Memory() {
        // Initialize all memory blocks as free
        for (int i = 0; i < 40; i++) {
            isFree[i] = true;
        }
    }
    
    public Object read(int[] address) { //[1,2]
        return memory[address[0]][address[1]];
    }
    
    public void printMemory() {
        for (int i = 0; i < memory[0].length; i++) {
            System.out.print(memory[0][i] + ": " );
            System.out.print( memory[1][i]);
            System.out.println();
        }
    }
    
    
    public void write(int address, Object value,Object variableName) {
    	isValid[address] = true;
        memory[0][address] = variableName;
        memory[1][address] = value;
    }
    
    public boolean isFree(int lower,int upper) {
    	for(int i = lower;i<=upper && i<isFree.length;i++)
    		if(isFree[i] == false)
    			return false;
    	return true;
    }
    public boolean allocate(int lower, int upper) {
    	int i = lower;
        for (i = lower; i < isFree.length && i<=upper; i++) {
            if (isFree[i]) {
                isFree[i] = false;
            }
        }
        return i == upper+1;
    }
    
    public void deallocate(int l,int b ) {
        // Check if block index is within bounds of memory
        for(int i = l; i<=b;i++) {
//        	memory[0][i] = null;
//            memory[1][i] = null;
        	isValid[i] = false;
        	isFree[i] = true;
        }
    }
}
