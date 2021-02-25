package general.tables;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.Long;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: OrderOut
 *
 */
@Entity
@Table(name="order_out_list")
@javax.persistence.SequenceGenerator(name="seq_order_out_list_id",sequenceName="seq_order_out_list_id",allocationSize=1)

public class OrderOutList implements Serializable {

	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_order_out_list_id")
	@Column(name = "id")
	private Long id;
	
	private Long order_id_out;
	private Long order_id_in;
	private int quantity_out;
	private int quantity_in;
	private Long matnr_id;
	private static final long serialVersionUID = 1L;

	public OrderOutList() {
		super();
	}   
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}   
	public Long getOrder_id_out() {
		return this.order_id_out;
	}

	public void setOrder_id_out(Long order_id_out) {
		this.order_id_out = order_id_out;
	}   
	public Long getOrder_id_in() {
		return this.order_id_in;
	}

	public void setOrder_id_in(Long order_id_in) {
		this.order_id_in = order_id_in;
	}   
	public int getQuantity_out() {
		return this.quantity_out;
	}

	public void setQuantity_out(int quantity_out) {
		this.quantity_out = quantity_out;
	}   
	public int getQuantity_in() {
		return this.quantity_in;
	}

	public void setQuantity_in(int quantity_in) {
		this.quantity_in = quantity_in;
	}
	public Long getMatnr_id() {
		return matnr_id;
	}
	public void setMatnr_id(Long matnr_id) {
		this.matnr_id = matnr_id;
	}
	
	@Transient
	private String matnrName;
	
	@Transient
	private String matnrCode;

	public String getMatnrName() {
		return matnrName;
	}
	public void setMatnrName(String matnrName) {
		this.matnrName = matnrName;
	}
	public String getMatnrCode() {
		return matnrCode;
	}
	public void setMatnrCode(String matnrCode) {
		this.matnrCode = matnrCode;
	}
	
	
	
   
}
