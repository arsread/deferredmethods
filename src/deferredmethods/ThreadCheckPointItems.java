package deferredmethods;

import java.util.LinkedList;
import java.util.PriorityQueue;

public class ThreadCheckPointItems {
	private final PriorityQueue<Integer> bufferHeap;
	private final LinkedList<GeneralProcessingCheckPoint> cpList;
	private int envCouter; 

	public ThreadCheckPointItems(){
		this.bufferHeap = new PriorityQueue<Integer>();
		this.cpList = new LinkedList<GeneralProcessingCheckPoint>();
		this.envCouter = 0;
	}

	public PriorityQueue<Integer> getBufferHeap(){
		return bufferHeap;
	}
	
	public LinkedList<GeneralProcessingCheckPoint> getCPList(){
		return cpList;
	}
	
	public int getEnvCouter(){
		return envCouter;
	}
	
	public void setEnvCouter(int value){
		envCouter = value;
	}
	
}
