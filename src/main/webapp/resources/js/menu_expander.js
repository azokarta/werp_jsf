$(document).ready(function(){
	if($('#main-page-content').size())
	{
		$('#left-sidebar-cnt').show();
		$('#main-menu-toggle').hide();
	}
	else
	{
		$('#main-menu-toggle').click(function(){
			$('#left-sidebar-cnt').slideToggle('fast',function(){});
		});
	}
	
	
	/*
	var oSidebar = $('#sidebar-cnt');
	var oContent = $('#main-content-cnt');
	if(oContent.size())
	{
		oSidebar.css('display','none');
		oContent.removeClass('ui-grid-col-10').addClass('ui-grid-col-12');
		$('#main-menu-toggle').click(function(){
			if(oContent.hasClass('ui-grid-col-12'))
			{
				oContent.removeClass('ui-grid-col-12').addClass('ui-grid-col-10');
				oSidebar.css('display','block');
				$(this).first('span').removeClass('ui-icon-folder-collapsed').addClass('ui-icon-folder-open');
			}
			else
			{
				oContent.removeClass('ui-grid-col-10').addClass('ui-grid-col-12');
				oSidebar.css('display','none');
				$(this).first('span').removeClass('ui-icon-folder-open').addClass('ui-icon-folder-collapsed');
			}
		});
	}
	else
	{
		$('#main-menu-toggle').hide();
	}*/
})