package org.openiot.security.mgmt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.openiot.lsm.security.oauth.mgmt.User;

@SessionScoped
@ManagedBean(name = "userTable")
public class UserManagmentService implements Serializable {

	private static final long serialVersionUID = -1181447616733794557L;
	
	private List<User> users;
	private User selectedUser;

	/**********************
	 * for multi select table
	 **********************/
	private User[] selectedUsers;

	public UserManagmentService() {
		users = new ArrayList<User>();

		// initial with DB
		users.add(createUser(0, "dawood", "daw@yahoo.com", "dawood"));
		users.add(createUser(1, "dawoo", "da@yahoo.com", "dawoo"));
		users.add(createUser(2, "dawo", "d@yahoo.com", "dawo"));
		users.add(createUser(3, "daw", "@yahoo.com", "daw"));
	}

	private User createUser(long id, String name, String email, String username){
		User user = new User();
		user.setName(name);
		user.setEmail(email);
		user.setUsername(username);
		user.setId(id);
		
		return user;
	}
	
	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
	}

	public User getSelectedUser() {
		return selectedUser;
	}

	public List<User> getUsers() {
		return users;
	}

	public User[] getSelectedUsers() {
		return selectedUsers;
	}

	// ////////////////////////
	// ////////////////////////

	public void addColumn() {
		users.add(createUser(5, "daw", "w", "d"));
		// TODO use customDilalogBox here
		// TODO add user to DB
	}

	public void removeColumn() {
		if (users.contains(selectedUser))
			;
		users.remove(selectedUser);
		// TODO remove user From DB
	}

	public String editColumn() {
//		RoleManagmentService managmentService = new RoleManagmentService();
//		managmentService.addRoles(selectedUser.getRoles());
		return "userRole";
		// TODO use customDialog here
		// TODO update DB here
	}

	public String manageRoles() {
		// TODO set Role for this user
		// and then go to below link
		return "userRoles";
	}
	// /////////////////////////
	// ////////////////////////
}
