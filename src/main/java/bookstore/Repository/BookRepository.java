package bookstore.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import bookstore.bean.BooksBean;

public interface BookRepository extends JpaRepository<BooksBean, Integer> {

	Optional<BooksBean> findByIsbn(String isbn);
	@Query("SELECT b FROM BooksBean b WHERE b.genreBean.genreId = :genreId")
	List<BooksBean> findGenreBean_GenreId(Integer genreId);

	//新增加--
	List<BooksBean> findByOnShelf(Integer onShelf);
	
	
}
