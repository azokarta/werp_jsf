package general.tables.search;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import general.Validation;
import general.tables.ServiceTable;

public class ServiceSearch extends ServiceTable{
	
	public void clearRest() {
		this.p_start_date = null;
		this.p_end_date = null;
		this.setDate_open(null);
		this.setServ_status(null);
		this.setBukrs(null);
		this.setCustomer_id(null);
		this.setBranch_id(null);
		this.setBukrs(null);
		this.setContract_id(null);
		this.setServ_type(0);
		this.setServ_app_num(null);
		this.setServ_status(null);
		this.setTovar_id(null);
		this.setMaster_id(null);
		this.setCategory((byte)0);
		//this.setCreated_by(null);
	}
	
	public String getCondition() {
		String condition = "";
		
		if (this.getServ_num() != null && this.getServ_num() > 0) {
			condition += " serv_num = "
					+ this.getServ_num();
			this.setTovar_sn(null);
			clearRest();
			
		} else if (this.getTovar_sn() != null && this.getTovar_sn().length() > 0) {
			condition += " tovar_sn = '"
					+ this.getTovar_sn() + "'";
			this.setServ_num(null);
			clearRest();
			
		} else {
			
			if (!Validation.isEmptyString(this.getBukrs())) {
				condition += " bukrs = '"
						+ this.getBukrs() + "'";
			}
			
//			if (this.getBranch_id() != null && this.getBranch_id() != 0) {
//				condition += " and branch_id = "
//						+ this.getBranch_id();
//			}
//			
//			if (this.getSelBranches().size() > 0) {
				condition += (condition.length() > 0 ? " and" : "");
				condition += " branch_id in ("
						+ getSelBranchesString() + ")";
//			}
			
			if (this.getServ_type() > 0) {
				condition += " and serv_type = "
						+ this.getServ_type();
			}
					
			if (this.getServ_status() != null && this.getServ_status() > 0) {
				condition += " and serv_status = "
						+ this.getServ_status();
			}
			
			if (!Validation.isEmptyString(this.getCustomer_firstname())) {
				condition += " and customer_firstname like '%"
						+ this.getCustomer_firstname() + "%'";
			}
			
			if (!Validation.isEmptyString(this.getCustomer_lastname())) {
				condition += " and customer_lastname like '%"
						+ this.getCustomer_lastname() + "%'";
			}
			
			if (this.getContract_id() != null && this.getContract_id() > 0) {
				condition += " and contract_number = "
						+ this.getContract_id();
			}
			
			if (this.getTovar_id() != null && this.getTovar_id()  > 0) {
				condition += " and tovar_id = "
						+ this.getTovar_id();
			}
			
			if (this.getCustomer_id() != null && this.getCustomer_id() > 0) {
				condition += " and customer_id = " + this.getCustomer_id(); 
			}
			
			if (!Validation.isEmptyLong(this.getMaster_id())) {
				condition += " and master_id = " + this.getMaster_id(); 
			}

			if (!Validation.isEmptyLong(this.getOper_id())) {
				condition += " and oper_id = " + this.getOper_id(); 
			}
			
			if (this.getCategory() > 0) {
				condition += " and category = " + this.getCategory(); 
			}
			
			if (this.getCreated_by() != null && this.getCreated_by() > 0) {
				condition += condition.length() > 0 ? " and ": ""; 
				condition += " created_by = "
						+ this.getCreated_by();				
			} 
			
			// ****************** Date parameter to search
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			if (this.getDate_open() != null) {
				condition += " and date_open = '"
						+ format1.format(this.getDate_open()) + "'";
			} else {
				if (p_start_date != null && p_end_date != null) {
					if (p_start_date.before(p_end_date)
							|| p_start_date.compareTo(p_end_date) == 0) {
						condition += " and date_open between '"
								+ format1.format(p_start_date) + "' and '"
								+ format1.format(p_end_date) + "'";
					}
				} else if (p_start_date != null) {
					condition = condition + " and date_open >= '"
							+ format1.format(p_start_date) + "'";
				} else if (p_end_date != null) {
					condition = condition + " and date_open <= '"
							+ format1.format(p_end_date) + "'";
				}
			}
		}
		return condition;
	}

	public String getSelBranchesString() {
		String branches = "0";
		for (String s:selBranches) {
			branches += (branches.length() > 0 ? ", ": "");
			branches += s;
		}
		return branches;
	}
	
	public Date p_start_date;
	public Date p_end_date;
	List<String> selBranches = new ArrayList<String>();
	
	public List<String> getSelBranches() {
		return selBranches;
	}

	public void setSelBranches(List<String> selBranches) {
		this.selBranches = selBranches;
	}

	public Date getP_start_date() {
		return p_start_date;
	}
	public void setP_start_date(Date p_start_date) {
		this.p_start_date = p_start_date;
	}
	public Date getP_end_date() {
		return p_end_date;
	}
	public void setP_end_date(Date p_end_date) {
		this.p_end_date = p_end_date;
	}
}
