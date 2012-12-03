package deferredmethods;

import java.util.LinkedList;
import java.util.PriorityQueue;

public class ThreadCheckPointItems {
	private final PriorityQueue<Integer> bufferHeap;
	private final LinkedList<GeneralProcessingCheckPoint> cpList;
	private int envCouter; 
	private int maxBufferId;
	private boolean isEnd;

	public ThreadCheckPointItems(){
		this.bufferHeap = new PriorityQueue<Integer>();
		this.cpList = new LinkedList<GeneralProcessingCheckPoint>();
		this.envCouter = 0;
		this.maxBufferId = 0;
		this.isEnd = false;
	}

	public PriorityQueue<Integer> getBufferHeap(){
		return bufferHeap;
	}
	
	public LinkedList<GeneralProcessingCheckPoint> getCPList(){
		return cpList;
	}
	
//	public int getEnvCouter(){
//		return envCouter;
//	}
//	
//	public void setEnvCouter(int value){
//		envCouter = value;
//	}
//	
	public void setEnd(){
		this.isEnd = true;
	}
	
	public boolean canBeRemoved(){
		if (isEnd && maxBufferId == envCouter) {
			return true;
		}
		return false;
	}
	
	public void comfirmBuffer(int bufferId){
		synchronized (this) {
			if (bufferId == envCouter + 1) {
				envCouter++;
				while (!bufferHeap.isEmpty()
						&& bufferHeap.peek() == envCouter + 1) {
					envCouter++;
					bufferHeap.remove();
				}
				updateCheckPoints(cpList, envCouter);
			} else {
				bufferHeap.add(bufferId);
			}
		}
	}

	public void handInBuffer(int bufferId){
		maxBufferId = Math.max(bufferId, maxBufferId);
	}
	
	private void updateCheckPoints(
			LinkedList<GeneralProcessingCheckPoint> cpList, int envCouter) {
		synchronized (cpList) {
			while (!cpList.isEmpty()
					&& cpList.peek().getBufferID() <= envCouter) {
				cpList.peek().setProcessed();
				cpList.remove();
			}
		}
	}
	
}
