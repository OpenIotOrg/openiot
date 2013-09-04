package org.openiot.lsm.beans;

/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This library is free software; you can redistribute it and/or
 * modify it either under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation
 * (the "LGPL"). If you do not alter this
 * notice, a recipient may use your version of this file under the LGPL.
 *
 * You should have received a copy of the LGPL along with this library
 * in the file COPYING-LGPL-2.1; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 * OF ANY KIND, either express or implied. See the LGPL  for
 * the specific language governing rights and limitations.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.openiot.lsm.pooling.ConnectionPool;
import org.openiot.lsm.utils.VirtuosoConstantUtil;

import virtuoso.jena.driver.VirtGraph;

public class UserActiveManager {
	private Connection conn;
	private String dataGraph = VirtuosoConstantUtil.sensormasherDataGraphURI;
	private String metaGraph = VirtuosoConstantUtil.sensormasherMetadataGraphURI;
	
	public UserActiveManager(){
		
	}
	
	public UserActiveManager(String metaGraph,String dataGraph){
		this.metaGraph = metaGraph;
		this.dataGraph = dataGraph;
	}
	
	public User getUserWithUserId(String userId) {
		// TODO Auto-generated method stub
		User user = null;
		userId = "<"+userId+">";
		String sql = "sparql select ?nickname ?pass ?username "+
				"where{ "+
				  userId +" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#User>."+
				  userId +" <http://lsm.deri.ie/ont/lsm.owl#hasUserName> ?username."+
				  userId +" <http://lsm.deri.ie/ont/lsm.owl#hasNickName> ?nickname."+
				  userId +" <http://lsm.deri.ie/ont/lsm.owl#hasPassword> ?pass."+
				"}";			 
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){					
					user = new User();
					user.setId(rs.getString(userId));
					user.setNickname(rs.getString("nickname"));
					user.setUsername(rs.getString("username"));
					user.setPass(rs.getString("pass"));					
				}
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return user;
	}

//	public User getUser(String username) {
//		// TODO Auto-generated method stub
//		User user = null;
//		String sql = "select * from " + ConstantsUtil.databaseName
//				+ "users u where u.username = '" + username +"'";
//		try{
//			VirtGraph graph = ConnectionPool.getConnectionPool().getConnection();
//			conn = graph.getConnection();
//			PreparedStatement ps = conn.prepareStatement(sql);
//			ResultSet rs = ps.executeQuery();
//			while(rs.next()){
//				user = new User();
//				user.setId(rs.getString("id"));
//				user.setNickname(rs.getString("nickname"));
//				user.setUsername(rs.getString("username"));
//				user.setPass(rs.getString("pass"));
//			}
//			ConnectionPool.getConnectionPool().free(graph);	
//		}catch(SQLException e){
//			e.printStackTrace();
//		}
//		return user;
//	}
	
	public User getUser(String username) {
		User user = null;		
		String sql = "sparql select ?userId ?nickname ?pass "+
				"from <"+ metaGraph+"> "+
				"where{ "+
				  "?userId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#User>."+
				  "?userId <http://lsm.deri.ie/ont/lsm.owl#hasUserName> \""+username+"\"."+
				  "?userId <http://lsm.deri.ie/ont/lsm.owl#hasNickName> ?nickname."+
				  "?userId <http://lsm.deri.ie/ont/lsm.owl#hasPassword> ?pass."+
				"}";			 
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){					
					user = new User();
					user.setId(rs.getString("userId"));
					user.setNickname(rs.getString("nickname"));
					user.setUsername(username);
					user.setPass(rs.getString("pass"));					
				}
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return user;
	}
	
	public User userAuthentication(String username,String pass){
		User user = null;
		String sql = "select id,username,pass from deri.dba.users where username='"+username+"' and pass='"+pass+"'";				
		try{
			conn = ConnectionPool.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){					
					user = new User();
					user.setId("http://lsm.deri.ie/resource/"+rs.getString("id"));
					user.setPass(pass);					
				}
				ConnectionPool.attemptClose(rs);				
			}
			ConnectionPool.attemptClose(st);
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
		return user;		
	}

	public void addNewUser(String username,String email, String pass) {
		// TODO Auto-generated method stub
		String triples = "";
		VirtGraph graph = null;
		long id = System.nanoTime();
//		triples+="<http://lsm.deri.ie/resource/"+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#User>.\n"+ 
//				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#hasUserName> \""+username+"\"."+
//				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#hasPassword> \""+pass+"\".";		
		String sql = "insert into deri.dba.users values('"+id+"','"+email+"','"+username+"','"+pass+"')";
		try{
			conn= ConnectionPool.getConnectionPool().getConnection();			
//			String sql = "sparql insert into graph <" + metaGraph+ ">{" + triples +"}";
			PreparedStatement ps = conn.prepareStatement(sql);
			boolean i = ps.execute(sql);
			System.out.println("Insert new user");
			ConnectionPool.attemptClose(ps);			
			ConnectionPool.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			ConnectionPool.attemptClose(conn);
		}
	}
}
