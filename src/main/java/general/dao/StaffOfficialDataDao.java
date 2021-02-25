package general.dao; 

import java.util.List;
import java.util.Map;

import general.tables.StaffOfficialData;

public interface StaffOfficialDataDao extends GenericDao<StaffOfficialData> {
    public List<StaffOfficialData> findAll(String condition);
    public Map<Long,StaffOfficialData> findAllMapByBukrs(String a_bukrs);
}
