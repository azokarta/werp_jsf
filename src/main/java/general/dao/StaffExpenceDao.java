package general.dao;

import java.util.List;

import general.tables.StaffExpence;

public interface StaffExpenceDao extends GenericDao<StaffExpence> {
    List<StaffExpence> findAllByStaffId(Long staffId);
    public List<StaffExpence> findAllGrouppedByStaffIDCurrency();
    public List<StaffExpence> findAllByBukrs(String a_bukrs);
    public List<StaffExpence> findAllByBukrsDate(String a_bukrs,java.sql.Date a_date);
}
