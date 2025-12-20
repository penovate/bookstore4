package bookstore.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import bookstore.bean.OrderItem;
import bookstore.bean.Orders;
import bookstore.dao.OrderItemDao;
import bookstore.dao.OrdersDao;
import bookstore.util.DBUtil;

public class OrderService {

	private OrdersDao ordersDao;
	private OrderItemDao orderItemDao;

	public OrderService() {
		this.ordersDao = new OrdersDao();
		this.orderItemDao = new OrderItemDao();
	}

	// 建立新訂單 (包含訂單主檔與明細)
	public void createOrder(Orders order, List<OrderItem> items) {
		Connection connection = null;
		try {
			connection = DBUtil.getConnection();
			connection.setAutoCommit(false); // 開啟交易

			// 計算訂單總額			
			BigDecimal totalAmount = new BigDecimal(0);
	        for (OrderItem item : items) {
	            // 通過Controller傳入的訂單明細Subtotal，計算訂單總價
	            totalAmount = totalAmount.add(item.getSubtotal());
	        }
	        // 將計算後的總金額設定給 Orders 物件
	        order.setTotalAmount(totalAmount);		
			
			// 1. 新增訂單主檔 (會自動回填 OrderId)
			ordersDao.insertOrder(order, connection);

			Integer orderId = order.getOrderId();
			if (orderId == null) {
				throw new RuntimeException("訂單建立失敗，無法取得 Order ID");
			}

			// 2. 新增訂單明細
			for (OrderItem item : items) {
				item.setOrderId(orderId); // 關聯到剛剛建立的訂單
				orderItemDao.insertOrderItem(item, connection);
			}

			connection.commit(); // 提交交易
			System.out.println("訂單建立成功！ Order ID: " + orderId);

		} catch (Exception e) {
			e.printStackTrace();
			if (connection != null) {
				try {
					connection.rollback();
					System.out.println("訂單建立失敗，交易回滾");
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			throw new RuntimeException("建立訂單失敗: " + e.getMessage());
		} finally {
			DBUtil.closeConnection(connection);
		}
	}
	
	// 在既有訂單中新增一筆或多筆訂單明細，並自動重新計算訂單總金額
		public void addItemsToOrder(Integer orderId, List<OrderItem> newItems) {
			if (newItems == null || newItems.isEmpty()) {
				System.out.println("沒有新的訂單明細需要新增。");
				return;
			}
			
			Connection connection = null;
			try {
				connection = DBUtil.getConnection();
				connection.setAutoCommit(false); // 開啟交易

				// 1. 檢查訂單是否存在
				Orders order = ordersDao.findOrderById(orderId);
				if (order == null) {
					throw new RuntimeException("找不到訂單 ID: " + orderId + "，無法新增明細。");
				}
				
				// 2. 新增新的訂單明細
				for (OrderItem item : newItems) {
					item.setOrderId(orderId); // 當前訂單id
					orderItemDao.insertOrderItem(item, connection);
				}

				// 3. 重新計算訂單金額
				List<OrderItem> allItems = orderItemDao.findOrderItemsByOrderId(orderId, connection);

				BigDecimal newTotal = new BigDecimal(0);
				for (OrderItem item : allItems) {
					newTotal = newTotal.add(item.getSubtotal());
				}

				// 4. 更新訂單主檔總金額
				order.setTotalAmount(newTotal);
				ordersDao.updateOrder(order, connection); // 用交易連線更新

				connection.commit(); // 提交交易
				System.out.println("已成功新增 " + newItems.size() + " 筆明細到訂單 ID: " + orderId + "，訂單總額已更新。");

			} catch (Exception e) {
				e.printStackTrace();
				if (connection != null) {
					try {
						connection.rollback();
						System.out.println("新增明細失敗，交易回滾");
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
				throw new RuntimeException("新增訂單明細失敗: " + e.getMessage());
			} finally {
				DBUtil.closeConnection(connection);
			}
		}

	// 更新訂單
	public void updateOrder(Orders order) {
		ordersDao.updateOrder(order);
	}
	
	// 更新單一訂單明細，並自動重新計算訂單總金額
	public void updateOrderItem(OrderItem item) {
		Connection connection = null;
		try {
			connection = DBUtil.getConnection();
			connection.setAutoCommit(false);
			
			// 1. 更新該筆明細
			orderItemDao.updateOrderItem(item, connection);
			
			// 2. 重新計算總金額
			List<OrderItem> items = orderItemDao.findOrderItemsByOrderId(item.getOrderId(), connection);
			
			BigDecimal newTotal = new BigDecimal(0);
			for (OrderItem i : items) {
				newTotal = newTotal.add(i.getSubtotal());
			}
			
			// 3. 更新訂單主檔總金額
			Orders order = ordersDao.findOrderById(item.getOrderId());
			order.setTotalAmount(newTotal);
			ordersDao.updateOrder(order, connection);
			
			connection.commit();
			System.out.println("明細更新成功，訂單總額已更新。");
			
		} catch (Exception e) {
			e.printStackTrace();
			if (connection != null) {
				try {
					connection.rollback();
					System.out.println("明細更新失敗，交易回滾");
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			throw new RuntimeException("更新明細失敗: " + e.getMessage());
		} finally {
			DBUtil.closeConnection(connection);
		}
	}

	// 刪除整筆訂單 (包含該訂單的所有明細)
	public void deleteOrder(Integer orderId) {
		Connection connection = null;
		try {
			connection = DBUtil.getConnection();
			connection.setAutoCommit(false);

			// 先刪除所有訂單明細(防止FK問題)
			orderItemDao.deleteOrderItemsByOrderId(orderId, connection);

			// 再刪除訂單主檔
			ordersDao.deleteOrderById(orderId, connection);

			connection.commit();
			System.out.println("訂單刪除成功！ Order ID: " + orderId);

		} catch (Exception e) {
			e.printStackTrace();
			if (connection != null) {
				try {
					connection.rollback();
					System.out.println("訂單刪除失敗，交易回滾");
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			throw new RuntimeException("刪除訂單失敗: " + e.getMessage());
		} finally {
			DBUtil.closeConnection(connection);
		}
	}

	// 刪除單一訂單明細，並自動重新計算訂單總金額
	public void deleteOrderItem(Integer orderItemId) {
		Connection connection = null;
		try {
			connection = DBUtil.getConnection();
			connection.setAutoCommit(false);

			// 1. 先查出這筆明細屬於哪張訂單 (為了後續重算總額)
			OrderItem itemToDelete = orderItemDao.findOrderItemById(orderItemId);
			if (itemToDelete == null) {
				throw new RuntimeException("找不到該筆訂單明細: " + orderItemId);
			}
			Integer orderId = itemToDelete.getOrderId();

			// 2. 刪除該筆明細
			orderItemDao.deleteOrderItemById(orderItemId, connection);

			// 3. 重新計算總金額
			List<OrderItem> items = orderItemDao.findOrderItemsByOrderId(orderId, connection);

			BigDecimal newTotal = new BigDecimal(0);
			for (OrderItem item : items) {
				newTotal = newTotal.add(item.getSubtotal());
			}

			// 4. 更新訂單主檔總金額
			Orders order = ordersDao.findOrderById(orderId);
			order.setTotalAmount(newTotal);
			ordersDao.updateOrder(order, connection); // 用交易連線更新

			connection.commit();
			System.out.println("明細刪除成功，訂單總額已更新。");

		} catch (Exception e) {
			e.printStackTrace();
			if (connection != null) {
				try {
					connection.rollback();
					System.out.println("明細刪除失敗，交易回滾");
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			throw new RuntimeException("刪除明細失敗: " + e.getMessage());
		} finally {
			DBUtil.closeConnection(connection);
		}
	}

	// 查詢所有訂單
	public List<Orders> getAllOrders() {
		List<Orders> allOrders = ordersDao.findAllOrders();
		return allOrders;
	}

	// 查詢使用者的訂單
	public List<Orders> getOrdersByUserId(Integer userId) {
		List<Orders> ordersByUserId = ordersDao.findOrdersByUserId(userId);
		return ordersByUserId;
	}

	// 查詢單筆訂單資訊
	public Orders getOrderById(Integer orderId) {
		Orders orderById = ordersDao.findOrderById(orderId);
		return orderById;
	}

	// 查詢某張訂單的所有明細
	public List<OrderItem> getOrderItems(Integer orderId) {
		List<OrderItem> orderItemByOrderId = orderItemDao.findOrderItemsByOrderId(orderId);
		return orderItemByOrderId;
	}
	
	// 查詢所有訂單明細
		public List<OrderItem> getAllOrderItems() {
			List<OrderItem> allOrderItems = orderItemDao.findAllOrderItems();
			return allOrderItems;
		}

}
