package logistics.werks;

import general.GeneralUtil;
import general.Validation;
import general.beans.WerksBean;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.Werks;
import general.tables.search.MatnrListSearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import logistics.LgBase;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

@ManagedBean
@ViewScoped
public class MatnrListBean extends LgBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -828560867020632052L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			searchModel.setBukrs(userData.getBukrs());
			loadUserWerks();
		}
	}

	private List<Werks> userWerks = new ArrayList<Werks>();

	private void loadUserWerks() {
		// WerksBranchDao d = (WerksBranchDao)
		// appContext.getContext().getBean("werksBranchDao");
		// userWerks = d.findAllWerksByBranch2(userData.getBranch_id());
		userWerks = werksBean.getUserWerks(searchModel.getBukrs());
	}

	public List<Werks> getUserWerks() {
		return userWerks;
	}

	private Map<Long, Matnr> matnrMap = new HashMap<Long, Matnr>();

	private void prepareMatnrMap() {
		matnrMap.clear();
		String cond = "";
		if (!Validation.isEmptyString(getMatnrCode())) {
			cond = " code LIKE '" + getMatnrCode() + "%' ";
		}

		if (!Validation.isEmptyString(getMatnrName())) {
			cond += (cond.length() > 0 ? " AND " : " ") + " text45 LIKE '%" + getMatnrName() + "%' ";
		}

		List<Matnr> mList = getMatnrDao().findAll(cond);
		for (int k = 0; k < mList.size(); k++) {
			matnrMap.put(mList.get(k).getMatnr(), mList.get(k));
		}
	}

	private String matnrCode;
	private String matnrName;
	private String barcode;

	public String getMatnrCode() {
		return matnrCode;
	}

	public void setMatnrCode(String matnrCode) {
		this.matnrCode = matnrCode;
	}

	public String getMatnrName() {
		return matnrName;
	}

	public void setMatnrName(String matnrName) {
		this.matnrName = matnrName;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	private void prepareOutputTable() {
		loadUserWerks();
		prepareMatnrMap();
		outputTable.clear();
		outputTableMap.clear();

		String cond = searchModel.getCondition();
		if (cond.length() > 0) {
			cond += String.format(" AND status IN('%s','%s','%s','%s') ", MatnrList.STATUS_ACCOUNTABILITY,
					MatnrList.STATUS_RECEIVED, MatnrList.STATUS_RESERVED, MatnrList.STATUS_MINI_CONTRACT);
			if (Validation.isEmptyLong(searchModel.getWerks())) {
				if (userWerks.size() > 0) {
					cond += getUserWerksCond();
				} else {
					cond += " AND werks = -1 ";
				}
			}

			if (!Validation.isEmptyString(barcode)) {
				cond += " AND barcode = '" + getBarcode() + "' ";
			}

			List<MatnrList> mlList = getMatnrListDao().getGrouppedList(cond);
			for (MatnrList ml : mlList) {
				Matnr m = matnrMap.get(ml.getMatnr());
				if (m != null) {

					OutputTable ot;
					Double inWerks = 0D;
					Double inAccount = 0D;
					Double reservedMenge = 0D;
					Double miniContractMenge = 0D;
					if (outputTableMap.get(m.getMatnr()) == null) {
						ot = new OutputTable();
					} else {
						ot = outputTableMap.get(m.getMatnr());
						inWerks = ot.getInWerksMenge();
						inAccount = ot.getInAccountMenge();
						reservedMenge = ot.getReservedMenge();
						miniContractMenge = ot.getMiniContractMenge();
					}

					if (ml.getStatus().equals(MatnrList.STATUS_ACCOUNTABILITY)) {
						inAccount += ml.getMenge();
					} else if (ml.getStatus().equals(MatnrList.STATUS_RESERVED)) {
						reservedMenge += ml.getMenge();
					} else if (ml.getStatus().equals(MatnrList.STATUS_RECEIVED)) {
						inWerks += ml.getMenge();
					} else if (MatnrList.STATUS_MINI_CONTRACT.equals(ml.getStatus())) {
						miniContractMenge += ml.getMenge();
					}

					ot.setMatnr(m.getMatnr());
					ot.setMatnrObject(m);
					ot.setInAccountMenge(inAccount);
					ot.setInWerksMenge(inWerks);
					ot.setReservedMenge(reservedMenge);
					ot.setMiniContractMenge(miniContractMenge);
					ot.setTotalMenge(inAccount + inWerks + reservedMenge + miniContractMenge);
					outputTableMap.put(m.getMatnr(), ot);
				}
			}

			for (Entry<Long, OutputTable> e : outputTableMap.entrySet()) {
				outputTable.add(e.getValue());
			}
		}
	}

	private OutputTable selectedRow;

	public OutputTable getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(OutputTable selectedRow) {
		this.selectedRow = selectedRow;
	}

	private String mlDialogTitle;

	public String getMlDialogTitle() {
		return mlDialogTitle;
	}

	public void setMlDialogTitle(String mlDialogTitle) {
		this.mlDialogTitle = mlDialogTitle;
	}

	private MatnrListSearch searchModel = new MatnrListSearch();

	public MatnrListSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(MatnrListSearch searchModel) {
		this.searchModel = searchModel;
	}

	List<OutputTable> outputTable = new ArrayList<MatnrListBean.OutputTable>();

	public List<OutputTable> getOutputTable() {
		return outputTable;
	}

	private Map<Long, OutputTable> outputTableMap = new HashMap<Long, MatnrListBean.OutputTable>();

	private List<MatnrList> matnrListDetail;

	public void setMatnrListDetail() {
		matnrListDetail = new ArrayList<MatnrList>();
		if (selectedRow != null) {
			String cond = searchModel.getCondition();
			if (cond.length() > 0) {
				cond += String.format(" AND status IN('%s','%s','%s','%s') AND matnr = %d ",
						MatnrList.STATUS_ACCOUNTABILITY, MatnrList.STATUS_RECEIVED, MatnrList.STATUS_RESERVED,
						MatnrList.STATUS_MINI_CONTRACT, selectedRow.getMatnr());
				if (Validation.isEmptyLong(searchModel.getWerks())) {
					if (userWerks != null && userWerks.size() > 0) {
						cond += getUserWerksCond();
					} else {
						cond += " AND werks = -1 ";
					}
				}
				matnrListDetail = getMatnrListDao().findAllWithStaff(cond);
				for (MatnrList ml : matnrListDetail) {
					ml.setMatnrObject(selectedRow.getMatnrObject());
				}
			}

			setMlDialogTitle(selectedRow.getMatnrObject().getText45());
		}
	}

	private String getUserWerksCond() {
		String[] werkIds = new String[userWerks.size()];
		for (int k = 0; k < userWerks.size(); k++) {
			werkIds[k] = userWerks.get(k).getWerks().toString();
		}

		return String.format(" AND werks IN (%s) ", "'" + String.join("','", werkIds) + "' ");
	}

	public List<MatnrList> getMatnrListDetail() {
		return matnrListDetail;
	}

	public class OutputTable {
		private Long matnr;
		private Double inWerksMenge = 0D;
		private Double inAccountMenge = 0D;
		private Double totalMenge = 0D;
		private Matnr matnrObject;
		private Double reservedMenge = 0D;
		private Double miniContractMenge = 0D;

		public Double getMiniContractMenge() {
			return miniContractMenge;
		}

		public void setMiniContractMenge(Double miniContractMenge) {
			this.miniContractMenge = miniContractMenge;
		}

		public Double getReservedMenge() {
			return reservedMenge;
		}

		public void setReservedMenge(Double reservedMenge) {
			this.reservedMenge = reservedMenge;
		}

		public Long getMatnr() {
			return matnr;
		}

		public void setMatnr(Long matnr) {
			this.matnr = matnr;
		}

		public Double getInWerksMenge() {
			return inWerksMenge;
		}

		public void setInWerksMenge(Double inWerksMenge) {
			this.inWerksMenge = inWerksMenge;
		}

		public Double getInAccountMenge() {
			return inAccountMenge;
		}

		public void setInAccountMenge(Double inAccountMenge) {
			this.inAccountMenge = inAccountMenge;
		}

		public Double getTotalMenge() {
			return totalMenge;
		}

		public void setTotalMenge(Double totalMenge) {
			this.totalMenge = totalMenge;
		}

		public Matnr getMatnrObject() {
			return matnrObject;
		}

		public void setMatnrObject(Matnr matnrObject) {
			this.matnrObject = matnrObject;
		}

	}

	public void Search() {
		selectedRow = null;
		prepareOutputTable();
	}

	private List<OutputTable> dialogTable = new ArrayList<MatnrListBean.OutputTable>();

	public List<OutputTable> getDialogTable() {
		return dialogTable;
	}

	public void setDialogTable(List<OutputTable> dialogTable) {
		this.dialogTable = dialogTable;
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

	public void exportMatnrItems(Object document) {
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

	@ManagedProperty(value = "#{werksBean}")
	WerksBean werksBean;

	public WerksBean getWerksBean() {
		return werksBean;
	}

	public void setWerksBean(WerksBean werksBean) {
		this.werksBean = werksBean;
	}

}
