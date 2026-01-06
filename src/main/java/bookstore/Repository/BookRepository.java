package bookstore.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bookstore.bean.BooksBean;

public interface BookRepository extends JpaRepository<BooksBean, Integer> {

}
