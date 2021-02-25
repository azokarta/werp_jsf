package general.tables;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "matnr_list")
@javax.persistence.SequenceGenerator(name = "seq_matnr_list_id", sequenceName = "seq_matnr_list_id", allocationSize = 1)
public class MatnrList implements Serializable {
	public static final String STATUS_RECEIVED = "RECEIVED";// принято
	public static final String STATUS_MOVING = "MOVING"; // в перемещении
	public static final String STATUS_MOVING_TEMP = "MOVING_TEMP"; // в
																	// перемещении
																	// Временно
																	// для
																	// материалов
																	// без
																	// баркода
	public static final String STATUS_RETURNED = "RETURNED"; // возврат
	public static final String STATUS_SOLD = "SOLD"; // Продано
	public static final String STATUS_RESERVED = "RESERVED"; // Зарезервирован
	public static final String STATUS_ACCOUNTABILITY = "ACCOUNTABILITY"; // ПОДОТЧЕТ
	public static final String STATUS_LOSS = "LOSS"; // Списаны по потеряннам
	public static final String STATUS_RESOLD = "RESOLD";// Перепродан
	public static final String STATUS_RESOLD_SERVICE = "RESOLD_SERVICE";// Перепродан
																		// и
																		// сервисное
																		// обслуживание
	public static final String STATUS_SPENT_FOR_REPAIR = "SPENT_FOR_REPAIR";// Запчасти
																			// списаня
																			// для
																			// обновление
	public static final String STATUS_MINI_CONTRACT = "MINI_CONTRACT";// Резервирование для мини дог
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_matnr_list_id")
	@Column(name = "matnr_list_id", nullable = true)
	private Long matnr_list_id;

	@Column(name = "bukrs")
	private String bukrs;

	@Column(name = "gjahr")
	private int gjahr;

	@Column(name = "matnr")
	private Long matnr;

	@Column(name = "werks")
	private Long werks;

	@Column(name = "barcode")
	private String barcode;

	@Column(name = "dmbtr")
	private double dmbtr;

	@Column(name = "staff_id")
	private Long staff_id;

	@Column(name = "invoice")
	private Long invoice;

	@Column(name = "awkey")
	private Long awkey;

	@Column(name = "menge")
	private double menge;

	@Column(name = "status")
	private String status;

	private Long customer_id;
	private Date created_date;
	private Long created_by;
	private Long posting_id;
	private int state_id;
	private int bu;

	public MatnrList() {
		setStaff_id(0L);
	}

	public Long getPosting_id() {
		return posting_id;
	}

	public void setPosting_id(Long posting_id) {
		this.posting_id = posting_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getMenge() {
		return menge;
	}

	public void setMenge(double menge) {
		this.menge = menge;
	}

	public Long getMatnr_list_id() {
		return matnr_list_id;
	}

	public void setMatnr_list_id(Long matnr_list_id) {
		this.matnr_list_id = matnr_list_id;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public int getGjahr() {
		return gjahr;
	}

	public void setGjahr(int gjahr) {
		this.gjahr = gjahr;
	}

	public Long getMatnr() {
		return matnr;
	}

	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}

	public Long getWerks() {
		return werks;
	}

	public void setWerks(Long werks) {
		this.werks = werks;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public double getDmbtr() {
		return dmbtr;
	}

	public void setDmbtr(double dmbtr) {
		this.dmbtr = dmbtr;
	}

	public Long getStaff_id() {
		return staff_id;
	}

	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}

	public Long getInvoice() {
		return invoice;
	}

	public void setInvoice(Long invoice) {
		this.invoice = invoice;
	}

	public Long getAwkey() {
		return awkey;
	}

	public void setAwkey(Long awkey) {
		this.awkey = awkey;
	}

	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public Long getCreated_by() {
		return created_by;
	}

	public void setCreated_by(Long created_by) {
		this.created_by = created_by;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((awkey == null) ? 0 : awkey.hashCode());
		result = prime * result + ((barcode == null) ? 0 : barcode.hashCode());
		result = prime * result + ((bukrs == null) ? 0 : bukrs.hashCode());
		result = prime * result + ((created_by == null) ? 0 : created_by.hashCode());
		result = prime * result + ((created_date == null) ? 0 : created_date.hashCode());
		result = prime * result + ((customer_id == null) ? 0 : customer_id.hashCode());
		long temp;
		temp = Double.doubleToLongBits(dmbtr);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + gjahr;
		result = prime * result + ((invoice == null) ? 0 : invoice.hashCode());
		result = prime * result + ((matnr == null) ? 0 : matnr.hashCode());
		result = prime * result + ((matnr_list_id == null) ? 0 : matnr_list_id.hashCode());
		temp = Double.doubleToLongBits(menge);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((staff_id == null) ? 0 : staff_id.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((werks == null) ? 0 : werks.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatnrList other = (MatnrList) obj;
		if (awkey == null) {
			if (other.awkey != null)
				return false;
		} else if (!awkey.equals(other.awkey))
			return false;
		if (barcode == null) {
			if (other.barcode != null)
				return false;
		} else if (!barcode.equals(other.barcode))
			return false;
		if (bukrs == null) {
			if (other.bukrs != null)
				return false;
		} else if (!bukrs.equals(other.bukrs))
			return false;
		if (created_by == null) {
			if (other.created_by != null)
				return false;
		} else if (!created_by.equals(other.created_by))
			return false;
		if (created_date == null) {
			if (other.created_date != null)
				return false;
		} else if (!created_date.equals(other.created_date))
			return false;
		if (customer_id == null) {
			if (other.customer_id != null)
				return false;
		} else if (!customer_id.equals(other.customer_id))
			return false;
		if (Double.doubleToLongBits(dmbtr) != Double.doubleToLongBits(other.dmbtr))
			return false;
		if (gjahr != other.gjahr)
			return false;
		if (invoice == null) {
			if (other.invoice != null)
				return false;
		} else if (!invoice.equals(other.invoice))
			return false;
		if (matnr == null) {
			if (other.matnr != null)
				return false;
		} else if (!matnr.equals(other.matnr))
			return false;
		if (matnr_list_id == null) {
			if (other.matnr_list_id != null)
				return false;
		} else if (!matnr_list_id.equals(other.matnr_list_id))
			return false;
		if (Double.doubleToLongBits(menge) != Double.doubleToLongBits(other.menge))
			return false;
		if (staff_id == null) {
			if (other.staff_id != null)
				return false;
		} else if (!staff_id.equals(other.staff_id))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (werks == null) {
			if (other.werks != null)
				return false;
		} else if (!werks.equals(other.werks))
			return false;
		return true;
	}

	public Map<String, String> getStatuses() {
		Map<String, String> out = new HashMap<String, String>();
		out.put(STATUS_RECEIVED, "На складе");
		out.put(STATUS_MOVING, "В перемещении");
		out.put(STATUS_ACCOUNTABILITY, "В подотчете");
		return out;
	}

	@Transient
	String matnrName;
	
	@Transient
	String matnrNameEn;
	
	@Transient
	String matnrNameTr;

	@Transient
	String staffName;

	@Transient
	String matnrCode;

	@Transient
	String werksName;

	public String getWerksName() {
		return werksName;
	}

	public void setWerksName(String werksName) {
		this.werksName = werksName;
	}

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

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public int getState_id() {
		return state_id;
	}

	public void setState_id(int state_id) {
		this.state_id = state_id;
	}

	public int getBu() {
		return bu;
	}

	public void setBu(int bu) {
		this.bu = bu;
	}
	
	public String getMatnrNameEn() {
		return matnrNameEn;
	}

	public void setMatnrNameEn(String matnrNameEn) {
		this.matnrNameEn = matnrNameEn;
	}

	public String getMatnrNameTr() {
		return matnrNameTr;
	}

	public void setMatnrNameTr(String matnrNameTr) {
		this.matnrNameTr = matnrNameTr;
	}

	@Transient
	private Matnr matnrObject;

	public Matnr getMatnrObject() {
		return matnrObject;
	}

	public void setMatnrObject(Matnr matnrObject) {
		this.matnrObject = matnrObject;
	}

	@ManyToOne(targetEntity = Staff.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "staff_id", referencedColumnName = "staff_id", updatable = false, insertable = false)
	private Staff staff;

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public String getStatusName() {
		if (getStatus().equals(STATUS_ACCOUNTABILITY)) {
			return "В подотчете";
		} else if (getStatus().equals(STATUS_MOVING)) {
			return "В пути";
		} else if (getStatus().equals(STATUS_RESERVED)) {
			return "Зарезервирован в договоре";
		} else if (getStatus().equals(STATUS_RECEIVED)) {
			return "На складе";
		} else if (getStatus().equals(STATUS_SOLD)) {
			return "Продан";
		} else if (STATUS_LOSS.equals(getStatus())) {
			return "Потеря";
		}else if(STATUS_MINI_CONTRACT.equals(getStatus())){
			return "Зарезервирован в мини договоре";
		}
		return null;
	}

	/**
	 * Новый
	 */
	public static final int STATE_NEW = 1;// NOVYI
	/**
	 * Средний
	 */
	public static final int STATE_NORMAL = 2;// SREDNII

	/**
	 * Не подлежит
	 */
	public static final int STATE_OFF = 3;// Ne PODLEJIT

	/**
	 * Подлежит ремонту
	 */
	public static final int STATE_NEED_REPAIR = 4;// Подлежит ремонту

	/**
	 * Обновлен
	 */
	public static final int STATE_REPAIRED = 5;// Материал обновлен
}