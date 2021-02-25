package f4;
 

import general.tables.Month;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name = "monthF4Bean")
@ApplicationScoped
public class MonthF4 {
	
	List<Month> p_month_list = new ArrayList<Month>(); 
	@PostConstruct
	public void init(){
		p_month_list.add(new Month(1,"Январь","ru"));
		p_month_list.add(new Month(2,"Февраль","ru"));
		p_month_list.add(new Month(3,"Март","ru"));
		p_month_list.add(new Month(4,"Апрель","ru"));
		p_month_list.add(new Month(5,"Май","ru"));
		p_month_list.add(new Month(6,"Июнь","ru"));
		p_month_list.add(new Month(7,"Июль","ru"));
		p_month_list.add(new Month(8,"Август","ru"));
		p_month_list.add(new Month(9,"Сентябрь","ru"));
		p_month_list.add(new Month(10,"Октябрь","ru"));
		p_month_list.add(new Month(11,"Ноябрь","ru"));
		p_month_list.add(new Month(12,"Декабрь","ru"));
		
		p_month_list.add(new Month(1,"January","en"));
		p_month_list.add(new Month(2,"February","en"));
		p_month_list.add(new Month(3,"March","en"));
		p_month_list.add(new Month(4,"April","en"));
		p_month_list.add(new Month(5,"May","en"));
		p_month_list.add(new Month(6,"June","en"));
		p_month_list.add(new Month(7,"July","en"));
		p_month_list.add(new Month(8,"August","en"));
		p_month_list.add(new Month(9,"September","en"));
		p_month_list.add(new Month(10,"October","en"));
		p_month_list.add(new Month(11,"November","en"));
		p_month_list.add(new Month(12,"December","en"));
		
		p_month_list.add(new Month(1,"Ocak","tr"));
		p_month_list.add(new Month(2,"Şubat","tr"));
		p_month_list.add(new Month(3,"Mart","tr"));
		p_month_list.add(new Month(4,"Nisan","tr"));
		p_month_list.add(new Month(5,"Mayıs","tr"));
		p_month_list.add(new Month(6,"Haziran","tr"));
		p_month_list.add(new Month(7,"Temmuz","tr"));
		p_month_list.add(new Month(8,"Ağustos","tr"));
		p_month_list.add(new Month(9,"Eylül","tr"));
		p_month_list.add(new Month(10,"Ekim","tr"));
		p_month_list.add(new Month(11,"Kasım","tr"));
		p_month_list.add(new Month(12,"Aralık","tr"));
		
		
	}
	public List<Month> getP_month_list() {
		return p_month_list;
	}
}
