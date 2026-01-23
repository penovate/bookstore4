package bookstore.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import bookstore.bean.UserBean;
import bookstore.service.EmailService;
import bookstore.service.UsersService;
import bookstore.util.JwtUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
public class UserFrontController {
	
	private final UsersService userService;
	private final JwtUtil jwtUtil;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	
	@PostMapping("/api/user/google-login")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Map<String, Object> googleLogin(@RequestBody Map<String, String> data) {
		String accessToken = data.get("accessToken");
		Map<String, Object> response = new HashMap<>();
		
		try {
	        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + accessToken;
	        RestTemplate restTemplate = new RestTemplate();
	        Map<String, Object> googleUser = restTemplate.getForObject(userInfoUrl, Map.class);
	        
	        if (googleUser != null && googleUser.containsKey("email")) {
	            String email = (String) googleUser.get("email");
	            String name = (String) googleUser.get("name");
			
			UserBean user = userService.findByEmail(email);
			
			if (user != null) {
				String role = (user.getUserType() == 0) ? "SUPER_ADMIN" : (user.getUserType() == 1 ? "ADMIN" : "USER");
				String token = jwtUtil.generateToken(user.getUserId().toString(), role);
				
				response.put("success", true);
				response.put("token", token);
				response.put("userName", user.getUserName());
				response.put("role", role);
			} else {
				response.put("success", false);
				response.put("isNewUser", true);
				response.put("email", email);
				response.put("userName", name);
				response.put("message", "請完成基本資料設定！");
				}
	        }
			
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Google 驗證失敗");
		}
		return response;
	}
	
	@PostMapping("/api/user/login")
	@ResponseBody
	public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
	    Map<String, Object> response = new HashMap<>();
	    String email = loginData.get("email");
	    String rawPassword = loginData.get("userPwd");

	    UserBean user = userService.findByEmail(email);

	    if (user != null) {
	        if (passwordEncoder.matches(rawPassword, user.getUserPwd())) {
	            String role = (user.getUserType() == 0) ? "SUPER_ADMIN" : (user.getUserType() == 1 ? "ADMIN" : "USER");
	            String token = jwtUtil.generateToken(user.getUserId().toString(), role);

	            response.put("success", true);
	            response.put("token", token);
	            response.put("userName", user.getUserName());
	            response.put("role", role);
	            response.put("message", "歡迎回來！");
	        } else {
	            response.put("success", false);
	            response.put("message", "密碼錯誤！");
	        }
	    } else {
	        response.put("success", false);
	        response.put("message", "帳號不存在！");
	    }
	    return response;
	}
	
	@PostMapping("/api/user/register")
	@ResponseBody
	public Map<String, Object> userRegister(@RequestBody Map<String, Object> payload, HttpSession session){
		Map<String, Object> response = new HashMap<>();
		
		String email = (String) payload.get("email");
		String inputCode = (String) payload.get("verifyCode");
		
		String sessionCode = (String) session.getAttribute("verifyCode_" + email);
		if (sessionCode == null || !sessionCode.equals(inputCode)) {
			response.put("success", false);
			response.put("message", "驗證碼錯誤或已過期！");
			return response;
		}
		
		String phoneNum = (String) payload.get("phoneNum");
		Map<String, Object> uniqueCheck = userService.checkUserUnique(null, email, phoneNum);
		if (!(boolean) uniqueCheck.get("success")) {
			return uniqueCheck;
		}
		
		String userName = (String) payload.get("userName");
		String userPwd = (String) payload.get("userPwd");
		
		if (email == null || email.isEmpty() || userName == null || userName.isEmpty() ||
				userPwd == null || userPwd.isEmpty()) {
			response.put("success", false);
			response.put("message", "新增失敗！Email、姓名與密碼必須填入！");
			return response;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		UserBean user = new UserBean();
		user.setEmail(email);
		user.setUserName(userName);
		user.setUserPwd(passwordEncoder.encode(userPwd));
		user.setPhoneNum(phoneNum);
		String birthStr = (String) payload.get("birth");
		if (birthStr != null && !birthStr.isEmpty()) {
			try {
				user.setBirth(sdf.parse(birthStr));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			
		}
		user.setGender((String) payload.get("gender"));
		user.setAddress((String) payload.get("address"));
		
		Integer userType = (Integer) payload.get("userType");
		user.setUserType(userType == null ? 2 : user.getUserType());
	    user.setStatus(1);
	    user.setPoints(0);
	    
	    try {
			userService.saveUser(user);
			session.removeAttribute("verifyCode_" + email);
			response.put("success", true);
			response.put("message", "註冊成功！");
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("message", "註冊失敗！");
		}
		return response;
	}
	
	@PostMapping("/api/user/send-code")
	@ResponseBody
	public Map<String, Object> sendCode(@RequestBody Map<String, String> data, HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		String email = data.get("email");
		
		if (email == null || email.isEmpty()) {
			response.put("success", false);
			response.put("message", "請輸入電子信箱！");
			return response;
		}
		
		String code = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
		
		session.setAttribute("verifyCode_" + email, code);
		
		try {
			emailService.sendVerifyCode(email, code);
			response.put("success", true);
			response.put("message", "驗證碼已寄出！");
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("message", "郵件寄送失敗，請檢查設定！");
		}
		return response;
	}
}
