package general.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean
@ViewScoped
public class MonthBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] monthsRu = new String[]{"Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"};
	private String[] monthsEn = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
	private String[] monthsTr = new String[]{"Ocak","Şubat","Mart","Nisan","Mayıs","Haziran","Temmuz","Ağustos","Eylül","Ekim","Kasım","Aralık"};
	
	private List<LocalMonth> items;

	public List<LocalMonth> getItems() {
		this.items = new ArrayList<LocalMonth>();
		String lang = userData.getU_language();
		for(int i = 0; i < 12; i++){
			LocalMonth temp = new LocalMonth();
			temp.setId(i+1);
			if(lang.equals("en")){
				temp.setValue(this.monthsEn[i]);
			}else if(lang.equals("tr")){
				temp.setValue(this.monthsTr[i]);
			}else{
				temp.setValue(this.monthsRu[i]);
			}
			this.items.add(temp);
		}
		return items;
	}
	
	public class LocalMonth{
		private int id;
		private String value;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		
	}
	
	public String getMonthByIndex(int index){
		if(userData.getU_language().equals("en")){
			return this.monthsEn[index-1];
		}else if(userData.getU_language().equals("tr")){
			return this.monthsTr[index-1];
		}else{
			return this.monthsRu[index-1];
		}
	}

	@ManagedProperty(value="#{userinfo}")
	private User userData;
	public void setUserData(User userData) {
		this.userData = userData;
	}
}
