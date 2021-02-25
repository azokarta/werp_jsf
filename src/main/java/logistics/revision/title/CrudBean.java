package logistics.revision.title;

import java.io.InputStream;
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
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.primefaces.event.CellEditEvent;

import f4.BranchF4;
import f4.BukrsF4;
import f4.PositionF4;
import f4.WerksF4;
import finance.reports.Frep6.OutputTable;
import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.services.RevisionService;
import general.services.ZreportService;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.RevItem;
import general.tables.RevItemTitle;
import general.tables.RevItemType;
import general.tables.Revision;
import general.tables.Zreport;
import user.User;

@ManagedBean(name = "logRevTitleCrudBean")
@ViewScoped
public class CrudBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5383604590389130295L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			try {
				revId = new Long(GeneralUtil.getRequestParameter("revId"));
			} catch (NumberFormatException e) {

			}

			try {
				id = new Long(GeneralUtil.getRequestParameter("id"));
			} catch (NumberFormatException e) {

			}
			prepareMatnrMap();
		}
	}

	private Long revId;
	private Long id;
	private Revision revision;
	private RevItemTitle revItemTitle;

	public RevItemTitle getRevItemTitle() {
		return revItemTitle;
	}

	public void setRevItemTitle(RevItemTitle revItemTitle) {
		this.revItemTitle = revItemTitle;
	}

	public Revision getRevision() {
		return revision;
	}

	private void loadRevision() {
		RevisionService revSer = appContext.getContext().getBean("revisionService", RevisionService.class);
		revision = revSer.findWithDetail(revId);
	}

	Map<Long, Matnr> matnrMap = new HashMap<>();

	public Map<Long, Matnr> getMatnrMap() {
		return matnrMap;
	}

	private void prepareMatnrMap() {
		MatnrDao mDao = appContext.getContext().getBean("matnrDao", MatnrDao.class);
		matnrMap = mDao.getMappedList("");
	}

	private void loadOrBlankRevItemTitle() {
		if (mode.equals("create")) {
			revItemTitle = new RevItemTitle();
			revItemTitle.setRevId(revision.getId());
			loadMatnrItems();
		} else {
			revItemTitle = getRevService().findRevTitle(id);
			items = getRevService().findItemsByTitleId(id);
			for (RevItem ri : items) {
				ri.setMatnrObject(matnrMap.get(ri.getMatnr()));
			}
			recountItems();
		}
	}

	private String mode;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	private String pageHeader;

	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader() {
		if (mode.equals("create")) {
			pageHeader = "Создание акта материала";
		} else if (mode.equals("update")) {
			pageHeader = "Редактирование акта материала №" + revItemTitle.getId();
		} else if (mode.equals("view")) {
			pageHeader = "Просмотр акта материала №" + revItemTitle.getId();
		}
	}

	public void setRevision(Revision revision) {
		this.revision = revision;
	}

	public void initBean(String mode) {
		if (!GeneralUtil.isAjaxRequest()) {
			this.mode = mode;
			loadRevision();
			loadOrBlankRevItemTitle();
			setPageHeader();
			if ("update".equals(mode)) {
				prepareItemTypeMatnrList();
			}
		}
	}

	private List<Matnr> matnrItems;

	private void loadMatnrItems() {
		MatnrDao mDao = appContext.getContext().getBean("matnrDao", MatnrDao.class);
		matnrItems = mDao.findAll(" type = 1");
	}

	public List<Matnr> getMatnrItems() {
		return matnrItems;
	}

	List<RevItemType> itemTypeList;

	public List<RevItemType> getItemTypeList() {
		if (itemTypeList == null) {
			itemTypeList = getRevService().getItemTypes();
		}
		return itemTypeList;
	}

	private List<RevItem> items;

	public List<RevItem> getItems() {
		return items;
	}

	public void setItems(List<RevItem> items) {
		this.items = items;
	}

	public void Generate() {
		try {
			if (revItemTitle.getTypeId() == null || revItemTitle.getTypeId() == 0) {
				throw new DAOException("Выберите тип материала");
			}
			items = getRevService().findItemsByRevItemTypeId(revision, revItemTitle.getTypeId());
			for (RevItem ri : items) {
				ri.setMatnrObject(matnrMap.get(ri.getMatnr()));
			}
			isGenerated = true;
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private RevisionService getRevService() {
		return appContext.getContext().getBean("revisionService", RevisionService.class);
	}

	public void Save() {
		try {
			if (mode.equals("create")) {
				if (!isGenerated) {
					throw new DAOException("Данные не сформированы");
				}
				for (RevItemType rit : getItemTypeList()) {
					if (rit.getId() == revItemTitle.getTypeId()) {
						revItemTitle.setTitle(rit.getName());
						break;
					}
				}
				getRevService().createItemTitle(revItemTitle, items, userData);
			} else if (mode.equals("update")) {
				getRevService().updateItemTitle(revItemTitle, items, userData);
			}

			GeneralUtil.doRedirect(
					"/logistics/revision/title/View.xhtml?revId=" + revision.getId() + "&id=" + revItemTitle.getId());
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void recountItems() {
		if (items != null) {
			for (RevItem ri : items) {
				if (ri.getFactQuantity() - ri.getDbQuantity() > 0) {
					ri.setOverQuantity(ri.getFactQuantity() - ri.getDbQuantity());
				} else {
					ri.setOverQuantity(0D);
				}
				if (ri.getDbQuantity() - ri.getFactQuantity() > 0) {
					ri.setDeficitQuantity(ri.getDbQuantity() - ri.getFactQuantity());
				} else {
					ri.setDeficitQuantity(0D);
				}

				ri.setDeficitAmount(ri.getDeficitQuantity() * ri.getUnitPrice());
				ri.setOverAmount(ri.getUnitPrice() * ri.getOverQuantity());
			}
		}
	}

	public void onCellEdit(CellEditEvent e) {
		System.out.println("Old VAL: " + e.getRowIndex() + " => OLD VAL:" + e.getOldValue() + " => NEW VAL: "
				+ e.getNewValue().toString());
	}

	Map<Long, MatnrAct> actOuputMap = new HashMap<>();

	public Map<Long, MatnrAct> getActOuputMap() {
		return actOuputMap;
	}

	List<MatnrAct> actItems = new ArrayList<>();

	public List<MatnrAct> getActItems() {
		return actItems;
	}

	public void setActItems(List<MatnrAct> actItems) {
		this.actItems = actItems;
	}

	public void prepareActOutputMap() {
		totalQty = new TotalQty();
		actOuputMap = new HashMap<>();
		for (RevItem ri : items) {
			Long matnrId = ri.getMatnr();
			MatnrAct ma = new MatnrAct();
			if (actOuputMap.containsKey(matnrId)) {
				ma = actOuputMap.get(matnrId);
			}
			ma.setMatnrName(ri.getMatnrObject().getText45());
			if (ri.getFactQuantity() > 0) {
				totalQty.setFactQty(totalQty.getFactQty() + ri.getFactQuantity());
				ma.setMatnr(matnrId);
				ma.setFactQty(ma.getFactQty() + ri.getFactQuantity());

				if (ri.getStateId().equals(MatnrList.STATE_NEW)) {
					ma.setGoodStateQty(ma.getGoodStateQty() + ri.getFactQuantity());
					totalQty.setGoodStateQty(totalQty.getGoodStateQty() + ri.getFactQuantity());
				}

				if (ri.getStateId().equals(MatnrList.STATE_NEED_REPAIR)) {
					ma.setToRepairtyQty(ma.getToRepairtyQty() + ri.getFactQuantity());
					totalQty.setToRepairtyQty(totalQty.getToRepairtyQty() + ri.getFactQuantity());
				}

				if (ri.getStateId().equals(MatnrList.STATE_OFF)) {
					ma.setToWriteoffQty(ma.getToWriteoffQty() + ri.getFactQuantity());
					totalQty.setToWriteoffQty(totalQty.getToWriteoffQty() + ri.getFactQuantity());
				}

				actOuputMap.put(matnrId, ma);
			}
		}

		actItems = new ArrayList<>();
		for (Entry<Long, MatnrAct> e : actOuputMap.entrySet()) {
			actItems.add(e.getValue());
		}
	}

	TotalQty totalQty = new TotalQty();

	public TotalQty getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(TotalQty totalQty) {
		this.totalQty = totalQty;
	}

	public class TotalQty {
		private Double factQty = 0D;
		private Double goodStateQty = 0D;
		private Double toRepairtyQty = 0D;
		private Double toWriteoffQty = 0D;

		public Double getFactQty() {
			return factQty;
		}

		public void setFactQty(Double factQty) {
			this.factQty = factQty;
		}

		public Double getGoodStateQty() {
			return goodStateQty;
		}

		public void setGoodStateQty(Double goodStateQty) {
			this.goodStateQty = goodStateQty;
		}

		public Double getToRepairtyQty() {
			return toRepairtyQty;
		}

		public void setToRepairtyQty(Double toRepairtyQty) {
			this.toRepairtyQty = toRepairtyQty;
		}

		public Double getToWriteoffQty() {
			return toWriteoffQty;
		}

		public void setToWriteoffQty(Double toWriteoffQty) {
			this.toWriteoffQty = toWriteoffQty;
		}

	}

	public class MatnrAct {
		private Long matnr;
		private String matnrName;
		private Double factQty = 0D;
		private Double goodStateQty = 0D;
		private Double toRepairtyQty = 0D;
		private Double toWriteoffQty = 0D;
		private int sortId = 1;
		private String matnrCode;

		public String getMatnrCode() {
			return matnrCode;
		}

		public void setMatnrCode(String matnrCode) {
			this.matnrCode = matnrCode;
		}

		public int getSortId() {
			return sortId;
		}

		public void setSortId(int sortId) {
			this.sortId = sortId;
		}

		public Long getMatnr() {
			return matnr;
		}

		public void setMatnr(Long matnr) {
			this.matnr = matnr;
		}

		public String getMatnrName() {
			return matnrName;
		}

		public void setMatnrName(String matnrName) {
			this.matnrName = matnrName;
		}

		public Double getFactQty() {
			return factQty;
		}

		public void setFactQty(Double factQty) {
			this.factQty = factQty;
		}

		public Double getGoodStateQty() {
			return goodStateQty;
		}

		public void setGoodStateQty(Double goodStateQty) {
			this.goodStateQty = goodStateQty;
		}

		public Double getToRepairtyQty() {
			return toRepairtyQty;
		}

		public void setToRepairtyQty(Double toRepairtyQty) {
			this.toRepairtyQty = toRepairtyQty;
		}

		public Double getToWriteoffQty() {
			return toWriteoffQty;
		}

		public void setToWriteoffQty(Double toWriteoffQty) {
			this.toWriteoffQty = toWriteoffQty;
		}

	}

	List<Matnr> itemTypeMatnrList = new ArrayList<>();

	public List<Matnr> getItemTypeMatnrList() {
		return itemTypeMatnrList;
	}

	private void prepareItemTypeMatnrList() {
		if (revItemTitle.getTypeId() == 1 || revItemTitle.getTypeId() == 2) {
			for (RevItemType rit : getRevService().getItemTypes()) {
				if (rit.getId() == revItemTitle.getTypeId()) {
					for (Long mId : rit.getMatnrIdList()) {
						Matnr m = matnrMap.get(mId);
						if (m != null) {
							itemTypeMatnrList.add(m);
						}
					}
				}
			}
		}
	}

	public void addMatnrRow() {
		if (revItemTitle.getTypeId() == 1 || revItemTitle.getTypeId() == 2) {
			RevItem ri = new RevItem();
			ri.setDbQuantity(0D);
			ri.setFactQuantity(1D);
			items.add(0, ri);
		}
	}

	private boolean isGenerated = false;

	public void onItemTypeChanged() {
		items = new ArrayList<>();
		isGenerated = false;
		// System.out.println("ITEM TYPE CHANGED");
	}

	public boolean canUpdate() {
		if (!"view".equals(mode)) {
			return false;
		}

		if (!Revision.STATUS_IN_ACTION.equals(revision.getStatus())) {
			return false;
		}

		return true;
	}

	public void downloadMatnrAsExcel() {
		try {
			// if (detailedInfo.size()>0)
			// {
			Long a_rep_id = 87L;

			ZreportService zreportService = (ZreportService) appContext.getContext().getBean("zreportService");
			Zreport wa_zr = zreportService.getFile(a_rep_id);
			InputStream in = wa_zr.getFileu().getBinaryStream();

			HSSFWorkbook wb = new HSSFWorkbook(in);
			HSSFSheet sheet = wb.getSheetAt(0);

			HSSFRow row1 = sheet.getRow(1);
			HSSFRow row2 = sheet.getRow(2);

			HSSFCellStyle cellStyle1 = row1.getCell(1).getCellStyle();
			HSSFCellStyle cellStyle2 = row2.getCell(1).getCellStyle();

			int rowNum = 1;
			int count = 1;
			for (RevItem item : items) {
				HSSFRow row = sheet.getRow(rowNum);
				if (row == null) {
					sheet.createRow(rowNum);
					row = sheet.getRow(rowNum);
				}

				HSSFCell cell0 = row.createCell(0);
				cell0.setCellStyle(cellStyle1);

				String matnrName = item.getMatnrObject() == null ? "" : item.getMatnrObject().getText45();
				cell0.setCellValue(matnrName);

				HSSFCell cell1 = row.createCell(1);
				cell1.setCellStyle(cellStyle1);
				cell1.setCellValue(item.getBarcode());

				HSSFCell cell2 = row.createCell(2);
				cell2.setCellStyle(cellStyle1);
				if (item.getStateId() == 1) {
					cell2.setCellValue("Новый");
				} else if (item.getStateId() == 4) {
					cell2.setCellValue("Подлежит к ремонту");
				} else if (item.getStateId() == 3) {
					cell2.setCellValue("Подлежит к списанию");
				}

				HSSFCell cell3 = row.createCell(3);
				cell3.setCellStyle(cellStyle1);
				cell3.setCellValue(item.getFactQuantity() == 1 ? "+" : "-");

				rowNum++;
				count++;
			}

			String contentType = "application/vnd.ms-excel";
			FacesContext fc = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
			response.setHeader("Content-disposition", "attachment; filename=" + wa_zr.getName());
			response.setContentType(contentType);

			ServletOutputStream out = response.getOutputStream();
			wb.write(out);

			out.flush();
			out.close();
			fc.responseComplete();

		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println(e.getMessage());
		}
	}
	
	public void downloadPartAsExcel() {
		try {
			// if (detailedInfo.size()>0)
			// {
			Long a_rep_id = 89L;

			ZreportService zreportService = (ZreportService) appContext.getContext().getBean("zreportService");
			Zreport wa_zr = zreportService.getFile(a_rep_id);
			InputStream in = wa_zr.getFileu().getBinaryStream();

			HSSFWorkbook wb = new HSSFWorkbook(in);
			HSSFSheet sheet = wb.getSheetAt(0);

			HSSFRow row1 = sheet.getRow(1);
			HSSFRow row2 = sheet.getRow(2);

			HSSFCellStyle cellStyle1 = row1.getCell(1).getCellStyle();
			HSSFCellStyle cellStyle2 = row2.getCell(1).getCellStyle();

			int rowNum = 1;
			int count = 1;
			for (RevItem item : items) {
				HSSFRow row = sheet.getRow(rowNum);
				if (row == null) {
					sheet.createRow(rowNum);
					row = sheet.getRow(rowNum);
				}

				HSSFCell cell0 = row.createCell(0);
				cell0.setCellStyle(cellStyle1);

				String matnrCode = item.getMatnrObject() == null ? "" : item.getMatnrObject().getCode();
				String matnrName = item.getMatnrObject() == null ? "" : item.getMatnrObject().getText45();
				cell0.setCellValue(matnrCode);

				HSSFCell cell1 = row.createCell(1);
				cell1.setCellStyle(cellStyle1);
				cell1.setCellValue(matnrName);

				HSSFCell cell2 = row.createCell(2);
				cell2.setCellStyle(cellStyle1);
				cell2.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell2.setCellValue(item.getUnitPrice());;

				HSSFCell cell3 = row.createCell(3);
				cell3.setCellStyle(cellStyle1);
				cell3.setCellValue(item.getDbQuantity());
				
				HSSFCell cell4 = row.createCell(4);
				cell4.setCellStyle(cellStyle1);
				cell4.setCellValue(item.getDeficitQuantity());
				
				HSSFCell cell5 = row.createCell(5);
				cell5.setCellStyle(cellStyle1);
				cell5.setCellValue(item.getOverQuantity());
				
				HSSFCell cell6 = row.createCell(6);
				cell6.setCellStyle(cellStyle1);
				cell6.setCellValue(item.getFactQuantity());
				
				HSSFCell cell7 = row.createCell(7);
				cell7.setCellStyle(cellStyle1);
				cell7.setCellValue(item.getUnusableQuantity());
				
				HSSFCell cell8 = row.createCell(8);
				cell8.setCellStyle(cellStyle1);
				cell8.setCellValue(item.getUsefulQuantity());
				
				HSSFCell cell9 = row.createCell(9);
				cell9.setCellStyle(cellStyle1);
				cell9.setCellValue(item.getOverAmount());
				
				HSSFCell cell10 = row.createCell(10);
				cell10.setCellStyle(cellStyle1);
				cell10.setCellValue(item.getDeficitAmount());

				rowNum++;
				count++;
			}

			String contentType = "application/vnd.ms-excel";
			FacesContext fc = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
			response.setHeader("Content-disposition", "attachment; filename=" + wa_zr.getName());
			response.setContentType(contentType);

			ServletOutputStream out = response.getOutputStream();
			wb.write(out);

			out.flush();
			out.close();
			fc.responseComplete();

		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println(e.getMessage());
		}
	}

	//
	// public void downloadExcel1() {
	// try {
	// prepareActOutputMap();
	// Long a_rep_id = 86L;
	//
	// ZreportService zreportService = (ZreportService)
	// appContext.getContext().getBean("zreportService");
	// Zreport wa_zr = zreportService.getFile(a_rep_id);
	// // changing blob to inputstream
	// InputStream in = wa_zr.getFileu().getBinaryStream();
	//
	// // changing inputstream to HSSFWorkbook
	// XSSFWorkbook workbook = new XSSFWorkbook(in);
	// XSSFSheet sheet = workbook.getSheetAt(0);
	//
	// XSSFRow row0 = sheet.getRow(0);
	// XSSFCellStyle sellStyle1 = row0.getCell(0).getCellStyle();
	// row0.createCell(1);
	// row0.getCell(1).setCellValue("№" + revision.getId());
	//
	// XSSFRow row1 = sheet.getRow(1);
	// row1.createCell(1);
	// row1.getCell(1).setCellValue(bukrsF4Bean.getNameByBukrs(revision.getBukrs()));
	//
	// XSSFRow row2 = sheet.getRow(2);
	// row2.createCell(1);
	// row2.getCell(1).setCellValue(branchF4Bean.getName(revision.getBranchId()));
	//
	// XSSFRow row3 = sheet.getRow(3);
	// row3.createCell(1);
	// row3.getCell(1).setCellValue(werksF4Bean.getName(revision.getWerks() +
	// ""));
	//
	// XSSFRow row4 = sheet.getRow(4);
	// row4.createCell(1);
	// row4.getCell(1).setCellValue(revItemTitle.getTitle());
	//
	// SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	//
	// XSSFRow row5 = sheet.getRow(5);
	// row5.createCell(1);
	// row5.getCell(1).setCellValue(sdf.format(revision.getBeginDate()).toString());
	//
	// XSSFRow row6 = sheet.getRow(6);
	// row6.createCell(1);
	// row6.getCell(1).setCellValue(sdf.format(revision.getFinishDate()).toString());
	//
	// XSSFRow currentRow = null;
	// if (revItemTitle.getTypeId() == 1 || revItemTitle.getTypeId() == 2) {
	// int rowNumber = 9;
	// for (MatnrAct ma : actItems) {
	// currentRow = sheet.getRow(rowNumber);
	// if (currentRow == null) {
	// currentRow = sheet.createRow(rowNumber);
	// // currentRow = sheet.getRow(rowNumber);
	// }
	// currentRow.createCell(0).setCellValue(ma.getMatnrName());
	// currentRow.createCell(1).setCellValue(ma.getFactQty());
	// currentRow.createCell(2).setCellValue(ma.getGoodStateQty());
	// currentRow.createCell(3).setCellValue(ma.getToRepairtyQty());
	// currentRow.createCell(4).setCellValue(ma.getToWriteoffQty());
	// rowNumber++;
	// }
	//
	// currentRow = sheet.createRow(rowNumber);
	// currentRow.createCell(0).setCellValue("ИТОГО:");
	// currentRow.getCell(0).setCellStyle(sellStyle1);
	// currentRow.createCell(1).setCellValue(totalQty.getFactQty());
	// currentRow.createCell(2).setCellValue(totalQty.getGoodStateQty());
	// currentRow.createCell(3).setCellValue(totalQty.getToRepairtyQty());
	// currentRow.createCell(4).setCellValue(totalQty.getToWriteoffQty());
	//
	// currentRow = sheet.createRow(rowNumber + 2);
	// currentRow.createCell(0).setCellValue("Состав комиссии:");
	// currentRow.getCell(0).setCellStyle(sellStyle1);
	// currentRow.createCell(1).setCellValue("ФИО");
	// currentRow.createCell(2).setCellValue("Подпись");
	//
	// rowNumber = sheet.getLastRowNum() + 1;
	// for (RevResponsible rr : revision.getRevResponsibles()) {
	// currentRow = sheet.createRow(rowNumber);
	// currentRow.createCell(1).setCellValue((rr.getStaff() == null ? "" :
	// rr.getStaff().getLF()) + " ("
	// + positionF4Bean.getName(rr.getPositionId() + "") + ") ");
	// currentRow.createCell(0).setCellValue("");
	// rowNumber++;
	// }
	// }
	//
	// String contentType = "application/vnd.ms-excel";
	// FacesContext fc = FacesContext.getCurrentInstance();
	// HttpServletResponse response = (HttpServletResponse)
	// fc.getExternalContext().getResponse();
	// response.setHeader("Content-disposition", "attachment; filename=" +
	// wa_zr.getName());
	// response.setContentType(contentType);
	//
	// // writing excel to outputstream
	// ServletOutputStream out = response.getOutputStream();
	// workbook.write(out);
	//
	// // flushing and closing
	// out.flush();
	// out.close();
	// fc.responseComplete();
	// // }
	// // else throw new DAOException("Нет записей");
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// System.out.println(e.getMessage());
	// }
	// }

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty("#{userinfo}")
	User userData;

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	@ManagedProperty("#{bukrsF4Bean}")
	BukrsF4 bukrsF4Bean;

	public BukrsF4 getBukrsF4Bean() {
		return bukrsF4Bean;
	}

	public void setBukrsF4Bean(BukrsF4 bukrsF4Bean) {
		this.bukrsF4Bean = bukrsF4Bean;
	}

	@ManagedProperty("#{branchF4Bean}")
	BranchF4 branchF4Bean;

	public BranchF4 getBranchF4Bean() {
		return branchF4Bean;
	}

	public void setBranchF4Bean(BranchF4 branchF4Bean) {
		this.branchF4Bean = branchF4Bean;
	}

	@ManagedProperty("#{werksF4Bean}")
	WerksF4 werksF4Bean;

	public WerksF4 getWerksF4Bean() {
		return werksF4Bean;
	}

	public void setWerksF4Bean(WerksF4 werksF4Bean) {
		this.werksF4Bean = werksF4Bean;
	}

	@ManagedProperty("#{positionF4Bean}")
	PositionF4 positionF4Bean;

	public PositionF4 getPositionF4Bean() {
		return positionF4Bean;
	}

	public void setPositionF4Bean(PositionF4 positionF4Bean) {
		this.positionF4Bean = positionF4Bean;
	}

}
