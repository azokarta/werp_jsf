package general.tables;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;



/**
 * The persistent class for the WRITEOFF_REPAIR_ITEM database table.
 * 
 */
@Entity
@Table(name="WRITEOFF_REPAIR_ITEM")
@NamedQuery(name="WriteoffRepairItem.findAll", query="SELECT w FROM WriteoffRepairItem w")
public class WriteoffRepairItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="WRITEOFF_REPAIR_ITEM_ID_GENERATOR", sequenceName="SEQ_WRITEOFF_REPAIR_ITEM_ID", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="WRITEOFF_REPAIR_ITEM_ID_GENERATOR")
	private Long id;

	private String barcode;

	private Long matnr;

	private Double quantity;

	@Column(name="STATE_ID")
	private int stateId;

	@Column(name="TYPE_ID")
	private int typeId;
	
	//bi-directional many-to-one association to WriteoffRepair
	@ManyToOne
	@JoinColumn(name="WR_ID")
	private WriteoffRepair writeoffRepair;

	public WriteoffRepairItem() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBarcode() {
		return this.barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Long getMatnr() {
		return this.matnr;
	}

	public void setMatnr(Long matnr) {
		this.matnr = matnr;
	}

	public Double getQuantity() {
		return this.quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public int getStateId() {
		return this.stateId;
	}

	public void setStateId(int stateId) {
		this.stateId = stateId;
	}

	public int getTypeId() {
		return this.typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public WriteoffRepair getWriteoffRepair() {
		return this.writeoffRepair;
	}

	public void setWriteoffRepair(WriteoffRepair writeoffRepair) {
		this.writeoffRepair = writeoffRepair;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matnr == null) ? 0 : matnr.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WriteoffRepairItem other = (WriteoffRepairItem) obj;
		if (matnr == null) {
			if (other.matnr != null)
				return false;
		} else if (!matnr.equals(other.matnr))
			return false;
		return true;
	}	
	
}