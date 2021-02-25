package general.tables;

import general.Validation;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: InvoicePaymentTerm
 *
 */
@Entity
@Table(name="invoice_payment_term")
@javax.persistence.SequenceGenerator(name="seq_invoice_payment_term_id",sequenceName="seq_invoice_payment_term_id", allocationSize = 1)
public class InvoicePaymentTerm implements Serializable {

	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_invoice_payment_term_id")
	private Long ipt_id;
	private String name_ru;
	private String name_en;
	private String name_tr;
	private String code;
	private static final long serialVersionUID = 1L;

	public InvoicePaymentTerm() {
		super();
	}   
	public Long getIpt_id() {
		return this.ipt_id;
	}

	public void setIpt_id(Long ipt_id) {
		this.ipt_id = ipt_id;
	}   
	public String getName_ru() {
		return this.name_ru;
	}

	public void setName_ru(String name_ru) {
		this.name_ru = name_ru;
	}   
	public String getName_en() {
		return this.name_en;
	}

	public void setName_en(String name_en) {
		this.name_en = name_en;
	}   
	public String getName_tr() {
		return this.name_tr;
	}

	public void setName_tr(String name_tr) {
		this.name_tr = name_tr;
	}   
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}
   
	public String getName(String lang){
		if(lang.equals("en") && !Validation.isEmptyString(this.getName_en())){
			return this.getName_en();
		}else if(lang.equals("tr") && !Validation.isEmptyString(this.getName_tr())){
			return this.getName_tr();
		}
		return this.getName_ru();
	}
}
