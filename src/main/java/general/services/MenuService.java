package general.services; 


import org.primefaces.model.TreeNode;
import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;
import general.tables.Menu;

public interface MenuService{
	
	@Transactional
	public void create(Menu m) throws DAOException;
	
	@Transactional
	public void update(Menu m) throws DAOException;
	
	public TreeNode getMenuTree();
	
	@Transactional
	public void updateSort(Menu childMenu,Menu parentMenu,int sortOrder) throws DAOException;
}
