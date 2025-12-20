package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import bookstore.bean.UserBean;
import bookstore.dao.impl.UsersDAOImpl;

@WebServlet("/GetUser")
public class GetUser extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userIdStr = request.getParameter("userId");
		UserBean user = null;
		UsersDAOImpl dao = new UsersDAOImpl();
		
		try {
			if (userIdStr != null && !userIdStr.trim().isEmpty()) {
				Integer userId = Integer.parseInt(userIdStr);
				user= dao.selectUserById(userId);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		request.setAttribute("user", user);
		request.getRequestDispatcher("/users/GetUser.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
