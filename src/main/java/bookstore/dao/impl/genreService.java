package bookstore.dao.impl;

import java.util.ArrayList;
import java.util.List;

import bookstore.bean.GenreBean;
import bookstore.dao.GenresDao;

public class genreService {

	public List<GenreBean> getAllGenres() {
		List<GenreBean> genresList = new ArrayList<GenreBean>();
		GenresDao genresDao = new GenresDao();
		genresList = genresDao.getAllGenres();
		return genresList;
	}
}
