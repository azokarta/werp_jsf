package general.services;

import general.dao.DAOException;
import general.tables.MainPageFolders;

import org.springframework.transaction.annotation.Transactional;

public interface MainPageFoldersService{
	@Transactional
	public void update(MainPageFolders mpf) throws DAOException;

	@Transactional
	public void create(MainPageFolders mpf) throws DAOException;
}
