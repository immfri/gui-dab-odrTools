package processing;

public class ProcessEndNotifier extends Thread {
	 
	private ProcessManager pm;
    private Process process;
    private String command;

    
    public ProcessEndNotifier(ProcessManager pm, Process process, String command) {
    	
    	this.pm = pm;
    	this.process = process;  
        this.command = command;
        
        this.start();
    }

    @Override
    public void run() {
    	
    	try {
    		process.waitFor();
		} 
    	catch (InterruptedException e) {
			pm.cancel(process, command);
		}
    	
    	pm.cancel(process, command);
    }    
}
