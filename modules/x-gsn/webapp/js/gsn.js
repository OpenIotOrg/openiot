/**
 * gsn javascript
 */

//Note: Balise creation: $.BaliseName({"attribute1":"value1",...},textInsideTag)

function logIvo(msg) {
    console.info(msg);
}

var map;
var GSN = { 
	
    debugmode: false
    ,
    log: function (txt) {
        if(typeof console != "undefined")
            if(GSN.util.getURLParam("debug")==1 || GSN.debugmode) {
                console.log(txt);
            }
    }
    ,
    info: function (txt) {
        if(typeof console != "undefined")
            if(GSN.util.getURLParam("debug")==1 || GSN.debugmode) {
                console.info(txt);
            }
    }

    ,
    context: null //home, data, map || fullmap
    ,
    loaded: false
    /**
	* Initialize a page load (begin, tab click & back button)
	*/
    ,
    load: function(){
        if(GSN.loaded) return;
		
        var splittedURL = window.location.href.split('/');
        var pageName = splittedURL[splittedURL.length-1].split('.');
		
        if (pageName[0] == "index" || pageName[0] == ""){
            pageName[0] = "home";
        }
		
        var params=location.hash.substr(1).split(",");
		
        params[0] = pageName[0];
		
        GSN.context = params[0];

        //highlight the right tab in the navigation bar
        $("#navigation div").each(function(){
            if($("a",this).text()==GSN.context)
                $(this).addClass("selected");
            else
                $(this).removeClass("selected");
        });
				
        $("#main > div").hide();
        if (GSN.context!="map") {
            $("#toggleallmarkers").hide();
            $("#vsmenu .toggle").hide();
        }
        //for each page context
        if (GSN.context=="home")	{
            GSN.vsbox.container = "#vs";
            $("#main #control").show();
            $("#main #homediv").show();
            $("#control #closeall").show();
            //load and display all the visual sensors
            if (!GSN.loaded) GSN.updateall();
			
        } else if (GSN.context=="data")	{
            $("#msg").hide();
            $("#main #datachooser").show();
            if (!GSN.loaded) GSN.updateall();
        } else if (GSN.context=="map")	{
            GSN.vsbox.container = "#vs4map";
            $("#msg").hide();
            $("#main #control").show();
            $("#control #closeall").hide();
            $("#main #mapdiv").show();
            $("#toggleallmarkers").show();
            $("#vsmenu .toggle").show();
            if(!GSN.map.loaded) {
                GSN.updateall();
                GSN.map.init();
            }
			
            //take care of params
            if (params.length>1) {
                var lat=lng=zoom=null;
                for (var i=1;i<params.length;i++){
                    val = params[i].split("=");
                    if (val[0]=="lt") lat = val[1];
                    if (val[0]=="lo") lng = val[1];
                    if (val[0]=="z") zoom = parseInt(val[1]);
                }
                if (lat!=null) {
                    map.setCenterAndZoom(new LatLonPoint(lat,lng),zoom);
                }
            }
            GSN.map.showAllMarkers();
        } else if (GSN.context=="fullmap")	{
            GSN.vsbox.container = "#vs";
            if(!GSN.map.loaded) {
                GSN.map.init();
                GSN.updateall();
            }
        }
    }
	
	
    /**
	* iframe msg callback for webupload
	*/
    ,
    msgcallback: function (msg,code) {
        GSN.log(code+": "+msg);
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
	* Click on the virtual sensor on the left bar
	*/
    ,
    menu: function (vsName) {
        $("#msg").hide();
        //define the click depending the context (home,data,map)
        if (GSN.context=="home"){
            GSN.addandupdate(vsName);
        } else if (GSN.context=="map"){
            var prev;
            if ($("#vs4map div").size()!=0)
                prev = $("#vs4map div").attr("class").split(" ")[0].split("-")[1];
            if (prev != vsName) {
                $("#vs4map").empty();
                GSN.addandupdate(vsName);
                GSN.map.zoomOnMarker(vsName);
				
            } else
                GSN.vsbox.remove(vsName);
        } else if (GSN.context=="data"){
            GSN.data.init(vsName);
        } else if (GSN.context=="fullmap"){
            $(".vsbox").removeClass("followed");
            $(".vsbox-"+vsName).addClass("followed");
            GSN.map.zoomOnMarker(vsName);
        }
    }
    /**
	* Close all button
	*/
	
    ,
    closeall: function (){
        $("#msg").hide();
        $("#vs").empty();
        GSN.map.followMarker(null);
    }
	
	
    ,
    loaded : false
    /**
	* Initialize the gsn title and leftside menu
	*/
    ,
    vsName : new Array()
    ,
    protect: new Array()   // characterizes the above, if it is protected or not
    ,
    selectedSensors : new Array()
    ,
    numSensorAssociatedWithCategory : new Hash()
	
    ,
    init : function(data) {
        this.loaded=true;
        $(".loading").remove();
		
        //show all the gsn container info
        if ($(document).attr("title")=="GSN") {
            var gsn = $("gsn",data);
            $(document).attr("title",$(gsn).attr("name")+" :: GSN");
            $("#gsn-name").empty().append($(gsn).attr("name")+" :: GSN");
            $("#gsn-desc").empty().append($(gsn).attr("description"));
            $("#gsn-author").empty().append($(gsn).attr("author")+" ("+$(gsn).attr("email")+")");
        }
        //build the rightside vs menu
        $("#vsmenu").empty();



        var arraySize = 0;
        $("virtual-sensor",data).each(function(){
            name = $(this).attr("name");
                  // logIvo($(this).attr("protected"));   // just for logging
            GSN.vsName[arraySize] = name;
            GSN.protect[arraySize] = new Array(2);          // added May 2013
            GSN.protect[arraySize][0]=name;
            GSN.protect[arraySize][1] = $(this).attr("protected");
            //logIvo(GSN.vsName[arraySize]+" -- "+GSN.protect[arraySize]);
            arraySize++;

        //if ($("field[@name=latitude]",$(this)).text()!="")
        //	$("#menu-"+vsname).addClass("gpsenabled");
        });
		
        // Make a matrix of the array of the sensor with the group name in the second column
		
        //Test example
        //GSN.vsName.push("genepi_meteo_10_replay", "genepi_meteo_11_replay","genepi_meteo_12_replay","genepi_meteo_13_replay","genepi_meteo_15_replay","genepi_meteo_16_replay","genepi_meteo_18_replay","genepi_meteo_2_replay","genepi_meteo_3_replay","genepi_meteo_4_replay","genepi_meteo_6_replay","genepi_meteo_7_replay");
      GSN.vsName = GSN.util.regroupByUnderscore(GSN.vsName);
      GSN.protect.sort();
        for(var i=0;i<GSN.vsName.length;++i){
            logIvo(GSN.vsName[i]+"--"+GSN.protect[i][0]+"--"+GSN.protect[i][1]);
        }

        // Creation of the sidebar menu with categories
        var vsName = GSN.vsName;
        var previousCategoryName;
        for(var i=0;i<vsName.length;++i){
            if(vsName[i][1] != previousCategoryName && vsName[i][1] != "others"){
                // Append Group to menu if different from category others
                if(GSN.context == "data"){
                    $("#vsmenu").append($.A({
                        "class":"rubric",
                        "href":"javascript:GSN.util.toggle($(\"."+vsName[i][1]+" span\"));",
                        "id":"menu-rubric-"+vsName[i][1]+""
                    },"  Group: "+vsName[i][1]));
                    $("#menu-rubric-"+vsName[i][1]).prepend($.IMG({
                        'src':'../img/group.png'
                    }));                                                //vsmenu
                }
                else{
                    $("#vsmenu").append($.DIV({},$.A({
                        "class":"rubric",
                        "href":"javascript:GSN.util.toggle($(\"."+vsName[i][1]+" a\"));",
                        "id":"menu-rubric-"+vsName[i][1]+""
                    },"  Group: "+vsName[i][1])));
                    $("#menu-rubric-"+vsName[i][1]).prepend($.IMG({
                        'src':'../img/group.png'
                    }));
                }
                previousCategoryName = vsName[i][1];
                // New category start with 0 sensors associated
                GSN.numSensorAssociatedWithCategory.setItem(vsName[i][1],0);
            }
            if(vsName[i][1] != "others"){

                // Append Sensor Name to menu if different from category others
                if(GSN.context == "data"){
                    $("#vsmenu").append($.DIV({
                        "class":vsName[i][1]
                    },$.SPAN({
                        "class":"sensorName",
                        "id":"menu-"+vsName[i][0]+""
                    },vsName[i][0])));
                }
                else{
                    $("#vsmenu").append($.DIV({
                        "class":vsName[i][1]
                    },$.A({
                        "class":"sensorName",
                        "href":"javascript:GSN.menu('"+vsName[i][0]+"');",
                        "id":"menu-"+vsName[i][0]+""
                    },vsName[i][0])));
                }
                // increment the number of sensors associated to the rubric
                GSN.numSensorAssociatedWithCategory.setItem(vsName[i][1],GSN.numSensorAssociatedWithCategory.getItem(vsName[i][1])+1);
            }
        }// End for

		
        // Append Group Others to menu
        var othersRubricSensorPresent=false;
        for(var i=0;i<vsName.length;++i){
            if(vsName[i][1] == "others") othersRubricSensorPresent = true;
        }
		
        if(othersRubricSensorPresent){
            if(GSN.context == "data"){
                $("#vsmenu").append($.A({
                    "class":"rubric",
                    "href":"javascript:GSN.util.toggle($(\".others span\"));",
                    "id":"menu-rubric-others"
                },"  Others"));
                $("#menu-rubric-others").prepend($.IMG({
                    'src':'../img/group.png'
                }));
            }
            else{
                $("#vsmenu").append($.DIV({},$.A({
                    "class":"rubric",
                    "href":"javascript:GSN.util.toggle($(\".others a\"));",
                    "id":"menu-rubric-others"
                },"  Others")));
                $("#menu-rubric-others").prepend($.IMG({
                    'src':'../img/group.png'
                }));
            }
            GSN.numSensorAssociatedWithCategory.setItem("others",0);
			
            // Append Sensor Name to menu if it corresponds to category others
            for(var i=0;i<vsName.length;++i){
               // logIvo(vsName[i][0]+" -- "+GSN.vsName[i]+"--"+GSN.protect[i]);

                if(vsName[i][1] == "others"){
                    if(GSN.context == "data")   {
                        $("#vsmenu").append($.DIV({
                        "class":vsName[i][1]
                    },$.SPAN({
                        "class":"sensorName",
                        "id":"menu-"+vsName[i][0]
                    },vsName[i][0])));
                    } else $("#vsmenu").append($.DIV({
                        "class":vsName[i][1]
                    },$.A({
                        "class":"sensorName",
                        "href":"javascript:GSN.menu('"+vsName[i][0]+"');",
                        "id":"menu-"+vsName[i][0]+""
                    },vsName[i][0]+GSN.protect[i][1])));
                    GSN.numSensorAssociatedWithCategory.setItem(vsName[i][1],GSN.numSensorAssociatedWithCategory.getItem(vsName[i][1])+1);                    // added May 2013
                }
            }
        }
		
		
		
        // Hide all the sensors in the side bar
        $(".sensorName").hide();
		
        // Drag and Drop Functionnality
        if(GSN.context == "data"){
            // Sensors configuration
            $("#vsmenu .sensorName").draggable({
                cursor: 'move',
                helper: 'clone',
                start: function(){}
            });
			
            // Sensor Group configuration
            $("#vsmenu .rubric").draggable({
                cursor: 'move',
                helper: 'clone',
                revert: true,
                start: function(){}
            });
			
            // Drop area configuration
            $("#dropAreaMask").droppable({
                accept: ".ui-draggable",
                drop: function(ev, ui) {
                    var sensorDroppedName = $(ui.draggable.element).text();

                    if(!GSN.isAlreadyInSelectedSensorArray(sensorDroppedName)){
                        if(sensorDroppedName.substr(0,9) == "  Group: "){
                            //If a group has been dropped
                            GSN.AddSensorGroupToTheDraggableArea($(ui.draggable.element).text().substr(9));
                        }	else if (sensorDroppedName == "  Others") {
                            //If the group 'others' has been dropped
                            GSN.AddSensorGroupToTheDraggableArea("others");
                        }	else {
                            //If a sensor has been dropped
                            GSN.AddSensorToTheDraggableArea(sensorDroppedName,GSN.selectedSensors.length);
                        }
                    }
					
					
                }
            });

        }
			
    }
	
    ,
    isAlreadyInSelectedSensorArray: function(sensorName){
        // Look in the selected sensor array to see if the selected sensor don't already belongs to this former
        var alreadyBelongSelectedSensor = false;
        for(var i=0; i<GSN.selectedSensors.length; ++i){
            if(sensorName == GSN.selectedSensors[i]){
                alreadyBelongSelectedSensor = true;
                break;
            }
        }
        return alreadyBelongSelectedSensor;
    }
	
    /**
	* Add a sensor to the drop Area add the sensor to the selected sensor array
	*/
    ,
    AddSensorToTheDraggableArea: function(sensorDroppedName,colorID){
        sensorDroppedName = jQuery.trim(sensorDroppedName);
		
        // Add the dropped sensor to the drop area and set border
        $("#dropArea").prepend($.SPAN({
            'class':'sensorName',
            'id':'inDraggableArea-'+sensorDroppedName
        }));
        $("#inDraggableArea-"+sensorDroppedName).css("border-left", "10px solid #FFA84C");
		
		
        // Add a link do delete the sensor from the draggable area
        $("#inDraggableArea-"+sensorDroppedName).append("<a href=\"javascript:GSN.removeFromDraggableArea('"+sensorDroppedName+"')\"><img src=\"../img/button_cancel.png\"/></a>");
        $("#inDraggableArea-"+sensorDroppedName).append("&nbsp;&nbsp;"+sensorDroppedName+"");
		
		
		
        try{
            $("#inDraggableArea-"+sensorDroppedName).append('<div class="colorpicker" id="colorpicker-'+sensorDroppedName+'" style="display:none;position:relative;left:80px;"></div>');
            $("#inDraggableArea-"+sensorDroppedName).append('<input type="text" maxlength="7" size="7" onclick="if($(\'#colorpicker-'+sensorDroppedName+'\').css(\'display\') == \'none\'){$(\'.colorpicker\').hide(\'slow\');$(\'#colorpicker-'+sensorDroppedName+'\').show(\'slow\');$(\'#colorpickerButton-'+sensorDroppedName+'\').show();}" id="hexcode-'+sensorDroppedName+'" name="hexcode-'+sensorDroppedName+'" value="#'+DEFAULT_COLOR_CODE[colorID%DEFAULT_COLOR_CODE.length]+'" style="float:right;position:relative;top:-20px;width:55px;border-width:1px;"/>');
            $("#inDraggableArea-"+sensorDroppedName).append('<input type="button" class="colorpicker" id="colorpickerButton-'+sensorDroppedName+'" onclick="$(\'.colorpicker\').hide(\'slow\');" value="Ok" style="display:none;float:right;position:relative;top:-22px;left:-5px;"/>');

            $('#colorpicker-'+sensorDroppedName).farbtastic('#hexcode-'+sensorDroppedName);
        } catch(err) {
            $("#colorpicker-"+sensorDroppedName).remove();
            $("#hexcode-"+sensorDroppedName).remove();
            $("#inDraggableArea-"+sensorDroppedName).append('<input type="text" maxlength="7" size="7" id="hexcode-'+sensorDroppedName+'" name="hexcode-'+sensorDroppedName+'" onchange="$(this).css(\'background-color\',$(this).val())" value="#'+DEFAULT_COLOR_CODE[colorID%DEFAULT_COLOR_CODE.length]+'" style="float:right;position:relative;top:-20px;width:55px;border-width:1px;background-color:#'+DEFAULT_COLOR_CODE[colorID%DEFAULT_COLOR_CODE.length]+';"/>');
		
            txt="There was an error with farbtastic.\n\n";
            txt+="Error description: " + err.description + "\n\n";
            GSN.log(txt);
        }
		
		
        // Remove the selected sensor from the sidebar
        $("#menu-"+sensorDroppedName).remove();
		
        // Resize the drop area
        stringLength = $("#dropArea").css('height').length
        newSize = parseInt($("#dropArea").css('height').substr(0,stringLength-2))+30+'px';
        $("#dropArea").css('height',newSize);
		
        // Resize the drop area mask
        stringLength = $("#dropAreaMask").css('height').length
        newSize = parseInt($("#dropAreaMask").css('height').substr(0,stringLength-2))+30+'px';
        $("#dropAreaMask").css('height',newSize);
		
        // Add the dropped sensor to the selected sensor array
        GSN.selectedSensors.push(sensorDroppedName);
		
		
    }
	
    /**
	* Add a sensor group to the drop Area add the sensors to the selected sensor array
	*/
    ,
    AddSensorGroupToTheDraggableArea: function(groupName){
        if(confirm("Are you sure that you want to add the "+GSN.numSensorAssociatedWithCategory.getItem(groupName)+" sensors of the '"+groupName+"' group?")){
            var nbSelectedSensor = GSN.selectedSensors.length;
            var nbSensorToAdd = GSN.numSensorAssociatedWithCategory.getItem(groupName);
            var nbSensor = GSN.vsName.length;

            for(var i=0; i < nbSensor; ++i){
                if(GSN.vsName[i][1] == groupName){
                    // If this sensor is already in the selected sensor array we remove it to have after a nive grouping inside the drop area
                    if(GSN.isAlreadyInSelectedSensorArray(GSN.vsName[i][0]))
                        GSN.removeFromDraggableArea(GSN.vsName[i][0]);
					
                    // Add the sensor to the draggable area
                    GSN.AddSensorToTheDraggableArea(GSN.vsName[i][0],(nbSelectedSensor+i));
                }
            }
        }
    }
	
	
    /**
	* Remove From drop Area the sensor and remove the sensor from the selected sensor array
	*/
    ,
    removeFromDraggableArea: function(sensorName) {
        // Remove the selected sensor from the drop area
        $("#inDraggableArea-"+sensorName).remove();
        var nameCategory;
		
        // Remove the sensor from the selected sensor array
        for(var i=0; i<GSN.selectedSensors.length;++i){
            if(jQuery.trim(GSN.selectedSensors[i]) == jQuery.trim(sensorName)){
                GSN.selectedSensors.splice(i,1);
            }
        }
		
		
        // Find the category name associated with the sensor name
        for(var i=0;i<GSN.vsName.length;++i){
            if(GSN.vsName[i][0] == sensorName) nameCategory = GSN.vsName[i][1];
        }
		
        //alert(GSN.selectedSensors+"        "+nameCategory+"          "+$("#vsmenu #menu-rubric-"+nameCategory).length);
		
        // Append the sensor in the sidebar in the correct category
        $("#vsmenu #menu-rubric-"+nameCategory).after($.DIV({
            "class":nameCategory
        },$.SPAN({
            "class":"sensorName",
            "id":"menu-"+sensorName
        },sensorName)));
		
        // Make the appended sensor in the side bar draggable
        $("."+nameCategory+" #menu-"+sensorName).draggable({
            cursor: 'move',
            helper: 'clone',
            start: function(){}
        });
		
        // Resize the drop area
        stringLength = $("#dropArea").css('height').length
        newSize = parseInt($("#dropArea").css('height').substr(0,stringLength-2))-24+'px';
        $("#dropArea").css('height',newSize);
		
        // Resize the drop area mask
        stringLength = $("#dropAreaMask").css('height').length
        newSize = parseInt($("#dropAreaMask").css('height').substr(0,stringLength-2))-24+'px';
        $("#dropAreaMask").css('height',newSize);
    }
	
	
    ,
    updatenb: 0
    ,
    updateallchange: function(){
        if($("#refreshall_timeout").attr("value") != 0)
            GSN.updateall();
    }
	
	
    /**
	* Ajax call to update all the sensor display on the page and the map
	*/
    ,
    updateall: function(num,showall){
        var firstload = !GSN.loaded;
		
        //to prevent multiple update instance
        if (typeof num == "number" && num != GSN.updatenb) return;
        GSN.updatenb++;
		
        $(".refreshing").show();
		
		
  		
        $.ajax({
            type: "GET",
            url: "/gsn",
            success: function(data){
                var start = new Date();
                //initalisation of gsn info, vsmenu
                if (!GSN.loaded) GSN.init(data);
			
                //create vsbox on the first load
                if (firstload && GSN.context == "home") {
                    for (var i = 0; i < 10; ++i){
                        var n = $($("virtual-sensor",data).get(i)).attr("name");
                        if (n!=null) GSN.vsbox.add(n);
                    }
                } else if (firstload && GSN.context == "fullmap") {
                    $("virtual-sensor",data).each(function(){
                        GSN.vsbox.add($(this).attr("name"));
                    });
                }
			
                //update vsbox
                $("virtual-sensor",data).each(function(){
                    GSN.vsbox.update(this);
                });
			
			
                //next refresh
                if($("#refreshall_timeout").attr("value") > 0)
                    setTimeout("GSN.updateall("+GSN.updatenb+")", $("#refreshall_timeout").attr("value"));
			
                $(".refreshing").hide();
			
                var diff = new Date() - start;
                GSN.log("updateall time:"+diff/1000);
			
                if(firstload){
                    //update map
                    if (GSN.context=="map" || GSN.context=="fullmap"){
                        GSN.map.showAllMarkers();
                    }
                }
			
            }
        });
		
		
    }
	
	
    /**
	* Add a vsbox if it doesn't exist, bring it to front and update it
	*/
    ,
    addandupdate: function(vsName){
        GSN.vsbox.bringToFront(vsName);
        $.ajax({
            type: "GET",
            url: "/gsn?name="+vsName,
            success: function(data){
                $("virtual-sensor[@name="+vsName+"]",data).each(function(){
                    GSN.vsbox.update(this);
                });
            }
        });
    }
	
	
    /**
	* vsbox, display the vs info
	*/
    ,
    vsbox: {
        //box showing all vs info
        container: "#vs"
		
		
        /**
		* Create an empty vsbox
		*/
        ,
        add: function(vsName) {
            var vsdiv = "vsbox-"+vsName;
			
            if($(this.container).find("."+vsdiv).size()!=0) return; //already exists
			
			
            $(this.container).append($.DIV({
                "class":vsdiv+" vsbox"
            },
            $.H3({},$.SPAN({
                "class":"vsname"
            },vsName),
            $.A({
                "href":"javascript:GSN.vsbox.remove('"+vsName+"');",
                "class":"close"
            },$.IMG({
                'src':'./img/button_cancel.png'
            })),
            $.SPAN({
                "class":"timed"
            },"loading...")
            ),$.UL({
                "class":"tabnav"
            },
            $.LI({},$.A({
                "href":"javascript:GSN.vsbox.toggle('"+vsName+"','dynamic');",
                "class":"tabdynamic active"
            },"Real-Time")),
            $.LI({},$.A({
                "href":"javascript:GSN.vsbox.toggle('"+vsName+"','static');",
                "class":"tabstatic"
            },"Addressing")),
            $.LI({},$.A({
                "href":"javascript:GSN.vsbox.toggle('"+vsName+"','structure');",
                "class":"tabstructure"
            },"Structure")),
            $.LI({},$.A({
                "href":"javascript:GSN.vsbox.toggle('"+vsName+"','description');",
                "class":"tabdescription"
            },"Description")),
            $.LI({},$.A({
                "href":"javascript:GSN.vsbox.toggle('"+vsName+"','upload');",
                "class":"tabupload"
            },"Upload")),
            $.LI({},$.A({
                "href":"./data.html",
                "class":"tabdata"
            },"Download"))
            ),
            $.DL({
                "class":"dynamic"
            }),
            $.DL({
                "class":"static"
            }),
            $.DL({
                "class":"structure"
            }),
            $.DL({
                "class":"description"
            }),
            $.DL({
                "class":"upload"
            }/*,
									  
									  	$.FORM({"action":"/upload","method":"post","enctype":"multipart/form-data","target":"webupload"},
									  		$.INPUT({"type":"hidden","name":"vsname","value":vsName}),
									  		$.SELECT({"class":"cmd","name":"cmd"}),
									  		$.DL({"class":"input"}),
									  		$.INPUT({"type":"submit","value":"upload"}),
									  		$.P({},"* compulsary fields.")
									  	)*/
            )
            ,$.DL({
                "class":"data"
            })
            ));
			
            $(this.container).find("."+vsdiv+" dl.upload").html('<form target="webupload" enctype="multipart/form-data" method="post" action="/upload"><input value="'+vsName+'" name="vsname" type="hidden"><select name="cmd" class="cmd"></select><dl class="input"></dl><input value="upload" type="submit"><p>* compulsary fields.</p></form>');
            $(this.container).find("."+vsdiv+" select.cmd").bind("change", function(event) {
                GSN.vsbox.toggleWebInput(event)
            });
        }
		
		
        /**
		* Bring a vsbox at the beginning of the container
		*/
        ,
        bringToFront: function(vsName) {
            this.add(vsName);
            var vsdiv = "vsbox-"+vsName;
            $("."+vsdiv, $(this.container)).hide();
            $(this.container).prepend($("."+vsdiv, $(this.container)));
            $("."+vsdiv, $(this.container)).fadeIn("slow");
        }
		
		
        /**
		* Update and show all the data of the vsbox
		*/
        ,
        update: function (vs){

            //when map is enable, update marker
            if (GSN.map.loaded){
                var lat = $("field[@name=latitude]",vs).text();
                var lon = $("field[@name=longitude]",vs).text();
                if (lat != "" && lon != ""){
                    GSN.map.updateMarker($(vs).attr("name"),lat,lon);
                }
            }
			
            //update the vsbox
            var vsd = $(".vsbox-"+$(vs).attr("name"), $(this.container))[0];
            if (typeof vsd == "undefined") return;
            //if (vsd.css("display")=="none") return;
			
            var vsdl = $("dl", vsd);
            var dynamic = vsdl.get(0);
            var static_ = vsdl.get(1);
            var struct = vsdl.get(2);
            var input = $("dl.input",vsdl.get(4));
            dl = dynamic;
			
            var name,cat,type,value;
            var last_cmd,cmd;
            var hiddenclass ="";
            //update the vsbox the first time, when it's empty
            if ($(dynamic).children().size()==0 && $(static_).children().size()==0){
                var gotDynamic,gotStatic,gotInput = false;
                $("field",vs).each(function(){
                    name = $(this).attr("name");
                    cat = $(this).attr("category");
                    cmd = $(this).attr("command");
                    type = $(this).attr("type");
                    value = $(this).text();
				
                    if (name=="timed") {
                        //if (value != "") value = GSN.util.printDate(value);
                        $(vsd).find("span.timed").empty().append(value);
                        return;
                    }
				
                    if (cat=="input") {
                        dl = input;
                        if (!gotInput) {
                            $(vsd).find("a.tabupload").show();
                            gotInput = true;
                        }
                    } else if (cat=="predicate") {
                        dl = static_;
                        if (!gotStatic) {
                            $("a.tabstatic", vsd).show();
                            if (!gotDynamic) {
                                $(vsd).find("a.tabstatic").addClass("active");
                                $(vsd).find("> dl").hide();
                                $(vsd).find("dl.static").show();
                            }
                            gotStatic = true;
                        }
                    } else {
                        //add to structure

                        var s = type ;
                        if ($(this).attr("description")!=null)
                            s += ' <img src="style/help_icon.gif" alt="" title="'+$(this).attr("description")+'"/>';
                        $(struct).append('<dt>'+name+'</dt><dd class="'+name+'">'+s+'</dd>');
                        if (!gotDynamic) {
                            $("a.tabdynamic", vsd).show();
                            $("a.tabstructure", vsd).show();
                            gotDynamic = true;
                        }
                    }

                    $(vsd).find("dl.data").show();
							                      // $(this).attr("name")
                    //set the value
                    if (cat == null) {
                        if (value == "") {
                            value = "null";
                        } else if (type.indexOf("svg") != -1){
                            value = '<embed type="image/svg+xml" width="400" height="400" src="'+value+'" PLUGINSPAGE="http://www.adobe.com/svg/viewer/install/" />';
                        } else if (type.indexOf("image") != -1){
                            value = '<img src="'+value+'"/>';
                        } else if (type.indexOf("text") != -1) {
                            $.ajax({
                                async: false,
                                type: "GET",
                                cache: false,
                                url: value,
                                success: function(answer) {
                                    value = '<span>' + GSN.util.resumelongsentences(answer) + '</span>';
                                }
                            });
                        } else if (type.indexOf("binary") != -1){
                            value = '<a href="'+value+'">download <img src="style/download_arrow.gif" alt="" /></a>';
                        }
                    } else if (cat == "input") {
                        if (last_cmd != cmd) {
                            if (last_cmd != null) hiddenclass = ' hidden';
                            $("select.cmd", vsd).append($.OPTION({},cmd));
                            last_cmd = cmd;
                        }
                        var comp = '';
                        if (type.substr(0,1)=="*") {
                            comp = '*';
                            type=type.substr(1);
                        }

                        if (type.split(":")[0].indexOf("binary") != -1){
                            value = '<input type="file" name="'+cmd+";"+name+'"/>';
                        } else if (type.split(":")[0].indexOf("select") != -1){
                            var options = type.split(":")[1].split("|");
                            value = '<select name="'+cmd+";"+name+'">';
                            for (var i = 0; i < options.length;i++){
                                value += '<option>'+options[i]+'</option>';
                            }
                            value += '</select>';
                        }  else if (type.split(":")[0].indexOf("radio") != -1 ||
                            type.split(":")[0].indexOf("checkbox") != -1){
                            var options = type.split(":")[1].split("|");
                            value = '';
                            for (var i = 0; i < options.length;i++){
                                value += '<input type="'+type.split(":")[0]+'" name="'+cmd+";"+name+'" value="'+options[i]+'">'+options[i]+'</input>';
                            }
                        } else {
                            value = '<input type="text" name="'+cmd+";"+name+'"/>';
                        }
                        if ($(this).attr("description")!=null)
                            value += ' <img src="style/help_icon.gif" alt="" title="'+$(this).attr("description")+'"/>';

                        name = comp+name;
                    }
                    $(dl).append('<dt class="'+cmd+hiddenclass+'">'+name+'</dt><dd class="'+name+((cmd!=null)?' '+cmd:'')+hiddenclass+'">'+value+'</dd>');
                });
			  
                if ($(vs).attr("description")!="") {
                    var i;
                    for(i=0;i<GSN.vsName.length;++i){
                        //logIvo(GSN.vsName[i][0]);                 // added May 2013
                        //logIvo($(vs).attr("name"));
                        if (GSN.vsName[i][0] == $(vs).attr("name")) {
                            break;
                        }
                    }
                    if (GSN.protect[i][1] == " ") {
                         value2 =  $(vs).attr("description");
                         $("dl.description", vsd).empty().append('<p> Bla').append($.DD({}, value2)).append('</p>');
                         logIvo("Iter = "+$(vs).attr("description"));
                    } else  {
                        // $("dl.description", vsd).empty().append($.DD({},$(vs).attr("description")));
                        value2 =  $(vs).attr("description");
                        individual_values = value2.split('#');
                        value3 = [];
                        value3.push('<dl>');

                        for(i=0; i<individual_values.length;i++)  {
                            parts = individual_values[i].split('@');
                            value3.push('<dt>'+parts[0]+'</dt> <dd>'+parts[1]+'</dd>');
                            //logIvo("Vals = "+parts[0]+" -- "+parts[1]);

                        }
                        value3.push('</dl>');
                        var res = value3.join("").toString();
                        //logIvo("Vals = "+res);
                        var dummy=" Privately Owned Sensor ";
                        $("dl.description", vsd).empty().append(res).append('<dl> <dt> Visibility: </dt> </dl>').append($.DD({}, dummy));
                        //$("dl.description", vsd).empty().append('<dl> <dt> Bla: </dt> <dd>').append($.DD({}, value2)).append('</dd> </dl>');
                    }
                    $("a.tabdescription", vsd).show();
                    if (!gotStatic) {
                        $(vsd).find("a.tabdescription", vsd).addClass("active");
                        $(vsd).find("> dl").hide();
                        $(vsd).find("dl.description").show();
                        //logIvo("Iter2 = "+  $(vsd).find("dl.description"));
                    }

                }
                $(vsd).find("img").ToolTip();
                return true;
            } else {
                //update the vsbox when the value already exists
                var dds = $("dd",dl);
                var dd,field;
                for (var i = 0; i<dds.size();i++){
                    dd = dds.get(i);
                    field = $("field[@name="+$(dd).attr("class")+"]",vs);
                    type = $(field).attr("type");
                    value = $(field).text();
                    if (value!="") {
                        if (type.indexOf("svg") != -1){
                            $("embed",dd).attr("src",value);
                        } else if (type.indexOf("image") != -1){
                            $("img",dd).attr("src",value);
                        } else if (type.indexOf("text") != -1) {
                            $.ajax({
                                async: false,
                                type: "GET",
                                cache: false,
                                url: value,
                                success: function(answer) {
                                    $("span",dd).text(GSN.util.resumelongsentences(answer));
                                }
                            });
                        } else if (type.indexOf("binary") != -1){
                            $("a",dd).attr("href",value);
                        } else {
                            $(dd).empty().append(value);
                        }
                    }
                }
                value = $("field[@name=timed]",vs).text();
                //if (value != "") value = GSN.util.printDate(value);
                $("span.timed", vsd).empty().append(value);
                return false;
            }
        }
		
		
        /**
		* Remove the vsbox from the container
		*/
        ,
        remove: function (vsName) {
            var vsdiv = "vsbox-"+vsName;
            $("."+vsdiv, $(this.container)).remove();
        }
		
		
        /**
		* Vsbox tabs control
		*/
        ,
        toggle: function (vsName,dl){
            var vsdiv = "vsbox-"+vsName;
            $("."+vsdiv+" > dl", $(this.container)).hide();
            $("."+vsdiv+" > dl."+dl, $(this.container)).show();
            $("."+vsdiv+" a", $(this.container)).removeClass("active");
            $("."+vsdiv+" a.tab"+dl, $(this.container)).addClass("active");
        }
        ,
        toggleWebInput: function (event){
            var cmd = event.target.options[event.target.selectedIndex].text;
            $(event.target).parent().find("dt").hide();
            $(event.target).parent().find("dd").hide();
            $(event.target).parent().find("dt."+cmd).show();
            $(event.target).parent().find("dd."+cmd).show();
        }
    },
	
	
    /**
	* All the map thing
	*/
    map: {
        loaded: false //the #vsmap div is initialized
        ,
        markers : new Array()
        ,
        highlighted : null
        ,
        highlightedmarker : null
		
		
        /**
		* Initialize the map
		*/
        ,
        init : function(){
            this.loaded=true;
            map.setCenterAndZoom(new LatLonPoint(0,0),1);
			
            // Setting the map type
            var map_type;
            if(DEFAULT_MAP_TYPE=="road"){
                map_type = Mapstraction.ROAD;
            } else if(DEFAULT_MAP_TYPE=="satellite") {
                map_type = Mapstraction.SATELLITE;
            } else if(DEFAULT_MAP_TYPE=="hybrid") {
                map_type = Mapstraction.HYBRID;
            } else {
                alert("Error: "+DEFAULT_MAP_TYPE+" is an unknown map type");
                return;
            }
			  
            map.setMapType(map_type);
		    
            //set the different control on the map
            map.addMapTypeControls();
            map.addLargeControls();
			
        }
		
		

		
		
        /**
		* Add marker
		*/
        ,
        addMarker: function(vsName,lat,lon){
            var marker = new Marker(new LatLonPoint(lat,lon));
            marker.setAttribute("vsname",vsName);
  		
  		
            if(mapProvider=="microsoft"){
                marker.setIcon("./img/green_marker.png");
                marker.setInfoBubble("Show/Hide Information: <a style='text-decoration:underline;color:blue;' href='javascript:GSN.menu(\""+vsName+"\");if (GSN.context==\"fullmap\")GSN.vsbox.bringToFront(\""+vsName+"\");'>"+vsName+"</a>");
                GSN.map.markers.push(marker);
            }
            if(mapProvider=="google"){
                marker.setIcon("./img/green_marker.png");
                marker.setInfoBubble("<script>GSN.menu(\""+vsName+"\");if (GSN.context=='fullmap')GSN.vsbox.bringToFront(\""+vsName+"\");</script>Selected Sensor: "+vsName);
                GSN.map.markers.push(marker);
            }
            if(mapProvider=="yahoo"){
                marker.setInfoBubble("<script>GSN.menu(\""+vsName+"\");if (GSN.context=='fullmap')GSN.vsbox.bringToFront(\""+vsName+"\");</script>Selected Sensor: "+vsName);
                GSN.map.markers.push(marker);
            }
			
            map.addMarker(marker);
            //add gpsenable class
            $("#menu-"+vsName).addClass("gpsenabled");
			
            if(GSN.context=="fullmap"){
                var vs = $(".vsbox-"+vsName+" > h3 > span.vsname")
                $(vs).wrap("<a href=\"javascript:GSN.menu('"+$(vs).text()+"');\"></a>");
            }
        }
		
		
        /**
		* Update marker
		*/
        ,
        updateMarker: function(vsName,lat,lon){
            for (x=0; x<GSN.map.markers.length; x++) {
                var m = GSN.map.markers[x];
                if (m.getAttribute("vsname") == vsName) {
                    m.hide();
                    map.removeMarker(m);
                    GSN.map.markers.splice(x,1);
                }
            }
            GSN.map.addMarker(vsName,lat,lon);
        }
		
		
        /**
		* Highlight a marker
		* Stop it if called with null name
		*/
        ,
        zoomOnMarker: function(vsName){
            if (!GSN.map.loaded) return;
			
            if (vsName!=null) {
                for (x in GSN.map.markers) {
                    var m = GSN.map.markers[x];
                    if (m.getAttribute("vsname") == vsName) {
                        GSN.map.highlighted = x;
                        map.setCenter(new LatLonPoint(m.location.lat,m.location.lon))
                        return;
                    }
                }
            }
        }
		
        ,
        areVisible: true
        ,
        toggleAllMarkers: function(){
            for (x=0; x<GSN.map.markers.length; x++) {
                var m = GSN.map.markers[x];
                if(GSN.areVisible) m.hide();
                else m.show();
            }
            GSN.areVisible = !GSN.areVisible;
        }
		
		
        /**
		* Zoom out to see all marker
		*/
        ,
        showAllMarkers: function(){
            map.autoCenterAndZoom();
        }
    }
	
	
    /**
	* Data part
	*/	
    ,
    data : {
	
        fields : new Array(),
        fields_type : new Array(),
        criterias : new Array(),
        nb_crit : 0,
        radio:false,
		
        /**
		* Initialisation of the Data part (called as soon as a user selected sensors and click on the button)
		*/	
        init: function(){
            GSN.data.fields.splice(0);
            GSN.data.fields_type.splice(0);
			
            $("#step1Container .data").hide("slow").prev().unbind('click').click(function(){
                $(this).next().toggle("slow");$(this).next().next().toggle("slow");
            });
			
            //remove the deselection function and drag and drop
            $("#dropArea img").remove();
            $(".sensorName").draggableDisable();
            $(".rubric").draggableDisable();
            $('.nextStepButton').remove();
			
			
            // Remove eventual duplicate in the selected sensors array
            for(var i=1; i<GSN.selectedSensors.length; ++i)
                if(GSN.selectedSensors[i-1] == GSN.selectedSensors[i]) GSN.selectedSensors.splice(i,1);
			
			
            $("form").attr("action","");
            $("#dataSet").remove();
            $("#step2Container").empty();
            $("#resetButton").show();
			
            $("#formular").append("<input type=\"hidden\" name=\"numberSelectedSensor\" id=\"numberSelectedSensor\" value=\""+GSN.selectedSensors.length+"\">");
            for(var i=0; i<GSN.selectedSensors.length; ++i)
                //TODO                $("#formular").append("<input type=\"hidden\" name=\"vsName"+i+"\" id=\"vsName"+i+"\" value=\""+GSN.selectedSensors[i]+"\">");
                $("#formular").append("<input type=\"hidden\" name=\"vsname\" id=\"vsName"+i+"\" value=\""+GSN.selectedSensors[i]+"\">");

            $("#step2Container").append("<div class=\"step\">Step 2/5 : Selection of the Fields</div>");
            $("#step2Container").append("<div class=\"data\" id=\"fields\"></div>");
            $('#fields').append('<br />');
            $('#fields').append('<span>Aggregation</span>');
            $('#fields').append('<select id="agg_function" name="agg_function"><option value="-1">No Aggregation</option><option value="avg">AVG</option><option value="max">MAX</option><option value="min">MIN</option></select>');
            $('#fields').append('<input disabled="disabled" id="agg_period" name="agg_period" size="5" value="2" type="text" />');
            $('#fields').append('<select disabled="disabled" id="agg_unit" name="agg_unit"><option value="3600000">Hours</option><option value="60000">Minutes</option><option value="1000">Seconds</option><option value="1">Milli Seconds</option></select>');
            $('#fields').append('<br /><br />');
            
            $('#agg_function').change(function(){
                $('#agg_period').attr('disabled',$(this).val() == '-1');
                $('#agg_unit').attr('disabled',$(this).val() == '-1');
            });

            //$("#fields").append("<input type=\"radio\" id=\"commonReq\" name=\"commonReq\" onClick=\"javascript:GSN.data.radio=false;GSN.data.init();\" />Common request ");
            //$("#fields").append("<input type=\"radio\" id=\"aggregReq\" name=\"aggregReq\" onClick=\"javascript:GSN.data.radio=true;GSN.data.init();\" />Aggregate functions<br/><br/>");
            $("#fields").append($.DIV({
                'id':'separation'
            },""));
            //            if (GSN.data.radio) {
            //                $("#aggregReq").attr("checked", true);
            //            } else {
            //                $("#commonReq").attr("checked", true);
            //            }

            for(var i=0; i<GSN.selectedSensors.length; ++i){
                $.ajax({
                    async: false,
                    type: "GET",
                    url: "/gsn?REQUEST=113&name="+GSN.selectedSensors[i],
                    success: function(msg) {
                        $("virtual-sensor field", msg).each(function() {
                            if ($(this).attr("type").substr(0,3) != "bin") {
                                GSN.data.fields.push($(this).attr("name"));
                                GSN.data.fields_type.push($(this).attr("type"));
                            }
                        });
                        GSN.data.fields.push("end");
                        GSN.data.fields_type.push("end");
                    }
                });
            }
            GSN.data.selectCommonFieldAndDisplay();
        },
		
        /**
		* Field part: select the common field from the selected sensors
		*/	
        selectCommonFieldAndDisplay: function(){
            //Find Common Fields
            var tempCommonFields = new Array();
            var secondEndSeen;
            var firstEndSeen = -1;
			
            //look for the first end position in the GSN.data.fields array
            for(var k=0; k<GSN.data.fields.length; ++k){
                if(GSN.data.fields[k] == "end"){
                    secondEndSeen = k;
                    break;
                }
            }
			
            //if more than one sensor selected
            if(GSN.selectedSensors.length > 1){
                for(var k=secondEndSeen+1; k<GSN.data.fields.length; ++k){
					
                    if(GSN.data.fields[k] != "end"){
                        for(var m=firstEndSeen+1; m<secondEndSeen; ++m){
                            if(GSN.data.fields[k] == GSN.data.fields[m]){
                                tempCommonFields.push(GSN.data.fields[k]);
                            }
                        }
                    }
                    else{
                        //if => GSN.data.fields[k] == "end"
                        firstEndSeen = secondEndSeen;
                        secondEndSeen = GSN.data.fields[k];
                    }
                }
                GSN.data.fields = tempCommonFields;
            } else {
                GSN.data.fields.pop();
            }
			
            if(GSN.selectedSensors.length > 1){
                $("#separation").append("Common Found Fields:<br/>");
            } else {
                $("#separation").append("Select field(s):<br/>");
            }
			
            //Display Common Fields
            for(var i=0; i<GSN.data.fields.length; i++){
                if (GSN.data.radio) {
                    //if (($(this).attr("type") == "int") || ($(this).attr("type") == "long") || ($(this).attr("type") == "double")) {
                    $("#separation").append("<div id='" + GSN.data.fields[i] + "'><input type=\"checkbox\" name=\"fields\" class=\"field\" value=\""+GSN.data.fields[i]+"\" onClick=\"javascript:GSN.data.aggregateSelect('"+GSN.data.fields[i]+"',this.checked)\">"+GSN.data.fields[i].prettyString()+" </div>");
                //}
                } else {
                    $("#separation").append("<input type=\"checkbox\" name=\"fields\" class=\"field\" value=\""+GSN.data.fields[i]+"\">"+GSN.data.fields[i].prettyString());
                    $("#separation").append("<br\>");
                }
				
                if (GSN.data.radio) {
                    $("#groupByContainer").empty();
                    $("#separation").append("<span id=\"groupByContainer\">");
                    $("#groupByContainer").append("<br/>Group by : <select name=\"aggregateGB\" id=\"aggregateGB\" size=\"1\" onChange=\"javascript:GSN.data.groupBy(this.value)\"></select><br/>");
                    for (var j=0; j < GSN.data.fields.length; j++) {
                        $("#aggregateGB").append("<option value=\"" + GSN.data.fields[j] + "\">" + GSN.data.fields[j].prettyString() + "</option>");
                    }
                    $("#aggregateGB").append("<option value=\"none\">None</option>");
                    $("#aggregateGB").append("</select>");
                    $("#separation").append("</span>");
                } else {
                    $("#checkAllContainer").remove();
                    $("#separation").append("<span id=\"checkAllContainer\"><br/><input type=\"checkbox\" id=\"all\" name=\"all\" onClick=\"javascript:GSN.data.checkAllFields(this.checked)\">Check all<br/></span>");
                }
            }
			
			
			
            GSN.data.appendNextStepButton("separation","if(GSN.data.atLeastOneFieldSelected())GSN.data.nbDatas()");
        },
		
        atLeastOneFieldSelected: function(){
            var n = $(".field:checked").length;
            if(n > 0) return true
            else alert("You have to select at least one field.");
        },
		
        /**
		* Append a button in "idInWichAppend" with an action: "onclick" an "idButton" and a "value" (configurable)
		*/	
        appendNextStepButton: function(idInWichAppend,onclick,idButton,value){
            if(idButton==null) idButton="";
            if(value==null) value="Next Step";
            $("#"+idInWichAppend+"").append('<br/><div class="nextStepButton"><input type="button" id="'+idButton+'" value="'+value+'" onClick="'+onclick+';"/>'+"&nbsp&nbsp&nbsp<input type=\"button\" id=\"resetButton\" value=\"Reset\" onClick=\"window.location='./data.html';\"/></div>");
        },
		
		
        aggregateSelect: function(that, checked){
            // To can choose the aggregate type for the field
            if (checked) {
                $("#"+that).append("<select name=\""+that+"AG\" id=\""+that+"AG\" size=\"1\"></select>");
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
                if ($(this).attr("class") == "field") {
                    $(this).attr("checked", check);
                }
            });
        },
		
		
        nbDatas: function() {
            $(".nextStepButton").remove();
            $("#step2Container .data").hide("slow").prev().unbind('click').click(function(){
                $(this).next().toggle("slow");
            });
            $("#step2Container .data :enabled").attr("disabled", "disabled");
			
            $("#step3Container").append("<div class=\"step\">Step 3/5 : Selection of the Data Range</div>");
            $("#step3Container").append("<div class=\"data\" id=\"nbDatas\"></div>");
            $("#nbDatas").append('<br /><select id="nb_selection" name="nb_selection"><option value="ALL">All Data</option><option value="SPECIFIED" selected="selected">Only</option></select>');
            $("#nbDatas").append('<input id="nb_value" name="nb_value" size="3" value="10" type="text"><span> Values</span><br />');
            $('#nb_selection').change(function(){
                var val = $(this).val();
                $('#nb_value').attr('disabled', (val == 'ALL'));
            });
            //            $("#nbDatas").append("<input type=\"radio\" name=\"nbdatas\" id=\"allDatas\" value=\"\"> All data<br/>");
            //            $("#nbDatas").append("<input type=\"radio\" name=\"nbdatas\" id=\"someDatas\" value=\"\" checked onclick=\"$('#nbOfDatas').focus()\"> Last <input onclick=\"$('#someDatas').attr('checked', 'checked');\" type=\"text\" name=\"nb\" value=\"10\" id=\"nbOfDatas\" size=\"4\"/> values<br/>");
            GSN.data.appendNextStepButton("nbDatas","GSN.data.addCriteria(true)");
        },
		
		
        addCriteria: function(newStep) {
            if (newStep) {
                $(".nextStepButton").remove();
                $("#step3Container .data").hide("slow").prev().unbind('click').click(function(){
                    $(this).next().toggle("slow");
                });
                $("#step3Container .data :enabled").attr("disabled", "disabled");
				
                $("#step4Container").append("<div class=\"step\">Step 4/5 : Selection of the Criterias</div>");
                $("#step4Container").append("<div class=\"data\" id=\"where\"></div>");
                $('#where').append('<a id="add_criterion_btn" href="#">Add Criterion</a><div id="list_of_criteria"></div>');
                //$("#where").append("<a id=\"addCrit\" href=\"javascript:GSN.data.addCriteria(false)\">Add criteria</a>");
                //                $("#where").append('<br/><br/>');
                GSN.data.appendNextStepButton("where","GSN.data.selectDataDisplay()");
                
                $('#add_criterion_btn').click(function() {
                    $(this).next().append(addCriteriaLine());
                    $('.remove-criterion').click (function() {
                        $(this).parent().remove();
                        $('.join:first').hide();
                    });
                    $('.join:first').hide();
                    $('.cfields').change(function(){
                        var field_name = $(this).val();
                        if (field_name == 'timed') {
                            $(this).next().val(GSN.util.printDate((new Date()).getTime()));
                            $(this).next().datePicker({
                                startDate:'01/01/2006'
                            });
                            $(this).next().next().val(GSN.util.printDate((new Date()).getTime()));
                            $(this).next().next().datePicker({
                                startDate:'01/01/2006'
                            });
                        }
                        else {
                            $(this).next().replaceWith(default_min_criterion());
                            $(this).next().next().replaceWith(default_max_criterion());
                        }
                    });
                });
                function addCriteriaLine () {
                    newcrit = '<div>';
                    newcrit += '<select class="join" name="cjoin"><option value="and">AND</option><option value="or">OR</option></select>';
                    newcrit += '<select name="cfield" class="cfields">';
                    for (var i = 0 ; i < GSN.data.fields.length ; i++) {
                        newcrit += '<option value="' + GSN.data.fields[i] + '">' + GSN.data.fields[i] + '</option>';
                    }
                    newcrit += '</select>';
                    newcrit += '| Between ' + default_min_criterion();
                    newcrit += ' and ' + default_max_criterion() + '<a class="remove-criterion" href="#"> Remove</a></div>';
                    return newcrit;
                }
                function default_min_criterion() {
                    return '<input name="cmin" class="min" size="3" value="-inf" type="text" />';
                }
                function default_max_criterion() {
                    return '<input name="cmax" class="max" size="3" value="+inf" type="text" />';
                }
            }

        //else {
        //    GSN.data.nb_crit++;
        //    newcrit = "<div id=\"where" + GSN.data.nb_crit + "\"></div>";
        //    $("#addCrit").before(newcrit);
        //GSN.data.addCriteriaLine(GSN.data.nb_crit, "");
        //GSN.data.criterias.push(GSN.data.nb_crit);
        //}
        },
		
        //        addCriteriaLine: function() {
        //        addCriteriaLine: function(nb_crit, field) {

        //            return newcrit;
        //            newcrit = "";
        //            if (GSN.data.criterias.length > 0) {
        //                newcrit += "<select name=\"critJoin\" id=\"critJoin" + nb_crit + "\" size=\"1\">";
        //                var critJoin = new Array("AND", "OR");
        //                for (var i=0; i < critJoin.length; i++) {
        //                    newcrit += "<option value=\""+critJoin[i]+"\">"+critJoin[i]+"</option>";
        //                }
        //                newcrit += "</select>";
        //            }
        //            newcrit += "<select name=\"neg\" size=\"1\" id=\"neg" + nb_crit + "\">";
        //            var neg = new Array("", "NOT");
        //            for (i=0; i < neg.length; i++) {
        //                newcrit += "<option value=\"" + neg[i] + "\" >" + neg[i] + "</option>";
        //            }
        //            newcrit += "</select> ";
        //            newcrit += "<select name=\"critfield\" id=\"critfield" + nb_crit + "\" size=\"1\" onChange=\"javascript:GSN.data.criteriaForType(this.value,"+nb_crit+")\">";
        //            for (var i=0; i< GSN.data.fields.length; i++) {
        //                newcrit += "<option value=\"" + GSN.data.fields[i] + "\">" + GSN.data.fields[i] + "</option>";
        //            }
        //            newcrit += "</select> ";
        //            var operators = new Array("&gt;", "&ge;", "&lt;", "&le;", "=", "LIKE");
        //            newcrit += "<select name=\"critop\" size=\"1\" id=\"critop" + nb_crit + "\">";
        //            for (i=0; i < operators.length; i++) {
        //                newcrit += "<option value=\"" + operators[i] + "\" >" + operators[i] + "</option>";
        //            }
        //            newcrit += "</select> ";
        //            newcrit += "<input type=\"text\" name=\"critval\" id=\"critval" + nb_crit + "\" size=\"18\">";
        //            newcrit += " <a href=\"javascript:GSN.data.removeCrit("+nb_crit+")\" id=\"remove" + nb_crit + "\"> (remove)</a>";
        //            $("#where"+nb_crit).append(newcrit);
        //            $("#critfield"+nb_crit).attr("value", field);
        //            GSN.data.criteriaForType(GSN.data.fields[0],nb_crit);
        //        },
		
		
        //        criteriaForType: function(field, nb_crit) {
        //            if (field == "timed") {
        //                $("#critval"+nb_crit).val(GSN.util.printDate((new Date()).getTime()));
        //                $("#critval"+nb_crit).datePicker({
        //                    startDate:'01/01/2006'
        //                });
        //            } else {
        //                $("#critval"+nb_crit).val("");
        //            }
        //        },
		
		
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
   	
   	
        selectDataDisplay: function() {
            $(".nextStepButton").remove();
            $("#step4Container .data").hide("slow").prev().unbind('click').click(function(){
                $(this).next().toggle("slow");
            });
            $("#step4Container .data :enabled").attr("disabled", "disabled");
	   	
            $("#step5Container").append("<div class=\"step\">Step 5/5 : Selection of the Format</div>");
            $("#step5Container").append("<div class=\"data\" id=\"display\"></div>");
            //$("#display").append($.DIV({
            //    "id":"showSQL"
            //},$.A({
            //    "href":"javascript:GSN.data.getDatas(true);"
            //},"Show SQL query")));
            $("#display").append("<input type=\"radio\" id=\"dispChartAndData\" value=\"DispChartAndValue\" name=\"display\" onClick=\"javascript:$('.chartOption').show('slow');$('.cvsFormat').hide('slow');\" checked>Plot and View Data<br/>");
            $("#display").append("<input type=\"radio\" id=\"dispChart\" value=\"DispChart\" name=\"display\" onClick=\"javascript:$('.chartOption').show('slow');$('.cvsFormat').hide('slow');\">Plot Data Only<br/>");
            $("#display").append("<input type=\"radio\" id=\"dispData\" value=\"DispData\" name=\"display\" onClick=\"javascript:$('.chartOption').hide('slow');$('.cvsFormat').hide('slow');\">View Data Only<br/>");
				
            $("#display").append("<input type=\"radio\" id=\"popup\" value=\"popup\" name=\"display\" onClick=\"javascript:$('.chartOption').show('slow');$('.cvsFormat').hide('slow');\">In a new window<br/>");
            $("#display").append("<input type=\"radio\" id=\"CSV\" value=\"CSV\" name=\"display\" onClick=\"javascript:$('.chartOption').hide('slow');$('.cvsFormat').show('slow');\">Download data<br/>");
            $("#display").append('<br/>');

			
            $("#display").append("<div class=\"chartOption\">Chart Selection:</div>");
            $("#display").append("<div class=\"chartOption\"><input type=\"radio\" id=\"barChart\" class=\"chartType\" name=\"chartType\" onClick=\"\" checked/>Bar Chart<br/></div>");
            $("#display").append("<div class=\"chartOption\"><input type=\"radio\" id=\"lineChart\" class=\"chartType\" name=\"chartType\" onClick=\"\"/>Line Chart<br/></div>");
            $("#display").append("<div class=\"chartOption\">Display:</div>");
            $("#display").append("<div class=\"chartOption\"><input type=\"radio\" id=\"allData\" class=\"dataDisplay\" name=\"dataDisplay\" onClick=\"\" checked/>All Values<br/></div>");
            $("#display").append("<div class=\"chartOption\"><input type=\"radio\" id=\"modData\" class=\"dataDisplay\" name=\"dataDisplay\" onClick=\"\"/>Snapshot Mode<br/></div>");
			
						
            //$("#display").append("<div class=\"cvsFormat\">Delimiters:</div>");
			
            //$("#display").append("<div class=\"cvsFormat\"><input type=\"radio\" id=\"semicolon\" class=\"chartType\" name=\"delimiter\" value=\"semicolon\" checked/>Semicolon<br/></div>");
            //$("#display").append("<div class=\"cvsFormat\"><input type=\"radio\" id=\"tab\" class=\"chartType\" name=\"delimiter\" value=\"tab\"/>Tab<br/></div>");
            //$("#display").append("<div class=\"cvsFormat\"><input type=\"radio\" id=\"space\" class=\"chartType\" name=\"delimiter\" value=\"space\"/>Space<br/></div>");
            //$("#display").append("<div class=\"cvsFormat\"><input type=\"radio\" id=\"other\" class=\"chartType\" name=\"delimiter\" value=\"other\"/>Other : <input type=\"text\" name=\"otherdelimiter\" size=\"2\"/><br/></div>");
			
            $("#display").append("<div class=\"chartOption\"><input type=\"checkbox\" name=\"fullscreenView\" value=\"fullscreenView\" id=\"fullscreenView\"/>Go Fullscreen</div>");
            GSN.data.appendNextStepButton("display","GSN.data.getDatas()","getDatas","Get Data");
			
            $("#display").append('<br/><br/>');
			
            $(".cvsFormat").hide();
			
            $("#step5Container .data").prev().click(function(){
                $(this).next().toggle("slow");
            });
			
            $("#step5Container").append("<div id=\"warningmsg\">" + "Warning: Not all the values are displayed. To get all the values, you have to download them." + "</div>");
            $("#warningmsg").hide();
        },
   	
   	
        getDatas: function(sql) {
            $("#chartContainer").empty();
            $("#dataSet").remove();
            $("table #dataSet","#datachooser").remove();
  		
  		
            $("#display").append($.SPAN({
                "class":"refreshing"
            },$.IMG({
                "src":"style/ajax-loader.gif",
                "alt":"loading",
                "title":""
            })));
            if ( ! $("#CSV").attr("checked") || sql) {
                if(GSN.selectedSensors.length > 1) request="multirequest=true";
                else request="multirequest=false";
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
                    if ($(this).attr("class") == "field" && $(this).attr("checked")) {
                        if ($("#commonReq").attr("checked")) {
                            request += "&fields=" + $(this).attr("value");
                        } else {
                            request += "&fields=" + $("#"+$(this).val()+"AG").val()+"("+$(this).attr("value")+")";
                        }
                    }
                });
                if ($("#someDatas").attr("checked") && $("#nbOfDatas").attr("value") != "") {
                    var nbValueToExtract = parseInt($("#nbOfDatas").attr("value"));
                    if(nbValueToExtract%6 != 0){
                        nbValueToExtract= nbValueToExtract + 6 - nbValueToExtract%6;
                    }
                    request += "&nb=" + nbValueToExtract;
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
				
                if (sql) {
                    $("#showSQL p").empty();
                    for(var i=0; i<GSN.selectedSensors.length; ++i){
                        request += "&sql=true";
                        $.ajax({
                            type: "GET",
                            url: "/data?"+request+"&vsName="+GSN.selectedSensors[i],
                            success: function(msg) {
                                $("#display .refreshing").remove();
                                $("#showSQL").append($.P({
                                    "class":"query"
                                },unescape(msg)));
                            }
                        });
                    }
                }
				
                if(!sql) GSN.data.displayDatas(request);
            } /*else if ($("#popup").attr("checked")) {
   			$("form").attr("target", "_blank");
   			$("form").attr("action", "/showData.jsp");
   			document.forms["formular"].submit();
   		} */ 
            else if ($("#CSV").attr("checked")) {
                $("form").attr("action", "/multidata");
                $("form").attr("target", "_blank");
   			
                $("#step2Container .data :disabled").removeAttr("disabled");
                $("#step3Container .data :disabled").removeAttr("disabled");
                if ( ! $("#someDatas").attr("checked") ) {
                    $("#nbOfDatas").attr("disabled", "disabled");
                }
                $("#step4Container .data :disabled").removeAttr("disabled");
   			
   			
                document.forms["formular"].submit();
                $("#display .refreshing").remove();
   			
                $("#step2Container .data :enabled").attr("disabled", "disabled");
                $("#step3Container .data :enabled").attr("disabled", "disabled");
                $("#step4Container .data :enabled").attr("disabled", "disabled");
   			
            }
        },
   	
   	
        displayDatas: function(request) {
   		
            //GSN.info("display data request: " + request.toString());
   		
            $('#getDatas').attr("value","Update");
			
            //should check no field selected...
            if ($(".field[checked]").length == 0) {
                $("#display .refreshing").remove();
                alert("You have to select at least one field.");
                return;
            }
   		
            // Define the target
            var target = "#datachooser";
            if ($("#popup").attr("checked")){
                var w = window.open("", "Data", "width=700,height=700,scrollbars=yes");
                if (w == null) {
                    alert('Your browser security setting blocks popup. Please turn it off for this website.');
                    return;
                }
                target = w.document.body;
            }
            if($('#fullscreenView').attr("checked"))
            {
                $('#container > div').hide('slow');
                window.resizeTo(screen.width,screen.height);
                window.moveTo(0,0);
                $('body').prepend('<div id="fullscreenContent" style="width:'+(screen.width)+';"></div>');
                $('#fullscreenContent').append('<br/><input type="button" value="Back to Data Part" onclick="$(\'#container > div\').show(\'slow\'); $(\'#fullscreenContent\').remove();"/>')
                $("#fullscreenContent").append('&nbsp;&nbsp;&nbsp;<input type="button" value="Go to Table Values" onclick="document.location.href=\'#dataSet\'"/>');
                $('#fullscreenContent').append('<br/><br/><div id="seeLink">See chart:<br/><ul></ul></div>');
                $('#fullscreenContent').append('<span id="chartContainer"></span><br/><br/><br/><br/><br/><br/><br/><br/>');
                target=$('#fullscreenContent');
            }
            else{
                $("#datachooser table",target).remove();
                $("#datachooser").append('<span id="chartContainer"></span>');
            }
            $("#chartContainer").hide();
   		
            var answerLinesFromXMLSensorNum = new Array();
            var minValue = new Array();
            var maxValue = new Array();
            for(var i=0; i<GSN.selectedSensors.length; ++i){
                var vsName = GSN.selectedSensors[i];
                var nbFields = $(".field[checked]").length;
                var sensorNumber = i;
                //alert("/data?"+request+"&vsName="+vsName+"&rand="+Math.random());
                $.ajax({
                    async: false,
                    type: "GET",
//TODO                    url: "/data?"+request+"&vsName="+vsName+"&rand="+Math.random(),
                    url: "/multidata?vsname=ss_mem_vs:heap_memory_usage",
                    success: function(answer) {



                        // Remove indicator
                        //alert(answer);
                        $("#display .refreshing").remove();
						
                        // Store the answer
                        answerLinesFromXMLSensorNum[sensorNumber] = $("line", answer);
						
                        // If no answer return
                        if ($("line",answer).size() == 0) {
                            alert('No data corresponds to your request for the sensor: '+vsName);
                            return;
                        }
						

                        $(target).append($.TABLE({
                            "size":"100%",
                            "align":"center",
                            "id":"dataSet"
                        }));
                        $("table#dataSet",target).append("<tr><td class='step' align='center' colspan='"+nbFields+"'>"+vsName.prettyString()+"</td></tr>");
						
                        var line,tr,rows;
                        var lines = $("line", answer);
						
                        if($("#nbOfDatas").attr("value")) nbOfData = parseInt($("#nbOfDatas").attr("value"))+1;
                        else nbOfData = lines.size();
							
                        for (var i = 0; i<nbOfData ; i++){
                            line = lines.get(i);
							
                            if (i==0)
                                tr = $.TR({
                                    "id":"line"+i,
                                    "class":"step"
                                });
                            else
                                tr = $.TR({
                                    "id":"line"+i,
                                    "class":"data"
                                });
							
                            rows = $("field", line);
							
                            for (var j = 0; j<rows.size();j++){
                                if(j <= nbFields && i == 0) $(tr).append($.TD({},$(rows.get(j)).text().prettyString()));
                                else $(tr).append($.TD({},$(rows.get(j)).text()));
                            }
                            $("table#dataSet",target).append(tr);
                        }
						
                        //table coloration
                        if (w != null){
                            $("table#dataSet .step", target).css("background","#ffa84c");
                        }
						
                        $(target).append('<br/><br/>');
                    }
                });
            }
   		
   		
   		
   		
            var nbSelectedFields = $("field",answerLinesFromXMLSensorNum[0].get(0)).length;
            var nbSelectedSensors = GSN.selectedSensors.length;
            GSN.info("nbSelectedFields="+nbSelectedFields);
            GSN.info("nbSelectedSensors="+nbSelectedSensors);
			
			
            // Find the index of Timed
            regularExpression = new RegExp("timed","i");
            var timedIndexInNbSelectedFieldsArray=-1;
            for(var m=0; m < nbSelectedFields; m++){
                if(regularExpression.test($("field",answerLinesFromXMLSensorNum[0].get(0)).eq(m).text())){
                    timedIndexInNbSelectedFieldsArray = m; // Will be useful to detect if parseFloat needed or not for comparison
                }
            }
			
			
            // Compute the min/max for each field for each sensor
            var incModulo = 0;
            GSN.data.modulo = Math.floor(($("field",answerLinesFromXMLSensorNum[0]).length-1)/GSN.data.nbDispVal/nbSelectedFields);//Math.floor($("field",answerLinesFromXMLSensorNum[0]).length/GSN.data.nbDispVal/nbSelectedFields);
            var modulo = GSN.data.modulo;
            GSN.info("Modulo="+modulo);
			
            if($("#allData").attr("checked")) modulo = 1;
			
            // creation of a Matrix dimension: nbSelectedSensors * nbSelectedFields
            var values = new Array(nbSelectedSensors);
            for(var i=0; i<nbSelectedSensors; ++i)
                values[i] = new Array(nbSelectedFields);
			
            for(var i=0; i < nbSelectedSensors; i++){
                //Initialisation of the Matrix
                for(var m=0; m < nbSelectedFields; m++) values[i][m] = 0;
				
                for(var m=0; m < nbSelectedFields; m++){
                    // Initialisation of min/max values
                    if(i==0 && timedIndexInNbSelectedFieldsArray != m){
                        minValue[m] = parseFloat($("field",answerLinesFromXMLSensorNum[i].get(1)).eq(m).text());
                        maxValue[m] = parseFloat($("field",answerLinesFromXMLSensorNum[i].get(1)).eq(m).text());
                    }
                    else if(i==0 && timedIndexInNbSelectedFieldsArray == m){
                        minValue[m] = $("field",answerLinesFromXMLSensorNum[i].get(1)).eq(m).text();
                        maxValue[m] = $("field",answerLinesFromXMLSensorNum[i].get(1)).eq(m).text();
                    }
					
                    incModulo = 0;
					
					
                    for(var k=0; k< (answerLinesFromXMLSensorNum[i].length-1); k++){
                        // 1 because first line only vsName
                        var field = $("field",answerLinesFromXMLSensorNum[i].get(1+k));
						
                        if(timedIndexInNbSelectedFieldsArray != m) actualValue = parseFloat(field.eq(m).text());
                        else actualValue = field.eq(m).text();
							
                        GSN.log("Actual Value:"+actualValue);
						
                        if(minValue[m] >  actualValue) minValue[m] = actualValue;
                        if(maxValue[m] <  actualValue) maxValue[m] = actualValue;
						
                        // Computation of the average
                        if(k == 0 && timedIndexInNbSelectedFieldsArray != m) average = actualValue;
                        else if(timedIndexInNbSelectedFieldsArray != m) average = (average + actualValue)/2;
                        else average = actualValue; // case it is the time
						
                        // Construct values Array which contains the values for the current selected sensor and for the current field
                        nbValueToShow = parseInt($("#nbOfDatas").attr("value"));
                        offset= 6 - nbValueToShow%6;
						
                        if((answerLinesFromXMLSensorNum[i].length-1) > 6 && $("#modData").attr("checked")){
                            // If too many data reduce (take data every modulo)
                            if(values[i][m] == "" && incModulo == 0){
                                values[i][m] = average;
                            }else if(incModulo == 0){
                                values[i][m] = average+","+values[i][m];
                            }
                            incModulo++;
                            incModulo %= modulo;
                        }
                        else{
                            if(k < nbValueToShow){
                                if(values[i][m] == ""){
                                    values[i][m] = actualValue;
                                }else{
                                    values[i][m] = actualValue+","+values[i][m];
                                }
                            }
                        }
                    }
                //GSN.log(minValue[m]);
                //GSN.log(maxValue[m]);
                }
            }
            GSN.info("Final minValue:");
            GSN.log(minValue);
            GSN.info("Final maxValue:");
            GSN.log(maxValue);
			
            GSN.info("Generation Bar Chart:");
            GSN.data.makeChart(nbSelectedFields,timedIndexInNbSelectedFieldsArray,answerLinesFromXMLSensorNum,values,minValue,maxValue,"barChart");
            GSN.info("Generation Line Chart:");
            GSN.data.makeChart(nbSelectedFields,timedIndexInNbSelectedFieldsArray,answerLinesFromXMLSensorNum,values,minValue,maxValue,"lineChart");
			
			
            if($('#barChart').attr("checked") && ($('#dispChartAndData').attr("checked") || $('#popup').attr("checked") || $('#dispChart').attr("checked"))) $('.lineChart').hide();
            if($('#lineChart').attr("checked") && ($('#dispChartAndData').attr("checked") || $('#popup').attr("checked") || $('#dispChart').attr("checked"))) $('.barChart').hide();
            if($('#dispData').attr("checked")){
                $('.lineChart').hide();
                $('.barChart').hide();
            }
            if($('#dispChart').attr("checked")) $('#dataSet').hide();
				
            $('#barChart').attr("onclick","$('.lineChart').hide();$('.barChart').show();");
            $('#lineChart').attr("onclick","$('.lineChart').show();$('.barChart').hide();");
        }// End displayDatas
   	
        ,
        modulo:0
        ,
        nbDispVal:NUMBER_OF_VALUE_TO_DISPLAY_IN_CHARTS_IN_MODULUS_MODE // Number of value to display in modulo mode
        ,
        makeChart: function(nbSelectedFields,timedIndexInNbSelectedFieldsArray,answerLinesFromXMLSensorNum,values,minValue,maxValue,typeChart){
            // Chart Part
            var nbValue = Math.floor($("field",answerLinesFromXMLSensorNum[0]).length/nbSelectedFields)-1;
            GSN.log("Nb Values extracted="+nbValue);
            GSN.log("Nb Values requested="+$("#nbOfDatas").val());
            GSN.log("Offset Values="+(nbValue-$("#nbOfDatas").val()));
			
            if ($("#allDatas").attr("checked") && $("#nbOfDatas").attr("value") < nbValue) {
                $("#warningmsg").show();
            }
            else {
                $("#warningmsg").hide();
            }
					
            for(var m=0; m < nbSelectedFields; m++){
                regularExpression = new RegExp("timed","i");
                if(timedIndexInNbSelectedFieldsArray != m){
					
					
                    if($('#fullscreenView').attr("checked")){
                        $("#chartContainer").append('<div class="'+typeChart+'" id="'+typeChart+m+'"></div>');
                        var so = new SWFObject("./open-flash-chart/open-flash-chart.swf", typeChart+m, screen.width-100, screen.height-250, "9", "#FFFFFF");
                        if($("#"+typeChart).attr("checked")){
                            $("#chartContainer").append('&nbsp;&nbsp;&nbsp;<input type="button" value="Back to Data Part" onclick="$(\'#container > div\').show(\'slow\'); $(\'#fullscreenContent\').remove();"/>');
                            $("#chartContainer").append('&nbsp;&nbsp;&nbsp;<input type="button" value="Go to Top" onclick="document.location.href=\'#fullscreenContent\'"/>');
                            $("#chartContainer").append('&nbsp;&nbsp;&nbsp;<input type="button" value="Go to Table Values" onclick="document.location.href=\'#dataSet\'"/>');
                            $("#chartContainer").append('<br/><br/>');
                            $("#seeLink ul").append("<li><a href='#"+typeChart+m+"'>"+$("field",answerLinesFromXMLSensorNum[0].get(0)).eq(m).text().prettyString()+"</a></li>");
                        }
                    }
                    else{
                        $("#chartContainer").append('<div style="border-width:10px;" class="'+typeChart+'" id="'+typeChart+m+'"></div><br/>');
                        var so = new SWFObject("./open-flash-chart/open-flash-chart.swf", typeChart+m, "450", "400", "9", "#FFFFFF");
                    }
                    so.addVariable("variables","true");
                    so.addVariable("title","Data viewer: "+$("field",answerLinesFromXMLSensorNum[0].get(0)).eq(m).text().prettyString()+",{font-size:20px; color: #000000; margin: 5px; padding:5px; padding-left: 20px; padding-right: 20px;}");
					
		   		
                    if(!$("#modData").attr("checked")){
                        x_step = Math.max(GSN.data.modulo,1);
                    }	else {
                        x_step = 2;
                    }
                    GSN.log("x_step="+x_step);
		   		
		   		
                    so.addVariable("x_axis_steps",x_step);
                    so.addVariable("y_label_style","10,#000000");
					
                    so.addVariable("x_label_style","10,#000000,2,"+x_step+",#eeeeee");
					
					
                    // Timed on x axis
                    var x_label;
                    var incModulo = 0;
                    if(timedIndexInNbSelectedFieldsArray != -1){
                        x_label = values[0][timedIndexInNbSelectedFieldsArray];
                    }
                    else{
                        x_label = 1;
                        for(var p=1; p < nbValue; p++){
                            if(incModulo == GSN.data.modulo){
                                x_label += ","+(p+1);
                                incModulo = 0;
                            }
                            incModulo++;
                        }
                    }
					
					
                    so.addVariable("x_labels",x_label);
                    so.addVariable("y_legend",""+$("field",answerLinesFromXMLSensorNum[0].get(0)).eq(m).text().prettyString()+",12,#000000");
                    so.addVariable("y_ticks","5,10,5");
					


                    so.addVariable("x_axis_colour","#000000");
                    so.addVariable("x_grid_colour","#eeeeee");
                    so.addVariable("y_axis_colour","#000000");
                    so.addVariable("y_grid_colour","#eeeeee");
                    so.addVariable("tool_tip","#key#\n#x_label#\n#val#");
					
                    //so.addVariable("inner_background","#FCFDDC,#FF6600,90");
                    so.addVariable("bg_colour","#ffffff");

					
                    GSN.log($("field",answerLinesFromXMLSensorNum[0].get(0)).eq(m).text().prettyString()+":  min: "+minValue[m]+"   MAX: "+maxValue[m]);
                    //deltaDiv = Math.abs(Math.floor((parseFloat(maxValue[m]) - parseFloat(minValue[m]))));
                    //					if(minValue[m] < 0) minVal = Math.floor(1.05*parseFloat(minValue[m]));
                    //					else minVal = Math.floor(0.95*parseFloat(minValue[m]));
                    //
                    //					if(parseInt(maxValue[m]) < 0) maxVal = 0;
                    //					else maxVal = Math.floor(1.05*parseFloat(maxValue[m]));
					
					
                    so.addVariable("y_min",minValue[m]);
                    so.addVariable("y_max",maxValue[m]);
					
                    if(typeChart == "barChart"){
                        GSN.data.makeBarChart(so,values,m);
                    }
                    else if(typeChart == "lineChart"){
                        GSN.data.makeLineChart(so,values,m);
                    }
                    so.write(typeChart+m,"");
					
                }
				
				
                GSN.log("X Label: "+x_label);
            }
            // End Chart Part
   		
            $('#chartContainer').show();
   		
        }
	
	
        ,
        makeLineChart: function(so,values,field){
            for(i=0; i<GSN.selectedSensors.length;++i){
                if(i==0){
                    so.addVariable("line_dot","2,"+$('#hexcode-'+GSN.selectedSensors[i]).val()+","+GSN.selectedSensors[i]+",10,4");
                    so.addVariable("values",values[i][field]);
                }
                else{
                    so.addVariable("line_dot_"+(i+1),"2,"+$('#hexcode-'+GSN.selectedSensors[i]).val()+","+GSN.selectedSensors[i]+",10,4");
                    so.addVariable("values_"+(i+1),values[i][field]);
                }
            }
        }
		
		
        ,
        makeBarChart: function(so,values,field){
            so.addVariable("x_axis_3d","3");
            for(i=0; i<GSN.selectedSensors.length;++i){
                if(i == 0){
                    so.addVariable("bar_3d","75,"+$('#hexcode-'+GSN.selectedSensors[i]).val()+","+GSN.selectedSensors[0]+",10");
                    so.addVariable("values",values[i][field]);
                }
                else{
                    so.addVariable("bar_3d_"+(i+1),"75,"+$('#hexcode-'+GSN.selectedSensors[i]).val()+","+GSN.selectedSensors[i]+",10");
                    so.addVariable("values_"+(i+1),values[i][field]);
                }
            }
        }
   	
    }//End GSN.data
	

	
    ,
    util: {
        /**
		* Pretty print of timestamp date
		*/
        printDate: function(date){
            date = new Date(parseInt(date));
            var value = GSN.util.addleadingzero(date.getDate())+"/"+GSN.util.addleadingzero(date.getMonth()+1)+"/"+date.getFullYear();
            value += " "+GSN.util.addleadingzero(date.getHours())+":"+GSN.util.addleadingzero(date.getMinutes())+":"+GSN.util.addleadingzero(date.getSeconds());
            return value;
        }
	    
        ,
        resumelongsentences: function(sentence) {
            if (sentence.toString().length > 50) {
                sentence = sentence.toString().substring(0,46);
                sentence += " ...";
            }
            return sentence;
        }
	    
	    
        /**
		* Add a zero if less then 10
		*/
        ,
        addleadingzero : function (num){
            var n = String(num);
            return (n.length == 1 ? "0"+n : n);
        }
		
		
        /**
		* toggle the display of sensors in the sidebar
		*/
        ,
        toggle: function(obj){
            $("a",obj).show();
            obj.toggle();
        }
		
        /**
		* Take an array return a sorted matrix with sensor name in first col and rubric name in the second col
		*/
		

        ,
        regroupByRubricSensorName: function(vsName){
            vsName.sort();
			
            //Creation of a Matrix n*2 and initialization
            vsNameRubric = new Array(vsName.length);
            for(var i=0; i<vsName.length; ++i){
                vsNameRubric[i] = new Array(2);
                vsNameRubric[i][0] = vsName[i];
                vsNameRubric[i][1] = "others";
            }
			
            //fill the matrix
            var numberAtTheEnd = new RegExp("[0-9]$");
            var createRubric = false;
            var firstTimeTry = true;
            var rubricName;
            for(var i=1; i<vsName.length; ++i){
                for(var similarDegree=3; similarDegree<vsName[i].length; ++similarDegree){
                    regularExpression = new RegExp("^"+vsName[i-1].substr(0,similarDegree),"i");
					
                    if(!regularExpression.test(vsName[i]) && firstTimeTry){
							
                        break;
                    }
                    if(regularExpression.test(vsName[i]) && firstTimeTry){
                        firstTimeTry=false;
                    }
						
                    if(numberAtTheEnd.test(vsName[i].substr(0,similarDegree))){
                        break;
                    }
						
                    if(regularExpression.test(vsName[i]) && !firstTimeTry){
                        vsNameRubric[i][1] = vsName[i-1].substr(0,similarDegree);
                        vsNameRubric[i-1][1] = vsName[i-1].substr(0,similarDegree);
                    }
						
						
                    if(!regularExpression.test(vsName[i]) && !firstTimeTry){
                        break;
                    }
					
					
                }
            }
			
            return vsNameRubric;
        }

        /**
                * Take an array return a sorted array with grouping to the first underscore
                */
        ,
        regroupByUnderscore: function(vsName){
            var name, index;

            vsName.sort();
            vsNameUnderscore = new Array(vsName.length);
            for(var i=0;i<vsName.length;++i)
            {
                name = vsName[i];
                vsNameUnderscore[i] = new Array(2);
                index = name.indexOf("_");
                vsNameUnderscore[i][0] = name;
                if(index>=1) vsNameUnderscore[i][1] = name.substr(0,index);
                else vsNameUnderscore[i][1] = "others";
            }

            return vsNameUnderscore;
        }
		
        ,
        getURLParam: function(strParamName){
            var strReturn = "";
            var strHref = window.location.href;
            if ( strHref.indexOf("?") > -1 ){
                var strQueryString = strHref.substr(strHref.indexOf("?")).toLowerCase();
                var aQueryString = strQueryString.split("&");
                for ( var iParam = 0; iParam < aQueryString.length; iParam++ ){
                    if (
                        aQueryString[iParam].indexOf(strParamName.toLowerCase() + "=") > -1 ){
                        var aParam = aQueryString[iParam].split("=");
                        strReturn = aParam[1];
                        break;
                    }
                }
            }
            return unescape(strReturn);
        }
		
		
    }
};

