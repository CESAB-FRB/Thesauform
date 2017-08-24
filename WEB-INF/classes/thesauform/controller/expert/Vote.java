package thesauform.controller.expert;

import java.io.IOException;

import thesauform.beans.Person;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;
import thesauform.model.VotesModel;

import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class servletVote
 */
@WebServlet("/expert/vote")
public class Vote extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2716291277703428486L;

	private final String EMPTY_TRAIT_NAME_MESSAGE = "Empty parameter trait name";
	private final String EMPTY_PROPERTY_MESSAGE = "Empty parameter property";
	private final String EMPTY_VALUE_MESSAGE = "Empty parameter value";
	private final String EMPTY_VOTE_VALUE_MESSAGE = "Empty parameter vote_value";
	private final String WRONG_VOTE_VALUE_MESSAGE = "Wrong parameter vote_value";
	private final String EMPTY_ACTION_MESSAGE = "Empty parameter action";
	private final String WRONG_ACTION_MESSAGE = "Wrong parameter action";

	

	public String normalSpecialChar(String value) {
		value = value.replaceAll(";and;", "&");
		return value;
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// manage errors messages
		SkosTraitModel traitModel = null;
		Integer countNb = 0;
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
						String action = request.getParameter("action");
						if (action == null || action.isEmpty()) {
							throw new Exception(EMPTY_ACTION_MESSAGE);
						} else {
							if (!(action.equals("add") || action.equals("del"))) {
								throw new Exception(WRONG_ACTION_MESSAGE + " " + action);
							}
						}
						String traitName = request.getParameter("trait_name");
						if (traitName == null || traitName.isEmpty()) {
							throw new Exception(EMPTY_TRAIT_NAME_MESSAGE);
						}
						String property = request.getParameter("property");
						if (property == null || property.isEmpty()) {
							throw new Exception(EMPTY_PROPERTY_MESSAGE);
						}
						String value = request.getParameter("value");
						if (value == null || value.isEmpty()) {
							throw new Exception(EMPTY_VALUE_MESSAGE);
						}
						//@PATCH for special character & et #
						value = normalSpecialChar(value);
						//@PATCH for def + ref
						if(value.matches(".*\\(ref: .+\\)")) {
							value = value.replaceAll("(.*)\\(ref: (.*)\\)", "$1__$2");
						}
						Integer voteValue;
						String voteValueString = request.getParameter("vote_value");
						if (voteValueString == null || voteValueString.isEmpty()) {
							throw new Exception(EMPTY_VOTE_VALUE_MESSAGE);
						}
						else {
							if(voteValueString.matches("[012345]")) {
								voteValue = Integer.parseInt(voteValueString);
							}
							else {
								throw new Exception(WRONG_VOTE_VALUE_MESSAGE);
							}
						}
						if(ThesauformConfiguration.database) {
							traitModel = new SkosTraitModel(ThesauformConfiguration.data_file);
						}
						else {
							traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.data_file));
						}
						VotesModel myVote;
						if(ThesauformConfiguration.database) {
							myVote = new VotesModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
									property, user.getName(), value, voteValue);
						}
						else {
							myVote = new VotesModel(traitModel,
									getServletContext().getRealPath(ThesauformConfiguration.data_file),
									getServletContext().getRealPath(ThesauformConfiguration.data_file_tmp), traitName,
									property, user.getName(), value, voteValue);
						}
						// do vote
						if (action.equals("add")) {
							countNb = myVote.addVote();
						} else {
							countNb = myVote.deleteVote();
						}
						// return vote number
						response.setContentType("application/json");
						PrintWriter writer = response.getWriter();
						writer.write("{\"nb\":\"" + countNb + "\"}");
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
