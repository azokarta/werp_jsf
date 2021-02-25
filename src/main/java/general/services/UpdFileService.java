package general.services;

import javax.servlet.http.Part;

import general.dao.DAOException;
import general.tables.UpdFile;

import org.primefaces.model.UploadedFile;
import org.springframework.transaction.annotation.Transactional;

import user.User;



public interface UpdFileService {
	@Transactional
	public void create(Part p, UpdFile file, User userData) throws DAOException;
	
	@Transactional
	public void create(UploadedFile updFile, UpdFile file, User userData) throws DAOException;
	
	public String getFilePathWithName(UpdFile uf);
	
	UpdFile findOne(Long id);
}
