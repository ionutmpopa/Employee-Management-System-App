package ro.sci.ems.domain;

import org.hibernate.validator.constraints.NotEmpty;

public class User extends AbstractModel {

    @NotEmpty(message = "E-mail cannot be empty!")
	private String email;

    @NotEmpty(message = "Password cannot be empty!")
	private String password;

    @NotEmpty(message = "First name cannot be empty!")
	private String firstName;

    @NotEmpty(message = "Last name cannot be empty!")
	private String lastName;

	private boolean enabled;


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public boolean setEnabled(boolean value) {
		this.enabled = value;
		return true;
	}
}
