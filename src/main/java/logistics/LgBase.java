package logistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import f4.WerksF4;
import general.AppContext;
import general.Validation;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.StaffDao;
import general.dao.WerksBranchDao;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.Staff;
import general.tables.Werks;
import general.tables.WerksBranch;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import user.User;

@ManagedBean
public class LgBase {

	List<Werks> userWerksList = new ArrayList<Werks>();

	public List<Werks> getUserWerksList() {
		return userWerksList;
	}

	protected void prepareUserWerks() {
		if (userData.isSys_admin() || userData.isMain()) {
			userWerksList = werksF4Bean.getWerks_list();
		} else {
			WerksBranchDao d = (WerksBranchDao) appContext.getContext()
					.getBean("werksBranchDao");
			for (WerksBranch wb : d.findAll("branch_id = "
					+ userData.getBranch_id())) {
				Werks temp = werksF4Bean.getL_werks_map().get(wb.getWerks());
				if(temp != null){
					userWerksList.add(temp);
				}
			}
		}
		// System.out.println("UWL: " + userWerksList.size());
		prepareUserWerksIds();
	}

	private String[] userWerksIds;

	public String[] getUserWerksIds() {
		if (userWerksIds == null) {
			userWerksIds = new String[0];
		}
		return userWerksIds;
	}

	private String[] prepareUserWerksIds() {
		String[] out = new String[userWerksList.size()];
		for (int i = 0; i < userWerksList.size(); i++) {
			out[i] = userWerksList.get(i).getWerks().toString();
		}

		return out;
	}

	protected String getPreparedCondition() {
		String condition = "";
		return condition;
	}

	protected MatnrListDao getMatnrListDao() {
		return (MatnrListDao) appContext.getContext().getBean("matnrListDao");
	}

	protected MatnrDao getMatnrDao() {
		return (MatnrDao) appContext.getContext().getBean("matnrDao");
	}

	public void preparepMatnrList(List<MatnrList> mlList) {
		String[] ids = new String[mlList.size()];
		String[] staffIds = new String[mlList.size()];
		int i = 0;

		for (MatnrList ml : mlList) {
			ids[i] = ml.getMatnr().toString();
			if(!Validation.isEmptyLong(ml.getStaff_id())){
				staffIds[i] = ml.getStaff_id().toString();
			}
			i++;
		}

		if (ids.length == 0) {
			return;
		}
		StringBuffer staffCondition = new StringBuffer("");
		
		String condition = "";
		if (ids.length > 1000) {
			int chunkSize = (int) Math.ceil(ids.length / 1000D);
			String[] conditions = new String[chunkSize];
			int counter = 0;
			for (int k = 0; k < ids.length; k += 1000) {
				conditions[counter] = String.format(
						" matnr IN (%s) ",
						"'"
								+ String.join("','", Arrays.copyOfRange(ids, k,
										(k + 1000 < ids.length ? k + 1000
												: ids.length))) + "'");
				counter++;
			}
			condition = "( " + String.join(" OR ", conditions) + " ) ";
		} else {
			condition = String.format(" matnr IN(%s) ",
					"'" + String.join("','", ids) + "'");
			
			if(staffIds.length > 0){
				for(int k = 0; k < staffIds.length; k++){
					if(!Validation.isEmptyString(staffIds[k])){
						staffCondition.append("'" + staffIds[k] + "',");
					}
				}
			}
		}

		List<Matnr> mList = getMatnrDao().findAll(condition);
		for (Matnr m : mList) {
			// items.get(matnrIndexes.get(m.getMatnr())).setMatnrName(m.getText45());;
			for (MatnrList ml : mlList) {
				if (ml.getMatnr().equals(m.getMatnr())) {
					ml.setMatnrName(m.getText45());
					ml.setMatnrCode(m.getCode());
				}
			}
		}

		if (staffCondition.length() > 0) {
			staffCondition.setLength(staffCondition.length()-1);
			StaffDao sd = (StaffDao) appContext.getContext()
					.getBean("staffDao");

			List<Staff> staffList = sd.findAll("staff_id IN(" + staffCondition.toString() + ") ");
			for(Staff stf:staffList){
				for (MatnrList ml : mlList) {
					if(!Validation.isEmptyLong(ml.getStaff_id()) && ml.getStaff_id().equals(stf.getStaff_id())){
						ml.setStaffName(stf.getLF());
					}
				}
			}
		}
	}

	@ManagedProperty(value = "#{appContext}")
	protected AppContext appContext;
	@ManagedProperty(value = "#{userinfo}")
	protected User userData;

	@ManagedProperty(value = "#{werksF4Bean}")
	protected WerksF4 werksF4Bean;

	public AppContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}

	public User getUserData() {
		return userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	public WerksF4 getWerksF4Bean() {
		return werksF4Bean;
	}

	public void setWerksF4Bean(WerksF4 werksF4Bean) {
		this.werksF4Bean = werksF4Bean;
	}

}
