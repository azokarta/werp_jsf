package general.dao; 

import java.util.List;
import general.tables.Bank;

public interface BankDao extends GenericDao<Bank> {
    
	public List<Bank> findAll();
	public List<Bank> findAll(String condition);
}
