package com.naicson.yugioh.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter  extends HttpFilter {

	protected static final Logger LOGGER = LoggerFactory.getLogger(CorrelationIdFilter.class);
	
	private static final  String CORRELATION_ID = "correlation-id";
	
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
        try {
            MDC.put("CorrelationId", getCorrelationId());
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("CorrelationId");
        }
    }

    private String getCorrelationId() {
        return UUID.randomUUID().toString();
    }

//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//		
//		  var req = (HttpServletRequest) request;
//          var resp = (HttpServletResponse) response;
//          var cid = req.getHeader(CORRELATION_ID);
//          
//          LOGGER.info("[cid: {}] REQ {} {}?{}", cid, req.getMethod(), req.getRequestURL(), req.getQueryString());
//          resp.addHeader(CORRELATION_ID, cid);
//          chain.doFilter(req, resp);
//          LOGGER.info("[cid: {}] RESP status: {}", cid, resp.getStatus());
//	}

}
