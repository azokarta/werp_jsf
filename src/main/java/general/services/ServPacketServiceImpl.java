package general.services;

import java.util.Calendar;
import java.util.List;

import general.Validation;
import general.dao.DAOException;
import general.dao.ServPacketDao;
import general.dao.ServPacketPosDao;
import general.dao.ServPacketWarDao;
import general.tables.ServConMatnrWar;
import general.tables.ServPacket;
import general.tables.ServPacketPos;
import general.tables.ServPacketWar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.User;

@Service("servPacketService")
public class ServPacketServiceImpl implements ServPacketService {

	@Autowired
	ServPacketDao spDao;
	
	@Autowired
	ServPacketPosDao spPosDao;
	
	@Autowired
	ServPacketWarDao spWarDao;
	
	@Override
	public boolean createServPacket(ServPacket a_sp, List<ServPacketPos> a_spPosL, List<ServPacketWar> a_spWarL, User userData, String a_trCode, Long a_trId) throws DAOException {
		try {
			if (validateSP(a_sp) && validateSpPos(a_spPosL) && validateSpWar(a_spWarL)) {
			
				spDao.create(a_sp);
				
				for (ServPacketPos spPos:a_spPosL) {
					spPos.setSp_id(a_sp.getId());
					spPosDao.create(spPos);
				}
				
				for (ServPacketWar spWar:a_spWarL) {
					spWar.setSp_id(a_sp.getId());
					spWarDao.create(spWar);
				}
				
				return true;
			}		
			else 
				return false;
			
		} catch(Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	@Override
	public boolean updateServPacket(ServPacket a_sp, List<ServPacketPos> a_spPosL, List<ServPacketWar> spWarL, User userData, String a_trCode, Long a_trId) throws DAOException {
		try {
			
			
			
			return true;
		} catch(Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	public boolean validateSP(ServPacket a_sp) throws Exception {
		try {			
			if (Validation.isEmptyString(a_sp.getBukrs()))
				throw new Exception("Please select Company! Bukrs is empty!");
			
			if (Validation.isEmptyLong(a_sp.getCountry_id()))
				throw new Exception("Please select Country! Country is empty!");
			
			if (Validation.isEmptyString(a_sp.getName()))
				throw new Exception("Please enter Name for Service Packet! Name is empty!");
			
			if (Validation.isEmptyString(a_sp.getInfo()))
				throw new Exception("Please write short explanation for Service Packet! Info field is empty!");
			
			if (a_sp.getDiscount() < 0)
				throw new Exception("Discount amount is wrong! Discount amount cannot be a negative number!");
			
			if (a_sp.getMaster_bonus() < 0)
				throw new Exception("Master Bonus amount is wrong! It cannot be a negative number!");
			
			if (a_sp.getOper_bonus() < 0)
				throw new Exception("Operator Bonus amount is wrong! It cannot be a negative number!");
			
			if (a_sp.getTovar_category() <= 0)
				throw new Exception("Please select Appliance Category! Appliance Category is empty!");
			
			if (Validation.isEmptyLong(a_sp.getTovar_id()))
				throw new Exception("Please select Device! Device is empty!");
			
			Calendar curDate = Calendar.getInstance();
			Calendar spDate = Calendar.getInstance();
			spDate.setTime(a_sp.getStart_date());
			Calendar firstDay = Calendar.getInstance();
			Calendar lastDay = Calendar.getInstance();
			firstDay.set(curDate.get(Calendar.YEAR),
					curDate.get(Calendar.MONTH), 1);
			if (curDate.getTime().getDate() < 4) {
				System.out.println("CurDate: " + curDate.getTime().getDate());
				firstDay.add(Calendar.MONTH, -1);
			}
			lastDay.set(Calendar.DAY_OF_MONTH,
					lastDay.getActualMaximum(Calendar.DAY_OF_MONTH));
			
			if (spDate.getTime().before(firstDay.getTime())
					&& spDate.getTime().after(lastDay.getTime())) 
				throw new Exception("Start date must be in the current month.");
			
//			if (a_sp.getPrice() <= 0)
//				throw new Exception("Price is less or equal to zero! Please enter correct discount or price amount!");
			
			if (a_sp.getPrice() < 0)
				throw new Exception("Price is less than zero! Please enter correct discount or price amount!");
			
			return true;
		} catch(Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}
	
	public boolean validateSpPos(List<ServPacketPos> a_spPosL) throws Exception {
		try {
			if (a_spPosL.size() == 0)
				throw new Exception("ServPacketPos list is empty!");
						
			for (ServPacketPos spPos:a_spPosL) {
				if (Validation.isEmptyString(spPos.getInfo()))
					throw new Exception("Info is empty!");
				
				if (Validation.isEmptyString(spPos.getWaers()))
					throw new Exception("Currency is empty!");
				
				if (Validation.isEmptyLong(spPos.getOperation()))
					throw new Exception("Operation type is empty!");
				
				if (spPos.getOperation().intValue() == 1 && Validation.isEmptyLong(spPos.getMatnr_id()))
					throw new Exception("Matnr is empty!");
				
				if (spPos.getPrice() < 0)
					throw new Exception("Price cannot be less than 0!");
				
				if (Validation.isEmptyLong(spPos.getQuantity()))
					throw new Exception("Quantity cannot be less or equal to 0!");
				
				if (spPos.getNew_war_inm() < 0)
					throw new Exception("Warranty month number cannot be negative number!");				
			}	
			return true;
		} catch(Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}
	
	public boolean validateSpWar(List<ServPacketWar> a_spWarL) throws Exception {
		try {
			if (a_spWarL.size() == 0)
				return true;
						
			for (ServPacketWar spWar:a_spWarL) {
				if (Validation.isEmptyString(spWar.getInfo()))
					throw new Exception("Warranty Info is empty!");
				
				if (Validation.isEmptyLong(spWar.getMatnr_id()))
					throw new Exception("Warranty Matnr is empty!");
				
				if (spWar.getWar_months() <= 0)
					throw new Exception("Warranty! Month number cannot be less or equal to 0!");				
			}	
			return true;
		} catch(Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}
	
	public boolean paymentStateUpdate(Long servId, double sum) {
			
		
		
		return true;
	}
}
