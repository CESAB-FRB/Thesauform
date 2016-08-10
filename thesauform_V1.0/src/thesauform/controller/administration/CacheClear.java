package thesauform.controller.administration;

import java.io.IOException;

import thesauform.beans.Person;
import thesauform.model.ThesauformConfiguration;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class servletUser
 */
@WebServlet("/administration/clearCache")
public class CacheClear extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4559081390625771113L;

	public static final String VUE_SUCCESS = "/WEB-INF/scripts/cache.jsp";
	private static final String VUE_FAILED = "/WEB-INF/scripts/cache.jsp";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// manage errors messages
		Map<String, String> errors = new HashMap<>();
		// test if a session is initialized
		HttpSession session = request.getSession(false);
		if (session != null) {
			if (session.getAttribute(ThesauformConfiguration.USR_SESSION) instanceof Person) {
				Person user = (Person) session.getAttribute(ThesauformConfiguration.USR_SESSION);
				boolean authentificationStatus = user.getAuthenticated();
				if (authentificationStatus) {
					// do treatment
					// clear cache
					try {
						PrintWriter cleanerF;
						if(ThesauformConfiguration.database) {
							cleanerF = new PrintWriter(ThesauformConfiguration.data_file_tmp);
						}
						else {
							cleanerF = new PrintWriter(
									getServletContext().getRealPath(ThesauformConfiguration.data_file_tmp));
						}
						cleanerF.print("");
						cleanerF.close();
						PrintWriter cleanerP;
						if(ThesauformConfiguration.database) {
							cleanerP = new PrintWriter(ThesauformConfiguration.person_file_tmp);
						}
						else {
							cleanerP = new PrintWriter(
									getServletContext().getRealPath(ThesauformConfiguration.person_file_tmp));
						}
						cleanerP.print("");
						cleanerP.close();
						this.getServletContext().getRequestDispatcher(VUE_SUCCESS).forward(request, response);
					} catch (Exception e) {
						errors.put(ThesauformConfiguration.CACHE_ERROR, ThesauformConfiguration.CACHE_MESSAGE);
						request.setAttribute(ThesauformConfiguration.CACHE_ERROR_PARAMETER, errors);
						this.getServletContext().getRequestDispatcher(VUE_FAILED).forward(request, response);
					}
				} else {
					// re-authenticate
					this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request,
							response);
				}
			} else {
				// re-authenticate
				this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request,
						response);
			}
		} else {
			// re-authenticate
			this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request,
					response);
		}
	}
}
