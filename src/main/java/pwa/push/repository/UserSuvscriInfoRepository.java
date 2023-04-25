package pwa.push.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import pwa.push.model.UserSubscriInfo;

public interface UserSuvscriInfoRepository extends JpaRepository<UserSubscriInfo, Integer>{
	List<UserSubscriInfo> findByP256dh(String p256dh);
	
	@Transactional
	int deleteByP256dh(String p256dh);
}
