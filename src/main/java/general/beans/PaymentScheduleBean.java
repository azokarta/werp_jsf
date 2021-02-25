package general.beans;

import general.AppContext;
import general.GeneralUtil;
import general.Helper;
import general.MessageController;
import general.dao.DAOException;
import general.dao.PaymentScheduleDao;
import general.services.PaymentScheduleService;
import general.tables.Bkpf;
import general.tables.PaymentSchedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

@ManagedBean(name = "paymentScheduleBean")
@ViewScoped
public class PaymentScheduleBean implements Serializable {

	private int psCounter = 0;

	public int getpsCounter() {
		return psCounter;
	}

	private Bkpf bkpf = new Bkpf();

	public void setBkpf(Bkpf bkpf) {
		this.bkpf = bkpf;
	}

	public Bkpf getBkpf() {
		return bkpf;
	}

	private static final long serialVersionUID = 2570247896948873007L;
	private PaymentSchedule schedule = new PaymentSchedule();
	private List<PaymentSchedule> psList = new ArrayList<PaymentSchedule>();

	public List<PaymentSchedule> getPsList() {
		return psList;
	}

	public PaymentSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(PaymentSchedule schedule) {
		this.schedule = schedule;
	}

	private boolean showList = true;
	private String errorMessage = "";

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isShowList() {
		return showList;
	}

	public void setShowList(boolean showList) {
		this.showList = showList;
	}

	public void loadPsList(Bkpf bkpf, int eventType) {
		this.bkpf = bkpf;
		PaymentScheduleDao d = (PaymentScheduleDao) appContext.getContext()
				.getBean("paymentScheduleDao");
		this.psList = d.findAll(String.format(" awkey = %d",this.getBkpfAwkey()),bkpf.getBukrs());
		if (eventType == 1) { // Добавление
			if (this.psList.size() > 0) {
				this.setShowList(false);
				this.setErrorMessage("В документе уже имеется график");
			} else {
				this.setShowList(true);
				this.preparePsList(12);
			}
		} else if (eventType == 2) {// Редактирование
			if (this.psList.size() == 0) {
				this.setShowList(false);
				this.setErrorMessage("В документе еще нет график");
			} else {
				this.setShowList(true);
			}
		} else if (eventType == 3) { // Просмотр

		}
		
		if(this.bkpf.getWaers() == "USD" && this.bkpf.getDmbtr() > 0L){
			this.setShowSum(true);
		}else if(this.bkpf.getWrbtr() > 0L){
			this.setShowSum(true);
		}else{
			this.setShowSum(false);
		}
	}

	public void preparePsList(int k) {
		this.psCounter = k;
		psList = new ArrayList<PaymentSchedule>();
		Double average;
		if(this.bkpf.getWaers().equals("USD")){
			average = Helper.getFormattedNumber(this.bkpf.getDmbtr() / k);
		}else{
			average = Helper.getFormattedNumber(this.bkpf.getWrbtr() / k);
		}

		Calendar cal = Calendar.getInstance();
		for (int i = 0; i < k; i++) {
			PaymentSchedule ps = new PaymentSchedule();
			ps.setAwkey(this.getBkpfAwkey());
			ps.setBukrs(bkpf.getBukrs());
			if (i > 0) {
				cal.add(Calendar.MONTH, 1);
			}
			ps.setPayment_date(cal.getTime());
			if ((i + 1) == k) {
				average += Helper.getFormattedNumber((this.bkpf.getWaers().equals("USD") ? this.bkpf.getDmbtr() : this.bkpf.getWrbtr())-(average*k));
			}
			ps.setSum(average);
			psList.add(ps);

		}
	}

	// public void preparepsList(PaymentSchedule ps){
	// List<PaymentSchedule> tempL = new ArrayList<PaymentSchedule>();
	// boolean b = false;
	// for(PaymentSchedule p:this.psList){
	// if(b){
	// tempL.add(p);
	// }
	// if(p == ps){
	// b = true;
	// }
	// }
	//
	// this.psList =
	// }

	private boolean showSum = false;
	
	public boolean isShowSum() {
		return showSum;
	}

	public void setShowSum(boolean showSum) {
		this.showSum = showSum;
	}

	public void addPsRow() {
		PaymentSchedule ps = new PaymentSchedule();
		ps.setAwkey(Long.valueOf(bkpf.getBelnr() + bkpf.getGjahr()));
		ps.setBukrs(bkpf.getBukrs());
		Calendar c = Calendar.getInstance();
		for (PaymentSchedule p : this.psList) {
			c.setTime(p.getPayment_date());
			c.add(Calendar.MONTH, 1);
			ps.setPayment_date(c.getTime());
		}
		this.psList.add(ps);
	}

	public void removePsRow(PaymentSchedule ps) {
		List<PaymentSchedule> tempList = new ArrayList<PaymentSchedule>();
		for (PaymentSchedule p : this.psList) {
			if (ps != p) {
				tempList.add(p);
			}
		}
		this.psList = new ArrayList<PaymentSchedule>();
		this.psList = tempList;
		// this.psCounter = this.psList.size();
	}

	private Long getBkpfAwkey() {
		String s = this.bkpf.getBelnr() + "" + this.bkpf.getGjahr();
		return Long.valueOf(s);
	}

	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	public void save() {
		try {
			PaymentScheduleService pService = (PaymentScheduleService) appContext
					.getContext().getBean("paymentScheduleService");
			pService.create(this.psList);
			GeneralUtil.addSuccessMessage("Schedule Added Successfully!");
			GeneralUtil.hideDialog("scheduleList");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void reset() {

	}

	public void checkSum() {

	}
}
