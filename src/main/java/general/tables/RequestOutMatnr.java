package general.tables;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


@Entity
@Table(name="REQUEST_OUT_MATNR")
public class RequestOutMatnr implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name="REQUEST_OUT_MATNR_ID_GENERATOR", sequenceName="SEQ_REQUEST_OUT_MATNR_ID",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REQUEST_OUT_MATNR_ID_GENERATOR")
	private Long id;
	

	private Long matnr;
	private Long request_id;
	private double quantity;
	
	



	//@ManyToOne(targetEntity=Matnr.class,fetch=FetchType.LAZY)
//	@OneToOne(fetch=FetchType.LAZY,cascade=CascadeType.PERSIST)
//	@JoinColumn(name="matnr",referencedColumnName="matnr",updatable=false,insertable=false)
//	@NotFound(action = NotFoundAction.IGNORE)
	
	public Long getMatnr() {
		return matnr;
	}
	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}
	public Long getRequest_id() {
		return request_id;
	}
	public void setRequest_id(Long request_id) {
		this.request_id = request_id;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	@Transient
	private Matnr matnrObject;
	public Matnr getMatnrObject() {
		return matnrObject;
	}
	public void setMatnrObject(Matnr matnrObject) {
		this.matnrObject = matnrObject;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Transient
	private double req_quantity = 0D;
	public double getReq_quantity() {
		return req_quantity;
	}
	public void setReq_quantity(double req_quantity) {
		this.req_quantity = req_quantity;
	}
	
	@Transient
	private double quantityInWerks;
	public double getQuantityInWerks() {
		return quantityInWerks;
	}
	public void setQuantityInWerks(double quantityInWerks) {
		this.quantityInWerks = quantityInWerks;
	}
	
}