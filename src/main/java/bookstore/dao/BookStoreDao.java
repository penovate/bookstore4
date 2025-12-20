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

import bookstore.bean.BooksBean;
import bookstore.util.DBUtil;

public class BookStoreDao {

//select all books-------
	public List<BooksBean> selectAllBooks() {
		ArrayList<BooksBean> bookList = new ArrayList<BooksBean>();
		String sql = "select e.*,g.genre_name from books e join genres g on e.genre_id = g.genre_id";
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Integer bookId = resultSet.getInt("book_id");
				String bookName = resultSet.getString("book_name");
				String author = resultSet.getString("author");
				String translator = resultSet.getString("translator");
				Integer genres = resultSet.getInt("genre_id");
				BigDecimal price = resultSet.getBigDecimal("price");
				Integer stock = resultSet.getInt("stock");
				String shortDesc = resultSet.getString("short_desc");
				LocalDateTime createdAt = resultSet.getObject("created_at", LocalDateTime.class);
				String press = resultSet.getString("press");
				String isbn = resultSet.getString("isbn");
				Boolean onShelf = resultSet.getBoolean("on_shelf");
				String genreName = resultSet.getString("genre_name");
				BooksBean books = new BooksBean(bookId, bookName, author, translator, genres, price, stock, shortDesc,
						createdAt, press, isbn, onShelf, genreName);
				bookList.add(books);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		}
		return bookList;
	}

//select books by Id--------------------

	public BooksBean selectBooksById(Integer bookId) {
		BooksBean booksBean = null;
		String sql = "select e.*,g.genre_name from books e join genres g on e.genre_id = g.genre_id where book_id = ?";
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
			preparedStatement.setInt(1, bookId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					String bookName = resultSet.getString("book_name");
					String author = resultSet.getString("author");
					String translator = resultSet.getString("translator");
					Integer genre = resultSet.getInt("genre_id");
					BigDecimal price = resultSet.getBigDecimal("price");
					Integer stock = resultSet.getInt("stock");
					String shortDesc = resultSet.getString("short_desc");
					LocalDateTime createdAt = resultSet.getObject("created_at", LocalDateTime.class);
					String press = resultSet.getString("press");
					String isbn = resultSet.getString("isbn");
					Boolean onShelf = resultSet.getBoolean("on_shelf");
					String genreName = resultSet.getString("genre_name");
					booksBean = new BooksBean(bookId, bookName, author, translator, genre, price, stock, shortDesc,
							createdAt, press, isbn, onShelf, genreName);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return booksBean;
	}

//select books by genres
	public List<BooksBean> selectBooksByGenres(Integer genres) {
		ArrayList<BooksBean> bookList = new ArrayList<BooksBean>();
		String sql = "select e.*,g.genre_name from books e join genre g on e.genre_id = g.genre_id where e.genre_id = ?";
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, genres);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					Integer bookId = resultSet.getInt("book_id");
					String bookName = resultSet.getString("book_name");
					String author = resultSet.getString("author");
					String translator = resultSet.getString("translator");
					BigDecimal price = resultSet.getBigDecimal("price");
					Integer stock = resultSet.getInt("stock");
					String shortDesc = resultSet.getString("short_desc");
					LocalDateTime createdAt = resultSet.getObject("created_at", LocalDateTime.class);
					String press = resultSet.getString("press");
					String isbn = resultSet.getString("isbn");
					Boolean onShelf = resultSet.getBoolean("on_shelf");
					String genreName = resultSet.getString("genre_name");
					BooksBean book = new BooksBean(bookId, bookName, author, translator, genres, price, stock,
							shortDesc, createdAt, press, isbn, onShelf, genreName);
					bookList.add(book);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bookList;
	}

//select by isbn------
	public BooksBean selectBooksByIsbn(String isbn) {
		BooksBean booksBean = null;
		String sql = "select e.*,g.genre_name from books e join genres g on e.genre_id = g.genre_id where e.isbn = ?";
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, isbn);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				Integer bookId = resultSet.getInt("book_id");
				String bookName = resultSet.getString("book_name");
				String author = resultSet.getString("author");
				String translator = resultSet.getString("translator");
				Integer genres = resultSet.getInt("genre_id");
				BigDecimal price = resultSet.getBigDecimal("price");
				Integer stock = resultSet.getInt("stock");
				String shortDesc = resultSet.getString("short_desc");
				LocalDateTime createdAt = resultSet.getObject("created_at", LocalDateTime.class);
				String press = resultSet.getString("press");
				Boolean onShelf = resultSet.getBoolean("on_shelf");
				String genreName = resultSet.getString("genre_name");
				booksBean = new BooksBean(bookId, bookName, author, translator, genres, price, stock, shortDesc,
						createdAt, press, isbn, onShelf, genreName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return booksBean;
	}

//select by onShelf-----
	public List<BooksBean> selectBooksByOnShelf(Boolean onShelf) {
		BooksBean booksBean = null;
		String sql = "select e.*,g.genre_name from books e join genres g on e.genre_id = g.genre_id where e.on_shelf = ?";
		List<BooksBean> bookList = new ArrayList<BooksBean>();
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setBoolean(1, onShelf);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Integer bookId = resultSet.getInt("book_id");
				String bookName = resultSet.getString("book_name");
				String author = resultSet.getString("author");
				String translator = resultSet.getString("translator");
				Integer genres = resultSet.getInt("genre_id");
				BigDecimal price = resultSet.getBigDecimal("price");
				Integer stock = resultSet.getInt("stock");
				String shortDesc = resultSet.getString("short_desc");
				LocalDateTime createdAt = resultSet.getObject("created_at", LocalDateTime.class);
				String press = resultSet.getString("press");
				String isbn = resultSet.getString("isbn");
				String genreName = resultSet.getString("genre_name");
				booksBean = new BooksBean(bookId, bookName, author, translator, genres, price, stock, shortDesc,
						createdAt, press, isbn, onShelf, genreName);
				bookList.add(booksBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bookList;
	}

// select by BookName--------------------

	public BooksBean selectBookByName(String bookName) {
		BooksBean booksBean = null;
		String sql = "select e.*,g.genre_name from books e join genres g on e.genre_id = g.genre_id  where e.book_name = ?";

		try (Connection connection = DBUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, bookName);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				Integer bookId = resultSet.getInt("book_id");
				String author = resultSet.getString("author");
				String translator = resultSet.getString("translator");
				Integer genres = resultSet.getInt("genre_id");
				BigDecimal price = resultSet.getBigDecimal("price");
				Integer stock = resultSet.getInt("stock");
				String shortDesc = resultSet.getString("short_desc");
				LocalDateTime createdAt = resultSet.getObject("created_at", LocalDateTime.class);
				String press = resultSet.getString("press");
				String isbn = resultSet.getString("isbn");
				String genreName = resultSet.getString("genre_name");
				Boolean onShelf = resultSet.getBoolean("on_shelf");
				booksBean = new BooksBean(bookId, bookName, author, translator, genres, price, stock, shortDesc,
						createdAt, press, isbn, onShelf, genreName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return booksBean;
	}

//insert books---------------------------
	public BooksBean insertBooks(BooksBean booksBean) {
		String sql = "insert into books"
				+ "(book_name,author,translator,press,genre_id,price,isbn,stock,short_desc,created_at,on_shelf)values"
				+ "(?,?,?,?,?,?,?,?,?,?,?)";
		LocalDateTime now = LocalDateTime.now();
		BooksBean bookInsert = new BooksBean();
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, booksBean.getBookName());
			preparedStatement.setString(2, booksBean.getAuthor());
			preparedStatement.setString(3, booksBean.getTranslator());
			preparedStatement.setString(4, booksBean.getPress());
			preparedStatement.setInt(5, booksBean.getGenre());
			preparedStatement.setBigDecimal(6, booksBean.getPrice());
			preparedStatement.setString(7, booksBean.getIsbn());
			preparedStatement.setInt(8, booksBean.getStock());
			preparedStatement.setString(9, booksBean.getShortDesc());
			preparedStatement.setTimestamp(10, Timestamp.valueOf(now));
			preparedStatement.setBoolean(11, booksBean.getOnShelf());
			preparedStatement.execute();
			bookInsert = selectBookByName(booksBean.getBookName());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("這裡拿到的bookInsert書名="+bookInsert.getBookName());
		return bookInsert;
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
