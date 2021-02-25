package general.services;

import general.dao.DAOException;
import general.output.tables.Podotchet;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

public interface FinancePodotchet {
	@Transactional //nachislenie i oplata
	public List<Podotchet> getPodotchetList(String a_bukrs, Long a_branch_id, String a_waers) throws DAOException;
}
