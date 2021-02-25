package general.tables.search;

import general.Validation;
import general.tables.Contract;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ContractSearch extends Contract{
	
	public String getCondition() {
		String condition="";
		String s = "SELECT c.* FROM Contract c ";
		boolean where = true;
		
		if (!Validation.isEmptyString(this.getAddress()) 
				|| !Validation.isEmptyString(this.getTel())) {
			s = "SELECT c.* FROM Contract c "
					+ "LEFT JOIN Address a ON c.addr_Home_Id = a.addr_id ";
			where = false;
			if (!Validation.isEmptyString(this.getAddress())) {
				condition = " a.address like '%"+this.address + "%'";
			}
			if (!Validation.isEmptyString(this.getTel())) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " (a.tel_Dom like '%"+this.tel + "%'"
						+ " or a.tel_Mob1 like '%"+this.tel + "%'"
						+ " or a.tel_Mob2 like '%"+this.tel + "%')";
			}		
		} else if (this.getContract_number() != null && this.getContract_number() > 0) {
			condition += " c.contract_number = " + this.getContract_number();
			this.setBukrs(null);
			this.setBranch_id(null);
			this.setDealer(null);
			this.setCollector(null);
			this.setDemo_sc(null);
			this.setCustomer_id(null);
			this.customer_fio = "";
			this.setFitter(null);
			this.setTovar_serial(null);
			p_start_date = null;
			p_end_date = null;
		} else if (!Validation.isEmptyString(this.getTovar_serial()) && (!this.getTovar_serial().equals("0"))) {
			condition += " c.tovar_serial = '" + this.getTovar_serial().trim() + "'"; 
			this.setBukrs(null);
			this.setBranch_id(null);
			this.setDealer(null);
			this.setCollector(null);
			this.setDemo_sc(null);
			this.setCustomer_id(null);
			this.customer_fio = "";
			this.setFitter(null);
			this.setContract_number(null);
			p_start_date = null;
			p_end_date = null;
		} else if (!Validation.isEmptyLong(this.getCustomer_id())) {
			condition += " c.customer_id = '" + this.getCustomer_id() + "'"; 
			this.setBukrs(null);
			this.setBranch_id(null);
			this.setDealer(null);
			this.setCollector(null);
			this.setDemo_sc(null);
			this.setTovar_serial("");
			this.customer_fio = "";
			this.setFitter(null);
			this.setContract_number(null);
			p_start_date = null;
			p_end_date = null;
		} else {
			
			if (!Validation.isEmptyString(this.getBukrs()) &&  !this.getBukrs().equals("0")) {
				condition += " c.bukrs = '"
						+ this.getBukrs() + "'";
			} 
			
			if (!Validation.isEmptyLong(this.getBranch_id())) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " c.branch_id = "
						+ this.getBranch_id();
			}
			
			if (!Validation.isEmptyLong(this.getOld_sn())) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " c.old_sn = "
						+ this.getOld_sn();
			}
			
			if (this.getServ_branch_id() != null && this.getServ_branch_id() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " c.serv_branch_id = "
						+ this.getServ_branch_id();	
			} 
			
			if (this.getContract_status_id()!= null && this.getContract_status_id() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " c.contract_status_id = "
							+ this.getContract_status_id();
			}
			
			if (this.getLast_state() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " c.last_state = "
							+ this.getLast_state();
			}

			if (this.getTovar_category() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " c.tovar_category = "
							+ this.getTovar_category();
			}

			if (this.getDealer() != null && this.getDealer() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " c.dealer = "
						+ this.getDealer();
			} 
			
			if (this.getCollector() != null && this.getCollector() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " c.collector = " + this.getCollector();
			}
			
			if (this.getCustomer_id() != null && this.getCustomer_id() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " c.customer_id = "
						+ this.getCustomer_id();
			} 
			
			if (this.getDemo_sc() != null && this.getDemo_sc() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " c.demo_sc = "
						+ this.getDemo_sc();
			}
			
			if (this.getFitter() != null && this.getFitter() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " c.fitter = "
						+ this.getFitter();
			}
			
			if (this.getMtConfirmed() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " c.mt_confirmed = "
						+ this.getMtConfirmed();
//				System.out.println("Confirmed = " + this.getMtConfirmed());
			}

			if (this.getMarkedType() != null 
					&& this.getMarkedType().intValue() > -1) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " c.marked_type = "
						+ this.getMarkedType();
			}			
			
			if (this.getNal() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				if (this.getNal() == 1)
					condition += " c.payment_schedule <= 1";
				else if (this.getNal() == 2)
					condition += " c.payment_schedule > 1";
			} 
			
			// ******************_Date_parameter_to_search_********************
			SimpleDateFormat format1 = new SimpleDateFormat("YYYY-MM-dd");
			if (this.getContract_date() != null) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " c.contract_date = '"
				  		+ format1.format(this.getContract_date()) + "'";
			} else {
				if (p_start_date != null && p_end_date != null) {
					if (p_start_date.before(p_end_date)
							|| p_start_date.compareTo(p_end_date) == 0) {
						condition += (condition.length() > 0 ? " and " : "");
						condition += " c.contract_date between '"
								+ format1.format(p_start_date) + "' and '"
								+ format1.format(p_end_date) + "'";
					}
				} else if (p_start_date != null) {
					condition += (condition.length() > 0 ? " and " : "");
					condition = condition
							+ " c.contract_date >= '"
							+ format1.format(p_start_date) + "'";
				} else if (p_end_date != null) {
					condition += (condition.length() > 0 ? " and " : "");
					condition = condition
							+ " c.contract_date <= '"
							+ format1.format(p_end_date) + "'";
				}
			}
		}
		if(condition.length() > 0){
			s += " WHERE ";
			s += condition;
		}
		
		System.out.println("Condition for Contract search: " + s);
		
		return s;
	}

	public String getConditionJPQL() {
		String condition="";
		String s = "SELECT c FROM Contract c ";
		boolean where = true;
		
		if (!Validation.isEmptyString(this.getAddress()) 
				|| !Validation.isEmptyString(this.getTel())) {
			s = "SELECT c FROM Address a, Contract c WHERE c.addrHomeId = a.addr_id = and ";
			where = false;
			if (!Validation.isEmptyString(this.getAddress())) {
				condition = " a.address like '%"+this.address + "%'";
			}
			condition += (condition.length() > 0 ? " and " : "");
			if (!Validation.isEmptyString(this.getTel())) {
				condition += " (a.telDom like '%"+this.tel + "%'"
						+ " or a.telMob1 like '%"+this.tel + "%'"
						+ " or a.telMob2 like '%"+this.tel + "%')";
			}		
		} else if (this.getContract_number() != null && this.getContract_number() > 0) {
			condition += " contract_number = " + this.getContract_number();
			this.setBukrs(null);
			this.setBranch_id(null);
			this.setDealer(null);
			this.setCollector(null);
			this.setDemo_sc(null);
			this.setCustomer_id(null);
			this.customer_fio = "";
			this.setFitter(null);
			this.setTovar_serial(null);
			p_start_date = null;
			p_end_date = null;
		} else if (!Validation.isEmptyString(this.getTovar_serial()) && (!this.getTovar_serial().equals("0"))) {
			condition += " tovar_serial = '" + this.getTovar_serial().trim() + "'"; 
			this.setBukrs(null);
			this.setBranch_id(null);
			this.setDealer(null);
			this.setCollector(null);
			this.setDemo_sc(null);
			this.setCustomer_id(null);
			this.customer_fio = "";
			this.setFitter(null);
			this.setContract_number(null);
			p_start_date = null;
			p_end_date = null;
		} else {
			
			if (!Validation.isEmptyString(this.getBukrs()) &&  !this.getBukrs().equals("0")) {
				condition += " bukrs = '"
						+ this.getBukrs() + "'";
			} 
			
			if (this.getBranch_id() != null && this.getBranch_id() != 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " branch_id = "
						+ this.getBranch_id();	
			} 
			
			if (!Validation.isEmptyLong(this.getOld_sn())) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " old_sn = "
						+ this.getOld_sn();	
			} 
			
			if (this.getServ_branch_id() != null && this.getServ_branch_id() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " serv_branch_id = "
						+ this.getServ_branch_id();	
			} 
			
			if (this.getContract_status_id()!= null && this.getContract_status_id() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " contract_status_id = "
							+ this.getContract_status_id();
			}
			
			if (this.getLast_state() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " last_state = "
							+ this.getLast_state();
			}

			if (this.getTovar_category() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " tovar_category = "
							+ this.getTovar_category();
			}

			if (this.getDealer() != null && this.getDealer() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " dealer = "
						+ this.getDealer();
			} 
			
			if (this.getCollector() != null && this.getCollector() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " collector = " + this.getCollector();
			}
			
			if (this.getCustomer_id() != null && this.getCustomer_id() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " customer_id = "
						+ this.getCustomer_id();
			} 
			
			if (this.getDemo_sc() != null && this.getDemo_sc() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " demo_sc = "
						+ this.getDemo_sc();
			}
			
			if (this.getFitter() != null && this.getFitter() > 0) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " fitter = "
						+ this.getFitter();
			} 

			
			// ****************** Date parameter to search
			SimpleDateFormat format1 = new SimpleDateFormat("YYYY-MM-dd");
			if (this.getContract_date() != null) {
				condition += (condition.length() > 0 ? " and " : "");
				condition += " contract_date = '"
				  		+ format1.format(this.getContract_date()) + "'";
			} else {
				if (p_start_date != null && p_end_date != null) {
					if (p_start_date.before(p_end_date)
							|| p_start_date.compareTo(p_end_date) == 0) {
						condition += (condition.length() > 0 ? " and " : "");
						condition += " contract_date between '"
								+ format1.format(p_start_date) + "' and '"
								+ format1.format(p_end_date) + "'";
					}
				} else if (p_start_date != null) {
					condition += (condition.length() > 0 ? " and " : "");
					condition = condition
							+ " contract_date >= '"
							+ format1.format(p_start_date) + "'";
				} else if (p_end_date != null) {
					condition += (condition.length() > 0 ? " and " : "");
					condition = condition
							+ " contract_date <= '"
							+ format1.format(p_end_date) + "'";
				}
			}
		}
		if(condition.length() > 0){
			if(where){
				s += " WHERE ";
			}
			s += condition;
		}
		return s;
	}
	
	public Date p_start_date;
	public Date p_end_date;
	
	public String address;
	public String tel;
	public int nal;
	
	public int getNal() {
		return nal;
	}

	public void setNal(int nal) {
		this.nal = nal;
	}

	public static final int NAL_ALL = 0; 
	public static final int NAL_NAL = 1;
	public static final int NAL_NOT = 2;
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getCustomCondition(String cond) {
		return this.getCondition() + cond;
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
	
	public String customer_fio;
	public String getCustomer_fio() {
		return customer_fio;
	}
	public void setCustomer_fio(String customer_fio) {
		this.customer_fio = customer_fio;
	}

}
