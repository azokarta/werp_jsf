package logistics.werks;

import general.GeneralUtil;
import general.Validation;
import general.dao.MatnrMovementDao;
import general.dao.MatnrMovementItemDao;
import general.dao.PostingDao;
import general.dao.StaffDao;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.MatnrMovement;
import general.tables.MatnrMovementItem;
import general.tables.Posting;
import general.tables.Staff;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import logistics.LgBase;

@ManagedBean(name = "matnrHistoryBean")
@ViewScoped
public class MatnrHistoryBean extends LgBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9143075553860321991L;

	@PostConstruct
	public void init() {
		try {
			matnrListId = Long.valueOf(GeneralUtil
					.getRequestParameter("matnrListId"));
		} catch (Exception e) {
			matnrListId = 0L;
		}

		try {
			loadMatnrList();
		} catch (Exception e) {

		}
		setMatnrListStatus();
		prepareHistoryTable();
	}

	private Long matnrListId;
	private MatnrList matnrList;

	private void loadMatnrList() throws Exception {
		matnrList = getMatnrListDao().find(matnrListId);
		if (matnrList == null) {
			throw new Exception("Материал не найден!");
		}

		Matnr matnr = getMatnrDao().find(matnrList.getMatnr());
		if (matnr == null) {
			throw new Exception("Материал не найден!");
		}

		matnrList.setMatnrName(matnr.getText45());
		matnrList.setMatnrCode(matnr.getCode());

		if (!Validation.isEmptyLong(matnrList.getStaff_id())) {
			StaffDao sd = (StaffDao) getAppContext().getContext().getBean(
					"staffDao");
			Staff staff = sd.find(matnrList.getStaff_id());
			if (staff != null) {
				matnrList.setStaffName(staff.getLF());
			}
		}

		if (!Validation.isEmptyLong(matnrList.getWerks())) {
			matnrList.setWerksName(getWerksF4Bean().getName(
					String.valueOf(matnrList.getWerks())));
		}
	}

	public MatnrList getMatnrList() {
		return matnrList;
	}

	private String matnrListStatus;

	public void setMatnrListStatus() {
		if (matnrList.getStatus().equals(MatnrList.STATUS_ACCOUNTABILITY)) {
			matnrListStatus = "В подотчете ( " + matnrList.getStaffName()
					+ ") ";
		} else if (matnrList.getStatus().equals(MatnrList.STATUS_MOVING)
				|| matnrList.getStatus().equals(MatnrList.STATUS_MOVING_TEMP)) {
			matnrListStatus = "В перемещении";
		} else if (matnrList.getStatus().equals(MatnrList.STATUS_RECEIVED)) {
			matnrListStatus = "На складе ( " + matnrList.getWerksName() + ") ";
		} else if (matnrList.getStatus().equals(MatnrList.STATUS_SOLD)) {
			matnrListStatus = "Продан";
		}
	}

	public String getMatnrListStatus() {
		return matnrListStatus;
	}

	private List<MatnrHistoryTable> historyTable = new ArrayList<MatnrHistoryBean.MatnrHistoryTable>();

	public List<MatnrHistoryTable> getHistoryTable() {
		return historyTable;
	}

	private void prepareHistoryTable() {
		MatnrMovementItemDao mmiDao = (MatnrMovementItemDao) getAppContext()
				.getContext().getBean("matnrMovementItemDao");
		List<MatnrMovementItem> temp = mmiDao.findAll(" matnr_list_id = "
				+ matnrList.getMatnr_list_id());
		String[] ids = new String[temp.size()];
		for (int i = 0; i < temp.size(); i++) {
			ids[i] = temp.get(i).getMm_id().toString();
		}

		String cond = String.format("mm_id IN(%s)",
				"'" + String.join("','", ids) + "'");
		MatnrMovementDao mmDao = (MatnrMovementDao) getAppContext()
				.getContext().getBean("matnrMovementDao");
		Map<Long, MatnrMovement> mmMap = new HashMap<Long, MatnrMovement>();
		for (MatnrMovement mm : mmDao.findAll(cond)) {
			mmMap.put(mm.getMm_id(), mm);
		}

		// if (!Validation.isEmptyLong(matnrList.getPosting_id())) {
		// PostingDao pd = (PostingDao) getAppContext().getContext().getBean(
		// "postingDao");
		// Posting posting = pd.find(matnrList.getPosting_id());
		// if (posting != null) {
		// historyTable.add(new MatnrHistoryTable("", getWerksF4Bean()
		// .getName(String.valueOf(posting.getWerks())),
		// "ОПРИХОДОВАНИЕ", posting.getCreated_date(), "", 0L));
		// }
		//
		// }
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

		for (MatnrMovementItem mmi : temp) {
			MatnrMovement tempMovement = mmMap.get(mmi.getMm_id());
			if (tempMovement != null) {
				MatnrHistoryTable mHT = new MatnrHistoryTable();
				mHT.setActionType(tempMovement.getTypeName());
				mHT.setDate(tempMovement.getMm_date());
				if (!Validation.isEmptyLong(tempMovement.getFrom_werks())) {
					mHT.setFromWerks(getWerksF4Bean().getName(
							String.valueOf(tempMovement.getFrom_werks())));
				}

				if (!Validation.isEmptyLong(tempMovement.getTo_werks())) {
					mHT.setToWerks(getWerksF4Bean().getName(
							String.valueOf(tempMovement.getTo_werks())));
				}

				if (!Validation.isEmptyLong(tempMovement.getStaff_id())
						&& !Validation.isEmptyString(tempMovement.getStatus())
						&& tempMovement.getStatus().equals(
								MatnrMovement.STATUS_ACCOUNTABILITY)) {
					StaffDao stfDao = (StaffDao) getAppContext().getContext()
							.getBean("staffDao");
					Staff stf = stfDao.find(tempMovement.getStaff_id());
					if (stf != null) {
						mHT.setToWerks(stf.getLF());
					}
				}
				String info = "";
				if (!Validation.isEmptyString(tempMovement.getStatus())
						&& tempMovement.getStatus().equals(
								MatnrMovement.STATUS_ACCOUNTABILITY)) {
					if (mmi.getReceived_date() != null) {
						info += "ПОЛУЧЕНО ОБРАТНО. ДАТА: "
								+ sdf.format(mmi.getReceived_date());
					}
				}
				mHT.setInfo(info
						+ " "
						+ (tempMovement.getNote() == null ? "" : tempMovement
								.getNote()));

				mHT.setMovementItemId(mmi.getItem_id());
				historyTable.add(mHT);
			}
		}

		Collections.sort(historyTable, new Comparator<MatnrHistoryTable>() {

			@Override
			public int compare(MatnrHistoryTable o1, MatnrHistoryTable o2) {
				if (o2.getDate() != null && o1.getDate() != null) {
					return o1.getDate().compareTo(o2.getDate());
				}
				return 0;
			}
		});
	}

	public class MatnrHistoryTable {
		private String fromWerks;
		private String toWerks;
		private String actionType;
		private Date date;
		private String relatedDocument;
		private Long movementItemId;
		private String info;

		public String getInfo() {
			return info;
		}

		public void setInfo(String info) {
			this.info = info;
		}

		public MatnrHistoryTable() {
			// TODO Auto-generated constructor stub
		}

		public MatnrHistoryTable(String fromWerks, String toWerks,
				String actionType, Date date, String relatedDoc, Long movementId) {
			this.fromWerks = fromWerks;
			this.toWerks = toWerks;
			this.actionType = actionType;
			this.date = date;
			this.relatedDocument = relatedDoc;
			this.movementItemId = movementId;
		}

		public Long getMovementItemId() {
			return movementItemId;
		}

		public void setMovementItemId(Long movementItemId) {
			this.movementItemId = movementItemId;
		}

		public String getFromWerks() {
			return fromWerks;
		}

		public void setFromWerks(String fromWerks) {
			this.fromWerks = fromWerks;
		}

		public String getToWerks() {
			return toWerks;
		}

		public void setToWerks(String toWerks) {
			this.toWerks = toWerks;
		}

		public String getActionType() {
			return actionType;
		}

		public void setActionType(String actionType) {
			this.actionType = actionType;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public String getRelatedDocument() {
			return relatedDocument;
		}

		public void setRelatedDocument(String relatedDocument) {
			this.relatedDocument = relatedDocument;
		}

		public String getRelatedDocUrl() {
			String out = "";
			// if(rela)
			return out;
		}
	}

}
