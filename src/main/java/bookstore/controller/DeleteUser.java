package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;

import bookstore.dao.impl.UsersDAOImpl;

@WebServlet("/DeleteUser")
public class DeleteUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String message = "";
		String userIdStr = request.getParameter("user_id");
		
		if (userIdStr == null || userIdStr.trim().isEmpty()) {
			message = "刪除失敗！缺少會員ID！";
		} else {
			UsersDAOImpl dao = new UsersDAOImpl();
			
			try {
				Integer userId = Integer.parseInt(userIdStr);
				int count = dao.deleteUser(userId);
				
				if (count > 0) {
					message ="會員資料刪除成功！";
				} else {
					message = "刪除失敗！查無此會員或系統錯誤！";
				}
			} catch (NumberFormatException e) {
				message = "刪除失敗：會員ID格式錯誤！";
				e.printStackTrace();
			} catch (Exception e) {
				message = "刪除失敗！此會員目前仍有訂單或評價等資料紀錄，無法刪除！";
				e.printStackTrace();
			}
		}
		
		String encodedMessage = URLEncoder.encode(message, "UTF-8");
        
		response.sendRedirect(request.getContextPath() 
		    + "/GetAllUsers?status=success&msg=" + encodedMessage);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
