package logistics.werks;

import general.AppContext;
import general.GeneralUtil;
import general.Validation;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.dao.MatnrMovementDao;
import general.dao.MatnrMovementItemDao;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.MatnrMovement;
import general.tables.MatnrMovementItem;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "logListBean")
@ViewScoped
public class LogList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5298628247048659918L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			prepareMatnrMap();
			//prepareLogList();
		}
	}

	private Map<Long, Matnr> matnrMap = new HashMap<Long, Matnr>();

	private void prepareMatnrMap() {
		MatnrDao mDao = (MatnrDao) appContext.getContext().getBean("matnrDao");
		matnrMap = mDao.getMappedList("");
	}

	private SearchModel searchModel = new SearchModel();
	public SearchModel getSearchModel() {
		return searchModel;
	}
	public void setSearchModel(SearchModel searchModel) {
		this.searchModel = searchModel;
	}

	List<WerksLogTable> logList = new ArrayList<LogList.WerksLogTable>();

	public List<WerksLogTable> getLogList() {
		return logList;
	}

	public void prepareLogList() {
		outputTable.clear();
		try {
			String error = "";
			if (Validation.isEmptyString(searchModel.getBukrs())) {
				error += "Выберите компанию" + "\n";
			}

			if (Validation.isEmptyLong(searchModel.getWerks())) {
				error += "Выберите склад" + "\n";
			}

			if (searchModel.getFromDate() == null) {
				error += "Введите Дата от";
			}

			if (error.length() > 0) {
				throw new DAOException(error);
			}
			mmItemMap.clear();
			mmList.clear();
			MatnrMovementDao mmDao = (MatnrMovementDao) appContext.getContext()
					.getBean("matnrMovementDao");
			mmList = mmDao.findAll(searchModel.getCondition());
			MatnrMovementItemDao mmiDao = (MatnrMovementItemDao) appContext
					.getContext().getBean("matnrMovementItemDao");
			String[] mmIds = new String[mmList.size()];
			for (int k = 0; k < mmList.size(); k++) {
				mmIds[k] = mmList.get(k).getMm_id().toString();
				Map<Long, List<OutputItemsTable>> tempMap = new HashMap<Long, List<OutputItemsTable>>();
				for (MatnrMovementItem mmi : mmiDao
						.findAllWithMatnrListByMmId(mmList.get(k).getMm_id())) {
					if (mmi.getMatnrList() != null) {
						MatnrList ml = mmi.getMatnrList();
						Matnr m = matnrMap.get(mmi.getMatnr());
						if (m != null) {
							if (!tempMap.containsKey(mmi.getMatnr())) {
								tempMap.put(
										mmi.getMatnr(),
										new ArrayList<LogList.OutputItemsTable>());
							}

							OutputItemsTable oit = new OutputItemsTable();
							oit.setMatnrBarcode(ml.getBarcode());
							oit.setMatnrCode(m.getCode());
							oit.setMatnrName(m.getText45());
							oit.setMenge(1D);
							if (Validation.isEmptyString(ml.getBarcode())) {
								OutputItemsTable oitTemp = tempMap.get(
										mmi.getMatnr()).size() > 0 ? tempMap
										.get(mmi.getMatnr()).get(0) : null;
								if (oitTemp != null) {
									oit.setMenge(oitTemp.getMenge() + 1D);
								}
								if (tempMap.get(mmi.getMatnr()).size() > 0) {
									tempMap.get(mmi.getMatnr()).set(0, oit);
								} else {
									tempMap.get(mmi.getMatnr()).add(oit);
								}

							} else {
								tempMap.get(mmi.getMatnr()).add(oit);
							}
						}
					}
				}
				mmItemMap.put(mmList.get(k).getMm_id(),
						new ArrayList<LogList.OutputItemsTable>());
				for (Entry<Long, List<OutputItemsTable>> e : tempMap.entrySet()) {
					mmItemMap.get(mmList.get(k).getMm_id())
							.addAll(e.getValue());
				}
			}

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private List<MatnrMovement> mmList = new ArrayList<MatnrMovement>();

	public List<MatnrMovement> getMmList() {
		return mmList;
	}

	private Map<Long, List<OutputItemsTable>> mmItemMap = new HashMap<Long, List<OutputItemsTable>>();

	public List<OutputItemsTable> getOutputItems(Long mmId) {
		return mmItemMap.get(mmId);
	}

	Map<Integer, List<OutputItemsTable>> outputItemsMap = new HashMap<Integer, List<OutputItemsTable>>();

	public List<OutputItemsTable> getActionItems(Integer i) {
		return outputItemsMap.get(i);
	}

	public class OutputItemsTable {

		String matnrCode;
		String matnrName;
		Double menge = 0D;
		String matnrBarcode;

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

		public Double getMenge() {
			return menge;
		}

		public void setMenge(Double menge) {
			this.menge = menge;
		}

		public String getMatnrBarcode() {
			return matnrBarcode;
		}

		public void setMatnrBarcode(String matnrBarcode) {
			this.matnrBarcode = matnrBarcode;
		}

	}

	private OutputTable selectedRow;

	public OutputTable getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(OutputTable selectedRow) {
		this.selectedRow = selectedRow;
	}

	List<OutputTable> outputTable = new ArrayList<LogList.OutputTable>();

	public List<OutputTable> getOutputTable() {
		return outputTable;
	}

	public class OutputTable {
		
		private Double inQuantity;
		private Double outQuantity;
		private Double balanceQuantity;
		private String sender;
		private String receiver;
		private String matnrName;
		private String matnrCode;
		public Double getInQuantity() {
			return inQuantity;
		}
		public void setInQuantity(Double inQuantity) {
			this.inQuantity = inQuantity;
		}
		public Double getOutQuantity() {
			return outQuantity;
		}
		public void setOutQuantity(Double outQuantity) {
			this.outQuantity = outQuantity;
		}
		public Double getBalanceQuantity() {
			return balanceQuantity;
		}
		public void setBalanceQuantity(Double balanceQuantity) {
			this.balanceQuantity = balanceQuantity;
		}
		public String getSender() {
			return sender;
		}
		public void setSender(String sender) {
			this.sender = sender;
		}
		public String getReceiver() {
			return receiver;
		}
		public void setReceiver(String receiver) {
			this.receiver = receiver;
		}
		public String getMatnrName() {
			return matnrName;
		}
		public void setMatnrName(String matnrName) {
			this.matnrName = matnrName;
		}
		public String getMatnrCode() {
			return matnrCode;
		}
		public void setMatnrCode(String matnrCode) {
			this.matnrCode = matnrCode;
		}
		
		

	}

	public class InnerTempClass {
		private String matnrCode;
		private String matnrName;
		private String matnrBarcode;
		private String note;
		private Double menge = 0D;

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

		public String getMatnrBarcode() {
			return matnrBarcode;
		}

		public void setMatnrBarcode(String matnrBarcode) {
			this.matnrBarcode = matnrBarcode;
		}

		public String getNote() {
			return note;
		}

		public void setNote(String note) {
			this.note = note;
		}

		public Double getMenge() {
			return menge;
		}

		public void setMenge(Double menge) {
			this.menge = menge;
		}

	}

	public class WerksLogTable {
		Date date;
		private Double inQuantity;
		private Double outQuantity;
		private Double balanceQuantity;
		private String sender;
		private String receiver;
		private String matnrName;
		private String matnrCode;
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public Double getInQuantity() {
			return inQuantity;
		}
		public void setInQuantity(Double inQuantity) {
			this.inQuantity = inQuantity;
		}
		public Double getOutQuantity() {
			return outQuantity;
		}
		public void setOutQuantity(Double outQuantity) {
			this.outQuantity = outQuantity;
		}
		public Double getBalanceQuantity() {
			return balanceQuantity;
		}
		public void setBalanceQuantity(Double balanceQuantity) {
			this.balanceQuantity = balanceQuantity;
		}
		public String getSender() {
			return sender;
		}
		public void setSender(String sender) {
			this.sender = sender;
		}
		public String getReceiver() {
			return receiver;
		}
		public void setReceiver(String receiver) {
			this.receiver = receiver;
		}
		public String getMatnrName() {
			return matnrName;
		}
		public void setMatnrName(String matnrName) {
			this.matnrName = matnrName;
		}
		public String getMatnrCode() {
			return matnrCode;
		}
		public void setMatnrCode(String matnrCode) {
			this.matnrCode = matnrCode;
		}
		
		
	}

	public class SearchModel {
		private String bukrs;
		private Long werks;
		private Date fromDate;
		private Date toDate;

		public String getBukrs() {
			return bukrs;
		}

		public void setBukrs(String bukrs) {
			this.bukrs = bukrs;
		}

		public Long getWerks() {
			return werks;
		}

		public void setWerks(Long werks) {
			this.werks = werks;
		}

		public Date getFromDate() {
			return fromDate;
		}

		public void setFromDate(Date fromDate) {
			this.fromDate = fromDate;
		}

		public Date getToDate() {
			return toDate;
		}

		public void setToDate(Date toDate) {
			this.toDate = toDate;
		}

		public String getCondition() {
			String cond = "";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			if (!Validation.isEmptyLong(werks)) {
				cond += String.format(" (from_werks = %d OR to_werks = %d)",
						werks, werks);
			}

			if (fromDate != null) {
				cond += (cond.length() > 0 ? " AND " : " ") + " mm_date >= '"
						+ sdf.format(fromDate) + "'";
			}

			if (toDate != null) {
				cond += (cond.length() > 0 ? " AND " : " ") + " mm_date <= '"
						+ sdf.format(toDate) + "'";
			}

			return cond;
		}

		public String getConditionForPosting() {
			String cond = "";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			if (!Validation.isEmptyLong(werks)) {
				cond += String.format("werks = %d", werks);
			}

			if (fromDate != null) {
				cond += (cond.length() > 0 ? " AND " : " ")
						+ " posting_date >= '" + sdf.format(fromDate) + "'";
			}

			if (toDate != null) {
				cond += (cond.length() > 0 ? " AND " : " ")
						+ " posting_date <= '" + sdf.format(toDate) + "'";
			}

			return cond;
		}
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	@ManagedProperty(value = "#{userinfo}")
	private User userData;
	public void setUserData(User userData) {
		this.userData = userData;
	}
}
