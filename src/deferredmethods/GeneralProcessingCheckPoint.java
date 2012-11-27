package deferredmethods;

public class GeneralProcessingCheckPoint implements ProcessingCheckPoint {
	private final int bufferID;
	private boolean isprocessed;
	
	public GeneralProcessingCheckPoint(int id){
		this.bufferID = id;
		this.isprocessed = false;
		
	}
	
	public int getBufferID(){
		return bufferID;
	}
	
	@Override
	public boolean isProcessed() {
		return isprocessed;
	}
	
	public synchronized void setProcessed() {
		this.isprocessed = true;
		notifyAll();
	}

	@Override
	public synchronized void awaitProcessed() throws InterruptedException {
		while (!isprocessed) {
			wait();
		}
	}

}
