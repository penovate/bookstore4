package bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bookstore.bean.ClubRegistrationsBean;

public interface ClubRegistrationsRepository extends JpaRepository<ClubRegistrationsBean, Integer> {

}
