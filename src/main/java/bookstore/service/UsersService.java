package bookstore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bookstore.bean.UserBean;
import bookstore.repository.UserRepository;

@Service
@Transactional
public class UsersService {

	@Autowired
	private UserRepository userRepo;
	
	public UserBean login(String email, String password) {
		if (email == null || password == null) return null;
		return userRepo.findByEmailAndUserPwd(email, password);
	}
	
	public List<UserBean> findAllUsers() {
		return userRepo.findAll();
	}
	
	public List<UserBean> searchUsers(String keyword, Integer type) {
	    if ((keyword == null || keyword.trim().isEmpty()) && type == null) {
	        return userRepo.findAll();
	    }
	    
	    return userRepo.searchUsersGlobal(keyword, type);
	}
	
	public UserBean findById(Integer id) {
		return userRepo.findById(id).orElse(null);
	}
	
	public UserBean saveUser(UserBean user) {
		return userRepo.save(user);
	}
	
	public void deleteUser(Integer id) {
		userRepo.deleteById(id);
	}
	
	public void updateStatus(Integer id, Integer status) {
		UserBean user = userRepo.findById(id).orElse(null);
		if (user != null) {
			user.setStatus(status);
			userRepo.save(user);
		}
	}
	
	
}
