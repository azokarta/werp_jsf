package general.dao; 

import general.tables.LegalEntity;

import java.util.List;

public interface LegalEntityDao extends GenericDao<LegalEntity> {
    
	public List<LegalEntity> findAll();
	public List<LegalEntity> findAll(String condition);
}
