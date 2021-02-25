package general.tables;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by admin on 22.11.2017.
 */
@Entity
@Table(name = "KPI_ITEM")
public class KpiItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "KPI_ITEM_ID_GENERATOR", sequenceName = "SEQ_KPI_ITEM_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "KPI_ITEM_ID_GENERATOR")
    @Column(updatable = false)
    private Long id;

    @Column(name = "INDICATOR_ID")
    private Integer indicatorId;

    @Column(name = "POINT")
    private Double point;

    @Column(name = "VALUE")
    private Double value;

    // bi-directional many-to-one association to KpiSetting
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KPI_ID", referencedColumnName = "ID")
    private KpiSetting kpiSetting;

    public KpiItem() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIndicatorId() {
        return this.indicatorId;
    }

    public void setIndicatorId(Integer indicatorId) {
        this.indicatorId = indicatorId;
    }

    public Double getPoint() {
        return this.point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }

    public Double getValue() {
        return this.value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public KpiSetting getKpiSetting() {
        return this.kpiSetting;
    }

    public void setKpiSetting(KpiSetting kpiSetting) {
        this.kpiSetting = kpiSetting;
    }

    @Transient
    public String getIndicatorName(){
        //return KpiConstants.getIndicatorsMap().get(indicatorId);
    	return "";
    }

    //Набранный бал
    @Transient
    private Double score;

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    //Количество выполненных
    @Transient
    private Double doneValue;

    public Double getDoneValue() {
        return doneValue;
    }

    public void setDoneValue(Double doneValue) {
        this.doneValue = doneValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KpiItem item = (KpiItem) o;

        if (id != null ? !id.equals(item.id) : item.id != null) return false;
        if (indicatorId != null ? !indicatorId.equals(item.indicatorId) : item.indicatorId != null) return false;
        return kpiSetting != null ? kpiSetting.equals(item.kpiSetting) : item.kpiSetting == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (indicatorId != null ? indicatorId.hashCode() : 0);
        result = 31 * result + (kpiSetting != null ? kpiSetting.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "KpiItem{" +
                "id=" + id +
                ", indicatorId=" + indicatorId +
                ", point=" + point +
                ", value=" + value +
                ", kpiSetting=" + kpiSetting +
                ", score=" + score +
                ", doneValue=" + doneValue +
                '}';
    }
}
