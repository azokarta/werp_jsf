package general.tables;

import java.io.Serializable;
import java.lang.Double;
import java.lang.Long;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: MatnrMovementItem
 *
 */
@Entity
@Table(name="matnr_movement_item")
@javax.persistence.SequenceGenerator(name="seq_matnr_movement_item_id",sequenceName="seq_matnr_movement_item_id",allocationSize=1)

public class MatnrMovementItem implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_matnr_movement_item_id")
	@Column(name = "item_id")
    private Long item_id;
	
	private Long mm_id;
	private Long matnr_list_id;
	private Double old_dmbtr;
	private Double new_dmbtr;
	private String status;
	private Long matnr;
	private Date received_date;
	
	private static final long serialVersionUID = 1L;

	public MatnrMovementItem() {
		super();
	}
   
	
	public Date getReceived_date() {
		return received_date;
	}
	public void setReceived_date(Date received_date) {
		this.received_date = received_date;
	}


	public Long getMatnr_list_id() {
		return this.matnr_list_id;
	}

	public void setMatnr_list_id(Long matnr_list_id) {
		this.matnr_list_id = matnr_list_id;
	}   
	public Double getOld_dmbtr() {
		return this.old_dmbtr;
	}

	public void setOld_dmbtr(Double old_dmbtr) {
		this.old_dmbtr = old_dmbtr;
	}   
	public Double getNew_dmbtr() {
		return this.new_dmbtr;
	}

	public void setNew_dmbtr(Double new_dmbtr) {
		this.new_dmbtr = new_dmbtr;
	}
	public Long getMm_id() {
		return mm_id;
	}
	public void setMm_id(Long mm_id) {
		this.mm_id = mm_id;
	}

	public Long getItem_id() {
		return item_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getMatnr() {
		return matnr;
	}

	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}
   
	@OneToOne(targetEntity=MatnrList.class)
	@JoinColumn(name="matnr_list_id",referencedColumnName="matnr_list_id",updatable=false,insertable=false)
	private MatnrList matnrList;
	public MatnrList getMatnrList() {
		return matnrList;
	}
	public void setMatnrList(MatnrList matnrList) {
		this.matnrList = matnrList;
	}
	
	@Transient
	Matnr matnrObject;
	public Matnr getMatnrObject() {
		return matnrObject;
	}
	public void setMatnrObject(Matnr matnrObject) {
		this.matnrObject = matnrObject;
	}
	
}
