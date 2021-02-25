package general.dao.impl;

import java.util.List;

import javax.persistence.Query;
import general.dao.MainPageFoldersDao;
import general.tables.MainPageFolders;

import org.springframework.stereotype.Component;

@Component("mainPageFoldersDao")
public class MainPageFoldersDaoImpl extends GenericDaoImpl<MainPageFolders> implements MainPageFoldersDao{

	@Override
	public List<MainPageFolders> findAll(String condition) {
		String sql = "SELECT f FROM MainPageFolders f ";
		if(condition.length() > 0){
			sql += " WHERE " + condition;
		}
		Query q = this.em.createQuery(sql);
		List<MainPageFolders> l = q.getResultList();
		return l;
	}
}
