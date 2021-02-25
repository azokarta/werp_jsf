package general.tables;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GenerationType;

@Entity
@Table(name = "fact_table")
@javax.persistence.SequenceGenerator(name="seq_fact_table_id",sequenceName="seq_fact_table_id", allocationSize = 1)
public class FactTable {
	
	public FactTable() {
		this.setStaff_id(0L);
		this.setYear(Calendar.getInstance().get(Calendar.YEAR));
		this.setMonth(Calendar.getInstance().get(Calendar.MONTH));
		this.setSale_count(0);
		this.setCancel_count(0);
		this.setGroup_count(0);
		this.setSumm(0D);
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_fact_table_id")
	private Long fact_id;
	
	@Column(name = "staff_id")
	private Long staff_id;
	
    @Column(name = "pyramid_id")
	private Long pyramid_id;
        
	@Column(name = "year")
	private Integer year;
	
	@Column(name = "month")
	private Integer month;
	
	@Column(name = "sale_count")
	private Integer sale_count;
	
	@Column(name = "cancel_count")
	private Integer cancel_count;
	
	@Column(name = "group_count")
	private Integer group_count;
	
	@Column(name = "summ")
	private Double summ;
	
	@Column(name = "complete_summ")
	private Double complete_summ;
	
	@Column(name = "branch_id")
	private Long branch_id;
	
	@Column(name = "waers")
	private String waers;
	
	@Column(name = "position_id")
	private Long position_id;
	
	@Column(name = "business_area_id")
	private Long business_area_id;

	public Long getFact_id() {
		return fact_id;
	}
	
	public Long getStaff_id() {
		return staff_id;
	}

	public void setStaff_id(Long staff_id) {
		this.staff_id = staff_id;
	}
        
        public Long getPyramid_id() {
		return pyramid_id;
	}

	public void setPyramid_id(Long pyramid_id) {
		this.pyramid_id = pyramid_id;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getSale_count() {
		return sale_count;
	}

	public void setSale_count(Integer sale_count) {
		this.sale_count = sale_count;
	}

	public Integer getCancel_count() {
		return cancel_count;
	}

	public void setCancel_count(Integer cancel_count) {
		this.cancel_count = cancel_count;
	}

	public Integer getGroup_count() {
		return group_count;
	}

	public void setGroup_count(Integer group_count) {
		this.group_count = group_count;
	}

	public Double getSumm() {
		return summ;
	}

	public void setSumm(Double summ) {
		this.summ = summ;
	}

	public Long getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}

	public String getWaers() {
		return waers;
	}

	public void setWaers(String waers) {
		this.waers = waers;
	}

	public Long getPosition_id() {
		return position_id;
	}

	public void setPosition_id(Long position_id) {
		this.position_id = position_id;
	}

	public Long getBusiness_area_id() {
		return business_area_id;
	}

	public void setBusiness_area_id(Long business_area_id) {
		this.business_area_id = business_area_id;
	}

	public Double getComplete_summ() {
		return complete_summ;
	}

	public void setComplete_summ(Double complete_summ) {
		this.complete_summ = complete_summ;
	}
	
}
