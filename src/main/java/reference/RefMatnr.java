package reference;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.dao.MatnrSparePartDao;
import general.dao.MatnrWarDao;
import general.services.MatnrService;
import general.tables.Matnr;
import general.tables.MatnrSparePart;
import general.tables.MatnrWar;
import general.tables.search.MatnrSearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import user.User;

import static general.util.GlobalValues.matnrImagePath;

@ManagedBean(name = "refMatnr")
@ViewScoped
public class RefMatnr implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String transactionCode = "REFMATNR";
	private static final Long transactionId = 114L;
	private MatnrSearch searchModel = new MatnrSearch();
	private List<Matnr> items;
	private Matnr selected;

	@PostConstruct
	public void init() {
		// TODO PERMISSION
		if (!GeneralUtil.isAjaxRequest()) {
			PermissionController.canRead(userData, transactionId);
			loadItems();
			prepareFnoItems();
		}
	}

	public boolean canAll() {
		return PermissionController.canAll(userData, transactionId);
	}

	private List<MatnrFno> fnoItems = new ArrayList<RefMatnr.MatnrFno>();

	public List<MatnrFno> getFnoItems() {
		return fnoItems;
	}

	public void setFnoItems(List<MatnrFno> fnoItems) {
		this.fnoItems = fnoItems;
	}

	private void prepareFnoItems() {
		fnoItems = new ArrayList<RefMatnr.MatnrFno>();
		MatnrFno mf = new MatnrFno();
		mf.setKey("IS_M");
		mf.setVal("Мембрана");
		fnoItems.add(mf);
	}

	public MatnrSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(MatnrSearch searchModel) {
		this.searchModel = searchModel;
	}

	public List<Matnr> getItems() {
		return items;
	}

	public void loadItems() {
		MatnrDao md = (MatnrDao) appContext.getContext().getBean("matnrDao");
		items = md.findAll(this.searchModel.getCondition());
	}

	public Matnr getSelected() {
		return selected;
	}

	public void setSelected(Matnr selected) {
		this.selected = selected;
		prepareCatItems();
		prepareSelectedCats();
		loadMW(selected);
		prepareSelectedFno();
		setImages();
	}

	private void prepareSelectedFno() {
		if (selected != null) {
			selectedFno = new String[1];
			if (selected.getIs_m() > 0) {
				selectedFno[0] = "IS_M";
			}
		}
	}

	private void loadMW(Matnr m) {
		oldMatnrWar = null;
		if (m != null) {
			MatnrWarDao mwDao = (MatnrWarDao) appContext.getContext().getBean("matnrWarDao");
			List<MatnrWar> mwList = mwDao.findAll(" matnr_id = " + m.getMatnr() + " ORDER BY from_date DESC ");
			if (mwList.size() > 0) {
				oldMatnrWar = mwList.get(0);

			}

			matnrWar = new MatnrWar();
			matnrWar.setMatnr_id(m.getMatnr());
		}
	}

	public Matnr prepareCreate() {
		this.selected = new Matnr();
		prepareCatItems();
		prepareSelectedCats();
		setOldMatnrWar(null);
		setMatnrWar(new MatnrWar());
		return this.selected;
	}

	public void Save() {
		try {
			if (this.selected == null) {
				throw new DAOException("Selected is NULL");
			}

			selectedCats = new ArrayList<Long>();
			for (String s : selectedCatIds) {
				selectedCats.add(new Long(s));
			}

			selected.setIs_m(0);
			if (selectedFno != null) {
				for (int i = 0; i < selectedFno.length; i++) {
					if (selectedFno[i] != null) {
						if (selectedFno[i].equals("IS_M")) {
							selected.setIs_m(1);
						}
					}
				}
			}

			if (this.selected.getMatnr() == null) {
				this.Create();
			} else {
				this.Update();
			}
			GeneralUtil.addSuccessMessage("Сохранено успешно");
			GeneralUtil.hideDialog("MatnrCreateDialog");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		MatnrService mService = (MatnrService) appContext.getContext().getBean("matnrService");
		mService.create(this.selected, selectedCats, matnrWar);
	}

	private void Update() {
		MatnrService mService = (MatnrService) appContext.getContext().getBean("matnrService");
		mService.update(this.selected, selectedCats, matnrWar, oldMatnrWar, userData.getUserid());
	}

	public void Reset() {

	}

	List<Matnr> catItems;
	List<MatnrSparePart> spItems;
	private List<Long> selectedCats;

	private void prepareCatItems() {
		MatnrDao mDao = (MatnrDao) appContext.getContext().getBean("matnrDao");
		String cond = " type = 1 ";
		// if (!Validation.isEmptyString(searchModel.getBukrs())) {
		// cond += " AND bukrs = '" + searchModel.getBukrs() + "' ";
		// }

		catItems = mDao.findAll(cond);
	}

	private String[] selectedCatIds;

	public String[] getSelectedCatIds() {
		return selectedCatIds;
	}

	public void setSelectedCatIds(String[] selectedCatIds) {
		this.selectedCatIds = selectedCatIds;
		// for (String s : selectedCatIds) {
		// System.out.println("SELECTED: " + s);
		// ;
		// }
	}

	private void prepareSelectedCats() {
		selectedCats = new ArrayList<Long>();
		spItems = new ArrayList<MatnrSparePart>();
		MatnrSparePartDao mspDao = (MatnrSparePartDao) appContext.getContext().getBean("matnrSparePartDao");
		if (selected != null) {
			spItems = mspDao.findAll(" sparepart_id = " + selected.getMatnr());
		}
		selectedCatIds = new String[spItems.size()];
		for (int i = 0; i < spItems.size(); i++) {
			selectedCatIds[i] = spItems.get(i).getTovar_id().toString();
		}

	}

	// public void CreateBu() {
	// if (selected != null) {
	// try {
	// if (selected.getType() != 2 || selected.getCode().startsWith("BU")) {
	// throw new DAOException("Материал не подлежит к создани БУ");
	// }
	// Matnr buMatnr = new Matnr();
	// BeanUtils.copyProperties(selected, buMatnr);
	// buMatnr.setMatnr(null);
	// buMatnr.setCode("BU" + selected.getCode());
	// MatnrService mService = (MatnrService) appContext.getContext()
	// .getBean("matnrService");
	// mService.create(buMatnr, selectedCats, matnrWar);
	// GeneralUtil.addSuccessMessage("Создан БУ материала "
	// + selected.getText45());
	//
	// } catch (DAOException e) {
	// GeneralUtil.addErrorMessage(e.getMessage());
	// }
	// }
	// }

	public List<Matnr> getCatItems() {
		return catItems;
	}

	public List<Long> getSelectedCats() {
		return selectedCats;
	}

	private MatnrWar oldMatnrWar;

	public MatnrWar getOldMatnrWar() {
		return oldMatnrWar;
	}

	public void setOldMatnrWar(MatnrWar oldMatnrWar) {
		this.oldMatnrWar = oldMatnrWar;
	}

	private MatnrWar matnrWar;

	public MatnrWar getMatnrWar() {
		return matnrWar;
	}

	public void setMatnrWar(MatnrWar matnrWar) {
		this.matnrWar = matnrWar;
	}

	private boolean oldMatnrEdit = false;

	public boolean getOldMatnrEdit() {
		return oldMatnrEdit;
	}

	public void setOldMatnrEdit(boolean oldMatnrEdit) {
		this.oldMatnrEdit = oldMatnrEdit;
	}

	public boolean checkMatnrType(Long mtId) {
		if (mtId == null) {
			return true;
		}
		return mtId != 3;
	}

	private String[] selectedFno;

	public String[] getSelectedFno() {
		return selectedFno;
	}

	public void setSelectedFno(String[] selectedFno) {
		this.selectedFno = selectedFno;
	}

	public class MatnrFno {
		private String key;
		private String val;

		public MatnrFno() {
			// TODO Auto-generated constructor stub
		}

		public MatnrFno(String k, String v) {
			this.key = k;
			this.val = v;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getVal() {
			return val;
		}

		public void setVal(String val) {
			this.val = val;
		}

	}

	@ManagedProperty(value = "#{appContext}")
	AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{userinfo}")
	User userData;

	public void setUserData(User userData) {
		this.userData = userData;
	}

	public void exportXLS(Object document) {
		// System.out.println(document);
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);

		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
			HSSFCell cell = header.getCell(i);

			cell.setCellStyle(cellStyle);
		}
	}

	List<String> images = new ArrayList<>();

	private String getDirPathFromProperty() {

		return matnrImagePath;

	}

	private void setImages() {
		images = new ArrayList<>();
		if (selected != null) {
			String dirPath = getDirPathFromProperty() + File.separator + selected.getCode();
			File f = new File(dirPath);
			if (f.isDirectory()) {
				for (File img : f.listFiles()) {
					if (img.getName().endsWith(".png")) {
						images.add(img.getName());
					}
				}
			}
		}
	}

	public List<String> getImages() {
		return images;
	}

	public StreamedContent getImage(String name) throws IOException {
		File f = new File(getDirPathFromProperty() + File.separator + selected.getCode() + File.separator + name);

		// if (f.exists()) {
		// System.out.println("FILE EXISTS");
		// //System.out.println(f.getPath());
		// if (f.getAbsolutePath().endsWith(".png")) {
		// System.out.println("END: " + f.getAbsolutePath());
		// }
		// }
		return new DefaultStreamedContent(new FileInputStream(f));

		// return null;
	}
}
