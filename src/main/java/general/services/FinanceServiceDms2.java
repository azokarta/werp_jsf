package general.services;


import general.dao.DAOException;
import general.tables.Promotion;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface FinanceServiceDms2 {

    @Transactional
    public boolean createPromoFinanceDoc(List<Promotion> a_promoList, String a_bukrs, Long a_branch, Long a_contractNumber, Long a_customerId, Long a_awkey, Long a_userID, String a_tcode)
            throws DAOException;

    @Transactional
    public boolean cancelPromoFinanceDoc(Long a_contractNumber, Long a_userID, String a_tcode);

    @Transactional
    public boolean holdFromDealerAccountForPromo(String a_bukrs, Long a_contractNumber, Long a_dealerStaffId, Long a_branchId,String a_waers, double a_summa, Long a_userID, String a_tcode)
            throws DAOException;

    @Transactional
    public boolean cancelHeldFromDealerAccountForPromo(String a_bukrs, Long a_contractNumber, Long a_userID, String a_tcode) throws DAOException;










}