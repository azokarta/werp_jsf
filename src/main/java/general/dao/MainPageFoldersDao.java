package general.dao;

import java.util.List;
import general.tables.MainPageFolders;

public interface MainPageFoldersDao extends GenericDao<MainPageFolders> {
	public List<MainPageFolders> findAll(String condition);
}
