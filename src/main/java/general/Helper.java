package general;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Helper {

	public static String getCurrentDateForDb()
	{
		return (new SimpleDateFormat("yyyy-MM-dd")).format(Calendar.getInstance().getTime());
	}
	
	public static int getCurrentYear()
	{
		return Calendar.getInstance().get(Calendar.YEAR);
	}
	
	public static int getCurrentMonth()
	{
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}
	
	public static Double getFormattedNumber(double d){
		DecimalFormat df = new DecimalFormat("#.00");
		return Double.valueOf(df.format(d));
	}
	
	public static Long returnAwkey(Long a_belnr, int a_gjahr)
	{
		if (a_belnr !=null && a_gjahr>0)
		{
			Long awkey = Long.parseLong(String.valueOf(a_belnr)+String.valueOf(a_gjahr));
			return awkey;
		}
		else
		{
			return null;
		}
	}
	
	public static String getFormattedDateForDb(Date d){
		if(d == null){
			return "";
		}
		
		Calendar cld = Calendar.getInstance();
		cld.setTime(d);
		return new SimpleDateFormat("yyyy-MM-dd").format(cld.getTime());
	}
	
	public static String getErrorMessage(Long a_id, String a_lang){
		String text = "";
		String en = "";
		String ru = "";
		String tr = "";
		if (a_id.equals(1L)) {
			en = "Select Currency";
			tr = "Select Currency";
			ru = "Выберите валюту";
		}
		else if (a_id.equals(2L)) {
			en = "Exchange rate is 0 or less";
			tr = "Exchange rate is 0 or less";
			ru = "Курсе равен 0 или меньше.";
		}
		else if (a_id.equals(3L)) {
			en = "Select Cash/Bank";
			tr = "Select Cash/Bank";
			ru = "Выберите Касса/Банк.";
		}
		else if (a_id.equals(4L)) {
			en = "Select Department";
			tr = "Select Department";
			ru = "Выберите Отдел";
		}
		else if (a_id.equals(5L)) {
			en = "Select company";
			tr = "Select company";
			ru = "Выберите компанию";
		}
		else if (a_id.equals(6L)) {
			en = "Transaction failed";
			tr = "Transaction failed";
			ru = "Проводка не прошла";
		}
		else if (a_id.equals(7L)) {
			en = "Select branch";
			tr = "Select branch";
			ru = "Выберите филиал";
		}
		else if (a_id.equals(8L)) {
			en = "Enter amount";
			tr = "Enter amount";
			ru = "Введите сумму";
		}
		else if (a_id.equals(9L)) {
			en = "Select customer";
			tr = "Select customer";
			ru = "Выберите клиента";
		}
		else if (a_id.equals(10L)) {
			en = "Select warehouse";
			tr = "Select warehouse";
			ru = "Выберите склад";
		}
		else if (a_id.equals(11L)) {
			en = "Incorrect quantity";
			tr = "Incorrect quantity";
			ru = "Количество неправильно";
		}
		else if (a_id.equals(12L)) {
			en = "Choose GL account";
			tr = "Choose GL account";
			ru = "Выберите счет ГК";
		}
		else if (a_id.equals(13L)) {
			en = "Select date from";
			tr = "Select date from";
			ru = "Выберите дату с";
		}
		else if (a_id.equals(14L)) {
			en = "Select date to";
			tr = "Select date to";
			ru = "Выберите дату по";
		}
		
		
		

		else if (a_id.equals(60L)) {
			en = "Select company account";
			tr = "Select company account";
			ru = "Выберите счет фирмы";
		}
		else if (a_id.equals(61L)) {
			en = "Amount is 0 or negative";
			tr = "Amount is 0 or negative";
			ru = "Сумма 0 или отрицательная";
		}
		else if (a_id.equals(62L)) {
			en = "Not enough money on company account";
			tr = "Not enough money on company account";
			ru = "Сумма на счету фирмы недостаточна.";
		}
		else if (a_id.equals(63L)) {
			en = "Select employee";
			tr = "Select employee";
			ru = "Выберите сотрудника";
		}
		else if (a_id.equals(64L)) {
			en = "Not enough money to issue, press save";
			tr = "Not enough money to issue, press save";
			ru = "Сумма достаточна для выдачи. нажмите сохранить.";
		}
		else if (a_id.equals(65L)) {
			en = "You already have an advance request";
			tr = "You already have an advance request";
			ru = "У вас имеется запрос на аванс.";
		}
		else if (a_id.equals(66L)) {
			en = "Company account currency not equal to advance request currency";
			tr = "Company account currency not equal to advance request currency";
			ru = "Валюта счета фирмы не равна валюте аванса.";
		}
		else if (a_id.equals(67L)) {
			en = "Not enough money on employee account";
			tr = "Not enough money on employee account";
			ru = "На счету сотрудника не хватает средств";
		}
		else if (a_id.equals(69L)) {
			en = "Incorrect date";
			tr = "Incorrect date";
			ru = "Дата неправильная.";
		}
		else if (a_id.equals(70L)) {
			en = "No money on balance account";
			tr = "No money on balance account";
			ru = "Средства на балансе нет.";
		}
		else if (a_id.equals(71L)) {
			en = "Payment amount is larger than on balance account";
			tr = "Payment amount is larger than on balance account";
			ru = "Сумма оплачиваемая больше чем на балансе.";
		}
		else if (a_id.equals(72L)) {
			en = "Payment amount is larger than on debt account";
			tr = "Payment amount is larger than on debt account";
			ru = "Сумма оплачиваемая больше чем на долге.";
		}
		else if (a_id.equals(73L)) {
			en = "No positive debt on debt account";
			tr = "No positive debt on debt account";
			ru = "Плюсовой долг нет на долг. счете.";
		}
		else if (a_id.equals(74L)) {
			en = "Amount is larger";
			tr = "Amount is larger";
			ru = "Сумма больше.";
		}
		else if (a_id.equals(75L)) {
			en = "No debt on on balance account";
			tr = "No debt on on balance account";
			ru = "Долг нет на баланс счете.";
		}
		else if (a_id.equals(76L)) {
			en = "No debt";
			tr = "No debt";
			ru = "Долгов нет";
		}
		else if (a_id.equals(77L)) {
			en = "Already have a request for salary accrual";
			tr = "Already have a request for salary accrual";
			ru = "Запрос на начисление ЗП имеется.";
		}
		else if (a_id.equals(78L)) {
			en = "Salary not accrued yet";
			tr = "Salary not accrued yet";
			ru = "Заработня плата еще не начислена!";
		}
		
		
		
		
		
		
		
		
		else if (a_id.equals(90L)) {
			en = "Accountable employee not chosen";
			tr = "Accountable employee not chosen";
			ru = "Подотчет не выбран";
		}
		else if (a_id.equals(91L)) {
			en = "Need at least 1 cash/bank";
			tr = "Need at least 1 cash/bank";
			ru = "Нужна минимум 1 касса или банк";
		}
		else if (a_id.equals(92L)) {
			en = "Need at least 2 cash/bank";
			tr = "Need at least 2 cash/bank";
			ru = "Нужны минимум 2 кассы или банк";
		}
		else if (a_id.equals(100L)) {
			en = "Not found";
			tr = "Not found";
			ru = "Не найдено";
		}
		else if (a_id.equals(101L)) {
			en = "Success";
			tr = "Success";
			ru = "Успешно";
		}
		else if (a_id.equals(102L)) {
			en = "No access";
			tr = "No access";
			ru = "У вас нет прав.";
		}
		else if (a_id.equals(103L)) {
			en = "No branch found";
			tr = "No branch found";
			ru = "Филиал не найден.";
		}
		else if (a_id.equals(104L)) {
			en = "Saved";
			tr = "Saved";
			ru = "Сохранен";
		}
		
		
		
			
		if (a_lang.equals("en")) text = en;
		else if (a_lang.equals("ru")) text = ru;
		else if (a_lang.equals("tr")) text = tr;
		
		return text;
	}
}