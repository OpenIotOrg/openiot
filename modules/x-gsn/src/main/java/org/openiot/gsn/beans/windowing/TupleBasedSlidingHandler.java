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

package org.openiot.gsn.beans.windowing;

import org.openiot.gsn.Main;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.StreamSource;
import org.openiot.gsn.storage.SQLUtils;
import org.openiot.gsn.utils.CaseInsensitiveComparator;
import org.openiot.gsn.utils.GSNRuntimeException;
import org.openiot.gsn.wrappers.AbstractWrapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class TupleBasedSlidingHandler implements SlidingHandler {

	private static final transient Logger logger = Logger.getLogger(TupleBasedSlidingHandler.class);
	private List<StreamSource> streamSources; //only holds WindowType.TUPLE_BASED_SLIDE_ON_EACH_TUPLE types of stream sources
	private Map<StreamSource, Long> slidingHashMap;
	private AbstractWrapper wrapper;

	public TupleBasedSlidingHandler(AbstractWrapper wrapper) {
		streamSources = Collections.synchronizedList(new ArrayList<StreamSource>());
		slidingHashMap = Collections.synchronizedMap(new HashMap<StreamSource, Long>());
		this.wrapper = wrapper;
	}

	public void addStreamSource(StreamSource streamSource) {
		if (streamSource.getWindowingType() != WindowType.TUPLE_BASED_SLIDE_ON_EACH_TUPLE) {
			if (streamSource.getWindowingType() == WindowType.TUPLE_BASED) {
				slidingHashMap.put(streamSource, streamSource.getParsedSlideValue() - streamSource.getParsedStorageSize());
			} else {
				slidingHashMap.put(streamSource, 0L);
			}
		} else {
			streamSources.add(streamSource);
		}
		SQLViewQueryRewriter rewriter = new TupleBasedSQLViewQueryRewriter();
		rewriter.setStreamSource(streamSource);
		rewriter.initialize();
	}

	public boolean dataAvailable(StreamElement streamElement) {
		boolean toReturn = false;
		synchronized (streamSources) {
			for (StreamSource streamSource : streamSources) {
				toReturn = streamSource.getQueryRewriter().dataAvailable(streamElement.getTimeStamp()) || toReturn;
			}
		}
		synchronized (slidingHashMap) {
			for (StreamSource streamSource : slidingHashMap.keySet()) {
				long slideVar = slidingHashMap.get(streamSource) + 1;
				if (slideVar == streamSource.getParsedSlideValue()) {
					toReturn = streamSource.getQueryRewriter().dataAvailable(streamElement.getTimeStamp()) || toReturn;
					slideVar = 0;
				}
				slidingHashMap.put(streamSource, slideVar);
			}
		}
		return toReturn;
	}

	public long getOldestTimestamp() {
		long timed1 = -1;
		long timed2 = -1;
		long maxTupleCount = 0;
		long maxTupleForTimeBased = 0;
		long maxWindowSize = 0;

		//WindowType.TUPLE_BASED_SLIDE_ON_EACH_TUPLE sliding windows are saved in streamSources list 
		synchronized (streamSources) {
			for (StreamSource streamSource : streamSources) {
				maxTupleCount = Math.max(maxTupleCount, streamSource.getParsedStorageSize());
			}
		}

		synchronized (slidingHashMap) {
			for (StreamSource streamSource : slidingHashMap.keySet()) {
				if (streamSource.getWindowingType() == WindowType.TUPLE_BASED) {
					maxTupleCount = Math.max(maxTupleCount, streamSource.getParsedStorageSize() + streamSource.getParsedSlideValue());
				} else {
					maxTupleForTimeBased = Math.max(maxTupleForTimeBased, streamSource.getParsedSlideValue());
					maxWindowSize = Math.max(maxWindowSize, streamSource.getParsedStorageSize());
				}
			}
		}

		if (maxTupleCount > 0) {
			StringBuilder query = new StringBuilder();
			if (Main.getWindowStorage().isH2() || Main.getWindowStorage().isMysqlDB() || Main.getWindowStorage().isPostgres()) {
				query.append(" select timed from ").append(wrapper.getDBAliasInStr());
				query.append(" order by timed desc limit 1 offset ").append(maxTupleCount - 1);
			} else if (Main.getWindowStorage().isSqlServer()) {
				query.append(" select min(timed) from (select top ").append(maxTupleCount).append(" * ").append(" from ").append(
						wrapper.getDBAliasInStr()).append(" order by timed desc )as X  ");
			}else if (Main.getWindowStorage().isOracle()) {
				query.append(" select timed from (select timed from ").append(Main.getWindowStorage().tableNameGeneratorInString(wrapper.getDBAliasInStr()));
				query.append(" order by timed desc) where rownum = ").append(maxTupleCount);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Query1 for getting oldest timestamp : " + query);
			}
			Connection conn = null;
			try {
				ResultSet resultSet = Main.getWindowStorage().executeQueryWithResultSet(query,conn=Main.getWindowStorage().getConnection());
				if (resultSet.next()) {
					timed1 = resultSet.getLong(1);
				} else {
					return -1;
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			} finally {
				Main.getWindowStorage().close(conn);
			}
		}

		if (maxTupleCount > 0) {
			StringBuilder query = new StringBuilder();
			if (Main.getWindowStorage().isH2() || Main.getWindowStorage().isMysqlDB() || Main.getWindowStorage().isPostgres()) {
				query.append(" select timed from ").append(wrapper.getDBAliasInStr());
				query.append(" order by timed desc limit 1 offset ").append(maxTupleCount - 1);
			} else if (Main.getWindowStorage().isSqlServer()) {
				query.append(" select min(timed) from (select top ").append(maxTupleCount).append(" * ").append(" from ").append(
						wrapper.getDBAliasInStr()).append(" order by timed desc )as X  ");
			}else if (Main.getWindowStorage().isOracle()) {
				query.append(" select timed from ( select timed from ").append(Main.getWindowStorage().tableNameGeneratorInString(wrapper.getDBAliasInStr()));
				query.append(" order by timed desc) where rownum = ").append(maxTupleCount);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Query1 for getting oldest timestamp : " + query);
			}
			Connection conn = null;
			try {
				ResultSet resultSet = Main.getWindowStorage().executeQueryWithResultSet(query,conn=Main.getWindowStorage().getConnection());
				if (resultSet.next()) {
					timed1 = resultSet.getLong(1);
				} else {
					return -1;
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			} finally {
				Main.getWindowStorage().close(conn);
			}
		}

		if (maxWindowSize > 0) {
			StringBuilder query = new StringBuilder();
			if (Main.getWindowStorage().isMysqlDB() || Main.getWindowStorage().isPostgres()) {
				query.append(" select timed - ").append(maxWindowSize).append(" from (select timed from ").append(wrapper.getDBAliasInStr());
				query.append(" order by timed desc limit 1 offset ").append(maxTupleForTimeBased - 1).append(" ) as X ");
			} else if (Main.getWindowStorage().isH2()) {
				query.append(" select timed - ").append(maxWindowSize).append(" from ").append(wrapper.getDBAliasInStr()).append(
				" where timed in (select timed from ").append(wrapper.getDBAliasInStr());
				query.append(" order by timed desc limit 1 offset ").append(maxTupleForTimeBased - 1).append(" ) ");
			} else if (Main.getWindowStorage().isSqlServer()) {
				query.append(" select min(timed) - ").append(maxWindowSize).append(" from (select top ").append(maxTupleForTimeBased).append(" * ").append(" from ").append(wrapper.getDBAliasInStr()).append(" order by timed desc ) as X  ");
			}else if (Main.getWindowStorage().isOracle()){
				query.append(" select timed - ").append(maxWindowSize).append(" from (select timed from (select timed from ").append(Main.getWindowStorage().tableNameGeneratorInString(wrapper.getDBAliasInStr()));
				query.append(" order by timed desc) where rownum = ").append(maxTupleForTimeBased).append(") ) ");
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Query2 for getting oldest timestamp : " + query);
			}
			Connection conn = null;
			try {
				ResultSet resultSet = Main.getWindowStorage().executeQueryWithResultSet(query,conn=Main.getWindowStorage().getConnection());
				if (resultSet.next()) {
					timed2 = resultSet.getLong(1);
				} else {
					return -1;
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			} finally {
				Main.getWindowStorage().close(conn);
			}
		}

		if (timed1 >= 0 && timed2 >= 0) {
			return Math.min(timed1, timed2);
		}
		return (timed1 == -1) ? timed2 : timed1;
	}

	public void removeStreamSource(StreamSource streamSource) {
		streamSources.remove(streamSource);
		slidingHashMap.remove(streamSource);
		streamSource.getQueryRewriter().dispose();
	}

	public void dispose() {
		synchronized (streamSources) {
			for (StreamSource streamSource : streamSources) {
				streamSource.getQueryRewriter().dispose();
			}
			streamSources.clear();
		}
		synchronized (slidingHashMap) {
			for (StreamSource streamSource : slidingHashMap.keySet()) {
				streamSource.getQueryRewriter().dispose();
			}
			slidingHashMap.clear();
		}
	}

	public boolean isInterestedIn(StreamSource streamSource) {
		return WindowType.isTupleBased(streamSource.getWindowingType());
	}

	private class TupleBasedSQLViewQueryRewriter extends SQLViewQueryRewriter {

		@Override
		public CharSequence createViewSQL() {
			if (cachedSqlQuery != null) {
				return cachedSqlQuery;
			}
			if (streamSource.getWrapper() == null) {
				throw new GSNRuntimeException("Wrapper object is null, most probably a bug, please report it !");
			}
			if (streamSource.validate() == false) {
				throw new GSNRuntimeException("Validation of this object the stream source failed, please check the logs.");
			}
			CharSequence wrapperAlias = streamSource.getWrapper().getDBAliasInStr();
			long windowSize = streamSource.getParsedStorageSize();
			if (streamSource.getSamplingRate() == 0 || windowSize == 0) {
				return cachedSqlQuery = new StringBuilder("select * from ").append(wrapperAlias).append(" where 1=0");
			}
			TreeMap<CharSequence, CharSequence> rewritingMapping = new TreeMap<CharSequence, CharSequence>(new CaseInsensitiveComparator());
			rewritingMapping.put("wrapper", wrapperAlias);
			
			String sqlQuery = streamSource.getSqlQuery();
            StringBuilder toReturn = new StringBuilder();
            
            int fromIndex = sqlQuery.indexOf(" from ");
            if(Main.getWindowStorage().isH2() && fromIndex > -1){
            	toReturn.append(sqlQuery.substring(0, fromIndex + 6)).append(" (select * from ").append(sqlQuery.substring(fromIndex + 6));
            }else{
            	toReturn.append(sqlQuery);
            }
			
			if (streamSource.getSqlQuery().toLowerCase().indexOf(" where ") < 0) {
				toReturn.append(" where ");
			} else {
				toReturn.append(" and ");
			}

			if (streamSource.getSamplingRate() != 1) {
				if (Main.getWindowStorage().isH2()) {
					toReturn.append("( timed - (timed / 100) * 100 < ").append(streamSource.getSamplingRate() * 100).append(") and ");
				} else {
					toReturn.append("( mod( timed , 100)< ").append(streamSource.getSamplingRate() * 100).append(") and ");
				}
			}
			WindowType windowingType = streamSource.getWindowingType();
			if (windowingType == WindowType.TUPLE_BASED_SLIDE_ON_EACH_TUPLE) {
				if (Main.getWindowStorage().isMysqlDB() || Main.getWindowStorage().isPostgres()) {
					toReturn.append("timed >= (select timed from ").append(wrapperAlias).append(" order by timed desc limit 1 offset ").append(windowSize - 1).append(" ) order by timed desc ");
				} else if (Main.getWindowStorage().isH2()) {
					toReturn.append("timed >= (select distinct(timed) from ").append(wrapperAlias).append(" where timed in (select timed from ").append(wrapperAlias).append(" order by timed desc limit 1 offset ").append(windowSize - 1).append(
					" )) order by timed desc ");
				} else if (Main.getWindowStorage().isSqlServer()) {
					toReturn.append("timed >= (select min(timed) from (select TOP ").append(windowSize).append(" timed from ").append(
							wrapperAlias).append(" order by timed desc ) as y )");
				}else if (Main.getWindowStorage().isOracle()) {
					toReturn.append("(timed >= (select timed from (select timed from "+Main.getWindowStorage().tableNameGeneratorInString(wrapperAlias)+" order by timed desc ) where rownum="+windowSize+") )");
				}else {
					logger.fatal("Not supported DB!");
				}
			} else {
				CharSequence viewHelperTableName =Main.getWindowStorage().tableNameGeneratorInString(SQLViewQueryRewriter.VIEW_HELPER_TABLE);
				if (windowingType == WindowType.TUPLE_BASED) {
					if (Main.getWindowStorage().isMysqlDB() || Main.getWindowStorage().isPostgres()) {
						toReturn.append("timed <= (select timed from ").append(viewHelperTableName).append(
						" where U_ID='").append(streamSource.getUIDStr()).append("') and timed >= (select timed from ");
						toReturn.append(wrapperAlias).append(" where timed <= (select timed from ");
						toReturn.append(viewHelperTableName).append(" where U_ID='").append(streamSource.getUIDStr());
						toReturn.append("') ").append(" order by timed desc limit 1 offset ").append(windowSize - 1).append(" )");
						toReturn.append(" order by timed desc ");
					} else if (Main.getWindowStorage().isH2()) {
						toReturn.append("timed <= (select timed from ").append(viewHelperTableName).append(
						" where U_ID='").append(streamSource.getUIDStr()).append("') and timed >= (select distinct(timed) from ");
						toReturn.append(wrapperAlias).append(" where timed in (select timed from ").append(wrapperAlias).append(
						" where timed <= (select timed from ");
						toReturn.append(viewHelperTableName).append(" where U_ID='").append(streamSource.getUIDStr());
						toReturn.append("') ").append(" order by timed desc limit 1 offset ").append(windowSize - 1).append(" ))");
						toReturn.append(" order by timed desc ");
					} else if (Main.getWindowStorage().isSqlServer()) {
						toReturn.append("timed in (select TOP ").append(windowSize).append(" timed from ").append(wrapperAlias).append(
						" where timed <= (select timed from ").append(viewHelperTableName).append(" where U_ID='").append(streamSource.getUIDStr()).append("') order by timed desc) ");
					}else if (Main.getWindowStorage().isOracle()) {
						toReturn.append("timed <= (select timed from ").append(Main.getWindowStorage().tableNameGeneratorInString(viewHelperTableName)).append(
						" where U_ID='").append(streamSource.getUIDStr()).append("') and timed >= (select timed from ");
						toReturn.append(wrapperAlias).append(" where timed <= (select * from (select timed from ");
						toReturn.append(viewHelperTableName).append(" where U_ID='").append(Main.getWindowStorage().tableNameGeneratorInString(streamSource.getUIDStr()));
						toReturn.append("' order by timed desc  ) where rownum = "+windowSize+")  ").append(" )");
						toReturn.append(" order by timed desc ");
						// Note, in oracle rownum starts with 1.
					}else {
						logger.fatal("Not supported DB!");
					}
				} else { // WindowType.TIME_BASED_WIN_TUPLE_BASED_SLIDE
					toReturn.append("timed in (select timed from ").append(wrapperAlias).append(" where timed <= (select timed from ").append(viewHelperTableName).append(" where U_ID='").append(Main.getWindowStorage().tableNameGeneratorInString(streamSource.getUIDStr())).append(
					"') and timed >= (select timed from ").append(viewHelperTableName).append(
					" where U_ID='").append(Main.getWindowStorage().tableNameGeneratorInString(streamSource.getUIDStr())).append("') - ").append(windowSize).append(" ) ");
					//					if (StorageManager.isH2() || StorageManager.isMysqlDB()) {
					toReturn.append(" order by timed desc ");
					//					} else if (StorageManager.isOracle()) {
					//						 TODO
					//					}
				}

			}
			
			if(Main.getWindowStorage().isH2() && fromIndex > -1){
            	toReturn.append(")");
            }

			toReturn = new StringBuilder(SQLUtils.newRewrite(toReturn, rewritingMapping));
			if (logger.isDebugEnabled()) {
				logger.debug(new StringBuilder().append("The original Query : ").append(streamSource.getSqlQuery()).toString());
				logger.debug(new StringBuilder().append("The merged query : ").append(toReturn.toString()).append(" of the StreamSource ").append(streamSource.getAlias()).append(" of the InputStream: ").append(
						streamSource.getInputStream().getInputStreamName()).append("").toString());
			}
			return cachedSqlQuery = toReturn;
		}
	}
}
