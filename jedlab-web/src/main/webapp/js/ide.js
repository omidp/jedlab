jQuery(document).ready(function(){
				var browserHeight = document.documentElement.clientHeight;
				jQuery("#all").height(browserHeight);				
		        editor.getWrapperElement().style.height = (browserHeight-(310+10))+'px';
		        editor.refresh();
			});

editor = CodeMirror
		.fromTextArea(
				document.getElementById("formCode:code"),
				{
					lineNumbers : true,
					styleActiveLine : true,
					matchBrackets : true,
					theme : "eclipse",
					mode : "text/x-java",
					extraKeys : {
						"F12" : function(cm) {
							cm.setOption("fullScreen", !cm
									.getOption("fullScreen"));
							editor.refresh();
						},
						"F11" : function() {
							if ((document.fullScreenElement && document.fullScreenElement !== null)
									|| (!document.mozFullScreen && !document.webkitIsFullScreen)) {
								if (document.documentElement.requestFullScreen) {
									document.documentElement
											.requestFullScreen();
								} else if (document.documentElement.mozRequestFullScreen) {
									document.documentElement
											.mozRequestFullScreen();
								} else if (document.documentElement.webkitRequestFullScreen) {
									document.documentElement
											.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);
								}
							} else {
								if (document.cancelFullScreen) {
									document.cancelFullScreen();
								} else if (document.mozCancelFullScreen) {
									document.mozCancelFullScreen();
								} else if (document.webkitCancelFullScreen) {
									document.webkitCancelFullScreen();
								}
							}
							editor.refresh();
						},
						"F4" : function() {
							// openApplet();
						},
						"F3" : function() {
							// getImportPaste();
						},
						"F2" : function() {
							// document.forms[0].submit();
							// postForCompile();
						}
					}
				});

var embedded = false;
var isLimitedWidth = window.matchMedia("only screen and (max-width: 760px)").matches;
var isLimitedHeight = window.matchMedia("only screen and (max-height: 760px)").matches;
var limitWidth = isLimitedWidth || embedded;
var limitHeight = isLimitedHeight || embedded;
var isMobile = isLimitedWidth || isLimitedHeight;
jQuery(document).ready(function() {
	// Create the main layout
	jQuery('#all').w2layout({
		name : 'all',
		padding : 4,
		panels : getLayoutPanels()
	});

	w2ui.all.on({
		type : "resize",
		execute : "after"
	}, function(event, data) {
		// handleResizeMain(event, data);
		handleResizeSidebar(event, data);
	});
	var t = sessionStorage.getItem("tabSelectedId");
	if (t == null)
		t = "tab1";
	selectTab(t);
	jQuery('#editortabs').w2tabs({
		name : 'editortabs',
		active : t,
		tabs : [ {
			id : 'tab1',
			caption : descriptionMsg
		}, {
			id : 'tab2',
			caption : 'IDE'
		},{
			id : 'tab3',
			caption : 'Test cases'
		} ],
		onClick : function(event) {
			selectTab(event.target);
		}
	});
	
	w2ui.all.content("main", jqContent(jQuery("#core-page")));
	w2ui.all.content("preview", jqContent(jQuery("#outputArea")));
	

});

function selectTab(id) {
	jQuery('#core-page .tab').hide();
	jQuery('#core-page #' + id).show();
	if(id == 'tab2')
	{		
		editor.refresh();
	}
	sessionStorage.setItem("tabSelectedId", id);
}

function jqContent(jqElem) {
	return {
		render : function() {
			jQuery(this.box).empty();
			jQuery(this.box).append(jqElem);
		}
	}
}

function handleResizeSidebar(event, data) {
	var dx = jQuery("#sidebar").innerWidth()
			- jQuery("#sidebarblock").outerWidth();
	if (dx >= 0) {
		jQuery("#sidebarhandle").css("right", dx + "px");
	}
}

function getLayoutPanels() {
	var pstyle = 'border: 0px; padding: 0px; overflow: hidden;';
	var zstyle = 'border: 1px solid #dfdfdf; padding: 0px; overflow: hidden;';
	return [ {
		type : 'main',
		minSize : 180,
		style : zstyle,
		resizable : true,
		toolbar : {
			items : getToolbarItems(),
			onClick : handleToolbarClick
		}
	}, {
		type : 'preview',
		size : "30%",
		minSize : 100,
		resizable : true,
		style : zstyle,
		title : 'Output'
	}, {
		type : 'bottom',
		size : 67,
		style : zstyle,
		hidden : limitHeight
	} ];
}

function getToolbarItems() {
	return [ {
		type : 'break',
		id : 'break0',
		hidden : embedded
	}, {
		type : 'button',
		id : 'run',
		caption : 'Run',
		hint : 'Compile & execute',
		icon : 'fa fa-play'
	}, {
		type : 'button',
		id : 'compile',
		caption : 'Compile',
		hint : 'Compile',
		icon : 'fa fa-remove'
	}, {
		type : 'button',
		id : 'share',
		caption : 'Share',
		hint : 'Share the code on JEDLab',
		icon : 'fa fa-share',
		hidden : embedded
	}, {
		type : 'break',
		id : 'break1',
		hidden : embedded
	}, {
		type : 'spacer'
	}

	];
}

function handleToolbarClick(event) {
	if (event.target == "run") {
		jQuery("#formCode\\:codeBtnExec").click();
	}
	
	if (event.target == "compile") {
		jQuery("#formCode\\:codeBtn").click();
	}
	
	if (event.target == "share") {
		jQuery("#formCode\\:codeBtnGist").click();
	}
	
	

}