package bookstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import bookstore.bean.Orders;
import bookstore.util.DBUtil;

public class OrdersDao {

	// 新增訂單 (一般用，自動取得連線)
	public void insertOrder(Orders order) {
		try (Connection connection = DBUtil.getConnection()) {
			insertOrder(order, connection); // 呼叫下面那個支援交易的方法
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("訂單新增失敗");
		}
	}
	
	// 新增訂單 (交易用，使用外部傳入的連線)
	public void insertOrder(Orders order, Connection connection) throws SQLException {
		String sql = "INSERT INTO orders (user_id, total_amount, payment_method, payment_status, order_status, recipient_at, address, phone, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		// Statement.RETURN_GENERATED_KEYS: 告訴資料庫我們需要拿回自動產生的PK (Order_id)
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
			
			preparedStatement.setInt(1, order.getUserId());
			preparedStatement.setBigDecimal(2, order.getTotalAmount());
			preparedStatement.setString(3, order.getPaymentMethod());
			preparedStatement.setString(4, order.getPaymentStatus());
			preparedStatement.setString(5, order.getOrderStatus());
			preparedStatement.setString(6, order.getRecipientAt());
			preparedStatement.setString(7, order.getAddress());
			preparedStatement.setString(8, order.getPhone());
			
			// 手動補單或歷史清單可能就會有時間，提供後台人員可指定訂單時間，如果沒填就用現在時間
			Timestamp createdAt = order.getCreatedAt();
			if (createdAt == null) {
				createdAt = new Timestamp(System.currentTimeMillis());
			}
			preparedStatement.setTimestamp(9, createdAt);
			
			preparedStatement.execute();
			
			// 取回自動產生的主鍵 (Auto-Increment ID)
			// getGeneratedKeys() 會回傳一個 ResultSet，裡面包含剛產生的 Key
			try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					// 取得第一個欄位 (也就是 ID)
					int orderId = generatedKeys.getInt(1);
					// 將 ID 設定回傳入的 order 物件中，這樣呼叫者就知道新訂單的 ID 是多少了
					order.setOrderId(orderId);
					System.out.println("訂單新增成功! UserID: " + order.getUserId() + ", OrderID: " + orderId);
				} else {
					throw new SQLException("訂單新增失敗，無法取得 ID。");
				}
			}
		}
	}

	// 更新訂單 (一般用，自動取得連線)
	public void updateOrder(Orders order) {
		try (Connection connection = DBUtil.getConnection()) {
			updateOrder(order, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("訂單更新失敗");
		}
	}

	// 更新訂單 (交易用，使用外部傳入的連線)(訂單金額、付款方式、付款狀態、訂單狀態、收件人、地址、電話、各個時間點)
	public void updateOrder(Orders order, Connection connection) throws SQLException {
		String sql = "UPDATE orders SET total_amount = ?, payment_method = ?, payment_status = ?, order_status = ?, recipient_at = ?, address = ?, phone = ?, shipped_at = ?, delivered_at = ?, received_at = ?, paid_at = ?, completed_at = ?, updated_at = ? WHERE order_id = ?";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			
			preparedStatement.setBigDecimal(1, order.getTotalAmount());
			preparedStatement.setString(2, order.getPaymentMethod());
			preparedStatement.setString(3, order.getPaymentStatus());
			preparedStatement.setString(4, order.getOrderStatus());
			preparedStatement.setString(5, order.getRecipientAt());
			preparedStatement.setString(6, order.getAddress());
			preparedStatement.setString(7, order.getPhone());
			preparedStatement.setTimestamp(8, order.getShippedAt());
			preparedStatement.setTimestamp(9, order.getDeliveredAt());
			preparedStatement.setTimestamp(10, order.getReceivedAt());
			preparedStatement.setTimestamp(11, order.getPaidAt());
			preparedStatement.setTimestamp(12, order.getCompletedAt());
			Timestamp updateTime = new Timestamp(System.currentTimeMillis());
			preparedStatement.setTimestamp(13, updateTime); // 更新時間
			preparedStatement.setInt(14, order.getOrderId());

			preparedStatement.execute();
			System.out.println("訂單ID: " + order.getOrderId() + "更新成功!");
		}
	}

	// 刪除訂單 (一般用，自動取得連線)
	public void deleteOrderById(Integer orderId) {
		try (Connection connection = DBUtil.getConnection()) {
			deleteOrderById(orderId, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("訂單刪除失敗");
		}
	}

	// 刪除訂單 (交易用，使用外部傳入的連線)
	public void deleteOrderById(Integer orderId, Connection connection) throws SQLException {
		String sql = "DELETE FROM orders WHERE order_id = ?";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			
			preparedStatement.setInt(1, orderId);
			
			preparedStatement.execute();
			System.out.println("訂單ID: " + orderId + "刪除成功!");
		}
	}

	// 查詢所有訂單
	public List<Orders> findAllOrders() {
		List<Orders> ordersList = new ArrayList<>();
		String sql = "SELECT * FROM orders";

		try (Connection connection = DBUtil.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(sql);
			 ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				Orders order = new Orders();
				order.setOrderId(resultSet.getInt("order_id"));
				order.setUserId(resultSet.getInt("user_id"));
				order.setTotalAmount(resultSet.getBigDecimal("total_amount"));
				order.setPaymentMethod(resultSet.getString("payment_method"));
				order.setPaymentStatus(resultSet.getString("payment_status"));
				order.setOrderStatus(resultSet.getString("order_status"));
				order.setRecipientAt(resultSet.getString("recipient_at"));
				order.setAddress(resultSet.getString("address"));
				order.setPhone(resultSet.getString("phone"));
				order.setShippedAt(resultSet.getTimestamp("shipped_at"));
				order.setDeliveredAt(resultSet.getTimestamp("delivered_at"));
				order.setReceivedAt(resultSet.getTimestamp("received_at"));
				order.setCreatedAt(resultSet.getTimestamp("created_at"));
				order.setPaidAt(resultSet.getTimestamp("paid_at"));
				order.setCompletedAt(resultSet.getTimestamp("completed_at"));
				order.setUpdatedAt(resultSet.getTimestamp("updated_at"));
				
				ordersList.add(order);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ordersList;
	}

	// 依訂單ID查詢訂單 (一般用，自動取得連線)
	public Orders findOrderById(Integer orderId) {
		try (Connection connection = DBUtil.getConnection()) {
			return findOrderById(orderId, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// 依訂單ID查詢訂單 (交易用，使用外部傳入的連線)
	public Orders findOrderById(Integer orderId, Connection connection) throws SQLException {
		Orders order = null;
		String sql = "SELECT * FROM orders WHERE order_id = ?";

		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			
			preparedStatement.setInt(1, orderId);
			
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					order = new Orders();
					order.setOrderId(resultSet.getInt("order_id"));
					order.setUserId(resultSet.getInt("user_id"));
					order.setTotalAmount(resultSet.getBigDecimal("total_amount"));
					order.setPaymentMethod(resultSet.getString("payment_method"));
					order.setPaymentStatus(resultSet.getString("payment_status"));
					order.setOrderStatus(resultSet.getString("order_status"));
					order.setRecipientAt(resultSet.getString("recipient_at"));
					order.setAddress(resultSet.getString("address"));
					order.setPhone(resultSet.getString("phone"));
					order.setShippedAt(resultSet.getTimestamp("shipped_at"));
					order.setDeliveredAt(resultSet.getTimestamp("delivered_at"));
					order.setReceivedAt(resultSet.getTimestamp("received_at"));
					order.setCreatedAt(resultSet.getTimestamp("created_at"));
					order.setPaidAt(resultSet.getTimestamp("paid_at"));
					order.setCompletedAt(resultSet.getTimestamp("completed_at"));
					order.setUpdatedAt(resultSet.getTimestamp("updated_at"));
				}
			}
		}
		return order;
	}
	// 依使用者ID查詢訂單
	public List<Orders> findOrdersByUserId(Integer userId) {
		List<Orders> ordersList = new ArrayList<>();
		String sql = "SELECT * FROM orders WHERE user_id = ?";

		try (Connection connection = DBUtil.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			
			preparedStatement.setInt(1, userId);
			
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					Orders order = new Orders();
					order.setOrderId(resultSet.getInt("order_id"));
					order.setUserId(resultSet.getInt("user_id"));
					order.setTotalAmount(resultSet.getBigDecimal("total_amount"));
					order.setPaymentMethod(resultSet.getString("payment_method"));
					order.setPaymentStatus(resultSet.getString("payment_status"));
					order.setOrderStatus(resultSet.getString("order_status"));
					order.setRecipientAt(resultSet.getString("recipient_at"));
					order.setAddress(resultSet.getString("address"));
					order.setPhone(resultSet.getString("phone"));
					order.setShippedAt(resultSet.getTimestamp("shipped_at"));
					order.setDeliveredAt(resultSet.getTimestamp("delivered_at"));
					order.setReceivedAt(resultSet.getTimestamp("received_at"));
					order.setCreatedAt(resultSet.getTimestamp("created_at"));
					order.setPaidAt(resultSet.getTimestamp("paid_at"));
					order.setCompletedAt(resultSet.getTimestamp("completed_at"));
					order.setUpdatedAt(resultSet.getTimestamp("updated_at"));
					
					ordersList.add(order);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ordersList;
	}
}
