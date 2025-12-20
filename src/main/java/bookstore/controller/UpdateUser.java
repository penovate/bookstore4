package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Date;

import bookstore.bean.UserBean;
import bookstore.dao.impl.UsersDAOImpl;

@WebServlet("/UpdateUser")
public class UpdateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String userIdStr = request.getParameter("user_id");
		if (userIdStr == null || userIdStr.isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/GetAllUsers?error=缺少會員ID");
			return;
		}
		
		UsersDAOImpl dao = new UsersDAOImpl();
		
		Integer userId = Integer.parseInt(userIdStr);
		UserBean user = dao.selectUserById(userId);
		if (user != null) {
			request.setAttribute("user", user);
			request.getRequestDispatcher("/users/UserUpdate.jsp").forward(request, response);
		} else {
			response.sendRedirect(request.getContextPath() + "/GetAllUsers?error=找不到該會員資料！");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
        String message = "";
        String userIdStr = request.getParameter("user_id"); 
        String email = request.getParameter("email");
        String password = request.getParameter("user_pwd");
        String name = request.getParameter("user_name");
        String gender = request.getParameter("gender");
        String birthStr = request.getParameter("birth");
        String phone = request.getParameter("phone_num");
        String address = request.getParameter("address");
        String userTypeStr = request.getParameter("user_type"); 
        String statusStr = request.getParameter("status"); 
        String pointsStr = request.getParameter("points"); 
        
        if (userIdStr == null || userIdStr.isEmpty()) {
            message = "更新失敗！缺少會員ID！";
            response.sendRedirect(request.getContextPath() + "/GetAllUsers?message=" + URLEncoder.encode(message, "UTF-8"));
            return;
        }
        
        UserBean user = new UserBean();
        try {
			user.setUserId(Integer.parseInt(userIdStr));
			user.setEmail(email);
			user.setUserPwd(password);
			user.setUserName(name);
			user.setGender(gender);
			user.setPhoneNum(phone);
			user.setAddress(address);
			
			if (birthStr != null && !birthStr.trim().isEmpty()) {
				user.setBirth(Date.valueOf(birthStr));
			}
			
			if (userTypeStr != null && !userTypeStr.trim().isEmpty()) {
				user.setUserType(Integer.parseInt(userTypeStr));
			}
			
			if (statusStr != null && !statusStr.trim().isEmpty()) {
				user.setStatus(Integer.parseInt(statusStr));
			}
			
			if (pointsStr != null && !pointsStr.trim().isEmpty()) {
				user.setPoints(Integer.parseInt(pointsStr));
			}

			UsersDAOImpl dao = new UsersDAOImpl();
			int result = dao.updateUser(user);

			
			if (result > 0) {
			    message = "會員資料更新成功！";
			} else {
			    message = "會員資料更新失敗！資料未變動或 ID 錯誤！";
			}
			    
		} catch (Exception e) {
			e.printStackTrace();
			message = "更新失敗：資料格式不正確或資料庫錯誤！";
		}
        
        response.sendRedirect(request.getContextPath() 
        	    + "/GetAllUsers?status=success&msg=" + URLEncoder.encode(message, "UTF-8"));
    }
}