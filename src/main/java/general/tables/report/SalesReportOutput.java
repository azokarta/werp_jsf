package general.tables.report;

import general.tables.Branch;
import general.tables.Contract;
import general.tables.Staff;

public class SalesReportOutput {
	
	public SalesReportOutput() {
		init();
	}
	
	public SalesReportOutput(int i) {
		this.index = i;
		init();
	}
	
	private void init() {
		this.region = new Branch();
		this.branch = new Branch();
		this.manager = new Staff();
		this.dealer = new Staff();
		this.demosec = new Staff();
		this.coordinator = new Staff();
		this.director = new Staff();
		this.nalichka = 0;
		this.rassrochka = 0;
		this.cancelled = 0;
		this.total = 0;
		this.contract = new Contract();
		this.pyramidId = 0L;
		this.parentPyramidId = 0L;
	}
	
	public int index;
	private String bukrs;
	private Branch region;
	private Branch branch; 
	public Contract contract;
	private Staff coordinator; // 3
	private Staff director; // 3
	private Staff manager; // 3
	private Staff dealer; // 4
	private Staff demosec; // 8
	public double nalichka;
	public double rassrochka;
	public double cancelled;
	public double total;
	public Long pyramidId;
	public Long parentPyramidId;
	
	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public Staff getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(Staff coordinator) {
		this.coordinator = coordinator;
	}

	public Staff getDirector() {
		return director;
	}

	public void setDirector(Staff director) {
		this.director = director;
	}

	public Long getPyramidId() {
		return pyramidId;
	}

	public void setPyramidId(Long pyramidId) {
		this.pyramidId = pyramidId;
	}

	public Long getParentPyramidId() {
		return parentPyramidId;
	}

	public void setParentPyramidId(Long parentPyramidId) {
		this.parentPyramidId = parentPyramidId;
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

	public Branch getRegion() {
		return region;
	}

	public void setRegion(Branch region) {
		this.region = region;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public Staff getManager() {
		return manager;
	}

	public void setManager(Staff manager) {
		this.manager = manager;
	}

	public Staff getDealer() {
		return dealer;
	}

	public void setDealer(Staff dealer) {
		this.dealer = dealer;
	}

	public Staff getDemosec() {
		return demosec;
	}

	public void setDemosec(Staff demosec) {
		this.demosec = demosec;
	}

	public double getNalichka() {
		return nalichka;
	}

	public void setNalichka(double nalichka) {
		this.nalichka = nalichka;
	}

	public double getRassrochka() {
		return rassrochka;
	}

	public void setRassrochka(double rassrochka) {
		this.rassrochka = rassrochka;
	}

	public double getCancelled() {
		return cancelled;
	}

	public void setCancelled(double cancelled) {
		this.cancelled = cancelled;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}
		
}
