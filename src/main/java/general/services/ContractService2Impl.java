package general.services;

import java.util.Calendar;
import java.util.List;

import general.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import f4.ContractTypeF4;
import general.tables.Contract;
import general.tables.ContractHistory;
import general.tables.ContractPromos;
import general.tables.ContractType;
import general.tables.Matnr;
import general.tables.Promotion;
import general.tables.User;

@Service("contractService2")
public class ContractService2Impl implements ContractService2 {
    @Autowired
    private ContractDao conDao;

    @Autowired
    private ContractHistoryDao chDao;

    @Autowired
    private ContractTypeDao contractTypeDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PromotionDao promotionDao;

    @Autowired
    private ContractPromosDao contractPromosDao;

    @Autowired
    private FinanceServiceDms2 financeServiceDms2;

//    @Autowired
//    @Qualifier("contractTypeF4Bean")
//    private ContractTypeF4 contractTypeF4;


    @Autowired
    ContractDao contractDao;

    @Autowired
    MatnrDao matnrDao;

    @Autowired
    ContractService contractService;


    

    
    @Transactional
    public boolean onCreateContractHistoryPromo(Long a_contractId, List<Promotion> promoList, Long a_userId) throws DAOException
    {
        try
        {
            User wauser = userDao.find(a_userId);
            for (Promotion wa_ipt : promoList) {
                ContractHistory wa_ch = new ContractHistory(a_contractId);
                wa_ch.setContract_id(a_contractId);
                wa_ch.setOld_id(wa_ipt.getId());
                wa_ch.setOper_on(80L);
                wa_ch.setOper_type(1L);
                wa_ch.setOld_text(wa_ipt.getName());
                String wa_info = "Promo-Campaign : "+ wa_ipt.getName() + " is added.";
                wa_info += " (User: " + wauser.getUsername() + ")";
                wa_info = wa_info.substring(0, Math.min(wa_info.length(), 125));
                wa_ch.setUser_id(a_userId);
                wa_ch.setInfo(wa_info);
                wa_ch.setProcessed(0);
                chDao.create(wa_ch);
            }

            return true;
        }
        catch (DAOException ex)
        {
            throw new DAOException(ex.getMessage()+" onCreateContractHistoryPromo-> finance service dms2");
        }
    }


    @Transactional
    public boolean onRemoveContractHistoryPromo(Long a_contractNumber, Long a_userId) throws DAOException
    {
        try
        {

            Contract contract = contractDao.findByContractNumber(a_contractNumber);
            User wauser = userDao.find(a_userId);
            for (Promotion wa_ipt : promotionDao.findContractPromotions(contract.getContract_id())) {
                ContractHistory wa_ch = new ContractHistory(contract.getContract_id());
                wa_ch.setContract_id(contract.getContract_id());
                wa_ch.setOld_id(wa_ipt.getId());
                wa_ch.setOper_on(80L);
                wa_ch.setOper_type(5L);
                wa_ch.setOld_text(wa_ipt.getName());
                String wa_info = "Promo-Campaign : "+ wa_ipt.getName() + " is cancelled or returned.";
                wa_info += " (User: " + wauser.getUsername() + ")";
                wa_info = wa_info.substring(0, Math.min(wa_info.length(), 125));
                wa_ch.setUser_id(a_userId);
                wa_ch.setInfo(wa_info);
                wa_ch.setProcessed(0);
                chDao.create(wa_ch);
            }

            return true;
        }
        catch (DAOException ex)
        {
            throw new DAOException(ex.getMessage()+" onRemoveContractHistoryPromo-> finance service dms2");
        }
    }

    @Transactional
    public boolean onCreateContractHistoryMatnr(Long a_contractId, Long a_matnr, Long a_userId) throws DAOException
    {
        try
        {
            Matnr wamatnr = matnrDao.find(a_matnr);
            // Contract History
            ContractHistory wa_ch = new ContractHistory();

            Calendar cal = Calendar.getInstance();
            wa_ch.setContract_id(a_contractId);
            wa_ch.setRec_date(cal.getTime());
            wa_ch.setNew_id(wamatnr.getMatnr());
            wa_ch.setOper_on(30L);
            wa_ch.setOper_type(1L);

            wa_ch.setInfo("Matnr added:"+wamatnr.getText45());
            wa_ch.setProcessed(1);
            wa_ch.setUser_id(a_userId);

            chDao.create(wa_ch);

            return true;
        }
        catch (DAOException ex)
        {
            throw new DAOException(ex.getMessage()+" onCreateContractHistoryMatnr-> finance service dms2");
        }
    }

    @Transactional
    public boolean onRemoveContractHistoryMatnr(Long a_contractNumber, Long a_userId) throws DAOException
    {
        try
        {
            Contract contract = contractDao.findByContractNumber(a_contractNumber);
            ContractType contractType = contractTypeDao.find(contract.getContract_type_id());//contractTypeF4.getCt_map().get(contract.getContract_type_id());
            Matnr wamatnr = matnrDao.find(contractType.getMatnr());
            // Contract History
            ContractHistory wa_ch = new ContractHistory();

            Calendar cal = Calendar.getInstance();
            wa_ch.setContract_id(contract.getContract_id());
            wa_ch.setRec_date(cal.getTime());
            wa_ch.setNew_id(wamatnr.getMatnr());
            wa_ch.setOper_on(30L);
            wa_ch.setOper_type(5L);

            wa_ch.setInfo("Matnr cancelled or returned:"+wamatnr.getText45());
            wa_ch.setProcessed(1);
            wa_ch.setUser_id(a_userId);

            chDao.create(wa_ch);

            return true;
        }
        catch (DAOException ex)
        {
            throw new DAOException(ex.getMessage()+" onRemoveContractHistoryMatnr-> finance service dms2");
        }
    }


    @Transactional
    public boolean onCreateContractMatnr(Long a_contractNumber, String a_tovarSerial, Long a_matnrListId, Long a_userId) throws DAOException
    {
        try
        {
            Contract contract = contractDao.findByContractNumber(a_contractNumber);
            contract.setMatnr_release_date(Calendar.getInstance().getTime());
            contract.setMatnr_list_id(a_matnrListId);
            contract.setTovar_serial(a_tovarSerial);
            contract.setUpdated_date(Calendar.getInstance().getTime());
            contract.setUpdated_by(a_userId);
            contract.setLast_state(Contract.LASTSTATE_GIVEN);
            contractDao.update(contract);

            if (contract.getTovar_category() == ContractType.TOVARCAT_VACUUM_CLEANER) {
                contractService.createServFilterVCForNewContract(contract);
            }

            return true;
        }

        catch (DAOException ex)
        {
            throw new DAOException(ex.getMessage()+" onCreateContractMatnr-> finance service dms2");
        }
        catch (Exception ex) {
            throw new DAOException(ex.getMessage());
        }
    }

    @Transactional
    public boolean onCancelWriteOffAndReturnContractMatnr(Long a_contractNumber, Long a_userId) throws DAOException
    {
        try
        {
            Contract contract = contractDao.findByContractNumber(a_contractNumber);
            contract.setMatnr_release_date(null);
            contract.setMatnr_list_id(null);
            contract.setTovar_serial("");
            contract.setUpdated_date(Calendar.getInstance().getTime());
            contract.setUpdated_by(a_userId);
            contract.setLast_state(Contract.LASTSTATE_RETURNED);
            contractDao.update(contract);

            return true;
        }
        catch (DAOException ex)
        {
            throw new DAOException(ex.getMessage()+" onCancelWriteOffAndReturnContractMatnr-> finance service dms2");
        }
        catch (Exception ex) {
            throw new DAOException(ex.getMessage());
        }
    }

    @Transactional
    public boolean onCreateContractPromo(List<Promotion> a_promoList, Long a_contractId) throws DAOException
    {
        try
        {
            for(Promotion wapt:a_promoList){

                ContractPromos wacp = new ContractPromos();
                wacp.setContract_id(a_contractId);
                wacp.setPromo_id(wapt.getId());
                contractPromosDao.create(wacp);
            }

            return true;
        }
        catch (DAOException ex)
        {
            throw new DAOException(ex.getMessage()+" onCreateContractPromo-> finance service dms2");
        }
    }

    @Transactional
    public boolean onCancelWriteOffAndReturnContractPromo(Long a_contractNumber) throws DAOException
    {
        try
        {
            Contract contract = contractDao.findByContractNumber(a_contractNumber);

            List<ContractPromos> conPromos = contractPromosDao.findAllByContrID(contract.getContract_id());
            for(ContractPromos wacp:conPromos){
                contractPromosDao.delete(wacp.getId());
            }

            return true;
        }
        catch (DAOException ex)
        {
            throw new DAOException(ex.getMessage()+" onCancelWriteOffAndReturnContractPromo-> finance service dms2");
        }
    }
}
