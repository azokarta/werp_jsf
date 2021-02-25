package general.tables;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by admin on 22.11.2017.
 */
@Entity
@Table(name = "KPI_DATA")
public class KpiData implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "KPI_DATA_ID_GENERATOR", sequenceName = "SEQ_KPI_DATA_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "KPI_DATA_ID_GENERATOR")
    @Column(updatable = false)
    private Long id;

    @Column(name = "STAFF_ID")
    private Long staffId;

    @Column(name = "BRANCH_ID")
    private Long branchId;

    private String bukrs;

    private Double value;
    private Double point;

    @Column(name = "DONE_VALUE")
    private Double doneValue;

    @Column(name = "INDICATOR_ID")
    private Integer indicatorId;

    @Column(name = "POSITION_ID")
    private Long positionId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT")
    private Date createdAt;

    private int year;
    private int month;
    private Double score;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT")
    private Date updatedAt;

    @Column(name = "VERSION")
    @Version
    private Integer version;

    public KpiData() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

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

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }

    public Double getDoneValue() {
        return doneValue;
    }

    public void setDoneValue(Double doneValue) {
        this.doneValue = doneValue;
    }

    public Integer getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Integer indicatorId) {
        this.indicatorId = indicatorId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
