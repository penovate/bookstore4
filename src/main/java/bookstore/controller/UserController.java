package bookstore.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import bookstore.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
public class UserController {

	private final UsersService userService;
	
	private final JwtUtil jwtUtil;
	
	@GetMapping("/login")
	public String showLogin() {
		return "users/login";
	}
	
	// Vue 版本登入
	@PostMapping("/api/login")
    @ResponseBody 
    public Map<String, Object> doLoginApi(@RequestBody Map<String, String> loginData) {
        
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
            
            if (user.getUserType() != null && user.getUserType().equals(2)) {
            	response.put("success", false);
            	response.put("message", "您沒有權限進入後台管理系統！");
            	return response;
            }
            
            String role;
            Integer type = user.getUserType();
            
            if (type == 0) {
            	role = "SUPER_ADMIN";
            } else if (type == 1) {
            	role = "ADMIN";
            } else {
            	role = "USER";
            }
            
            String token = "";
			try {
				token = jwtUtil.generateToken(user.getUserId().toString(), role);
			} catch (Exception e) {
				e.printStackTrace();
				response.put("success", false);
				response.put("message", "系統生成證件失敗！");
				return response;
			}
            response.put("success", true);
            response.put("message", "登入成功！");
            response.put("token", token);
            response.put("role", role);
            response.put("userName", user.getUserName());
            response.put("userId", user.getUserId());
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
                model.addAttribute("loginMessage", "您沒有權限進入後台管理系統！");
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
	        @RequestParam(required = false) String keyword,
	        @RequestParam(required = false) Integer userTypeFilter) {
	    
	    try {
	        System.out.println("====== API 成功觸發 ======");
	        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword : null;
	        
	        List<UserBean> users = userService.searchUsers(cleanKeyword, userTypeFilter);
	        System.out.println("成功查詢到 " + (users != null ? users.size() : 0) + " 筆資料");
	        return users;
	    } catch (Exception e) {
	        System.err.println("API 執行失敗，原因如下：");
	        e.printStackTrace(); 
	        return null;
	    }
	}
	
//	@GetMapping("/users/list")
//	public String listUsers(@RequestParam(required = false) String searchName, @RequestParam(required = false) Integer userTypeFilter, Model model) {
//		List<UserBean> users = userService.searchUsers(searchName, userTypeFilter);
//		
//		model.addAttribute("users", users);
//		model.addAttribute("currentSearchName", searchName);
//		model.addAttribute("currentUserTypeFilter", userTypeFilter);
//		
//		return "users/UserList";
//	}
	
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
	        return user;
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
		Map<String, Object> uniqueCheck = userService.checkUserUnique(null, user.getEmail(), user.getPhoneNum());
		if (!(boolean) uniqueCheck.get("success")) {
			return uniqueCheck;
		}
		
	    Map<String, Object> response = new HashMap<>();
	    
	    if (user.getEmail() == null || user.getEmail().isEmpty() || 
	        user.getUserPwd() == null || user.getUserPwd().isEmpty() || 
	        user.getUserName() == null || user.getUserName().isEmpty()) {
	        response.put("success", false);
	        response.put("message", "新增失敗！信箱、密碼與姓名必須填入！");
	        return response;
	    }
	    
	    if (user.getUserType() != null && user.getUserType() == 2) {
	        response.put("success", false);
	        response.put("message", "新增失敗！後台系統禁止直接建立一般會員，請引導使用者至前台註冊。");
	        return response;
	    }

	    if (user.getUserType() == null) {
	        user.setUserType(1); 
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
	        response.put("user", user);
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("success", false);
	        response.put("message", "新增失敗！資料庫錯誤，請檢查 Email 是否重複！");
	    }
	    return response;
	}
	
	@GetMapping("/api/users/check-unique")
	@ResponseBody
	public Map<String, Object> checkUniqueApi(@RequestParam(required = false) Integer userId, 
			@RequestParam(required = false) String email, @RequestParam(required = false) String phoneNum) {
		return userService.checkUserUnique(userId, email, phoneNum);
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
	
	// Vue 版本軟刪除（啟用/停權）
	@PutMapping("/api/data/status/{id}")
	@ResponseBody
	public Map<String, Object> toggleUserStatus(@PathVariable("id") Integer id, @RequestBody Map<String, Integer> payload, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Integer newStatus = payload.get("status");
			
			String authHeader = request.getHeader("Authorization");
			String token = authHeader.substring(7);
			String currentOperUserId = jwtUtil.getMemberId(token);
			
			if (id.toString().equals(currentOperUserId) && newStatus != null && newStatus == 2) {
	            response.put("success", false);
	            response.put("message", "更新失敗：您無法停權自己的帳號！");
	            return response;
	        }
			
			if (newStatus == null) {
				response.put("success", false);
				response.put("message", "狀態碼不可為空！");
				return response;
			}
			userService.updateStatus(id, newStatus);
			response.put("success", true);
			String action = (newStatus == 2) ? "停權" : "啟用";
			response.put("message", "已成功將會員" + action + "！");
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("message", "狀態變更失敗！系統錯誤");
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
	public Map<String, Object> processUpdateApi(@RequestBody UserBean user, HttpServletRequest request) {
	    Map<String, Object> response = new HashMap<>();
	    
	    Map<String, Object> uniqueCheck = userService.checkUserUnique(user.getUserId(), user.getEmail(), user.getPhoneNum());
	    if (!(boolean) uniqueCheck.get("success")) {
	        return uniqueCheck;
	    }
	    
	    try {
			String authHeader = request.getHeader("Authorization");
			String token = authHeader.substring(7);
			String currentUserId = jwtUtil.getMemberId(token);
			String currentRole = jwtUtil.getRole(token);
			
			UserBean existingUser = userService.findById(user.getUserId());
	        if (existingUser == null) {
	            response.put("success", false);
	            response.put("message", "更新失敗：找不到該會員！");
	            return response;
	        }
			
	        if ("ADMIN".equals(currentRole)) {
	        	if ((existingUser.getUserType() == 0 || existingUser.getUserType() == 1) 
	                    && !existingUser.getUserId().toString().equals(currentUserId)) {
	                    response.put("success", false);
	                    response.put("message", "權限不足：您無法修改其他管理員的資料！");
	                    return response;
	                }

	        	if (user.getUserType() != null && user.getUserType() != existingUser.getUserType()) {
	                if (existingUser.getUserType() == 2 && (user.getUserType() == 0 || user.getUserType() == 1)) {
	                    response.put("success", false);
	                    response.put("message", "權限不足：一般管理員無法提升會員權限！");
	                    return response;
	                }
	                if ((existingUser.getUserType() == 0 || existingUser.getUserType() == 1) && user.getUserType() == 2) {
	                    response.put("success", false);
	                    response.put("message", "權限不足：您無法將管理員降級為一般會員！");
	                    return response;
	                }
	            }
	        }
	            
	        if (user.getUserPwd() == null || user.getUserPwd().trim().isEmpty()) {
	            user.setUserPwd(existingUser.getUserPwd());
	        }

	        if (user.getPoints() == null) {
	            user.setPoints(existingUser.getPoints());
	        }
	        if (user.getStatus() == null) {
	            user.setStatus(existingUser.getStatus());
	        }

	        userService.saveUser(user);
	        
	        response.put("success", true);
	        response.put("message", "會員資料更新成功！");    
	            
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("success", false);
	        response.put("message", "更新失敗：伺服器發生錯誤！");
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
