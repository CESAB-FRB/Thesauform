package thesauform.controller.expert;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import thesauform.beans.Person;
import thesauform.model.ThesauformConfiguration;

/**
 * Servlet Filter implementation class ExpertAuthentificationFilter
 */
@WebFilter("/ExpertAuthentificationFilter")
public class ExpertAuthenticationFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public ExpertAuthenticationFilter() {
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
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// check user session if logged and rank against the path
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();
		if (session.getAttribute(ThesauformConfiguration.USR_SESSION) instanceof Person) {
			Person user = (Person) session.getAttribute(ThesauformConfiguration.USR_SESSION);
			String rank = user.getRight();
			boolean authentificationStatus = user.getAuthenticated()
					&& (rank == ThesauformConfiguration.VOTE_RANK || rank == ThesauformConfiguration.ADMIN_RANK);
			if (authentificationStatus) {
				// pass the request along the filter chain
				chain.doFilter(request, response);
			} else {
				// re-authenticate
				request.getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
			}
		} else {
			// re-authenticate
			request.getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
