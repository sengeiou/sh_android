package com.fav24.dataservices.filter;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class AccessPolicyMonitorFilter implements Filter {

	private static Log logger = LogFactory.getLog(AccessPolicyMonitorFilter.class);


	public void init(FilterConfig config) throws ServletException {
		// nothing to implement here
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		long startTime = 0l;
		StringBuilder logText = new StringBuilder();

		try {
			if (logger.isDebugEnabled()) {

				// HTTP Servlet Request.
				HttpServletRequest hreq = (HttpServletRequest) request;

				logText.append("Request: ").append(hreq.getContextPath());
				startTime = System.currentTimeMillis();
			}

			// Next filter
			chain.doFilter(request, response);
		} 
		finally {
			if (logger.isDebugEnabled()) {

				// Log the servlet path and time taken
				logText.append(" resolved in ").append(System.currentTimeMillis() - startTime).append(" ms.");

				logger.debug(logText.toString());
			}
		}
	}

	public void destroy() {
		// Nothing to see here
	}
}
