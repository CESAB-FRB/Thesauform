package thesauform.controller;

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
@WebServlet("/annotationModification")
public class ServletAnnotation extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7789241938936364734L;
	
	public static final String VUE_SUCCESS = "/WEB-INF/scripts/annotation.jsp";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// test if a session is initialized
		HttpSession session = request.getSession(false);
		if (session != null) {
			if (session.getAttribute(ThesauformConfiguration.USR_SESSION) instanceof Person) {
				Person user = (Person) session.getAttribute(ThesauformConfiguration.USR_SESSION);
				boolean authentificationStatus = user.getAuthenticated();
				if (authentificationStatus) {
					// do treatment
					if (request.getParameter("inputAnn") == null && request.getParameter("nameAdd") != null) {
						try {
							Annotation.submitSynchronized(session, request, false);
							request.removeAttribute("nameAdd");
						} catch (Exception e) {
							//TODO exception management
							ThesauformConfiguration.thesauform_logger.error(String.class.getName()+ ": error" + e.getMessage());
						}
					} else if (request.getParameter("inputAnn") != null) {
						try {
							Annotation.submitSynchronized(session, request, true);
							request.removeAttribute("inputAnn");
						} catch (Exception e) {
							//TODO exception management
							ThesauformConfiguration.thesauform_logger.error(String.class.getName()+ ": error" + e.getMessage());
						}
					}
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
					// do treatment
					if (request.getParameter("fail") == null) {
						if (request.getParameter("inputAnn") == null && request.getParameter("nameAdd") != null) {
							try {
								Annotation.submitSynchronized(session, request, false);
								request.removeAttribute("nameAdd");
							} catch (Exception e) {
								// @TODO exception management
								System.err.println(e.getMessage());
							}
						} else if (request.getParameter("inputAnn") != null) {
							try {
								Annotation.submitSynchronized(session, request, true);
								request.removeAttribute("inputAnn");
							} catch (Exception e) {
								// @TODO exception management
								System.err.println(e.getMessage());
							}
						}
					}
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
