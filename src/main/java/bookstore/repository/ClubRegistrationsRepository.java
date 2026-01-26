package bookstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import bookstore.bean.ClubRegistrationsBean;

public interface ClubRegistrationsRepository extends JpaRepository<ClubRegistrationsBean, Integer> {

	List<ClubRegistrationsBean> findByBookClub_ClubId(Integer clubId);

	List<ClubRegistrationsBean> findByUser_UserId(Integer userId);

	Optional<ClubRegistrationsBean> findByBookClub_ClubIdAndUser_UserId(Integer clubId, Integer userId);

	boolean existsByBookClub_ClubIdAndUser_UserId(Integer clubId, Integer userId);

}
