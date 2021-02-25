package general.services;

import java.util.List;
import java.util.Map;

import general.dao.DAOException;
import general.tables.RevItem;
import general.tables.RevItemTitle;
import general.tables.RevItemType;
import general.tables.Revision;
import user.User;

import org.springframework.transaction.annotation.Transactional;

public interface RevisionService {
	
	public List<Revision> findAll();
	
	public Revision find(Long id);
	
	public Revision findWithDetail(Long id);
	
	public List<RevItemType> getItemTypes();
	
	public List<RevItem> findItemsByRevItemTypeId(Revision rev, int typeId);
	
	public RevItemTitle findRevTitle(Long id);
	
	public List<RevItem> findItemsByTitleId(Long revTitleId);
	
	public List<RevItemTitle> findRevTitlesByRevId(Long revId);
	
	public Map<RevItemTitle,List<RevItem>> findCurrentResult(Long revId);
	
	public List<RevItem> getPreparedPostingItems(Long revId);
	
	public List<RevItem> getPreparedWriteoffItems(Long revId);
	
	@Transactional
	public void create(Revision r, User userData) throws DAOException;
	
	@Transactional
	public void update(Revision r, User userData) throws DAOException;
	
	@Transactional
	public void finish(Revision r, User userData) throws DAOException;
	
	@Transactional
	public void closeDoc(Revision r, Long userId,List<RevItem> postingItems, List<RevItem> writeoffItems) throws DAOException;
	
	@Transactional
	public void returnToAction(Revision r, User userData) throws DAOException;
	
	@Transactional
	public void createItemTitle(RevItemTitle revTitle,List<RevItem> items, User userData) throws DAOException;
	
	@Transactional
	public void updateItemTitle(RevItemTitle revTitle,List<RevItem> items, User userData) throws DAOException;
}
