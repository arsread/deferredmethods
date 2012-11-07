package deferredmethods;

public interface ProcessingCheckPoint {
	
	public boolean isProcessed();
	
	public void awaitProcessed();
}
