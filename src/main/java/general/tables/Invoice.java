package general.tables;

import java.io.Serializable;
import java.lang.Long;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.*;

import general.MessageProvider;
import general.Validation;

/**
 * Entity implementation class for Entity: Invoice
 *
 */
@Entity
@Table(name = "INVOICE_TABLE")
@javax.persistence.SequenceGenerator(name = "seq_invoice_id", sequenceName = "seq_invoice_id", allocationSize = 1)
public class Invoice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8997794227092420799L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_invoice_id")
	private Long id;

	private Long from_werks;
	private Long to_werks;
	private Long customer_id;
	private Integer status_id;
	private Integer type_id;
	private Long responsible_id;
	private Date invoice_date;
	private Long department_id;
	private Date created_at;
	private Date updated_at;
	private Long created_by;
	private Long updated_by;
	private String bukrs;
	private Long awkey;
	private Long branch_id;
	private int is_system;
	private String note;
	private Long contract_number;
	private Long service_number;
	private Long reg_number;
	private Double amount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFrom_werks() {
		return from_werks;
	}

	public void setFrom_werks(Long from_werks) {
		this.from_werks = from_werks;
	}

	public Long getTo_werks() {
		return to_werks;
	}

	public void setTo_werks(Long to_werks) {
		this.to_werks = to_werks;
	}

	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customerId) {
		this.customer_id = customerId == null ? 0L : customerId;
	}

	public Integer getStatus_id() {
		return status_id;
	}

	public void setStatus_id(Integer status_id) {
		this.status_id = status_id;
	}

	public Integer getType_id() {
		return type_id;
	}

	public void setType_id(Integer type_id) {
		this.type_id = type_id;
	}

	public Long getResponsible_id() {
		return responsible_id;
	}

	public void setResponsible_id(Long responsible_id) {
		if (Validation.isEmptyLong(responsible_id)) {
			this.responsible_id = 0L;
		} else {
			this.responsible_id = responsible_id;
		}
	}

	public Date getInvoice_date() {
		return invoice_date;
	}

	public void setInvoice_date(Date invoice_date) {
		this.invoice_date = invoice_date;
	}

	public Long getDepartment_id() {
		return department_id;
	}

	public void setDepartment_id(Long department_id) {
		this.department_id = department_id;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}

	public Long getCreated_by() {
		return created_by;
	}

	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}

	public Long getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(Long updated_by) {
		this.updated_by = updated_by;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getReg_number() {
		return reg_number;
	}

	public void setReg_number(Long reg_number) {
		this.reg_number = reg_number;
	}

	public Long getAwkey() {
		return awkey;
	}

	public void setAwkey(Long awkey) {
		this.awkey = awkey;
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public int getIs_system() {
		return is_system;
	}

	public void setIs_system(int is_system) {
		this.is_system = is_system;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Long getContract_number() {
		return contract_number;
	}

	public void setContract_number(Long contract_number) {
		this.contract_number = contract_number;
	}

	public Long getService_number() {
		return service_number;
	}

	public void setService_number(Long service_number) {
		this.service_number = service_number;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Invoice() {
		super();
		setCreated_at(Calendar.getInstance().getTime());
		setStatus_id(STATUS_NEW);
		setIs_system(0);
		setFrom_werks(0L);
		setTo_werks(0L);
		setResponsible_id(0L);
		setBranch_id(0L);
		setAwkey(0L);
		setCustomer_id(0L);
		this.contract_number = 0L;
		this.service_number = 0L;
		this.amount = 0D;
	}

	public String getStatusName() {
		MessageProvider mp = new MessageProvider();
		if (getStatus_id() == STATUS_NEW) {
			return mp.getValue("logistics.invoice.status_new");
			//return "Новый";
		} else if (getStatus_id() == STATUS_DONE) {
			return mp.getValue("logistics.invoice.status_done");
			//return "Закрыт";
		} else if (getStatus_id().equals(STATUS_MOVING)) {
			return mp.getValue("logistics.invoice.status_moving");
			//return "В пути";
		} else if (getStatus_id().equals(STATUS_CANCELLED)) {
			return mp.getValue("logistics.invoice.status_cancelled");
			//return "Отменен";
		} else if (getStatus_id().equals(STATUS_ARCHIVE)) {
			return mp.getValue("logistics.invoice.status_archive");
			//return "Отменен (Архив)";
		} else if (STATUS_DELETED.equals(getStatus_id())) {
			return mp.getValue("logistics.invoice.status_deleted");
			//return "Удален";
		}

		return getStatus_id() + "";
	}

	public String getTypeName() {
		MessageProvider mp = new MessageProvider();
		if (TYPE_ACCOUNTABILITY.equals(getType_id())) {
			return mp.getValue("logistics.invoice_for_acc");
			//return "Накладная для подотчета";
		} else if (TYPE_ACCOUNTABILITY_RETURN.equals(getType_id())) {
			//return "Накладная для возврата с подотчета";
			return mp.getValue("logistics.invoice_for_return_acc");
		} else if (TYPE_POSTING.equals(getType_id())) {
			return mp.getValue("logistics.invoice_for_posting");
			//return "Накладная для оприходования";
		} else if (TYPE_POSTING_IN.equals(getType_id())) {
			return mp.getValue("logistics.invoice_for_posting_in");
			//return "Накладная для внутреннего оприходования";
		} else if (TYPE_RETURN.equals(getType_id())) {
			return mp.getValue("logistics.invoice_for_return");
			//return "Накладная для возврата";
		} else if (TYPE_RETURN_DOC.equals(getType_id())) {
			return "Накладная для документа возврата";
		} else if (TYPE_SEND.equals(getType_id())) {
			return mp.getValue("logistics.invoice_for_send");
			//return "Накладная для отправки";
		} else if (TYPE_WRITEOFF.equals(getType_id())) {
			return mp.getValue("logistics.invoice_for_writeoff");
			//return "Накладная для списания";
		} else if (TYPE_WRITEOFF_DOC.equals(getType_id())) {
			return mp.getValue("logistics.invoice_writeoff_doc");
			//return "Документ списания";
		} else if (TYPE_WRITEOFF_LOSS.equals(getType_id())) {
			return mp.getValue("logistics.invoice_for_writeoff_loss");
			//return "Накладная для списания по потере";
		} else if (TYPE_MINI_CONTRACT.equals(getType_id())) {
			return "Резервирование материала (Мини договор)";
		}

		return null;
	}

	public static final Integer TYPE_POSTING = 1;// Оприходования
	public static final Integer TYPE_WRITEOFF = 2;// СПИСАНИе
	public static final Integer TYPE_SEND = 3;// Отправка
	public static final Integer TYPE_ACCOUNTABILITY = 4;// Подотчет
	public static final Integer TYPE_WRITEOFF_DOC = 5;// Документ на списание
	public static final Integer TYPE_POSTING_IN = 6;// Внутр. оприходование
	public static final Integer TYPE_RETURN = 7;// Возврат
	public static final Integer TYPE_RETURN_DOC = 8;// Документ для возврата
	public static final Integer TYPE_ACCOUNTABILITY_RETURN = 9;// Возврат с
																// подотчета
	public static final Integer TYPE_WRITEOFF_LOSS = 10;// Списание по потерянн
	public static final Integer TYPE_MINI_CONTRACT = 11;// Резервирование
														// материала (мини
														// контракт)

	public static final Integer STATUS_NEW = 1;
	public static final Integer STATUS_DONE = 2;// Закрыт

	// Для отправки
	public static final Integer STATUS_MOVING = 3;// В пути
	public static final Integer STATUS_CANCELLED = 4;// Отменен

	public static final Integer STATUS_ARCHIVE = 5;
	public static final Integer STATUS_DELETED = 6;

	public static final Long TRANSACTION_ID_POSTING = 234L;
	public static final Long TRANSACTION_ID_WRITEOFF = 106L;
	public static final Long TRANSACTION_ID_SEND = 108L;
	public static final Long TRANSACTION_ID_ACCOUNTABILITY = 152L;
	public static final Long TRANSACTION_ID_WRITEOFF_DOC = 232L;
	public static final Long TRANSACTION_ID_POSTING_IN = 17L;
	public static final Long TRANSACTION_ID_RETURN = 252L;
	public static final Long TRANSACTION_ID_ACCOUNTABILITY_RETURN = 313L;
	public static final Long TRANSACTION_ID_WRITEOFF_LOSS = 314L;
	public static final Long TRANSACTION_ID_MINI_CONTRACT = 536L;

	public static final String CONTEXT = "invoice";

	public String getDocumentViewLink() {
		if (getType_id().equals(TYPE_ACCOUNTABILITY)) {
			return "/logistics/invoice/accountability/View.xhtml?id=" + getId();
		}
		if (getType_id().equals(TYPE_ACCOUNTABILITY_RETURN)) {
			return "/logistics/invoice/accountability/return/View.xhtml?id=" + getId();
		} else if (getType_id().equals(TYPE_POSTING)) {
			return "/logistics/invoice/posting/View.xhtml?id=" + getId();
		} else if (getType_id().equals(TYPE_POSTING_IN)) {
			return "/logistics/invoice/posting-in/View.xhtml?id=" + getId();
		} else if (getType_id().equals(TYPE_SEND)) {
			return "/logistics/invoice/send/View.xhtml?id=" + getId();
		} else if (getType_id().equals(TYPE_WRITEOFF)) {
			return "/logistics/invoice/writeoff/View.xhtml?id=" + getId();
		}else if (getType_id().equals(TYPE_RETURN)) {
			return "/logistics/invoice/return/View.xhtml?id=" + getId();
		} else if (getType_id().equals(TYPE_WRITEOFF_DOC)) {
			return "/logistics/invoice/writeoff-doc/View.xhtml?id=" + getId();
		} else if (TYPE_MINI_CONTRACT.equals(getType_id())) {
			return "/logistics/invoice/reservation/View.xhtml?id=" + getId();
		} else if (TYPE_WRITEOFF_LOSS.equals(getType_id())) {
			return "/logistics/invoice/writeoff/loss/View.xhtml?id=" + getId();
		}
		return "";
	}

	@Transient
	private Staff creator;

	public Staff getCreator() {
		return creator;
	}

	public void setCreator(Staff creator) {
		this.creator = creator;
	}

	@Transient
	private Staff responsible;

	public Staff getResponsible() {
		return responsible;
	}

	public void setResponsible(Staff responsible) {
		this.responsible = responsible;
	}

	@Transient
	public String getFormattedRegNumber() {
		if (Validation.isEmptyLong(getReg_number())) {
			return "";
		}
		return String.format("%07d", getReg_number());
	}
}
