/**
 * gsn javascript
 **/
var map;
 
var GSN = { 
	debugmode: false
	,baseurl: ""
	,plot: false
	,gsnFlag: false
	,wikiUrlAddPage: ""
	,wikiUrlGetPage: ""
	,wikiNamespace: 0
	,vsName: ""
	,vsIP: ""
	,vsPort: ""
	,requestMem: ""
	,debug: function (txt) {
		if(typeof console != "undefined" && this.debugmode) {
			console.debug(txt);
		}	
	}
	,context: null //home, data, map || fullmap
	/**
	* Initialize a page load (begin, tab click & back button)
	*/
	,load: function(){
		//by default, load home
		if (location.hash == "") location.hash = "data";
		
		GSN.debug("init:"+location.hash);
		var params=location.hash.substr(1).split(",");
		
		GSN.context = params[0];
			$("#msg").hide();
			$("#main #datachooser").show();
			if (!GSN.loaded) GSN.updateall();
			
	}
	/**
	* Initialize a page load (begin, tab click & back button)
	*/
	,loadQPN: function(){
		//by default, load home
		if (location.hash == "") location.hash = "data";
		
		GSN.debug("init:"+location.hash);
		var params=location.hash.substr(1).split(",");
		
		GSN.context = params[0];
			$("#msg").hide();
			$("#main #datachooser").show();
			//if  (!GSN.loaded) GSN.updateQPNall();
			
	}
	/**
	* iframe msg callback for webupload
	*/
	,msgcallback: function (msg,code) {
		GSN.debug(code+": "+msg);
		$("#msg").html(msg);		
		$("#msg").removeClass();
		if (code <= 200)
			$("#msg").addClass("good");
		else
			$("#msg").addClass("bad");
		$("#msg").show();
		document.getElementById("container").scrollIntoView(true);
	}
	/**
	* Click on the top navigation bar
	*/
	,nav: function (page) {
		$.historyLoad(page);
		return false;
	}
	/**
	* Click on the virtual sensor on the left bar
	*/
	,menu: function (vsName) {
		$("#msg").hide();
		//define the click depending the context (home,data,map)
		GSN.data.init(vsName);
	}
	/**
	* Close all button
	*/
	,closeall: function (){
		$("#msg").hide();
		$("#vs").empty();
		GSN.map.followMarker(null);
	}
	,loaded : false
	/**
	* Initialize the gsn title and leftside menu
	*/
	,init : function(data) {
		this.loaded=true;
		this.gsnFlag=true;
		$(".loading").remove();
		
		//show all the gsn container info
	/*	if ($(document).attr("title")=="GSN") {
			var gsn = $("gsn",data);
			$(document).attr("title",$(gsn).attr("name")+" :: GSN");
			$("#gsn-name").empty().append($(gsn).attr("name")+" :: GSN");
			$("#gsn-desc").empty().append($(gsn).attr("description"));
			$("#gsn-author").empty().append($(gsn).attr("author")+" ("+$(gsn).attr("email")+")");
		}
		*/
		//build the leftside vs menu
	/*	$("#vsmenu").empty();
		$("virtual-sensor",data).each(function(){
			var vsname = $(this).attr("name");
			$("#vsmenu").append($.LI({},$.A({"href":"javascript:GSN.menu('"+vsname+"');","id":"menu-"+vsname+""},vsname)));
			//if ($("field[@name=latitude]",$(this)).text()!="") 
			//	$("#menu-"+vsname).addClass("gpsenabled");
		});
		*/
	}
	/**
	* Initialize the GSN Object
	*/
	,initQPN : function(data) {
		this.loaded=true;
		this.gsnFlag=false;
		$(".loading").remove();
	}
	,updatenb: 0
	,updateallchange: function(){
		if($("#refreshall_timeout").attr("value") != 0)
			GSN.updateall();
	}
	/**
	* Ajax call to update all the sensor display on the page and the map
	*/
	,updateall: function(num,showall){
		//to prevent multiple update instance
		if (typeof num == "number" && num != GSN.updatenb) return;
		GSN.updatenb++;
		
		$(".refreshing").show();
		
		var firstload = !GSN.loaded;
  		
		$.ajax({ type: "GET", url: GSN.baseurl+"/gsn", success: function(data){
			var start = new Date();
			//initalisation of gsn info, vsmenu
			if (!GSN.loaded) GSN.init(data);
			
			//create vsbox on the first load
		/*	if (firstload && GSN.context == "home") {
				for (var i = 0; i < 10; ++i){
					var n = $($("virtual-sensor",data).get(i)).attr("name");
					if (n!=null) GSN.vsbox.add(n);
				}
			} else if (firstload && GSN.context == "fullmap") {
				$("virtual-sensor",data).each(function(){
					GSN.vsbox.add($(this).attr("name"));
				});
			}
			*/
			//update vsbox
		/*	$("virtual-sensor",data).each(function(){
				GSN.vsbox.update(this);
			});
			
			//update map
			GSN.map.autozoomandcenter();
			*/
			//next refresh
/*			if($("#refreshall_timeout").attr("value") > 0)
				setTimeout("GSN.updateall("+GSN.updatenb+")", $("#refreshall_timeout").attr("value"));
			
			$(".refreshing").hide();	
	*/		
			var diff = new Date() - start;
			GSN.debug("updateall time:"+diff/1000); 
		}});
	}
	/**
	* Data part
	*/	
	,data : {
	
		fields : new Array(),
		fields_type : new Array(),
		criterias : new Array(),
		nb_crit : 0,
		text : "",
		initPlot: function(vsName, radio){
			if (radio == null) {
				radio = false;
			}
			plot=true;
   			$("form").attr("action", "");
			$("#dataSet").remove();
			$("#criterias").empty();
			//$("#criterias").append("<tr><td class=\"step\">Step 1/5 : Selection of the Virtual Sensor</td></tr>");
			//$("#criterias").append("<tr><td class=\"data\" id=\"vsensor\">Selected virtual sensor : " + vsName + "</td></tr>");
			//$("#criterias").append("<input type=\"hidden\" name=\"plot\" id=\"vsName\" value=\"1\">");
			$("#criterias").append("<input type=\"hidden\" name=\"vsName\" id=\"vsName\" value=\""+vsName+"\">");
			$("#criterias").append("<tr><td class=\"step\">Selection of the fields</td></tr>");
			$("#criterias").append("<tr><td class=\"data\" id=\"fields\"></td></tr>");
			//$("#fields").append("<input type=\"radio\" id=\"commonReq\" name=\"commonReq\" value=\"true\" onClick=\"javascript:GSN.data.init('" + vsName + "', false)\">Common request ");
			//$("#fields").append("<input type=\"radio\" id=\"aggregReq\" name=\"commonReq\" value=\"false\" onClick=\"javascript:GSN.data.init('" + vsName + "', true)\">Aggregate functions<br><br>");
			if (radio) {
				$("#aggregReq").attr("checked", true);
			} else {
				$("#commonReq").attr("checked", true);
			}
				
			$("#fields").append("Select field(s)<br/>");
			$.ajax({
				type: "GET",
				url: GSN.baseurl+"/gsn?REQUEST=113&name="+vsName,
				success: function(msg) {
					GSN.data.fields = new Array();
					GSN.data.fields_type = new Array();
					GSN.data.criterias = new Array();
					GSN.data.nb_crit = 0;
					$("virtual-sensor field", msg).each(function() {
						if ($(this).attr("type").substr(0,3) != "bin") {
							GSN.data.fields.push($(this).attr("name"));
							GSN.data.fields_type.push($(this).attr("type"));
							if (radio) {
								if (($(this).attr("type") == "int") || ($(this).attr("type") == "bigint") || ($(this).attr("type") == "integer") || ($(this).attr("type") == "long") || ($(this).attr("type") == "double")) {
									$("#fields").append("<div id='" + $(this).attr("name") + "'><input type=\"checkbox\" name=\"fields\" id=\"field\" value=\""+$(this).attr("name")+"\" onClick=\"javascript:GSN.data.aggregateSelect('"+$(this).attr("name")+"',this.checked)\">"+$(this).attr("name")+" </div>");
								}
							} else {
								$("#fields").append("<input type=\"checkbox\" name=\"fields\" id=\"field\" value=\""+$(this).attr("name")+"\">"+$(this).attr("name")+"<br/>");
							}
						}
					});
					if (radio) {
						$("#fields").append("<br/>Group by : <select name=\"aggregateGB\" id=\"aggregateGB\" size=\"1\" onChange=\"javascript:GSN.data.groupBy(this.value)\"></select><br/>");
						for (i = 0; i < GSN.data.fields.length; i++) {
							$("#aggregateGB").append("<option value=\"" + GSN.data.fields[i] + "\">" + GSN.data.fields[i] + "</option>");
						}
						$("#aggregateGB").append("<option value=\"none\">None</option>");
					} else {
						$("#fields").append("<br/><input type=\"checkbox\" name=\"all\" onClick=\"javascript:GSN.data.checkAllFields(this.checked)\">Check all<br/>");
					}
					
					GSN.data.nbDatas();
					plot=false; //standard setting
				}
				
				
			});
			
			
		},
		init: function(vsName, radio){
		plot=false;
			if (radio == null) {
				radio = false;
			}
   			$("form").attr("action", "");
			$("#dataSet").remove();
			$("#criterias").empty();
			//$("#criterias").append("<tr><td class=\"step\">Step 1/5 : Selection of the Virtual Sensor</td></tr>");
			//$("#criterias").append("<tr><td class=\"data\" id=\"vsensor\">Selected virtual sensor : " + vsName + "</td></tr>");
			$("#criterias").append("<input type=\"hidden\" name=\"vsName\" id=\"vsName\" value=\""+vsName+"\">");
			$("#criterias").append("<tr><td class=\"step\">Selection of the fields for "+vsName+"</td></tr>");
			$("#criterias").append("<tr><td class=\"data\" id=\"fields\"></td></tr>");
			$("#fields").append("<input type=\"radio\" id=\"commonReq\" name=\"commonReq\" value=\"true\" onClick=\"javascript:GSN.data.init('" + vsName + "', false)\">Common request ");
			$("#fields").append("<input type=\"radio\" id=\"aggregReq\" name=\"commonReq\" value=\"false\" onClick=\"javascript:GSN.data.init('" + vsName + "', true)\">Aggregate functions<br><br>");
			if (radio) {
				$("#aggregReq").attr("checked", true);
			} else {
				$("#commonReq").attr("checked", true);
			}
				
			$("#fields").append("Select field(s)<br/>");
			$.ajax({
				type: "GET",
				url: GSN.baseurl+"/gsn?REQUEST=113&name="+vsName,
				success: function(msg) {
					GSN.data.fields = new Array();
					GSN.data.fields_type = new Array();
					GSN.data.criterias = new Array();
					GSN.data.nb_crit = 0;
					$("virtual-sensor field", msg).each(function() {
						if ($(this).attr("type").substr(0,3) != "bin") {
							GSN.data.fields.push($(this).attr("name"));
							GSN.data.fields_type.push($(this).attr("type"));
							if (radio) {
								if (($(this).attr("type") == "int") || ($(this).attr("type") == "bigint") || ($(this).attr("type") == "integer") || ($(this).attr("type") == "long") || ($(this).attr("type") == "double")) {
									$("#fields").append("<div id='" + $(this).attr("name") + "'><input type=\"checkbox\" name=\"fields\" id=\"field\" value=\""+$(this).attr("name")+"\" onClick=\"javascript:GSN.data.aggregateSelect('"+$(this).attr("name")+"',this.checked)\">"+$(this).attr("name")+" </div>");
								}
							} else {
								$("#fields").append("<input type=\"checkbox\" name=\"fields\" id=\"field\" value=\""+$(this).attr("name")+"\">"+$(this).attr("name")+"<br/>");
							}
						}
					});
					if (radio) {
						$("#fields").append("<br/>Group by : <select name=\"aggregateGB\" id=\"aggregateGB\" size=\"1\" onChange=\"javascript:GSN.data.groupBy(this.value)\"></select><br/>");
						for (i = 0; i < GSN.data.fields.length; i++) {
							$("#aggregateGB").append("<option value=\"" + GSN.data.fields[i] + "\">" + GSN.data.fields[i] + "</option>");
						}
						$("#aggregateGB").append("<option value=\"none\">None</option>");
					} else {
						$("#fields").append("<br/><input type=\"checkbox\" name=\"all\" onClick=\"javascript:GSN.data.checkAllFields(this.checked)\">Check all<br/>");
					}
					//$("#fields").append("<br><a href=\"javascript:GSN.data.nbDatas()\" id=\"nextStep\">Next step</a>");
					GSN.data.nbDatas();
				}
			});
		},
		initQPN: function(vsname, vsip, vsport, radio){
			if (radio == null) {
				radio = false;
			}
			plot=false;
			GSN.vsName = vsname;
			GSN.vsIP = vsip;
			GSN.vsPort = vsport;
   			//$("form").attr("action", "");
			$("#dataSet").remove();
			$("#criterias").empty();
			//$("#criterias").append("<tr><td class=\"step\">Step 1/5 : Selection of the Virtual Sensor</td></tr>");
			//$("#criterias").append("<tr><td class=\"data\" id=\"vsensor\">Selected virtual sensor : " + vsName + "</td></tr>");
			$("#criterias").append("<input type=\"hidden\" name=\"vsname\" id=\"vsname\" value=\""+vsname+"\">");
			$("#criterias").append("<input type=\"hidden\" name=\"vsip\" id=\"vsip\" value=\""+vsip+"\">");
			$("#criterias").append("<input type=\"hidden\" name=\"vsport\" id=\"vsport\" value=\""+vsport+"\">");
			$("#criterias").append("<tr><td class=\"step\">Selection of the fields for "+vsname+"</td></tr>");
			$("#criterias").append("<tr><td class=\"data\" id=\"fields\"></td></tr>");
			$("#fields").append("<input type=\"radio\" id=\"commonReq\" name=\"commonReq\" value=\"true\" onClick=\"javascript:GSN.data.initQPN('" + vsname + "', '" + vsip + "', '" + vsport + "', false)\">Common request ");
			$("#fields").append("<input type=\"radio\" id=\"aggregReq\" name=\"commonReq\" value=\"false\" onClick=\"javascript:GSN.data.initQPN('" + vsname + "', '" + vsip + "', '" + vsport + "', true)\">Aggregate functions<br><br>");
			if (radio) {
				$("#aggregReq").attr("checked", true);
			} else {
				$("#commonReq").attr("checked", true);
			}
				
			$("#fields").append("Select field(s)<br/>");
			$.ajax({
				type: "GET",
				url: GSN.baseurl+"/DataServlet?Action=GetMetaData&url="+escape("http://"+vsip+":"+vsport+"/axis2/services/qpn")+"&AddrName="+escape(vsname)+"&AddrIP="+escape(vsip)+"&AddrPort="+escape(vsport),
				success: function(msg) {
					GSN.data.fields = new Array();
					GSN.data.fields_type = new Array();
					GSN.data.criterias = new Array();
					GSN.data.nb_crit = 0;
					$("metadata column", msg).each(function() {
						if ($(this).attr("type").substr(0,3) != "bin") {
							GSN.data.fields.push($(this).attr("name"));
							GSN.data.fields_type.push($(this).attr("type"));
							if (radio) {
								if (($(this).attr("type") == "BIGINT") || ($(this).attr("type") == "long") || ($(this).attr("type") == "INTEGER") || ($(this).attr("type") == "DOUBLE")) {
									$("#fields").append("<div id='" + $(this).attr("name") + "'><input type=\"checkbox\" name=\"fields\" id=\"field\" value=\""+$(this).attr("name")+"\" onClick=\"javascript:GSN.data.aggregateSelect('"+$(this).attr("name")+"',this.checked)\">"+$(this).attr("name")+" </div>");
								}
							} else {
								$("#fields").append("<input type=\"checkbox\" name=\"fields\" id=\"field\" value=\""+$(this).attr("name")+"\">"+$(this).attr("name")+"<br/>");
							}
						}
					});
					if (radio) {
						$("#fields").append("<br/>Group by : <select name=\"aggregateGB\" id=\"aggregateGB\" size=\"1\" onChange=\"javascript:GSN.data.groupBy(this.value)\"></select><br/>");
						for (i = 0; i < GSN.data.fields.length; i++) {
							$("#aggregateGB").append("<option value=\"" + GSN.data.fields[i] + "\">" + GSN.data.fields[i] + "</option>");
						}
						$("#aggregateGB").append("<option value=\"none\">None</option>");
					} else {
						$("#fields").append("<br/><input type=\"checkbox\" name=\"all\" onClick=\"javascript:GSN.data.checkAllFields(this.checked)\">Check all<br/>");
					}
					//$("#fields").append("<br><a href=\"javascript:GSN.data.nbDatas()\" id=\"nextStep\">Next step</a>");
					GSN.data.nbDatas();
				}
			});
		},
		aggregateSelect: function(that, checked){
		  // To can choose the aggregate type for the field
		  if (checked) {
    		  $("#"+that).append(" <select name=\""+that+"AG\" id=\""+that+"AG\" size=\"1\"></select>");
    		  $("#"+that+"AG").append("<option value=\"AVG\">AVG</option>");
    		  $("#"+that+"AG").append("<option value=\"MAX\">MAX</option>");
    		  $("#"+that+"AG").append("<option value=\"MIN\">MIN</option>");
    	   } else {
    	       $("#"+that+"AG").remove();
    	   }
						
		},
		groupBy: function(option) {
			if (option == "timed") {
				$("#aggregateGB").after("<input type=\"text\" name=\"gbdelta\" id=\"gbdelta\" size=\"5\"><select name=\"gbdeltameasure\" id=\"gbdeltameasure\" size=\"1\"></select>");
				$("#gbdeltameasure").append("<option value=\"ms\">Milisecond</option>");
				$("#gbdeltameasure").append("<option value=\"s\">Second</option>");
				$("#gbdeltameasure").append("<option value=\"m\">Minute</option>");
				$("#gbdeltameasure").append("<option value=\"h\">Hour</option>");
				$("#gbdeltameasure").append("<option value=\"d\">Day</option>");
			} else {
				$("#gbdelta").remove();
				$("#gbdeltameasure").remove();
			}
		},
		checkAllFields: function(check){
			$("input").each(function () {
				if ($(this).attr("id") == "field") {
					$(this).attr("checked", check);
				}
			});
		},
		nbDatas: function() {
			$("#nextStep").remove();
			$("#criterias").append("<tr><td class=\"step\">Selection of the Time Interval</td></tr>");
			$("#criterias").append("<tr><td class=\"data\" id=\"nbDatas\"></td></tr>");
			if (GSN.gsnFlag){
				//$("#nbDatas").append("<input type=\"radio\" name=\"nbdatas\" id=\"allDatas\" value=\"\" checked> All data<br/>");
				//$("#nbDatas").append("<input type=\"radio\" name=\"nbdatas\" id=\"someDatas\" value=\"\"> Last <input type=\"text\" name=\"nb\" value=\"\" id=\"nbOfDatas\" size=\"4\"/> values<br/>");
				$("#nbDatas").append("<input type=\"radio\" name=\"nbdatas\" id=\"intervall\" value=\"checked\"> From ");
				$("#nbDatas").append("&nbsp;<input type=\"text\" name=\"fromInterval\" value=\"\" id=\"fromInterval\" />");
			}else 
				$("#nbDatas").append("From &nbsp;<input type=\"text\" name=\"fromInterval\" value=\"\" id=\"fromInterval\" />");
			$("#fromInterval").val(GSN.util.printDate((new Date()).getTime()- 3600000));
			$("#fromInterval").datePicker({startDate:'01/01/2006'});
			$("#nbDatas").append(" &nbsp; &nbsp; &nbsp; To <input type=\"text\" name=\"toInterval\" value=\"\" id=\"toInterval\" />");
			$("#toInterval").val(GSN.util.printDate((new Date()).getTime()));
			$("#toInterval").datePicker({startDate:'01/01/2006'});
			//$("#nbDatas").append("<br><a href=\"javascript:GSN.data.addCriteria(true)\" id=\"nextStep\">Next step</a>");
			GSN.data.addCriteria(true);
		},	
		addCriteria: function(newStep) {
			if (newStep) {
				$("#nextStep").remove();
				$("#criterias").append("<tr><td class=\"step\">Selection of the criterias</td></tr>");
				$("#criterias").append("<tr><td class=\"data\" id=\"where\">");
				$("#where").append("<a id=\"addCrit\" href=\"javascript:GSN.data.addCriteria(false)\">Add criteria</a>");
				//$("#where").append("<br/><br/><a id=\"nextStep\" href=\"javascript:GSN.data.selectDataDisplayWiki()\">Next step</a></td></tr>");
				
				GSN.data.selectDataDisplayWiki();
			} else {
				GSN.data.nb_crit++;
				newcrit = "<div id=\"where" + GSN.data.nb_crit + "\"></div>";
	    		$("#addCrit").before(newcrit);
	    		GSN.data.addCriteriaLine(GSN.data.nb_crit, "");
	    		GSN.data.criterias.push(GSN.data.nb_crit);
			}
		},
		addCriteriaLine: function(nb_crit, field) {
				newcrit = "";
				if (GSN.data.criterias.length > 0) {
					newcrit += "<select name=\"critJoin\" id=\"critJoin" + nb_crit + "\" size=\"1\">";
					var critJoin = new Array("AND", "OR");
					for (var i=0; i < critJoin.length; i++) {
						newcrit += "<option value=\""+critJoin[i]+"\">"+critJoin[i]+"</option>";
					}
					newcrit += "</select>";
				}
				newcrit += "<select name=\"neg\" size=\"1\" id=\"neg" + nb_crit + "\">";
				var neg = new Array("", "NOT");
	    		for (i=0; i < neg.length; i++) {
	    			newcrit += "<option value=\"" + neg[i] + "\" >" + neg[i] + "</option>";
	    		}
	    		newcrit += "</select> ";
	    		newcrit += "<select name=\"critfield\" id=\"critfield" + nb_crit + "\" size=\"1\" onChange=\"javascript:GSN.data.criteriaForType(this.value,"+nb_crit+")\">";
	    		for (var i=0; i< GSN.data.fields.length; i++) {
	    			newcrit += "<option value=\"" + GSN.data.fields[i] + "\">" + GSN.data.fields[i] + "</option>";
	    		}
	    		newcrit += "</select> ";
	    		var operators = new Array("&gt;", "&gt;=", "&lt;", "&lt;=", "=", "LIKE");
	    		newcrit += "<select name=\"critop\" size=\"1\" id=\"critop" + nb_crit + "\">";
	    		for (i=0; i < operators.length; i++) {
	    			newcrit += "<option value=\"" + operators[i] + "\" >" + operators[i] + "</option>";
	    		}
	    		newcrit += "</select> ";
	    		newcrit += "<input type=\"text\" name=\"critval\" id=\"critval" + nb_crit + "\" size=\"18\">";
	    		newcrit += " <a href=\"javascript:GSN.data.removeCrit("+nb_crit+")\" id=\"remove" + nb_crit + "\"> (remove)</a>";
	    		$("#where"+nb_crit).append(newcrit);
	    		$("#critfield"+nb_crit).attr("value", field);
	    		GSN.data.criteriaForType(GSN.data.fields[0],nb_crit);
		},
		criteriaForType: function(field, nb_crit) {
			if (field == "timed") {
				$("#critval"+nb_crit).val(GSN.util.printDate((new Date()).getTime()));
				$("#critval"+nb_crit).datePicker({startDate:'01/01/2006'});
			} else {
				$("#critval"+nb_crit).val("");
			}
		},
		removeCrit: function(critnb) {
	   		$("#where"+critnb).remove();
	   		var critTMP = new Array();
	   		for (var i=0; i<GSN.data.criterias.length; i++) {
	   			if (GSN.data.criterias[i] == critnb) {
	   				if (i == 0 && GSN.data.criterias.length > 0) {
	   					$("#critJoin"+GSN.data.criterias[i+1]).remove();
	   				}
	   			} else {
	   				critTMP.push(GSN.data.criterias[i]);
	   			}
	   		}
	   		GSN.data.criterias = critTMP;
	   	},
	   	selectDataDisplayWiki: function() {
	   	if (!plot) {
	   		$("#nextStep").remove();
	   		$("#criterias").append("<tr><td class=\"step\">Naming of the query</td></tr>");
			$("#criterias").append("<tr><td class=\"data\" id=\"display\">");
			//$("#display").append($.DIV({"id":"showSQL"},$.A({"href":"javascript:GSN.data.getDatas(true);"},"Show SQL query")));
			$("#display").append("Query name: <input type=\"text\" id=\"queryname\" value=\"query"+Math.floor(Math.random()*1000000000)+"\" name=\"name\" size=\"30\">");
			//$("#display").append("<input type=\"radio\" id=\"popup\" value=\"popup\" name=\"display\" onClick=\"javascript:GSN.data.showFormatCSV()\">In a new window<br/>");
			//$("#display").append("<input type=\"radio\" id=\"CSV\" value=\"CSV\" name=\"display\" onClick=\"javascript:GSN.data.showFormatCSV()\">Download data<br/>");
			$("#display").append("<br/><br/><a id=\"getDatas\" href=\"javascript:GSN.data.getDatasWiki()\">Save Query</a><br/><br/>");
			$("#display").append($.DIV({"id":"showSQL"}));
			
			}
			else {
			
					 	    $("#nextStep").remove();
					 	    //here: use these fields later to be able to specify a filename
	   			//$("#criterias").append("<tr><td class=\"step\">Naming of the query</td></tr>");
				//$("#criterias").append("<tr><td class=\"data\" id=\"display\">");
			$("#criterias").append($.DIV({"id":"showSQL"},$.A({"href":"javascript:GSN.data.displaySQL();"},"Create Plot")));
				//$("#display").append("Query name: <input type=\"text\" id=\"queryname\" value=\"query"+request+"\" name=\"name\" size=\"30\">");
				//$("#display").append("<input type=\"radio\" id=\"popup\" value=\"popup\" name=\"display\" onClick=\"javascript:GSN.data.showFormatCSV()\">In a new window<br/>");
				//$("#display").append("<input type=\"radio\" id=\"CSV\" value=\"CSV\" name=\"display\" onClick=\"javascript:GSN.data.showFormatCSV()\">Download data<br/>");
			//	$("#display").append("<br/><br/><a id=\"getDatas\" href=\"javascript:GSN.data.getDatasWiki()\">Save Query</a><br/><br/>");
				$("#criterias").append($.DIV({"id":"showSQL"}));
					
			
				

			

			}
	   	},
	   	displaySQL: function() {
	   			
		//////////////////////new
		
			   	request = "";
	   			if (true) {
	   				request += "&commonReq=true";
	   			} else {
	   				request += "&commonReq=false";
	   				if ($("#aggregateGB").val() != "none") {
	   					request += "&groupby=" + $("#aggregateGB").val();
	   					if ($("#aggregateGB").attr("value") == "timed") {
	   						temp = $("#gbdelta").val();
	   						if ($("#gbdeltameasure").val() == "s") {
	   							temp = temp * 1000;
	   						} else if ($("#gbdeltameasure").val() == "m") {
	   							temp = temp * 60000;
	   						} else if ($("#gbdeltameasure").val() == "h") {
	   							temp = temp * 3600000;
	   						} else if ($("#gbdeltameasure").val() == "d") {
	   							temp = temp * 86400000; // 3600000 * 24
	   						}
	   						request += "&groupbytimed=" + temp
	   					}
	   				}
	   			}
	   			$("input").each(function () {
					if ($(this).attr("id") == "field" && $(this).attr("checked")) {
					   if (true) {
    						request += "&fields=" + $(this).attr("value");
    					} else {
    					   request += "&fields=" + $("#"+$(this).val()+"AG").val()+"("+$(this).attr("value")+")";
    					}
					}
				});
				if ($("#someDatas").attr("checked") && $("#nbOfDatas").attr("value") != "") {
					request += "&nb=" + $("#nbOfDatas").attr("value");
				}
				for (var i=0; i < GSN.data.criterias.length; i++) {
					if (i > 0) {
						request += "&critJoin="+$("#critJoin"+GSN.data.criterias[i]).val();
					}
					request += "&neg="+$("#neg"+GSN.data.criterias[i]).val();
					request += "&critfield="+$("#critfield"+GSN.data.criterias[i]).val();
					request += "&critop="+$("#critop"+GSN.data.criterias[i]).val();
					request += "&critval="+$("#critval"+GSN.data.criterias[i]).val();
				}
				if ($("#intervall").attr("checked") ) {
					// from
					if (GSN.data.criterias.length > 0) {
						request += "&critJoin=AND";
					}
					request += "&neg=";
					request += "&critfield=timed";
					request += "&critop=>";
					request += "&critval="+$("#fromInterval").val();
					// to
					request += "&critJoin=AND";
					request += "&neg=";
					request += "&critfield=timed";
					request += "&critop=<";
					request += "&critval="+$("#toInterval").val();
				}			

///////////////// end of new (Sebastian)

				request = "vsName="+$("#vsName").attr("value")+request;
			    url =  GSN.baseurl+"/data?"+request;
			   //	 $("form").attr("action", "/efef");
				$("form").append("<input type=\"hidden\" name=\"gsnqueryurl\" id=\"gsnqueryurl\" value=\""+url+"\">");
                $("form").submit();
				//$("#showSQL").append("<p>requestSQL="+request+"</p>");
				//$("#showSQL").append("<p>requestURL="+url+"</p>");
	
	   	},
	   	selectDataDisplay: function() {
			$("#criterias").append("<tr><td class=\"data\" id=\"display\">");
			$("#display").append("<input type=\"radio\" id=\"samePage\" value=\"samepage\" name=\"display\" onClick=\"javascript:GSN.data.showFormatCSV()\" checked>In this page<br/>");
			$("#display").append("<input type=\"radio\" id=\"popup\" value=\"popup\" name=\"display\" onClick=\"javascript:GSN.data.showFormatCSV()\">In a new window<br/>");
			$("#display").append("<input type=\"radio\" id=\"CSV\" value=\"CSV\" name=\"display\" onClick=\"javascript:GSN.data.showFormatCSV()\">Download data<br/>");
			$("#display").append("<br/><a id=\"getDatas\" href=\"javascript:GSN.data.getDatas()\">Get data</a><br/><br/>");
	   	},
	   	showFormatCSV: function() {
	   		if ($("#CSV").attr("checked")) {
	   			$("#getDatas").before($.DIV({"id":"cvsFormat"}));
	   			$("#cvsFormat").append($.INPUT({"type":"radio", "name":"delimiter", "id":"tab", "value":"tab"})).append("tab");
	   			$("#cvsFormat").append($.BR()).append($.INPUT({"type":"radio", "name":"delimiter", "id":"space", "value":"space"})).append("space");
	   			$("#cvsFormat").append($.BR()).append($.INPUT({"type":"radio", "name":"delimiter", "id":"other", "value":"other"})).append("other : ");
	   			$("#cvsFormat").append($.INPUT({"type":"text", "name":"otherdelimiter", "id":"otherdelimiter", "size":"2"})).append($.BR()).append($.BR());
	   		} else {
	   			$("#cvsFormat").remove();
	   		}
	   	},
	   	getDatas: function(sql) {
	  		$("table#dataSet","#datachooser").remove();
	   		$("#display").append($.SPAN({"class":"refreshing"},$.IMG({"src":"/gsn/style/ajax-loader.gif","alt":"loading","title":""})));
	   		if ($("#samePage").attr("checked") || $("#popup").attr("checked")) {
	   			GSN.data.displayDatas(GSN.requestMem);
	   		}  else if ($("#CSV").attr("checked") || $("#XML").attr("checked")) {
	   			//$("form#gsn_formular").attr("action", GSN.requestMem);
	   			$("form#gsn_formular").attr("target", "_self");
	   			if(GSN.gsnFlag) {
		   			document.forms["gsn_formular"].submit();
		   			$("#display .refreshing").remove();					
		   		} else 
			   		GSN.data.displayDatasQPN(GSN.requestMem);
	   			//document.location=GSN.requestMem;
	   		}
	   	},
	   	getDatasWiki: function(sql) {
	  		$("table#dataSet","#datachooser").remove();
	   		$("#display").append($.SPAN({"class":"refreshing"},$.IMG({"src":"/gsn/style/ajax-loader.gif","alt":"loading","title":""})));
	   		//if ($("#samePage").attr("checked") || $("#popup").attr("checked") || sql) {
	   			request = "";
	   			if ($("#commonReq").attr("checked")) {
	   				request += "&commonReq=true";
	   			} else {
	   				request += "&commonReq=false";
	   				if ($("#aggregateGB").val() != "none") {
	   					request += "&groupby=" + $("#aggregateGB").val();
	   					if ($("#aggregateGB").attr("value") == "timed") {
	   						temp = $("#gbdelta").val();
	   						if ($("#gbdeltameasure").val() == "s") {
	   							temp = temp * 1000;
	   						} else if ($("#gbdeltameasure").val() == "m") {
	   							temp = temp * 60000;
	   						} else if ($("#gbdeltameasure").val() == "h") {
	   							temp = temp * 3600000;
	   						} else if ($("#gbdeltameasure").val() == "d") {
	   							temp = temp * 86400000; // 3600000 * 24
	   						}
	   						request += "&groupbytimed=" + temp
	   					}
	   				}
	   			}
	   			$("input").each(function () {
					if ($(this).attr("id") == "field" && $(this).attr("checked")) {
					   if ($("#commonReq").attr("checked")) {
    						request += "&fields=" + $(this).attr("value");
    					} else {
    					   request += "&fields=" + $("#"+$(this).val()+"AG").val()+"("+$(this).attr("value")+")";
    					}
					}
				});
				if ($("#someDatas").attr("checked") && $("#nbOfDatas").attr("value") != "") {
					request += "&nb=" + $("#nbOfDatas").attr("value");
				}
				for (var i=0; i < GSN.data.criterias.length; i++) {
					if (i > 0) {
						request += "&critJoin="+$("#critJoin"+GSN.data.criterias[i]).val();
					}
					request += "&neg="+$("#neg"+GSN.data.criterias[i]).val();
					request += "&critfield="+$("#critfield"+GSN.data.criterias[i]).val();
					request += "&critop="+$("#critop"+GSN.data.criterias[i]).val();
					request += "&critval="+$("#critval"+GSN.data.criterias[i]).val();
				}
				if ($("#intervall").attr("checked") || !GSN.gsnFlag) {
					// from
					if (GSN.data.criterias.length > 0) {
						request += "&critJoin=AND";
					}
					request += "&neg=";
					request += "&critfield=timed";
					request += "&critop=>";
					request += "&critval="+$("#fromInterval").val();
					// to
					request += "&critJoin=AND";
					request += "&neg=";
					request += "&critfield=timed";
					request += "&critop=<";
					request += "&critval="+$("#toInterval").val();
				}
				if (GSN.gsnFlag) {
					//request += "&sql=true";
					request = "vsName="+$("#vsName").attr("value")+request;
	   			
					$.ajax({
						type: "GET",
						url: GSN.baseurl+"/data?"+request+"&sql=true",
						error: function(msg) {
							alert(msg);
						},
						success: function(msg) {
							//$("#showSQL").append($.P({"class":"request"},request));
							query = msg.split(";");
							queryname = $("#queryname").attr("value");
							//$("#showSQL").append($.P({"class":"request"},queryname));
							//$("#showSQL").append($.P({"class":"request"},queryname));
							//$("#showSQL").append($.P({"class":"request"},request));
							pagetitle = "Data for "+$("#vsName").attr("value")+" with query named "+queryname;
							textbox = "{{GSN Query Statement|"+$("#vsName").attr("value")+"|"+queryname;
							request = request.replace(/>/g,"gt");
							request = request.replace(/</g,"lt");
							textbox = textbox+"|"+query[0]+"|"+GSN.baseurl+"}}";
							textbox = textbox+"\n\n{{#gsn_query_display:"+GSN.baseurl+"/data?"+request+"|"+GSN.baseurl+"}}\n";
							GSN.data.addSemanticInformation(request);
							textbox = textbox + GSN.data.text;
							//$("#showSQL").append($.P({"class":"request"},textbox));
							$.post(
								GSN.wikiUrlAddPage,
								{namespace: GSN.wikiNamespace,
								 pagetitle: pagetitle,
								 textbox: textbox },
								function(msg1) {
									reference = GSN.wikiUrlGetPage+pagetitle;
									$("#showSQL").append($.A({"class":"Wiki Page","href":reference},"Access Query"));
									$("#display .refreshing").remove();					
								}
							);
						}
					});
					
					
				}else {
					request = "AddrName="+GSN.vsName+"&AddrIP="+GSN.vsIP+"&AddrPort="+GSN.vsPort + request;
	   			
					queryname = $("#queryname").attr("value");
					//$("#showSQL").append($.P({"class":"request"},queryname));
					//$("#showSQL").append($.P({"class":"request"},queryname));
					//$("#showSQL").append($.P({"class":"request"},request));
					pagetitle = "Data for "+GSN.vsName+" with query named "+queryname;
					textbox = "{{QPN Query Statement|"+GSN.vsName+"|"+GSN.vsIP+"|"+GSN.vsPort+"}}\n\n";
					request = request.replace(/>/g,"gt");
					request = request.replace(/</g,"lt");
					textbox = textbox+"<QpnQueryDisplay request='"+GSN.baseurl+"/DataDownload?"+request+"' />\n\n";
					GSN.data.addSemanticInformation(request);
					textbox = textbox + GSN.data.text;
					//$("#showSQL").append($.P({"class":"request"},textbox));
					$.post(
						GSN.wikiUrlAddPage,
						{namespace: GSN.wikiNamespace,
						 pagetitle: pagetitle,
						 textbox: textbox },
						function(msg1) {
							reference = GSN.wikiUrlGetPage+pagetitle;
							$("#showSQL").append($.A({"class":"Wiki Page","href":reference},"Access Query"));
							$("#display .refreshing").remove();					
						}
					);
				}
	   	},
	   	addSemanticInformation: function(request) {
	   		text = "";
	   		req = request;
	   		pairs = req.split("&");
	   		var i = 0;
	   		for ( i = 0; i < pairs.length ;i++ ) {
			   	nameValue = pairs[i].split("=");
			   	if(nameValue[1]!="")
				   	text = text+"[[query_"+nameValue[0]+":="+nameValue[1]+"|  ]]";
			}
			text = text+"[[query_baseurl:="+GSN.baseurl+"| ]]";
		   	text = text+"[[query_request::"+GSN.baseurl+"/data?"+request+"| ]]";
		   	GSN.data.text = text;
	   	},
	   	displayDatas: function(request) {
	   			//url: GSN.baseurl+"/data?"+request,
	   			$.ajax({
				type: "GET",
				url: request,
				success: function(msg) {
					//remove indicator	
					$("#display .refreshing").remove();					
				
	
					//should check no field selected...
					if ($("data", msg).size() == 0) {
						alert(msg);
						return;
					}
					else if ($("line", msg).size() == 0) {
						alert('No data corresponds to your request');
						return;
					}

					var target = "#datachooser";
					if ($("#popup").attr("checked")){
						var w = window.open("", "Data", "width=700,height=700,scrollbars=yes");
						if (w == null) {
							alert('Your browser security setting blocks popup. Please turn it off for this website.');
							return;
						}
						target = w.document.body;
					}

					$("table#dataSet",target).remove();
					$(target).append($.TABLE({"size":"100%", "id":"dataSet"}));
					
					
					var line,tr,rows;
					var lines = $("line", msg);
					for (var i = 0; i<lines.size();i++){
						line = lines.get(i);
						
						if (i==0)
							tr = $.TR({"id":"line"+i, "class":"step"});
						else
							tr = $.TR({"id":"line"+i, "class":"data"});
						
						rows = $("field", line);
						for (var j = 0; j<rows.size();j++){
							$(tr).append($.TD({},$(rows.get(j)).text()));
						}
						$("table#dataSet",target).append(tr);
					}
					
					if (w != null){
						$("table#dataSet .step", target).css("background","#7FB2E1")
					}
				}
				});
			},
	   	displayDatasQPN: function(request) {
	   			//url: GSN.baseurl+"/data?"+request,
	   			request = GSN.requestMem;
	   			if ($("#CSV").attr("checked")){
	   				request = request+"&display=txt";
	   				if($("#tab").attr("checked")){
		   				request = request+"&delimiter=tab";
		   			}
		   			if($("#space").attr("checked")){
		   				request = request+"&delimiter=space";
		   			}
		   			if($("#other").attr("checked")){
		   				request = request+"&delimiter=other";
		   				request = request+"&otherdelimiter="+$("#otherdelimiter").attr("value");
		   			}
	   			} else if ($("#XML").attr("checked"))
	   				request = request+"&display=xml";
	   			$.ajax({
				type: "GET",
				url: request,
				success: function(msg) {
					//remove indicator	
					var filename = $("download-file", msg).text();
	 			   //alert(filename);
	 			   var addr = request.split("DataDownload");
					reference = addr[0]+"files/"+filename;
					$("#display").append($.A({"class":"Data File","href":reference},"Access Data File"));
					$("#display .refreshing").remove();					
	 			   
				}
			   });
	   	}
	}
	,util: {
		/**
		* Pretty print of timestamp date
		*/
		printDate: function(date){
			date = new Date(parseInt(date));
			var value = GSN.util.addleadingzero(date.getDate())+"/"+GSN.util.addleadingzero(date.getMonth()+1)+"/"+date.getFullYear();
	        value += " "+GSN.util.addleadingzero(date.getHours())+":"+GSN.util.addleadingzero(date.getMinutes())+":"+GSN.util.addleadingzero(date.getSeconds());	       
	        return value;
	    }
	    /**
		* Add a zero if less then 10
		*/
		,addleadingzero : function (num){
			var n = String(num);
			return (n.length == 1 ? "0"+n : n);
		}
	}	
};