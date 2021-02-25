package general.tables;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "matnr_movement")
@javax.persistence.SequenceGenerator(name = "seq_matnr_movement_id", sequenceName = "seq_matnr_movement_id", allocationSize = 1)
public class MatnrMovement implements Serializable {

	public static final String STATUS_RECEIVED = "RECEIVED";// принято
	public static final String STATUS_MOVING = "MOVING";// принято
	public static final String STATUS_ACCOUNTABILITY = "ACCOUNTABILITY";// Подотчет
	public static final String STATUS_SOLD = "SOLD";// ПРодан

	public static final String TYPE_RETURN = "RETURN";// Возврат
	public static final String TYPE_MOVEMENT_SEND = "MOVEMENT_SEND";// Перемещение-Отправка
	public static final String TYPE_MOVEMENT_RECEIVE = "MOVEMENT_RECEIVE";// Перемещение-Принятия
	public static final String TYPE_ACCOUNTABILITY = "ACCOUNTABILITY";// Подотчет
	public static final String TYPE_SALE = "SALE";//Продажа
	public static final String TYPE_POSTING = "POSTING";//Оприходования
	/**
     *
     */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_matnr_movement_id")
	@Column(name = "mm_id")
	private Long mm_id;

	@Column(name = "matnr_list_id")
	private Long matnr_list_id;

	@Column(name = "from_werks")
	private Long from_werks;

	@Column(name = "to_werks")
	private Long to_werks;

	@Column(name = "awkey")
	private Long awkey;

	@Column(name = "old_dmbtr")
	private double old_dmbtr;

	@Column(name = "new_dmbtr")
	private double new_dmbtr;
	
	private Date mm_date;
	public Date getMm_date() {
		return mm_date;
	}

	public void setMm_date(Date mm_date) {
		this.mm_date = mm_date;
	}

	private Long staff_id;
	public Long getStaff_id() {
		return staff_id;
	}

	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}

	private String bukrs;
	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	private Date created_date;
	private String status;
	private String mm_type;
	private Long created_by;
	private Date received_date;
	private Long received_by;
	private String note;
	private Long customer_id;
	private Long related_id;
	
	public Long getRelated_id() {
		return related_id;
	}

	public void setRelated_id(Long related_id) {
		this.related_id = related_id;
	}

	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getReceived_date() {
		return received_date;
	}

	public void setReceived_date(Date received_date) {
		this.received_date = received_date;
	}

	public Long getReceived_by() {
		return received_by;
	}

	public void setReceived_by(Long received_by) {
		this.received_by = received_by;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	private Double dmbtr;

	public Double getDmbtr() {
		return dmbtr;
	}

	public void setDmbtr(Double dmbtr) {
		this.dmbtr = dmbtr;
	}

	public Long getMm_id() {
		return mm_id;
	}

	public void setMm_id(Long mm_id) {
		this.mm_id = mm_id;
	}

	public Long getMatnr_list_id() {
		return matnr_list_id;
	}

	public void setMatnr_list_id(Long matnr_list_id) {
		this.matnr_list_id = matnr_list_id;
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

	public Long getAwkey() {
		return awkey;
	}

	public void setAwkey(Long awkey) {
		this.awkey = awkey;
	}

	public double getNew_dmbtr() {
		return new_dmbtr;
	}

	public void setNew_dmbtr(double new_dmbtr) {
		this.new_dmbtr = new_dmbtr;
	}

	public double getOld_dmbtr() {
		return old_dmbtr;
	}

	public void setOld_dmbtr(double old_dmbtr) {
		this.old_dmbtr = old_dmbtr;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMm_type() {
		return mm_type;
	}

	public void setMm_type(String mm_type) {
		this.mm_type = mm_type;
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
		result = prime * result
				+ ((created_date == null) ? 0 : created_date.hashCode());
		result = prime * result + ((dmbtr == null) ? 0 : dmbtr.hashCode());
		result = prime * result
				+ ((from_werks == null) ? 0 : from_werks.hashCode());
		result = prime * result
				+ ((matnr_list_id == null) ? 0 : matnr_list_id.hashCode());
		result = prime * result + ((mm_id == null) ? 0 : mm_id.hashCode());
		result = prime * result + ((mm_type == null) ? 0 : mm_type.hashCode());
		long temp;
		temp = Double.doubleToLongBits(new_dmbtr);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(old_dmbtr);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((to_werks == null) ? 0 : to_werks.hashCode());
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
		MatnrMovement other = (MatnrMovement) obj;
		if (awkey == null) {
			if (other.awkey != null)
				return false;
		} else if (!awkey.equals(other.awkey))
			return false;
		if (created_date == null) {
			if (other.created_date != null)
				return false;
		} else if (!created_date.equals(other.created_date))
			return false;
		if (dmbtr == null) {
			if (other.dmbtr != null)
				return false;
		} else if (!dmbtr.equals(other.dmbtr))
			return false;
		if (from_werks == null) {
			if (other.from_werks != null)
				return false;
		} else if (!from_werks.equals(other.from_werks))
			return false;
		if (matnr_list_id == null) {
			if (other.matnr_list_id != null)
				return false;
		} else if (!matnr_list_id.equals(other.matnr_list_id))
			return false;
		if (mm_id == null) {
			if (other.mm_id != null)
				return false;
		} else if (!mm_id.equals(other.mm_id))
			return false;
		if (mm_type == null) {
			if (other.mm_type != null)
				return false;
		} else if (!mm_type.equals(other.mm_type))
			return false;
		if (Double.doubleToLongBits(new_dmbtr) != Double
				.doubleToLongBits(other.new_dmbtr))
			return false;
		if (Double.doubleToLongBits(old_dmbtr) != Double
				.doubleToLongBits(other.old_dmbtr))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (to_werks == null) {
			if (other.to_werks != null)
				return false;
		} else if (!to_werks.equals(other.to_werks))
			return false;
		return true;
	}

	public String getStatusName() {
		if (getStatus() != null) {
			return getStatuses().get(getStatus());
		}

		return "";
	}

	public Map<String, String> getStatuses() {
		Map<String, String> out = new HashMap<String, String>();
		out.put(STATUS_RECEIVED, "ПРИНЯТО");
		out.put(STATUS_MOVING, "В ПЕРЕМЕЩЕНИИ");
		return out;
	}
	
	@Transient
	Staff responsible;
	
	@Transient 
	Staff receiver;
	public Staff getResponsible() {
		return responsible;
	}

	public void setResponsible(Staff responsible) {
		this.responsible = responsible;
	}

	public Staff getReceiver() {
		return receiver;
	}

	public void setReceiver(Staff receiver) {
		this.receiver = receiver;
	}
	
	public String getTypeName(){
		String s = "";
		if(mm_type != null){
			if(mm_type.equals(MatnrMovement.TYPE_ACCOUNTABILITY)){
				s = "ПОДОТЧЕТ";
			}else if(mm_type.equals("MOVEMENT")){
				s = "ПЕРЕМЕЩЕНИЕ";
			}else if(mm_type.equals(MatnrMovement.TYPE_MOVEMENT_SEND)){
				s = "ОТПРАВКА";
			}else if(mm_type.equals(MatnrMovement.TYPE_MOVEMENT_RECEIVE)){
				s = "ПРИНЯТИЯ";
			}else if(mm_type.equals(MatnrMovement.TYPE_RETURN)){
				s = "ВОЗВРАТ";
			}else if(mm_type.equals(MatnrMovement.TYPE_SALE)){
				s = "ПРОДАЖА";
			}else if(mm_type.equals(MatnrMovement.TYPE_POSTING)){
				s = "ОПРИХОДОВАНИЕ";
			}
		}
		return s;
	}
	
	@ManyToOne
	@JoinColumn(name = "staff_id", referencedColumnName = "staff_id", insertable = false, updatable = false)
	private Staff staff;
	public Staff getStaff() {
		return staff;
	}
	public void setStaff(Staff staff) {
		this.staff = staff;
	}
	
	

//	@OneToOne(optional=false,targetEntity=Staff.class)
//	@JoinColumn(name="received_by",insertable=false,referencedColumnName="staff_id",updatable=false,nullable=true)
//	private Staff receiver;
//	public Staff getReceiver() {
//		return receiver;
//	}
//
//	public void setReceiver(Staff receiver) {
//		this.receiver = receiver;
//	}
	
}