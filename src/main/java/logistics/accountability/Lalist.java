package logistics.accountability;

import f4.WerksF4;
import general.AppContext;
import general.GeneralUtil;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.dao.MatnrListDao;
import general.dao.WerksDao;
import general.services.AccountabilityMatnrService;
import general.tables.Matnr;
import general.tables.MatnrList;
import general.tables.Staff;
import general.tables.Werks;
import general.tables.search.MatnrListSearch;
import general.tables.search.MatnrSearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import user.User;

@ManagedBean(name = "lalistBean")
@ViewScoped
public class Lalist implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7181859271115535565L;

	@PostConstruct
	public void init() {
		prepareWerks();
	}

	private MatnrListSearch searchModel = new MatnrListSearch();

	public MatnrListSearch getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(MatnrListSearch searchModel) {
		this.searchModel = searchModel;
	}

	private Map<Long, Integer> matnrTotalList = new LinkedHashMap<Long, Integer>();
	private List<MatnrList> items = new ArrayList<MatnrList>();

	public List<MatnrList> getItems() {
		return items;
	}

	private Map<String, Integer> grouppedItems = new HashMap<String, Integer>();

	public Map<String, Integer> getGrouppedItems() {
		return grouppedItems;
	}

	private void loadItems() {
		searchModel.setStatus(MatnrList.STATUS_ACCOUNTABILITY);
		//if (selectedStaff == null) {
			//searchModel.setStaff_id(313L);
		//}

		MatnrListDao d = (MatnrListDao) appContext.getContext().getBean(
				"matnrListDao");
		items = d.dynamicFind3(searchModel.getCondition());
		prepareItems();
	}

	private void prepareMatnrList(List<MatnrList> l) {
		String[] ids = new String[l.size()];
		int i = 0;

		for (MatnrList ml : l) {
			ids[i] = ml.getMatnr().toString();
			i++;
		}

		if (ids.length > 0) {
			MatnrDao d = (MatnrDao) appContext.getContext().getBean("matnrDao");
			List<Matnr> mList = d.findAll(String.format(" matnr IN(%s) ", "'"
					+ String.join("','", ids) + "'"));

			for (Matnr m : mList) {
				// items.get(matnrIndexes.get(m.getMatnr())).setMatnrName(m.getText45());;
				for (MatnrList ml : l) {
					if (ml.getMatnr().equals(m.getMatnr())) {
						ml.setMatnrName(m.getText45());
						ml.setMatnrCode(m.getCode());
					}
				}
			}
		}
	}

	private void prepareItems() {
		matnrTotalList.clear();
		prepareMatnrList(items);

		int temp = 0;
		for (MatnrList ml : items) {
			if (!matnrTotalList.containsKey(ml.getMatnr())) {
				matnrTotalList.put(ml.getMatnr(), 0);
			}
			temp = matnrTotalList.get(ml.getMatnr()) + 1;
			matnrTotalList.put(ml.getMatnr(), temp);
		}
	}

	private Werks selectedWerks;

	public Werks getSelectedWerks() {
		return selectedWerks;
	}

	public void setSelectedWerks(Werks selectedWerks) {
		this.selectedWerks = selectedWerks;
	}

	private Staff selectedStaff;

	public Staff getSelectedStaff() {
		return selectedStaff;
	}

	public void assignStaff(Staff stf) {
		selectedStaff = stf;
		if (selectedStaff != null) {
			searchModel.setStaff_id(stf.getStaff_id());
		}
	}

	public void removeStaff() {
		selectedStaff = null;
	}

	private Matnr selectedMatnr;

	public void assignMatnr(Matnr mtnr) {
		selectedMatnr = mtnr;
	}

	public void removeMatnr() {
		selectedMatnr = null;
	}

	public Matnr getSelectedMatnr() {
		return selectedMatnr;
	}

	public void Search() {
		items.clear();
		try {
			if (selectedStaff == null) {
				throw new DAOException("Выберите сотрудника");
			}
			loadItems();
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public void Create() {
		try {
			// MatnrL
			//throw new DAOException("TODO");
			if(selectedStaff == null){
				throw new DAOException("Выберите сотрудника");
			}
			AccountabilityMatnrService s = (AccountabilityMatnrService)appContext.getContext().getBean("accountabilityMatnrService");
			for(AccountabilityMatnrs m:accountMatnrsList){
				m.setStaffId(selectedStaff.getStaff_id());
			}
			//s.create(accountMatnrsList);
			accountMatnrsList.clear();
			loadMatnrList();
			GeneralUtil.addSuccessMessage("Материалы переданы успешно");
		} catch (DAOException e) {
			GeneralUtil.addErrorMessage(e.getMessage());
		}
	}

	public int getMatnrTotal(Long matnrId) {
		return matnrTotalList.containsKey(matnrId) ? matnrTotalList
				.get(matnrId) : 0;
	}

	List<Werks> werks = null;

	public List<Werks> getWerks() {
		return werks;
	}

	private void prepareWerks() {
		if (userData.isSys_admin()) {
			werks = werksF4Bean.getWerks_list();
		} else {
			werks = new ArrayList<Werks>();
			for (Werks w : werksF4Bean.getWerks_list()) {
				if (w.getBukrs().equals(userData.getBukrs())) {
					werks.add(w);
				}
			}
		}
	}

	List<AccountabilityMatnrs> accountMatnrsList = new ArrayList<Lalist.AccountabilityMatnrs>();

	public List<AccountabilityMatnrs> getAccountMatnrsList() {
		return accountMatnrsList;
	}

	public void addAccountMatnr() {
		//System.out.println("SIZE: " + selectedAccountMatnrs.size());
		for (MatnrList ml : selectedAccountMatnrs) {
			AccountabilityMatnrs m = new AccountabilityMatnrs();
			m.setMenge(ml.getMenge());
			m.setMatnr(ml.getMatnr());
			m.setMatnrName(ml.getMatnrName());
			m.setBarcode(ml.getBarcode());
			m.setError(null);
			m.setCreatedBy(userData.getUserid());
			m.setWerks(searchModel.getWerks());
			m.setMatnrCode(ml.getMatnrCode());
			//m.setStaffId(selectedStaff.getStaff_id());
			if(!accountMatnrsList.contains(m)){
				accountMatnrsList.add(m);
			}
		}
		//System.out.println("ACCOUNTSIE: " + accountMatnrsList.size());
		selectedAccountMatnrs.clear();
		//matnrList.clear();
	}

	public void removeAccountMatnr(AccountabilityMatnrs m) {
		accountMatnrsList.remove(m);
	}

	private List<MatnrList> matnrList = new ArrayList<MatnrList>();// Список для
																	// выбора из
																	// диалога

	public List<MatnrList> getMatnrList() {
		return matnrList;
	}

	public void loadMatnrList() {
		matnrList.clear();
		MatnrListDao d = (MatnrListDao) appContext.getContext().getBean(
				"matnrListDao");
		matnrList = d.dynamicFind(" werks = " + searchModel.getWerks() +" AND status = '" + MatnrList.STATUS_RECEIVED + "'");
		prepareMatnrList(matnrList);
	}

	public class AccountabilityMatnrs {
		double menge;
		Long matnr;
		String matnrName;
		String matnrCode;
		String barcode;
		Long createdBy;
		Long werks;
		String error;
		Long staffId;
		
		public Long getStaffId() {
			return staffId;
		}

		public void setStaffId(Long staffId) {
			this.staffId = staffId;
		}

		public Long getCreatedBy() {
			return createdBy;
		}

		public void setCreatedBy(Long createdBy) {
			this.createdBy = createdBy;
		}

		public Long getWerks() {
			return werks;
		}

		public void setWerks(Long werks) {
			this.werks = werks;
		}

		public String getError() {
			return error;
		}

		public void setError(String error) {
			this.error = error;
		}

		public double getMenge() {
			return menge;
		}

		public void setMenge(double menge) {
			this.menge = menge;
		}

		public Long getMatnr() {
			return matnr;
		}

		public void setMatnr(Long matnr) {
			this.matnr = matnr;
		}

		public String getMatnrName() {
			return matnrName;
		}

		public void setMatnrName(String matnrName) {
			this.matnrName = matnrName;
		}

		public String getBarcode() {
			return barcode;
		}

		public void setBarcode(String barcode) {
			this.barcode = barcode;
		}
		
		

		public String getMatnrCode() {
			return matnrCode;
		}

		public void setMatnrCode(String matnrCode) {
			this.matnrCode = matnrCode;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((barcode == null) ? 0 : barcode.hashCode());
			result = prime * result + ((matnr == null) ? 0 : matnr.hashCode());
			result = prime * result
					+ ((matnrName == null) ? 0 : matnrName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			/*if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;*/
			AccountabilityMatnrs other = (AccountabilityMatnrs) obj;
			/*if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (barcode == null) {
				if (other.barcode != null)
					return false;
			} else if (!barcode.equals(other.barcode))
				return false;*/
			if (matnr == null) {
				if (other.matnr != null)
					return false;
			} else if (!matnr.equals(other.matnr))
				return false;
			
			return true;
		}

		private Lalist getOuterType() {
			return Lalist.this;
		}

		
	}

	private List<MatnrList> selectedAccountMatnrs = new ArrayList<MatnrList>();
	public List<MatnrList> getSelectedAccountMatnrs() {
		return selectedAccountMatnrs;
	}
	public void setSelectedAccountMatnrs(List<MatnrList> selectedAccountMatnrs) {
		this.selectedAccountMatnrs = selectedAccountMatnrs;
	}

	/*********************/
	@ManagedProperty(value = "#{appContext}")
	private AppContext appContext;
	@ManagedProperty(value = "#{userinfo}")
	private User userData;
	@ManagedProperty(value = "#{werksF4Bean}")
	private WerksF4 werksF4Bean;

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
