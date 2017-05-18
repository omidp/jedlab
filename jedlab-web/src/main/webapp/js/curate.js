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
						//common.notification("#{messages['Error_Title']}", "errormsg");
				    },
				    error: function(errMsg) {				    	
				    	var b = JSON.parse(errMsg.responseText);
						common.notification(b.errors[0].msg, "errormsg");
				    }
				}
			);
			
		},
		deletePageBlock:function(bid){
			
			jQuery.ajax(
					{
						type:"DELETE",
						url: contextPath+"/seam/resource/api/pages/blocks/"+bid,						
						contentType: "application/json; charset=utf-8",
						dataType: "json",
						success: function(data){																				
							jQuery("#block_"+bid).hide();							
						},
						failure: function(errMsg) {
							//common.notification("#{messages['Error_Title']}", "errormsg");
					    },
					    error: function(errMsg) {					    	
					    	
					    }
					}
				);
			
			
		},
		deleteCurate:function(curateId){
			
			jQuery.ajax(
					{
						type:"DELETE",
						url: contextPath+"/seam/resource/api/pages/curates/"+curateId,						
						contentType: "application/json; charset=utf-8",
						dataType: "json",
						success: function(data){																				
							jQuery("#lnk_"+curateId).hide();
							jQuery("#del_"+curateId).hide();
						},
						failure: function(errMsg) {
							//common.notification("#{messages['Error_Title']}", "errormsg");
					    },
					    error: function(errMsg) {					    	
					    	
					    }
					}
				);
			
		},
		addCurate:function(bid){
			var val = jQuery("#curinp_"+bid).val();
			var p = jQuery("#curbtn_"+bid).html();
			jQuery("#curbtn_"+bid).html(processing);
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
							jQuery("#curbtn_"+bid).val(processing);
							jQuery("#curbtn_"+bid).html(p);
							curate.visibility(data.inProgress);
						},
						failure: function(errMsg) {
							//common.notification("#{messages['Error_Title']}", "errormsg");
					    },
					    error: function(errMsg) {					    	
					    	var b = JSON.parse(errMsg.responseText);
							common.notification(b.errors[0].msg, "errormsg");
							jQuery("#curbtn_"+bid).html(p);
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
								//common.notification("#{messages['Error_Title']}", "errormsg");
						    },
						    error: function(errMsg) {
						    	var b = JSON.parse(errMsg.responseText);
								common.notification(b.errors[0].msg, "errormsg");
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
		},
		visibility:function(inProg){
			if(inProg == 'true')
			{
				jQuery("#waitForApproval").show();
				jQuery("#btnApprovalAction").hide();
			} 
			else
			{
				jQuery("#waitForApproval").hide();
				jQuery("#btnApprovalAction").show();
			}
		}
		
		
}