package thesauform.controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import thesauform.model.Format;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;
import thesauform.model.vocabularies.TraitVocTemp;

/**
 * Servlet implementation class search
 */
@WebServlet("/searchApi")
public class SearchApi extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3615271529291595778L;

	public static final String GET_VIZ = "viz";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchApi() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// set visualization request
		request.setAttribute(GET_VIZ, "1");

		PrintWriter pw = response.getWriter();
		// trait model
		SkosTraitModel traitModel = null;
		// get param in order to know what to print
		String param = request.getParameter("trait");
		String paramAllTraits = request.getParameter("allTraits");

		// set public file
		traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.public_data_file));
		if (param != null) {
			Resource trait = traitModel.getResource(Format.formatName(param));
			// Query get Trait Info
			// set the prefixes of the query
			String prolog2 = "PREFIX rdf: <" + RDF.getURI() + ">";
			String prolog3 = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>";
			String prolog4 = "PREFIX rdfs: <" + RDFS.getURI() + ">";
			String prolog5 = "PREFIX skosxl: <http://www.w3.org/2008/05/skos-xl#>";
			String prolog6 = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>";
			String prolog7 = "PREFIX dc: <http://purl.org/dc/elements/1.1/>";
			String prolog8 = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>";
			String prolog9 = "PREFIX owl: <http://www.w3.org/2002/07/owl#>";
			String prolog10 = "PREFIX dct: <http://purl.org/dc/terms/>";
			String prolog1 = "PREFIX trait: <" + TraitVocTemp.getUri() + ">";
			// Query string.
			String queryString = prolog1 + "\n" + prolog2 + "\n" + prolog3 + "\n" + prolog4 + "\n" + prolog5 + "\n"
					+ prolog6 + "\n" + prolog7 + "\n" + prolog8 + "\n" + prolog9 + "\n" + prolog10 + "\n"
					+ "CONSTRUCT { " + "?s ?p ?o . " + "?s ?p ?l . " + "}" + "WHERE {" + " { ?s rdfs:label '"
					+ trait.getLocalName() + "' . " + " ?s ?p ?o . " + "filter(!isBlank(?o)) " + "} UNION {"
					+ "?s rdfs:label '" + trait.getLocalName() + "' . "
					+ "?s ?p ?altLabelNode . ?altLabelNode skosxl:literalForm ?l . "
					+ "OPTIONAL { ?s ?p ?altLabelNode . ?altLabelNode rdf:value ?l . }" + "}}";
			Query query = QueryFactory.create(queryString);
			QueryExecution qexec = QueryExecutionFactory.create(query, trait.getModel());
			try {
				Model results = qexec.execConstruct();
				results.write(pw, "N3");
			} finally {
				// QueryExecution objects should be closed to free any system
				// resources
				qexec.close();
			}
		}
		if (paramAllTraits != null) {
			String prolog2 = "PREFIX rdf: <" + RDF.getURI() + ">";
			String prolog3 = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>";
			String prolog4 = "PREFIX rdfs: <" + RDFS.getURI() + ">";
			String prolog5 = "PREFIX skosxl: <http://www.w3.org/2008/05/skos-xl#>";
			String prolog6 = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>";
			String prolog7 = "PREFIX dc: <http://purl.org/dc/elements/1.1/>";
			String prolog8 = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>";
			String prolog9 = "PREFIX owl: <http://www.w3.org/2002/07/owl#>";
			String prolog10 = "PREFIX dct: <http://purl.org/dc/terms/>";
			String prolog1 = "PREFIX trait: <" + TraitVocTemp.getUri() + ">";
			// Query string.
			String queryString = prolog1 + "\n" + prolog2 + "\n" + prolog3 + "\n" + prolog4 + "\n" + prolog5 + "\n"
					+ prolog6 + "\n" + prolog7 + "\n" + prolog8 + "\n" + prolog9 + "\n" + prolog10 + "\n"
					+ "CONSTRUCT { " + "?thesaurus rdfs:label ?o . " + " ?s skos:inScheme ?thesaurus . " + "}"
					+ "WHERE {" + " ?thesaurus rdf:type skos:ConceptScheme . " + " ?thesaurus rdfs:label ?o . "
					+ " ?s rdf:type skos:Concept . " + "}";
			Query query = QueryFactory.create(queryString);
			QueryExecution qexec = QueryExecutionFactory.create(query, traitModel.getModel());
			try {
				Model results = qexec.execConstruct();
				results.write(pw, "N3");
			} finally {
				// QueryExecution objects should be closed to free any system
				// resources
				qexec.close();
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}