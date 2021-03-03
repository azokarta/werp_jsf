package logistics.invoice;

import general.AppContext;
import general.GeneralUtil;
import general.PermissionController;
import general.dao.DAOException;
import general.services.InvoiceService;
import general.services.MatnrListService;
import general.tables.Invoice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "logInvoiceActionBean", eager = true)
@ViewScoped
public class ActionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8937104162665397292L;
	/**
	 * 
	 */

	private Invoice invoice;

	public void initBean(Invoice i) throws Exception {
		this.invoice = i;
		prepareButtons();
	}

	private List<Button> buttonList = new ArrayList<ActionBean.Button>();

	public List<Button> getButtonList() {
		return buttonList;
	}
	
	private String getActionByLang(String actionName, String lang){
		if("en".equals(lang)){
			if("update".equals(actionName)){
				return "Edit";
			} else if("create_writeoff".equals(actionName)){
				return "Create write-off document";
			} else if("do_cancel".equals(actionName)){
				return "Cancel";
			} else if("do_posting".equals(actionName)){
				return "Receive to warehouse";
			} else if("do_writeoff".equals(actionName)){
				return "Write off";
			} else if("do_send".equals(actionName)){
				return "Send";
			} else if("do_receive".equals(actionName)){
				return "Receive";
			} else if("do_accountability".equals(actionName)){
				return "Send to accountability";
			} else if("do_matnr_state".equals(actionName)){
				return "Set goods states";
			} else if("do_delete".equals(actionName)){
				return "Delete";
			} else if("do_return".equals(actionName)){
				return "Return to warehouse";
			} else if("do_reserve".equals(actionName)){
				return "Reserve";
			} else if("do_print_accountablity".equals(actionName)){
				return "Print";
			} else if("list".equals(actionName)){
				return "Go to list";
			}
		}
		
		if("update".equals(actionName)){
			return "Редактировать";
		} else if("create_writeoff".equals(actionName)){
			return "Создать списание";
		} else if("do_cancel".equals(actionName)){
			return "Отменить";
		} else if("do_posting".equals(actionName)){
			return "Оприходовать";
		} else if("do_writeoff".equals(actionName)){
			return "Списать";
		} else if("do_send".equals(actionName)){
			return "Отправить";
		} else if("do_receive".equals(actionName)){
			return "Оприходовать";
		} else if("do_accountability".equals(actionName)){
			return "Сдать в подотчет";
		} else if("do_matnr_state".equals(actionName)){
			return "Проставить состояние аппаратов";
		} else if("do_delete".equals(actionName)){
			return "Удалить";
		} else if("do_return".equals(actionName)){
			return "Вернуть на склад";
		} else if("do_reserve".equals(actionName)){
			return "Зарезервировать";
		} else if("do_print_accountablity".equals(actionName)){
			return "Распечатать";
		} else if("list".equals(actionName)){
			return "В список";
		}
		
		return "";
	}

	private void prepareButtons() throws Exception {
		if (invoice == null) {
			throw new Exception("Invoice is null");
		}

		boolean isCreator = userData.getUserid().equals(invoice.getCreated_by());
		boolean isSysAdmin = userData.isSys_admin();

		buttonList = new ArrayList<ActionBean.Button>();
		if (invoice.getStatus_id().equals(Invoice.STATUS_NEW)) {
			if (invoice.getIs_system() == 0 && invoice.getCreated_by().equals(userData.getUserid())) {
				Button b1 = new Button("update", "");
				buttonList.add(b1);
			} else if (invoice.getType_id().equals(Invoice.TYPE_RETURN)) {
				Button b1 = new Button("update", "");
				buttonList.add(b1);
			}

			if (invoice.getType_id().equals(Invoice.TYPE_WRITEOFF_DOC)) {

				if (PermissionController.canAll(userData, Invoice.TRANSACTION_ID_WRITEOFF)) {
					Button b2 = new Button("create_writeoff", "");
					buttonList.add(b2);
				}
				if (userData.isSys_admin()) {
					Button b3 = new Button("do_cancel", "");
					buttonList.add(b3);
				}
			} else if (invoice.getType_id().equals(Invoice.TYPE_POSTING)) {
				if (isCreator || isSysAdmin) {
					Button b1 = new Button("do_posting", "");
					buttonList.add(b1);
				}
			} else if (invoice.getType_id().equals(Invoice.TYPE_WRITEOFF)) {

				if (isCreator || isSysAdmin) {
					Button b1 = new Button("do_writeoff", "");
					buttonList.add(b1);
				}
			} else if (invoice.getType_id().equals(Invoice.TYPE_WRITEOFF_LOSS)) {
				if (isCreator || isSysAdmin) {
					Button b1 = new Button("do_writeoff", "");
					buttonList.add(b1);
				}
			} else if (invoice.getType_id().equals(Invoice.TYPE_SEND)) {
				if (isCreator || isSysAdmin) {
					Button b1 = new Button("do_send", "");
					buttonList.add(b1);
				}

			} else if (invoice.getType_id().equals(Invoice.TYPE_POSTING_IN)) {
				// if (isCreator || isSysAdmin) {
				Button b1 = new Button("do_receive", "");
				buttonList.add(b1);
				// }

			} else if (invoice.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY)) {
				if (isCreator || isSysAdmin) {
					Button b1 = new Button("do_accountability", "");
					buttonList.add(b1);

					Button b2 = new Button("do_matnr_state", "");
					buttonList.add(b2);

					Button b3 = new Button("do_delete", "post");
					buttonList.add(b3);
				}

			} else if (invoice.getType_id().equals(Invoice.TYPE_RETURN)) {
				Button b1 = new Button("do_return", "");
				buttonList.add(b1);

				if (isCreator || isSysAdmin) {
					Button b3 = new Button("do_delete", "post");
					buttonList.add(b3);
				}

			} else if (invoice.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY_RETURN)) {
				if (isCreator || isSysAdmin) {
					Button b1 = new Button("do_return", "");
					buttonList.add(b1);

					Button b2 = new Button("do_delete", "post");
					buttonList.add(b2);
				}
			} else if (Invoice.TYPE_MINI_CONTRACT.equals(invoice.getType_id())) {
				if (isCreator || isSysAdmin) {
					Button b1 = new Button("do_reserve", "");
					buttonList.add(b1);

					Button b3 = new Button("do_delete", "post");
					buttonList.add(b3);
				}
			}
		} else if (invoice.getStatus_id().equals(Invoice.STATUS_DONE)) {
			if (invoice.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY)
					|| invoice.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY_RETURN)) {
				Button b1 = new Button("do_print_accountablity", "");
				buttonList.add(b1);
			} else if (Invoice.TYPE_MINI_CONTRACT.equals(invoice.getType_id())) {
				if (isCreator || isSysAdmin) {
					Button b1 = new Button("do_cancel", "");
					buttonList.add(b1);
				}
			}
		}

		Button bToList = new Button("list", "");
		buttonList.add(bToList);
	}

	public class Button {
		private String name;
		private String label;
		private String type;
		private boolean disabled = false;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isDisabled() {
			return disabled;
		}

		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}

		public Button(String name, String type) {
			super();
			this.name = name;
			this.label = getActionByLang(name, userData.getU_language());
			this.type = type;
		}

		public void doAction() {
			setDisabled(true);
			if (getName().equals("update")) {
				if (invoice.getType_id().equals(Invoice.TYPE_WRITEOFF)) {
					GeneralUtil.doRedirect("/logistics/invoice/writeoff/Update.xhtml?id=" + invoice.getId());
				} else if (invoice.getType_id().equals(Invoice.TYPE_POSTING)) {
					GeneralUtil.doRedirect("/logistics/invoice/posting/Update.xhtml?id=" + invoice.getId());
				} else if (invoice.getType_id().equals(Invoice.TYPE_SEND)) {
					GeneralUtil.doRedirect("/logistics/invoice/send/Update.xhtml?id=" + invoice.getId());
				} else if (invoice.getType_id().equals(Invoice.TYPE_POSTING_IN)) {
					GeneralUtil.doRedirect("/logistics/invoice/posting-in/Update.xhtml?id=" + invoice.getId());
				} else if (invoice.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY)) {
					GeneralUtil.doRedirect("/logistics/invoice/accountability/Update.xhtml?id=" + invoice.getId());
				} else if (invoice.getType_id().equals(Invoice.TYPE_RETURN)) {
					GeneralUtil.doRedirect("/logistics/invoice/return/Update.xhtml?id=" + invoice.getId());
				} else if (invoice.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY_RETURN)) {
					GeneralUtil
							.doRedirect("/logistics/invoice/accountability/return/Update.xhtml?id=" + invoice.getId());
				} else if (invoice.getType_id().equals(Invoice.TYPE_WRITEOFF_LOSS)) {
					GeneralUtil.doRedirect("/logistics/invoice/writeoff/loss/Update.xhtml?id=" + invoice.getId());
				} else if (invoice.getType_id().equals(Invoice.TYPE_MINI_CONTRACT)) {
					GeneralUtil.doRedirect("/logistics/invoice/reservation/Update.xhtml?id=" + invoice.getId());
				} else {
					// GeneralUtil
					// .doRedirect("/logistics/invoice/Update.xhtml?id="
					// + invoice.getId());
				}

			} else if (getName().equals("create_writeoff")) {

				GeneralUtil.doRedirect("/logistics/invoice/writeoff/Create.xhtml?parent_id=" + invoice.getId());

			} else if (getName().equals("list")) {

				if (invoice.getType_id().equals(Invoice.TYPE_WRITEOFF_DOC)) {
					GeneralUtil.doRedirect("/logistics/invoice/writeoff-doc/List.xhtml");

				} else if (invoice.getType_id().equals(Invoice.TYPE_WRITEOFF)) {

					GeneralUtil.doRedirect("/logistics/invoice/writeoff/List.xhtml");

				} else if (invoice.getType_id().equals(Invoice.TYPE_WRITEOFF_LOSS)) {

					GeneralUtil.doRedirect("/logistics/invoice/writeoff/loss/List.xhtml");

				} else if (invoice.getType_id().equals(Invoice.TYPE_POSTING)) {
					GeneralUtil.doRedirect("/logistics/invoice/posting/List.xhtml");
				} else if (invoice.getType_id().equals(Invoice.TYPE_SEND)) {
					GeneralUtil.doRedirect("/logistics/invoice/send/List.xhtml");
				} else if (invoice.getType_id().equals(Invoice.TYPE_POSTING_IN)) {
					GeneralUtil.doRedirect("/logistics/invoice/posting-in/List.xhtml");
				} else if (invoice.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY)) {
					GeneralUtil.doRedirect("/logistics/invoice/accountability/List.xhtml");
				} else if (invoice.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY_RETURN)) {
					GeneralUtil.doRedirect("/logistics/invoice/accountability/return/List.xhtml");
				} else if (invoice.getType_id().equals(Invoice.TYPE_RETURN)) {
					GeneralUtil.doRedirect("/logistics/invoice/return/List.xhtml");
				} else if (invoice.getType_id().equals(Invoice.TYPE_MINI_CONTRACT)) {
					GeneralUtil.doRedirect("/logistics/invoice/reservation/List.xhtml");
				}

			} else if (getName().equals("do_posting")) {
				doPosting();
			} else if (getName().equals("do_writeoff")) {
				if (invoice.getType_id().equals(Invoice.TYPE_WRITEOFF)) {
					doWriteoff();
				} else if (invoice.getType_id().equals(Invoice.TYPE_WRITEOFF_LOSS)) {
					doWriteoffLoss();
				}

			} else if (getName().equals("do_send")) {
				doSend();
			} else if (getName().equals("do_receive")) {
				doReceive();
			} else if (getName().equals("do_accountability")) {
				doAccountability();
			} else if (getName().equals("do_return")) {
				if (invoice.getType_id().equals(Invoice.TYPE_RETURN)) {
					doReturn();
				} else if (invoice.getType_id().equals(Invoice.TYPE_ACCOUNTABILITY_RETURN)) {
					doReturnAccountability();
				}

			} else if (getName().equals("do_matnr_state")) {
				doMatnrState();
			} else if (getName().equals("do_print_accountablity")) {
				doPrintAccountability();
			} else if ("do_delete".equals(getName())) {
				if (Invoice.TYPE_ACCOUNTABILITY.equals(invoice.getType_id())) {
					doDelete("accountability");
				} else if (Invoice.TYPE_MINI_CONTRACT.equals(invoice.getType_id())) {
					doDelete("reservation");
				} else if (Invoice.TYPE_RETURN.equals(invoice.getType_id())) {
					doDelete("return");
				} else if (Invoice.TYPE_ACCOUNTABILITY_RETURN.equals(invoice.getType_id())) {
					doDelete("accountability/return");
				}
			} else if ("do_reserve".equals(getName())) {
				doReserve();
			} else if ("do_cancel".equals(getName())) {
				if (Invoice.TYPE_MINI_CONTRACT.equals(invoice.getType_id())) {
					doCancelReservation();
				} else if (Invoice.TYPE_WRITEOFF_DOC.equals(invoice.getType_id())) {
					doCancelWriteoffDoc();
				}
			}

			setDisabled(false);
		}

	}

	private void reloadPage(String s) {
		GeneralUtil.doRedirect("/logistics/invoice/" + s + "/View.xhtml?id=" + invoice.getId());
	}

	/********* ACTIONS ****************/

	private void doCancelReservation() {
		try {
			MatnrListService mls = (MatnrListService) appContext.getContext().getBean("matnrListService");
			mls.doCancelReserve(invoice, userData.getUserid());
			reloadPage("reservation");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void doCancelWriteoffDoc() {
		try {
			MatnrListService mls = (MatnrListService) appContext.getContext().getBean("matnrListService");
			mls.doCancelWriteoffDoc(invoice, userData.getUserid());
			reloadPage("writeoff-doc");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void doDelete(String str) {
		try {
			InvoiceService s = (InvoiceService) appContext.getContext().getBean("invoiceService");
			s.delete(invoice, userData);
			reloadPage(str);
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void doReserve() {
		try {
			MatnrListService mls = (MatnrListService) appContext.getContext().getBean("matnrListService");
			mls.doReserve(invoice, userData);
			reloadPage("reservation");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void doMatnrState() {
		GeneralUtil.showDialog("amsDlg");
	}

	private void doPrintAccountability() {
		GeneralUtil.showDialog("pringDlg");
	}

	public void doReturnAccountability() {
		try {
			if (invoice == null) {
				throw new DAOException("Invoice Is Null");
			}
			if (!invoice.getStatus_id().equals(Invoice.STATUS_NEW)) {
				throw new DAOException("Документ уже оприходован");
			}

			MatnrListService mls = (MatnrListService) appContext.getContext().getBean("matnrListService");
			mls.doReturnAccountability(invoice, userData);
			GeneralUtil.addSuccessMessage("Операция завершена успешно!");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void doPosting() {
		try {
			if (invoice == null) {
				throw new DAOException("Invoice Is Null");
			}
			if (!invoice.getStatus_id().equals(Invoice.STATUS_NEW)) {
				throw new DAOException("Материалы оприходованы успешно!");
			}
			MatnrListService mls = (MatnrListService) appContext.getContext().getBean("matnrListService");
			mls.doPosting(invoice, userData);
			GeneralUtil.addSuccessMessage("Материалы оприходованы успешно!");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void doWriteoff() {
		try {
			if (invoice == null) {
				throw new DAOException("Invoice Is Null");
			}

			if (!invoice.getStatus_id().equals(Invoice.STATUS_NEW)) {
				throw new DAOException("Документ уже списан");
			}
			MatnrListService mls = (MatnrListService) appContext.getContext().getBean("matnrListService");
			mls.doWriteoff(invoice, userData.getUserid());
			GeneralUtil.addSuccessMessage("Материалы списаны успешно!");

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void doWriteoffLoss() {
		try {
			if (invoice == null) {
				throw new DAOException("Invoice Is Null");
			}

			if (!invoice.getStatus_id().equals(Invoice.STATUS_NEW)) {
				throw new DAOException("Документ уже списан");
			}
			MatnrListService mls = (MatnrListService) appContext.getContext().getBean("matnrListService");
			mls.doWriteoffLoss(invoice, userData);
			GeneralUtil.addSuccessMessage("Материалы списаны успешно!");

		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void doSend() {
		try {
			if (invoice == null) {
				throw new DAOException("Invoice Is Null");
			}

			if (!invoice.getStatus_id().equals(Invoice.STATUS_NEW)) {
				throw new DAOException("Документ не принодлежит к отправке");
			}

			MatnrListService mls = (MatnrListService) appContext.getContext().getBean("matnrListService");
			mls.doSend(invoice, userData);
			GeneralUtil.addSuccessMessage("Материалы отправлены успешно!");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void doReceive() {
		try {
			MatnrListService mls = (MatnrListService) appContext.getContext().getBean("matnrListService");
			mls.doReceive(invoice, userData);
			GeneralUtil.addSuccessMessage("Материалы оприходованы успешно!");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void doAccountability() {
		try {
			MatnrListService mls = (MatnrListService) appContext.getContext().getBean("matnrListService");
			mls.doAccountability(invoice, userData);
			GeneralUtil.addSuccessMessage("Материалы сданы успешно!");
			reloadPage("accountability");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void doReturn() {
		try {
			MatnrListService mls = (MatnrListService) appContext.getContext().getBean("matnrListService");
			mls.doReturn(invoice, userData);
			GeneralUtil.addSuccessMessage("Возврат выполнено успешно!");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private List<general.tables.User> userList = new ArrayList<general.tables.User>();

	public List<general.tables.User> getUserList() {
		return userList;
	}

	public void setUserList(List<general.tables.User> userList) {
		this.userList = userList;
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
}