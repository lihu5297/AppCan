package org.zywx.coopman.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.commons.Enums.USER_STATUS;
import org.zywx.coopman.entity.User;

public interface UserDao extends PagingAndSortingRepository<User, Long> {
	
	/**
	    * @Title: findByAccount
	    * @Description: 根据邮箱,删除状态账户来查询用户
	    * @param @param account
	    * @param @return    参数
	    * @return User    返回类型
		* @user jingjian.wu
		* @date 2015年8月13日 下午4:17:25
	    * @throws
	 */
	public User findByAccountAndDel(String account,DELTYPE del);

	/**
	 * 根据账户密码查询用户<br>
	 * 使用单点登录之前，临时采用的登录认证方案
	 * 
	 * @author yang.li
	 * @date 2015年8月20日
	 * 
	 * @param account
	 * @param password
	 * @param delType
	 * @return
	 */
	public List<User> findByAccountAndPasswordAndDel(String account, String password, DELTYPE delType);
	
	/**
	 * 分页查询用户列表
	 * @user jingjian.wu
	 * @date 2015年9月10日 下午5:48:08
	 */
	public Page<User> findByDel(DELTYPE del,Pageable pageRequest);
	
	/**
	 * 查询用户列表
	 * @user jingjian.wu
	 * @date 2015年9月10日 下午5:48:08
	 */
	public List<User> findByDel(DELTYPE del);
	
	/**
	 * 查询用户列表
	 * @user jingjian.wu
	 * @date 2015年9月10日 下午5:48:08
	 */
//	@Query(value="select u from User u where u.del = ?1 and (u.userName like ?2 or u.account like ?3)")
//	public Page<User> findByNickNameLikeOrAccountLikeAndDel(DELTYPE del,String nickeName,String account,Pageable pageRequest);

	public Page<User> findByStatusAndDelOrderByCreatedAtDesc(USER_STATUS status, DELTYPE normal, Pageable pageRequest);
	
	public Page<User> findByStatusAndFilialeIdAndDelOrderByCreatedAtDesc(USER_STATUS status, Long filialeId, DELTYPE normal, Pageable pageRequest);
	
	public Page<User> findByStatusInAndDelOrderByCreatedAtDesc(List<USER_STATUS> status, DELTYPE normal, Pageable pageRequest);
	
	public Page<User> findByStatusInAndFilialeIdAndDelOrderByCreatedAtDesc(List<USER_STATUS> status, Long filialeId, DELTYPE normal, Pageable pageRequest);
	
	public List<User> findByStatusInAndDel(List<USER_STATUS> status, DELTYPE normal);

	//修复因参数序号错误导致的查询错误,无单位查询
	@Query(value="select u from User u where (u.userName like ?1 or u.account like ?1 or u.email like ?1) and u.del = ?3 and u.status = ?2 order by u.id desc")
	public Page<User> findByUserNameLikeOrAccountLikeOrEmainLikeAndStatusAndDel(String search, USER_STATUS status,
			DELTYPE normal, Pageable pageRequest);
	//有单位查询
	@Query(value="select u from User u where (u.userName like ?1 or u.account like ?1 or u.email like ?1 or u.filialeId in (?4)) and u.del = ?3 and u.status = ?2 order by u.id desc")
	public Page<User> findByUserNameLikeOrAccountLikeOrEmainLikeAndStatusAndDel(String search, USER_STATUS status,
			DELTYPE normal, Pageable pageRequest,List filialeIdList);
	
	@Query(value="select u from User u where (u.userName like ?1 or u.account like ?2) and filialeId = ?3 and u.del = ?5 and u.status = ?4 order by u.id desc")
	public Page<User> findByUserNameLikeOrAccountLikeAndFilialeIdAndStatusAndDel(String userName, String account, Long filialeId, USER_STATUS status,
			DELTYPE normal, Pageable pageRequest);
	
	//无单位查询
	@Query(value="select u from User u where (u.userName like ?1 or u.account like ?1 or u.email like ?1) and u.del = ?3 and u.status in (?2) order by u.id desc")
	public Page<User> findByUserNameLikeOrAccountLikeOrEmailAndStatusInAndDel(String search, List<USER_STATUS> status,
			DELTYPE normal, Pageable pageRequest);
	
	//有单位查询
	@Query(value="select u from User u where (u.userName like ?1 or u.account like ?1 or u.email like ?1 or u.filialeId in (?4)) and u.del = ?3 and u.status in (?2) order by u.id desc")
	public Page<User> findByUserNameLikeOrAccountLikeOrEmailAndStatusInAndDel(String search, List<USER_STATUS> status,
			DELTYPE normal, Pageable pageRequest,List filialeIdList);
	
	@Query(value="select u from User u where (u.userName like ?1 or u.account like ?2) and filialeId = ?3 and u.del = ?5 and u.status in (?4) order by u.id desc")
	public Page<User> findByUserNameLikeOrAccountLikeAndFilialeIdAndStatusInAndDel(String userName, String account, Long filialeId, List<USER_STATUS> status,
			DELTYPE normal, Pageable pageRequest);
	
	public List<User> findByAccount(String account);
	
	public List<User> findByEmail(String email);

}
