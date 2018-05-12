package com.kirat.solutions.domain;

public class UpdateBookRequest extends Response{
	
	private String id;
	private String htmlContent;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getHtmlContent() {
		return htmlContent;
	}
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
	

}
