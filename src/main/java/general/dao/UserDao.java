package general.dao;
import java.util.List;
import java.util.Map;

import general.tables.Staff;
import general.tables.User;
import general.tables.report.HrReport1;
public interface UserDao extends GenericDao<User> {
    
    public User loadUserByUsername(String username);
    public User findByUsername(String a_username);
    public Long countUserByUsername(String a_username);
    public Long countUserbyId(Long a_user_id);
    public List<User> findAll(String condition);
    public String getUserFio(Long a_user_id);
    public User findUserByStaffId(Long staffId);
    
    List<User> findAllWithStaff();
    
    User findWithStaff(Long id);
    
    public List<User> findAllWithUserRoles(String condition);
    
    public List<User> findFromMessageGroup(String condition);
    
    /*********REPORTS****************/
    public List<Object[]> getRep1Data(String bukrs, Long branchId, Long roleId, int isRoot, String username);
    
    public List<Object[]> getUsernameFio(String a_where);
    
    public List<Long> findFromPosition(String condition);
}
