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
	
	public void setProcessed() {
		this.isprocessed = true;
	}

	@Override
	public void awaitProcessed() {
		while (!isprocessed) {
			try { Thread.sleep(1000); } catch (InterruptedException e) { };
		}
		
	}

}
