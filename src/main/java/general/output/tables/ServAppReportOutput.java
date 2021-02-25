package general.output.tables;

import general.tables.Branch;
import general.tables.ServiceApplication;
import general.tables.Staff;

public class ServAppReportOutput {
	
	public ServAppReportOutput() {
		init();
	}
	
	public ServAppReportOutput(int i) {
		this.index = i;
		init();
	}
	
	private void init() {
		this.branch = new Branch();
		this.operator = new Staff();			
	}
	
	public int index;
	private ServiceApplication servapp;
	private String bukrs;
	private Branch branch; 
	private Staff operator; // 8
	public int zf;  	// 7 
	public int prof;	// 6
	public int rserv;	// 3
	public int cserv;	// 1
	public int others;	// 5
	public int complain;	// 4
	public int total;
	public boolean byMonth;

	public boolean isByMonth() {
		return byMonth;
	}

	public void setByMonth(boolean byMonth) {
		this.byMonth = byMonth;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getComplain() {
		return complain;
	}

	public void setComplain(int complain) {
		this.complain = complain;
	}

	public ServiceApplication getServapp() {
		return servapp;
	}

	public void setServapp(ServiceApplication servapp) {
		this.servapp = servapp;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public Staff getOperator() {
		return operator;
	}

	public void setOperator(Staff operator) {
		this.operator = operator;
	}

	public int getZf() {
		return zf;
	}

	public void setZf(int zf) {
		this.zf = zf;
	}

	public int getProf() {
		return prof;
	}

	public void setProf(int prof) {
		this.prof = prof;
	}

	public int getRserv() {
		return rserv;
	}

	public void setRserv(int rserv) {
		this.rserv = rserv;
	}

	public int getCserv() {
		return cserv;
	}

	public void setCserv(int cserv) {
		this.cserv = cserv;
	}

	public int getOthers() {
		return others;
	}

	public void setOthers(int others) {
		this.others = others;
	}
}
