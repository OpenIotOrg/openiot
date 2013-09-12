package org.openiot.lsm.manager;

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
*/

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.openiot.lsm.beans.User;
import org.openiot.lsm.pooling.ConnectionPool;
import org.openiot.lsm.utils.VirtuosoConstantUtil;

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

//	public void addNewUser(String username,String email, String pass) {
//		// TODO Auto-generated method stub
//		long id = System.nanoTime();
//		String sql = "insert into deri.dba.users values('"+id+"','"+email+"','"+username+"','"+pass+"')";
//		try{
//			conn= ConnectionPool.getConnectionPool().getConnection();			
//			PreparedStatement ps = conn.prepareStatement(sql);
//			boolean i = ps.execute(sql);
//			System.out.println("Insert new user");
//			ConnectionPool.attemptClose(ps);			
//			ConnectionPool.attemptClose(conn);
//		}catch(Exception e){
//			e.printStackTrace();
//			ConnectionPool.attemptClose(conn);
//		}
//	}
}
