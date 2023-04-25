package pwa.push.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Data //lombok
@Entity  //JPA -> ORM
public class UserSubscriInfo {
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "p256dh",length = 100)
	private String p256dh;             //p256dh 사용자 브라우저 고유키
	
	@Column(name = "author", length = 100)
	private String author;            //사용권한
	
	@Column(columnDefinition = "varchar(200) comment 'endpoint'")
	private String endpoint;        //브라우저 url
	
	@Column(name = "expirationTime", length = 100)
	private String expirationTime;
	
	@Column(name = "useYn", length = 1)
	private String useYn;           //사용유무
	
	@CreationTimestamp              // 자동으로 현재 시간이 세팅
	private Timestamp createDate;
	
	@CreationTimestamp              // 자동으로 현재 시간이 세팅
	private Timestamp updateDate;
	
}

