import bookstore.dao.BookDao;

public class test {

	public static void main(String[] args) {

		BookDao bookDao = new BookDao();
		System.out.println(bookDao.selectAllBooks());
	}
}
