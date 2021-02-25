package general.clone;

import java.util.ArrayList;
import java.util.List;

import dms.promotion.PromoTable;
import general.tables.Bkpf;
import general.tables.Bseg;
import general.tables.Contract;
import general.tables.Customer;
import general.tables.Promotion;
import general.tables.ServiceApplication;
import general.tables.Staff;

public class Clone {
	public static Bkpf cloneBkpf(Bkpf old_bkpf){
		if (old_bkpf!=null)
		{
			Bkpf new_bkpf = new Bkpf();
			new_bkpf.setBukrs(old_bkpf.getBukrs());
			new_bkpf.setBelnr(old_bkpf.getBelnr());
			new_bkpf.setGjahr(old_bkpf.getGjahr());
			new_bkpf.setBlart(old_bkpf.getBlart());
			new_bkpf.setBudat(old_bkpf.getBudat());
			new_bkpf.setBldat(old_bkpf.getBldat());
			new_bkpf.setMonat(old_bkpf.getMonat());
			new_bkpf.setBusiness_area_id(old_bkpf.getBusiness_area_id());
			new_bkpf.setCpudt(old_bkpf.getCpudt());
			new_bkpf.setUsnam(old_bkpf.getUsnam());
			new_bkpf.setTcode(old_bkpf.getTcode());
			new_bkpf.setStblg(old_bkpf.getStblg());
			new_bkpf.setStjah(old_bkpf.getStjah());
			new_bkpf.setBktxt(old_bkpf.getBktxt());
			new_bkpf.setWaers(old_bkpf.getWaers());
			new_bkpf.setKursf(old_bkpf.getKursf());
			new_bkpf.setBrnch(old_bkpf.getBrnch());
			new_bkpf.setAwtyp(old_bkpf.getAwtyp());
			new_bkpf.setAwkey(old_bkpf.getAwkey());
			new_bkpf.setAwkey2(old_bkpf.getAwkey2());
			new_bkpf.setContract_number(old_bkpf.getContract_number());
			new_bkpf.setCustomer_id(old_bkpf.getCustomer_id());
			new_bkpf.setStorno(old_bkpf.getStorno());
			new_bkpf.setDmbtr(old_bkpf.getDmbtr());
			new_bkpf.setDmbtr_paid(old_bkpf.getDmbtr_paid());
			new_bkpf.setWrbtr(old_bkpf.getWrbtr());
			new_bkpf.setWrbtr_paid(old_bkpf.getWrbtr_paid());
			new_bkpf.setPayroll_id(old_bkpf.getPayroll_id());
			new_bkpf.setDep(old_bkpf.getDep());
			new_bkpf.setLog_doc(old_bkpf.getLog_doc());
			return new_bkpf;
		}
		else
		{
			return null;
		}
		
	}
	public static List<Bseg> cloneLBseg(List<Bseg> al_bseg)
	{
		if (al_bseg.size()>0)
		{
			List<Bseg> l_bseg = new ArrayList<Bseg>();
			for(Bseg wa_bseg:al_bseg)
			{	
				Bseg wa_bseg2 = new Bseg();
				wa_bseg2.setBukrs(wa_bseg.getBukrs());
				wa_bseg2.setBelnr(wa_bseg.getBelnr());
				wa_bseg2.setGjahr(wa_bseg.getGjahr());
				wa_bseg2.setBuzei(wa_bseg.getBuzei());
				wa_bseg2.setBschl(wa_bseg.getBschl());
				wa_bseg2.setShkzg(wa_bseg.getShkzg());
				wa_bseg2.setDmbtr(wa_bseg.getDmbtr());
				wa_bseg2.setWrbtr(wa_bseg.getWrbtr());
				wa_bseg2.setSgtxt(wa_bseg.getSgtxt());
				wa_bseg2.setHkont(wa_bseg.getHkont());
				wa_bseg2.setLifnr(wa_bseg.getLifnr());
				wa_bseg2.setMatnr(wa_bseg.getMatnr());
				wa_bseg2.setWerks(wa_bseg.getWerks());
				wa_bseg2.setMenge(wa_bseg.getMenge());
				wa_bseg2.setMeins(wa_bseg.getMeins());
				wa_bseg2.setDep(wa_bseg.getDep());
				wa_bseg2.setBrnch(wa_bseg.getBrnch());
				wa_bseg2.setAwkey(wa_bseg.getAwkey());
				l_bseg.add(wa_bseg);
			}
			return l_bseg;
		}
		else
		{
			return null;
		}
	}
	
	public static Promotion clonePromotion(Promotion oldP)
	{
		if (oldP != null)
		{
			Promotion newP = new Promotion();
			
			newP.setId(oldP.getId());
			newP.setBonus(oldP.getBonus());
			newP.setBranch_id(oldP.getBranch_id());
			newP.setBukrs(oldP.getBukrs());
			newP.setCountry_id(oldP.getCountry_id());
			newP.setDate_end(oldP.getDate_end());
			newP.setDate_start(oldP.getDate_start());
			newP.setDiscount(oldP.getDiscount());
			newP.setFd_currency(oldP.getFd_currency());
			newP.setFrom_dealer(oldP.getFrom_dealer());
			newP.setInfo(oldP.getInfo());
			newP.setIs_active(oldP.getIs_active());
			newP.setMatnr(oldP.getMatnr());
			newP.setName(oldP.getName());
			newP.setPm_number(oldP.getPm_number());
			newP.setPm_scope(oldP.getPm_scope());
			newP.setPm_type(oldP.getPm_type());
			newP.setRegion_id(oldP.getRegion_id());
			newP.setContract_type(oldP.getContract_type());
			
			return newP;
		}
		else
		{
			return null;
		}
	}	
	
	public static ServiceApplication cloneServiceApplication(ServiceApplication oldSA)
	{
		if (oldSA != null)
		{
			ServiceApplication newSA = new ServiceApplication();
			
			newSA.setId(oldSA.getId());
			newSA.setBukrs(oldSA.getBukrs());
			newSA.setContract_number(oldSA.getContract_number());
			newSA.setCustomer_id(oldSA.getCustomer_id());
			newSA.setAdate(oldSA.getAdate());
			newSA.setApp_number(oldSA.getApp_number());
			newSA.setBranch_id(oldSA.getBranch_id());
			newSA.setInfo(oldSA.getInfo());
			newSA.setApp_type(oldSA.getApp_type());
			newSA.setCreated_by(oldSA.getCreated_by());
			newSA.setApp_status(oldSA.getApp_status());
			newSA.setUpdated_by(oldSA.getUpdated_by());
			newSA.setUpdated_date(oldSA.getUpdated_date());
			newSA.setApplicant_name(oldSA.getApplicant_name());
			newSA.setIn_phone_num(oldSA.getIn_phone_num());			
			
			return newSA;
		}
		else
		{
			return null;
		}
	}
	
	
	public static Customer cloneCustomer(Customer c_old) {
		Customer c_new = new Customer();
		
		c_new.setAddress(c_old.getAddress());
		c_new.setAddress_reg(c_old.getAddress_reg());
		c_new.setAddress_work(c_old.getAddress_work());
		
		c_new.setBirthday(c_old.getBirthday());
		c_new.setBuh(c_old.getBuh());
		
		c_new.setCountry_id(c_old.getCountry_id());
		c_new.setCreated_by(c_old.getCreated_by());
		c_new.setCreated_date(c_old.getCreated_date());
		
		c_new.setDirector(c_old.getDirector());
		c_new.setEmail(c_old.getEmail());
		
		c_new.setFirstname(c_old.getFirstname());
		c_new.setFiz_yur(c_old.getFiz_yur());
		
		c_new.setId(c_old.getId());
		c_new.setIin_bin(c_old.getIin_bin());
		
		c_new.setLastname(c_old.getLastname());
		
		c_new.setMiddlename(c_old.getMiddlename());
		c_new.setMobile(c_old.getMobile());
		c_new.setMobile2(c_old.getMobile2());
		
		c_new.setName(c_old.getName());
		
		c_new.setPassport_given_by(c_old.getPassport_given_by());
		c_new.setPassport_given_date(c_old.getPassport_given_date());
		c_new.setPassport_id(c_old.getPassport_id());
		
		c_new.setStaff_id(c_old.getStaff_id());
		c_new.setTelephone(c_old.getTelephone());
		c_new.setUpdated_by(c_old.getUpdated_by());
		c_new.setUpdated_date(c_old.getUpdated_date());
		
		return c_new;
	}
	
	public static Staff cloneStaff(Staff so) {
		Staff sn = new Staff();
		
		sn.setAccount(so.getAccount());
		sn.setAddress(so.getAddress());		
		sn.setBirthday(so.getBirthday());
		sn.setBranch_id(so.getBranch_id());		
		sn.setCreated_by(so.getCreated_by());
		sn.setCreated_date(so.getCreated_date());
		sn.setCustomer_id(so.getCustomer_id());		
		sn.setDepartment_id(so.getDepartment_id());
		sn.setEmail(so.getEmail());
		sn.setEmail2(so.getEmail2());		
		sn.setFirstname(so.getFirstname());
		sn.setHomephone(so.getHomephone());
		sn.setIin_bin(so.getIin_bin());
		sn.setLastname(so.getLastname());
		sn.setMiddlename(so.getMiddlename());
		sn.setMobile(so.getMobile());
		sn.setPassport_given_by(so.getPassport_given_by());
		sn.setPassport_given_date(so.getPassport_given_date());
		sn.setPassport_id(so.getPassport_id());
		sn.setPosition_id(so.getPosition_id());
		sn.setStaff_id(so.getStaff_id());
		sn.setUpdated_by(so.getUpdated_by());
		sn.setUpdated_date(so.getUpdated_date());
		sn.setWorkphone(so.getWorkphone());
		
		return sn;
	}
}
