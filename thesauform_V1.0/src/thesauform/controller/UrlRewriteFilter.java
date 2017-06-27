package thesauform.controller;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;


/**
 * Servlet Filter implementation class UrlRewriteFilter for URL rewritting
 */
@WebFilter("/UrlRewriteFilter")
public class UrlRewriteFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public UrlRewriteFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		//get the URL to map
		HttpServletRequest req = (HttpServletRequest) request;
	    String requestURI = req.getRequestURI();
	    //test the URL
        if(requestURI.contains("TRENVAS")) {
        	String newURI = requestURI.replaceAll("(.*)TRENVAS/", "annotationInfo?viz=1&trait=");
        	req.getRequestDispatcher("/"+newURI).forward(request, response);
        }
        else {
        	chain.doFilter(request, response);
        }
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
