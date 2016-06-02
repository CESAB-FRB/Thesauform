package thesauform.controller;

import com.hp.hpl.jena.rdf.model.NodeIterator;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Resource;

import thesauform.beans.Person;
import thesauform.beans.TraitConcept;
import thesauform.model.Format;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;
import thesauform.model.vocabularies.SkosVoc;

import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class arbreAnnotation
 */
@WebServlet("/annotationArbre")
public class ArbreAnnotation extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2819979634835124521L;

	public static final String VUE_SUCCESS = "/WEB-INF/scripts/arbreAnnotation.jsp";
	public static final String GET_PARAMETER = "trait";
	public static final String ERROR_PARAMETER = "parameter";
	public static final String ERROR_MESSAGE_PARAMETER = "parameter " + GET_PARAMETER + " empty";
	public static final String ERROR_CONCEPT = "concept";
	public static final String ERROR_MESSAGE_CONCEPT = "Cannot find trait in model";
	public static final String ERROR_SONS = "sons";
	public static final String EMPTY_SON = "No son";
	public static final String ERROR_REAL_NAME = "real_name";
	public static final String ERROR_URI = "uri";
	protected ThesauformConfiguration conf = new ThesauformConfiguration();

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// manage errors messages
		Map<String, String> errors = new HashMap<>();
		// trait model
		SkosTraitModel traitModel = null;
		// test if a session is initialized else it is visualization
		HttpSession session = request.getSession(false);
		if (request.getParameter(ThesauformConfiguration.GET_VIZ) != null
				&& request.getParameter(ThesauformConfiguration.GET_VIZ).equals("1")) {
			// set public file
			traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.public_data_file));
			request.setAttribute(ThesauformConfiguration.GET_VIZ, "1");
		} else {
			if (session != null) {
				if (session.getAttribute(ThesauformConfiguration.USR_SESSION) instanceof Person) {
					Person user = (Person) session.getAttribute(ThesauformConfiguration.USR_SESSION);
					boolean authentificationStatus = user.getAuthenticated();
					if (authentificationStatus) {
						// set protected file
						traitModel = new SkosTraitModel(
								getServletContext().getRealPath(ThesauformConfiguration.data_file));
					} else {
						// re-authenticate
						this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED)
								.forward(request, response);
					}
				}
			}
		}
		// treatment if user is logged
		if (traitModel != null) {
			String traitName = request.getParameter(GET_PARAMETER);
			// create object trait
			TraitConcept myTrait = new TraitConcept();
			// set name
			try {
				myTrait.setName(traitName);
			} catch (Exception e) {
				errors.put(ERROR_PARAMETER, ERROR_MESSAGE_PARAMETER + e.getMessage());

			}
			//// interrogate model
			// get trait concept from form parameter
			Resource concept = traitModel.getResource(Format.formatName(myTrait.getName()));
			if (concept != null) {
				// get trait real name
				try {
					myTrait.setRealName(traitModel.getLabelLiteralForm(traitModel.getPrefLabel(concept)));
				} catch (Exception e) {
					errors.put(ERROR_REAL_NAME, e.getMessage() + " for " + traitName);
				}
				// get trait URI
				try {
					myTrait.setUri(concept.toString());
				} catch (Exception e) {
					errors.put(ERROR_URI, e.getMessage() + " for " + traitName);
				}
				// get all sons concept in a list of TraitConcept
				try {
					NodeIterator SonsIt = traitModel.getSubclass(concept);
					if (SonsIt.hasNext()) {
						List<TraitConcept> mySonsList = new ArrayList<>();
						while (SonsIt.hasNext()) {
							TraitConcept myTraitTmp = new TraitConcept();
							Resource son = SonsIt.next().as(Resource.class);
							// set son name
							myTraitTmp.setRealName(traitModel.getLabelLiteralForm(traitModel.getPrefLabel(son)));
							// set son URI
							myTraitTmp.setUri(son.toString());
							// set son one artifact son to see if it is end of
							// tree
							if (son.hasProperty(SkosVoc.narrowerTransitive)) {
								myTraitTmp.setSonsList(Arrays.asList(myTraitTmp));
							}
							mySonsList.add(myTraitTmp);
						}
						myTrait.setSonsList(mySonsList);
					} else {
						throw new Exception(EMPTY_SON);
					}
				} catch (Exception e) {
					errors.put(ERROR_SONS, e.getMessage() + " for " + traitName);
				}
			} else {
				errors.put(ERROR_CONCEPT, ERROR_MESSAGE_CONCEPT + ": " + traitName);
			}
			request.setAttribute("my_errors", errors);
			request.setAttribute("my_trait", myTrait);
			traitModel.close();
			this.getServletContext().getRequestDispatcher(VUE_SUCCESS).forward(request, response);
		} else {
			// redirect to logging page
			request.setAttribute("my_errors", errors);
			this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request,
					response);
		}
	}
}
