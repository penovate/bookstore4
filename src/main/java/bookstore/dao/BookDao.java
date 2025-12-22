package bookstore.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import bookstore.bean.BooksBean;
import bookstore.bean.OrderItem;
import bookstore.bean.ReviewBean;
import bookstore.util.DBUtil;
import bookstore.util.HibernateUtil;

public class BookDao {

	SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

	// select all books-------
	public List<BooksBean> selectAllBooks() {
		Session session = sessionFactory.getCurrentSession();
		try {
			String hql = "from BooksBean b left join fetch b.genreBean";
			Query<BooksBean> query = session.createQuery(hql, BooksBean.class);
			List<BooksBean> bookList = query.getResultList();
			return bookList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

//select books by Id--------------------

	public BooksBean selectBooksById(Integer bookId) {
		Session session = sessionFactory.getCurrentSession();
		try {
			BooksBean book = session.find(BooksBean.class, bookId);
			return book;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

//select books by genres
	public List<BooksBean> selectBooksByGenres(Integer genres) {
		return null;
	}

//select by isbn------
	public BooksBean selectBooksByIsbn(String isbn) {
		return null;
	}

//select by onShelf-----
	public List<BooksBean> selectBooksByOnShelf(Boolean onShelf) {
		
		return null;
	}

// select by BookName--------------------

	// delete驗證，若評價有該書內容不可將書籍刪除
	public Boolean selectReviewByBookId(String bookId) {
		Boolean reviewCheck = false;
		String sql = "select * from reviews where book_id = ?";
		ReviewBean review = new ReviewBean();
		try {
			Connection connection = DBUtil.getConnection();
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setString(1, bookId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				review.setReviewId(rs.getString("review_id"));
				review.setUserId(rs.getString("user_id"));
				review.setRating(rs.getString("rating"));
				review.setComment(rs.getString("comment"));
				review.setCreatedAt(rs.getString("created_at"));
				if (review != null) {
					reviewCheck = true;
				} else
					reviewCheck = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(review);
		return reviewCheck;
	}

	// delete驗證，若訂單有該書，不可將書籍刪除
	public Boolean selectOrderItemByBookId(String bookId) {
		Integer bookId1 = Integer.valueOf(bookId);
		Boolean orderItemCheck = false;
		String sql = "select * from order_item where book_id = ?";
		OrderItem orderItem = new OrderItem();
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, bookId1);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				orderItem.setOrderItemId(resultSet.getInt("order_item_id"));
				orderItem.setOrderId(resultSet.getInt("order_id"));
				orderItem.setQuantity(resultSet.getInt("quantity"));
				orderItem.setPrice(resultSet.getBigDecimal("price"));
				orderItem.setSubtotal(resultSet.getBigDecimal("subtotal"));
				orderItem.setBookId(resultSet.getInt("book_id"));
				if (orderItem != null) {
					orderItemCheck = true;
				} else {
					orderItemCheck = false;
				}
			}
		} catch (SQLException e) {
		}
		System.out.println(orderItem);
		return orderItemCheck;
	}

	public BooksBean selectBookByName(String bookName) {
		
		return null;
	}

//insert books---------------------------
	public BooksBean insertBooks(BooksBean booksBean) {
		Session session = sessionFactory.getCurrentSession();
	BooksBean bookInsert =booksBean;	
	session.persist(booksBean);
		
		return bookInsert ;
	}

	// delete book by Id-----
	public void deleteBooks(Integer bookId) {
		String sql = "delete from books where book_id = ?";
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, bookId);
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// Update Book--------------
	public BooksBean upDateBook(Integer bookid, String bookName, String author, String translator, Integer genre,
			BigDecimal price, String isbn, Integer stock, String shortDesc, String press, Boolean onShelf) {
		String sql = "update books set book_name=?,author=?,translator=?"
				+ ",press=?,genre_id=?,price=?,isbn=?,stock=?,short_desc=?,on_shelf=? where book_id=?";
		BooksBean bookupDate = new BooksBean();
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, bookName);
			preparedStatement.setString(2, author);
			preparedStatement.setString(3, translator);
			preparedStatement.setString(4, press);
			preparedStatement.setInt(5, genre);
			preparedStatement.setBigDecimal(6, price);
			preparedStatement.setString(7, isbn);
			preparedStatement.setInt(8, stock);
			preparedStatement.setString(9, shortDesc);
			preparedStatement.setBoolean(10, onShelf);
			preparedStatement.setInt(11, bookid);
			preparedStatement.execute();
			bookupDate = selectBookByName(bookName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bookupDate;
	}

}
