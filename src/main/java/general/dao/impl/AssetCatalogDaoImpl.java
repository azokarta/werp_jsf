package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import general.dao.AssetCatalogDao;
import general.tables.AssetCatalog;

import org.springframework.stereotype.Component; 
@Component("assetCatalogDao")
public class AssetCatalogDaoImpl extends GenericDaoImpl<AssetCatalog> implements AssetCatalogDao {

	@Override
	public List<AssetCatalog> findAll() {
		Query q = this.em.createQuery("Select c FROM AssetCatalog c");
		return q.getResultList();
	}

	@Override
	public List<AssetCatalog> findAll(String condition) {
		String s = "SELECT ac FROM AssetCatalog ac";
		if(condition.length() > 0){
			s += " WHERE " + condition;
		}
		Query q = this.em.createQuery(s);
		List<AssetCatalog> l = q.getResultList();
		return l;
	}
	
}