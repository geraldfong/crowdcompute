package com.crowdcompute;

import java.util.Date;

public class Chunk {
	public boolean isAssigned;
	public boolean isCompleted;
	public Date assignDate;
	public int id;
	public String data;
	
	public Chunk(int id) {
		this.id = id;
		isAssigned = false;
		isCompleted = false;
		data = "\"\"";
	}
}
