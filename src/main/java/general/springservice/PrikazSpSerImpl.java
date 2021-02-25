package general.springservice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dit.message.UserStaff;
import general.dao.DAOException;
import general.dao2.AgreementDao;
import general.dao2.MessageGroupDao;
import general.dao2.PrikazAttachDao;
import general.dao2.PrikazDao;
import general.tables2.Agreement;
import general.tables2.MessageHeader;
import general.tables2.Prikaz;
import general.tables2.PrikazAttach;

@Service("prikazService")
public class PrikazSpSerImpl implements PrikazSpSer{
	@Autowired
	PrikazDao prikazDao;
	
	@Autowired
	PrikazAttachDao prikazAttachDao;
	
	@Autowired
	AgreementDao agreementDao;
	
	
	@Autowired
	MessageSpSer messService;
	
	public void createPrikaz(Prikaz a_pz,List<PrikazAttach> al_pa, List<Agreement> al_a) throws DAOException{
		try
		{	
//			
			Prikaz p_pz = new Prikaz();
			List<PrikazAttach> pl_pa = new ArrayList<>();
			List<Agreement> pl_a = new ArrayList<>();
			BeanUtils.copyProperties(a_pz, p_pz);
			for(PrikazAttach wa_pa:al_pa)
			{
				PrikazAttach temp_pa = new PrikazAttach();
				BeanUtils.copyProperties(wa_pa, temp_pa);
				pl_pa.add(temp_pa);
			}
			if (al_a==null || al_a.size()==0)
			{
				throw new DAOException("Выберите сотрудника для одобрения");
			}
			for(Agreement wa_a:al_a)
			{
				Agreement temp_a = new Agreement();
				BeanUtils.copyProperties(wa_a, temp_a);
				pl_a.add(wa_a);
			}
			
			
			
			if (!(p_pz.getBukrs()!=null && p_pz.getBukrs().length()>0))
			{
				throw new DAOException("Выберите компанию");
			}
			
			if (!(p_pz.getBranch_id()!=null && p_pz.getBranch_id()>=0L))
			{
				throw new DAOException("Выберите филиал");
			}
			
			if (!(p_pz.getDepartment_id()!=0))
			{
				throw new DAOException("Выберите отдел");
			}
			
			if (!(p_pz.getType_prikaz()!=0))
			{
				throw new DAOException("Выберите тип");
			}
			
			if (!(p_pz.getName_prikaz()!=null && p_pz.getName_prikaz().length()>0))
			{
				throw new DAOException("Заполните название");
			}
			
			prikazDao.create(p_pz);
			//System.out.println(a_pz.getId_prikaz());
			for(PrikazAttach wa_pa:pl_pa)
			{
				wa_pa.setId_prikaz(p_pz.getId_prikaz());
				prikazAttachDao.create(wa_pa);
			}
			
			for(Agreement wa_a:pl_a)
			{
				wa_a.setContext_id(p_pz.getId_prikaz());
				wa_a.setContext_a("PRIKAZ");
				if (wa_a.getUser_id()==null)
				{
					throw new DAOException("Выберите пользователя на согласование");
				}
				agreementDao.create(wa_a);
			}
			
			
		}	
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		} 
	}
	public void modifyPrikaz(Prikaz a_pz,List<PrikazAttach> al_pa, List<Agreement> al_a) throws DAOException{
		try
		{	
//			
			
			
			Long old_id = a_pz.getId_prikaz();
			Prikaz p_pz = new Prikaz();
			List<PrikazAttach> pl_pa = new ArrayList<>();
			List<Agreement> pl_a = new ArrayList<>();
			BeanUtils.copyProperties(a_pz, p_pz);
			for(PrikazAttach wa_pa:al_pa)
			{
				PrikazAttach temp_pa = new PrikazAttach();
				BeanUtils.copyProperties(wa_pa, temp_pa);
				pl_pa.add(temp_pa);
			}
			for(Agreement wa_a:al_a)
			{
				Agreement temp_a = new Agreement();
				BeanUtils.copyProperties(wa_a, temp_a);
				pl_a.add(wa_a);
			}
			
		
			
			if (!(p_pz.getBukrs()!=null && p_pz.getBukrs().length()>0))
			{
				throw new DAOException("Выберите компанию");
			}
			
			if (!(p_pz.getBranch_id()!=null && p_pz.getBranch_id()>=0L))
			{
				throw new DAOException("Выберите филиал");
			}
			
			if (!(p_pz.getType_prikaz()!=0))
			{
				throw new DAOException("Выберите тип");
			}
			
			if (!(p_pz.getName_prikaz()!=null && p_pz.getName_prikaz().length()>0))
			{
				throw new DAOException("Заполните название");
			}
			
			
			if (p_pz.getStatus_id()==2)
			{
				p_pz.setId_prikaz(null);
				p_pz.setStatus_id(0);
				prikazDao.create(p_pz);
				for(PrikazAttach wa_pa:pl_pa)
				{
					wa_pa.setId_prikaz(null);
					wa_pa.setPrikaz_attach_id(null);
					wa_pa.setId_prikaz(p_pz.getId_prikaz());
					prikazAttachDao.create(wa_pa);
				}
				int current = 1;
				for(Agreement wa_a:pl_a)
				{
					wa_a.setAgree_id(null);
					wa_a.setContext_id(null);
					wa_a.setContext_id(p_pz.getId_prikaz());
					wa_a.setContext_a("PRIKAZ");
					wa_a.setStatus_id(Agreement.waiting_status);
					wa_a.setNote("");
					wa_a.setCurrent_a(current);
					if (current==1)
					{
						current--;
					}
					
					if (wa_a.getUser_id()==null)
					{
						throw new DAOException("Выберите пользователя на согласование");
					}
					agreementDao.create(wa_a);
				}
				
				List<Prikaz> l_old_pz = new ArrayList<Prikaz>();
				l_old_pz = prikazDao.findAll("p.id_prikaz="+old_id+" or p.parent_id_prikaz="+old_id);
				if (l_old_pz!=null && l_old_pz.size()>0)				
				{
					for(Prikaz wa_prikaz:l_old_pz)
					{
						if (wa_prikaz!=null && wa_prikaz.getId_prikaz()>0L)
						{
							//wa_prikaz.setStatus_id(4);
							wa_prikaz.setParent_id_prikaz(p_pz.getId_prikaz());
							prikazDao.update(wa_prikaz);
						}
					}
					
				}
			}
			else if (p_pz.getStatus_id()==0)
			{
				prikazDao.update(p_pz);
				prikazAttachDao.deleteByIdPrikaz(p_pz.getId_prikaz());
				agreementDao.deleteByIdPrikaz(p_pz.getId_prikaz());
				
				for(PrikazAttach wa_pa:pl_pa)
				{
					wa_pa.setPrikaz_attach_id(null);
					wa_pa.setId_prikaz(p_pz.getId_prikaz());
					prikazAttachDao.create(wa_pa);
				}
				
				int current = 1;
				for(Agreement wa_a:pl_a)
				{
					wa_a.setAgree_id(null);
					wa_a.setContext_id(null);
					wa_a.setContext_id(p_pz.getId_prikaz());
					wa_a.setContext_a("PRIKAZ");
					wa_a.setStatus_id(Agreement.waiting_status);
					wa_a.setNote("");
					wa_a.setCurrent_a(current);
					if (current==1)
					{
						current--;
					}
					
					if (wa_a.getUser_id()==null)
					{
						throw new DAOException("Выберите пользователя на согласование");
					}
					agreementDao.create(wa_a);
				}
				
				
			}
			
			
		}	
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		} 
	}
	public void sendPrikaz(Prikaz a_pz,Agreement a_a) throws DAOException{
		try
		{	
			a_pz.setStatus_id(Prikaz.sent_status);
			prikazDao.update(a_pz);
			
			UserStaff wa_us = new UserStaff();
			wa_us.setUser_id(a_a.getUser_id());
			List<UserStaff> l_us = new ArrayList<UserStaff>();
			l_us.add(wa_us);
			if (l_us!=null && l_us.size()>0)
			{
				
				Calendar curDate = Calendar.getInstance();
				MessageHeader wa_mh = new MessageHeader();
				wa_mh.setMess_date(curDate.getTime());
				wa_mh.setMess_from(a_pz.getCreator_id());
				wa_mh.setMess_head_text("Прошу одобрить приказ");
				wa_mh.setMess_text("Документ на согласование. Для просмотра пройдите по ссылке: <a target='_blank' href='/werp/documents/prkz.xhtml?id="+a_pz.getId_prikaz()+"'>Одобрить приказ<a/>");
				messService.sendMessage(l_us, wa_mh, null, a_pz.getCreator_id());
			}
			
		}	
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		} 
	}
	
	public void confirmPrikaz( List<Agreement> al_a,Agreement a_a,Prikaz a_prikaz)throws DAOException{
		try
		{	
			Map<Integer,Agreement> a_map = new HashMap<Integer,Agreement>();
			
			for (Agreement wa_a:al_a)
			{
				a_map.put(wa_a.getStep(), wa_a);
			}
			Agreement temp_a = a_map.get(a_a.getStep()+1);
			//flag to next person
			if (temp_a!=null && a_a.getStatus_id()==2)
			{
				a_a.setCurrent_a(0);
				agreementDao.update(a_a);
				temp_a.setCurrent_a(1);
				agreementDao.update(temp_a);
				UserStaff wa_us = new UserStaff();
				wa_us.setUser_id(temp_a.getUser_id());
				List<UserStaff> l_us = new ArrayList<UserStaff>();
				l_us.add(wa_us);
				if (l_us!=null && l_us.size()>0)
				{
					
					Calendar curDate = Calendar.getInstance();
					MessageHeader wa_mh = new MessageHeader();
					wa_mh.setMess_date(curDate.getTime());
					wa_mh.setMess_from(a_prikaz.getCreator_id());
					wa_mh.setMess_head_text("Прошу одобрить приказ");
					wa_mh.setMess_text("Документ на согласование. Для просмотра пройдите по ссылке: <a target='_blank' href='/werp/documents/prkz.xhtml?id="+a_prikaz.getId_prikaz()+"'>Одобрить приказ<a/>");
					messService.sendMessage(l_us, wa_mh, null, a_prikaz.getCreator_id());
				}
				
				
			}
			//confirm
			else if (temp_a==null && a_a.getStatus_id()==2)
			{
				a_prikaz.setStatus_id(Prikaz.confirmed_status);
				prikazDao.update(a_prikaz);
				agreementDao.update(a_a);
			}
			else
			{
				agreementDao.update(a_a);
			}
			
		
			
		}	
		catch(DAOException ex)
		{
			throw new DAOException(ex.getMessage());
		} 
	}
}
