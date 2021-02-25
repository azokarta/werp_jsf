package general.dao;
import java.util.Date;
import java.util.List;
import java.util.Map;

import general.output.tables.Podotchet;
import general.tables.Customer;
import general.tables.report.CustomerReport;
public interface CustomerDao extends GenericDao<Customer> {
    /**
     * Returns an User object that matches the username given
     *
     * @param username
     * @return
     */
    public Customer loadUserByUsername(String username);     
    public Customer findByIinBin(String a_iin_bin);    
    public List<Customer> dynamicFindCustomers(String a_dynamicWhere);
    public Long countCustomerbyIinBin(String a_iin_bin);
    public Long countCustomerbyIinBinNotId(Long a_customer_id,String a_iin_bin);
    public Long countCustomerbyId(Long a_customer_id);
    public Long countCustomerEmployee(Long a_customer_id); 
    public Long countCustomerEmployeeByDate(Long a_customer_id, Date inDate);
    public Customer getById(Long customer_id);
    public Customer findByStaffId(Long staffId);
    public Long getCusIdByStaffId(Long staffId);
    public List<Customer> findAll(String condition);
    
    public List<Customer> findAll(String condition,int first,int max);
    
    public int getRowCount(String condition);
    
    public Map<Long, Customer> getMappedList(String cond);
    public List<Podotchet> dynamicFindCustomersPodotchet(String a_dynamicWhere);
    
    
    /***********REPORTS******************/
    public List<Object[]> findAnalyticsRep1Data(String bukrs,Long branchId, int day, int month);
}
