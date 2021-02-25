package general.tables;

import general.MessageProvider;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The persistent class for the DEMONSTRATION database table.
 * 
 */
@Entity
@Table(name = "DEMONSTRATION")
@NamedQuery(name = "Demonstration.findAll", query = "SELECT d FROM Demonstration d")
public class Demonstration implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "DEMONSTRATION_ID_GENERATOR", sequenceName = "SEQ_DEMONSTRATION_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEMONSTRATION_ID_GENERATOR")
	@Column(unique = true, nullable = false, precision = 38)
	private Long id;

	@Column(name = "CUSTOMER_ADDRESS", length = 400)
	private String customerAddress;

	@Column(name = "CUSTOMER_MOBILE", length = 45)
	private String customerMobile;

	@Column(name = "CUSTOMER_NAME", length = 255)
	private String customerName;

	@Column(name = "DATE_TIME")
	private Date dateTime;

	@Column(name = "DEALER_ID", precision = 38)
	private Long dealerId;

	@Column(name = "DEALER_BRANCH_ID", precision = 38)
	private Long dealerBranchId;

	@Column(name = "DEMOSEC_ID", nullable = false, precision = 38)
	private Long demosecId;

	@Column(name = "DIRECTOR_ID", nullable = false)
	private Long directorId;

	@Column(name = "MANAGER_ID", nullable = false)
	private Long managerId;

	@Column(name = "REF_COUNT", nullable = false, precision = 38)
	private Integer refCount;

	// @Column(length = 45)
	// private String status;

	@Column(name = "STATUS_ID", nullable = false)
	private Integer statusId;

	@Column(name = "CREATED_AT")
	private Date createdAt;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	@Column(name = "UPDATED_AT")
	private Date updatedAt;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	private String bukrs;
	private Integer location;

	@Column(name = "BRANCH_ID")
	private Long branchId;

	@Column(name = "RECOMMENDATION_ID")
	private Long recommendationId;

	public Demonstration() {
		this.dealerId = 0L;
		this.directorId = 0L;
		this.demosecId = 0L;
		this.branchId = 0L;
		this.managerId = 0L;
		this.refCount = 0;
		this.dealerBranchId = 0L;
		this.recommendationId = 0L;
		this.statusId = 0;
	}

	public Long getRecommendationId() {
		return recommendationId;
	}

	public void setRecommendationId(Long recommendationId) {
		this.recommendationId = recommendationId;
	}

	public Integer getLocation() {
		return location;
	}

	public void setLocation(Integer location) {
		this.location = location;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCustomerAddress() {
		return this.customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getCustomerMobile() {
		return this.customerMobile;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}

	public String getCustomerName() {
		return this.customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Date getDateTime() {
		return this.dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public Long getDealerId() {
		return this.dealerId;
	}

	public void setDealerId(Long dealerId) {
		this.dealerId = dealerId;
	}

	public Long getDemosecId() {
		return this.demosecId;
	}

	public void setDemosecId(Long demosecId) {
		this.demosecId = demosecId;
	}

	public Long getDirectorId() {
		return this.directorId;
	}

	public void setDirectorId(Long directorId) {
		this.directorId = directorId;
	}

	public Long getManagerId() {
		return this.managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	public Integer getRefCount() {
		return this.refCount;
	}

	public void setRefCount(Integer refCount) {
		this.refCount = refCount;
	}

	// public String getStatus() {
	// return this.status;
	// }
	//
	// public void setStatus(String status) {
	// this.status = status;
	// }

	public Integer getStatusId() {
		return this.statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public Long getDealerBranchId() {
		return dealerBranchId;
	}

	public void setDealerBranchId(Long dealerBranchId) {
		this.dealerBranchId = dealerBranchId;
	}

	public final static Integer DEMO_STATUS_SHOWN = 1;
	public final static Integer DEMO_STATUS_MOVED = 2; // "MOVED";
	public final static Integer DEMO_STATUS_CANCELLED = 3; // "CANCELLED";
	public final static Integer DEMO_STATUS_SOLD = 4; // "SOLD";
	public final static Integer DEMO_STATUS_CONTRACT = 5;// "CONTRACT";
	public final static Integer DEMO_STATUS_DELIVERY = 6;// "Доставка (без
															// демо)";

	public final static Integer LOCATION_CITY = 1;
	public final static Integer LOCATION_OUT_OF_TOWN = 2;

	public static Map<Integer, String> getStatuses() {
		HashMap<Integer, String> out = new HashMap<Integer, String>();
		MessageProvider mp = new MessageProvider();
		out.put(DEMO_STATUS_SHOWN, mp.getValue("dms.demo.status_shown"));
		out.put(DEMO_STATUS_MOVED, mp.getValue("dms.demo.status_moved"));
		out.put(DEMO_STATUS_CANCELLED, mp.getValue("dms.demo.status_cancelled"));
		out.put(DEMO_STATUS_CONTRACT, mp.getValue("dms.demo.status_contract"));
		out.put(DEMO_STATUS_SOLD, mp.getValue("dms.demo.status_sold"));
		out.put(DEMO_STATUS_DELIVERY, "ДОСТАВКА");
		return out;
	}

	public static Map<Integer, String> getLocations() {
		Map<Integer, String> out = new HashMap<>();
		out.put(LOCATION_CITY, "Город");
		out.put(LOCATION_OUT_OF_TOWN, "Загород");
		return out;
	}

	public String getStatusName() {
		return Demonstration.getStatuses().get(getStatusId());
	}

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(targetEntity = Staff.class, fetch = FetchType.LAZY)
	@JoinColumn(insertable = false, updatable = false, nullable = true, name = "DEALER_ID", referencedColumnName = "staff_id")
	private Staff dealerStaff;

	public Staff getDealerStaff() {
		return dealerStaff;
	}

	public void setDealerStaff(Staff dealerStaff) {
		this.dealerStaff = dealerStaff;
	}

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(targetEntity = Staff.class, fetch = FetchType.LAZY)
	@JoinColumn(insertable = false, updatable = false, nullable = true, name = "CREATED_BY", referencedColumnName = "staff_id")
	private Staff creatorStaff;

	public Staff getCreatorStaff() {
		return creatorStaff;
	}

	public void setCreatorStaff(Staff creatorStaff) {
		this.creatorStaff = creatorStaff;
	}

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(targetEntity = Staff.class, fetch = FetchType.LAZY)
	@JoinColumn(insertable = false, updatable = false, nullable = true, name = "DEMOSEC_ID", referencedColumnName = "staff_id")
	private Staff demosecStaff;

	public Staff getDemosecStaff() {
		return demosecStaff;
	}

	public void setDemosecStaff(Staff demosecStaff) {
		this.demosecStaff = demosecStaff;
	}

}