package general.tables.search;

import java.text.SimpleDateFormat;
import java.util.Date;

import general.Validation;
import general.tables.ServiceApplication;

public class ServappSearch extends ServiceApplication {

	public String getCondition() {
		String condition = "";

		if (this.getApp_number() != null && this.getApp_number() > 0) {
			condition += " app_number = "
					+ this.getApp_number();
			
			this.p_start_date = null;
			this.p_end_date = null;
			this.setAdate(null);
			this.setApp_status(null);
			this.setApplicant_name(null);
			this.setCustomer_id(null);
			this.setBranch_id(null);
			this.setBukrs(null);
			this.setContract_number(null);
			this.setCreated_by(null);
			this.setIn_phone_num(null);
			
		} else {
			
			if (!Validation.isEmptyString(this.getBukrs())) {
				condition += " bukrs = '"
						+ this.getBukrs() + "'";
			}
			
			if (this.getBranch_id() != null && this.getBranch_id() > 0) {
				condition += " and branch_id = "
						+ this.getBranch_id();
			}
			
			if (this.getApp_type() != null && this.getApp_type() > 0) {
				condition += " and app_type = "
						+ this.getApp_type();
			}
					
			if (this.getApp_status() != null && this.getApp_status() > 0) {
				condition += " and app_status = "
						+ this.getApp_status();
			}
			
			if (!Validation.isEmptyString(this.getApplicant_name())) {
				condition += " and applicant_name like '%"
						+ this.getApplicant_name() + "%'";
			}
			
			if (this.getContract_number() != null && this.getContract_number() > 0) {
				condition += " and contract_number = "
						+ this.getContract_number();
			}
			
			if (this.getCreated_by() != null && this.getCreated_by() > 0) {
				condition += " and created_by = "
						+ this.getCreated_by();
			}
			
			if (!Validation.isEmptyString(this.getIn_phone_num()) && this.getIn_phone_num().length() > 0) {
				condition += " and in_phone_num like '%"
						+ this.getIn_phone_num() + "%'";
			}
			
			if (this.getCustomer_id() != null && this.getCustomer_id() > 0) {
				condition += " and customer_id = " + this.getCustomer_id(); 
			}
			
			// ****************** Date parameter to search
			SimpleDateFormat format1 = new SimpleDateFormat("YYYY-MM-dd");
			if (this.getAdate() != null) {
				condition += " and adate = '"
						+ format1.format(this.getAdate()) + "'";
			} else {
				if (p_start_date != null && p_end_date != null) {
					if (p_start_date.before(p_end_date)
							|| p_start_date.compareTo(p_end_date) == 0) {
						condition += " and adate between '"
								+ format1.format(p_start_date) + "' and '"
								+ format1.format(p_end_date) + "'";
					}
				} else if (p_start_date != null) {
					condition = condition + " and adate >= '"
							+ format1.format(p_start_date) + "'";
				} else if (p_end_date != null) {
					condition = condition + " and adate <= '"
							+ format1.format(p_end_date) + "'";
				}
			}
		}
		return condition;
	}

	public Date p_start_date;
	public Date p_end_date;
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
