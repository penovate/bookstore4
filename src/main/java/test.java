import org.hibernate.Session;
import org.hibernate.SessionFactory;

import bookstore.dao.BookDao;
import bookstore.dao.impl.bookService;
import bookstore.util.HibernateUtil;

public class test {

	public static void main(String[] args) {

		BookDao bookDao = new BookDao();
		bookService bookService = new bookService();
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session ssession = sessionFactory.getCurrentSession();
		try {
			ssession.beginTransaction();
			bookService.deleteBook(18);
			ssession.getTransaction().commit();
		} catch (Exception e) {
			ssession.getTransaction().rollback();
			e.printStackTrace();
		}
	}
}
