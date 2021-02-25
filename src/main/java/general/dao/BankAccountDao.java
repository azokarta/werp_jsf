package general.dao; 

import java.util.List;

import general.tables.BankAccount;

public interface BankAccountDao extends GenericDao<BankAccount> {
	public List<BankAccount> findAll(String condition);
	
	public void deleteCustomerAccount(Long customerId);
}