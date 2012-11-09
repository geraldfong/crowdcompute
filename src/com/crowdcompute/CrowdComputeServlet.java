
package com.crowdcompute;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.*;

@SuppressWarnings( "serial" )
public class CrowdComputeServlet extends HttpServlet {
	
	private Map<String, Job> jobs;
	private Map<String, Long> activity;
	
	public CrowdComputeServlet() {
		jobs = new HashMap<String, Job>();
		Job mandJob = new Job();
		Job pentagonalJob = new Job();
		Job juliaJob = new Job();
		jobs.put("mandelbrot", mandJob);
		jobs.put("pentagonal", pentagonalJob);
		jobs.put("julia", juliaJob);
		activity = new HashMap<String,Long>();
	}
	
	public void doGet( HttpServletRequest req, HttpServletResponse resp) throws IOException {
		System.out.println(req.getLocalAddr());
		try {
			resp.setContentType( "text/plain" );
			PrintWriter writer = resp.getWriter();
			
			
			String method = req.getParameter("method");
			String jobId = req.getParameter("jobId");
			String chunkIdString = req.getParameter("chunkId");
			String data = req.getParameter("data");


			if( method == null) {
				writer.println( "Hello, you have not specified a method, ie 'getChunkId', 'result'");
				return;
			}
			
			
			if (jobId == null) {
				writer.println("You have not specified a jobId, ie 'mandlebrot'");
				return;
			}
			if (!jobs.containsKey(jobId)) {
				writer.println("I do not have the jobId: " + jobId);
				return;
			}
			
			Job job = jobs.get(jobId);
			
			if (method.equals("getChunkId")) {
				int chunkId = job.getNextId();
				writer.print(chunkId);
				System.out.println("Sending chunk: " + chunkId);
			} else if (method.equals("result")){
				if (chunkIdString == null) {
					writer.println("You have not specified chunkid");
					return;
				} else if (data == null) {
					writer.println("You have not specified data");
				}
				
				int chunkId = Integer.parseInt(chunkIdString);
				job.setResult(chunkId, data);
				job.printData(writer);
			} else if (method.equals("getChunk")){
				if (chunkIdString == null) {
					writer.println("You have not specified chunkid");
					return;
				}
				
				int chunkId = Integer.parseInt(chunkIdString);
				job.printChunk(writer, chunkId);
			} else if (method.equals("getResult")) {
				job.printData(writer);
			} else if (method.equals("getStatus")) {
				job.printStatus(writer);
			} else {
				writer.println("I do not knowthe method you specified");
			}
			
		} catch( Exception e) {
			e.printStackTrace();
		}
	}
	public void doPost( HttpServletRequest req, HttpServletResponse resp ) throws IOException {
		try {
			resp.setContentType( "text/plain" );
			PrintWriter writer = resp.getWriter();
			
			
			String method = req.getParameter("method");
			String jobId = req.getParameter("jobId");
			String chunkIdString = req.getParameter("chunkId");
			String data = req.getParameter("data");
			String chunkIdsString = req.getParameter("chunkIds");

			if( method == null) {
				writer.println( "Hello, you have not specified a method, ie 'getChunkId', 'result'");
				return;
			}
			
			if (jobId == null) {
				writer.println("You have not specified a jobId, ie 'mandelbrot'");
				return;
			}
			if (!jobs.containsKey(jobId)) {
				writer.println("I do not have the jobId: " + jobId);
				return;
			}
			
			Job job = jobs.get(jobId);
			
			if (method.equals("getChunkId")) {
				String guuid = req.getParameter("guuid");
				long time = System.currentTimeMillis();
				activity.put(guuid + "," + jobId, time);
				System.out.println(guuid);
				int chunkId = job.getNextId();
				writer.print(chunkId);
				System.out.println("Sending chunk: " + chunkId);
			} else if (method.equals("result")){
				if (chunkIdString == null) {
					writer.println("You have not specified chunkid");
					return;
				} else if (data == null) {
					writer.println("You have not specified data");
					return;
				}
				
				int chunkId = Integer.parseInt(chunkIdString);
				job.setResult(chunkId, data);
				job.printData(writer);
			} else if (method.equals("getResult")) {
				job.printData(writer);
			} else if (method.equals("getChunks")) {
				if (chunkIdsString == null || "".equals(chunkIdsString)) {
					writer.println("You have not specified chunkids");
					return;
				}
				String[] chunkIdsStringArray = chunkIdsString.split(",");
				Set<Integer> chunkIds = new HashSet<Integer>();
				for (String chunkIdStringValue : chunkIdsStringArray) {
					chunkIds.add(Integer.parseInt(chunkIdStringValue));
				}
				job.printChunks(writer, chunkIds);
			} else if(method.equals("getCrowdSize")) {
				long cur = System.currentTimeMillis();
				System.out.println(activity);
				System.out.println(cur);
				int count = 0;
				LinkedList<String> inactive = new LinkedList<String>();
				
				for(String key : activity.keySet()) {
					long timestamp = activity.get(key);
					if( cur-timestamp < 1000*60 ) {
						if( key.endsWith(jobId) ) {
							count++;
						}
					} else {
						inactive.add(key);
					}
				}
				
				for( String key : inactive ) {
					activity.remove(key);
				}
				System.out.println(count);
				writer.print(count);
			} else {
				writer.println("I do not know the method you specified");
			}
			
		} catch( Exception e) {
			e.printStackTrace();
		}
	}
}