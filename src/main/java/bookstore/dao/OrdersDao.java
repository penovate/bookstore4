package bookstore.dao;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import bookstore.bean.Orders;

public class OrdersDao {

	private Session session;

	public OrdersDao(Session session) {
		this.session = session;
	}

	// 新增訂單
	public void insertOrder(Orders order) {
		if (order.getCreatedAt() == null) {
			order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		}
		session.persist(order);
	}

	// 更新訂單
	public void updateOrder(Orders order) {
		order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		session.merge(order);
	}

	// 刪除訂單
	public void deleteOrderById(Integer orderId) {
		Orders order = session.get(Orders.class, orderId);
		if (order != null) {
			session.remove(order);
		}
	}

	// 查詢所有訂單
	public List<Orders> findAllOrders() {
		Query<Orders> query = session.createQuery("from Orders", Orders.class);
		return query.list();
	}

	// 依訂單ID查詢訂單
	public Orders findOrderById(Integer orderId) {
		return session.get(Orders.class, orderId);
	}

	// 依使用者ID查詢訂單
	public List<Orders> findOrdersByUserId(Integer userId) {
		Query<Orders> query = session.createQuery("from Orders o where o.user.userId = :userId", Orders.class);
		query.setParameter("userId", userId);
		return query.list();
	}
}
