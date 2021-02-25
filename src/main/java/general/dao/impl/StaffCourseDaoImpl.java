package general.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

import general.dao.StaffCourseDao;
import general.tables.StaffCourse;

@Component("staffCourseDao")

public class StaffCourseDaoImpl extends GenericDaoImpl<StaffCourse> implements StaffCourseDao {

	@Override
	public List<StaffCourse> findAllByStaffId(Long staffId) {
		Query q = this.em.createQuery("SELECT sc FROM StaffCourse sc WHERE staff_id = :s");
		q.setParameter("s",staffId);
		List<StaffCourse> l = q.getResultList();
		return l;
	}
}
