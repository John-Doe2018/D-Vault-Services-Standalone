package com.kirat.solutions.domain;

import org.json.simple.JSONObject;

public class SearchBookResponse extends Response{

	JSONObject jsonObject;

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}
}
