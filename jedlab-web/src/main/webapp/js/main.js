var jedlab = {
	init : function() {
		jedlab.resetPaging();
	},
	PostFormRequest : function(url, formId, callback) {
		new Ajax.Request(url, {
			method : 'post',
			parameters : Object.toJSON($(formId).serialize(true)),
			requestHeaders : {
				Accept : 'application/json'
			},
			contentType : 'application/json',
			onSuccess : function(transport) {
				callback(transport);
			},
			onFailure : function() {
				alert('Something went wrong...');
			}
		});
	},
	PostRequest : function(url, json, callback, failCallback) {
		new Ajax.Request(url, {
			method : 'post',
			parameters : json,
			requestHeaders : {
				Accept : 'application/json'
			},
			contentType : 'application/json',
			onSuccess : function(transport) {
				callback(transport);
			},
			onFailure : function(transport) {
				failCallback(transport);
			}
		});
	},
	GetRequest : function(url, callback, params) {
		if (jQuery.isEmptyObject(params))
			params = {};
		new Ajax.Request(url, {
			method : 'get',
			parameters : params,
			requestHeaders : {
				Accept : 'application/json'
			},
			contentType : 'application/json',
			onSuccess : function(transport) {
				callback(transport);
			},
			onFailure : function() {
				alert('Something went wrong...');
			}
		});
	},
	initUploader : function(uploader) {
		if ('files' in uploader) {
			if (uploader.files.length == 0) {
				return "select image";
			} else {
				var file = uploader.files[0];
				return file.name;
			}
		}
	},
	setPageScroll : function(componentId) {
		var pos = jQuery('#'+componentId.replace(new RegExp(':', 'g'), '\\:')).position();
		O$.setPageScrollPos({x:pos.left, y:pos.top})
	},
	updateTextFields : function(input_field) {
		var input_selector = 'input[type=text], input[type=password], input[type=email], input[type=url], input[type=tel], input[type=number], input[type=search], textarea';
		if (input_field != null || typeof input_field != 'undefined')
			input_selector = input_field;

		jQuery(input_selector)
				.each(
						function(index, element) {
							if (jQuery(element).val().length > 0
									|| element.autofocus
									|| jQuery(this).attr('placeholder') !== undefined
									|| jQuery(element)[0].validity.badInput === true) {
								jQuery(this).siblings('label, i').addClass(
										'active');
							} else {
								jQuery(this).siblings('label, i').removeClass(
										'active');
							}

						});

		jQuery(document).on('focus', input_selector, function() {
			jQuery(this).siblings('label, i').addClass('active');
		});

		jQuery(document)
				.on(
						'blur',
						input_selector,
						function() {
							var $inputElement = jQuery(this);
							if ($inputElement.val().length === 0
									&& $inputElement.attr('placeholder') === undefined) {
								$inputElement.siblings('label, i').removeClass(
										'active');
							}

							if ($inputElement.val().length === 0
									&& $inputElement.attr('placeholder') !== undefined) {
								$inputElement.siblings('i').removeClass(
										'active');
							}
							// validate_field($inputElement);
						});

	},
	notification : function(msg, severity) {
		jedlab.removeNotification();
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
	writePagingStorage : function() {
		var p = sessionStorage.getItem("paging");
		if (p == null)
			sessionStorage.setItem("paging", 1);
		else
			sessionStorage.setItem("paging", parseInt(p) + 1);

	},
	readPagingStorage : function() {
		return parseInt(sessionStorage.getItem("paging"));
	},
	resetPaging : function() {
		sessionStorage.setItem("paging", 0);
	},
	showLoading : function() {
		jQuery("#dimmerLoader").dimmer('show');
	},
	hideLoading : function() {
		jQuery("#dimmerLoader").dimmer('hide');
	}

};

var api = {
	project : {
		create : context.restCtx + "/projects",
		select : context.restCtx + "/projects"
	},
	task : {
		create : context.restCtx + "/tasks",
		count : context.restCtx + "/tasks/count",
		get : context.restCtx + "/tasks/{id}",
		comments : context.restCtx + "/tasks/{id}/comments"
	},
	team : {
		create : context.restCtx + "/teams"
	},
	teamMember : {
		create : context.restCtx + "/teamMembers"
	},
	comment : {
		create : context.restCtx + "/comments"
	}
};

var Project = Class.create({
	initialize : function(projectName) {
		this.projectName = projectName;
	},
	created : function(transport) {
		var location = transport.getResponseHeader('Location');
		jQuery("#btnProjectAction").removeClass("loading");
		jQuery('#createProjectModal').modal('hide');
		jQuery("#projNameInput").val('');
		jedlab.GetRequest(location, function(t) {
			var resp = t.responseJSON;
			var template = Handlebars.templates.projectItem(resp);
			jQuery("#projectListPanel").prepend(template);
			jQuery('.eyeBlock').popup();
			jQuery('.deleteBlock').popup();
			jedlab.notification(msgContext.created, 'infomsg');
		});
	},
	select : function(transport) {
		var json = '{"result":' + transport.responseText + '}';
		var template = Handlebars.templates.projectList(JSON.parse(json));
		jQuery("#projectListPanel").prepend(template);
		jQuery('.eyeBlock').popup();
		jQuery('.deleteBlock').popup();

	},
	fail : function(transport) {
		jQuery("#btnProjectAction").removeClass("loading");
		jQuery('#createProjectModal').modal('hide');
		jQuery("#projNameInput").val('');
		var msg = JSON.parse(transport.responseText);
		jedlab.notification(msg.errors["0"].msg, 'errormsg');
	}
});

var Task = Class.create({
	initialize : function(taskName) {
		this.taskName = taskName;
	},
	created : function(transport) {
		var location = transport.getResponseHeader('Location');
		jQuery("#btnCreateTask").removeClass("loading");
		jQuery("#taskInput").val('');
		jedlab.GetRequest(location, function(t) {
			var resp = t.responseJSON;
			var template = Handlebars.templates.taskItem(resp);
			jQuery("#taskTableBody").prepend(template);
			jedlab.notification(msgContext.created, 'infomsg');
		});
		jedlab.GetRequest(api.task.count, function(t) {
			var resp = t.responseText;
			jQuery("#taskCntSpan").html(resp);
		});
	},
	select : function(transport) {

	},
	fail : function(transport) {
		var msg = JSON.parse(transport.responseText);
		jedlab.notification(msg.errors["0"].msg, 'errormsg');
	}
});

var TeamMember = Class.create({
	initialize : function(teamId, email) {
		this.teamId = teamId;
		this.email = email;
	},
	created : function(transport) {
		var resp = transport.responseJSON;
		var teamMemberArea = jQuery("#teamMemberArea_" + resp.teamId);
		var template = Handlebars.templates.teamMemberItem(resp);
		teamMemberArea.prepend(template);
		jQuery("#teamMemberModal").modal('hide');
		jQuery("#btnTeamMemberAction").removeClass("loading");
	},
	select : function(transport) {

	},
	fail : function(transport) {
		var msg = JSON.parse(transport.responseText);
		jedlab.notification(msg.errors["0"].msg, 'errormsg');
		jQuery("#btnTeamMemberCancel").click();
	}
});

var Team = Class.create({
	initialize : function(name) {
		this.name = name;
	},
	created : function(transport) {
		var location = transport.getResponseHeader('Location');
		jedlab.GetRequest(location, function(t) {
			var resp = t.responseJSON;
			var template = Handlebars.templates.teamItem(resp);
			jQuery("#teamCards").prepend(template);
			jedlab.notification(msgContext.created, 'infomsg');
		});

	},
	select : function(transport) {

	},
	fail : function(transport) {
		var msg = JSON.parse(transport.responseText);
		jedlab.notification(msg.errors["0"].msg, 'errormsg');
		jQuery("#btnTeamMemberCancel").click();
	}
});

var Comment = Class.create({
	initialize : function(taskId, content) {
		this.taskId = taskId;
		this.content = content;
	},
	created : function(transport) {
		var location = transport.getResponseHeader('Location');
		jedlab.GetRequest(location, function(t) {
			var resp = t.responseJSON;
			var template = Handlebars.templates.commentItem(resp);
			jQuery("#cmList").prepend(template);
			jedlab.notification(msgContext.created, 'infomsg');
		});

	},
	select : function(transport) {

	},
	fail : function(transport) {
		var msg = JSON.parse(transport.responseText);
		jedlab.notification(msg.errors["0"].msg, 'errormsg');
		jQuery("#btnTeamMemberCancel").click();
	}
});

/*
 * Inline Text Editing 1.3 April 26, 2010 Corey Hart @
 * http://www.codenothing.com
 */
(function(jQuery, undefined) {

	jQuery.fn.inlineEdit = function(options) {
		return this
				.each(function() {
					// Settings and local cache
					var self = this, $main = jQuery(self), original, pk = jQuery(
							self).data("pk"), url = jQuery(self).data("url"), settings = jQuery
							.extend(
									{
										href : '/',
										requestType : 'POST',
										html : true,
										load : undefined,
										display : '.display',
										form : '.form',
										text : '.text',
										save : '.save',
										cancel : '.cancel',
										revert : '.revert',
										loadtxt : 'Loading...',
										hover : undefined,
										postVar : 'text',
										postData : {},
										dataType : 'text',
										contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
										postFormat : undefined
									}, options || {}, jQuery.metadata ? $main
											.metadata() : {}),

					// Cache All Selectors
					$display = $main.find(settings.display), $form = $main
							.find(settings.form), $text = $form
							.find(settings.text), $save = $form
							.find(settings.save), $revert = $form
							.find(settings.revert), $cancel = $form
							.find(settings.cancel);
					settings.postData.id = pk;
					settings.href = url;

					// Make sure the plugin only get initialized once
					if (jQuery.data(self, 'inline-edit') === true) {
						return;
					}
					jQuery.data(self, 'inline-edit', true);

					// Prevent sending form submission
					$form.bind('submit.inline-edit', function() {
						$save.trigger('click.inline-edit');
						return false;
					});

					// Display Actions
					$display.bind('click.inline-edit', function() {
						$display.hide();
						$form.show();

						if (settings.html) {
							if (original === undefined) {
								original = $display.html();
							}

							$text.val(original).focus();
						} else if (original === undefined) {
							original = $text.val();
						}

						return false;
					}).bind('mouseenter.inline-edit', function() {
						$display.addClass(settings.hover);
					}).bind('mouseleave.inline-edit', function() {
						$display.removeClass(settings.hover);
					});

					// Add revert handler
					$revert.bind('click.inline-edit', function() {
						$text.val(original || '').focus();
						return false;
					});

					// Cancel Actions
					$cancel.bind('click.inline-edit', function() {
						$form.hide();
						$display.show();

						// Remove hover action if stalled
						if ($display.hasClass(settings.hover)) {
							$display.removeClass(settings.hover);
						}

						return false;
					});

					// Save Actions
					$save.bind('click.inline-edit', function(event) {
						settings.postData[settings.postVar] = $text.val();
						$form.hide();
						$display.html(settings.loadtxt).show();

						if ($display.hasClass(settings.hover)) {
							$display.removeClass(settings.hover);
						}

						jQuery.ajax({
							url : settings.href,
							type : settings.requestType,
							data : settings.postFormat ? settings.postFormat
									.call($main, event, {
										settings : settings,
										postData : settings.postData
									}) : settings.postData,
							dataType : settings.dataType,
							contentType : settings.contentType,
							success : function(response) {
								original = undefined;

								if (settings.load) {
									settings.load.call($display, event, {
										response : response,
										settings : settings,
										elm : $display
									});
								} else {
									$display.html(response);
								}
							}
						});

						return false;
					});
				});
	};

})(jQuery);
