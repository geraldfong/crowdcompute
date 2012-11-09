package com.crowdcompute;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Job {
	
	private int NUM_CHUNKS = 100;
	
	private List<Chunk> chunks;

	public Job() {
		chunks = new ArrayList<Chunk>();
		for (int i = 0; i < NUM_CHUNKS; i++) {
			Chunk chunk = new Chunk(i);
			chunks.add(i, chunk);
		}
	}
	
	public synchronized int getNextId() {
		Chunk latestAssignedChunk = null;
		int start = (int)(chunks.size()*Math.random());
		//for (Chunk chunk : chunks) {
		for(int i = start+1; i != start; i++ ) {
			i %= chunks.size();
			Chunk chunk = chunks.get(i);
			
			if (chunk.isCompleted) {
				continue;
			}
			if(!chunk.isAssigned) {
				chunk.isAssigned = true;
				chunk.assignDate = new Date();
				return chunk.id;
			} 
			if (latestAssignedChunk == null) {
				latestAssignedChunk = chunk;
			} else if (latestAssignedChunk.assignDate.getTime() > chunk.assignDate.getTime()) {
				latestAssignedChunk = chunk;
			}
		}
		if (latestAssignedChunk == null) {
			return -1;
		}
		latestAssignedChunk.assignDate = new Date();
		return latestAssignedChunk.id;
	}
	
	public synchronized void setResult(int id, String data) throws Exception{
		if ( id < 0 || id >= NUM_CHUNKS) {
			throw new Exception("Bad ID: " + id);
		}
		
		Chunk chunk = chunks.get(id);
		chunk.data = data;
		chunk.isCompleted = true;
	}
	
	public synchronized void printData(PrintWriter writer) {
		String data = "[";
		for (Chunk chunk : chunks) {
			data += chunk.data + ",";
		}
		data = data.substring(0, data.length() - 1) + "]";
		writer.print(data);
	}
	
	public synchronized void printChunk(PrintWriter writer, int id) throws Exception{
		if ( id < 0 || id >= NUM_CHUNKS) {
			throw new Exception("Bad ID: " + id);
		}
		writer.print(chunks.get(id).data);
	}
	
	public synchronized void printStatus(PrintWriter writer) {
		
	}
	
	public synchronized void printChunks(PrintWriter writer, Set<Integer> ids) {
		StringBuilder data = new StringBuilder( "[" );
		for (int i = 0; i < NUM_CHUNKS; i++) {
			if (ids.contains(i)){
				data.append(chunks.get(i).data);
			} else {
				data.append("\"\"");
			}
			data.append(',');
		}
		data.setCharAt(data.length()-1, ']');
		writer.print(data.toString());
	}
}
