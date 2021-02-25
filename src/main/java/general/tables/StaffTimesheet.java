package general.tables;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * The persistent class for the STAFF_TIMESHEET database table.
 * 
 */
@Entity
@Table(name="STAFF_TIMESHEET")
@NamedQuery(name="StaffTimesheet.findAll", query="SELECT s FROM StaffTimesheet s")
public class StaffTimesheet implements Serializable {
	public static final Integer STATUS_PRESENT = 1;// Присутствовал
	public static final Integer STATUS_MISSING = 2;// отсутствовал
	public static final Integer STATUS_ILL = 3;// больничный
	public static final Integer STATUS_BUSINESS_TRIP = 4;// Командировка
	
	public Map<Integer, String> getStatuses(){
		Map<Integer, String> out = new HashMap<Integer, String>();
		
		out.put(StaffTimesheet.STATUS_PRESENT, "Присутствовал");
		out.put(StaffTimesheet.STATUS_MISSING, "Отсутствовал");
		out.put(StaffTimesheet.STATUS_ILL, "Больничный");
		out.put(StaffTimesheet.STATUS_BUSINESS_TRIP, "Командировка");
		
		return out;
	}
	
	public Map<Integer, String> getShortStatuses(){
		Map<Integer, String> out = new HashMap<Integer, String>();
		
		out.put(StaffTimesheet.STATUS_PRESENT, "Пр.");
		out.put(StaffTimesheet.STATUS_MISSING, "От.");
		out.put(StaffTimesheet.STATUS_ILL, "Бол.");
		out.put(StaffTimesheet.STATUS_BUSINESS_TRIP, "Ком.");
		
		return out;
	}
	
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="STAFF_TIMESHEET_ID_GENERATOR", sequenceName="SEQ_STAFF_TIMESHEET_ID",allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="STAFF_TIMESHEET_ID_GENERATOR")
	private long id;

	@Column(name="CREATED_BY")
	private Long createdBy;

	@Temporal(TemporalType.DATE)
	@Column(name="CREATED_DATE")
	private Date createdDate;

	private Integer d1;
	private Integer d10;
	private Integer d11;
	private Integer d12;
	private Integer d13;
	private Integer d14;
	private Integer d15;
	private Integer d16;
	private Integer d17;
	private Integer d18;
	private Integer d19;
	private Integer d2;
	private Integer d20;
	private Integer d21;
	private Integer d22;
	private Integer d23;
	private Integer d24;
	private Integer d25;
	private Integer d26;
	private Integer d27;
	private Integer d28;
	private Integer d29;
	private Integer d3;
	private Integer d30;
	private Integer d31;
	private Integer d4;
	private Integer d5;
	private Integer d6;
	private Integer d7;
	private Integer d8;
	private Integer d9;

	@Column(name="\"MONTH\"")
	private Integer month;

	@Column(name="UPDATED_BY")
	private Long updatedBy;

	@Temporal(TemporalType.DATE)
	@Column(name="UPDATED_DATE")
	private Date updatedDate;

	@Column(name="\"YEAR\"")
	private Integer year;
	
	@Column(name="BRANCH_ID")
	private Long branchId;
	
	@Column(name="BUKRS")
	private String bukrs;
	
	

	//bi-directional many-to-one association to StaffTimesheet
//	@ManyToOne
//	@JoinColumn(name="BRANCH_ID")
//	private Branch branch;

	//bi-directional many-to-one association to Company
//	@ManyToOne
//	@JoinColumn(name="BUKRS")
//	private Company company;

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	//bi-directional many-to-one association to Staff
	@ManyToOne
	@JoinColumn(name="STAFF_ID")
	private Staff staff;

	public StaffTimesheet() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getD1() {
		return this.d1;
	}

	public void setD1(Integer d1) {
		this.d1 = d1;
	}

	public Integer getD10() {
		return this.d10;
	}

	public void setD10(Integer d10) {
		this.d10 = d10;
	}

	public Integer getD11() {
		return this.d11;
	}

	public void setD11(Integer d11) {
		this.d11 = d11;
	}

	public Integer getD12() {
		return this.d12;
	}

	public void setD12(Integer d12) {
		this.d12 = d12;
	}

	public Integer getD13() {
		return this.d13;
	}

	public void setD13(Integer d13) {
		this.d13 = d13;
	}

	public Integer getD14() {
		return this.d14;
	}

	public void setD14(Integer d14) {
		this.d14 = d14;
	}

	public Integer getD15() {
		return this.d15;
	}

	public void setD15(Integer d15) {
		this.d15 = d15;
	}

	public Integer getD16() {
		return this.d16;
	}

	public void setD16(Integer d16) {
		this.d16 = d16;
	}

	public Integer getD17() {
		return this.d17;
	}

	public void setD17(Integer d17) {
		this.d17 = d17;
	}

	public Integer getD18() {
		return this.d18;
	}

	public void setD18(Integer d18) {
		this.d18 = d18;
	}

	public Integer getD19() {
		return this.d19;
	}

	public void setD19(Integer d19) {
		this.d19 = d19;
	}

	public Integer getD2() {
		return this.d2;
	}

	public void setD2(Integer d2) {
		this.d2 = d2;
	}

	public Integer getD20() {
		return this.d20;
	}

	public void setD20(Integer d20) {
		this.d20 = d20;
	}

	public Integer getD21() {
		return this.d21;
	}

	public void setD21(Integer d21) {
		this.d21 = d21;
	}

	public Integer getD22() {
		return this.d22;
	}

	public void setD22(Integer d22) {
		this.d22 = d22;
	}

	public Integer getD23() {
		return this.d23;
	}

	public void setD23(Integer d23) {
		this.d23 = d23;
	}

	public Integer getD24() {
		return this.d24;
	}

	public void setD24(Integer d24) {
		this.d24 = d24;
	}

	public Integer getD25() {
		return this.d25;
	}

	public void setD25(Integer d25) {
		this.d25 = d25;
	}

	public Integer getD26() {
		return this.d26;
	}

	public void setD26(Integer d26) {
		this.d26 = d26;
	}

	public Integer getD27() {
		return this.d27;
	}

	public void setD27(Integer d27) {
		this.d27 = d27;
	}

	public Integer getD28() {
		return this.d28;
	}

	public void setD28(Integer d28) {
		this.d28 = d28;
	}

	public Integer getD29() {
		return this.d29;
	}

	public void setD29(Integer d29) {
		this.d29 = d29;
	}

	public Integer getD3() {
		return this.d3;
	}

	public void setD3(Integer d3) {
		this.d3 = d3;
	}

	public Integer getD30() {
		return this.d30;
	}

	public void setD30(Integer d30) {
		this.d30 = d30;
	}

	public Integer getD31() {
		return this.d31;
	}

	public void setD31(Integer d31) {
		this.d31 = d31;
	}

	public Integer getD4() {
		return this.d4;
	}

	public void setD4(Integer d4) {
		this.d4 = d4;
	}

	public Integer getD5() {
		return this.d5;
	}

	public void setD5(Integer d5) {
		this.d5 = d5;
	}

	public Integer getD6() {
		return this.d6;
	}

	public void setD6(Integer d6) {
		this.d6 = d6;
	}

	public Integer getD7() {
		return this.d7;
	}

	public void setD7(Integer d7) {
		this.d7 = d7;
	}

	public Integer getD8() {
		return this.d8;
	}

	public void setD8(Integer d8) {
		this.d8 = d8;
	}

	public Integer getD9() {
		return this.d9;
	}

	public void setD9(Integer d9) {
		this.d9 = d9;
	}

	public Integer getMonth() {
		return this.month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Long getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Integer getYear() {
		return this.year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Staff getStaff() {
		return this.staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

}