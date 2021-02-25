package general.services;

import general.dao.DAOException;
import general.tables.Bonus;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

public interface BonusArchiveService {
	@Transactional
	Long checkBonusExistence (int a_monat, int a_gjahr) throws DAOException;
}
