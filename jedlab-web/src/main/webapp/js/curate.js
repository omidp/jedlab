var curate = {
		
		createCuratePage:function(){
			
			var val =jQuery("#curatePageTitle").val();
			jQuery.ajax(
				{
					type:"POST",
					url: contextPath+"/seam/resource/api/pages",
					data: JSON.stringify({ 'title': val }),
					contentType: "application/json; charset=utf-8",
					dataType: "json",
					success: function(data){
						location.href = window.location.origin + contextPath+ '/curate/page/'+data.id;
					},
					failure: function(errMsg) {
						common.notification("#{messages['Error_Title']}", "errormsg");
				    },
				    error: function(errMsg) {
						common.notification("#{messages['Error_Title']}", "errormsg");
				    }
				}
			);
			
		},
		addCurate:function(bid){
			var val = jQuery("#curinp_"+bid).val();

			jQuery.ajax(
					{
						type:"POST",
						url: contextPath+"/seam/resource/api/pages/curates",
						data: JSON.stringify({ "url": val, "pageBlock" :{"id":bid} }),
						contentType: "application/json; charset=utf-8",
						dataType: "json",
						success: function(data){																				
							var template = Handlebars.templates['curates'];
							var html  = template(data);											
							jQuery("#ul_"+bid).append(html);
							jQuery("#curinp_"+bid).val('');
						},
						failure: function(errMsg) {
							common.notification("#{messages['Error_Title']}", "errormsg");
					    },
					    error: function(errMsg) {
							common.notification("#{messages['Error_Title']}", "errormsg");
					    }
					}
				);
			
		},
		setUpClickEdit:function(data){
			var jq = '.clickedit';	
			if(typeof data != 'undefined')
				jq = '.clickedit.'+data;
			
			jQuery(jq).hide()
			.focusout(curate.endEdit)
			.keyup(function (e) {
			    if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
			        curate.endEdit(e);
			        return false;
			    } else {
			        return true;
			    }
			})
			.prev().click(function () {
			    jQuery(this).hide();
			    jQuery(this).next().show().focus();
			});
		},
		endEdit:function(e){
			var input = jQuery(e.target),
	        label = input && input.prev();

		    var blockId = label.data('block-id');	    
		    var blockTitle = label.data('block-title');
		    if(blockTitle != input.val())
			{
	
		    	jQuery.ajax(
						{
							type:"PUT",
							url: contextPath+"/seam/resource/api/pages/blocks/"+blockId,
							data: JSON.stringify({ 'id': blockId, 'title':  input.val() }),
							contentType: "application/json; charset=utf-8",
							dataType: "json",
							success: function(data){																				
							    
								label.text(input.val() === '' ? defaultText : input.val());
							    input.hide();
							    label.show();
							},
							failure: function(errMsg) {
								common.notification("#{messages['Error_Title']}", "errormsg");
						    },
						    error: function(errMsg) {
								common.notification("#{messages['Error_Title']}", "errormsg");
						    }
						}
					);
	
		    }
		    else
			{
		    	label.text(input.val() === '' ? defaultText : input.val());
			    input.hide();
			    label.show();
			}
		}
		
		
}