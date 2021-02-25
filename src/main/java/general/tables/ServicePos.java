package general.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "service_pos")
@javax.persistence.SequenceGenerator(name="seq_service_pos_id",sequenceName="seq_service_pos_id",allocationSize=1)
public class ServicePos {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_service_pos_id")
    private Long id;
	
	@Column(name = "serv_id")
	private Long serv_id;
	
	@Column(name = "matnr_id")
	private Long matnr_id;
	
	@Column(name = "matnr_price")
	private double matnr_price;
	
	@Column(name = "currency")
	private String currency;
	
	@Column(name = "operation")
	private Long operation;
	//1 - ПРОДАЖА_ЗАП, 2 - РЕМОНТ_ТОВАРА, 3 - ПРОФИЛАКТИКА, 4 - УСТАНОВКА
	public static int OPER_SELL = 1;
	public static int OPER_SERVICE = 2;	

	@Column(name = "serv_type") 
	private Long serv_type;
	//1 - Service Roboclean, 2 - Service Cebilon, 3 - Filters, 4 - Установка, 5 - Другое	

	@Column(name = "info1")
	private String info1;
	
	@Column(name = "info2")
	private String info2;
	
	@Column(name = "quantity")
	private Long quantity;
	
	@Column(name = "warranty")
	private int warranty;
	//0 - FALSE, 1 - TRUE 
	
	@Column(name = "summ")
	private double summ;
	
	@Column(name = "fno")
	private int fno;
	
	@Column(name = "new_war_inm")
	private int new_war_inm;
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + fno;
		result = prime * result + ((info1 == null) ? 0 : info1.hashCode());
		result = prime * result + ((info2 == null) ? 0 : info2.hashCode());
		result = prime * result
				+ ((matnr_id == null) ? 0 : matnr_id.hashCode());
		long temp;
		temp = Double.doubleToLongBits(matnr_price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + new_war_inm;
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
		result = prime * result
				+ ((quantity == null) ? 0 : quantity.hashCode());
		temp = Double.doubleToLongBits(summ);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + warranty;
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
		ServicePos other = (ServicePos) obj;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		if (fno != other.fno)
			return false;
		if (info1 == null) {
			if (other.info1 != null)
				return false;
		} else if (!info1.equals(other.info1))
			return false;
		if (info2 == null) {
			if (other.info2 != null)
				return false;
		} else if (!info2.equals(other.info2))
			return false;
		if (matnr_id == null) {
			if (other.matnr_id != null)
				return false;
		} else if (!matnr_id.equals(other.matnr_id))
			return false;
		if (Double.doubleToLongBits(matnr_price) != Double
				.doubleToLongBits(other.matnr_price))
			return false;
		if (new_war_inm != other.new_war_inm)
			return false;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
			return false;
		if (quantity == null) {
			if (other.quantity != null)
				return false;
		} else if (!quantity.equals(other.quantity))
			return false;
		if (Double.doubleToLongBits(summ) != Double
				.doubleToLongBits(other.summ))
			return false;
		if (warranty != other.warranty)
			return false;
		return true;
	}

	public Long getServ_type() {
		return serv_type;
	}

	public void setServ_type(Long serv_type) {
		this.serv_type = serv_type;
	}

	
	public static final int LSTATE_SKLAD = 1;
	public static final int LSTATE_GIVEN = 2;
	
	public static final int MAX_FNO = 5;
	
	public static int getLstateSklad() {
		return LSTATE_SKLAD;
	}

	public static int getLstateGiven() {
		return LSTATE_GIVEN;
	}

	public int getNew_war_inm() {
		return new_war_inm;
	}

	public void setNew_war_inm(int new_war_inm) {
		this.new_war_inm = new_war_inm;
	}

	public int getFno() {
		return fno;
	}

	public void setFno(int fno) {
		this.fno = fno;
	}

	public String getInfo1() {
		return info1;
	}

	public void setInfo1(String info1) {
		this.info1 = info1;
	}

	public String getInfo2() {
		return info2;
	}

	public void setInfo2(String info2) {
		this.info2 = info2;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public int getWarranty() {
		return warranty;
	}

	public void setWarranty(int warranty) {
		this.warranty = warranty;
	}

	public double getSumm() {
		return summ;
	}

	public void setSumm(double summ) {
		this.summ = summ;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getServ_id() {
		return serv_id;
	}

	public void setServ_id(Long serv_id) {
		this.serv_id = serv_id;
	}

	public Long getMatnr_id() {
		return matnr_id;
	}

	public void setMatnr_id(Long matnr_id) {
		this.matnr_id = matnr_id;
	}

	public double getMatnr_price() {
		return matnr_price;
	}

	public void setMatnr_price(double matnr_price) {
		this.matnr_price = matnr_price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Long getOperation() {
		return operation;
	}

	public void setOperation(Long operation) {
		this.operation = operation;
	}
}
