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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class RemoteTimeBasedSlidingHandler implements SlidingHandler {

	private static final transient Logger logger = Logger.getLogger(RemoteTimeBasedSlidingHandler.class);
	private List<StreamSource> streamSources;
	private Map<StreamSource, Long> slidingHashMap;
	private AbstractWrapper wrapper;
	private long timediff;

	public RemoteTimeBasedSlidingHandler(AbstractWrapper wrapper) {
		streamSources = Collections.synchronizedList(new ArrayList<StreamSource>());
		slidingHashMap = Collections.synchronizedMap(new HashMap<StreamSource, Long>());
		this.wrapper = wrapper;
	}

	public void addStreamSource(StreamSource streamSource) {
		SQLViewQueryRewriter rewriter = new RTBSQLViewQueryRewriter();
		rewriter.setStreamSource(streamSource);
		streamSource.setQueryRewriter(rewriter);
		rewriter.initialize();
		if (streamSource.getWindowingType() != WindowType.TIME_BASED_SLIDE_ON_EACH_TUPLE) {
			slidingHashMap.put(streamSource, -1L);
		}
		streamSources.add(streamSource);
	}

	public synchronized boolean dataAvailable(StreamElement streamElement) {
		boolean toReturn = false;
		synchronized (streamSources) {
			for (StreamSource streamSource : streamSources) {
				if (streamSource.getWindowingType() == WindowType.TIME_BASED_SLIDE_ON_EACH_TUPLE) {
					toReturn = streamSource.getQueryRewriter().dataAvailable(streamElement.getTimeStamp()) || toReturn;
				} else {
					long nextSlide = slidingHashMap.get(streamSource);
					// this is the first stream element
					if (nextSlide == -1) {
						slidingHashMap.put(streamSource, streamElement.getTimeStamp() + streamSource.getParsedSlideValue());
					} else {
						long timeStamp = streamElement.getTimeStamp();
						if (nextSlide <= timeStamp) {
							// long timestampDiff = timeStamp - nextSlide;
							// int slideValue =
							// streamSource.getParsedSlideValue();
							// nextSlide = nextSlide + (timestampDiff /
							// slideValue + 1) * slideValue;
							nextSlide = timeStamp + streamSource.getParsedSlideValue();
							toReturn = streamSource.getQueryRewriter().dataAvailable(timeStamp) || toReturn;
							slidingHashMap.put(streamSource, nextSlide);
						}
					}
				}
			}
		}
		return toReturn;
	}

	public void removeStreamSource(StreamSource streamSource) {
		streamSources.remove(streamSource);
		slidingHashMap.remove(streamSource);
		streamSource.getQueryRewriter().dispose();
	}

	public void dispose() {
		synchronized (streamSources) {
			for (Iterator<StreamSource> iterator = streamSources.iterator(); iterator.hasNext();) {
				StreamSource streamSource = iterator.next();
				streamSource.getQueryRewriter().dispose();
				iterator.remove();
				slidingHashMap.remove(streamSource);
			}
		}
	}

	public long getOldestTimestamp() {
		long timed1 = -1;
		long timed2 = -1;
		long maxTupleCount = 0;
		long maxSlideForTupleBased = 0;
		long maxWindowSize = 0;

		synchronized (streamSources) {
			for (StreamSource streamSource : streamSources) {
				if (streamSource.getWindowingType() != WindowType.TUPLE_BASED_WIN_TIME_BASED_SLIDE) {
					maxWindowSize = Math.max(maxWindowSize, streamSource.getParsedStorageSize());
				} else {
					maxSlideForTupleBased = Math.max(maxSlideForTupleBased, streamSource.getParsedSlideValue());
					maxTupleCount = Math.max(maxTupleCount, streamSource.getParsedStorageSize());
				}
				if (streamSource.getWindowingType() == WindowType.TIME_BASED) {
					maxWindowSize = Math.max(maxWindowSize, streamSource.getParsedStorageSize() + streamSource.getParsedSlideValue());
				}
			}
		}

		if (maxWindowSize > 0) {
			StringBuilder query = new StringBuilder();
			query.append("select max(timed) - ").append(maxWindowSize).append(" from ").append(wrapper.getDBAliasInStr());

			if (logger.isDebugEnabled()) {
				logger.debug("Query1 for getting oldest timestamp : " + query);
			}
			Connection conn = null;
			try {
				ResultSet resultSet = Main.getWindowStorage().executeQueryWithResultSet(query,conn = Main.getWindowStorage().getConnection());
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
				query.append(" select timed from ").append(wrapper.getDBAliasInStr()).append(" where timed <= ");
				query.append(System.currentTimeMillis() - timediff - maxSlideForTupleBased).append(" order by timed desc limit 1 offset ").append(
						maxTupleCount - 1);
			} else if (Main.getWindowStorage().isSqlServer()) {
				query.append(" select min(timed) from (select top ").append(maxTupleCount).append(" * ").append(" from ").append(
						wrapper.getDBAliasInStr()).append(" where timed <= ").append(System.currentTimeMillis() - timediff - maxSlideForTupleBased).append(" order by timed desc) as X  ");
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

	public boolean isInterestedIn(StreamSource streamSource) {
		return WindowType.isTimeBased(streamSource.getWindowingType());
	}

	private class RTBSQLViewQueryRewriter extends SQLViewQueryRewriter {

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
			if (streamSource.getSamplingRate() == 0 || (streamSource.isStorageCountBased() && windowSize == 0)) {
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
					toReturn.append(" ( timed - (timed / 100) * 100 < ").append(streamSource.getSamplingRate() * 100).append(") and ");
				} else {
					toReturn.append(" ( mod( timed , 100)< ").append(streamSource.getSamplingRate() * 100).append(") and ");
				}
			}
			WindowType windowingType = streamSource.getWindowingType();
			if (windowingType == WindowType.TIME_BASED_SLIDE_ON_EACH_TUPLE) {
				toReturn.append("(wrapper.timed >= (select timed from ").append(VIEW_HELPER_TABLE).append(" where U_ID='").append(
						streamSource.getUIDStr());
				toReturn.append("') - ").append(windowSize).append(") ");
				if (Main.getWindowStorage().isH2() || Main.getWindowStorage().isMysqlDB() || Main.getWindowStorage().isPostgres()) {
					toReturn.append(" order by timed desc ");
				}
			} else {
				if (windowingType == WindowType.TIME_BASED) {
					toReturn.append("timed in (select timed from ").append(wrapperAlias).append(" where timed <= (select timed from ").append(SQLViewQueryRewriter.VIEW_HELPER_TABLE).append(" where U_ID='").append(streamSource.getUIDStr()).append(
					"') and timed >= (select timed from ").append(SQLViewQueryRewriter.VIEW_HELPER_TABLE).append(
					" where U_ID='").append(streamSource.getUIDStr()).append("') - ").append(windowSize).append(" ) ");
					if (Main.getWindowStorage().isH2() || Main.getWindowStorage().isMysqlDB() || Main.getWindowStorage().isPostgres()) {
						toReturn.append(" order by timed desc ");
					}
				} else {// WindowType.TUPLE_BASED_WIN_TIME_BASED_SLIDE
					if (Main.getWindowStorage().isMysqlDB() || Main.getWindowStorage().isPostgres()) {
						toReturn.append("timed <= (select timed from ").append(SQLViewQueryRewriter.VIEW_HELPER_TABLE).append(
						" where U_ID='").append(streamSource.getUIDStr()).append("') and timed >= (select timed from ");
						toReturn.append(wrapperAlias).append(" where timed <= (select timed from ");
						toReturn.append(SQLViewQueryRewriter.VIEW_HELPER_TABLE).append(" where U_ID='").append(streamSource.getUIDStr());
						toReturn.append("') ").append(" order by timed desc limit 1 offset ").append(windowSize - 1).append(" )");
						toReturn.append(" order by timed desc ");
					} else if (Main.getWindowStorage().isH2()) {
						toReturn.append("timed <= (select timed from ").append(SQLViewQueryRewriter.VIEW_HELPER_TABLE).append(
						" where U_ID='").append(streamSource.getUIDStr()).append("') and timed >= (select distinct(timed) from ");
						toReturn.append(wrapperAlias).append(" where timed in (select timed from ").append(wrapperAlias).append(
						" where timed <= (select timed from ");
						toReturn.append(SQLViewQueryRewriter.VIEW_HELPER_TABLE).append(" where U_ID='").append(streamSource.getUIDStr());
						toReturn.append("') ").append(" order by timed desc limit 1 offset ").append(windowSize - 1).append(" ))");
						toReturn.append(" order by timed desc ");
					} else if (Main.getWindowStorage().isSqlServer()) {
						toReturn.append("timed in (select TOP ").append(windowSize).append(" timed from ").append(wrapperAlias).append(
						" where timed <= (select timed from ").append(SQLViewQueryRewriter.VIEW_HELPER_TABLE).append(" where U_ID='").append(streamSource.getUIDStr()).append("') order by timed desc ) ");
					}
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
