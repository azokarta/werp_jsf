package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "order_list")
@javax.persistence.SequenceGenerator(name="seq_order_list_id",sequenceName="seq_order_list_id",allocationSize=1)
public class OrderList {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_order_list_id")
    private Long id;

	@Column(name = "order_id")
	private Long order_id;
	
	@Column(name = "matnr_id")
	private Long matnr_id;
	
	@Column(name = "unit_price")
	private double unit_price;

	@Column(name = "currency")
	private String currency;

	@Column(name = "quantity")
	private int quantity;

	@Column(name = "info")
	private String info;
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public double getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(double unit_price) {
		this.unit_price = unit_price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public Long getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Long order_id) {
		this.order_id = order_id;
	}

	public Long getMatnr_id() {
		return matnr_id;
	}

	public void setMatnr_id(Long matnr_id) {
		this.matnr_id = matnr_id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Transient
	String matnrName;
	
	@Transient
	String matnrCode;

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
	
	
	
	/*
	@ManyToOne    
	@JoinColumns({
		  @JoinColumn(name = "bukrs", referencedColumnName ="bukrs", insertable = false, updatable = false),
		  @JoinColumn(name = "branch", referencedColumnName ="branch_id", insertable = false, updatable = false)
		})
	
	private Branch branch; 
	*/
	
}
