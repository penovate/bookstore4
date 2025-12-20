package bookstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bookstore.bean.OrderItem;
import bookstore.util.DBUtil;

public class OrderItemDao {

	// 新增訂單明細 (一般用，自動取得連線)
	public void insertOrderItem(OrderItem item) {
		try (Connection connection = DBUtil.getConnection()) {
			insertOrderItem(item, connection); // 呼叫下面那個支援交易的方法
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("訂單明細新增失敗");
		}
	}

	// 新增訂單明細 (交易用，使用外部傳入的連線)
	public void insertOrderItem(OrderItem item, Connection connection) throws SQLException {
		String sql = "INSERT INTO order_item (order_id, book_id, quantity, price, subtotal) VALUES (?, ?, ?, ?, ?)";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			
			preparedStatement.setInt(1, item.getOrderId());
			preparedStatement.setInt(2, item.getBookId());
			preparedStatement.setInt(3, item.getQuantity());
			preparedStatement.setBigDecimal(4, item.getPrice());
			preparedStatement.setBigDecimal(5, item.getSubtotal());

			preparedStatement.execute();
			System.out.println("訂單明細新增成功!");
		}
	}

	// 依訂單編號更新訂單明細 (數量、價格、小計)
	// 依訂單編號更新訂單明細 (一般用，自動取得連線)
	public void updateOrderItem(OrderItem item) {
		try (Connection connection = DBUtil.getConnection()) {
			updateOrderItem(item, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("訂單明細更新失敗");
		}
	}

	// 依訂單編號更新訂單明細 (交易用，使用外部傳入的連線)
	public void updateOrderItem(OrderItem item, Connection connection) throws SQLException {
		String sql = "UPDATE order_item SET quantity = ?, price = ?, subtotal = ? WHERE order_item_id = ?";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			
			preparedStatement.setInt(1, item.getQuantity());
			preparedStatement.setBigDecimal(2, item.getPrice());
			preparedStatement.setBigDecimal(3, item.getSubtotal());
			preparedStatement.setInt(4, item.getOrderItemId());

			preparedStatement.execute();
			System.out.println("訂單明細ID: " + item.getOrderItemId() + "更新成功!");
		}
	}

	// 依訂單明細ID刪除訂單明細 (一般用，自動取得連線)
	public void deleteOrderItemById(Integer orderItemId) {
		try (Connection connection = DBUtil.getConnection()) {
			deleteOrderItemById(orderItemId, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("訂單明細刪除失敗");
		}
	}

	// 依訂單明細ID刪除訂單明細 (交易用，使用外部傳入的連線)
	public void deleteOrderItemById(Integer orderItemId, Connection connection) throws SQLException {
		String sql = "DELETE FROM order_item WHERE order_item_id = ?";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			
			preparedStatement.setInt(1, orderItemId);
			
			preparedStatement.execute();
			System.out.println("訂單明細ID: " + orderItemId + "刪除成功!");
		}
	}

	// 依訂單ID刪除所有明細 (用於刪除訂單前，或更新訂單時重置明細)
	// 依訂單ID刪除所有明細 (一般用，自動取得連線)
	public void deleteOrderItemsByOrderId(Integer orderId) {
		try (Connection connection = DBUtil.getConnection()) {
			deleteOrderItemsByOrderId(orderId, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("訂單明細刪除失敗");
		}
	}

	// 依訂單ID刪除所有明細 (交易用，使用外部傳入的連線)
	public void deleteOrderItemsByOrderId(Integer orderId, Connection connection) throws SQLException {
		String sql = "DELETE FROM order_item WHERE order_id = ?";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			
			preparedStatement.setInt(1, orderId);
			
			preparedStatement.execute();
			System.out.println("訂單ID: " + orderId + " 的明細已全部刪除!");
		}
	}

	// 依訂單明細ID查詢訂單明細
	public OrderItem findOrderItemById(Integer orderItemId) {
		OrderItem item = null;
		String sql = "SELECT * FROM order_item WHERE order_item_id = ?";

		try (Connection connection = DBUtil.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			
			preparedStatement.setInt(1, orderItemId);
			
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					item = new OrderItem();
					item.setOrderItemId(resultSet.getInt("order_item_id"));
					item.setOrderId(resultSet.getInt("order_id"));
					item.setBookId(resultSet.getInt("book_id"));
					item.setQuantity(resultSet.getInt("quantity"));
					item.setPrice(resultSet.getBigDecimal("price"));
					item.setSubtotal(resultSet.getBigDecimal("subtotal"));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return item;
	}

	// 依訂單ID查詢所有明細 (一般用，自動取得連線)
	public List<OrderItem> findOrderItemsByOrderId(Integer orderId) {
		try (Connection connection = DBUtil.getConnection()) {
			return findOrderItemsByOrderId(orderId, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	// 依訂單ID查詢所有明細 (交易用，使用外部傳入的連線)
	public List<OrderItem> findOrderItemsByOrderId(Integer orderId, Connection connection) throws SQLException {
		List<OrderItem> itemList = new ArrayList<>();
		String sql = "SELECT * FROM order_item WHERE order_id = ?";

		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			
			preparedStatement.setInt(1, orderId);
			
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					OrderItem item = new OrderItem();
					item.setOrderItemId(resultSet.getInt("order_item_id"));
					item.setOrderId(resultSet.getInt("order_id"));
					item.setBookId(resultSet.getInt("book_id"));
					item.setQuantity(resultSet.getInt("quantity"));
					item.setPrice(resultSet.getBigDecimal("price"));
					item.setSubtotal(resultSet.getBigDecimal("subtotal"));
					
					itemList.add(item);
				}
			}
		}
		return itemList;
	}

	// 查詢所有訂單明細
	public List<OrderItem> findAllOrderItems() {
		List<OrderItem> itemList = new ArrayList<>();
		String sql = "SELECT * FROM order_item";

		try (Connection connection = DBUtil.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(sql);
			 ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				OrderItem item = new OrderItem();
				item.setOrderItemId(resultSet.getInt("order_item_id"));
				item.setOrderId(resultSet.getInt("order_id"));
				item.setBookId(resultSet.getInt("book_id"));
				item.setQuantity(resultSet.getInt("quantity"));
				item.setPrice(resultSet.getBigDecimal("price"));
				item.setSubtotal(resultSet.getBigDecimal("subtotal"));
				
				itemList.add(item);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return itemList;
	}
}
