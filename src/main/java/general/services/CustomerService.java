package general.services; 
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import general.dao.DAOException;
import general.output.tables.Podotchet;
import general.tables.BankAccount;
import general.tables.Customer;

public interface CustomerService{
	@Transactional 
	void createCustomer (Customer a_customer,List<BankAccount> baList) throws DAOException;
	
	@Transactional 
	void createCustomer (Customer a_customer) throws DAOException;
	 
	@Transactional 
	Customer searchByIinBin (String a_iin_bin ) throws DAOException;
	
	@Transactional 
	Customer searchById (Long a_customer_id ) throws DAOException;
	
	@Transactional
	void updateCustomer (Customer a_customer) throws DAOException;
	
	@Transactional
	void updateCustomer (Customer a_customer,List<BankAccount> baList) throws DAOException;
	
	@Transactional
	List<Customer> dynamicSearch (Customer a_customer) throws DAOException;
	
	@Transactional
	List<Podotchet> dynamicSearchPodotchet(Customer a_customer) throws DAOException;
	
	@Transactional
	public List<Customer> searchInternalCompanies(String a_bukrs) throws DAOException;
	 
	@Transactional
	public List<Customer> searchCompaniesByIin(String al_iin) throws DAOException;
}
