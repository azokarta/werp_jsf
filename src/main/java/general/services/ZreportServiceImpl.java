package general.services;

import java.io.IOException;
import java.io.InputStream;


import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import general.dao.DAOException;
import general.dao.ZreportDao;
import general.tables.Zreport;

import org.apache.poi.util.IOUtils;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("zreportService")
public class ZreportServiceImpl implements ZreportService{
	@Autowired
	private ZreportDao zrepDao;
	
	@Override
	public boolean uploadFile(UploadedFile u_file) throws DAOException, IOException {
		
		try {
			
			Zreport wa_zr = new Zreport();
			//wa_zr.setFileu( new javax.sql.rowset.serial.SerialBlob(u_file.getContents()));
			
			InputStream a_in = u_file.getInputstream();
			byte[] bytes = IOUtils.toByteArray(a_in);
			wa_zr.setFileu(new SerialBlob(bytes));
			wa_zr.setName(u_file.getFileName());
			zrepDao.create(wa_zr);
			
		} catch (SerialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public boolean deleteFile(Long a_zreport_id) throws DAOException {
		// TODO Auto-generated method stub
		zrepDao.delete(a_zreport_id);
		return true;
	}
	@Override
	public boolean changeFileName(Long a_zreport_id,String a_name) throws DAOException {
		// TODO Auto-generated method stub
		Zreport wa_zr = new Zreport();
		wa_zr = zrepDao.find(a_zreport_id);
		wa_zr.setName(a_name);
		zrepDao.update(wa_zr);
		return true;
	}
	@Override
	public Zreport getFile(Long a_zreport_id) throws DAOException {
		// TODO Auto-generated method stub
		
		return zrepDao.find(a_zreport_id);
	}
	


}
