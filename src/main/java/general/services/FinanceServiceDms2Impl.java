package general.services;

import general.dao.BkpfDao;
import general.dao.BsegDao;
import general.dao.ContractDao;
import general.dao.ContractHistoryDao;
import general.dao.ContractPromosDao;
import general.dao.DAOException;
import general.dao.MatnrDao;
import general.dao.PayrollDao;
import general.dao.UserDao;
import general.tables.Bkpf;
import general.tables.Bseg;
import general.tables.Bsik;
import general.tables.Contract;
import general.tables.ContractHistory;
import general.tables.ContractPromos;
import general.tables.ContractType;
import general.tables.Matnr;
import general.tables.Payroll;
import general.tables.Promotion;
import general.tables.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@Service("financeServiceDms2")
public class FinanceServiceDms2Impl implements FinanceServiceDms2{

    int dealer_pos_id=4;
    int manager_pos_id=3;
    int coordinator_pos_id=5;
    int demo_pos_id=8;
    int collector_pos_id=9;
    int director_pos_id=10;
    int trainer_pos_id=12;
    int main_trainer_pos_id=15;


    //bonus_type_id
    int premium = 1;
    int bonus = 2;
    int wage = 3;
    int opv = 4;
    int ipn = 5;
    int socnal = 6;
    int deposit = 7;
    int skidka = 8;
    int otmena = 9;
    int rashody = 10;
    int skidkaotdilera = 11;
    int zaakciya = 12;

    @Autowired
    FinanceService financeService;

    @Autowired
    PayrollService payrollService;

    @Autowired
    PayrollDao payrollDao;

    @Autowired
    ContractDao contractDao;

    @Autowired
    BkpfDao bkpfDao;

    @Autowired
    BsegDao bsegDao;

    @Autowired
    MatnrDao matnrDao;

    @Autowired
    ContractHistoryDao chDao;

    @Autowired
    UserDao userDao;

    @Autowired
    ContractService contractService;


    @Autowired
    private ContractPromosDao contractPromosDao;

    @Transactional
    public boolean createPromoFinanceDoc(List<Promotion> a_promoList, String a_bukrs, Long a_branch, Long a_contractNumber, Long a_customerId, Long a_awkey, Long a_userID, String a_tcode)
            throws DAOException
    {
        try {
            if (a_promoList != null && a_promoList.size() > 0) {
                Bkpf wa_bkpf = new Bkpf();
                List<Bseg> wal_bseg = new ArrayList<Bseg>();
                Calendar curDate = Calendar.getInstance();
                Time cputm = new Time(curDate.getTimeInMillis());

                if (a_branch==null){
                    throw new DAOException("Branch is empty");
                }

                if (a_userID==null || a_userID <= 0){
                    throw new DAOException("User is empty");
                }

                if (a_tcode == null || a_tcode.isEmpty())
                {
                    throw new DAOException("Transaction code is empty");
                }


                wa_bkpf.setBukrs(a_bukrs);
                wa_bkpf.setGjahr(curDate.get(Calendar.YEAR));
                wa_bkpf.setBlart("AK");
                wa_bkpf.setBudat(curDate.getTime());
                wa_bkpf.setBldat(curDate.getTime());
                wa_bkpf.setMonat(curDate.get(Calendar.MONTH)+1);
                wa_bkpf.setCpudt(curDate.getTime());
                wa_bkpf.setUsnam(a_userID);
                wa_bkpf.setTcode(a_tcode);
                wa_bkpf.setWaers("USD");
                wa_bkpf.setKursf(1);
                wa_bkpf.setBrnch(a_branch);
                wa_bkpf.setContract_number(a_contractNumber);
                wa_bkpf.setCustomer_id(a_customerId);
                wa_bkpf.setAwkey(a_awkey);
                wa_bkpf.setAwtyp(1);
                wa_bkpf.setClosed(0);


                int wa_buzei = 0;
                Bseg wa_bseg = new Bseg();
                //33100001
                //12100001

                for (Promotion wa_pro:a_promoList)
                {
                    wa_buzei++;
                    wa_bseg = new Bseg();
                    wa_bseg.setBukrs(wa_bkpf.getBukrs());
                    wa_bseg.setGjahr(wa_bkpf.getGjahr());
                    wa_bseg.setBuzei(wa_buzei);
                    wa_bseg.setBschl("3");
                    wa_bseg.setShkzg("S");
                    wa_bseg.setHkont("12100001");
                    wa_bseg.setLifnr(wa_bkpf.getCustomer_id());
                    wa_bseg.setMatnr(wa_pro.getMatnr());
                    wa_bseg.setDmbtr(0);
                    wa_bseg.setWrbtr(0);
                    wa_bseg.setMenge(1);
                    wal_bseg.add(wa_bseg);
                }

                wa_buzei++;
                wa_bseg = new Bseg();
                wa_bseg.setBukrs(wa_bkpf.getBukrs());
                wa_bseg.setGjahr(wa_bkpf.getGjahr());
                wa_bseg.setBuzei(wa_buzei);
                wa_bseg.setBschl("50");
                wa_bseg.setShkzg("H");
                wa_bseg.setHkont("60100030");
                wa_bseg.setDmbtr(0);
                wa_bseg.setWrbtr(0);
                wal_bseg.add(wa_bseg);




                if (wa_bkpf.getWaers() == null || wa_bkpf.getWaers().isEmpty()){
                    throw new DAOException("Currency is empty");
                }
                if (wa_bkpf.getKursf() == 0){
                    throw new DAOException("Currency rate is empty");
                }
                if (wa_bkpf.getMonat()==0){
                    throw new DAOException("Document month not chosen");
                }
//                if (wa_bkpf.getBusiness_area_id()==null || wa_bkpf.getBusiness_area_id()==0){
//                    throw new DAOException("Business area not chosen");
//                }
                if (wa_bkpf.getCpudt()==null){
                    throw new DAOException("System date not chosen");
                }



                List<Bsik> wal_bsik = new ArrayList<Bsik>();
                financeService.check_empty_fields(wa_bkpf, wal_bseg);
                financeService.insertNewFiDoc(wa_bkpf, wal_bseg, wal_bsik);

                return true;
            }
            else throw new DAOException("Promo finance doc not created");
        }
		catch (DAOException ex)
        {
            throw new DAOException(ex.getMessage()+" Create finance docs for promo -> finance service dms2 ");
        }
    }

    @Transactional
    public boolean cancelPromoFinanceDoc(Long a_contractNumber, Long a_userID, String a_tcode)
            throws DAOException
    {
        try {

            Contract con = contractDao.findByContractNumber(a_contractNumber);
            Bkpf a_bkpf_AK = new Bkpf();
            List<Bseg> al_bseg_AK = new ArrayList<Bseg>();
            a_bkpf_AK = bkpfDao.dynamicFindSingleBkpf("blart='AK' and contract_number="+a_contractNumber
                    + " and storno = 0 and awkey = "+con.getAwkey()+" and rownum=1 order by  budat desc,belnr desc ",con.getBukrs());
            if (a_bkpf_AK!=null && a_bkpf_AK.getBelnr()!=null)
            {
                al_bseg_AK = bsegDao.dynamicFindBseg("bukrs='"+con.getBukrs()+"' and belnr="+a_bkpf_AK.getBelnr()+" and gjahr="+a_bkpf_AK.getGjahr());
                financeService.cancelFiDoc(a_bkpf_AK, al_bseg_AK, a_userID, a_tcode);
                return true;
            }
            else throw new DAOException("Promo finance doc not could not be canncelled");
        }
        catch (DAOException ex)
        {
            throw new DAOException(ex.getMessage()+" Create finance docs for promo -> finance service dms2 ");
        }
    }


    @Transactional
    public boolean holdFromDealerAccountForPromo(String a_bukrs, Long a_contractNumber, Long a_dealerStaffId, Long a_branchId,String a_waers, double a_summa, Long a_userID, String a_tcode) throws DAOException{
        try{

            Calendar curDate = Calendar.getInstance();


            Payroll new_prl = new Payroll();
            new_prl.setGjahr(curDate.get(Calendar.YEAR));
            new_prl.setMonat(curDate.get(Calendar.MONTH)+1);
            new_prl.setApprove(0);
            new_prl.setPayroll_date(curDate.getTime());
            new_prl.setBldat(curDate.getTime());//BLDAT
            new_prl.setStaff_id(a_dealerStaffId);
            new_prl.setBranch_id(a_branchId);//BRANCH_ID
            new_prl.setContract_number(a_contractNumber);//CONTRACT_NUMBER
            new_prl.setPlan_amount(0);//PLAN_AMOUNT
            new_prl.setFact_amount(0);
            new_prl.setPosition_id((long) dealer_pos_id);
            new_prl.setMatnr_count(0);
            new_prl.setApprove(0);
            new_prl.setBukrs(a_bukrs);
            new_prl.setDrcrk("H");
            new_prl.setWaers(a_waers);
            new_prl.setDmbtr(a_summa);
            new_prl.setBonus_type_id(zaakciya);
            new_prl.setText45("Promo");
            payrollService.createNew(new_prl,a_userID,true,a_tcode,9);

            return true;
        }
        catch (DAOException ex)
        {
            throw new DAOException(ex.getMessage()+" Add promo materials-> finance service dms");
        }
    }

    @Transactional
    public boolean cancelHeldFromDealerAccountForPromo(String a_bukrs, Long a_contractNumber, Long a_userID, String a_tcode) throws DAOException{
        try{
            Calendar curDate = Calendar.getInstance();
            Contract con = contractDao.findByContractNumber(a_contractNumber);

            Payroll prlAD = new Payroll();
            prlAD = payrollDao.dynamicFindSinglePayroll(" bukrs ='"+a_bukrs+"' and contract_number="+a_contractNumber+" and bonus_type_id="+zaakciya
                    +" and staff_id="+con.getDealer()+" order by payroll_id DESC");




            if (prlAD!=null && payrollDao.dynamicFindCountPayroll(" parent_payroll_id="+prlAD.getPayroll_id())==0)
            {

                Payroll new_prl = new Payroll();
                new_prl.setGjahr(curDate.get(Calendar.YEAR));
                new_prl.setMonat(curDate.get(Calendar.MONTH)+1);
                new_prl.setApprove(0);
                new_prl.setPayroll_date(curDate.getTime());
                new_prl.setBldat(curDate.getTime());//BLDAT
                new_prl.setStaff_id(con.getDealer());
                new_prl.setBranch_id(con.getBranch_id());//BRANCH_ID
                new_prl.setContract_number(con.getContract_number());//CONTRACT_NUMBER
                new_prl.setPlan_amount(0);//PLAN_AMOUNT
                new_prl.setFact_amount(0);
                new_prl.setPosition_id((long) dealer_pos_id);
                new_prl.setMatnr_count(0);
                new_prl.setApprove(0);
                new_prl.setBukrs(con.getBukrs());
                new_prl.setDrcrk("S");
                new_prl.setWaers(prlAD.getWaers());
                new_prl.setDmbtr(prlAD.getDmbtr());
                new_prl.setBonus_type_id(otmena);
                new_prl.setText45("Cancel promo");
                new_prl.setParent_payroll_id(prlAD.getPayroll_id());
                payrollService.createNew(new_prl,a_userID,true,a_tcode,6);
            }

            return true;
        }
        catch (DAOException ex)
        {
            throw new DAOException(ex.getMessage()+" Add promo materials-> finance service dms");
        }
    }

   





}