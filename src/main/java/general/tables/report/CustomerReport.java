package general.tables.report;

import java.util.Date;

import general.tables.Customer;

public class CustomerReport extends Customer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1982551009804098334L;

	private Integer age;
	private String purchasedGoods;
	private Date purchasedDate;
	private Long contractId;

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public Date getPurchasedDate() {
		return purchasedDate;
	}

	public void setPurchasedDate(Date purchasedDate) {
		this.purchasedDate = purchasedDate;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getPurchasedGoods() {
		return purchasedGoods;
	}

	public void setPurchasedGoods(String purchasedGoods) {
		this.purchasedGoods = purchasedGoods;
	}

}
