package hr.staff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.services.StaffService;
import general.services.UpdFileService;
import general.tables.Staff;
import general.tables.UpdFile;
import user.User;

@ManagedBean(name = "hrStaffFileBean")
@ViewScoped
public class HrStaffFile implements Serializable {

	@PostConstruct
	public void init() {

	}

	private List<UpdFile> files;

	public List<UpdFile> getFiles() {
		return files;
	}

	public void setFiles(List<UpdFile> files) {
		this.files = files;
	}

	public void loadFiles() {
		files = new ArrayList<>();
		if (selectedStaff != null) {
			StaffService staffService = appContext.getContext().getBean("staffService", StaffService.class);
			files = staffService.findStaffAllFiles(selectedStaff.getStaff_id());
		}
	}

	private Staff selectedStaff;

	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void setSelectedStaff(Staff selectedStaff) {
		this.selectedStaff = selectedStaff;
	}

	private UpdFile previewFile;

	public UpdFile getPreviewFile() {
		return previewFile;
	}

	public void setPreviewFile(UpdFile previewFile) {
		this.previewFile = previewFile;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void handleFileUpload(FileUploadEvent event) {
		StaffService staffService = appContext.getContext().getBean("staffService", StaffService.class);
		UpdFileService fileService = appContext.getContext().getBean("updFileService", UpdFileService.class);
		UploadedFile uploadedFile = event.getFile();
		UpdFile updFile = new UpdFile();
		updFile.setCreated_by(userData.getUserid());
		updFile.setCreated_date(Calendar.getInstance().getTime());
		updFile.setFile_name(uploadedFile.getFileName());
		updFile.setFile_size(uploadedFile.getSize());
		updFile.setMime_type(uploadedFile.getContentType());

		try {
			if (selectedStaff == null) {
				throw new DAOException("Stf Error");
			}
			fileService.create(uploadedFile, updFile, userData);
			staffService.saveStaffFile(selectedStaff, updFile, userData);
			loadFiles();
			GeneralUtil.addSuccessMessage("Файл загружен успешно");

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void deleteFile(Long id) {
		if (selectedStaff != null) {
			StaffService staffService = appContext.getContext().getBean("staffService", StaffService.class);
			staffService.deleteStaffFile(selectedStaff.getStaff_id(), id);
		}
		loadFiles();
		GeneralUtil.addSuccessMessage("Файл удален успешно");
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
