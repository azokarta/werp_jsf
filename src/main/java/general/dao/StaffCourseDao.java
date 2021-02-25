package general.dao;

import java.util.List;

import general.tables.StaffCourse;

public interface StaffCourseDao extends GenericDao<StaffCourse> {
    List<StaffCourse> findAllByStaffId(Long staffId);
}
