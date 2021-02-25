package general.springservice;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import dit.message.UserStaff;
import general.dao.DAOException;
import general.tables2.Agreement;
import general.tables2.Prikaz;
import general.tables2.PrikazAttach;

public interface PrikazSpSer {
	
	@Transactional
	public void createPrikaz(Prikaz a_pz,List<PrikazAttach> al_pa, List<Agreement> al_a) throws DAOException;
	
	@Transactional
	public void modifyPrikaz(Prikaz a_pz,List<PrikazAttach> al_pa, List<Agreement> al_a) throws DAOException;
	
	@Transactional
	public void sendPrikaz(Prikaz a_pz,Agreement a_a) throws DAOException;
	
	
	@Transactional
	public void confirmPrikaz( List<Agreement> al_a,Agreement a_a,Prikaz a_prikaz) throws DAOException;
}
