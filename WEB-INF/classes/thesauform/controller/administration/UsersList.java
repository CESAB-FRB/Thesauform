package thesauform.controller.administration;

import java.io.IOException;

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
 * Servlet implementation class servletUser
 */
@WebServlet("/administration/usersList")
public class UsersList extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8471023734502503312L;

	public static final String VUE_SUCCESS = "/WEB-INF/scripts/users.jsp";
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
					UsersModel usersMap = null;					
					if(ThesauformConfiguration.database) {
						usersMap = new UsersModel(ThesauformConfiguration.person_file,
								ThesauformConfiguration.person_file_tmp);
					}
					else {
						usersMap = new UsersModel(getServletContext().getRealPath(ThesauformConfiguration.person_file),
								getServletContext().getRealPath(ThesauformConfiguration.person_file_tmp));
					}
					//TODO not here
					session.setAttribute(PERSON_FILE,
							getServletContext().getRealPath(ThesauformConfiguration.person_file));
					// do treatment
					// pass list of all users to the view
					request.setAttribute("my_user_list", usersMap.getAllUsers());
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
