package reference;

import general.AppContext;
import general.dao.InvoiceDeliveryTermDao;
import general.tables.InvoiceDeliveryTerm;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
@ManagedBean(name="refInvoiceDeliveryTerm")
@ViewScoped
public class RefInvoiceDeliveryTerm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<InvoiceDeliveryTerm> items;
	private InvoiceDeliveryTerm selected;
	public InvoiceDeliveryTerm getSelected() {
		return selected;
	}
	public void setSelected(InvoiceDeliveryTerm selected) {
		this.selected = selected;
	}
	public List<InvoiceDeliveryTerm> getItems() {
		InvoiceDeliveryTermDao d = (InvoiceDeliveryTermDao)appContext.getContext().getBean("invoiceDeliveryTermDao");
		this.items = d.findAll("");
		return items;
	}
	
	@ManagedProperty(value = "#{appContext}")
	AppContext appContext;
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

}
