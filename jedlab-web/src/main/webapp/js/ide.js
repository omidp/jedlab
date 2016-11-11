jQuery(document).ready(function(){
				var browserHeight = document.documentElement.clientHeight;
		        editor.getWrapperElement().style.height = (browserHeight-(310+10))+'px';
		        editor.refresh();
			});

			editor = CodeMirror.fromTextArea(document.getElementById("formCode:code"), {
			    lineNumbers: true,
			    styleActiveLine: true,
			    matchBrackets: true,
			    theme:"eclipse",
			    mode: "text/x-java",
			      extraKeys: {
			        "F12": function(cm) {
			          cm.setOption("fullScreen", !cm.getOption("fullScreen"));
			          editor.refresh();
			        },
			        "F11": function() {
			            if ((document.fullScreenElement && document.fullScreenElement !== null) || 
			                (!document.mozFullScreen && !document.webkitIsFullScreen)) {
			              if (document.documentElement.requestFullScreen) {
			                document.documentElement.requestFullScreen();
			              } else if (document.documentElement.mozRequestFullScreen) {
			                document.documentElement.mozRequestFullScreen();
			              } else if (document.documentElement.webkitRequestFullScreen) {
			                document.documentElement.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);  
			              }
			            }
			            else {
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
			        "F4": function() {
			          //openApplet();
			        },
			        "F3": function() {
			          //getImportPaste();
			        },
			        "F2": function() {
			          // document.forms[0].submit();
			          //postForCompile();
			        }
			      }
			    });