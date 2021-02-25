package general.tables;

import java.io.Serializable;
import javax.persistence.*;

import general.Validation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the KPI_SETTING database table.
 * 
 */
@Entity
@Table(name = "KPI_SETTING")
@NamedQuery(name = "KpiSetting.findAll", query = "SELECT k FROM KpiSetting k")
public class KpiSetting implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "KPI_SETTING_ID_GENERATOR", sequenceName = "SEQ_KPI_SETTING_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "KPI_SETTING_ID_GENERATOR")
	@Column(updatable = false)
	private Long id;

	@Column(name = "BRANCH_ID")
	private Long branchId;

	private String bukrs;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_AT")
	private Date createdAt;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	@Temporal(TemporalType.DATE)
	@Column(name = "FROM_DATE")
	private Date fromDate;

	@Column(name = "POSITION_ID")
	private Long positionId;

	@Temporal(TemporalType.DATE)
	@Column(name = "TO_DATE")
	private Date toDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED_AT")
	private Date updatedAt;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	// bi-directional many-to-one association to KpiItem
	@OneToMany(mappedBy = "kpiSetting", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<KpiItem> kpiItems;

	public KpiSetting() {
		setKpiItems(new ArrayList<>());
	}

	public Long getBranchId() {
		return this.branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public String getBukrs() {
		return this.bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Long getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getFromDate() {
		return this.fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Long getPositionId() {
		return this.positionId;
	}

	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}

	public Date getToDate() {
		return this.toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Long getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public List<KpiItem> getKpiItems() {
		return this.kpiItems;
	}

	public void setKpiItems(List<KpiItem> kpiItems) {
		this.kpiItems = kpiItems;
	}

	public KpiItem addKpiItem(KpiItem kpiItem) {
		getKpiItems().add(kpiItem);
		kpiItem.setKpiSetting(this);

		return kpiItem;
	}

	public KpiItem removeKpiItem(KpiItem kpiItem) {
		getKpiItems().remove(kpiItem);
		kpiItem.setKpiSetting(null);

		return kpiItem;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Transient
	public boolean isNew() {
		return Validation.isEmptyLong(getId());
	}
}