package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import bookstore.bean.UserBean;
import bookstore.dao.impl.UsersDAOImpl;

@WebServlet("/GetAllUsers")
public class GetAllUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String searchName = request.getParameter("searchName");
		String userTypeFilterStr = request.getParameter("userTypeFilter");
		
		Integer userTypeFilter = null;
		
		try {
			if (userTypeFilterStr != null && !userTypeFilterStr.trim().isEmpty()) {
				userTypeFilter = Integer.parseInt(userTypeFilterStr);
			}
		} catch (NumberFormatException e) {
		}
		
		UsersDAOImpl dao = new UsersDAOImpl();
		List<UserBean> users = dao.selectAllUsers(searchName, userTypeFilter);
		
		request.setAttribute("users", users);
		request.setAttribute("currentSearchName", searchName);
		request.setAttribute("currentUserTypeFilter", userTypeFilterStr);
		request.getRequestDispatcher("/users/UserList.jsp").forward(request, response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
