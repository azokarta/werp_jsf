package reference;

import general.AppContext;
import general.GeneralUtil;
import general.MessageProvider;
import general.Validation;
import general.dao.DAOException;
import general.dao.InvoicePaymentTermDao;
import general.services.IPTService;
import general.tables.InvoicePaymentTerm;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.springframework.transaction.annotation.Transactional;

@ManagedBean(name = "refInvoicePaymentTerm")
@ViewScoped
public class RefInvoicePaymentTerm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<InvoicePaymentTerm> items;
	private InvoicePaymentTerm selected;

	public InvoicePaymentTerm getSelected() {
		return selected;
	}

	public void setSelected(InvoicePaymentTerm selected) {
		this.selected = selected;
	}

	public List<InvoicePaymentTerm> getItems() {
		InvoicePaymentTermDao d = (InvoicePaymentTermDao)appContext.getContext().getBean("invoicePaymentTermDao");
		this.items = d.findAll("");
		return items;
	}

	public InvoicePaymentTerm prepareCreate() {
		this.selected = new InvoicePaymentTerm();
		return this.selected;
	}

	public void Reset() {
		this.selected = null;

	}

	public void Save() {
		try {
			if (this.selected == null || this.selected.getIpt_id() == null) {
				this.Create();
			} else {
				this.Update();
			}
			GeneralUtil.addSuccessMessage("Сохранено успешно");
			GeneralUtil.hideDialog("IptCreateDialog");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	private void Create() {
		IPTService service = (IPTService) appContext.getContext().getBean(
				"iptService");
		service.create(this.selected);
	}

	private void Update() {
		IPTService service = (IPTService) appContext.getContext().getBean(
				"iptService");
		service.update(this.selected);
	}

	@ManagedProperty(value = "#{appContext}")
	AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
