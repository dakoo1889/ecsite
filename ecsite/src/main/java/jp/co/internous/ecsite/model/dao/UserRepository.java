//loginFormから渡されるユーザ情報(ユーザ名、パスワード)を条件にDB検索するためのDAOを作成
package jp.co.internous.ecsite.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.internous.ecsite.model.entity.User;

public interface UserRepository extends JpaRepository<User,Long> { //userテーブルのentityにアクセスするためのもの。userと、useテーブルの
	
	List<User> findByUserNameAndPassword(String userName,String password); 

}
