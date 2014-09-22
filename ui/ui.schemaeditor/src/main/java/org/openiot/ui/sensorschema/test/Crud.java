/**
 *    Copyright (c) 2011-2014, OpenIoT
 *   
 *    This file is part of OpenIoT.
 *
 *    OpenIoT is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, version 3 of the License.
 *
 *    OpenIoT is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Contact: OpenIoT mailto: info@openiot.eu
 * 
 * 	   @author Prem Jayaraman
 */
package org.openiot.ui.sensorschema.test;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

@ManagedBean
@SessionScoped
public class Crud implements Serializable{
	  	private List<Item> list;
	    private transient DataModel<Item> model;
	    private Item item = new Item();
	    private boolean edit;

	    @PostConstruct
	    public void init() {
	        // list = dao.list();
	        // Actually, you should retrieve the list from DAO. This is just for demo.
	        list = new ArrayList<Item>();
//	        list.add(new Item(1L, "item1"));
//	        list.add(new Item(2L, "item2"));
//	        list.add(new Item(3L, "item3"));
	    }
	    
	    public void add() {

	        // Actually, the DAO should already have set the ID from DB. This is just for demo.
	    	item.setId(list.isEmpty() ? 1 : list.get(list.size() - 1).getId() + 1);
	        list.add(item);
	        item = new Item(); // Reset placeholder.
	    }

	    public void edit() {
	        item = model.getRowData();
	        edit = true;
	    }

	    public void save() {
	    	
	        item = new Item(); // Reset placeholder.
	        edit = false;
	    }

	    public void delete() {
	        // dao.delete(item);
	        list.remove(model.getRowData());
	    }

	    public List<Item> getList() {
	        return list;
	    }

	    public DataModel<Item> getModel() {
	        if (model == null) {
	            model = new ListDataModel<Item>(list);
	        }

	        return model;
	    }

	    public Item getItem() {
	        return item;
	    }

	    public boolean isEdit() {
	        return edit;
	    }

	    // Other getters/setters are actually unnecessary. Feel free to add them though.
	
}
