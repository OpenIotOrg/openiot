/*
 * @author Prem Prakash Jayaraman
 * @email prem.jayaraman@csiro.au
 */
package org.openiot.ui.sensorschema.sensormodel;

/**
 * 
 */
public class Author {
	
	/**
	 * Author first name
	 */
	private String Firstname;
	
	/**
	 * Author Family Name
	 */
	private String Surname;
	
	/**
	 * Author Email
	 */
	private String Email;
	
	/**
	 * Author Homepage
	 */
	private String Homepage;
	
	/**
	 * Author Nick Name
	 */
	private String Nickname;
	
	/**
	 * Author Web Page Address
	 */
	private String Weblog;
	
	/**
	 * 
	 */
	public Author(){
		this.Firstname = "OpenIoT";
		this.Surname= "OpenIoT";
		this.Email= "openiot.eu";
		this.Homepage= "openiot.eu";
		this.Nickname= "openiot";
		this.Weblog= "openiot.eu";
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public String getFirstname() {
		return Firstname;
	}

	/**
	 * 
	 *
	 * @param firstname 
	 */
	public void setFirstname(String firstname) {
		Firstname = firstname;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public String getSurname() {
		return Surname;
	}

	/**
	 * 
	 *
	 * @param surname 
	 */
	public void setSurname(String surname) {
		Surname = surname;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public String getEmail() {
		return Email;
	}

	/**
	 * 
	 *
	 * @param email 
	 */
	public void setEmail(String email) {
		Email = email;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public String getHomepage() {
		return Homepage;
	}

	/**
	 * 
	 *
	 * @param homepage 
	 */
	public void setHomepage(String homepage) {
		Homepage = homepage;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public String getNickname() {
		return Nickname;
	}

	/**
	 * 
	 *
	 * @param nickname 
	 */
	public void setNickname(String nickname) {
		Nickname = nickname;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public String getWeblog() {
		return Weblog;
	}

	/**
	 * 
	 *
	 * @param weblog 
	 */
	public void setWeblog(String weblog) {
		Weblog = weblog;
	}
	
}
