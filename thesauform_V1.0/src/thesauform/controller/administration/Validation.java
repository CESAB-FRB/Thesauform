package thesauform.controller.administration;

import java.awt.List;
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
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openjena.atlas.iterator.Iter;

/**
 * Servlet implementation class servletVote
 */
@WebServlet("/administration/validation")
public class Validation extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2452193494822424082L;

	private final String EMPTY_TRAIT_NAME_MESSAGE = "Empty parameter trait name";
	private final String EMPTY_PROPERTY_MESSAGE = "Empty parameter property";
	private final String EMPTY_VALUE_MESSAGE = "Empty parameter value";
	private final String EMPTY_VAL_VALUE_MESSAGE = "Empty parameter validation type";
	private final String WRONG_VAL_VALUE_MESSAGE = "Wrong parameter validation type";

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
						ValidatedModel myValidatedModel;
						if(ThesauformConfiguration.database) {
							myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
									property, value);
						}
						else {
							myValidatedModel = new ValidatedModel(traitModel,
									getServletContext().getRealPath(ThesauformConfiguration.data_file),
									getServletContext().getRealPath(ThesauformConfiguration.data_file_tmp), traitName,
									property, value);
						}
						if(validatedValue==1)
						{
							my_result = myValidatedModel.addValidated();
						}
						else {
							my_result = myValidatedModel.addInvalidated();
						}
						response.setContentType("application/json");
						PrintWriter writer = response.getWriter();
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

	synchronized public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
						Map<String, String[]> param = request.getParameterMap();
						String paramName = "";
						String property = "";
						String[] paramValueArray;
						String paramValue = "";
						String traitName = java.net.URLDecoder.decode(param.get("concept")[0], "UTF-8");
						if (traitName == null || traitName.isEmpty()) {
							throw new Exception(EMPTY_TRAIT_NAME_MESSAGE);
						}
						if(ThesauformConfiguration.database) {
							traitModel = new SkosTraitModel(ThesauformConfiguration.data_file);
						}
						else {
							traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.data_file));
						}	
						//do validation
						ValidatedModel myValidatedModel;
				/*		p = "prefLabel";
						p = "validatedAltLabel";
						p = "altLabel";
						p = "abbreviation";
						p = "prefUnit";
						p = "reference";
						p = "definition";
						p = "update";
						p = "delete";
						p = "comment";
						p = "insert";
						p = "broaderTransitive";
						p = "related";
*/
						//name
						paramName = "name";
						paramValueArray = param.get(paramName);
						if(paramValueArray != null) {
							paramValue = param.get(paramName)[0];
							if(ThesauformConfiguration.database) {
								myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
										property, paramValue);
							}
							else {
								myValidatedModel = new ValidatedModel(traitModel,
										getServletContext().getRealPath(ThesauformConfiguration.data_file),
										getServletContext().getRealPath(ThesauformConfiguration.data_file_tmp), traitName,
										property, paramValue);
							}
							my_result = myValidatedModel.addValidated();
							System.out.println(my_result);
						}
						//definition
						paramName = "definition";
						System.out.print(paramName + ": ");
						paramValueArray = param.get(paramName);
						if(paramValueArray != null) {
							paramValue = param.get(paramName)[0];
							if(ThesauformConfiguration.database) {
								myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
										property, paramValue);
							}
							else {
								myValidatedModel = new ValidatedModel(traitModel,
										getServletContext().getRealPath(ThesauformConfiguration.data_file),
										getServletContext().getRealPath(ThesauformConfiguration.data_file_tmp), traitName,
										property, paramValue);
							}
							my_result = myValidatedModel.addValidated();
							System.out.println(my_result);
						}
						//unit
						paramName = "unit";
						System.out.print(paramName + ": ");
						paramValueArray = param.get(paramName);
						if(paramValueArray != null) {
							paramValue = param.get(paramName)[0];
							if(ThesauformConfiguration.database) {
								myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
										property, paramValue);
							}
							else {
								myValidatedModel = new ValidatedModel(traitModel,
										getServletContext().getRealPath(ThesauformConfiguration.data_file),
										getServletContext().getRealPath(ThesauformConfiguration.data_file_tmp), traitName,
										property, paramValue);
							}
							my_result = myValidatedModel.addValidated();
							System.out.println(my_result);
						}
						//abbreviation
						paramName = "abbreviation";
						System.out.print(paramName + ": ");
						paramValueArray = param.get(paramName);
						if(paramValueArray != null) {
							paramValue = param.get(paramName)[0];
							if(ThesauformConfiguration.database) {
								myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
										property, paramValue);
							}
							else {
								myValidatedModel = new ValidatedModel(traitModel,
										getServletContext().getRealPath(ThesauformConfiguration.data_file),
										getServletContext().getRealPath(ThesauformConfiguration.data_file_tmp), traitName,
										property, paramValue);
							}
							my_result = myValidatedModel.addValidated();
							System.out.println(my_result);
						}
						//synonym
						paramName = "synonym";
						System.out.print(paramName + ": ");
						paramValueArray = param.get(paramName);
						if(paramValueArray != null) {
							paramValue = param.get(paramName)[0];
							if(ThesauformConfiguration.database) {
								myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
										property, paramValue);
							}
							else {
								myValidatedModel = new ValidatedModel(traitModel,
										getServletContext().getRealPath(ThesauformConfiguration.data_file),
										getServletContext().getRealPath(ThesauformConfiguration.data_file_tmp), traitName,
										property, paramValue);
							}
							my_result = myValidatedModel.addValidated();
							System.out.println(my_result);
						}
						//related
						paramName = "related";
						System.out.print(paramName + ": ");
						paramValueArray = param.get(paramName);
						if(paramValueArray != null) {
							paramValue = param.get(paramName)[0];
							if(ThesauformConfiguration.database) {
								myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
										property, paramValue);
							}
							else {
								myValidatedModel = new ValidatedModel(traitModel,
										getServletContext().getRealPath(ThesauformConfiguration.data_file),
										getServletContext().getRealPath(ThesauformConfiguration.data_file_tmp), traitName,
										property, paramValue);
							}
							my_result = myValidatedModel.addValidated();
							System.out.println(my_result);
						}
						//category
						paramName = "category";
						System.out.print(paramName + ": ");
						paramValueArray = param.get(paramName);
						if(paramValueArray != null) {
							paramValue = param.get(paramName)[0];
							if(ThesauformConfiguration.database) {
								myValidatedModel = new ValidatedModel(traitModel, ThesauformConfiguration.data_file, ThesauformConfiguration.data_file_tmp, traitName,
										property, paramValue);
							}
							else {
								myValidatedModel = new ValidatedModel(traitModel,
										getServletContext().getRealPath(ThesauformConfiguration.data_file),
										getServletContext().getRealPath(ThesauformConfiguration.data_file_tmp), traitName,
										property, paramValue);
							}
							my_result = myValidatedModel.addValidated();
							System.out.println(my_result);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
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
