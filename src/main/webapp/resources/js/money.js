function formatMoney(variable) {
//	number = variable.val();
//	//alert(number);
//	//$(this).val()
//	
//	number = number.toString().replace(/[^\,\d]/g,'');
//	var result = ''; 
//	var number_array = number.split(',');
//	number1='';
//	number2='';
//	for(var i = 0; i < number_array.length; i++) {
//		// Trim the excess whitespace.
//	    if (i==0)
//	    {
//	    	number1 = number_array[i];
//	    }
//	    if (i==1)
//	    {
//	    	number2 = number_array[i];
//	    }
//	}
//	//alert (number1);
//	var count = 0;
//	for(var i = number1.length; i >0; i--) {
//		count = count + 1;
//		if (count==4 || count==7 || count==10 || count==13 || count==16 || count==19 || count==22 || count==25 || count==28 || count==31 || count==34 || count==37 || count==40)
//	    {
//	    	result = number1.charAt(i-1)+' '+result ;
//	    }
//	    else{
//	    	result = number1.charAt(i-1)+result ;
//	    }
//
//	}
//	//alert (number1);
//	if (number2.length>0 && number2.length<3)
//	{
//		result = result +','+number2;
//	}
//	else if (number2.length>0)
//	{
//		result = result +','+number2.charAt(0)+number2.charAt(1);
//	}
//	else if (number2==0 && number_array.length>1)
//	{
//		result = result+',';
//	}
//	if (result[0]==0)
//	{
//		result = result.substring(1);
//	}	
//	variable.val(result);
//	if(number.length==0)
//	{
//		variable.val(0);
//	}	
	
}