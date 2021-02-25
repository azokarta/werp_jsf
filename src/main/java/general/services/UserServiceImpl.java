package general.services;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;

import general.PasswordHelper;
import general.Validation;
import general.dao.DAOException;
import general.dao.UserDao;
import general.dao.UserRoleDao;
import general.tables.Role;
import general.tables.User;
import general.tables.UserRole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {
	@Autowired
	UserDao dao;

	@Override
	public void createUser(User u, List<Role> selectedRoles) throws DAOException {
		String error = validateUser(u, true);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		dao.create(u);
		setUserRoles(u, selectedRoles);
	}

	private String validateUser(User u, boolean isNew) {
		String error = "";
		if (Validation.isEmptyString(u.getUsername())) {
			error += "Please, enter username";
		} else {
			if (isNew) {
				if (dao.countUserByUsername(u.getUsername()) > 0) {
					error += "User with the same username exists";
				}

				User tempUser = dao.findUserByStaffId(u.getStaff_id());
				if (tempUser != null) {
					error += "У сотрудника уже имеется аккаунт! \n";
				}
			} else {
				User tempUser = null;
				try {
					tempUser = this.searchByUsername(u.getUsername());
				} catch (DAOException e) {
				}

				if (tempUser != null && !tempUser.getUser_id().equals(u.getUser_id())) {
					error += "User with the same username exists";
				}

				tempUser = dao.findUserByStaffId(u.getStaff_id());
				if (tempUser != null && !tempUser.getUser_id().equals(u.getUser_id())) {
					error += "У сотрудника уже имеется аккаунт! \n";
				}
			}
		}

		if (!Validation.isEmptyString(u.getPassword())) {
			if (Validation.isValidPassword(u.getPassword())) {
				u.setSalt(PasswordHelper.getGeneratedSalt(u.getPassword()));
				try {
					u.setHash(PasswordHelper.getSha256(u.getPassword() + u.getSalt()));
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				u.setPassword(null);
			} else {
				error += "Password must contain at least: 1 digit, 1 upper letter, 8 symbols";
			}
		} else if (isNew) {
			error += "Please enter password";
		}

		if (!Validation.isEmptyString(u.getEmail())) {
			if (!Validation.isValidEmail(u.getEmail())) {
				error += "Invalid email";
			}
		} else {
			error += "Please enter email";
		}

		if (Validation.isEmptyString(u.getBukrs())) {
			error += "Enter company";
		}
		// System.out.println("BRANCH: " + u.getBranch_id());
		if (u.getBranch_id() == null || u.getBranch_id().longValue() == 0L) {
			error += "Enter branch";
		}

		if (isNew) {
			u.setCreate_time(Calendar.getInstance().getTime());
		}

		return error;
	}

	@Autowired
	UserRoleDao urDao;

	private void setUserRoles(User u, List<Role> selectedRoles) {
		if (!Validation.isEmptyLong(u.getUser_id())) {
			List<UserRole> urList = urDao.findUserRoles(u.getUser_id());
			for (UserRole ur : urList) {
				urDao.delete(ur.getId());
			}
		}
		for (Role r : selectedRoles) {
			UserRole ur = new UserRole();
			ur.setRoleId(r.getRole_id());
			ur.setUserId(u.getUser_id());
			urDao.create(ur);
		}
	}

	@Override
	public User searchByUsername(String a_username) throws DAOException {
		if (dao.countUserByUsername(a_username) == 0) {
			throw new DAOException("No user found");
		} else if (dao.countUserByUsername(a_username) == 1) {
			return dao.findByUsername(a_username);
		} else {
			throw new DAOException("Unexpected error");
		}
	}

	@Override
	public void updateUser(User u, List<Role> selectedRoles) throws DAOException {
		// System.out.println("em: " + u.getEmail());
		String error = validateUser(u, false);
		if (error.length() > 0) {
			throw new DAOException(error);
		}

		dao.update(u);
		setUserRoles(u, selectedRoles);
	}

	@Override
	public void changePassword(User user) throws DAOException {
		String error = "";
		if (!Validation.isEmptyString(user.getPassword())) {
			if (Validation.isValidPassword(user.getPassword())) {
				user.setSalt(PasswordHelper.getGeneratedSalt(user.getPassword()));
				try {
					user.setHash(PasswordHelper.getSha256(user.getPassword() + user.getSalt()));
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				user.setPassword(null);
			} else {
				error += "Пароль должен иметь минимум 1 цифру, 1 символ верхнего регистра. Пароль должен содержать минимум 8 символов";
			}
		} else {
			error += "Введите новый пароль";
		}

		if (error.length() > 0) {
			throw new DAOException(error);
		}

		dao.update(user);
	}

	@Override
	public void changeAgreement(Long userId, boolean agree) throws DAOException {
		User u = dao.find(userId);
		if (u == null) {
			throw new DAOException("Не найден пользователь");
		}

		u.setIs_agree(agree ? 1 : 0);
		if (!agree) {
			u.setIs_active(0);
		}
		u.setAgreement_date(Calendar.getInstance().getTime());
		dao.update(u);
	}
}
