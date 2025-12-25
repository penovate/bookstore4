package bookstore.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import bookstore.dao.BookDao;

@WebServlet("/bookDeleteCheck")
public class bookDeleteCheck extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public bookDeleteCheck() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("有進到doPost進行檢查");
		Integer bookId = Integer.parseInt(request.getParameter("bookId"));

		// true代表有資料
		BookDao bookDao = new BookDao();
		Boolean reviewCheck = bookDao.selectReviewByBookId(bookId);
		Boolean orderItemCheck = bookDao.selectOrderItemByBookId(bookId);

		Boolean allCheck = !reviewCheck && !orderItemCheck;
		System.out.println("allCheck檢查結果=" + allCheck);
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write(String.valueOf(allCheck));

	}

}
