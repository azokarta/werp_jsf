package general.services;

import general.dao.DAOException;
import general.tables.AssetCatalog;

import org.springframework.transaction.annotation.Transactional;

public interface AssetCatalogService {
	@Transactional
	void createCatalog(AssetCatalog c) throws DAOException;
	
	@Transactional
	void updateCatalog(AssetCatalog c) throws DAOException;
}
