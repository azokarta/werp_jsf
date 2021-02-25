package general.tables;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The persistent class for the REV_ITEM database table.
 * 
 */
@Entity
@Table(name = "REV_ITEM")
@NamedQuery(name = "RevItem.findAll", query = "SELECT r FROM RevItem r")
public class RevItem implements Serializable {
	private static final long serialVersionUID = 1L;

	private String barcode;

	@Column(name = "DB_QUANTITY")
	private Double dbQuantity;

	@Column(name = "FACT_QUANTITY")
	private Double factQuantity;

	@Id
	@SequenceGenerator(name = "REV_ITEM_ID_GENERATOR", sequenceName = "SEQ_REV_ITEM_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REV_ITEM_ID_GENERATOR")
	private Long id;

	private Long matnr;

	@Column(name = "PARENT_MATNR")
	private Long parentMatnr;

	@Column(name = "TITLE_ID")
	private Long titleId;

	@Column(name = "STATE_ID")
	private Integer stateId;

	@Column(name = "UNIT_PRICE")
	private Double unitPrice;

	@Column(name = "UNUSABLE_QUANTITY")
	private Double unusableQuantity;

	@Column(name = "USEFUL_QUANTITY")
	private Double usefulQuantity;

	public RevItem() {
		this.dbQuantity = 0D;
		this.factQuantity = 0D;
		this.matnr = 0L;
		this.parentMatnr = 0L;
		this.usefulQuantity = 0D;
		this.unusableQuantity = 0D;
		this.unitPrice = 0D;
		this.stateId = 0;
		this.titleId = 0L;
	}

	public String getBarcode() {
		return this.barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Double getDbQuantity() {
		return this.dbQuantity;
	}

	public void setDbQuantity(Double dbQuantity) {
		this.dbQuantity = dbQuantity;
	}

	public Double getFactQuantity() {
		return this.factQuantity;
	}

	public void setFactQuantity(Double factQuantity) {
		this.factQuantity = factQuantity;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMatnr() {
		return this.matnr;
	}

	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}

	public Long getParentMatnr() {
		return this.parentMatnr;
	}

	public void setParentMatnr(Long parentMatnr) {
		this.parentMatnr = parentMatnr;
	}

	public Long getTitleId() {
		return titleId;
	}

	public void setTitleId(Long titleId) {
		this.titleId = titleId;
	}

	public Integer getStateId() {
		return this.stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public Double getUnitPrice() {
		return this.unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Double getUnusableQuantity() {
		return this.unusableQuantity;
	}

	public void setUnusableQuantity(Double unusableQuantity) {
		this.unusableQuantity = unusableQuantity;
	}

	public Double getUsefulQuantity() {
		return this.usefulQuantity;
	}

	public void setUsefulQuantity(Double usefulQuantity) {
		this.usefulQuantity = usefulQuantity;
	}

	@Transient
	private Matnr matnrObject;

	public Matnr getMatnrObject() {
		return matnrObject;
	}

	public void setMatnrObject(Matnr matnrObject) {
		this.matnrObject = matnrObject;
	}

	@Transient
	private Double overQuantity = 0D;// Излишка

	public Double getOverQuantity() {
		return overQuantity;
	}

	public void setOverQuantity(Double overQuantity) {
		this.overQuantity = overQuantity;
	}

	@Transient
	private Double deficitQuantity = 0D;// Недостача

	public Double getDeficitQuantity() {
		return deficitQuantity;
	}

	public void setDeficitQuantity(Double deficitQuantity) {
		this.deficitQuantity = deficitQuantity;
	}

	@Transient
	private Double overAmount = 0D;

	public Double getOverAmount() {
		return overAmount;
	}

	public void setOverAmount(Double overAmount) {
		this.overAmount = overAmount;
	}

	@Transient
	private Double deficitAmount = 0D;

	public Double getDeficitAmount() {
		return deficitAmount;
	}

	public void setDeficitAmount(Double deficitAmount) {
		this.deficitAmount = deficitAmount;
	}

}