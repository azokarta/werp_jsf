package general.beans;

import general.AppContext;
import general.dao.UpdFileDao;
import general.services.UpdFileService;
import general.tables.UpdFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import user.User;

@ManagedBean(name = "fileBean")
@SessionScoped
public class FileBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6713723911770449184L;

	@PostConstruct
	public void init() {

	}

	public void download(Long id) throws Exception {
		UpdFileDao ufDao = (UpdFileDao) appContext.getContext().getBean(
				"updFileDao");
		UpdFile uf = ufDao.find(id);
		if (uf == null) {
			throw new Exception("File Not Found!");
		}

		UpdFileService ufService = (UpdFileService) appContext.getContext()
				.getBean("updFileService");

		File file = new File(ufService.getFilePathWithName(uf));
		HttpServletResponse response = (HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();

		response.setHeader("Content-Disposition",
				"attachment;filename=" + uf.getFile_name());
		response.setContentLength(uf.getFile_size().intValue());
		ServletOutputStream out = null;
		try {
			FileInputStream input = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			out = response.getOutputStream();
			int i = 0;
			while ((i = input.read(buffer)) != -1) {
				out.write(buffer);
				out.flush();
			}
			FacesContext.getCurrentInstance().getResponseComplete();
		} catch (IOException err) {
			err.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException err) {
				err.printStackTrace();
			}
		}
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{userinfo}")
	private User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

}
