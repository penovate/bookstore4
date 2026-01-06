package bookstore.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import bookstore.bean.BooksBean;
import bookstore.bean.OrderItem;
import bookstore.bean.Orders;
import bookstore.service.OrderService;
import bookstore.service.bookService;
//import bookstore.service.BookService; 
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class OrderController {

	@Autowired
	private OrderService orderService;

	/*
	@Autowired
	private BookService bookService;
	*/
	
	//待書籍程式也merge後刪掉改用上面的
	private bookService bookService; 

//==================新增類 Controller==================//
	
	// 建立新訂單
	@PostMapping("/order/insert")
	public String processInsertOrder(HttpServletRequest request, Model model) {
		try {
			// 接收表單資料與set物件
			Orders order = new Orders();
			Integer userId = Integer.parseInt(request.getParameter("userId"));
			order.getUserBean().setUserId(userId);
			order.setRecipientAt(request.getParameter("recipientName"));
			order.setAddress(request.getParameter("address"));
			order.setPhone(request.getParameter("phone"));
			String paymentMethod = request.getParameter("paymentMethod");
			order.setPaymentMethod(paymentMethod);
			
			// 判斷是否付款
			order.setPaymentStatus("貨到付款".equals(paymentMethod) ? "未付款" : "已付款");
			// 設定訂單狀態
			order.setOrderStatus("待出貨");

			// 處理多筆明細
			String[] bookIds = request.getParameterValues("bookId");
			String[] quantities = request.getParameterValues("quantity");
			String[] prices = request.getParameterValues("price");
			List<OrderItem> items = new ArrayList<>();

			if (bookIds != null) {
				for (int i = 0; i < bookIds.length; i++) {
					if (bookIds[i] != null && !bookIds[i].isEmpty()) {
						OrderItem item = new OrderItem();
						BooksBean book = new BooksBean();
						book.setBookId(Integer.parseInt(bookIds[i]));
						item.setBooksBean(book);
						item.setQuantity(Integer.parseInt(quantities[i]));
						item.setPrice(new BigDecimal(prices[i]));
						item.setSubtotal(item.getPrice().multiply(new BigDecimal(item.getQuantity())));//數量也要轉成BigDecimal物件，否則方法無法相乘
						items.add(item);
					}
				}
			}

			orderService.insertOrder(order, items);
			model.addAttribute("order", order);//資料掛載
			model.addAttribute("items", items);//資料掛載
			return "order/InsertOrderDisplay";

		} catch (Exception e) {
			model.addAttribute("error", "建立訂單失敗: " + e.getMessage());
			return "order/InsertOrderDisplay";
		}
	}
	
	// 新增明細到既有訂單
		@PostMapping("/order/addItems")
		//不使用Model，使用RedirectAttributes，因為model是導到新頁面，這裡是要新增完導回查詢單筆訂單頁面
		public String addItemsToOrder(@RequestParam("orderId") Integer orderId, HttpServletRequest request, RedirectAttributes redirectAttributes) {
			try {
				String[] bookIds = request.getParameterValues("bookId");
				String[] prices = request.getParameterValues("price");
				String[] quantities = request.getParameterValues("quantity");
				List<OrderItem> items = new ArrayList<>();

				if (bookIds != null) {
					for (int i = 0; i < bookIds.length; i++) {
						OrderItem item = new OrderItem();
						BooksBean book = new BooksBean();
						book.setBookId(Integer.parseInt(bookIds[i]));
						item.setBooksBean(book);
						item.setQuantity(Integer.parseInt(quantities[i]));
						item.setPrice(new BigDecimal(prices[i]));
						item.setSubtotal(item.getPrice().multiply(new BigDecimal(item.getQuantity())));//數量也要轉成BigDecimal物件，否則方法無法相乘
						items.add(item);
					}
				}
				orderService.addItemsToOrder(orderId, items);
				redirectAttributes.addAttribute("id", orderId);
				return "redirect:/order/get";
			} catch (Exception e) {
				//addFlashAttribute傳遞臨時訊息，等到跳轉後的下一個頁面讀取完後，就會直接刪除
				redirectAttributes.addFlashAttribute("error", "新增明細失敗");
				return "redirect:/order/get?id=" + orderId;
			}
		}
		
//==================查詢類 Controller==================//
		
		// 查詢活動中的訂單
		@GetMapping("/order/activeList")
		public String listActiveOrders(Model model) {
			List<Orders> orders = orderService.getAllActiveOrders();
			model.addAttribute("orders", orders);
			return "order/GetAllOrders";
		}
		
		// 查詢已取消的訂單
		@GetMapping("/order/cancelledList")
		public String listCancelledOrders(Model model) {
			List<Orders> orders = orderService.getCancelledOrders();
			model.addAttribute("orders", orders);
			return "order/GetAllCancelOrders";
		}
		
		// 查詢單筆訂單明細
		@GetMapping("/order/get")
		public String getOrderById(@RequestParam("id") Integer orderId, Model model) {
			Orders order = orderService.getOrderById(orderId);
			if (order == null) {
				return "redirect:/order/activeList?error=OrderNotFound";
			}
			List<OrderItem> items = orderService.getOrderItemsByOrderId(orderId);
			model.addAttribute("order", order);
			model.addAttribute("items", items);
			return "order/GetOrder";
		}
		
		// 查詢單筆已取消訂單明細
		@GetMapping("/order/getCancel")
		public String getCancelOrderById(@RequestParam("id") Integer orderId, Model model) {
			Orders order = orderService.getOrderById(orderId);
			if (order == null) {
				return "redirect:/order/cancelledList?error=OrderNotFound";
			}
			List<OrderItem> items = orderService.getOrderItemsByOrderId(orderId);
			model.addAttribute("order", order);
			model.addAttribute("items", items);
			return "order/GetCancelOrder";
		}
		
		// 查詢所有明細
		@GetMapping("/order/allItems")
		public String listAllItems(Model model) {
			List<OrderItem> items = orderService.getAllOrderItems();
			model.addAttribute("items", items);
			return "order/GetAllOrderItems";
		}

//==================更新類 Controller==================//
	
	// 更新訂單主檔
	@PostMapping("/order/update")
	public String processUpdateOrder(@ModelAttribute Orders order, Model model) {
		try {
			orderService.updateOrder(order);
			model.addAttribute("order", order);
			return "order/UpdateOrderDisplay";
		} catch (Exception e) {
			return "redirect:/order/activeList";
		}
	}

	// 更新單筆明細
	@PostMapping("/order/updateItem")
	public String updateOrderItem(@ModelAttribute OrderItem item, @RequestParam("orderId") Integer orderId, Model model) {
		try {
			Orders order = new Orders();
			order.setOrderId(orderId);
			item.setOrders(order);
			
			orderService.updateOrderItem(item);
			model.addAttribute("orderId", orderId);
			return "order/UpdateOrderItemDisplay";
		} catch (Exception e) {
			return "redirect:/order/get?id=" + orderId;
		}
	}

//==================取消與還原類 Controller==================//
	// --- 3. 刪除與狀態切換類 Controller ---

	// 取消訂單-軟刪除
	@PostMapping("/order/cancel")
	public String cancelOrder(@RequestParam("id") Integer orderId) {
		orderService.processCancelOrder(orderId);
		return "redirect:/order/activeList";
	}

	// 還原訂單
	@PostMapping("/order/restore")
	public String restoreOrder(@RequestParam("id") Integer orderId, RedirectAttributes redirectAttributes) {
		try {
			orderService.processRestoreOrder(orderId);
			return "redirect:/order/cancelledList";
		} catch (Exception e) {
			redirectAttributes.addAttribute("error", "還原訂單失敗");
			return "redirect:/order/cancelledList";
		}
	}

	// 徹底刪除訂單-硬刪除(專題目前改用軟刪除 廢棄掉了)
	/*
	@PostMapping("/order/delete")
	public String deleteOrder(@RequestParam("id") Integer id) {
		orderService.deleteOrder(id);
		return "redirect:/order/activeList";
	}
	*/

	// 刪除單筆明細
	@PostMapping("/order/deleteItem")
	public String deleteItem(@RequestParam("orderItemId") Integer orderItemId, @RequestParam("orderId") Integer orderId) {
		orderService.deleteOrderItem(orderItemId);
		return "redirect:/order/get?id=" + orderId;
	}

//==================其他 Controller==================//

	// 獲取書籍詳情回傳 JSON
	@GetMapping("/order/getBookDetail")
	@ResponseBody // ajax，不要找jsp
	public BooksBean getBookDetail(@RequestParam("bookId") Integer bookId) {
		return bookService.selectBookByIdS(bookId);
	}
}