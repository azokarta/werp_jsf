package general.tables;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "order_status")
@javax.persistence.SequenceGenerator(name="seq_order_status_id",sequenceName="seq_order_status_id",allocationSize=1)
public class OrderStatus {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq_order_status_id")
    private Long os_id;
	
	public Long getOs_id() {
		return os_id;
	}

	public void setOs_id(Long os_id) {
		this.os_id = os_id;
	}

	@Column(name = "name_ru")
	private String name_ru;
	
	@Column(name = "name_en")
	private String name_en;
	
	@Column(name = "name_tr")
	private String name_tr;

	public String getName_ru() {
		return name_ru;
	}

	public void setName_ru(String name_ru) {
		this.name_ru = name_ru;
	}

	public String getName_en() {
		return name_en;
	}

	public void setName_en(String name_en) {
		this.name_en = name_en;
	}

	public String getName_tr() {
		return name_tr;
	}

	public void setName_tr(String name_tr) {
		this.name_tr = name_tr;
	}
	
	public String getName(String lang){
		if(lang.equals("en") && this.getName_en().length() > 0){
			return this.getName_en();
		}else if(lang.equals("tr") && this.getName_tr().length() > 0){
			return this.getName_tr();
		}
		
		return this.getName_ru();
	}
	
	
	
	/************NEW********************/
	
	public static final Integer STATUS_OUT_NEW = 10;
	public static final Integer STATUS_OUT_SENT = 20;
	public static final Integer STATUS_OUT_PROCESSED = 30;
	public static final Integer STATUS_IN_NEW = 40;
	public static final Integer STATUS_IN_PROCESSED = 50;
	
	@Transient 
	private Map<Integer, String> statuses;
	public static Map<Integer, String> getStatuses(){
		Map<Integer, String> out = new HashMap<Integer, String>();
		out.put(STATUS_OUT_NEW, "НОВЫЕ");
		out.put(STATUS_OUT_SENT, "ОТПРАВЛЕННЫЕ");
		out.put(STATUS_OUT_PROCESSED, "ОБРАБОТАННЫЕ");
		
		out.put(STATUS_IN_NEW, "НОВЫЕ (ВХОДЯЩИЕ)");
		out.put(STATUS_IN_PROCESSED, "ОБРАБОТАННЫЕ (ВХОДЯЩИЕ)");
		return out;
	}
	
	public static Map<Integer, String> getInStatuses(){
		Map<Integer, String> out = new HashMap<Integer, String>();
		out.put(STATUS_IN_NEW, "НОВЫЕ (ВХОДЯЩИЕ)");
		out.put(STATUS_IN_PROCESSED, "ОБРАБОТАННЫЕ (ВХОДЯЩИЕ)");
		return out;
	}
	
	public static Map<Integer, String> getOutStatuses(){
		Map<Integer, String> out = new HashMap<Integer, String>();
		out.put(STATUS_OUT_NEW, "НОВЫЕ");
		out.put(STATUS_OUT_SENT, "ОТПРАВЛЕННЫЕ");
		out.put(STATUS_OUT_PROCESSED, "ОБРАБОТАННЫЕ");
		return out;
	}
}
