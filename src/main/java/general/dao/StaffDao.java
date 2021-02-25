package general.dao; 
import java.util.List;
import java.util.Map;

import general.tables.Staff;
public interface StaffDao extends GenericDao<Staff> {
    /**
     * Returns an User object that matches the username given
     *
     * @param username
     * @return
     */ 
	public Staff findByIinBin(String a_iin_bin);
	public Long countStaffbyIinBin(String a_iin_bin);
	public Long countStaffByIinBinNotId(Long a_staff_id,String a_iin_bin);
	public Long countStaffbyId(Long a_staff_id);
	public List<Staff> dynamicFindStaffSalary(String a_dynamicWhere);
	public List<Staff> dynamicFindStaffSalary2(String a_dynamicWhere);
	public List<Staff> findAll();
	public List<Staff> findByFIO(String f,String m, String l);
	public List<Staff> findAll(String condition);
	public Staff findByCustomerId(Long a_customer_id);
	
	public List<Staff> findAllLazy(String condition, int first, int pageSize);
	public int getRowCount(String condition);
	
	public Map<Long, Staff> getMappedList(String cond);
	
	public Staff findWithDetail(Long id);
	
	public List<Staff> findAllDissmissedLazy(String condition, int first, int pageSize);
	
	public int getRowCountDissmissed(String condition);
	
	public List<Staff> findAllCurrentLazy(String condition,String cond2, int first, int pageSize);
	
	public int getRowCountCurrent(String condition, String cond2);
	
	public List<Object[]> getFiredList(Long a_branch_id) throws DAOException;
}
