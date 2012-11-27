package deferredmethods;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class ThreadCheckPointItems {
	private final HashMap<Long, PriorityQueue<Integer>> bufferHeap;
	private final HashMap<Long, LinkedList<GeneralProcessingCheckPoint>> cpList;
	private final HashMap<Long, Integer> envCouter; 

	public ThreadCheckPointItems(){
		this.bufferHeap = new HashMap<Long, PriorityQueue<Integer>>();
		this.cpList = new HashMap<Long, LinkedList<GeneralProcessingCheckPoint>>();
		this.envCouter = new HashMap<Long, Integer>();
	}

	public PriorityQueue<Integer> getBufferHeap(long threadID){
		if (!bufferHeap.containsKey(threadID)){
			bufferHeap.put(threadID, new PriorityQueue<Integer>());
		}
		return bufferHeap.get(threadID);
	}
	
	public LinkedList<GeneralProcessingCheckPoint> getCPList(long threadID){
		if (!cpList.containsKey(threadID)){
			cpList.put(threadID, new LinkedList<GeneralProcessingCheckPoint>());
		}
		return cpList.get(threadID);
	}
	
	public int getEnvCouter(long threadID){
		if (!envCouter.containsKey(threadID)){
			envCouter.put(threadID, 0);
		}
		return envCouter.get(threadID);
	}
	
	public void increseEnvCouter(long threadID){
		int couter = envCouter.get(threadID);
		envCouter.put(threadID, couter+1);
	}
	
}
