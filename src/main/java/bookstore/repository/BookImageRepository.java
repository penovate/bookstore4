package bookstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import bookstore.bean.BookImageBean;
import bookstore.bean.BooksBean;

public interface BookImageRepository extends JpaRepository<BookImageBean, Integer> {

	Optional<BookImageBean> findByBookAndIsMain(BooksBean book, Boolean isMain);
}
