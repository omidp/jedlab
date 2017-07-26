
(function() {

	// getElementById
	function jqid(id) {
		return document.getElementById(id);
	}


	// output information
	function Output(msg) {
		var m = jqid("uploadInfo");
		m.innerHTML = msg + m.innerHTML;
	}


	// file drag hover
	function FileDragHover(e) {
		e.stopPropagation();
		e.preventDefault();
		e.target.className = (e.type == "dragover" ? "hover" : "");
	}


	// file selection
	function FileSelectHandler(e) {

		// cancel event and hover styling
		FileDragHover(e);

		// fetch FileList object
		var files = e.target.files || e.dataTransfer.files;
		// process all File objects
		for (var i = 0, f; f = files[i]; i++) {
			ParseFile(f);
			UploadFile(f);
		}

	}


	// output file information
	function ParseFile(file) {

		Output(
			"<p>File information: <strong>" + file.name +
			"</strong> type: <strong>" + file.type +
			"</strong> size: <strong>" + file.size +
			"</strong> bytes</p>"
		);

		

	}


	
	function UploadFile(file) {
		
		if(jqid("fieldName:cname").value == '' || jqid("fieldDur:dur").value == '')
		{
			common.notification("all fields required", "errormsg");
			return;
		}
		if (!((file.type == "video/mp4" || file.type == "video/mkv"))) 
		{
			common.notification("mp4/mkv is supported", "errormsg");
			return;
		}
		var xhr = new XMLHttpRequest();
		if (xhr.upload && ((file.type == "video/mp4") || file.type == "video/mkv")) {
			jqid("fieldUpload").style.display = "none";
			// create progress bar
			var o = jqid("progress");
			var progress = o.appendChild(document.createElement("p"));
			progress.appendChild(document.createTextNode("upload " + file.name));


			// progress bar
			xhr.upload.addEventListener("progress", function(e) {
				var pc = parseInt(100 - (e.loaded / e.total * 100));
				progress.style.backgroundPosition = pc + "% 0";
			}, false);

			// file received/failed
			xhr.onreadystatechange = function(e) {
				if (xhr.readyState == 4) {
					progress.className = (xhr.status == 200 ? "success" : "failure");
					if(xhr.status == 200)
						jqid("btnSelfAddress").click();					
				}else{
					//common.notification("error occured, try again", "errormsg");
					progress.className = "failure";
					jQuery("#progress").empty();
					jqid("fieldUpload").style.display = "block";
				}
			};

			// start upload
			xhr.open("POST", jqid("upload").action, true);
			xhr.setRequestHeader("X_FILENAME", file.name);
			xhr.setRequestHeader("X_CNAME", jqid("fieldName:cname").value);
			xhr.setRequestHeader("X_DUR", jqid("fieldDur:dur").value);
			xhr.setRequestHeader("X_CID", jqid("courseId").value);
			xhr.send(file);

		}

	}


	// initialize
	function Init() {

		var fileselect = jqid("fileselect"),
			filedrag = jqid("filedrag"),
			submitbutton = jqid("submitbutton");

		// file select
		fileselect.addEventListener("change", FileSelectHandler, false);

		// is XHR2 available?
		var xhr = new XMLHttpRequest();
		if (xhr.upload) {

			// file drop
			filedrag.addEventListener("dragover", FileDragHover, false);
			filedrag.addEventListener("dragleave", FileDragHover, false);
			filedrag.addEventListener("drop", FileSelectHandler, false);
			filedrag.style.display = "block";

			// remove submit button
			submitbutton.style.display = "none";
		}

	}

	// call initialization file
	if (window.File && window.FileList && window.FileReader) {
		Init();
	}


})();
