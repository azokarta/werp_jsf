package crm.dao;

import general.dao.GenericDao;
import general.tables.KpiItem;

import java.util.Date;
import java.util.List;

/**
 * Created by admin on 22.11.2017.
 */
public interface KpiItemDao extends GenericDao<KpiItem> {
    List<KpiItem> findAllCurrent(Long branchId,Long positionId);

    List<KpiItem> findAllCurrent(List<Long> branchIds, List<Long> positionIds, Date fromDate,Date toDate);

    List<KpiItem> findAllByYearAndMonth(int year,int month);

    List<KpiItem> findAll(String bukrs,Long branchId,Long positionId,int year,int month);

    List<KpiItem> findAllForAllBranches(String bukrs,Long positionId,int year,int month);

    //Indicators For All Branches,ByPosition,By year and month
    List<KpiItem> findAllForDataCalculatingForAllBranchesByPosition(List<Integer> indicators,Long positionId,int year,int month);

    //Indicators FOR branches
    List<KpiItem> findAllByBranchCurrentForDataCalculating(List<Integer> indicators);

    List<KpiItem> findAllForReport(List<Long> branchIds, List<Long> positionIds, Integer year,Integer month);
}
