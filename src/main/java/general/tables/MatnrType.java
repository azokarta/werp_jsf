package general.tables;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: MatnrType
 *
 */
@Entity
@Table(name="matnr_type")
@javax.persistence.SequenceGenerator(name="seq_matnr_type_id",sequenceName="seq_matnr_type_id",allocationSize=1)

public class MatnrType implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_matnr_type_id")
	private Long id;
	private String name_short;
	private String name_long;
	private static final long serialVersionUID = 1L;

	public MatnrType() {
		super();
	}   
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}   
	public String getName_short() {
		return this.name_short;
	}

	public void setName_short(String name_short) {
		this.name_short = name_short;
	}   
	public String getName_long() {
		return this.name_long;
	}

	public void setName_long(String name_long) {
		this.name_long = name_long;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((name_long == null) ? 0 : name_long.hashCode());
		result = prime * result
				+ ((name_short == null) ? 0 : name_short.hashCode());
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
		MatnrType other = (MatnrType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name_long == null) {
			if (other.name_long != null)
				return false;
		} else if (!name_long.equals(other.name_long))
			return false;
		if (name_short == null) {
			if (other.name_short != null)
				return false;
		} else if (!name_short.equals(other.name_short))
			return false;
		return true;
	}
   
	
}
