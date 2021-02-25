package general.dao;

import general.tables.Department;
import java.util.List;

/**
 *
 * @author onlasyn
 */

public interface DepartmentDao extends GenericDao<Department>{
    
    public List<Department> findAll(String condition);
}