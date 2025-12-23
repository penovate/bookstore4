package bookstore.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import bookstore.bean.GenreBean;
import bookstore.dao.BookDao;
import bookstore.dao.GenresDao;
import bookstore.util.HibernateUtil;

public class genreService {

	public List<GenreBean> getAllGenres() {
		List<GenreBean> genresList = new ArrayList<GenreBean>();
		GenresDao genresDao = new GenresDao();
		genresList = genresDao.getAllGenres();
		return genresList;
	}

	// select genre by Id--------------------
	public GenreBean selectGenreById(Integer genreId) {
		BookDao bookDao = new BookDao();
		GenreBean genreBean = bookDao.selectGenreById(genreId);
		return genreBean;
	}
}
