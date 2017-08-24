package thesauform.controller;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import thesauform.beans.Person;
import thesauform.model.ThesauformConfiguration;
import thesauform.model.UsersModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class servetAnnotation
 */
@WebServlet({"/authentication","/expert/authentication","/administration/authentication"})
public class AuthenticationThesauform extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6797405185157677447L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getServletContext().setAttribute("_database_", ThesauformConfiguration.database);
		getServletContext().setAttribute("_database_path_", ThesauformConfiguration.database_path);
		getServletContext().setAttribute("_tab_title_", ThesauformConfiguration.tab_title);
		getServletContext().setAttribute("_data_file_", ThesauformConfiguration.data_file);
		getServletContext().setAttribute("_data_file_tmp_", ThesauformConfiguration.data_file_tmp);
		getServletContext().setAttribute("_public_data_file_", ThesauformConfiguration.public_data_file);
		getServletContext().setAttribute("_person_file_", ThesauformConfiguration.person_file);
		getServletContext().setAttribute("_person_file_tmp_", ThesauformConfiguration.person_file_tmp);
		getServletContext().setAttribute("_term_uri_", ThesauformConfiguration.term_uri);
		getServletContext().setAttribute("_person_uri_", ThesauformConfiguration.person_uri);
		getServletContext().setAttribute("_super_root_", ThesauformConfiguration.super_root);
		getServletContext().setAttribute("_term_root_", ThesauformConfiguration.term_root);
		getServletContext().setAttribute("_software_title_", ThesauformConfiguration.software_title);
		getServletContext().setAttribute("_explanation_title_", ThesauformConfiguration.explanation_title);
		getServletContext().setAttribute("_contact_information_", ThesauformConfiguration.contact_information);
		getServletContext().setAttribute("_contact_mail_", ThesauformConfiguration.contact_mail);
		getServletContext().setAttribute("_trait_display_", ThesauformConfiguration.trait_display);
		getServletContext().setAttribute("_logos_", ThesauformConfiguration.logos);
		getServletContext().setAttribute("_logo_header_", ThesauformConfiguration.logo);
		getServletContext().setAttribute("_facet_list_", ThesauformConfiguration.facet_list);
		// test if a session is initialized
		HttpSession session = request.getSession(false);
		// get the mode
		String action = request.getParameter(ThesauformConfiguration.PARAMETER_MODE);
		if (session != null) {
			if (session.getAttribute(ThesauformConfiguration.USR_SESSION) instanceof Person) {
				Person user = (Person) session.getAttribute(ThesauformConfiguration.USR_SESSION);
				boolean authentificationStatus = user.getAuthenticated();
				if (authentificationStatus) {
					if (action != null) {
						switch (action) {
						case ThesauformConfiguration.MODE_ANNOTATION:
							this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_SUCCESS_ANNOTATION).forward(request,
									response);
							break;
						case ThesauformConfiguration.MODE_VOTE:
							this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_SUCCESS_VOTE).forward(request, response);
							break;
						case ThesauformConfiguration.MODE_ADMINISTRATION:
							this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_SUCCESS_ADMINISTRATION).forward(request,
									response);
							break;
						default:
							this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
							break;
						}
					} else {
						this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
					}
				} else {
					this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
				}
			} else {
				this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
			}
		} else {
			this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* Get actual class name to be printed on */

		// manage errors messages
		Map<String, String> errors = new HashMap<>();
		// create session
		HttpSession session = request.getSession();
		// get the mode
		String action = request.getParameter(ThesauformConfiguration.PARAMETER_MODE);
		// test no exception is encountered
		boolean isAuthentificationProcess = true;
		// initialize object person
		Person user = new Person();
		// set property name
		try {
			user.setName(request.getParameter(ThesauformConfiguration.FORM_NAME));
		} catch (Exception e) {
			errors.put(ThesauformConfiguration.FORM_NAME, ThesauformConfiguration.ERROR_MSG + e.getMessage());
			isAuthentificationProcess = false;
		}
		// set property password
		try {
			user.setPassword(request.getParameter(ThesauformConfiguration.FORM_PASS));
		} catch (Exception e) {
			errors.put(ThesauformConfiguration.FORM_PASS, ThesauformConfiguration.ERROR_MSG + e.getMessage());
			isAuthentificationProcess = false;
		}
		// authentication would no success if an error is set before
		if (isAuthentificationProcess) {
			// test if person exist in data file
			UsersModel usersMap = null;					
			if(ThesauformConfiguration.database) {
				usersMap = new UsersModel(ThesauformConfiguration.person_file,
						ThesauformConfiguration.person_file_tmp);
			}
			else {
				usersMap = new UsersModel(getServletContext().getRealPath(ThesauformConfiguration.person_file),
						getServletContext().getRealPath(ThesauformConfiguration.person_file_tmp));
			}
			boolean authentificationStatus = false;
			try {
				switch (action) {
				case ThesauformConfiguration.MODE_ANNOTATION:
					authentificationStatus = usersMap.authentifyUser(user);
					break;
				case ThesauformConfiguration.MODE_VOTE: // check if expert or administrator
					authentificationStatus = usersMap.authentifyUser(user, ThesauformConfiguration.VOTE_RANK)
							|| usersMap.authentifyUser(user, ThesauformConfiguration.ADMIN_RANK);
					break;
				case ThesauformConfiguration.MODE_ADMINISTRATION: // check if administrator
					authentificationStatus = usersMap.authentifyUser(user, ThesauformConfiguration.ADMIN_RANK);
					break;
				default:
					this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (authentificationStatus) {
				try {
					// authentication success, set session
					user = usersMap.getUser(user.getName());
					user.isAuthenticated();
					session.setAttribute(ThesauformConfiguration.USR_SESSION, user);
				} catch (Exception e) {
					errors.put(ThesauformConfiguration.FORM_STATUS, ThesauformConfiguration.ERROR_MSG + ThesauformConfiguration.ERROR_STATUS);
					request.setAttribute(ThesauformConfiguration.FORM_ERROR, errors);
					session.setAttribute(ThesauformConfiguration.USR_SESSION, null);
					this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
				}
				// display view
				switch (action) {
				case ThesauformConfiguration.MODE_ANNOTATION:
					this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_SUCCESS_ANNOTATION).forward(request, response);
					break;
				case ThesauformConfiguration.MODE_VOTE:
					this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_SUCCESS_VOTE).forward(request, response);
					break;
				case ThesauformConfiguration.MODE_ADMINISTRATION:
					this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_SUCCESS_ADMINISTRATION).forward(request,
							response);
					break;
				default:
					this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
					break;
				}
			} else {
				errors.put(ThesauformConfiguration.FORM_STATUS, ThesauformConfiguration.ERROR_MSG + ThesauformConfiguration.ERROR_STATUS);
				request.setAttribute(ThesauformConfiguration.FORM_ERROR, errors);
				session.setAttribute(ThesauformConfiguration.USR_SESSION, null);
				this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
			}
		} else {
			request.setAttribute(ThesauformConfiguration.FORM_ERROR, errors);
			session.setAttribute(ThesauformConfiguration.USR_SESSION, null);
			this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
		}
	}
}
