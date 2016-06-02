package thesauform.controller.administration;

import java.io.IOException;

import thesauform.beans.Person;
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
 * Servlet implementation class servletUser
 */
@WebServlet("/administration/userModification")
public class UserAdministration extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2175503653162146870L;

	public static final String VUE_SUCCESS = "/WEB-INF/scripts/user.jsp";
	private static final String VUE_FAILED = "/WEB-INF/scripts/user.jsp";
	private static final String USER_ERROR = "user_error";
	private static final String USER_ERROR_PARAMETER = "errors";
	private static final String EMPTY_USER_MESSAGE = "user parameter is empty";
	private static final String SUCCESS_PARAMETER = "success";
	private static final String SUCCESS_MESSAGE = "User modification done";
	private static final String SUCCESS_DELETE_MESSAGE = "User deletion done";
	private static final String FAILED_DELETE_MESSAGE = "User deletion failed";

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
					UsersModel usersMap = new UsersModel(getServletContext().getRealPath(ThesauformConfiguration.person_file),
							getServletContext().getRealPath(ThesauformConfiguration.person_file_tmp));
					// do treatment
					// test if required parameter is present
					String userName = request.getParameter("user_name");
					if (userName != null && !userName.isEmpty()) {
						// get user information from name
						try {
							request.setAttribute("my_user", usersMap.getUser(userName));
							this.getServletContext().getRequestDispatcher(VUE_SUCCESS).forward(request, response);
						} catch (Exception e) {
							errors.put(USER_ERROR, e.getMessage());
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
					// TODO not here
					session.setAttribute(ThesauformConfiguration.PERSON_FILE,
							getServletContext().getRealPath(ThesauformConfiguration.person_file));
					// do treatment
					// test if required parameter is present
					String userName = request.getParameter("user_name");
					if (userName != null && !userName.isEmpty()) {
						// test if deletion of user is asked
						String deleteUser = request.getParameter("del");
						if (deleteUser != null && deleteUser.equals("1")) {
							try {
								request.setAttribute("my_user", usersMap.getUser(userName));
								if (usersMap.deleteUser(userName)) {
									request.setAttribute(SUCCESS_PARAMETER, SUCCESS_DELETE_MESSAGE);
									this.getServletContext().getRequestDispatcher(VUE_SUCCESS).forward(request,
											response);
								} else {
									throw new Exception(FAILED_DELETE_MESSAGE);
								}
							} catch (Exception e) {
								errors.put(USER_ERROR, e.getMessage());
								request.setAttribute(USER_ERROR_PARAMETER, errors);
								this.getServletContext().getRequestDispatcher(VUE_FAILED).forward(request, response);
							}
						} else {
							// get new informations
							boolean fail = false;
							String userMail = request.getParameter("user_mail");
							String userRight = request.getParameter("user_right");
							String userPassword = request.getParameter("user_password");
							Person modifiedUser = new Person();
							try {
								modifiedUser.setName(userName);
							} catch (Exception ex) {
								errors.put(USER_ERROR, ex.getMessage());
								fail = true;
							}
							try {
								modifiedUser.setMail(userMail);
							} catch (Exception ex) {
								errors.put(USER_ERROR, ex.getMessage());
								fail = true;
							}
							try {
								modifiedUser.setRight(userRight);
							} catch (Exception ex) {
								errors.put(USER_ERROR, ex.getMessage());
								fail = true;
							}
							try {
								modifiedUser.setPassword(userPassword);
							} catch (Exception ex) {
								errors.put(USER_ERROR, ex.getMessage());
								fail = true;
							}
							// change user in the model
							if (!fail) {
								try {
									usersMap.modifyUser(modifiedUser);
									request.setAttribute("my_user", usersMap.getUser(userName));
									request.setAttribute(SUCCESS_PARAMETER, SUCCESS_MESSAGE);
									this.getServletContext().getRequestDispatcher(VUE_SUCCESS).forward(request,
											response);
								} catch (Exception e) {
									errors.put(USER_ERROR, e.getMessage());
									request.setAttribute(USER_ERROR_PARAMETER, errors);
									this.getServletContext().getRequestDispatcher(VUE_FAILED).forward(request,
											response);
								}
							} else {
								request.setAttribute(USER_ERROR_PARAMETER, errors);
								this.getServletContext().getRequestDispatcher(VUE_FAILED).forward(request, response);
							}
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
