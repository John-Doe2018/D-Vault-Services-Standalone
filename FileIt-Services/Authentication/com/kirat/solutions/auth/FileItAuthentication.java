package com.kirat.solutions.auth;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public class FileItAuthentication {

	public static void main(String[] args) {
		System.setProperty("java.security.auth.login.config", "jaas.config");

		String name = "Bikash";
		String password = "Bikash@123";

		/*try {
			LoginContext lc = new LoginContext("Test", new TestCallbackHandler(name, password));
			lc.login();
		} catch (LoginException e) {
			e.printStackTrace();
		}
*/	}
}
	
