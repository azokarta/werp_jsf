package general.tables;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


@Entity
@Table(name="REQUEST_MATNR")
public class RequestMatnr implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 775310691077110379L;


	@Id
	@SequenceGenerator(name="REQUEST_MATNR_ID_GENERATOR", sequenceName="SEQ_REQUEST_MATNR_ID",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REQUEST_MATNR_ID_GENERATOR")
	private Long id;
	

	private Long matnr;
	private Long request_id;
	private String matnr_str;
	private Long units;
	private String units_str;
	private double quantity;
	public Long getMatnr() {
		return matnr;
	}
	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}
	public String getMatnr_str() {
		return matnr_str;
	}
	public void setMatnr_str(String matnr_str) {
		this.matnr_str = matnr_str;
	}
	public Long getUnits() {
		return units;
	}
	public void setUnits(Long units) {
		this.units = units;
	}
	public String getUnits_str() {
		return units_str;
	}
	public void setUnits_str(String units_str) {
		this.units_str = units_str;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public Long getRequest_id() {
		return request_id;
	}
	public void setRequest_id(Long request_id) {
		this.request_id = request_id;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}




	//@ManyToOne(targetEntity=Matnr.class,fetch=FetchType.LAZY)
//	@OneToOne(fetch=FetchType.LAZY,cascade=CascadeType.PERSIST)
//	@JoinColumn(name="matnr",referencedColumnName="matnr",updatable=false,insertable=false)
//	@NotFound(action = NotFoundAction.IGNORE)
	
//	@ManyToOne(optional=false)
//	@JoinColumn(name="request_id",referencedColumnName="id",insertable=false,updatable=false)
//	private Request requestObject;
//	public Request getRequestObject() {
//		return requestObject;
//	}
//	public void setRequestObject(Request requestObject) {
//		this.requestObject = requestObject;
//	}




	@Transient
	private Matnr matnrObject;
	public Matnr getMatnrObject() {
		return matnrObject;
	}
	public void setMatnrObject(Matnr matnrObject) {
		this.matnrObject = matnrObject;
	}
	
}