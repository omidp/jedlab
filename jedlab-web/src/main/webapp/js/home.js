jQuery(document).ready(function() {
	localStorage.setItem("scrollIndex", 0);
});

var jedlab = {
	init : function() {
		jQuery("#featureContent, .classroom-maximum, #newsletter").mousewheel(
				function(event) {
					if (event.deltaY < 0) {
						jQuery(this).stop();
						jedlab.down();
					}
					if (event.deltaY > 0) {
						jQuery(this).stop();
						jedlab.up();
					}
				});

		jQuery(document).keydown(function(e) {
			switch (e.which) {

			case 40: // down
				jedlab.down();
				break;

			case 38: // up
				jedlab.up();
				break;

			default:
				return; // exit this handler for other keys
			}
			e.preventDefault(); // prevent the default action (scroll / move
								// caret)
		});
	},
	scrollerObj : function() {
		return [ "top-scroll", "featureContent", "newsletter" ];
	},
	down : function() {
		var index = localStorage.getItem("scrollIndex") == null ? 0
				: parseInt(localStorage.getItem("scrollIndex"));
		var size = parseInt(jedlab.scrollerObj().length);
		if (index < 0)
			index = 0;
		if (index >= (size - 1))
			index = size - 2;
		var idx = index + 1;
		//
		if(idx == 2)
		{
			 jQuery("#typed").typed({startDelay:100,
		            stringsElement: jQuery('#typed-strings')
		        });
		}
		//
		var jqId = "#" + jedlab.scrollerObj()[idx];
		var obj = jQuery(jqId);
		jQuery('html, body').stop().animate({
			scrollTop : obj.offset().top
		}, 'slow');
		localStorage.setItem("scrollIndex", idx);
	},
	up : function() {
		var index = localStorage.getItem("scrollIndex") == null ? 1
				: parseInt(localStorage.getItem("scrollIndex"));
		var size = parseInt(jedlab.scrollerObj().length);
		if (index < 1)
			index = 1;
		if (index >= size)
			index = jedlab.scrollerObj().length;
		var idx = index - 1;
		var jqId = "#" + jedlab.scrollerObj()[idx];
		var obj = jQuery(jqId);
		jQuery('html, body').stop().animate({
			scrollTop : obj.offset().top
		}, 'slow');
		localStorage.setItem("scrollIndex", idx);
	}
}