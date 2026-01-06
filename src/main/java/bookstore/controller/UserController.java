package bookstore.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import bookstore.bean.UserBean;
import bookstore.service.UsersService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

	@Autowired
	private UsersService userService;
	
	@GetMapping("/login")
	public String showLogin() {
		return "users/login";
	}
	
	// Vue 版本登入
	@PostMapping("/api/login")
    @ResponseBody 
    public Map<String, Object> doLoginApi(@RequestBody Map<String, String> loginData, HttpSession session) {
        
        String email = loginData.get("email");
        String password = loginData.get("password");
        Map<String, Object> response = new HashMap<>();
        
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            response.put("success", false);
            response.put("message", "信箱和密碼欄位皆不可為空！");
            return response;
        }
        
        UserBean user = userService.login(email, password);
        
        if (user != null) {
            if (user.getStatus() != null && user.getStatus().equals(2)) {
                response.put("success", false);
                response.put("message", "您的帳號已被停權，請聯繫管理員！");
                return response;
            }
            if (user.getUserType() == null || !user.getUserType().equals(0)) {
                response.put("success", false);
                response.put("message", "您沒有管理員權限！");
                return response;
            }
            
            session.setAttribute("user", user);
            response.put("success", true);
            response.put("message", "登入成功！");
            return response;
        } else {
            response.put("success", false);
            response.put("message", "帳號或密碼錯誤！");
            return response;
        }
    }
	
	@PostMapping("/login")
	public String doLogin(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
		
		if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
			model.addAttribute("loginMessage", "信箱和密碼欄位皆不可為空！");
			return "users/login";
		}
		
		UserBean user = userService.login(email, password);
		
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
	
	// Vue 版本查詢全部
	@GetMapping("/api/data/list")
	@ResponseBody 
	public List<UserBean> getUsersListApi(
	        @RequestParam(required = false) String searchName, 
	        @RequestParam(required = false) Integer userTypeFilter) {
	    
	    try {
	        System.out.println("====== API 成功觸發 ======");
	        String name = (searchName != null && !searchName.trim().isEmpty()) ? searchName : null;
	        
	        List<UserBean> users = userService.searchUsers(name, userTypeFilter);
	        System.out.println("成功查詢到 " + (users != null ? users.size() : 0) + " 筆資料");
	        return users;
	    } catch (Exception e) {
	        System.err.println("API 執行失敗，原因如下：");
	        e.printStackTrace(); 
	        return null;
	    }
	}
	
	@GetMapping("/users/list")
	public String listUsers(@RequestParam(required = false) String searchName, @RequestParam(required = false) Integer userTypeFilter, Model model) {
		List<UserBean> users = userService.searchUsers(searchName, userTypeFilter);
		
		model.addAttribute("users", users);
		model.addAttribute("currentSearchName", searchName);
		model.addAttribute("currentUserTypeFilter", userTypeFilter);
		
		return "users/UserList";
	}
	
	// Vue 版本查詢單筆
	@GetMapping("/api/data/get/{id}")
	@ResponseBody
	public UserBean getUserDetailApi(@PathVariable("id") Integer id) {
	    System.out.println("====== API 觸發：查詢 ID " + id + " ======");
	    try {
	        UserBean user = userService.findById(id);
	        if (user == null) {
	            System.out.println("找不到該會員！");
	            return null;
	        }
	        return user; // 只要你在 Bean 加上了 @JsonIgnoreProperties，這裡直接回傳即可
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	@GetMapping("/users/get")
	public String getUserById(@RequestParam("userId") Integer userId, Model model) {
		UserBean user = userService.findById(userId);
		model.addAttribute("user", user);
		return "users/GetUser";
	}
	
	// Vue 版本新增
	@PostMapping("/api/users/insert")
	@ResponseBody
	public Map<String, Object> processInsertApi(@RequestBody UserBean user) {
	    Map<String, Object> response = new HashMap<>();
	    
	    if (user.getEmail() == null || user.getEmail().isEmpty() || 
	        user.getUserPwd() == null || user.getUserPwd().isEmpty() || 
	        user.getUserName() == null || user.getUserName().isEmpty()) {
	        response.put("success", false);
	        response.put("message", "新增失敗！信箱、密碼與姓名必須填入！");
	        return response;
	    }

	    user.setStatus(1);
	    user.setPoints(0);
	    if (user.getUserType() == null) {
	        user.setUserType(1);
	    }

	    try {
	        userService.saveUser(user);
	        response.put("success", true);
	        response.put("message", "新增會員資料成功！");
	        response.put("user", user); // 回傳存好的資料讓 Vue 顯示
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("success", false);
	        response.put("message", "新增失敗！資料庫錯誤，請檢查 Email 是否重複！");
	    }
	    return response;
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
			userService.saveUser(user);
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
	
	// Vue 版本刪除
	@DeleteMapping("/api/users/delete/{id}") 
	@ResponseBody
	public Map<String, Object> deleteUserApi(@PathVariable("id") Integer id) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        userService.deleteUser(id);
	        response.put("success", true);
	        response.put("message", "會員資料刪除成功！");
	    } catch (org.springframework.dao.DataIntegrityViolationException e) {
	        response.put("success", false);
	        response.put("message", "刪除失敗！此會員目前仍有訂單或相關紀錄，無法刪除。");
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("success", false);
	        response.put("message", "刪除失敗：系統發生錯誤！");
	    }
	    return response;
	}
	
	
	@GetMapping("/users/delete")
	public String deleteUser(@RequestParam("userId") Integer userId, RedirectAttributes ra) {
		String message = "";
		
		try {
			userService.deleteUser(userId);
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
		UserBean user = userService.findById(userId);
		
		if (user != null) {
			model.addAttribute("user", user);
			return "users/UserUpdate";
		} else {
			return "redirect:/users/list?status=error&msg=找不到該會員資料！";
		}
	}
	
	// Vue 版修改
	@PutMapping("/api/data/update")
	@ResponseBody
	public Map<String, Object> processUpdateApi(@RequestBody UserBean user) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        // saveUser 在 Hibernate 裡通常是 saveOrUpdate，有 ID 就會跑更新
	        userService.saveUser(user);
	        response.put("success", true);
	        response.put("message", "會員資料更新成功！");
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("success", false);
	        response.put("message", "更新失敗：資料格式不正確或資料庫錯誤！");
	    }
	    return response;
	}
	
	@PostMapping("/users/update")
	public String processUpdate(@ModelAttribute UserBean user, RedirectAttributes ra) {
		String message = "";
		
		try {
			userService.saveUser(user);
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
