package com.oppsci.ngraphstore.web.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oppsci.ngraphstore.web.role.Role;
import com.oppsci.ngraphstore.web.role.RoleDAO;

@Service("userService")
public class UserService implements UserDetailsService{

	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private RoleDAO roleDAO;

	
	public int updateUser(User user) {
		return userDAO.updateUser(user);
	}

	public int deleteUser(int id) {
		return userDAO.deleteUser(id);
	}

	
	public User getUserByName(String name) {
		return userDAO.getUserByName(name);
	}

	public User getUserByID(int id) {
		return userDAO.getUserByID(id);
	}

	public List<User> getAllUsers() {
		return userDAO.getAllUsers();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = getUserByName(username);
		List<Role> roles = roleDAO.getUserRoles(user.getId());
		Collection<SimpleGrantedAuthority> authorities = new HashSet<SimpleGrantedAuthority>();
		for (int i = 0; i < roles.size(); i++) {
			Role role = roles.get(i);
			authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
		}
		return new org.springframework.security.core.userdetails.User(username, user.getPassword(),
				authorities);
	}

	public int addUser(String userName, String rawPassword, PasswordEncoder encoder) {
		return userDAO.addUser(userName, encoder.encode(rawPassword));
	}
}
