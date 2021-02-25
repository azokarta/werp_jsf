package crm.services;


import java.util.List;

import crm.dto.CrmKpiFinanceDto;

/**
 * Created by admin on 22.11.2017.
 */
public interface CrmKpiReportService {
    //Данные всех филиалов, в разрезе компании
    List<CrmKpiFinanceDto> getCurrentKpiForBranches(String bukrs, int year, int month);

    //Данные группы
    List<CrmKpiFinanceDto> getCurrentKpiForGroups(Long branchId, int year, int month);

    List<CrmKpiFinanceDto> getDataForFinance(String bukrs, int year, int month);
}
