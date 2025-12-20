package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;

import bookstore.bean.UserBean;
import bookstore.dao.impl.UsersDAOImpl;

@WebServlet("/InsertUser")
public class InsertUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String message = "";
		
		String email = request.getParameter("email");
		String password = request.getParameter("user_pwd");
		String name = request.getParameter("user_name");
		String gender = request.getParameter("gender");
		String birthStr = request.getParameter("birth");
		String phone = request.getParameter("phone_num");
		String address = request.getParameter("address");
		String userTypeStr = request.getParameter("user_type");
		
		if (email == null || email.isEmpty() || password == null || password.isEmpty() || name == null || name.isEmpty()) {
			message = "新增失敗！信箱、密碼與姓名必須填入！";
			request.setAttribute("message", message);
			request.getRequestDispatcher("/users/UserInsert.jsp").forward(request, response);
			return;
		}
		
		if (phone != null && !phone.trim().isEmpty()) {
			if (!phone.matches("09\\d{8}")) {
				message = "新增失敗！電話號碼必須是以 09 開頭的 10 個數字！";
				request.setAttribute("message", message);
		        request.getRequestDispatcher("/users/UserInsert.jsp").forward(request, response);
		        return;
			}
		}
		
		UserBean user = new UserBean();
		user.setEmail(email);
		user.setUserPwd(password);
		user.setUserName(name);
        user.setGender(gender);
        user.setPhoneNum(phone);
        user.setAddress(address);
        user.setStatus(1);
        user.setPoints(0);
        
        if (birthStr != null && !birthStr.trim().isEmpty()) {
        	try {
				user.setBirth(Date.valueOf(birthStr));
			} catch (Exception e) {
				System.out.println("日期格式軟換失敗: " + birthStr);
			}
        }
        
        if (userTypeStr != null && !userTypeStr.trim().isEmpty()) {
        	try {
				user.setUserType(Integer.parseInt(userTypeStr));
			} catch (NumberFormatException e) {
				user.setUserType(1);
			}
        } else {
        	user.setUserType(1);
        }
        
        UsersDAOImpl dao = new UsersDAOImpl();
        int result = 0;
        
        try {
			result = dao.insertUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			message = "新增失敗！資料庫寫入錯誤，請檢察 Email 是否重複！";
		}
        
        if (result > 0) {
        	message = "新增會員資料成功！";
        	request.setAttribute("user", user);
        	request.setAttribute("message", message);
        	request.getRequestDispatcher("/users/UserInsertFinish.jsp").forward(request, response);
        	return;
        } else {
        	request.setAttribute("message", message);
        	request.getRequestDispatcher("/users/UserInsert.jsp").forward(request, response);
        	return;
        }
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect(request.getContextPath() + "/users/UserInsert.jsp");
	}
}
