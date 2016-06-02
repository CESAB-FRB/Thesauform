package thesauform.controller.administration;

import java.io.IOException;

import thesauform.beans.Person;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;
import thesauform.model.UsersModel;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class servletUserAdd
 */
@WebServlet("/administration/userAdd")
public class UserAdd extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 186725532988271889L;

	public static final String VUE_SUCCESS = "/WEB-INF/scripts/addUser.jsp";
	public static final String VUE_FAILED = "/WEB-INF/scripts/addUser.jsp";
	private static final String USER_ERROR = "user_error";
	private static final String USER_ERROR_PARAMETER = "errors";
	private static final String EMPTY_USER_MESSAGE = "user parameter is empty";
	private static final String SUCCESS_PARAMETER = "success";
	private static final String SUCCESS_MESSAGE = "User modification done";

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
		// manage errors messages
		Map<String, String> errors = new HashMap<>();
		// test if a session is initialized
		HttpSession session = request.getSession(false);
		if (session != null) {
			if (session.getAttribute(ThesauformConfiguration.USR_SESSION) instanceof Person) {
				Person user = (Person) session.getAttribute(ThesauformConfiguration.USR_SESSION);
				boolean authentificationStatus = user.getAuthenticated();
				if (authentificationStatus) {
					UsersModel usersMap = new UsersModel(getServletContext().getRealPath(ThesauformConfiguration.person_file),
							getServletContext().getRealPath(ThesauformConfiguration.person_file_tmp));
					// do treatment
					String userName = request.getParameter("user_name");
					if (userName != null && !userName.isEmpty()) {
						// add new informations
						boolean fail = false;
						String userMail = request.getParameter("user_mail");
						String userRight = request.getParameter("user_right");
						String userPassword = request.getParameter("user_password");
						Person newUser = new Person();
						try {
							newUser.setName(userName);
						} catch (Exception ex) {
							errors.put(USER_ERROR, ex.getMessage());
							fail = true;
						}
						try {
							newUser.setMail(userMail);
						} catch (Exception ex) {
							errors.put(USER_ERROR, ex.getMessage());
							fail = true;
						}
						try {
							newUser.setRight(userRight);
						} catch (Exception ex) {
							errors.put(USER_ERROR, ex.getMessage());
							fail = true;
						}
						try {
							newUser.setPassword(userPassword);
						} catch (Exception ex) {
							errors.put(USER_ERROR, ex.getMessage());
							fail = true;
						}
						// change user in the model
						if (!fail) {
							try {
								usersMap.addUser(newUser);
								// add user in trait model too
								SkosTraitModel traitModel = new SkosTraitModel(
										getServletContext().getRealPath(ThesauformConfiguration.data_file));
								// add user in trait model if not existing
								traitModel.getPerson(userName, userMail);
								traitModel.save(getServletContext().getRealPath(ThesauformConfiguration.data_file));
								traitModel.close();
								request.setAttribute("my_user", usersMap.getUser(userName));
								request.setAttribute(SUCCESS_PARAMETER, SUCCESS_MESSAGE);
								this.getServletContext().getRequestDispatcher(VUE_SUCCESS).forward(request, response);
							} catch (Exception e) {
								errors.put(USER_ERROR, e.getMessage());
								request.setAttribute(USER_ERROR_PARAMETER, errors);
								this.getServletContext().getRequestDispatcher(VUE_FAILED).forward(request, response);
							}
						} else {
							request.setAttribute(USER_ERROR_PARAMETER, errors);
							this.getServletContext().getRequestDispatcher(VUE_FAILED).forward(request, response);
						}
					} else {
						errors.put(USER_ERROR, EMPTY_USER_MESSAGE);
						request.setAttribute(USER_ERROR_PARAMETER, errors);
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
