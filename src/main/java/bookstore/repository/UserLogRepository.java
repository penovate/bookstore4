package bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bookstore.bean.UserLogBean;

public interface UserLogRepository extends JpaRepository<UserLogBean, Integer> {

}
