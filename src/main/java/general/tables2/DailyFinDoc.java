package general.tables2;



import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "daily_fin_doc")
public class DailyFinDoc implements Serializable {

    @EmbeddedId
    private BsegIdentity bsegId;

    @Column(name = "budat")
    private Date budat;

    @Column(name = "monat")
    private Integer monat;

    @Column(name = "brnch")
    private Long brnch;

    @Column(name = "hkont")
    private String hkont;

    @Column(name = "waers")
    private String waers;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "shkzg")
    private String shkzg;

    @Column(name = "status")
    private Integer status;

    public DailyFinDoc() {
    }

    public DailyFinDoc(BsegIdentity bsegId, Date budat, Integer monat, Long brnch, String hkont, String waers, Double amount, String shkzg, Integer status) {
        this.bsegId = bsegId;
        this.budat = budat;
        this.monat = monat;
        this.brnch = brnch;
        this.hkont = hkont;
        this.waers = waers;
        this.amount = amount;
        this.shkzg = shkzg;
        this.status = status;
    }

    public BsegIdentity getBsegId() {
        return bsegId;
    }

    public void setBsegId(BsegIdentity bsegId) {
        this.bsegId = bsegId;
    }

    public Date getBudat() {
        return budat;
    }

    public void setBudat(Date budat) {
        this.budat = budat;
    }

    public Integer getMonat() {
        return monat;
    }

    public void setMonat(Integer monat) {
        this.monat = monat;
    }

    public Long getBrnch() {
        return brnch;
    }

    public void setBrnch(Long brnch) {
        this.brnch = brnch;
    }

    public String getHkont() {
        return hkont;
    }

    public void setHkont(String hkont) {
        this.hkont = hkont;
    }

    public String getWaers() {
        return waers;
    }

    public void setWaers(String waers) {
        this.waers = waers;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getShkzg() {
        return shkzg;
    }

    public void setShkzg(String shkzg) {
        this.shkzg = shkzg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}