var common = {
		
		notification : function(msg, severity) {
			common.removeNotification();
			var msgTemplate = "<ul id='messages' class='message'><li class=%SEVERITY%>%CONTENT%</li>";
			var render = msgTemplate.replace(/%CONTENT%/, msg).replace(
					/%SEVERITY%/, severity);
			jQuery(".alert-box").append(render);
			jQuery("ul.message li").click(function() {
				jQuery(this).hide('drop', {}, 1000).fadeOut();
			});
		},
		removeNotification : function() {
			jQuery(".alert-box").empty();
		},
		isIE : function(){
			var ua = window.navigator.userAgent;
		    var msie = ua.indexOf("MSIE ");

		    if (msie > 0) // If Internet Explorer, return version number
		    {
		        return true;
		    }
		    else  // If another browser, return 0
		    {
		    	if (msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./))  // If Internet Explorer, return version number
		        {
		            return true;
		        }
		    }

		    return false;
		},
		showLoading : function() {
			jQuery("#dimmerLoader").dimmer('show');
		},
		hideLoading : function() {
			jQuery("#dimmerLoader").dimmer('hide');
		}
};



