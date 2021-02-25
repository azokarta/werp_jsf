package general.dao;

import java.util.List;

import general.tables.StaffFile;

public interface StaffFileDao extends GenericDao<StaffFile> {
	public List<StaffFile> findAllByStaffId(Long staffId);

	public List<StaffFile> findAllByStaffIdAndFileId(Long staffId, Long fileId);
}
