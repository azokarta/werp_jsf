package general.services;

import general.dao.DAOException;
import general.tables.WriteoffRepair;

import org.springframework.transaction.annotation.Transactional;

import user.User;

public interface WriteoffRepairService {

	@Transactional
	void create(WriteoffRepair wr, Long userId) throws DAOException;

	@Transactional
	void update(WriteoffRepair wr, Long userId) throws DAOException;

	@Transactional
	void writeoff(WriteoffRepair wr, Long userId) throws DAOException;
}
