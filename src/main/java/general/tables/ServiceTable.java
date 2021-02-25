package general.tables;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "service")
@javax.persistence.SequenceGenerator(name="seq_service_id",sequenceName="seq_service_id",allocationSize=1)
public class ServiceTable implements Cloneable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_service_id")
    private Long id;
	
	@Column(name = "date_open")
	private Date date_open;
	
	@Column(name = "serv_num")
	private Long serv_num;

	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "branch_id")
	private Long branch_id;
		
	@Column(name = "customer_firstname")
	private String customer_firstname;
	
	@Column(name = "customer_midname")
	private String customer_midname;
	
	@Column(name = "customer_lastname")
	private String customer_lastname;
	
	@Column(name = "addr")
	private String addr;
	
	@Column(name = "tel")
	private String tel;
	
	@Column(name = "tovar_id")
	private Long tovar_id;
	
	@Column(name = "tovar_sn")
	private String tovar_sn;
	
	@Column(name = "contract_info")
	private String contract_info;

	@Column(name = "serv_type")
	private int serv_type;
	
	public static final int TYPE_FILTERS = 1;
	public static final int TYPE_FITTING = 2;
	public static final int TYPE_SERVICE = 3;
	public static final int TYPE_PACKET = 4;
	public static final int TYPE_SELLING = 5;
	
	//1 - FILTERS, 2 - USTANOVKA, 3 - SERVICE, 4 - PROPHILAXIS, 5 - OTHER
		
	@Column(name = "serv_app_num")
	private Long serv_app_num;
	
	public static final int CATEGORY_ROB = 1;
	public static final int CATEGORY_CEB = 2;
	
	public static int getCategoryRob() {
		return CATEGORY_ROB;
	}

	public static int getCategoryCeb() {
		return CATEGORY_CEB;
	}
	
	@Column(name = "serv_status")
	private Long serv_status;
	
	public static final int STATUS_NEW = 1;
	public static final int STATUS_PROCESSING = 2;
	public static final int STATUS_DONE = 3;
	public static final int STATUS_PAID = 4;
	public static final int STATUS_RETURNED = 5;
	public static final int STATUS_CANCELLED = 6;
	//1-НОВЫЙ, 2-В ОБРАБОТКЕ, 3-ОБРАБОТАН, 4-ОПЛАЧЕН, 5-ОТМЕНЕН

	@Column(name = "date_close")
	private Date date_close;
	
	@Column(name = "info")
	private String info;
	
	@Column(name = "payment_due")
	private double payment_due;
	
	@Column(name = "paid")
	private double paid;
	
	@Column(name = "currency")
	private String currency;

	@Column(name = "master_id")
	private Long master_id;
	
	@Column(name = "summ_total")
	private double summTotal;
	
	@Column(name = "CREATED_DATE")
	private Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	private Date updatedDate;
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public double getSummTotal() {
		return summTotal;
	}

	public void setSummTotal(double summTotal) {
		this.summTotal = summTotal;
	}

	public static int getTypeFilters() {
		return TYPE_FILTERS;
	}

	public static int getTypeFitting() {
		return TYPE_FITTING;
	}

	public static int getTypeService() {
		return TYPE_SERVICE;
	}

	public static int getTypePacket() {
		return TYPE_PACKET;
	}

	public static int getStatusNew() {
		return STATUS_NEW;
	}

	public static int getStatusProcessing() {
		return STATUS_PROCESSING;
	}

	public static int getStatusDone() {
		return STATUS_DONE;
	}

	public static int getStatusPaid() {
		return STATUS_PAID;
	}

	public static int getStatusReturned() {
		return STATUS_RETURNED;
	}

	public static int getStatusCancelled() {
		return STATUS_CANCELLED;
	}

	@Column(name = "master_premi")
	private double master_premi;
	
	@Column(name = "master_currency")
	private String master_currency;
	
	@Column(name = "oper_id")
	private Long oper_id;
	
	@Column(name = "oper_premi")
	private double oper_premi;
	
	@Column(name = "oper_currency")
	private String oper_currency;

	@Column(name = "awkey")
	private Long awkey;
	
	@Column(name = "category")
	private int category;
	
	@Column(name = "contract_id")
	private Long contract_id;
	
	@Column(name = "customer_id")
	private Long customer_id;
	
	@Column(name = "created_by")
	private Long created_by;

	@Column(name = "discount")
	private double discount;

	@Column(name = "serv_packet_id")
	private Long serv_packet_id;

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public Long getServ_packet_id() {
		return serv_packet_id;
	}

	public void setServ_packet_id(Long serv_packet_id) {
		this.serv_packet_id = serv_packet_id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate_open() {
		return date_open;
	}

	public void setDate_open(Date date_open) {
		this.date_open = date_open;
	}

	public Long getServ_num() {
		return serv_num;
	}

	public void setServ_num(Long serv_num) {
		this.serv_num = serv_num;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public String getCustomer_firstname() {
		return customer_firstname;
	}

	public void setCustomer_firstname(String customer_firstname) {
		this.customer_firstname = customer_firstname;
	}

	public String getCustomer_midname() {
		return customer_midname;
	}

	public void setCustomer_midname(String customer_midname) {
		this.customer_midname = customer_midname;
	}

	public String getCustomer_lastname() {
		return customer_lastname;
	}

	public void setCustomer_lastname(String customer_lastname) {
		this.customer_lastname = customer_lastname;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public Long getTovar_id() {
		return tovar_id;
	}

	public void setTovar_id(Long tovar_id) {
		this.tovar_id = tovar_id;
	}

	public String getTovar_sn() {
		return tovar_sn;
	}

	public void setTovar_sn(String tovar_sn) {
		this.tovar_sn = tovar_sn;
	}

	public String getContract_info() {
		return contract_info;
	}

	public void setContract_info(String contract_info) {
		this.contract_info = contract_info;
	}

	public int getServ_type() {
		return serv_type;
	}

	public void setServ_type(int serv_type) {
		this.serv_type = serv_type;
	}

	public Long getServ_app_num() {
		return serv_app_num;
	}

	public void setServ_app_num(Long serv_app_num) {
		this.serv_app_num = serv_app_num;
	}

	public Long getServ_status() {
		return serv_status;
	}

	public void setServ_status(Long serv_status) {
		this.serv_status = serv_status;
	}

	public Date getDate_close() {
		return date_close;
	}

	public void setDate_close(Date date_close) {
		this.date_close = date_close;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public double getPayment_due() {
		return payment_due;
	}

	public void setPayment_due(double payment_due) {
		this.payment_due = payment_due;
	}

	public double getPaid() {
		return paid;
	}

	public void setPaid(double paid) {
		this.paid = paid;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Long getMaster_id() {
		return master_id;
	}

	public void setMaster_id(Long master_id) {
		this.master_id = master_id;
	}

	public double getMaster_premi() {
		return master_premi;
	}

	public void setMaster_premi(double master_premi) {
		this.master_premi = master_premi;
	}

	public String getMaster_currency() {
		return master_currency;
	}

	public void setMaster_currency(String master_currency) {
		this.master_currency = master_currency;
	}

	public Long getOper_id() {
		return oper_id;
	}

	public void setOper_id(Long oper_id) {
		this.oper_id = oper_id;
	}

	public double getOper_premi() {
		return oper_premi;
	}

	public void setOper_premi(double oper_premi) {
		this.oper_premi = oper_premi;
	}

	public String getOper_currency() {
		return oper_currency;
	}

	public void setOper_currency(String oper_currency) {
		this.oper_currency = oper_currency;
	}

	public Long getAwkey() {
		return awkey;
	}

	public void setAwkey(Long awkey) {
		this.awkey = awkey;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public Long getContract_id() {
		return contract_id;
	}

	public void setContract_id(Long contract_id) {
		this.contract_id = contract_id;
	}

	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}

	public Long getCreated_by() {
		return created_by;
	}

	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}
	
	@Override
    public ServiceTable clone() throws CloneNotSupportedException {
        return (ServiceTable) super.clone();
    }
		
}
