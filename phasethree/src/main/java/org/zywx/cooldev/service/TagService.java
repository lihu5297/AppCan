package org.zywx.cooldev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.dao.TagDao;
import org.zywx.cooldev.entity.Tag;

@Service
public class TagService extends BaseService {
	@Autowired
	private TagDao tagDao;
	
	public Tag addTag(Tag t) {
		Tag retT =  tagDao.findOneByName(t.getName());
		if(retT == null) {
			return tagDao.save(t);
		} else {
			t.setId(retT.getId());
			return retT;
		}

	}
	
	public void removeTag(long tagId) {
		tagDao.delete(tagId);;
	}
	
	public Tag getTag(long tagId) {
		return tagDao.findOne(tagId);
	}
	
	public Tag getTag(String name) {
		return tagDao.findOneByName(name);
	}
}
