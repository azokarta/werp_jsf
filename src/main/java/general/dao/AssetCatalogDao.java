package general.dao;

import java.util.List;

import general.tables.AssetCatalog;

public interface AssetCatalogDao extends GenericDao<AssetCatalog> {
	List<AssetCatalog> findAll();
	List<AssetCatalog> findAll(String condition);
}
