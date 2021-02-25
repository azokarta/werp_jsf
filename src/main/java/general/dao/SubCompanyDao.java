package general.dao; 

import java.util.List;
import general.tables.SubCompany;

public interface SubCompanyDao extends GenericDao<SubCompany> {
    
	public List<SubCompany> findAll();
	public List<SubCompany> findAll(String condition);
}
