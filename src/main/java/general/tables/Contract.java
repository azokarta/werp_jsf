package general.tables;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "contract")
@javax.persistence.SequenceGenerator(name="seq_contract_id",sequenceName="seq_contract_id", allocationSize = 1)
public class Contract implements Cloneable, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4245963711454590201L;

	@Override
    public Contract clone() throws CloneNotSupportedException {
        return (Contract) super.clone();
    }
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_contract_id")
    private Long contract_id;
	
	@Column(name = "bukrs")
	private String bukrs;
	
	@Column(name = "contract_number")
	private Long contract_number;
	
	@Column(name = "contract_type_id")
	private Long contract_type_id;
	
	@Column(name = "branch_id")
	private Long branch_id;
	
	@Column(name = "contract_date")
	private Date contract_date;
	
	@Column(name = "contract_status_id")
	private Long contract_status_id;
	
	@Column(name = "payment_schedule")
	private int payment_schedule;
	
	@Column(name = "price_list_id")
	private Long price_list_id;
	
	@Column(name = "tovar_serial")
	private String tovar_serial;
	
	@Column(name = "demo_sc")
	private Long demo_sc;
	
	@Column(name = "dealer")
	private Long dealer;
	
	@Column(name = "collector")
	private Long collector;
	
	@Column(name = "fitter")
	private Long fitter;
	
	@Column(name = "customer_id")
	private Long customer_id;
	
	@Column(name = "paid")
	private double paid; 
	
	@Column(name = "price")
	private double price; 
	
	@Column(name = "first_payment")
	private double first_payment;	 
	
	@Column(name = "matnr_list_id")
	private Long matnr_list_id;

	@Column(name = "matnr_release_date")
	private Date matnr_release_date;
	
	@Column(name = "created_by", nullable = true)
	private Long created_by;
	
	@Column(name = "created_date", nullable = true)
	private Date created_date;
	
	@Column(name = "updated_by", nullable = true)
	private Long updated_by;
	
	@Column(name = "updated_date", nullable = true)
	private Date updated_date;
	
	@Column(name = "cancel_date", nullable = true)
	private Date cancel_date;
	
	@Column(name = "new_contract_date", nullable = true)
	private Date new_contract_date;
	
	@Column(name = "ref_contract_id")
	private Long ref_contract_id;
	
	@Column(name = "serv_branch_id")
	private Long serv_branch_id;
	
	@Column(name = "FIN_BRANCH_ID")
	private Long finBranchId;
	
	@Column(name = "LEGAL_ENTITY_ID")
	private Long legal_entity_id;
	
	@Column(name = "LE_INFO")
	private String le_info;
	


	@Column(name = "info2")
	private String info2;


	@Column(name = "trade_in")
	private int tradeIn;

	@Column(name = "trade_in_matnr_list_id")
	private Long tradeInMatnrListId;

	@Column(name = "trade_in_tovar_serial")
	private String tradeInTovarSerial;
	
	

	@Column(name="MT_CONFIRMED")
	private byte mtConfirmed;
	
	public static byte MT_CONFIRMED_FALSE = 1;
	public static byte MT_CONFIRMED_APPROVED = 2;
	public static byte MT_CONFIRMED_REJECTED = 3;
	
	@Column(name="MT_CONF_BY")
	private String mtConfirmedby;

	@Column(name="MARKED_TYPE")
	private Long markedType;
	
	public static Long MT_ALL = -1L;
	public static Long MT_STANDARD_PRICE = 0L;
	public static Long MT_CUT_OFF_PRICE = 1L;
	public static Long MT_GIFT_FOR_FREE = 2L;
	public static Long MT_SERVICE = 3L;
	
	public byte getMtConfirmed() {
		return mtConfirmed;
	}

	public void setMtConfirmed(byte mtConfirmed) {
		this.mtConfirmed = mtConfirmed;
	}

	public String getMtConfirmedby() {
		return mtConfirmedby;
	}

	public void setMtConfirmedby(String mtConfirmedby) {
		this.mtConfirmedby = mtConfirmedby;
	}
	
	public Long getMarkedType() {
		return markedType;
	}

	public void setMarkedType(Long markedType) {
		this.markedType = markedType;
	}

	public Long getLegal_entity_id() {
		return legal_entity_id;
	}

	public void setLegal_entity_id(Long legal_entity_id) {
		this.legal_entity_id = legal_entity_id;
	}

	public String getLe_info() {
		return le_info;
	}

	public void setLe_info(String le_info) {
		this.le_info = le_info;
	}

	@Column(name = "dealer_subtract")
	private double dealer_subtract;

	@Column(name = "discount_from_ref")
	private byte discount_from_ref;

	@Column(name = "info")
	private String info;

	@Column(name = "bank_partner_id")
	private Long bank_partner_id;

	@Column(name = "awkey")
	private Long awkey;
	
	@Column(name = "war_start")
	private Date war_start;
	
	@Column(name = "contract_barcode")
	private String contract_barcode;
	
	@Column(name = "close_date", nullable = true)
	private Date close_date;
	
	@Column(name = "last_state")
	private int last_state;
	
	public static final int LASTSTATE_SIGNED = 1;
	public static final int LASTSTATE_GIVEN = 2;
	public static final int LASTSTATE_RETURNED = 3;
	public static final int LASTSTATE_INSTALLED = 4;
	public static final int LASTSTATE_DISMANTLED = 5;
	
	public static int getLaststateSigned() {
		return LASTSTATE_SIGNED;
	}

	public static int getLaststateGiven() {
		return LASTSTATE_GIVEN;
	}

	public static int getLaststateReturned() {
		return LASTSTATE_RETURNED;
	}

	public static int getLaststateInstalled() {
		return LASTSTATE_INSTALLED;
	}

	public static int getLaststateDismantled() {
		return LASTSTATE_DISMANTLED;
	}

	@Column(name = "warranty")
	private int warranty;
	
	@Column(name = "waers")
	private String waers;
	
	@Column(name = "old_id")
	private Long old_id;
	
	@Column(name = "old_sn")
	private Long old_sn;
	
	@Column(name = "rate")
	private double rate;
	
	@Column(name = "skidka_vol")
	private double skidka_vol;

	//TOVAR_CATEGORY
	@Column(name = "TOVAR_CATEGORY")
	private int tovar_category;

	@Column(name = "ADDR_HOME_ID")
	private Long addrHomeId;

	@Column(name = "ADDR_WORK_ID")
	private Long addrWorkId;

	@Column(name = "ADDR_SERVICE_ID")
	private Long addrServiceId;

	
	
	public String getInfo2() {
		return info2;
	}

	public void setInfo2(String info2) {
		this.info2 = info2;
	}

	public int getTradeIn() {
		return tradeIn;
	}

	public void setTradeIn(int tradeIn) {
		this.tradeIn = tradeIn;
	}

	public Long getTradeInMatnrListId() {
		return tradeInMatnrListId;
	}

	public void setTradeInMatnrListId(Long tradeInMatnrListId) {
		this.tradeInMatnrListId = tradeInMatnrListId;
	}

	public String getTradeInTovarSerial() {
		return tradeInTovarSerial;
	}

	public void setTradeInTovarSerial(String tradeInTovarSerial) {
		this.tradeInTovarSerial = tradeInTovarSerial;
	}

	public Long getFinBranchId() {
		return finBranchId;
	}

	public void setFinBranchId(Long finBranchId) {
		this.finBranchId = finBranchId;
	}
	
	public Long getAddrHomeId() {
		return addrHomeId;
	}

	public void setAddrHomeId(Long addrHomeId) {
		this.addrHomeId = addrHomeId;
	}

	public Long getAddrWorkId() {
		return addrWorkId;
	}

	public void setAddrWorkId(Long addrWorkId) {
		this.addrWorkId = addrWorkId;
	}

	public Long getAddrServiceId() {
		return addrServiceId;
	}

	public void setAddrServiceId(Long addrServiceId) {
		this.addrServiceId = addrServiceId;
	}

	public int getTovar_category() {
		return tovar_category;
	}

	public void setTovar_category(int tovar_category) {
		this.tovar_category = tovar_category;
	}

	public double getSkidka_vol() {
		return skidka_vol;
	}

	public void setSkidka_vol(double skidka_vol) {
		this.skidka_vol = skidka_vol;
	}

	public String getWaers() {
		return waers;
	}

	public void setWaers(String waers) {
		this.waers = waers;
	}

	public Long  getOld_id() {
		return old_id;
	}

	public void setOld_id(Long old_id) {
		this.old_id = old_id;
	}

	public Long getOld_sn() {
		return old_sn;
	}

	public void setOld_sn(Long old_sn) {
		this.old_sn = old_sn;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public int getWarranty() {
		return warranty;
	}

	public void setWarranty(int warranty) {
		this.warranty = warranty;
	}

	public int getLast_state() {
		return last_state;
	}

	public void setLast_state(int last_state) {
		this.last_state = last_state;
	}

	public Date getClose_date() {
		return close_date;
	}

	public void setClose_date(Date close_date) {
		this.close_date = close_date;
	}

	public String getContract_barcode() {
		return contract_barcode;
	}

	public void setContract_barcode(String contract_barcode) {
		this.contract_barcode = contract_barcode;
	}

	public Date getWar_start() {
		return war_start;
	}

	public void setWar_start(Date war_start) {
		this.war_start = war_start;
	}

	public Long getAwkey() {
		return awkey;
	}

	public void setAwkey(Long awkey) {
		this.awkey = awkey;
	}

	public Long getBank_partner_id() {
		return bank_partner_id;
	}

	public void setBank_partner_id(Long bank_partner_id) {
		this.bank_partner_id = bank_partner_id;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public double getDealer_subtract() {
		return dealer_subtract;
	}

	public void setDealer_subtract(double dealer_subtract) {
		this.dealer_subtract = dealer_subtract;
	}

	public byte getDiscount_from_ref() {
		return discount_from_ref;
	}

	public void setDiscount_from_ref(byte discount_from_ref) {
		this.discount_from_ref = discount_from_ref;
	}

	public Long getContract_id() {
		return contract_id;
	}

	public void setContract_id(Long contract_id) {
		this.contract_id = contract_id;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getContract_number() {
		return contract_number;
	}

	public void setContract_number(Long contract_number) {
		this.contract_number = contract_number;
	}

	public Long getContract_type_id() {
		return contract_type_id;
	}

	public void setContract_type_id(Long contract_type_id) {
		this.contract_type_id = contract_type_id;
	}

	public Date getContract_date() {
		return contract_date;
	}

	public void setContract_date(Date contract_date) {
		this.contract_date = contract_date;
	}

	public int getPayment_schedule() {
		return payment_schedule;
	}

	public void setPayment_schedule(int payment_schedule) {
		this.payment_schedule = payment_schedule;
	}

	public Long getPrice_list_id() {
		return price_list_id;
	}

	public void setPrice_list_id(Long price_list_id) {
		this.price_list_id = price_list_id;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getFirst_payment() {
		return first_payment;
	}

	public void setFirst_payment(double first_payment) {
		this.first_payment = first_payment;
	}
	
	public Long getCreated_by() {
		return created_by;
	}

	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public Long getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(Long updated_by) {
		this.updated_by = updated_by;
	}

	public Date getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(Date updated_date) {
		this.updated_date = updated_date;
	}

	public Long getDemo_sc() {
		return demo_sc;
	}

	public void setDemo_sc(Long demo_sc) {
		this.demo_sc = demo_sc;
	}

	public Long getDealer() {
		return dealer;
	}

	public void setDealer(Long dealer) {
		this.dealer = dealer;
	}

	public Long getCollector() {
		return collector;
	}

	public void setCollector(Long collector) {
		this.collector = collector;
	}

	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}

	public double getPaid() {
		return paid;
	}

	public void setPaid(double paid) {
		this.paid = paid;
	}

	public Long getMatnr_list_id() {
		return matnr_list_id;
	}

	public void setMatnr_list_id(Long matnr_list_id) {
		this.matnr_list_id = matnr_list_id;
	}

	public Date getMatnr_release_date() {
		return matnr_release_date;
	}

	public void setMatnr_release_date(Date matnr_release_date) {
		this.matnr_release_date = matnr_release_date;
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public Long getContract_status_id() {
		return contract_status_id;
	}

	public void setContract_status_id(Long contract_status_id) {
		this.contract_status_id = contract_status_id;
	}

	public Date getCancel_date() {
		return cancel_date;
	}

	public void setCancel_date(Date cancel_date) {
		this.cancel_date = cancel_date;
	}

	public Date getNew_contract_date() {
		return new_contract_date;
	}

	public void setNew_contract_date(Date new_contract_date) {
		this.new_contract_date = new_contract_date;
	}

	public Long getRef_contract_id() {
		return ref_contract_id;
	}

	public void setRef_contract_id(Long ref_contract_id) {
		this.ref_contract_id = ref_contract_id;
	}

	public Long getServ_branch_id() {
		return serv_branch_id;
	}

	public void setServ_branch_id(Long serv_branch_id) {
		this.serv_branch_id = serv_branch_id;
	}

	public Long getFitter() {
		return fitter;
	}

	public void setFitter(Long fitter) {
		this.fitter = fitter;
	}

	public String getTovar_serial() {
		return tovar_serial;
	}

	public void setTovar_serial(String tovar_serial) {
		this.tovar_serial = tovar_serial;
	}
}
