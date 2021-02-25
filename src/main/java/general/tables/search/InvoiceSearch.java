package general.tables.search;

import java.text.SimpleDateFormat;
import java.util.Date;

import general.Validation;
import general.tables.Invoice;

public class InvoiceSearch extends Invoice {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1153441284959656731L;

	private Date invoiceDateFrom;
	private Date invoiceDateTo;
	private int serviceType;

	public Date getInvoiceDateFrom() {
		return invoiceDateFrom;
	}

	public void setInvoiceDateFrom(Date invoiceDateFrom) {
		this.invoiceDateFrom = invoiceDateFrom;
	}

	public Date getInvoiceDateTo() {
		return invoiceDateTo;
	}

	public void setInvoiceDateTo(Date invoiceDateTo) {
		this.invoiceDateTo = invoiceDateTo;
	}

	public int getServiceType() {
		return serviceType;
	}

	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}

	public String getCondition() {
		String c = "";
		if (!Validation.isEmptyString(this.getBukrs())) {
			c += " bukrs = " + this.getBukrs();
		}

		if (!Validation.isEmptyLong(getId())) {
			c += (c.length() > 0 ? " AND " : " ") + " id = " + getId();
		}

		if (!Validation.isEmptyLong(getBranch_id())) {
			c += (c.length() > 0 ? " AND " : " ") + " branch_id = " + getBranch_id();
		}

		if (this.getCustomer_id() != null && this.getCustomer_id().longValue() > 0L) {
			c += (c.length() > 0 ? " AND " : " ") + " customer_id = " + this.getCustomer_id();
		}

		if (getType_id() != null && getType_id() > 0) {
			c += (c.length() > 0 ? " AND " : " ") + " type_id = " + getType_id();
		}

		if (getStatus_id() != null && getStatus_id() > 0) {
			c += (c.length() > 0 ? " AND " : " ") + " status_id = " + getStatus_id();
		}

		if (getService_number() != null && getService_number() > 0) {
			c += (c.length() > 0 ? " AND " : " ") + " service_number = " + getService_number();
		}

		if (getContract_number() != null && getContract_number() > 0) {
			c += (c.length() > 0 ? " AND " : " ") + " contract_number = " + getContract_number();
		}

		if (!Validation.isEmptyLong(getFrom_werks())) {
			c += (c.length() > 0 ? " AND " : " ") + " from_werks = " + getFrom_werks();
		}

		if (!Validation.isEmptyLong(getTo_werks())) {
			c += (c.length() > 0 ? " AND " : " ") + " to_werks = " + getTo_werks();
		}

		if (getInvoiceDateFrom() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			c += (c.length() > 0 ? " AND " : " ") + " invoice_date >= '" + sdf.format(getInvoiceDateFrom()).toString()
					+ "' ";
		}

		if (getInvoiceDateTo() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			c += (c.length() > 0 ? " AND " : " ") + " invoice_date <= '" + sdf.format(getInvoiceDateTo()).toString()
					+ "' ";
		}

		if (getServiceType() > 0) {
			if (getServiceType() == 1) {
				c += (c.length() > 0 ? " AND " : " ") + " contract_number IS NOT NULL AND contract_number > 0 ";
			} else if (getServiceType() == 2) {
				c += (c.length() > 0 ? " AND " : " ") + " service_number IS NOT NULL AND service_number > 0 ";
			}
		}

		return c;
	}

}
