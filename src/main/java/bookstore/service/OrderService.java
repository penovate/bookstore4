package bookstore.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bookstore.bean.OrderItem;
import bookstore.bean.Orders;
import bookstore.repository.OrderItemRepository;
import bookstore.repository.OrdersRepository;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	// 新增訂單
	public void insertOrder(Orders order, List<OrderItem> items) {
		// 計算總金額
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (OrderItem item : items) {
			BigDecimal subtotal = item.getSubtotal();
			totalAmount = totalAmount.add(subtotal);
		}
		// 總金額賦值
		order.setTotalAmount(totalAmount);

		// 新增訂單
		ordersRepository.save(order);

		// 新增訂單明細
		for (OrderItem item : items) {
			item.setOrders(order);
			orderItemRepository.save(item);
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

			//修改訂單狀態為已取消
			order.setOrderStatus("已取消");

			//更新訂單
			ordersRepository.save(order);
		} else {
			//沒找到訂單就丟例外
			throw new RuntimeException("找不到訂單 ID: " + orderId);
		}
	}
	
	//還原訂單
	public void processRestoreOrder(Integer orderId) {
	    
	    Optional<Orders> optional = ordersRepository.findById(orderId);

	    if (optional.isPresent()) {
	        Orders order = optional.get();

	        // 確定訂單狀態為已取消才可以還原訂單
	        if ("已取消".equals(order.getOrderStatus())) {
	            order.setOrderStatus("待處理");
	            ordersRepository.save(order);
	            
	        } else {
	            //如果訂單狀態不是已取消，丟例外，不能還原
	            throw new RuntimeException("訂單狀態非「已取消」，無法還原");
	        }

	    } else {
	        // 找不到訂單，丟例外，找不到訂單
	        throw new RuntimeException("找不到訂單 ID: " + orderId);
	    }
	}

	//刪除訂單明細
	public void deleteOrderItem(Integer orderItemId) {
	    
	    Optional<OrderItem> optional = orderItemRepository.findById(orderItemId);

	    if (optional.isPresent()) {
	        OrderItem item = optional.get();

	        //取得訂單id，方便等等刪除明細後更新訂單總金額
	        Integer orderId = item.getOrders().getOrderId();

	        //刪除訂單明細
	        orderItemRepository.deleteById(orderItemId);

	        //依據訂單id重算整筆訂單總金額並更新，private方法，詳下面
	        updateOrderTotalAmount(orderId);
	        
	    } else {
	        // 如果找不到該筆明細，丟例外，找不到訂單明細
	        throw new RuntimeException("找不到該筆訂單明細");
	    }
	}

	//依據訂單id重算整筆訂單總金額並更新訂單的方法
	private void updateOrderTotalAmount(Integer orderId) {
	    List<OrderItem> allItems = orderItemRepository.findByOrders_OrderId(orderId);
	    
	    //重新計算訂單總金額
	    BigDecimal newTotal = BigDecimal.ZERO; // 從 0 開始
	    for (OrderItem item : allItems) {
	        BigDecimal subtotal = item.getSubtotal();
	        newTotal = newTotal.add(subtotal);
	    }

	    //找要更新的訂單
	    Optional<Orders> optional = ordersRepository.findById(orderId);

	    if (optional.isPresent()) {
	        Orders order = optional.get();
	        order.setTotalAmount(newTotal);
	        //將新的總金額更新進資料庫
	        ordersRepository.save(order);
	    } else {
	        //找不到訂單，就丟例外
	        throw new RuntimeException("找不到訂單，無法更新訂單總額");
	    }
	}

	//---查詢操作，加上readOnly設定可以優化效能---//

	//查詢全部訂單
	@Transactional(readOnly = true)
	public List<Orders> getAllOrders() {
		return ordersRepository.findAll();
	}

	//查詢單筆訂單
	@Transactional(readOnly = true)
	public Orders getOrderById(Integer orderId) {
		Optional<Orders> optional = ordersRepository.findById(orderId);
		if (optional.isPresent()) {
	        return optional.get();	
	    }
		return null;
	}
	
	//查詢活動訂單
	@Transactional(readOnly = true)
	public List<Orders> getAllActiveOrders() {
		return ordersRepository.findActiveOrders();
	}

	//查詢已取消與已退款的訂單
	@Transactional(readOnly = true)
	public List<Orders> getCancelledOrders() {
		return ordersRepository.findCancelAndRefundedOrders(); 
	}
	
	//查詢單一使用者所有訂單
	@Transactional(readOnly = true)
	public List<Orders> getOrdersByUserId(Integer userId) {
		return ordersRepository.findByUserBean_UserId(userId);
	}

	//查詢單筆訂單所有明細
	@Transactional(readOnly = true)
	public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
		return orderItemRepository.findByOrders_OrderId(orderId);
	}
	
	//查詢所有訂單明細
	@Transactional(readOnly = true)
    public List<OrderItem> getAllOrderItems() {
		return orderItemRepository.findAll();
	}
}