package org.openiot.ld4s.lod_cloud;

import org.apache.commons.codec.digest.DigestUtils;

public class Person {

	private String firstname = null;

	private String uri = null;

	private String surname = null;

	private String nickname = null;

	private String email = null;
	
	private String email_sha1 = null;

	private String homepage = null;

	private String weblog = null;

	public Person(){
		new Person(null, null, null, null, null, null, null);
	}
	public Person(String firstname, String surname, String nickname, String email,
			String homepage, String weblog, String uri){
		setFirstname(firstname);
		setSurname(surname);
		setNickname(nickname);
		setEmail(email);
		setHomepage(homepage);
		setWeblog(weblog);
		setUri(uri);
	}

	public void setFirstname(String firstname) {
		if (firstname != null && firstname.charAt(0) != -1){
			this.firstname = Character.toUpperCase(firstname.charAt(0))+firstname.substring(1);
		}
	}

	public String getFirstname() {
		return firstname;
	}

	public void setSurname(String surname) {
		if (surname != null){
			this.surname = Character.toUpperCase(surname.charAt(0))+surname.substring(1);
		}
	}

	public String getSurname() {
		return surname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getNickname() {
		return nickname;
	}

	public void setEmail(String email) {
		if (email != null && email.trim().compareTo("") != 0){
			this.email = email;
			this.email_sha1 = DigestUtils.shaHex(this.email);
		}
	}

	public String getEmail() {
		return email;
	}
	
	public String getEmailSha1() {
		return email_sha1;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setWeblog(String weblog) {
		this.weblog = weblog;
	}

	public String getWeblog() {
		return weblog;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getUri() {
		return uri;
	}


}
