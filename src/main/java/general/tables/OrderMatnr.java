package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "order_matnr")
@javax.persistence.SequenceGenerator(name = "seq_order_matnr_id", sequenceName = "seq_order_matnr_id", allocationSize = 1)
public class OrderMatnr {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_order_matnr_id")
	private Long id;

	private Long order_id;
	private Long matnr;
	private Double quantity;
	private Double unit_price;
	private Double amount;
	private Double posting_quantity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Long order_id) {
		this.order_id = order_id;
	}

	public Long getMatnr() {
		return matnr;
	}

	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(Double unit_price) {
		this.unit_price = unit_price;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public OrderMatnr() {
		super();
		setAmount(0D);
		setQuantity(0D);
		setUnit_price(0D);
		setPosting_quantity(0D);
	}

	public Double getPosting_quantity() {
		return posting_quantity;
	}

	public void setPosting_quantity(Double posting_quantity) {
		this.posting_quantity = posting_quantity;
	}

	@Transient
	private Matnr matnrObject;

	public Matnr getMatnrObject() {
		return matnrObject;
	}

	public void setMatnrObject(Matnr matnrObject) {
		this.matnrObject = matnrObject;
	}

}
