package general.tables;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: WerksBranch
 *
 */
@Entity
@Table(name="werks_branch")

public class WerksBranch implements Serializable {

	@Id
	private Long branch_id;
	private String bukrs;
	private Long werks;
	private String text45;
	private static final long serialVersionUID = 1L;

	public WerksBranch() {
		super();
	}   
	public String getBukrs() {
		return this.bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}   
	public Long getWerks() {
		return this.werks;
	}

	public void setWerks(Long werks) {
		this.werks = werks;
	}   
	public Long getBranch_id() {
		return this.branch_id;
	}

	public void setBranch_id(Long branch_id) {
		this.branch_id = branch_id;
	}   
	public String getText45() {
		return this.text45;
	}

	public void setText45(String text45) {
		this.text45 = text45;
	}
   
}
