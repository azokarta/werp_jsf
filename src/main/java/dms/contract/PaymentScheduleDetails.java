package dms.contract;

import general.tables.PaymentSchedule;

public class PaymentScheduleDetails {
	
	public PaymentScheduleDetails() {
		// TODO Auto-generated constructor stub
		this.ps = new PaymentSchedule();
		this.ps.setPaid(0);
		this.ps.setSum(0);
		this.mon_dis = false;
		this.info = "";
	} 

	public PaymentScheduleDetails(int i) {
		// TODO Auto-generated constructor stub
		this.index = i;
		this.ps = new PaymentSchedule();
		this.ps.setPaid(0);
		this.ps.setSum(0);
		this.mon_dis = false;
		this.info = "";
	} 

	public PaymentScheduleDetails(int i, PaymentSchedule a_ps) {
		// TODO Auto-generated constructor stub
		this.index = i;
		this.ps = a_ps;
		this.ps.setPaid(0);
		this.ps.setSum(0);
		this.mon_dis = false;
		this.info = "";
	} 
	
	public int index;
	private PaymentSchedule ps;
	public boolean mon_dis;
	public String info;
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public PaymentSchedule getPs() {
		return ps;
	}

	public void setPs(PaymentSchedule ps) {
		this.ps = ps;
	}

	public boolean isMon_dis() {
		return mon_dis;
	}

	public void setMon_dis(boolean mon_dis) {
		this.mon_dis = mon_dis;
	}	
	
}
