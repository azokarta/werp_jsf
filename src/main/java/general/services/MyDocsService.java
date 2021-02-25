package general.services;



import general.dao.DAOException;
import general.tables.IMdTable;
import general.tables.MyDocs;

import org.springframework.transaction.annotation.Transactional;

import user.User;

public interface MyDocsService {
	
	@Transactional
	void create(MyDocs md, User userData) throws DAOException;
	
	@Transactional
	void addMd(IMdTable imd,User userData,Long owner, Integer status) throws DAOException;
	
	@Transactional
	void removeMd(IMdTable imd,Long owner) throws DAOException;
	
	@Transactional
	void removeAllFromMd(IMdTable imd) throws DAOException;
}