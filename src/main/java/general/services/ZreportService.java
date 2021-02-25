package general.services;

import java.io.IOException;

import general.dao.DAOException;
import general.tables.Zreport;

import org.primefaces.model.UploadedFile;
import org.springframework.transaction.annotation.Transactional;

public interface ZreportService {
	@Transactional
	public boolean uploadFile(UploadedFile u_file) throws DAOException, IOException;
	
	@Transactional
	public boolean deleteFile(Long a_zreport_id) throws DAOException;
	
	@Transactional
	public Zreport getFile(Long a_zreport_id) throws DAOException;
	
	
	@Transactional
	public boolean changeFileName(Long a_zreport_id,String a_name) throws DAOException;
}
