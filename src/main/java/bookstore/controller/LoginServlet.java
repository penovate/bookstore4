package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import bookstore.bean.UserBean;
import bookstore.dao.impl.UsersDAOImpl;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
			request.setAttribute("loginMessage", "信箱和密碼欄位皆不可為空！");
			request.getRequestDispatcher("/users/login.jsp").forward(request, response);
			return;
		}
		
		UsersDAOImpl dao = new UsersDAOImpl();
		UserBean user = dao.selectUserByCredentials(email, password);
		
		if (user != null) {
			if (user.getStatus() != null && user.getStatus().equals(2)) {
				request.setAttribute("loginMessage", "您的帳號已被停權，請聯繫管理員！");
				request.getRequestDispatcher("/users/login.jsp").forward(request, response);
				return;
			}
			
			if(!user.getUserType().equals(0)) {
				request.setAttribute("loginMessage", "您沒有權限進入後台管理系統，請使用管理員帳號登入！");
				request.getRequestDispatcher("/users/login.jsp").forward(request, response);
				return;
			}
			
			HttpSession session = request.getSession();
			session.setAttribute("user", user);
			response.sendRedirect(request.getContextPath() + "/users/home.html");
			
		} else {
			request.setAttribute("loginMessage", "登入失敗，信箱或密碼錯誤！");
			request.getRequestDispatcher("/users/login.jsp").forward(request, response);
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect(request.getContextPath() + "/users/login.jsp");
	}
}
