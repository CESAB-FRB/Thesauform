package thesauform.controller.administration;

import java.io.IOException;

import thesauform.beans.Person;
import thesauform.controller.expert.Vote;
import thesauform.model.SkosModel;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;
import thesauform.model.ValidatedModel;
import thesauform.model.vocabularies.ChangeVoc;
import thesauform.model.vocabularies.SkosVoc;
import thesauform.model.vocabularies.SkosXLVoc;
import thesauform.model.vocabularies.TraitVocTemp;

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
@WebServlet("/administration/validate_test")
public class ValidateTest extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2452193494822424082L;

	private final String EMPTY_TRAIT_NAME_MESSAGE = "Empty parameter trait name";
	private final String EMPTY_PROPERTY_MESSAGE = "Empty parameter property";
	private final String EMPTY_VALUE_MESSAGE = "Empty parameter value";
	private final String EMPTY_VAL_VALUE_MESSAGE = "Empty parameter validation type";
	private final String WRONG_VAL_VALUE_MESSAGE = "Wrong parameter validation type";

	public boolean testValidated(ValidatedModel validatedModel,Integer type) {
		boolean my_return = false;
		try {
			if(type==1)
			{
				my_return = validatedModel.addValidated();
			}
			else {
				my_return = validatedModel.addInvalidated();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return(my_return);
	}
	
	@Override
	synchronized public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// manage errors messages
		SkosTraitModel traitModel = null;
		Boolean my_result = false;
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
						String traitName = request.getParameter("trait_name");
						traitName = java.net.URLDecoder.decode(traitName, "UTF-8");
						if (traitName == null || traitName.isEmpty()) {
							throw new Exception(EMPTY_TRAIT_NAME_MESSAGE);
						}
						String property = request.getParameter("property");
						if (property == null || property.isEmpty()) {
							throw new Exception(EMPTY_PROPERTY_MESSAGE);
						}
						String value = request.getParameter("value");
						value = java.net.URLDecoder.decode(value, "UTF-8");
						if (value == null || value.isEmpty()) {
							throw new Exception(EMPTY_VALUE_MESSAGE);
						}
						String comment = request.getParameter("comment");
						if(comment!=null&&!comment.isEmpty()) {
							comment = java.net.URLDecoder.decode(comment, "UTF-8");
						}
						//@PATCH for special character & et #
						value = Vote.normalSpecialChar(value);
						//@PATCH for def + ref
						if(value.matches(".*\\(ref: .+\\)")) {
							value = value.replaceAll("(.*)\\(ref: (.*)\\)", "$1__$2");
						}
						Integer validatedValue;
						String validatedValueString = request.getParameter("val");
						if (validatedValueString == null || validatedValueString.isEmpty()) {
							throw new Exception(EMPTY_VAL_VALUE_MESSAGE);
						}
						else {
							if(validatedValueString.matches("[01]")) {
								validatedValue = Integer.parseInt(validatedValueString);
							}
							else {
								throw new Exception(WRONG_VAL_VALUE_MESSAGE);
							}
						}
						if(ThesauformConfiguration.database) {
							traitModel = new SkosTraitModel(ThesauformConfiguration.data_file);
						}
						else {
							traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.data_file));
						}
						
						// do validation
						response.setContentType("application/json");
						PrintWriter writer = response.getWriter();
	
						ValidatedModel myValidatedModel;
						property = "prefLabel";
						value = "node_1";
						String test = "";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test1.0 true";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						property = "prefLabel";
						value = "super_node";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test1.1 true";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						property = "prefLabel";
						value = "super node";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test1.2 true";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						property = "validatedAltLabel";
						value = "nnn1";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test2.0 true";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						value = "b1";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test2.1 false";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						property = "altLabel";
						value = "numéro 1";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test3.0 true";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));						value = "numéro 1";
						value = "super num";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test3.1 true";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						value = "super faux";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test3.2 false";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						property = "abbreviation";
						value = "4";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test4.0 true";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						value = "n4";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test4.1 true";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						value = "10";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test4.2 false";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						property = "prefUnit";
						value = "mega mètre";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test5 true";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						value = "cm";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test5.1 true";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						value = "faux";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test5.2 false";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						property = "broaderTransitive";
						value = "Node";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test6.0 true";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						value = "Node 4";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test6.1 true";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						value = "Node 44444";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test6.2 false";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						property = "related";
						value = "Node 4";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test7.0 true";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						value = "super node league";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test7.1 true";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						value = "whouhou";
						myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
								property, value);
						test = "test7.2 false";
						System.out.println(test + ":" +testValidated(myValidatedModel,validatedValue));
						writer.write("{\"result\":\"" + my_result + "\"}");
						writer.close();
						// clean memory
						traitModel.close();
					} catch (Exception ex) {
						ex.printStackTrace();
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
