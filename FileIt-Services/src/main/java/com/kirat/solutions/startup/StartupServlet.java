package com.kirat.solutions.startup;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kirat.solutions.batch.BatchProcessor;
import com.kirat.solutions.util.ClassificationMapUtil;
import com.kirat.solutions.util.FileItException;

public class StartupServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public StartupServlet() {
		try {
			ClassificationMapUtil.getInstance().addMaptoContext();
			BatchProcessor.startProcess();
		} catch (FileItException e) {
			e.printStackTrace();
		}
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException{
		
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException{
		
	}
}
