package com.kirat.solutions.domain;

import java.util.List;

public class AddFileRequest extends Response {
	private String binderName;
	private List<BookRequest> oBookRequests;

	public String getBinderName() {
		return binderName;
	}

	public void setBinderName(String binderName) {
		this.binderName = binderName;
	}

	public List<BookRequest> getoBookRequests() {
		return oBookRequests;
	}

	public void setoBookRequests(List<BookRequest> oBookRequests) {
		this.oBookRequests = oBookRequests;
	}

}
