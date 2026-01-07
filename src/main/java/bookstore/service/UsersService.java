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
	
	public List<UserBean> searchUsers(String name, Integer type) {
		boolean hasName = (name != null && !name.trim().isEmpty());
		boolean hasType = (type != null);
		
		if (hasName && hasType) {
			return userRepo.findByUserNameContainingAndUserType(name, type);
		} else if (hasName) {
			return userRepo.findByUserNameContaining(name);
		} else if (hasType) {
			return userRepo.findByUserType(type);
		} else {
			return userRepo.findAll();
		}
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
