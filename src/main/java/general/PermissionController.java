package general;

import general.dao.DAOException;
import general.tables.Role_action;
import user.User;


public class PermissionController {
	
	public static final String NO_PERMISSION_MSG = "Нет доступа!";

	public final static int CAN_READ	= 1;
	public final static int CAN_WRITE	= 2;
	public final static int CAN_ALL		= 3;
	
	/**
	 * 
	 * @param userData - Данные юзера
	 * @param transactionId
	 * @param actionId
	 */
	
	public static void canRead(User userData,Long transactionId)
	{
		boolean hasPermission = false;
		if(userData.isSys_admin()){
			hasPermission = true;
		}else{
			for(Role_action ra:userData.getUserRoleActions()){
				if(ra.getTransaction_id().longValue() == transactionId.longValue() && (ra.getAction_id() == CAN_ALL || ra.getAction_id() == CAN_READ)){
					hasPermission = true;
					break;
				}
			}
		}
		
		if(!hasPermission){
			GeneralUtil.doRedirect("/no_permission.xhtml");
		}
	}
	
	public static void canWrite(User userData, Long transactionId) throws DAOException
	{
		boolean hasPermission = false;
		if(userData.isSys_admin()){
			hasPermission = true;
		}else{
			for(Role_action ra: userData.getUserRoleActions()){
				if(ra.getTransaction_id().longValue() == transactionId.longValue() && (ra.getAction_id() == CAN_WRITE || ra.getAction_id() == CAN_ALL)){
					hasPermission = true;
					break;
				}
			}
		}
		
		if(!hasPermission){
			throw new DAOException("No Permission!");
		}
	}
	
	
	public static void canWriteRedirect(User userData, Long transactionId) throws DAOException
	{
		boolean hasPermission = false;
		if(userData.isSys_admin()){
			hasPermission = true;
		}else{
			for(Role_action ra: userData.getUserRoleActions()){
				if(ra.getTransaction_id().longValue() == transactionId.longValue() && (ra.getAction_id() == CAN_WRITE || ra.getAction_id() == CAN_ALL)){
					hasPermission = true;
					break;
				}
			}
		}		
		if(!hasPermission){
			GeneralUtil.doRedirect("/no_permission.xhtml");
		}
	}
	
	
	public static boolean canView(User userData,Long transactionId)
	{
		boolean hasPermission = false;
		if(userData.isSys_admin()){
			hasPermission = true;
		}else{
			for(Role_action ra:userData.getUserRoleActions()){
				if(ra.getTransaction_id().longValue() == transactionId.longValue() && (ra.getAction_id() == CAN_ALL || ra.getAction_id() == CAN_READ)){
					hasPermission = true;
					break;
				}
			}
		}
		
		return hasPermission;
	}
	
	public static boolean canCreate(User userData, Long transactionId) throws DAOException
	{
		boolean hasPermission = false;
		if(userData.isSys_admin()){
			hasPermission = true;
		}else{
			for(Role_action ra: userData.getUserRoleActions()){
				if(ra.getTransaction_id().longValue() == transactionId.longValue() && (ra.getAction_id() == CAN_WRITE || ra.getAction_id() == CAN_ALL)){
					hasPermission = true;
					break;
				}
			}
		}

		return hasPermission;
	}
	
	public static boolean canAll(User userData, Long transactionId) throws DAOException
	{
		boolean hasPermission = false;
		if(userData.isSys_admin()){
			hasPermission = true;
		}else{
			for(Role_action ra: userData.getUserRoleActions()){
				if(ra.getTransaction_id().equals(transactionId) && ra.getAction_id() == CAN_ALL){
					hasPermission = true;
					break;
				}
			}
		}

		return hasPermission;
	}
}