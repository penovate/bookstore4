package bookstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import bookstore.bean.BooksBean;

public interface BookRepository extends JpaRepository<BooksBean, Integer> {

	Optional<BooksBean> findByIsbn(String isbn);

	List<BooksBean> findGenreBeanByGenreId(Integer genreId);

	List<BooksBean> findByOnShelf(Boolean status);
	
	
}
