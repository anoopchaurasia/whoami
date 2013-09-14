package com.whoami;

public interface ResponseCollector {
	public void onResponse(String body, int code, String path);
}
