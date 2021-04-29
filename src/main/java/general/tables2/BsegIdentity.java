package general.tables2;



import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;


@Embeddable
public class BsegIdentity implements Serializable {
    @Column(name = "bukrs")
    private String bukrs;

    @Column(name = "belnr")
    private Long belnr;

    @Column(name = "gjahr")
    private Integer gjahr;

    @Column(name = "buzei")
    private Integer buzei;

    public BsegIdentity() {
    }

    public BsegIdentity(String bukrs, Long belnr, Integer gjahr, Integer buzei) {
        this.bukrs = bukrs;
        this.belnr = belnr;
        this.gjahr = gjahr;
        this.buzei = buzei;
    }

    public String getBukrs() {
        return bukrs;
    }

    public void setBukrs(String bukrs) {
        this.bukrs = bukrs;
    }

    public Long getBelnr() {
        return belnr;
    }

    public void setBelnr(Long belnr) {
        this.belnr = belnr;
    }

    public Integer getGjahr() {
        return gjahr;
    }

    public void setGjahr(Integer gjahr) {
        this.gjahr = gjahr;
    }

    public Integer getBuzei() {
        return buzei;
    }

    public void setBuzei(Integer buzei) {
        this.buzei = buzei;
    }
}