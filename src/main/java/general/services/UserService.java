package general.services;

import java.util.List;

import general.dao.DAOException;
import general.tables.Role;
import general.tables.User;
import general.tables.UserRole;

import org.springframework.transaction.annotation.Transactional;

public interface UserService {
	@Transactional
	void createUser(User a_user,List<Role> selectedRoles) throws DAOException;

	@Transactional
	User searchByUsername(String a_username) throws DAOException;

	@Transactional
	void updateUser(User a_user,List<Role> selectedRoles) throws DAOException;
	
	@Transactional
	void changePassword(User user) throws DAOException;
	
	@Transactional
	void changeAgreement(Long userId,boolean agree) throws DAOException;
}
