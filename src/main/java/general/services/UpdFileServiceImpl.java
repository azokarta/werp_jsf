package general.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Calendar;

import javax.servlet.http.Part;

import general.dao.DAOException;
import general.dao.UpdFileDao;
import general.tables.UpdFile;

import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import user.User;

@Service("updFileService")
public class UpdFileServiceImpl implements UpdFileService {

	@Autowired
	UpdFileDao fileDao;

	@Value("${upd_path}")
	private String updPath;

	@Override
	public void create(Part p, UpdFile file, User userData) throws DAOException {
		String error = validate(file, userData);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		fileDao.create(file);
		try {
			saveFile(p, file.getId());
		} catch (IOException e) {
			throw new DAOException(e);
		}
	}

	private String validate(UpdFile f, User userData) {
		String error = "";
		f.setCreated_by(userData.getUserid());
		f.setCreated_date(Calendar.getInstance().getTime());
		// TODO validate mime type, size
		return error;
	}

	private <T> void saveFile(T p, Long id) throws IOException {
		InputStream in = null;
		if (p instanceof Part) {
			try {
				in = ((Part) p).getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (p instanceof UploadedFile) {
			in = ((UploadedFile) p).getInputstream();
		} else {
			throw new DAOException("Upd File Error");
		}

		try {

			Files.copy(in, new File(getPreparedUpdPath(), id.toString()).toPath());
		} catch (IOException e) {
			throw new DAOException(e);
		}
	}

	private String getPreparedUpdPath() {
		Calendar cld = Calendar.getInstance();
		String path = updPath + File.separator + cld.get(Calendar.YEAR);
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdir();
		}

		path += File.separator + (cld.get(Calendar.MONTH) + 1);
		dir = new File(path);
		if (!dir.exists()) {
			dir.mkdir();
		}

		return path;

	}

	@Override
	public String getFilePathWithName(UpdFile uf) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(uf.getCreated_date());
		return updPath + File.separator + cld.get(Calendar.YEAR) + File.separator + (cld.get(Calendar.MONTH) + 1)
				+ File.separator + uf.getId();
	}

	@Override
	public void create(UploadedFile p, UpdFile file, User userData) throws DAOException {
		String error = validate(file, userData);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		fileDao.create(file);

		try {
			saveFile(p, file.getId());
		} catch (IOException e) {
			throw new DAOException(e);
		}
	}

	@Override
	public UpdFile findOne(Long id) {
		return fileDao.find(id);
	}
}