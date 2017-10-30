package org.zywx.cooldev.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.dao.AdviceDao;
import org.zywx.cooldev.entity.Advice;

/**
 * @
 * @author yang.li
 *
 */
@Service
public class AdviceService extends BaseService {
	@Autowired
	private AdviceDao adviceDao;
	
	public Advice addAdvice(Advice advice) {
		return adviceDao.save(advice);
	}

	
	public List<Map<String,Object>> findByPage() {
		
		String sql = "select a.id,a.content,u.account from T_ADVICE a left join T_USER u on a.userId = u.id order by id desc";
		
		List<Map<String, Object>> advices = this.jdbcTpl.queryForList(sql);
		
		
		
		return advices;
	}
}
