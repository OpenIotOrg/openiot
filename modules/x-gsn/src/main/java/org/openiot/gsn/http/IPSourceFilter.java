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

package org.openiot.gsn.http;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IPSourceFilter implements Filter {

    private static transient Logger logger = Logger.getLogger( IPSourceFilter.class );

    private FilterConfig filterConfig;

    private String[] allowedSrcIps = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        allowedSrcIps = filterConfig.getInitParameter("allowedIps").split(";");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String ip = request.getRemoteAddr();

        HttpServletResponse httpResp = null;

        if (response instanceof HttpServletResponse)
            httpResp = (HttpServletResponse) response;

        if (isAllowed(ip)) {
            chain.doFilter(request, response);
        } else {
            logger.warn("IP: " + ip + " not allowed.");
            httpResp.sendError(HttpServletResponse.SC_FORBIDDEN);
        }

    }

    public void destroy() {}

    private boolean isAllowed (String ip) {
        for (String aip : allowedSrcIps) {
            if (aip.equals(ip))
                return true;
        }
        return false;
    }
}
