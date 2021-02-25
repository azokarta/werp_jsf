package general.tables;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the MATNR_SPARE_PARTS database table.
 * 
 */
@Entity
@Table(name="MATNR_SPARE_PARTS")
@NamedQuery(name="MatnrSparePart.findAll", query="SELECT m FROM MatnrSparePart m")
public class MatnrSparePart implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="MATNR_SPARE_PARTS_ID_GENERATOR", sequenceName="SEQ_MATNR_SPARE_PARTS_ID",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MATNR_SPARE_PARTS_ID_GENERATOR")
	private Long id;

	private Long sparepart_id;

	private Long tovar_id;

	public MatnrSparePart() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSparepart_id() {
		return this.sparepart_id;
	}

	public void setSparepart_id(Long sparepart_id) {
		this.sparepart_id = sparepart_id;
	}

	public Long getTovar_id() {
		return this.tovar_id;
	}

	public void setTovar_id(Long tovar_id) {
		this.tovar_id = tovar_id;
	}

}