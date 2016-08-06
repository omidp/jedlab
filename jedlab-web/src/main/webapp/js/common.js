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
		playVideo : function(uri, uid, p){
	       document.applets[0].setParam("audio", true); 
	       document.applets[0].setParam("url", uri);
	       document.applets[0].setParam("userId", uid);
	       document.applets[0].setParam("password", p);
	       document.applets[0].setParam("local", false);
	       document.applets[0].setParam("keepAspect", false);
	       common.restartVideo();
		},
		restartVideo : function(){
			document.applets[0].restart(); 
		}
};



