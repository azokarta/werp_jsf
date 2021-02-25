package general.services;

import general.GeneralUtil;
import general.Validation;
import general.dao.AddressDao;
import general.dao.CityDao;
import general.dao.CityregDao;
import general.dao.DAOException;
import general.tables.Address;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("addressService")
public class AddressServiceImpl implements AddressService {
	@Autowired
	private AddressDao addrDao;
	
	@Autowired
	private CityDao cityDao;

	@Autowired
	private CityregDao regDao;

	@Override
	public boolean createAddress(Address addr, Long a_userid, String a_trcode)
			throws DAOException {
		try {
			validateAddress(addr);
			addr.setAddress(getFullAddressAsString(addr));
			
			addrDao.create(addr);
			
			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public boolean updateAddress(Address addr, Long a_userid, String a_trcode)
			throws DAOException {
		try {
			validateAddress(addr);
			addr.setAddress(getFullAddressAsString(addr));
			
			addrDao.update(addr);
			
			return true;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	public boolean validateAddress(Address addr) throws Exception {

		if (Validation.isEmptyLong(addr.getCustomerId())) 
			throw new DAOException("Customer ID is empty!");
		
		if (addr.getAddrType() <= 0) 
			throw new DAOException("Please select AddressType!");
		
		if (Validation.isEmptyLong(addr.getCountryId())) 
			throw new DAOException("Please select country");

		if (Validation.isEmptyLong(addr.getStateId())) 
			throw new DAOException("Please select state");

		if (Validation.isEmptyLong(addr.getCityId())) 
			throw new DAOException("Please select city");

		if (Validation.isEmptyString(addr.getStreet())
				&& Validation.isEmptyString(addr.getAvenue())
				&& Validation.isEmptyString(addr.getMicrodistrict())
				&& Validation.isEmptyString(addr.getVillage())) 
			throw new DAOException(
					"Please enter at least one of the Street/Avenue/Microdistrict/Village name!");

		if (Validation.isEmptyString(addr.getAp_number())) 
			throw new DAOException("Please enter the house/apartment number");

		if (Validation.isEmptyString(addr.getTelMob1())) 
			throw new DAOException("Please enter Mobile number");

		return true;
	}

	public boolean isAlphaNumeric(String s) {
		String pattern = "^[a-zA-Z0-9]*$";
		if (s.matches(pattern)) {
			return true;
		}
		return false;
	}
	
	public String getFullAddressAsString(Address addr) {
		String address = "";
		if (addr != null) {
			/*
			 * addr = p_countryF4Bean.getL_country_map()
			 * .get(a_con.getAddr_dom_countryid()).getCountry();
			 * 
			 * if (a_con.getAddr_dom_stateid() != null &
			 * a_con.getAddr_dom_stateid() > 0L) { addr =
			 * p_stateF4Bean.getL_state_map() .get(a_con.getAddr_dom_stateid())
			 * .getStatename() + ", "; }
			 */

			address = address
					+ cityDao.find(addr.getCityId()).getName();

			if (!Validation.isEmptyLong(addr.getRegId())) 
				address += ", "
						+ regDao.find(addr.getRegId()).getRegname();

			if (!Validation.isEmptyString(addr.getMicrodistrict()))
				address = address + ", md." + addr.getMicrodistrict();

			if (!Validation.isEmptyString(addr.getVillage()))
				address = address + ", vil." + addr.getVillage();

			if (!Validation.isEmptyString(addr.getAvenue()))
				address = address + ", av." + addr.getAvenue();
			
			if (!Validation.isEmptyString(addr.getStreet()))
				address = address + ", str." + addr.getStreet();

			if (!Validation.isEmptyString(addr.getAp_number()))
				address = address + " " + addr.getAp_number();
			
			if (!Validation.isEmptyString(addr.getAp_drob())) 
				address = address + " / " + addr.getAp_drob();
			

			if (!Validation.isEmptyLong(addr.getFlat_number())) 
				address = address + ", " + addr.getFlat_number();

			System.out.println("Address: " + address);
		} else
			GeneralUtil.addMessage("Null", "Address is empty!");
		return address;
	}

}
