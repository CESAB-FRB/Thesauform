package thesauform.controller.administration;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;

/**
 * Servlet implementation class ExportFile
 */
@WebServlet("/administration/generateCollection")
public class GenerateCollection extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String VUE_FAILED = "/WEB-INF/scripts/generateCollection.jsp";
	private static final String INSERT_COLLECTION_TYPE = "insert";
	private static final String UPDATE_COLLECTION_TYPE = "update";
	private static final String DELETE_COLLECTION_TYPE = "delete";
	private static final String ALL_TYPE = "all";
	private static final String ROUND_ZERO = "zero";
	private static final String TYPE_ERROR = "type";
	private static final String TYPE_ERROR_MESSAGE = "Wrong type of collection given";
	private static final String USER_ERROR_PARAMETER = "my_errors";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// manage errors messages
		Map<String, String> errors = new HashMap<>();
		errors.put(TYPE_ERROR, "");
		// trait model
		SkosTraitModel traitModel = null;
		//get parameter
		String collectionType = request.getParameter("type");
		String typeCollection = null;
		List<String> collectionMember = null;
		//do treatment
		try {
			if (collectionType != null && !collectionType.isEmpty()) {
				if(ThesauformConfiguration.database) {
					traitModel = new SkosTraitModel(ThesauformConfiguration.data_file);
				}
				else {
					traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.data_file));
				}
				switch (collectionType) {
					case INSERT_COLLECTION_TYPE:
						typeCollection = INSERT_COLLECTION_TYPE;
						collectionMember = traitModel.getInCollection(typeCollection);
						break;
					case DELETE_COLLECTION_TYPE:
						typeCollection = DELETE_COLLECTION_TYPE;
						collectionMember = traitModel.getInCollection(typeCollection);
						break;
					case UPDATE_COLLECTION_TYPE:
						typeCollection = UPDATE_COLLECTION_TYPE;
						collectionMember = traitModel.getInCollection(typeCollection);
						break;
					case ALL_TYPE:
						collectionMember = traitModel.getAllConcept();
						break;
					case ROUND_ZERO:
						collectionMember = traitModel.getAllConceptWithDef();
						break;
					default:
						errors.put(TYPE_ERROR, TYPE_ERROR_MESSAGE);
						throw new Exception(TYPE_ERROR_MESSAGE);
				}
				PrintWriter pw = response.getWriter();
				for (String member : collectionMember) {
					pw.println(member);			
				}
			}
			else {
				errors.put(TYPE_ERROR, TYPE_ERROR_MESSAGE);
				throw new Exception(TYPE_ERROR_MESSAGE);
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			request.setAttribute(USER_ERROR_PARAMETER, errors);
			this.getServletContext().getRequestDispatcher(VUE_FAILED).forward(request, response);			
		}
	}
}
