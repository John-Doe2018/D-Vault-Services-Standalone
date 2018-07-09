package com.kirat.solutions.auth;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import com.kirat.solutions.util.FileItException;

public class FileItAuthentication {

	public static void checkCredentials(String userName, String password) throws FileItException {
		System.setProperty("java.security.auth.login.config",
				FileItAuthentication.class.getClassLoader().getResource("jaas.config").getPath());

		//String name = "Bikash";
		//String password = "Bikash@123";

		try {
			LoginContext lc = new LoginContext("FileItLogin", new FileItCallbackHandler(userName, password));
			lc.login();
		} catch (LoginException e) {
			throw new FileItException(e.getMessage());
		}
	}
}
