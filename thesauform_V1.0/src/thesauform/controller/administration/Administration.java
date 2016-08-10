package thesauform.controller.administration;

import java.io.IOException;

import thesauform.beans.Person;
import thesauform.model.ThesauformConfiguration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class servetAnnotation
 */
@WebServlet("/administration")
public class Administration extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2368655649426336349L;

	public static final String VUE_SUCCESS = "/WEB-INF/scripts/administration.jsp";
	private static final String PERSON_FILE = "person_file";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// test if a session is initialized
		HttpSession session = request.getSession(false);
		if (session != null) {
			if (session.getAttribute(ThesauformConfiguration.USR_SESSION) instanceof Person) {
				Person user = (Person) session.getAttribute(ThesauformConfiguration.USR_SESSION);
				boolean authentificationStatus = user.getAuthenticated();
				if (authentificationStatus) {
					if(ThesauformConfiguration.database) {
						session.setAttribute(PERSON_FILE, ThesauformConfiguration.person_file);						
					}
					else {
						session.setAttribute(PERSON_FILE, getServletContext().getRealPath(ThesauformConfiguration.person_file));
					}
					// do treatment
					this.getServletContext().getRequestDispatcher(VUE_SUCCESS).forward(request, response);
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

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// test if a session is initialized
		HttpSession session = request.getSession(false);
		if (session != null) {
			if (session.getAttribute(ThesauformConfiguration.USR_SESSION) instanceof Person) {
				Person user = (Person) session.getAttribute(ThesauformConfiguration.USR_SESSION);
				boolean authentificationStatus = user.getAuthenticated();
				if (authentificationStatus) {
					if(ThesauformConfiguration.database) {
						session.setAttribute(PERSON_FILE, ThesauformConfiguration.person_file);
					}
					else {
						session.setAttribute(PERSON_FILE, getServletContext().getRealPath(ThesauformConfiguration.person_file));
					}
					// do treatment
					this.getServletContext().getRequestDispatcher(VUE_SUCCESS).forward(request, response);
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
