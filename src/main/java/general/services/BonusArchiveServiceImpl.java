package general.services;
 

import general.dao.BonusArchiveDao;
import general.dao.DAOException; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("bonusArchiveService")
public class BonusArchiveServiceImpl implements BonusArchiveService{
	@Autowired
    private BonusArchiveDao bonArcDao;
	 
	@Override
    public Long checkBonusExistence (int a_monat, int a_gjahr) throws DAOException{ 
		return bonArcDao.countAnyOldOrEqualBonus(a_monat,a_gjahr);
	}
}
