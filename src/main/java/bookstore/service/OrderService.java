package bookstore.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bookstore.bean.OrderItem;
import bookstore.bean.Orders;
import bookstore.bean.UserBean;
import bookstore.aop.BusinessException;
import bookstore.bean.BooksBean;
import bookstore.bean.Cart;
import bookstore.bean.CouponBean;
import bookstore.repository.BookRepository;
import bookstore.repository.CartRepository;
import bookstore.repository.OrderItemRepository;
import bookstore.repository.OrdersRepository;
import bookstore.repository.UserRepository;
import bookstore.repository.CouponRepository;
import bookstore.dto.CheckoutRequest;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CouponRepository couponRepository;

	// 從購物車轉訂單 (Checkout Transcation)
	@SuppressWarnings("null")
	public Orders createOrderFromCart(Integer userId, CheckoutRequest request) {

		// 1. 取得使用者資料
		UserBean user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(400, "找不到使用者"));

		// 2. 取得使用者購物車
		List<Cart> cartItems = cartRepository.findByUserId(userId);
		if (cartItems.isEmpty()) {
			throw new BusinessException(400, "購物車是空的");
		}

		// 3. 檢查庫存 & 計算總金額
		BigDecimal totalAmount = BigDecimal.ZERO;

		for (Cart cart : cartItems) {
			BooksBean book = cart.getBooksBean();

			// 檢查上架狀態
			if (book.getOnShelf() != 1) {
				throw new BusinessException(400, "書籍 " + book.getBookName() + " 已下架，請移除後再結帳");
			}

			// 檢查庫存
			if (book.getStock() < cart.getQuantity()) {
				throw new BusinessException(400,
						"書籍 " + book.getBookName() + " 庫存不足，僅剩 " + book.getStock() + " 本");
			}

			// 扣除庫存
			book.setStock(book.getStock() - cart.getQuantity());
			bookRepository.save(book);

			// 計算書籍小計
			BigDecimal subtotal = book.getPrice().multiply(new BigDecimal(cart.getQuantity()));
			totalAmount = totalAmount.add(subtotal);
		}

		// 4. 計算運費 (宅配<1000 => 50, 超商<350 => 50)與判斷付款狀態
		BigDecimal shippingFee = BigDecimal.ZERO;
		String delivery = request.getDeliveryMethod(); // 宅配到府 or 超商取貨
		String paymentStatus = "";
		String paymentMethod = request.getPaymentMethod();

		if ("宅配到府".equals(delivery)) {
			if (totalAmount.compareTo(new BigDecimal(1000)) < 0) {
				shippingFee = new BigDecimal(50);
			} else {
				shippingFee = new BigDecimal(0);
			}
		} else if ("超商取貨".equals(delivery)) {
			if (totalAmount.compareTo(new BigDecimal(350)) < 0) {
				shippingFee = new BigDecimal(50);
			} else {
				shippingFee = new BigDecimal(0);
			}
		}

		if ("信用卡".equals(paymentMethod)) {
			paymentStatus = "已付款";
		} else if ("貨到付款".equals(paymentMethod)) {
			paymentStatus = "未付款";
		}

		// 計算實付金額
		BigDecimal finalAmount = totalAmount.add(shippingFee);

		// 5. 建立訂單
		Orders order = new Orders();
		order.setUserBean(user);
		order.setTotalAmount(totalAmount); // 商品總額
		order.setShippingFee(shippingFee); // 運費
		order.setFinalAmount(finalAmount); // 實付總額

		order.setPaymentMethod(request.getPaymentMethod());
		order.setPaymentStatus(paymentStatus);
		order.setOrderStatus("待出貨");

		order.setRecipientAt(request.getRecipientName());
		order.setPhone(request.getRecipientPhone());
		order.setAddress(request.getAddress()); // 地址或超商門市名稱
		order.setDeliveryMethod(delivery);
		order.setCouponId(request.getCouponId());

		order.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
		order.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

		ordersRepository.save(order);

		// 6. 建立訂單明細
		for (Cart cart : cartItems) {
			OrderItem item = new OrderItem();
			item.setOrders(order);
			item.setBooksBean(cart.getBooksBean());
			item.setQuantity(cart.getQuantity());
			item.setPrice(cart.getBooksBean().getPrice());
			item.setSubtotal(cart.getBooksBean().getPrice().multiply(new BigDecimal(cart.getQuantity())));

			orderItemRepository.save(item);
		}

		// 8. 處理優惠券 (如果有)
		if (order.getCouponId() != null) {
			CouponBean coupon = couponRepository.findById(order.getCouponId())
					.orElseThrow(() -> new BusinessException(400, "優惠券不存在"));

			// 驗證優惠券歸屬
			if (!coupon.getUserId().equals(order.getUserBean().getUserId())) {
				throw new BusinessException(400, "這張優惠券不屬於您");
			}

			// 驗證是否已使用
			if (coupon.getStatus() == 1) {
				throw new BusinessException(400, "優惠券已使用");
			}

			// 驗證低消
			if (order.getTotalAmount().compareTo(coupon.getMinSpend()) < 0) {
				throw new BusinessException(400, "未達優惠券最低消費金額: " + coupon.getMinSpend());
			}

			// 套用折扣
			BigDecimal discount = coupon.getDiscountAmount();
			order.setDiscount(discount);

			// 重新計算實付金額
			// 實付金額 = 總金額 + 運費 - 折扣金額
			BigDecimal newFinal = order.getFinalAmount().subtract(discount);
			if (newFinal.compareTo(BigDecimal.ZERO) < 0) {
				newFinal = BigDecimal.ZERO;
			}
			order.setFinalAmount(newFinal);

			// 標記已使用
			coupon.setStatus(1);
			coupon.setUsedAt(new java.sql.Timestamp(System.currentTimeMillis()));
			couponRepository.save(coupon);
			ordersRepository.save(order); // 更新訂單的折扣和最終金額
		}

		// 7. 清空購物車
		cartRepository.deleteByUserId(userId);
		return order;
	}

	// 後台新增訂單
	public void insertOrder(Orders order, List<OrderItem> items) {
		// 0. 檢查庫存與上架狀態 (新增邏輯)
		for (OrderItem item : items) {
			Integer bookId = item.getBooksBean().getBookId();
			// 必須重新查詢以確保獲取最新庫存與狀態
			BooksBean book = bookRepository.findById(bookId)
					.orElseThrow(() -> new BusinessException(400, "找不到書籍 ID: " + bookId));

			// 檢查上架狀態
			if (book.getOnShelf() != 1) {
				throw new BusinessException(400, "書籍 " + book.getBookName() + " 已下架，無法建立訂單");
			}

			// 檢查庫存
			if (book.getStock() < item.getQuantity()) {
				throw new BusinessException(400,
						"書籍 " + book.getBookName() + " 庫存不足，僅剩 " + book.getStock() + " 本");
			}

			// 更新 Item 的 BookBean 確保數據一致
			item.setBooksBean(book);
		}

		// 計算總金額
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (OrderItem item : items) {
			BigDecimal subtotal = item.getSubtotal();
			totalAmount = totalAmount.add(subtotal);
		}
		// 總金額賦值
		order.setTotalAmount(totalAmount);

		// 計算運費 (宅配<1000 => 50, 超商<350 => 50)
		BigDecimal shippingFee = BigDecimal.ZERO;
		String delivery = order.getDeliveryMethod();

		if ("HOME".equals(delivery) || "宅配到府".equals(delivery)) {
			if (totalAmount.compareTo(new BigDecimal(1000)) < 0) {
				shippingFee = new BigDecimal(50);
			} else {
				shippingFee = new BigDecimal(0);
			}
		} else if ("STORE".equals(delivery) || "超商取貨".equals(delivery)) {
			if (totalAmount.compareTo(new BigDecimal(350)) < 0) {
				shippingFee = new BigDecimal(50);
			} else {
				shippingFee = new BigDecimal(0);
			}
		}

		order.setShippingFee(shippingFee);
		order.setFinalAmount(totalAmount.add(shippingFee));

		// 新增訂單
		ordersRepository.save(order);

		// 新增訂單明細並扣除庫存
		for (OrderItem item : items) {
			item.setOrders(order);
			orderItemRepository.save(item);

			// 扣除庫存 (新增邏輯)
			BooksBean book = item.getBooksBean();
			book.setStock(book.getStock() - item.getQuantity());
			bookRepository.save(book);
		}

		// 8. 處理優惠券 (如果有)
		if (order.getCouponId() != null) {
			CouponBean coupon = couponRepository.findById(order.getCouponId())
					.orElseThrow(() -> new BusinessException(400, "優惠券不存在"));

			// 驗證優惠券歸屬
			if (!coupon.getUserId().equals(order.getUserBean().getUserId())) {
				throw new BusinessException(400, "這張優惠券不屬於您");
			}

			// 驗證是否已使用
			if (coupon.getStatus() == 1) {
				throw new BusinessException(400, "優惠券已使用");
			}

			// 驗證低消
			if (order.getTotalAmount().compareTo(coupon.getMinSpend()) < 0) {
				throw new BusinessException(400, "未達優惠券最低消費金額: " + coupon.getMinSpend());
			}

			// 套用折扣
			BigDecimal discount = coupon.getDiscountAmount();
			order.setDiscount(discount);

			// 重新計算實付金額
			// 實付金額 = 總金額 + 運費 - 折扣金額
			BigDecimal newFinal = order.getFinalAmount().subtract(discount);
			if (newFinal.compareTo(BigDecimal.ZERO) < 0) {
				newFinal = BigDecimal.ZERO;
			}
			order.setFinalAmount(newFinal);

			// 標記已使用
			coupon.setStatus(1);
			coupon.setUsedAt(new java.sql.Timestamp(System.currentTimeMillis()));
			couponRepository.save(coupon);
			ordersRepository.save(order); // 更新訂單的折扣和最終金額
		}
	}

	// 針對已有訂單新增明細
	public void addItemsToOrder(Integer orderId, List<OrderItem> newItems) {
		if (newItems == null || newItems.isEmpty()) {
			System.out.println("沒有明細需要新增");
			return;
		}

		Optional<Orders> optional = ordersRepository.findById(orderId);
		Orders order;

		if (optional.isPresent()) {
			order = optional.get();
		} else {
			throw new RuntimeException("找不到訂單 ID: " + orderId);
		}

		for (OrderItem item : newItems) {
			item.setOrders(order);
			orderItemRepository.save(item);
		}
		updateOrderTotalAmount(orderId);// private方法，詳class最下面
	}

	// 更新訂單
	public void updateOrder(Orders order) {
		ordersRepository.save(order);
	}

	// 更新訂單明細
	public void updateOrderItem(OrderItem item) {
		orderItemRepository.save(item);
		updateOrderTotalAmount(item.getOrders().getOrderId());
	}

	// 刪除訂單(硬刪除)
	// 專題後續採軟刪除，此方法不會用，但保留
	public void deleteOrder(Integer orderId) {
		// 先刪明細再刪訂單
		orderItemRepository.deleteByOrderId(orderId);
		ordersRepository.deleteById(orderId);
	}

	// 取消訂單(軟刪除)
	public void processCancelOrder(Integer orderId) {

		Optional<Orders> optional = ordersRepository.findById(orderId);

		if (optional.isPresent()) {
			Orders order = optional.get();

			// 修改訂單狀態為已取消
			order.setOrderStatus("已取消");

			// 更新訂單
			ordersRepository.save(order);
		} else {
			// 沒找到訂單就丟例外
			throw new RuntimeException("找不到訂單 ID: " + orderId);
		}
	}

	// 還原訂單
	public void processRestoreOrder(Integer orderId) {

		Optional<Orders> optional = ordersRepository.findById(orderId);

		if (optional.isPresent()) {
			Orders order = optional.get();

			// 確定訂單狀態為已取消才可以還原訂單
			if ("已取消".equals(order.getOrderStatus())) {
				order.setOrderStatus("待處理");
				ordersRepository.save(order);

			} else {
				// 如果訂單狀態不是已取消，丟例外，不能還原
				throw new RuntimeException("訂單狀態非「已取消」，無法還原");
			}

		} else {
			// 找不到訂單，丟例外，找不到訂單
			throw new RuntimeException("找不到訂單 ID: " + orderId);
		}
	}

	// 刪除訂單明細
	public void deleteOrderItem(Integer orderItemId) {

		Optional<OrderItem> optional = orderItemRepository.findById(orderItemId);

		if (optional.isPresent()) {
			OrderItem item = optional.get();

			// 取得訂單id，方便等等刪除明細後更新訂單總金額
			Integer orderId = item.getOrders().getOrderId();

			// 刪除訂單明細
			orderItemRepository.deleteById(orderItemId);

			// 依據訂單id重算整筆訂單總金額並更新，private方法，詳下面
			updateOrderTotalAmount(orderId);

		} else {
			// 如果找不到該筆明細，丟例外，找不到訂單明細
			throw new RuntimeException("找不到該筆訂單明細");
		}
	}

	// 依據訂單id重算整筆訂單總金額並更新訂單的方法
	private void updateOrderTotalAmount(Integer orderId) {
		List<OrderItem> allItems = orderItemRepository.findByOrders_OrderId(orderId);

		// 重新計算訂單總金額
		BigDecimal newTotal = BigDecimal.ZERO; // 從 0 開始
		for (OrderItem item : allItems) {
			BigDecimal subtotal = item.getSubtotal();
			newTotal = newTotal.add(subtotal);
		}

		// 找要更新的訂單
		Optional<Orders> optional = ordersRepository.findById(orderId);

		if (optional.isPresent()) {
			Orders order = optional.get();
			order.setTotalAmount(newTotal);

			// 重新計算運費
			BigDecimal shippingFee = BigDecimal.ZERO;
			String delivery = order.getDeliveryMethod();

			if ("HOME".equals(delivery) || "宅配到府".equals(delivery)) {
				// 宅配 < 1000 => 50
				if (newTotal.compareTo(new BigDecimal(1000)) < 0) {
					shippingFee = new BigDecimal(50);
				}
			} else if ("STORE".equals(delivery) || "超商取貨".equals(delivery)) {
				// 超商 < 350 => 50 (修正後的門檻)
				if (newTotal.compareTo(new BigDecimal(350)) < 0) {
					shippingFee = new BigDecimal(50);
				}
			}

			order.setShippingFee(shippingFee);

			// 重新計算實付金額
			BigDecimal finalAmount = newTotal.add(shippingFee);
			order.setFinalAmount(finalAmount);

			// 將新的總金額更新進資料庫
			ordersRepository.save(order);
		} else {
			// 找不到訂單，就丟例外
			throw new RuntimeException("找不到訂單，無法更新訂單總額");
		}
	}

	// ---查詢操作，加上readOnly設定可以優化效能---//

	// 查詢全部訂單
	@Transactional(readOnly = true)
	public List<Orders> getAllOrders() {
		return ordersRepository.findAll();
	}

	// 查詢單筆訂單
	@Transactional(readOnly = true)
	public Orders getOrderById(Integer orderId) {
		Optional<Orders> optional = ordersRepository.findById(orderId);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	// 查詢活動訂單
	@Transactional(readOnly = true)
	public List<Orders> getAllActiveOrders() {
		return ordersRepository.findActiveOrders();
	}

	// 查詢已取消與已退款的訂單
	@Transactional(readOnly = true)
	public List<Orders> getCancelledOrders() {
		return ordersRepository.findCancelAndRefundedOrders();
	}

	// 查詢單一使用者所有訂單
	@Transactional(readOnly = true)
	public List<Orders> getOrdersByUserId(Integer userId) {
		return ordersRepository.findByUserBean_UserId(userId);
	}

	// 查詢單筆訂單所有明細
	@Transactional(readOnly = true)
	public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
		return orderItemRepository.findByOrders_OrderId(orderId);
	}

	// 查詢所有訂單明細
	@Transactional(readOnly = true)
	public List<OrderItem> getAllOrderItems() {
		return orderItemRepository.findAll();
	}
}