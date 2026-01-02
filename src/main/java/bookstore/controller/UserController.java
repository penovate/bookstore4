package bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import bookstore.bean.UserBean;
import bookstore.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

	@Autowired
	private UserRepository userRepo;
	
	@GetMapping("/login")
	public String showLogin() {
		return "users/login";
	}
	
	@PostMapping("/login")
	public String doLogin(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
		
		if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
			model.addAttribute("loginMessage", "信箱和密碼欄位皆不可為空！");
			return "users/login";
		}
		
		UserBean user = userRepo.findByEmailAndUserPwd(email, password);
		
		if (user != null) {
			if (user.getStatus() != null && user.getStatus().equals(2)) {
                model.addAttribute("loginMessage", "您的帳號已被停權，請聯繫管理員！");
                return "users/login";
            }
            
            if (user.getUserType() == null || !user.getUserType().equals(0)) {
                model.addAttribute("loginMessage", "您沒有權限進入後台管理系統，請使用管理員帳號登入！");
                return "users/login";
            }
			session.setAttribute("user", user);
			return "redirect:/users/home.jsp";
		} else {
			model.addAttribute("loginMessage", "帳號或密碼錯誤！");
			return "users/login";
		}
	}
	
	@GetMapping("/logout")
	public String doLogout(HttpSession session) {
		
		if (session != null) {
			session.invalidate();
		}
		
		return "redirect:/login?logout=true";
	}
	
	@GetMapping("users/home.jsp")
	public String showHome(HttpSession session, HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		return "users/home";
	}
	
	@GetMapping("/users/list")
	public String listUsers(@RequestParam(required = false) String searchName, @RequestParam(required = false) Integer userTypeFilter, Model model) {
		List<UserBean> users;
		
		boolean hasName = (searchName != null && !searchName.trim().isEmpty());
		boolean hasType = (userTypeFilter != null);
		
		if (hasName && hasType) {
			users = userRepo.findByUserNameContainingAndUserType(searchName.trim(), userTypeFilter);
		} else if (hasName) {
			users = userRepo.findByUserNameContaining(searchName.trim());
		} else if (hasType) {
			users = userRepo.findByUserType(userTypeFilter);
		} else {
			users = userRepo.findAll();
		}
		
		model.addAttribute("users", users);
		model.addAttribute("currentSearchName", searchName);
		model.addAttribute("currentUserTypeFilter", userTypeFilter);
		
		return "users/UserList";
	}
	
	@GetMapping("/users/get")
	public String getUserById(@RequestParam("userId") Integer userId, Model model) {
		UserBean user = userRepo.findById(userId).orElse(null);
		model.addAttribute("user", user);
		return "users/GetUser";
	}
	
	@PostMapping("/users/insert")
	public String processInsert(@ModelAttribute UserBean user, Model model) {
		
		if (user.getEmail() == null || user.getEmail().isEmpty() || user.getUserPwd() == null || user.getUserPwd().isEmpty() || user.getUserName() == null || user.getUserName().isEmpty()) {
			model.addAttribute("message", "新增失敗！信箱、密碼與姓名必須填入！");
			return "users/UserInsert";
		}
		
		if (user.getPhoneNum() != null && !user.getPhoneNum().trim().isEmpty()) {
			if (!user.getPhoneNum().matches("09\\d{8}")) {
				model.addAttribute("message", "新增失敗！電話號碼必須是以 09 開頭的 10 個數字！");
				return "users/UserInsert";
			}
		}
		
		user.setStatus(1);
		user.setPoints(0);
		if (user.getUserType() == null) {
			user.setUserType(1);
		}
		
		try {
			userRepo.save(user);
			model.addAttribute("user", user);
			model.addAttribute("message", "新增會員資料成功！");
			return "users/UserInsertFinish";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "新增失敗！資料庫寫入錯誤，請檢察 Email 是否重複！");
			return "users/UserInsert";
		}
	}
	
	@GetMapping("/users/insert")
	public String showInsertForm() {
	    return "users/UserInsert";
	}
	
	@GetMapping("/users/delete")
	public String deleteUser(@RequestParam("userId") Integer userId, RedirectAttributes ra) {
		String message = "";
		
		try {
			userRepo.deleteById(userId);
			message = "會員資料刪除成功！";
		} catch (org.springframework.dao.EmptyResultDataAccessException e) {
	        message = "刪除失敗！查無此會員。";
	    } catch (org.springframework.dao.DataIntegrityViolationException e) {
	        message = "刪除失敗！此會員目前仍有訂單或評價等資料紀錄，無法刪除！";
	    } catch (Exception e) {
	        message = "刪除失敗：系統發生錯誤！";
	        e.printStackTrace();
	    }
		
		ra.addAttribute("status", "success");
	    ra.addAttribute("msg", message);
	    
	    return "redirect:/users/list";
	}

	@GetMapping("/users/update")
	public String showUpdatePage(@RequestParam("userId") Integer userId, Model model) {
		UserBean user = userRepo.findById(userId).orElse(null);
		
		if (user != null) {
			model.addAttribute("user", user);
			return "users/UserUpdate";
		} else {
			return "redirect:/users/list?status=error&msg=找不到該會員資料！";
		}
	}
	
	@PostMapping("/users/update")
	public String processUpdate(@ModelAttribute UserBean user, RedirectAttributes ra) {
		String message = "";
		
		try {
			userRepo.save(user);
			message = "會員資料更新成功！";
		} catch (Exception e) {
			e.printStackTrace();
			message = "更新失敗：資料格式不正確或資料庫錯誤！";
		}
		
		ra.addAttribute("status", "success");
	    ra.addAttribute("msg", message);
	    return "redirect:/users/list";
	}
}
