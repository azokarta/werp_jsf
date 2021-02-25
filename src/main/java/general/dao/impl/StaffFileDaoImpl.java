package general.dao.impl;

import general.dao.StaffFileDao;
import general.tables.StaffFile;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component("staffFileDao")
public class StaffFileDaoImpl extends GenericDaoImpl<StaffFile>implements StaffFileDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<StaffFile> findAllByStaffId(Long staffId) {
		String s = "SELECT s FROM StaffFile s WHERE s.staff_id = " + staffId;
		Query q = em.createQuery(s);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StaffFile> findAllByStaffIdAndFileId(Long staffId, Long fileId) {
		String s = "SELECT s FROM StaffFile s WHERE s.staff_id = " + staffId + " AND s.file_id = " + fileId;
		Query q = em.createQuery(s);
		return q.getResultList();
	}

}
