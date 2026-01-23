package bookstore.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bookstore.bean.UserBean;
import bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UsersService {

	private final UserRepository userRepo;
	
	// 後台會員系統
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
	
	public void updateStatus(Integer id, Integer status) {
		UserBean user = userRepo.findById(id).orElse(null);
		if (user != null) {
			user.setStatus(status);
			userRepo.save(user);
		}
	}
	
	public Map<String, Object> checkUserUnique(Integer userId, String email, String phoneNum) {
		Map<String, Object> result = new HashMap<>();
		result.put("success", true);
		
		if (userId == null) {
			if (userRepo.existsByEmail(email)) {
				result.put("success", false);
				result.put("message", "該 Email 已被註冊！");
				return result;
			}
			if (phoneNum != null && !phoneNum.isEmpty() && userRepo.existsByPhoneNum(phoneNum)) {
				result.put("success", false);
				result.put("message", "該手機號碼已被使用！");
				return result;
			}
		} else {
			if (userRepo.existsByEmailAndUserIdNot(email, userId)) {
				result.put("success", false);
				result.put("message", "Email 已被其他會員使用！");
				return result;
			}
			if (phoneNum != null && !phoneNum.isEmpty() && userRepo.existsByPhoneNumAndUserIdNot(phoneNum, userId)) {
				result.put("success", false);
				result.put("message", "手機號碼已被其他會員使用！");
				return result;
			}
		}
		return result;
	}
	
	// 前台書店系統
	
	public UserBean findByEmail(String email) {
		return userRepo.findByEmail(email);
	}
	
}
