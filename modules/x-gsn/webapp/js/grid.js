	$.addGridControl = function(t,p) {
		if (t.grid) return false;
		var grid = {
				table: t,
				scrollTop: 0,
				height: p.height,
				page: 0,	
				headers: [],
				cols: [],
				dragStart: function(i,x) {
					this.resizing = { idx: i, startX: x};
					this.hDiv.style.cursor = "e-resize"
		
				},
				dragMove: function(x) {
					if (this.resizing) {
						var diff = x-this.resizing.startX
						var h = this.headers[this.resizing.idx]
						var newWidth = h.width + diff
						if (newWidth > 50) { 
							h.el.style.width = newWidth+"px";
							h.newWidth = newWidth; 
							this.cols[this.resizing.idx].style.width = newWidth+"px";
							this.newWidth = this.width+diff;
							this.table.style.width = this.newWidth + "px"
							this.hTable.style.width = this.newWidth + "px"
							this.hDiv.scrollLeft = this.bDiv.scrollLeft;
						}
					}
				},
				dragEnd: function() {
					this.hDiv.style.cursor = "default"
					if (this.resizing) {
						var idx = this.resizing.idx
						this.headers[idx].width = this.headers[idx].newWidth
						this.width = this.newWidth;
						this.resizing = false;
					}
				},
				scroll: function() {
					var scrollTop = this.bDiv.scrollTop
					if (scrollTop != this.scrollTop) {
						this.scrollTop = scrollTop
						if ((this.bDiv.scrollHeight-scrollTop-this.height) <= 0) {
							this.populate();
						}
					} else {
						this.hDiv.scrollLeft = this.bDiv.scrollLeft;
					}
	

				},
				addXmlData: function(xml) {
					var tbody = $("tbody",this.table);
					$("rows row",xml).each(
						function() {
							var row = document.createElement("tr");
							row.id = this.getAttribute('id');
							$("cell",this).each(
								function () {
									var td = document.createElement("td");
									td.appendChild(document.createTextNode(this.firstChild.nodeValue));
									row.appendChild(td);
									
								}
							)
							tbody.append(row);
						}
					);
					this.loading = false;
					$("div.loading",this.hDiv).fadeOut("fast");
				},
				addJSONData: function(JSON) {
					eval("var data = " + JSON);
					var tbody = $("tbody",this.table);
					var row = ""
					var cur = ""
					for (var i=0;i<data.rows.length;i++) {
						cur = data.rows[i]
						row = '<tr id="'+cur.id+'">'
						for (var j=0;j<cur.cell.length;j++) row += "<td>"+cur.cell[j]+"</td>"
						row += '</tr>';
						tbody.append(row);
						
					}
					tbody = null;
					this.loading = false;
					$("div.loading",this.hDiv).fadeOut("fast");
				},
				populate: function() {
					if (!this.loading) {
						this.loading = true;
						this.page++
						$("div.loading",this.hDiv).fadeIn("fast");
						$.get("api.jsp?vs=GPSVS",function(xml) { grid.addXmlData(xml) });
						//$.get("api.jsp/"+this.page+"/JSON",function(xml) { grid.addJSONData(xml) });
					}
				}
			}
			
		var thead = $("thead:first",t).get(0);
		var count = 0;
		$("tr:first th",thead).each(
			function () {
				var w = p.width[count]
				var res = document.createElement("span");
				$(res).html("&nbsp;");
				var idx=count
				$(res).mousedown(
					function (e) {
						grid.dragStart(idx,e.clientX);
						return false;
					}
				)
				$(this).css("width",w+"px").prepend(res);
				
				grid.headers[count++] = { width: w, el: this };
			}
		)
		count = 0;
		$("tbody tr:first td",t).each(
			function() {
				var w = p.width[count]
				$(this).css("width",w+"px");
				grid.cols[count++] = this ;
			}
		);
		//grid.width = $.getCSS ? $.getCSS(t,"width") : $.getCSS(t,"width");
		grid.bWidth = grid.width;
		grid.hTable = document.createElement("table");
		grid.hTable.cellSpacing="0"; 
		grid.hTable.className = "scroll";
		grid.hTable.appendChild(thead);
		thead = null;
		grid.hDiv = document.createElement("div")
		$(grid.hDiv)
			.css({ width: grid.width+"px", overflow: "hidden"})
			.append(grid.hTable)
			.prepend('<div class="loading">loading</div>')			
			.bind("selectstart", function () { return false; });
		
		$(t).mouseover(
				function(e) {
					var td = (e.target || e.srcElement);
					td.className = "hover" 
					td.parentNode.className = "hover"
				}
			)
			.mouseout(
				function(e) {
					var td = (e.target || e.srcElement);
					td.className = "" 
					td.parentNode.className = ""
				}
			)
			.before(grid.hDiv)
			
		grid.bDiv = document.createElement("div")
		$(grid.bDiv)
			.scroll(function (e) {grid.scroll()})
			.css({ height: p.height+"px", padding: "0px", margin: "0px", overflow: "auto", width: (grid.width+16)+"px"})
			.append(t)
		$(grid.hDiv).mousemove(function (e) {grid.dragMove(e.clientX);}).after(grid.bDiv)
		//while (grid.bDiv.scrollHeight<=grid.height) grid.populate()
		grid.populate()
	
		$(document).mouseup(function (e) {grid.dragEnd();})
		t.grid = grid;
		// MSIE memory leak
		$(window).unload(function () {
				t.grid = null;			
			}
		);
		
		
	}
	
	$.fn.grid = function(p) {
		return this.each(
			function() {
				$.addGridControl(this,p);
			}
		);
	}

	
	
	
	
	
	
	
	
	
	