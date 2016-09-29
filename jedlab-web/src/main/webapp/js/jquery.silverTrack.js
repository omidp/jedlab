/*
 * jQuery Easing v1.3 - http://gsgd.co.uk/sandbox/jquery/easing/
 *
 * Uses the built in easing capabilities added In jQuery 1.1
 * to offer multiple easing options
 *
 * TERMS OF USE - jQuery Easing
 * 
 * Open source under the BSD License. 
 * 
 * Copyright © 2008 George McGinley Smith
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of 
 * conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list 
 * of conditions and the following disclaimer in the documentation and/or other materials 
 * provided with the distribution.
 * 
 * Neither the name of the author nor the names of contributors may be used to endorse 
 * or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 *  COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 *  GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 *
*/

// t: current time, b: begInnIng value, c: change In value, d: duration
jQuery.easing['jswing'] = jQuery.easing['swing'];

jQuery.extend( jQuery.easing,
{
	def: 'easeOutQuad',
	swing: function (x, t, b, c, d) {
		//alert(jQuery.easing.default);
		return jQuery.easing[jQuery.easing.def](x, t, b, c, d);
	},
	easeInQuad: function (x, t, b, c, d) {
		return c*(t/=d)*t + b;
	},
	easeOutQuad: function (x, t, b, c, d) {
		return -c *(t/=d)*(t-2) + b;
	},
	easeInOutQuad: function (x, t, b, c, d) {
		if ((t/=d/2) < 1) return c/2*t*t + b;
		return -c/2 * ((--t)*(t-2) - 1) + b;
	},
	easeInCubic: function (x, t, b, c, d) {
		return c*(t/=d)*t*t + b;
	},
	easeOutCubic: function (x, t, b, c, d) {
		return c*((t=t/d-1)*t*t + 1) + b;
	},
	easeInOutCubic: function (x, t, b, c, d) {
		if ((t/=d/2) < 1) return c/2*t*t*t + b;
		return c/2*((t-=2)*t*t + 2) + b;
	},
	easeInQuart: function (x, t, b, c, d) {
		return c*(t/=d)*t*t*t + b;
	},
	easeOutQuart: function (x, t, b, c, d) {
		return -c * ((t=t/d-1)*t*t*t - 1) + b;
	},
	easeInOutQuart: function (x, t, b, c, d) {
		if ((t/=d/2) < 1) return c/2*t*t*t*t + b;
		return -c/2 * ((t-=2)*t*t*t - 2) + b;
	},
	easeInQuint: function (x, t, b, c, d) {
		return c*(t/=d)*t*t*t*t + b;
	},
	easeOutQuint: function (x, t, b, c, d) {
		return c*((t=t/d-1)*t*t*t*t + 1) + b;
	},
	easeInOutQuint: function (x, t, b, c, d) {
		if ((t/=d/2) < 1) return c/2*t*t*t*t*t + b;
		return c/2*((t-=2)*t*t*t*t + 2) + b;
	},
	easeInSine: function (x, t, b, c, d) {
		return -c * Math.cos(t/d * (Math.PI/2)) + c + b;
	},
	easeOutSine: function (x, t, b, c, d) {
		return c * Math.sin(t/d * (Math.PI/2)) + b;
	},
	easeInOutSine: function (x, t, b, c, d) {
		return -c/2 * (Math.cos(Math.PI*t/d) - 1) + b;
	},
	easeInExpo: function (x, t, b, c, d) {
		return (t==0) ? b : c * Math.pow(2, 10 * (t/d - 1)) + b;
	},
	easeOutExpo: function (x, t, b, c, d) {
		return (t==d) ? b+c : c * (-Math.pow(2, -10 * t/d) + 1) + b;
	},
	easeInOutExpo: function (x, t, b, c, d) {
		if (t==0) return b;
		if (t==d) return b+c;
		if ((t/=d/2) < 1) return c/2 * Math.pow(2, 10 * (t - 1)) + b;
		return c/2 * (-Math.pow(2, -10 * --t) + 2) + b;
	},
	easeInCirc: function (x, t, b, c, d) {
		return -c * (Math.sqrt(1 - (t/=d)*t) - 1) + b;
	},
	easeOutCirc: function (x, t, b, c, d) {
		return c * Math.sqrt(1 - (t=t/d-1)*t) + b;
	},
	easeInOutCirc: function (x, t, b, c, d) {
		if ((t/=d/2) < 1) return -c/2 * (Math.sqrt(1 - t*t) - 1) + b;
		return c/2 * (Math.sqrt(1 - (t-=2)*t) + 1) + b;
	},
	easeInElastic: function (x, t, b, c, d) {
		var s=1.70158;var p=0;var a=c;
		if (t==0) return b;  if ((t/=d)==1) return b+c;  if (!p) p=d*.3;
		if (a < Math.abs(c)) { a=c; var s=p/4; }
		else var s = p/(2*Math.PI) * Math.asin (c/a);
		return -(a*Math.pow(2,10*(t-=1)) * Math.sin( (t*d-s)*(2*Math.PI)/p )) + b;
	},
	easeOutElastic: function (x, t, b, c, d) {
		var s=1.70158;var p=0;var a=c;
		if (t==0) return b;  if ((t/=d)==1) return b+c;  if (!p) p=d*.3;
		if (a < Math.abs(c)) { a=c; var s=p/4; }
		else var s = p/(2*Math.PI) * Math.asin (c/a);
		return a*Math.pow(2,-10*t) * Math.sin( (t*d-s)*(2*Math.PI)/p ) + c + b;
	},
	easeInOutElastic: function (x, t, b, c, d) {
		var s=1.70158;var p=0;var a=c;
		if (t==0) return b;  if ((t/=d/2)==2) return b+c;  if (!p) p=d*(.3*1.5);
		if (a < Math.abs(c)) { a=c; var s=p/4; }
		else var s = p/(2*Math.PI) * Math.asin (c/a);
		if (t < 1) return -.5*(a*Math.pow(2,10*(t-=1)) * Math.sin( (t*d-s)*(2*Math.PI)/p )) + b;
		return a*Math.pow(2,-10*(t-=1)) * Math.sin( (t*d-s)*(2*Math.PI)/p )*.5 + c + b;
	},
	easeInBack: function (x, t, b, c, d, s) {
		if (s == undefined) s = 1.70158;
		return c*(t/=d)*t*((s+1)*t - s) + b;
	},
	easeOutBack: function (x, t, b, c, d, s) {
		if (s == undefined) s = 1.70158;
		return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
	},
	easeInOutBack: function (x, t, b, c, d, s) {
		if (s == undefined) s = 1.70158; 
		if ((t/=d/2) < 1) return c/2*(t*t*(((s*=(1.525))+1)*t - s)) + b;
		return c/2*((t-=2)*t*(((s*=(1.525))+1)*t + s) + 2) + b;
	},
	easeInBounce: function (x, t, b, c, d) {
		return c - jQuery.easing.easeOutBounce (x, d-t, 0, c, d) + b;
	},
	easeOutBounce: function (x, t, b, c, d) {
		if ((t/=d) < (1/2.75)) {
			return c*(7.5625*t*t) + b;
		} else if (t < (2/2.75)) {
			return c*(7.5625*(t-=(1.5/2.75))*t + .75) + b;
		} else if (t < (2.5/2.75)) {
			return c*(7.5625*(t-=(2.25/2.75))*t + .9375) + b;
		} else {
			return c*(7.5625*(t-=(2.625/2.75))*t + .984375) + b;
		}
	},
	easeInOutBounce: function (x, t, b, c, d) {
		if (t < d/2) return jQuery.easing.easeInBounce (x, t*2, 0, c, d) * .5 + b;
		return jQuery.easing.easeOutBounce (x, t*2-d, 0, c, d) * .5 + c*.5 + b;
	}
});

/*
 *
 * TERMS OF USE - EASING EQUATIONS
 * 
 * Open source under the BSD License. 
 * 
 * Copyright © 2001 Robert Penner
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of 
 * conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list 
 * of conditions and the following disclaimer in the documentation and/or other materials 
 * provided with the distribution.
 * 
 * Neither the name of the author nor the names of contributors may be used to endorse 
 * or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 *  COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 *  GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 *
 */
/* Modernizr 2.7.1 (Custom Build) | MIT & BSD
 * Build: http://modernizr.com/download/#-csstransforms3d-mq-teststyles-testprop-testallprops-prefixes-domprefixes
 */
;window.Modernizr=function(a,b,c){function z(a){i.cssText=a}function A(a,b){return z(l.join(a+";")+(b||""))}function B(a,b){return typeof a===b}function C(a,b){return!!~(""+a).indexOf(b)}function D(a,b){for(var d in a){var e=a[d];if(!C(e,"-")&&i[e]!==c)return b=="pfx"?e:!0}return!1}function E(a,b,d){for(var e in a){var f=b[a[e]];if(f!==c)return d===!1?a[e]:B(f,"function")?f.bind(d||b):f}return!1}function F(a,b,c){var d=a.charAt(0).toUpperCase()+a.slice(1),e=(a+" "+n.join(d+" ")+d).split(" ");return B(b,"string")||B(b,"undefined")?D(e,b):(e=(a+" "+o.join(d+" ")+d).split(" "),E(e,b,c))}var d="2.7.1",e={},f=b.documentElement,g="modernizr",h=b.createElement(g),i=h.style,j,k={}.toString,l=" -webkit- -moz- -o- -ms- ".split(" "),m="Webkit Moz O ms",n=m.split(" "),o=m.toLowerCase().split(" "),p={},q={},r={},s=[],t=s.slice,u,v=function(a,c,d,e){var h,i,j,k,l=b.createElement("div"),m=b.body,n=m||b.createElement("body");if(parseInt(d,10))while(d--)j=b.createElement("div"),j.id=e?e[d]:g+(d+1),l.appendChild(j);return h=["&#173;",'<style id="s',g,'">',a,"</style>"].join(""),l.id=g,(m?l:n).innerHTML+=h,n.appendChild(l),m||(n.style.background="",n.style.overflow="hidden",k=f.style.overflow,f.style.overflow="hidden",f.appendChild(n)),i=c(l,a),m?l.parentNode.removeChild(l):(n.parentNode.removeChild(n),f.style.overflow=k),!!i},w=function(b){var c=a.matchMedia||a.msMatchMedia;if(c)return c(b).matches;var d;return v("@media "+b+" { #"+g+" { position: absolute; } }",function(b){d=(a.getComputedStyle?getComputedStyle(b,null):b.currentStyle)["position"]=="absolute"}),d},x={}.hasOwnProperty,y;!B(x,"undefined")&&!B(x.call,"undefined")?y=function(a,b){return x.call(a,b)}:y=function(a,b){return b in a&&B(a.constructor.prototype[b],"undefined")},Function.prototype.bind||(Function.prototype.bind=function(b){var c=this;if(typeof c!="function")throw new TypeError;var d=t.call(arguments,1),e=function(){if(this instanceof e){var a=function(){};a.prototype=c.prototype;var f=new a,g=c.apply(f,d.concat(t.call(arguments)));return Object(g)===g?g:f}return c.apply(b,d.concat(t.call(arguments)))};return e}),p.csstransforms3d=function(){var a=!!F("perspective");return a&&"webkitPerspective"in f.style&&v("@media (transform-3d),(-webkit-transform-3d){#modernizr{left:9px;position:absolute;height:3px;}}",function(b,c){a=b.offsetLeft===9&&b.offsetHeight===3}),a};for(var G in p)y(p,G)&&(u=G.toLowerCase(),e[u]=p[G](),s.push((e[u]?"":"no-")+u));return e.addTest=function(a,b){if(typeof a=="object")for(var d in a)y(a,d)&&e.addTest(d,a[d]);else{a=a.toLowerCase();if(e[a]!==c)return e;b=typeof b=="function"?b():b,typeof enableClasses!="undefined"&&enableClasses&&(f.className+=" "+(b?"":"no-")+a),e[a]=b}return e},z(""),h=j=null,e._version=d,e._prefixes=l,e._domPrefixes=o,e._cssomPrefixes=n,e.mq=w,e.testProp=function(a){return D([a])},e.testAllProps=F,e.testStyles=v,e}(this,this.document);

/*!
 * ResponsiveHub - JavaScript goodies for Responsive Design
 * https://github.com/globocom/responsive-hub
 * version: 0.4.0
 */

(function ($, window, document) {

  $.responsiveHub = function(settings) {
    if (typeof settings === "object") {
      ResponsiveHub.init(settings);

    } else if (typeof settings === "string") {
      var args = [].splice.call(arguments, 0, arguments.length);
      var methodName = args.splice(0, 1)[0];

      if (ResponsiveHub[methodName]) {
        return ResponsiveHub[methodName].apply(ResponsiveHub, args);
      } else {
        if (window.console && window.console.log) {
          console.log("[ResponsiveHub] Undefined method '" + methodName + "'");
        }
      }
    }
  };

  var ResponsiveHub = {
    currentLayout: null,
    resizeBound: false,
    hasMediaQuerySupport: false,
    windowObj: null,
    loaded: false,
    resizeStopDelay: 500,
    _resizeTimer: null,

    init: function(settings) {
      if (!this.loaded) {
        this.loaded = true;
        this.windowObj = this._getWindow();
        this.layouts = settings.layouts;
        this.defaultLayout = settings.defaultLayout;

        this._boot();
      }
    },

    self: function() {
      return this;
    },

    width: function() {
      return this.windowObj.width();
    },

    layout: function() {
      if (!this.hasMediaQuerySupport) {
        return this.defaultLayout;
      }

      var widths = [];
      var keys = this._keys(this.layouts);
      for (var j in keys) {
        widths.push(parseInt(keys[j], 10));
      }

      widths.sort(function(a,b){return b - a});
      var width = this.width();
      for (var i in widths) {
        var w = widths[i];
        if (width >= w) return this.layouts[w];
      }

      return this.layouts[widths[widths.length - 1]];
    },

    ready: function(layout, callback) {
      this._bind("responsiveready", layout, callback);
    },

    change: function(layout, callback) {
      this._bind("responsivechange", layout, callback);
    },

    isResizing: function() {
      return this._resizeTimer !== null;
    },

    resizeStart: function(callback) {
      this.windowObj.bind("resizeStart", callback);
    },

    resizeStop: function(callback) {
      this.windowObj.bind("resizeStop", callback);
    },

    isTouch: function() {
      var wnd = (this.windowObj || this._getWindow()).get(0);
      return !!(('ontouchstart' in wnd) || (wnd.DocumentTouch && wnd.document instanceof DocumentTouch));
    },

    hasFlash: function() {
      try { return !! new ActiveXObject('ShockwaveFlash.ShockwaveFlash'); } catch(e1) {}
      var mimeType = this._mimeTypeFlash();
      return !! (mimeType && mimeType.enabledPlugin);
    },

    _updateLayout: function() {
      var self = $.responsiveHub("self");
      var layout = self.layout();

      if (layout != self.currentLayout) {
        self.currentLayout = layout;
        self.windowObj.trigger("responsivechange" + layout, [self._newEvent()]);
      }
    },

    _resizeStartStop: function(event) {
      var self = $.responsiveHub("self");

      if (self._resizeTimer) {
        clearTimeout(self._resizeTimer);
      } else {
        self.windowObj.trigger("resizeStart", [event]);
      }

      self._resizeTimer = setTimeout(function() {
        self._resizeTimer = null;
        self.windowObj.trigger("resizeStop", [event]);
      }, self.resizeStopDelay);
    },

    _boot: function() {
      this.hasMediaQuerySupport = Modernizr.mq("only all");
      if (!this.resizeBound && this.hasMediaQuerySupport) {

        this.windowObj.bind("resize", this._updateLayout);
        this.windowObj.bind("resize", this._resizeStartStop);

        this.resizeBound = true;
      }

      if (!this.currentLayout) {
        this.currentLayout = this.layout();
        var readyEvent = "responsiveready" + this.currentLayout;

        this.windowObj.trigger(readyEvent, [this._newEvent()]);
        this.windowObj.unbind(readyEvent);
      }
    },

    _unbind: function() {
      $(window).unbind(".responsivehub");
    },

    _bind: function(namespace, layout, callback) {
      var self = this;
      var layouts = this._flatten(this._isArray(layout) ? layout : [layout]);
      var eventCallback = function(event, responsiveHubEvent) {
        callback(responsiveHubEvent);
      }

      $.each(layouts, function(index, value) {
        $(window).bind(namespace + value + ".responsivehub", eventCallback);
      });
    },

    _newEvent: function() {
      return {layout: this.currentLayout, touch: this.isTouch()};
    },

    // https://github.com/jiujitsumind/underscorejs/blob/master/underscore.js#L644
    _keys: Object.keys || function(obj) {
      var keys = [];
      for (var key in obj) if (obj.hasOwnProperty(key)) keys[keys.length] = key;
      return keys;
    },

    _flatten: function(array, shallow) {
      var self = this;
      var flatten = function(input, shallow, output) {
        for (var i = 0; i < input.length; i++) {
          var value = input[i];
          if (self._isArray(value)) {
            shallow ? output.push(value) : flatten(value, shallow, output);
          } else {
            output.push(value);
          }
        }
        return output;
      }

      return flatten(array, shallow, []);
    },

    _getWindow: function() {
      return $(window);
    },

    _mimeTypeFlash: function() {
      return navigator.mimeTypes["application/x-shockwave-flash"];
    },

    _isArray: Array.isArray || function(obj) {
      return Object.prototype.toString.call(obj) === '[object Array]';
    }
  };

})(jQuery, window, document);

/*!
 * jQuery SilverTrack
 * https://github.com/tulios/jquery.silver_track
 * version: 0.4.0
 */

(function ($, window, document) {

  var instanceName = "silverTrackInstance";

  $.fn.silverTrack = function(options) {
    var container = $(this);

    if (!container.data(instanceName)) {
      options = $.extend({}, $.fn.silverTrack.options, options);
      var instance = new SilverTrack(container, options);
      container.data(instanceName, instance);
      return instance;
    }

    return container.data(instanceName);
  };

  $.fn.isSilverTrackInstalled = function() {
    var container = $(this);
    return container.data(instanceName) != null;
  };

  $.fn.silverTrack.options = {
    perPage: 4,
    itemClass: "item",
    mode: "horizontal",
    autoHeight: false,
    cover: false,
    duration: 600,
    easing: "swing",
    /*
     * Args: movement, duration, easing, afterCallback
     * - easing and afterCallback may be optional
     * - movement will be {left: someValue} or {height: someValue}
     */
    animateFunction: null,
    animationAxis: "x"
  };

  var SilverTrack = function (container, options) {
    this.options = options;
    this.container = container;
    this.paginationEnabled = true;
    this.calculateTotalPages = true;
    this.currentPage = 1;
    this.totalPages = 1;
    this.plugins = [];

    this.reloadItems();
  };

  SilverTrack.prototype = {

    start: function() {
      if (this.options.animateFunction === null) {
        this._validateAnimationEasing();
      }

      this._executeAll("beforeStart");
      this._init();
      this._executeAll("afterStart");
    },

    /*
     * page: Number
     * opts: {animate: true|false}
     */
    goToPage: function(page, opts) {
      opts = $.extend({animate: true}, opts);

      var duration = opts.animate ? this.options.duration : 0;
      var useCover = this.options.cover && (page === 1);
      var direction = page > this.currentPage ? "next" : "prev";
      var items = useCover ? this._getCover() : this._calculateItemsForPagination(page);

      if (!this._canPaginate(page)) {
        return;
      }

      if (items.length > 0) {
        var shift = this._calculateItemPosition(items.get(0));
        var event = {name: direction, page: page, cover: useCover, items: items};

        if (items.length < this.options.perPage && !useCover && (this.isModeHorizontal() || this.isAxisY())) {
          shift -= this._calculateMaxShiftAvailable(items);
        }

        this._executeAll("beforePagination", [event]);
        this.currentPage = page;
        this.paginationEnabled = false;

        this._slide(shift, event, duration);
        this._adjustHeight(items, duration);
      }
    },

    next: function() {
      this.goToPage(this.currentPage + 1);
    },

    prev: function() {
      this.goToPage(this.currentPage - 1);
    },

    hasPrev: function() {
      return !(this.currentPage === 1);
    },

    hasNext: function() {
      return !(this.currentPage === this.totalPages || this.totalPages <= 1);
    },

    isModeHorizontal: function() {
      return this.options.mode === "horizontal";
    },

    isAxisY: function() {
      return this.options.animationAxis === "y"
    },

    /*
     * {
     *  page: Number,                // default: 1
     *  keepCurrentPage: true|false, // default: false
     *  animate: true|false          // default: false
     * }
     */
    restart: function(opts) {
      opts = $.extend({
        page: 1,
        keepCurrentPage: false,
        animate: false
      }, opts);

      if (opts.keepCurrentPage) {
        opts.page = this.currentPage;
      }

      this._executeAll("beforeRestart");
      this.container.css("height", "");
      this._getItems(true).css("top", "");

      this.paginationEnabled = true;
      this.currentPage = 1;

      this._init();
      this.goToPage(opts.page, {animate: opts.animate});
      this._executeAll("afterRestart");
    },

    install: function(plugin) {
      this.plugins.push(plugin);
      this._callFunction(plugin, "onInstall");
      return this;
    },

    reloadItems: function() {
      this._items = null;
    },

    updateTotalPages: function(totalPages) {
      this.calculateTotalPages = false;
      this.totalPages = this._abs(totalPages);
      this._executeAll("onTotalPagesUpdate");
    },

    findPluginByName: function(name) {
      for (var i = 0; i < this.plugins.length; i++) {
        var plugin = this.plugins[i];
        if (plugin.PluginName === name) {
          return plugin;
        }
      }

      return null;
    },

    _init: function() {
      this.animatedAttribute = this.isAxisY() ? "top" : "left";
      this._positionElements();
      if (this.calculateTotalPages) {
        this._calculateTotalPages();
      }
    },

    _getItems: function(ignoreCoverFilter) {
      if (!this._items) {
        this._items = $("." + this.options.itemClass, this.container);
      }

      return !ignoreCoverFilter && this.options.cover ? this._items.not(":first") : this._items;
    },

    _getCover: function() {
      return $("." + this.options.itemClass + ":first", this.container);
    },

    _canPaginate: function(page) {
      if (!this.paginationEnabled || page > this.totalPages || page < 1) {
        return false;
      }

      return true;
    },

    _slide: function(shift, event, duration) {
      var self = this;
      var movement = {};
      movement[this.animatedAttribute] = "-" + shift + "px";

      var afterCallback = function() {
        self.paginationEnabled = true;
        self._executeAll("afterAnimation", [event]);
      }

      this._executeAll("beforeAnimation", [event]);
      this._animate(movement, duration, afterCallback)
    },

    _adjustHeight: function(items, duration) {
      if (this.options.autoHeight === true) {
        var newHeight = 0;

        if (this.isModeHorizontal()) {
          newHeight = $(items[0]).outerHeight(true);

        } else {
          items.each(function(index, value) {
            newHeight += $(value).outerHeight(true);
          });
        }

        var event = {items: items, newHeight: newHeight};
        this._executeAll("beforeAdjustHeight", [event]);
        this._animate({"height": newHeight + "px"}, duration);
        this._executeAll("afterAdjustHeight", [event]);
      }
    },

    _animate: function(movement, duration, afterCallback) {
      var easing = this.options.easing;
      if (this.options.animateFunction !== null) {
        this.options.animateFunction(movement, duration, easing, afterCallback);

      } else {
        this.container.animate(movement, duration, easing, afterCallback);
      }
    },

    _positionElements: function() {
      this._calculateItemDimension();
      this._calculateCoverDimention();
      this.container.css(this.animatedAttribute, "0px");

      if (this.isModeHorizontal()) {
        this._positionHorizontal();

      } else {
        this._positionVertical();
      }
    },

    _positionHorizontal: function() {
      var width = 0;
      this._getItems(true).each(function(index, value) {
        var item = $(value);
        item.css({"left": width + "px"});
        width += item.outerWidth(true);
      });

      this.container.css("width", width + "px");
    },

    _positionVertical: function() {
      if (this.isAxisY()) {
        this._positionVerticalAxisY();

      } else {
        this._positionVerticalAxisX();
      }
    },

    _positionVerticalAxisX: function() {
      var width = 0;
      var height = 0;

      var perPage = this.options.perPage;
      var useCover = this.options.cover;
      var pageItem = 0;

      this._getItems(true).each(function(index, value) {
        var item = $(value);
        item.css({"top": height + "px", "left": width + "px"});
        pageItem++;

        if (pageItem === perPage || (useCover && index === 0)) {
          pageItem = 0;
          height = 0;
          width += item.outerWidth(true);

        } else {
          height += item.outerHeight(true);
        }
      });

      this.container.css("width", width + this.itemWidth + "px");
    },

    _positionVerticalAxisY: function() {
      var width = 0;
      var height = 0;

      var perPage = this.options.perPage;
      var useCover = this.options.cover;

      this._getItems(true).each(function(index, value) {
        var item = $(value);
        item.css({"top": height + "px", "left": width + "px"});
        height += item.outerHeight(true);
      });

      this.container.css({"width": width + this.itemWidth + "px", "height": height + this.itemHeight});
    },

    _calculateTotalPages: function() {
      this.totalPages = Math.ceil(this._getItems().length/this.options.perPage);

      if (this.options.cover) {
        this.totalPages += 1;
      }
    },

    _calculateMaxShiftAvailable: function(items) {
      var amount = this.options.perPage - items.length;
      return (this.isAxisY() ? this.itemHeight : this.itemWidth) * amount
    },

    _calculateItemPosition: function(item) {
      return this._abs($(item).css(this.animatedAttribute));
    },

    _calculateItemsForPagination: function(page) {
      var delta = this.options.cover ? (page - 1) * this.options.perPage : page * this.options.perPage;
      return this._getItems().slice(delta - this.options.perPage, delta);
    },

    _calculateWidth: function(items, isCover) {
      if (this.options.cover && isCover) {
        return this.coverWidth;
      }

      return items.length * this.itemWidth;
    },

    _calculateItemDimension: function() {
      var complement = this.options.cover ? ":eq(1)" : ":first";
      var item = $("." + this.options.itemClass + complement, this.container);
      this.itemWidth = item.outerWidth(true);
      this.itemHeight = item.outerHeight(true);
    },

    _calculateCoverDimention: function() {
      if (this.options.cover) {
        var coverItem = this._getCover();
        this.coverWidth = coverItem.outerWidth(true);
        this.coverHeight = coverItem.outerWidth(true);

      } else {
        this.coverWidth = 0;
        this.coverHeight = 0;
      }
    },

    _validateAnimationEasing: function() {
      var easingFuctionExists = !!($.easing && $.easing[this.options.easing]);

      if (!easingFuctionExists) {
        this.options.easing = $.fn.silverTrack.options.easing;
      }
    },

    _executeAll: function(name, args) {
      for (var i = 0; i < this.plugins.length; i++) {
        this._callFunction(this.plugins[i], name, args);
      }
    },

    _callFunction: function(obj, name, args) {
      if(obj && name && typeof obj[name] === 'function') {
        obj[name].apply(obj, [this].concat(args || []));
      }
    },

    _abs: function(string) {
      return Math.abs(parseInt(string, 10));
    }

  }

  SilverTrack.Plugins = {};

  $.silverTrackPlugin = function(name, obj) {
    SilverTrack.Plugins[name] = function(settings){
      var options = $.extend({}, this.defaults, settings);
      this.PluginName = name;
      this.initialize(options);
    };

    SilverTrack.Plugins[name].prototype = $.extend({
      defaults: {},
      initialize: function(options) {},

      onInstall: function(track) {},
      beforeStart: function(track) {},
      afterStart: function(track) {},
      beforeRestart: function(track) {},
      afterRestart: function(track) {},
      onTotalPagesUpdate: function(track){},

      /* Event format
       *  {
       *    name: "prev", // or "next"
       *    page: 1,
       *    cover: false,
       *    items: []
       *  }
       */
      beforeAnimation: function(track, event) {},
      afterAnimation: function(track, event) {},
      beforePagination: function(track, event) {},

      /* Event format
       *  {
       *    items: [],
       *    newHeight: 150
       *  }
       */
      beforeAdjustHeight: function(track, event) {},
      afterAdjustHeight: function(track, event) {}
    }, obj);
  }

  window.SilverTrack = SilverTrack;

})(jQuery, window, document);
/*!
 * jQuery SilverTrack
 * https://github.com/tulios/jquery.silver_track
 * version: 0.3.0
 *
 * SilverTrack Recipes
 * version: 0.1.0
 */

(function ($, window, document) {

  if (!window.SilverTrack) {
    return;
  }

  SilverTrack.Recipes = {};
  SilverTrack.Factory = function(element, options) {
    this.element = element;
    this.options = $.extend({}, options);
  }

  SilverTrack.Factory.prototype = {
    track: null,

    create: function(configCallback) {
      configCallback(this.externalInterface());
      return this.track;
    },

    externalInterface: function() {
      var self = this;
      var defaultTrack = function() {
        if (!self.track) {
          self.track = self.element.silverTrack();
        }
      }

      return {
        createTrack: function(callback) {
          if (callback !== undefined) {
            self.track = callback(self.element, self.options);
          }
          defaultTrack();
        },

        installPlugins: function(callback) {
          defaultTrack();
          if (callback !== undefined) {
            callback(self.track, self.options);
          }
        }
      }
    }
  }

  $.silverTrackRecipes = function(recipeName, configCallback) {
    SilverTrack.Recipes[recipeName] = function(element, options) {
      return new SilverTrack.Factory(element, options).create(configCallback);
    }
  }

  $.silverTrackRecipes.create = function(recipeName, element, options) {
    return SilverTrack.Recipes[recipeName](element, options);
  }

})(jQuery, window, document);
/*!
 * jQuery SilverTrack
 * https://github.com/tulios/jquery.silver_track
 * version: 0.4.0
 *
 * Navigator
 * version: 0.2.0
 *
 */
(function($, window, document) {

  /*
   * track.install(new SilverTrack.Plugins.Navigator({
   *   prev: $("a.prev"),
   *   next: $("a.next")
   * }));
   *
   */
  $.silverTrackPlugin("Navigator", {
    defaults: {
      disabledClass: "disabled",
      beforePagination: null
    },

    initialize: function(options) {
      this.track = null;
      this.options = options;
      this.prev = this.options.prev;
      this.next = this.options.next;

      var self = this;
      this.prev.addClass(this.options.disabledClass).click(function(e) {
        e.preventDefault();
        self.track.prev();
      });

      this.next.addClass(this.options.disabledClass).click(function(e) {
        e.preventDefault();
        self.track.next();
      });
    },

    onInstall: function(track) {
      this.track = track;
    },

    afterStart: function() {
      this.afterAnimation();
    },

    afterAnimation: function() {
      this.track.hasPrev() ? this._enable(this.prev) : this._disable(this.prev);
      this.track.hasNext() ? this._enable(this.next) : this._disable(this.next);
    },

    afterRestart: function() {
      this.afterAnimation();
    },

    onTotalPagesUpdate: function() {
      this.afterAnimation();
    },

    beforePagination: function(track, event) {
      if (this.options.beforePagination) {
        this.options.beforePagination(track, event);
      }
    },

    _enable: function(element) {
      element.removeClass(this.options.disabledClass);
    },

    _disable: function(element) {
      element.addClass(this.options.disabledClass);
    }
  });

})(jQuery, window, document);
/*!
 * jQuery SilverTrack
 * https://github.com/tulios/jquery.silver_track
 * version: 0.4.0
 *
 * Bullet Navigator
 * version: 0.1.1
 *
 */
(function($, window, document) {

  /*
   * track.install(new SilverTrack.Plugins.BulletNavigator({
   *   container: $(".bullet-pagination")
   * }));
   *
   */
  $.silverTrackPlugin("BulletNavigator", {
    defaults: {
      bulletClass: "bullet",
      activeClass: "active"
    },

    initialize: function(options) {
      this.track = null;
      this.options = options;
      this.container = this.options.container;
    },

    onInstall: function(track) {
      this.track = track;
    },

    afterStart: function() {
      this._createBullets();
      this._getBulletByPage(1).addClass(this.options.activeClass);
      this._setupBulletClick();
    },

    afterAnimation: function() {
      this._setupBulletClick();
    },

    beforePagination: function(track, event) {
      var bullet = this._getBulletByPage(event.page);
      this._updateBullets(bullet);
    },

    afterRestart: function() {
      this._clearBullets();
      this._createBullets();

      var bullet = this._getBulletByPage(this.track.currentPage);
      this._updateBullets(bullet);
      this._setupBulletClick();
    },

    onTotalPagesUpdate: function() {
      this._clearBullets();
      this._createBullets();
      this._getBullets().click(function(e) {
        e.preventDefault();
      });
    },

    _clearBullets: function() {
      $("." + this.options.bulletClass, this.container).remove();
    },

    _createBullets: function() {
      for (var i = 0; i < this.track.totalPages; i++) {
        this.container.append(this._createBullet(i + 1));
      }
    },

    _setupBulletClick: function() {
      var self = this;
      var bullets = this._getBullets();
      bullets.click(function(e) {
        e.preventDefault();
        var bullet = $(this);
        self._updateBullets(bullet);
        self.track.goToPage(bullet.data("page"));
        bullets.unbind("click");
      });
    },

    _updateBullets: function(bullet) {
      this._getBullets().removeClass(this.options.activeClass);
      bullet.addClass(this.options.activeClass);
    },

    _getBulletByPage: function(page) {
      return $("." + this.options.bulletClass + "[data-page='" + page + "']", this.container);
    },

    _getBullets: function() {
      return $("." + this.options.bulletClass, this.container);
    },

    _createBullet: function(page) {
      return $("<a></a>", {"class": this.options.bulletClass, "data-page": page, "href": "#"});
    }

  });

})(jQuery, window, document);
/*!
 * jQuery SilverTrack
 * https://github.com/tulios/jquery.silver_track
 * version: 0.4.0
 *
 * ResponsiveHub Connector
 * version: 0.1.0
 *
 * This plugin depends on modernizr.mediaqueries.js
 */
(function($, window, document) {

  /*
   * track.install(new SilverTrack.Plugins.ResponsiveHubConnector({
   *   layouts: ["phone", "small-tablet", "tablet", "web"],
   *   onReady: function(track, options, event) {},
   *   onChange: function(track, options, event) {}
   * }));
   *
   */
  $.silverTrackPlugin("ResponsiveHubConnector", {
    initialize: function(options) {
      this.options = options;
      this.layouts = this.options.layouts;
      this.enabled = !!$.responsiveHub;
    },

    onInstall: function(track) {
      this.track = track;

      if (this.enabled) {
        var self = this;
        $.responsiveHub("ready", this.layouts, function(event) {
          self.options.onReady(self.track, self.options, event);
        });

        $.responsiveHub("change", this.layouts, function(event) {
          self.options.onChange(self.track, self.options, event);
        });
      }
    }
  });

})(jQuery, window, document);
/***************************************************BEGIN HERE********************/
jQuery(function() {

	  jQuery.silverTrackRecipes("basic", function(factory) {
	    factory.createTrack(function(element, options) {
	      return element.silverTrack({
	        easing: "easeInOutQuad",
	        duration: 600
	      });
	    });

	    factory.installPlugins(function(track, options) {
	      var parent = track.container.parents(".track");

	      track.install(new SilverTrack.Plugins.Navigator({
	        prev: jQuery("a.prev", parent),
	        next: jQuery("a.next", parent)
	      }));

	      track.install(new SilverTrack.Plugins.BulletNavigator({
	        container: jQuery(".bullet-pagination", parent)
	      }));

	      track.install(new SilverTrack.Plugins.ResponsiveHubConnector({
	        layouts: ["phone", "small-tablet", "tablet", "web"],
	        onReady: function(track, options, event) {
	          options.onChange(track, options, event);
	        },

	        onChange: function(track, options, event) {
	          track.options.mode = "horizontal";
	          track.options.autoheight = false;
	          track.options.perPage = 4;

	          if (event.layout === "small-tablet") {
	            track.options.perPage = 3;

	          } else if (event.layout === "phone") {
	            track.options.mode = "vertical";
	            track.options.autoHeight = true;
	          }

	          track.restart({keepCurrentPage: true});
	        }
	      }));
	    });
	  });

	});
