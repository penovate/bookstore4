package bookstore.controller;

import java.io.IOException;
import bookstore.bean.BooksBean;
import bookstore.dao.impl.OrderService;
import bookstore.dao.impl.bookService;
import bookstore.util.HibernateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.math.BigDecimal;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet("/GetBookDetail")
public class GetBookDetail extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final bookService bookService = new bookService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 1. 設定響應 Content Type 和編碼
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		String jsonResponse;

		// 2. 獲取前端參數
		String bookIdStr = request.getParameter("bookId");

		// *** 警告：這裡會捕獲 Service 層拋出的 NumberFormatException ***
		try {

			// 檢查 ID 是否為空
			if (bookIdStr == null || bookIdStr.trim().isEmpty()) {
				// 雖然拋出，但會被後面的 catch(Exception e) 捕獲
				throw new IllegalArgumentException("書籍 ID 不可為空");
			}

			Integer bookId = Integer.parseInt(bookIdStr.trim());

			// *** 呼叫 Service 層，Service 內部會執行 Integer.valueOf()，可能拋出 NumberFormatException
			// ***
			SessionFactory factory = HibernateUtil.getSessionFactory();
			Session session = factory.getCurrentSession();

			BooksBean book = bookService.selectBookByIdS(bookId);

			if (book != null) {
				// 4. 查詢成功：手動組裝 JSON 字串

				// 確保書籍名稱中的特殊字符被正確處理
				String name = book.getBookName().replace("\"", "\\\"");

				// 確保價格不為 null
				BigDecimal price = book.getPrice() != null ? book.getPrice() : BigDecimal.ZERO;

				// *** 最終修正: 使用 toPlainString() 確保輸出為簡潔數字格式 ***
				jsonResponse = String.format("{\"bookName\": \"%s\", \"price\": %s}", name, price.toPlainString()); // <--
																													// 關鍵修正點

				// System.out.println("成功回傳 JSON: " + jsonResponse); // 幫助除錯

			} else {
				// 5. 查詢失敗：找不到書籍 (或 Service 內部轉換失敗返回 null)
				jsonResponse = String.format("{\"error\": \"%s\"}", "找不到書籍ID為 " + bookIdStr + "的書籍");
			}
		}
		// *** 【關鍵修正】: 捕獲 NumberFormatException，這很可能來自 Service 內部未捕獲的 Integer.valueOf()
		// ***
		catch (NumberFormatException e) {
			// 處理 Service 層拋出的 NumberFormatException
			jsonResponse = String.format("{\"error\": \"%s\"}", "書籍 ID 格式錯誤，請輸入整數。");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 設置 400 狀態碼
		}
		// 捕獲所有其他運行時錯誤（包括 IllegalArgumentException, NullPointerException 等）
		catch (Exception e) {
			e.printStackTrace(); // 打印錯誤堆棧到 Tomcat 控制台
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 設置 500 狀態碼
			jsonResponse = String.format("{\"error\": \"%s\"}", "伺服器內部錯誤，請檢查 Tomcat 日誌。");
		}

		// 6. 將 JSON 寫入響應
		out.print(jsonResponse);
		out.flush();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}