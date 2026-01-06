package bookstore.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bookstore.bean.UserBean;

@Repository
public interface UserRepository extends JpaRepository<UserBean, Integer> {
	UserBean findByEmailAndUserPwd(String email, String userPwd);
	List<UserBean> findByUserNameContainingAndUserType(String userName, Integer userType);
	List<UserBean> findByUserNameContaining(String userName);
	List<UserBean> findByUserType(Integer userType);
}
