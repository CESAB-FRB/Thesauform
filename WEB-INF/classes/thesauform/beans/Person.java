package thesauform.beans;

import java.util.Arrays;

public class Person {

	private String name;
	private String mail;
	private String password;
	private String right;
	private boolean authenticated;
	public static final String MISSING_NAME = "name is empty";
	public static final String MISSING_MAIL = "email is empty";
	public static final String MISSING_PASSWORD = "password is empty";
	public static final String MISSING_RIGHT = "right is empty";
	public static final String WRONG_RIGHT = "right is wrong";
	public static final String WRONG_MAIL = "email is wrong format";
	public static final String[] ARRAY_RIGHT = { "admin", "public", "expert" };

	private boolean valideMail(String mail) throws Exception {
		boolean returnVal = false;
		if (mail != null && !mail.isEmpty()) {
			if (!mail.matches("([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)")) {
				throw new Exception(WRONG_MAIL);
			} else {
				returnVal = true;
			}
		} else {
			throw new Exception(MISSING_MAIL);
		}
		return (returnVal);
	}

	private boolean valideRight(String right) throws Exception {
		boolean returnVal = false;
		if (right != null && !right.isEmpty()) {
			if (Arrays.asList(ARRAY_RIGHT).contains(right)) {
				returnVal = true;
			} else {
				throw new Exception(WRONG_RIGHT);
			}
		} else {
			throw new Exception(MISSING_RIGHT);
		}
		return (returnVal);
	}

	private boolean valideName(String name) throws Exception {
		boolean returnVal = false;
		if (name != null && !name.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_NAME);
		}
		return (returnVal);
	}

	private boolean validePassword(String password) throws Exception {
		boolean returnVal = false;
		if (password != null && !password.isEmpty()) {
			returnVal = true;
		} else {
			throw new Exception(MISSING_PASSWORD);
		}
		return (returnVal);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) throws Exception {
		try {
			if (valideName(name)) {
				this.name = name;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public String getMail() {
		return this.mail;
	}

	public void setMail(String mail) throws Exception {
		try {
			if (valideMail(mail)) {
				this.mail = mail;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) throws Exception {
		try {
			if (validePassword(password)) {
				this.password = password;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public String getRight() {
		return this.right;
	}

	public void setRight(String right) throws Exception {
		try {
			if (valideRight(right)) {
				this.right = right;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public boolean getAuthenticated() {
		return this.authenticated;
	}

	public void isAuthenticated() {
		this.authenticated = true;
	}
}
