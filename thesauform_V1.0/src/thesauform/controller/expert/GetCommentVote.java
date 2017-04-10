package thesauform.controller.expert;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import thesauform.beans.Person;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class GetCommentVote
 */
@WebServlet("/expert/get_comment")
public class GetCommentVote extends HttpServlet {


	private static final long serialVersionUID = -5277705004621906579L;
	/**
	 * get comment for a vote
	 */
	private static final String GET_TERM_PARAMETER = "concept";
	private static final String GET_PROPERTY_PARAMETER = "prop";
	private static final String GET_VALUE_PARAMETER = "value";
	private static final String ERROR_PARAMETER = "parameter";
	private static final String ERROR_MESSAGE_PARAMETER = "one parameter is empty";
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SkosTraitModel traitModel = null;
		// manage errors messages
		Map<String, String> errors = new HashMap<>();
		//get comment for the vote
		String comment = "";
		// test if a session is initialized
		HttpSession session = request.getSession(false);
		if (session != null) {
			if (session.getAttribute(ThesauformConfiguration.USR_SESSION) instanceof Person) {
				Person user = (Person) session.getAttribute(ThesauformConfiguration.USR_SESSION);
				boolean authentificationStatus = user.getAuthenticated();
				if (authentificationStatus) {
					// do treatment
					try {
						// test parameters
						String concept = URLDecoder.decode(request.getParameter(GET_TERM_PARAMETER), "UTF-8");
						String property = URLDecoder.decode(request.getParameter(GET_PROPERTY_PARAMETER), "UTF-8");
						String value = URLDecoder.decode(request.getParameter(GET_VALUE_PARAMETER), "UTF-8");
						if (concept == null || concept.isEmpty() || property == null || property.isEmpty() || value == null || value.isEmpty()) {
							errors.put(ERROR_PARAMETER, ERROR_MESSAGE_PARAMETER);
							throw new Exception(ERROR_MESSAGE_PARAMETER);
						}
						if(ThesauformConfiguration.database) {
							traitModel = new SkosTraitModel(ThesauformConfiguration.data_file);
						}
						else {
							traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.data_file));
						}
						// return vote comment
						comment = traitModel.getVoteComment(concept, property, user.getName(), value);
						response.setContentType("application/json");
						PrintWriter writer = response.getWriter();
						writer.write("{\"comment\":\"" + comment + "\"}");
						writer.close();
						// clean memory
						traitModel.close();
					} catch (Exception ex) {
						response.setContentType("application/json");
						PrintWriter writer = response.getWriter();
						ex.printStackTrace();
						writer.write("{\"error\":\"1\", \"message\":\"" + ex.toString() + "\"}");
						writer.close();
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
