package reports.logistics;

import f4.WerksF4;
import general.AppContext;
import general.GeneralUtil;
import general.MessageProvider;
import general.Validation;
import general.dao.DAOException;
import general.dao.InvoiceDao;
import general.dao.InvoiceItemDao;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.StaffDao;
import general.tables.Invoice;
import general.tables.InvoiceItem;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.Staff;
import general.tables.Werks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "repLog1Bean")
@ViewScoped
public class RepLog1 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7483620619725791623L;

	@PostConstruct
	public void init() {
		if (!GeneralUtil.isAjaxRequest()) {
			prepareMatnrMap();
			prepareStaffMap();

			barcode = GeneralUtil.getRequestParameter("barcode");
			if (!Validation.isEmptyString(barcode)) {
				Search();
			}
		}
	}

	private Map<Long, Staff> stfMap = new HashMap<Long, Staff>();
	private Map<Long, Matnr> matnrMap = new HashMap<Long, Matnr>();

	private void prepareStaffMap() {
		StaffDao d = (StaffDao) appContext.getContext().getBean("staffDao");
		stfMap = d.getMappedList("");
	}

	private void prepareMatnrMap() {
		MatnrDao d = (MatnrDao) appContext.getContext().getBean("matnrDao");
		matnrMap = d.getMappedList("");
	}

	private String barcode;

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		if (!Validation.isEmptyString(barcode)) {
			this.barcode = barcode.trim();
		}
	}

	private MatnrList ml;

	public MatnrList getMl() {
		return ml;
	}

	public String getMlStatusName() {
		if (ml != null) {
			MessageProvider mp = new MessageProvider();
			if (ml.getStatus().equals(MatnrList.STATUS_ACCOUNTABILITY)) {
				return mp.getValue("logistics.matnr_status_acc");
				//return "В подотчете";
			} else if (ml.getStatus().equals(MatnrList.STATUS_MOVING)) {
				//return "В пути";
				return mp.getValue("logistics.matnr_status_moving");
			} else if (ml.getStatus().equals(MatnrList.STATUS_RESERVED)) {
				//return "Зарезервирован в договоре";
				return mp.getValue("logistics.matnr_status_reserved");
			} else if (ml.getStatus().equals(MatnrList.STATUS_RECEIVED)) {
				//return "На складе";
				return mp.getValue("logistics.matnr_status_in_whouse");
			} else if (ml.getStatus().equals(MatnrList.STATUS_SOLD)) {
				//return "Продан";
				return mp.getValue("logistics.matnr_status_sold");
			} else if (MatnrList.STATUS_LOSS.equals(ml.getStatus())) {
				//return "Потеря";
				return mp.getValue("logistics.matnr_status_sold");
			}else if(MatnrList.STATUS_MINI_CONTRACT.equals(ml.getStatus())){
				//return "Зарезервирован в мини договоре";
				return mp.getValue("logistics.matnr_status_reserved_mini");
			}
			return ml.getStatusName();
		}

		return null;
	}

	/**
	 * 
	 * @param stfId
	 * @return ФИО подотчетника текущего материала
	 */
	public String getCurrentStfName() {

		if (ml != null && !Validation.isEmptyLong(ml.getStaff_id()) && stfMap.containsKey(ml.getStaff_id())) {
			return stfMap.get(ml.getStaff_id()).getLF();
		}

		return null;
	}

	private void loadMl() {
		if (!Validation.isEmptyString(barcode)) {
			MatnrListDao mlDao = (MatnrListDao) appContext.getContext().getBean("matnrListDao");
			ml = mlDao.findByBarcode(barcode);

			if (ml != null) {
				Matnr m = matnrMap.get(ml.getMatnr());
				ml.setMatnrObject(m);

				// if(!Validation.isEmptyLong(ml.getStaff_id()) &&
				// stfMap.containsKey(ml.getStaff_id())){
				// ml.setStaff(stfMap.get(ml.getStaff_id()));
				// }
			}
		}
	}

	public void Search() {
		try {
			Map<Long, Werks> werksMap = werksF4.getL_werks_map();
			items = new ArrayList<RepLog1.OutputTable>();
			if (Validation.isEmptyString(barcode)) {
				throw new DAOException("Напишите номер");
			}
			loadMl();
			InvoiceItemDao iiDao = (InvoiceItemDao) appContext.getContext().getBean("invoiceItemDao");
			List<InvoiceItem> iiList = iiDao.findAllForRep1(barcode);
			if (iiList.size() > 0) {

				String[] ids = new String[iiList.size()];
				for (int i = 0; i < iiList.size(); i++) {
					ids[i] = iiList.get(i).getInvoice_id().toString();
				}

				InvoiceDao inDao = (InvoiceDao) appContext.getContext().getBean("invoiceDao");

				List<Invoice> inList = inDao.findAll(String.format(
						" id IN(%s) AND (status_id = %d OR (status_id = %d AND type_id = %d) OR (status_id=%d AND type_id=%d)) ORDER BY id ASC ",
						"'" + String.join("','", ids) + "'", Invoice.STATUS_DONE, Invoice.STATUS_NEW,
						Invoice.TYPE_WRITEOFF_DOC, Invoice.STATUS_ARCHIVE, Invoice.TYPE_WRITEOFF));
				MessageProvider mp = new MessageProvider();
				for (Invoice in : inList) {
					// System.out.println("INID: " + in.getId());
					OutputTable ot = new OutputTable();

					Werks fromWerks = werksMap.get(in.getFrom_werks());
					Werks toWerks = werksMap.get(in.getTo_werks());

					if (in.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY)) {

						if (fromWerks != null) {
							ot.setSender(fromWerks.getText45());
						}

						Staff stf = stfMap.get(in.getResponsible_id());
						if (stf != null) {
							ot.setReceiver(stf.getLF());
						}

						//ot.setActionName("ПОДОТЧЕТ");
						ot.setActionName(mp.getValue("logistics.doc_type_acc"));
					} else if (in.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY_RETURN)) {

						if (toWerks != null) {
							ot.setReceiver(toWerks.getText45());
						}

						Staff stf = stfMap.get(in.getResponsible_id());
						if (stf != null) {
							ot.setSender(stf.getLF());
						}

						//ot.setActionName("ВОЗВРАТ С ПОДОТЧЕТА");
						ot.setActionName(mp.getValue("logistics.doc_type_return_acc"));

					} else if (in.getType_id().equals(Invoice.TYPE_POSTING)) {
						//ot.setActionName("ОПРИХОДОВАНИЕ");
						ot.setActionName(mp.getValue("logistics.doc_type_posting"));
						if (toWerks != null) {
							ot.setReceiver(toWerks.getText45());
						}
					} else if (in.getType_id().equals(Invoice.TYPE_RETURN)) {
						//ot.setActionName("ВОЗВРАТ");
						ot.setActionName(mp.getValue("logistics.doc_type_return"));
						if (toWerks != null) {
							ot.setReceiver(toWerks.getText45());
						}
					} else if (in.getType_id().equals(Invoice.TYPE_SEND)) {
						ot.setActionName(mp.getValue("logistics.doc_type_send"));
						//ot.setActionName("ОТПРАВКА");
						if (toWerks != null) {
							ot.setReceiver(toWerks.getText45());
						}
						if (fromWerks != null) {
							ot.setSender(fromWerks.getText45());
						}
					} else if (in.getType_id().equals(Invoice.TYPE_WRITEOFF)) {
						//ot.setActionName("СПИСАНИЕ");
						ot.setActionName(mp.getValue("logistics.doc_type_writeoff"));
						if (fromWerks != null) {
							ot.setSender(fromWerks.getText45());
						}

					} else if (in.getType_id().equals(Invoice.TYPE_POSTING_IN)) {
						//ot.setActionName("ПОЛУЧЕНИЕ МАТЕРИАЛА");
						ot.setActionName(mp.getValue("logistics.doc_type_receive"));
						if (toWerks != null) {
							ot.setReceiver(toWerks.getText45());
						}

						if (fromWerks != null) {
							ot.setSender(fromWerks.getText45());
						}
					} else if (in.getType_id().equals(Invoice.TYPE_WRITEOFF_DOC)) {
						if (Validation.isEmptyLong(in.getContract_number())) {
							continue;
						}
						//ot.setActionName("РЕЗЕРВИРОВАНИЕ МАТЕРИАЛА В ДОГОВОРЕ");
						ot.setActionName(mp.getValue("logistics.doc_type_reserve_in_contract"));
						ot.setSender(fromWerks == null ? "" : fromWerks.getText45());
					} else if (in.getType_id().equals(Invoice.TYPE_WRITEOFF_LOSS)) {
						ot.setActionName(mp.getValue("logistics.doc_type_writeoff_lost"));
						//ot.setActionName("СПИСАНИЕ ПО ПОТЕРЕ");
						ot.setSender(fromWerks == null ? "" : fromWerks.getText45());
					} else {
						ot.setActionName("DD " + in.getType_id());
					}

					ot.setId(in.getId());
					ot.setDate(in.getInvoice_date());
					ot.setContractNumber(in.getContract_number());
					ot.setServiceNumber(in.getService_number());

					items.add(ot);
				}
			}
			// for(InvoiceI)
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private List<OutputTable> items = new ArrayList<RepLog1.OutputTable>();

	public List<OutputTable> getItems() {
		return items;
	}

	public class OutputTable {
		private Long id;
		private String actionName;
		private String sender;
		private String receiver;
		private Date date;
		private String note;
		private String documentLink;
		private Long contractNumber;
		private Long serviceNumber;

		public Long getContractNumber() {
			return contractNumber;
		}

		public void setContractNumber(Long contractNumber) {
			if (Validation.isEmptyLong(contractNumber)) {
				this.contractNumber = 0L;
			} else {
				this.contractNumber = contractNumber;
			}
		}

		public Long getServiceNumber() {
			return serviceNumber;
		}

		public void setServiceNumber(Long serviceNumber) {
			if (Validation.isEmptyLong(serviceNumber)) {
				this.serviceNumber = 0L;
			} else {
				this.serviceNumber = serviceNumber;
			}
		}

		public String getDocumentLink() {
			return documentLink;
		}

		public void setDocumentLink(String documentLink) {
			this.documentLink = documentLink;
		}

		public String getActionName() {
			return actionName;
		}

		public void setActionName(String actionName) {
			this.actionName = actionName;
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

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public String getNote() {
			return note;
		}

		public void setNote(String note) {
			this.note = note;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

	}

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

	@ManagedProperty(value = "#{werksF4Bean}")
	WerksF4 werksF4;

	public WerksF4 getWerksF4() {
		return werksF4;
	}

	public void setWerksF4(WerksF4 werksF4) {
		this.werksF4 = werksF4;
	}
}
