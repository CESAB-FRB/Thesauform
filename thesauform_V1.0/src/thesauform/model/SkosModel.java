package thesauform.model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import thesauform.model.vocabularies.ChangeVoc;
import thesauform.model.vocabularies.RefVoc;
import thesauform.model.vocabularies.SkosVoc;
import thesauform.model.vocabularies.SkosXLVoc;
import thesauform.model.vocabularies.StatusVoc;
import thesauform.model.vocabularies.TraitVocTemp;
import thesauform.model.vocabularies.UnitVoc;

/*
 * TODO Vote should be linked to annotation directly
 */
public class SkosModel implements AnnotationModel {

	protected Model m = ModelFactory.createDefaultModel();
	private static String uri = null;

	public SkosModel() {
		m.setNsPrefix("skos", ThesauformConfiguration.skos);
		m.setNsPrefix("skosxl", ThesauformConfiguration.skosXL);
		m.setNsPrefix("dc", DC.getURI());
		m.setNsPrefix("owl", OWL.getURI());
		m.setNsPrefix("rdfs", RDFS.getURI());
		m.setNsPrefix("rdf", RDF.getURI());
		m.setNsPrefix("dct", DCTerms.getURI());
		m.setNsPrefix("foaf", FOAF.NS);
		m.setNsPrefix("xsd", XSD.getURI());
		m.setNsPrefix("vs", ThesauformConfiguration.vs);
	}

	public SkosModel(String file) {
		// TODO manage exeception
		// use the file manager to read an RDF document into the model
		FileManager.get().readModel(m, file);
	}

	public void print() {
		m.write(System.err, "RDF/XML");
	}

	public void save(String file, String filebis) {
		Boolean test = false;
		try {
			FileOutputStream ost = new FileOutputStream(file);
			m.write(ost, "RDF/XML");
			test = true;
		} catch (FileNotFoundException e) {
			ThesauformConfiguration.thesauform_logger
					.error(String.class.getName() + ": file not found " + e.getMessage());
		}
		if (test) {
			try {
				FileOutputStream ost = new FileOutputStream(filebis);
				m.write(ost, "RDF/XML");

			} catch (FileNotFoundException e) {
				ThesauformConfiguration.thesauform_logger
						.error(String.class.getName() + ": file not found " + e.getMessage());
			}
		}
	}

	public void save(String file) {
		try {
			FileOutputStream ost = new FileOutputStream(file);
			m.write(ost, "RDF/XML");
		} catch (FileNotFoundException e) {
			ThesauformConfiguration.thesauform_logger
					.error(String.class.getName() + ": file not found " + e.getMessage());
		}
	}

	public void saveN3(String file, String filebis) {
		Boolean test = false;
		try {
			FileOutputStream ost = new FileOutputStream(file);
			m.write(ost, "N3");
			test = true;
		} catch (FileNotFoundException e) {
			ThesauformConfiguration.thesauform_logger
					.error(String.class.getName() + ": file not found " + e.getMessage());
		}
		if (test) {
			try {
				FileOutputStream ost = new FileOutputStream(filebis);
				m.write(ost, "N3");
			} catch (FileNotFoundException e) {
				ThesauformConfiguration.thesauform_logger
						.error(String.class.getName() + ": file not found " + e.getMessage());
			}
		}
	}

	public void close() {
		m.close();
	}

	public Model getModel() {
		return m;
	}

	public void setUri(String ns) {
		uri = ns;
	}

	public Resource createResource(String cl) {
		Resource classe = m.createResource(uri + "#" + Format.formatName(cl));
		return classe;
	}

	public Resource createResourceExt(String URI, String cl) {
		Resource classe = m.createResource(URI + Format.formatName(cl));
		return classe;
	}

	public Literal createLiteral(String cl) {
		Literal classe = m.createTypedLiteral(cl);
		return classe;
	}

	public Resource createResource() {
		Resource classe = m.createResource();
		return classe;
	}

	public Resource addResource(Resource r) {
		m.add(r, RDF.type, SkosVoc.Concept);
		return r;
	}

	public Property createProperty(String prop) {
		Property property = m.createProperty(uri + "#" + prop);
		return property;
	}

	public Resource getResource(String cl) {
		Resource classe = m.getResource(uri + "#" + cl);
		return classe;
	}

	public Resource getResourceV(String cl) {
		Resource classe = m.getResource(cl);
		return classe;
	}

	public Property getProperty(String prop) {
		Property property = m.getProperty(uri + "#" + prop);
		return property;
	}

	public void setResource(Resource cl1, Property prop, Resource cl2) {
		m.add(cl1, prop, cl2);
	}

	public void setResource(Resource cl1, Property prop, String cl2) {
		m.add(cl1, prop, m.createTypedLiteral(cl2));
	}

	public void setResourcePer(Resource cl1, Property prop, String cl2) {
		m.add(cl1, prop, cl2);
	}

	public void setResource(Resource cl1, Property prop, Calendar date) {
		m.add(cl1, prop, m.createTypedLiteral(date));
	}

	// dangerous to use
	public void setResource(String cl1, String prop, String cl2) {
		m.add(this.getResource(cl1), this.getProperty(prop), this.getResource(cl2));
	}

	public Map<String, Map<String, String>> getAllPersons() {
		Map<String, Map<String, String>> returnMap = new TreeMap<>();
		String prolog = "PREFIX user: <" + ThesauformConfiguration.person_uri + ">";
		String prolog2 = "PREFIX rdf: <" + RDF.getURI() + ">";
		String prolog3 = "PREFIX foaf: <" + FOAF.getURI() + ">";
		// Query string.
		String queryString = prolog + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
				+ ThesauformConfiguration.NL + "SELECT ?name ?mail ?password ?right WHERE {"
				+ "?person rdf:type foaf:Person ." + "?person foaf:name ?name ." + "?person user:password ?password ."
				+ "?person user:right ?right ." + "?person foaf:mbox ?mail } order by ?name ";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			if (rs.hasNext()) {
				for (; rs.hasNext();) {
					QuerySolution rb = rs.nextSolution();
					Map<String, String> returnMapTmp = new TreeMap<>();
					String name = rb.get("?name").asNode().getLiteralLexicalForm();
					String mail = rb.get("?mail").asNode().getLiteralLexicalForm();
					String password = rb.get("?password").asNode().getLiteralLexicalForm();
					String right = rb.get("?right").asNode().getLiteralLexicalForm();
					returnMapTmp.put("name", name);
					returnMapTmp.put("mail", mail);
					returnMapTmp.put("password", password);
					returnMapTmp.put("right", right);
					returnMap.put(name, returnMapTmp);
				}
			}

		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return returnMap;
	}

	/**
	 * Get all concept in the database
	 * @return List of name
	 */
	public List<String> getAllConcept() {
		List<String> returnList = new ArrayList<String>();
		String prolog1 = "PREFIX trait: <" + ThesauformConfiguration.term_uri + "#>";
		String prolog2 = "PREFIX rdf: <" + ThesauformConfiguration.rdf + ">";
		String prolog3 = "PREFIX skos: <" + ThesauformConfiguration.skos + ">";
		String prolog4 = "PREFIX rdfs: <" + ThesauformConfiguration.rdfs + ">";
		String prolog5 = "PREFIX change: <" + ThesauformConfiguration.term_uri + ThesauformConfiguration.uriChange + ">";
		//Query string.
		String queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
				+ ThesauformConfiguration.NL + prolog4 + ThesauformConfiguration.NL + prolog5
				+ ThesauformConfiguration.NL + "SELECT DISTINCT ?name WHERE {" + "?trait rdf:type skos:Concept ."
				+ "?trait rdfs:label ?name " + "}";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			for (; rs.hasNext();) {
				QuerySolution rb = rs.nextSolution();
				returnList.add(rb.get("?name").asNode().getLiteralLexicalForm());
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return returnList;
	}

	/**
	 * Get all concept with at least one definition
	 * @return List of concept name
	 */
	public List<String> getAllConceptWithDef() {
		List<String> returnList = new ArrayList<String>();
		String prolog1 = "PREFIX trait: <" + ThesauformConfiguration.term_uri + "#>";
		String prolog2 = "PREFIX rdf: <" + ThesauformConfiguration.rdf + ">";
		String prolog3 = "PREFIX skos: <" + ThesauformConfiguration.skos + ">";
		String prolog4 = "PREFIX rdfs: <" + ThesauformConfiguration.rdfs + ">";
		String prolog5 = "PREFIX change: <" + ThesauformConfiguration.term_uri + ThesauformConfiguration.uriChange + ">";
		// Query string.
		String queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
				+ ThesauformConfiguration.NL + prolog4 + ThesauformConfiguration.NL + prolog5
				+ ThesauformConfiguration.NL + "SELECT DISTINCT ?name WHERE {" + "?trait rdf:type skos:Concept ."
				+ "?trait rdfs:label ?name . ?trait skos:definition ?def " + "}";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			for (; rs.hasNext();) {
				QuerySolution rb = rs.nextSolution();
				returnList.add(rb.get("?name").asNode().getLiteralLexicalForm());
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return returnList;
	}
	
	public Resource getPerson(String name, String email) {
		// TODO Auto-generated method stub
		Resource person = null;
		String prolog2 = "PREFIX rdf: <" + RDF.getURI() + ">";
		String prolog3 = "PREFIX foaf: <" + FOAF.getURI() + ">";
		// Query string.
		String queryString = prolog2 + ThesauformConfiguration.NL + prolog3 + ThesauformConfiguration.NL
				+ "SELECT ?person ?name ?mail WHERE {" + "?person rdf:type foaf:Person ."
				+ "?person foaf:name ?name . FILTER (?name=\"" + name + "\")."
				+ "?person foaf:mbox ?mail . FILTER (?mail=\"" + email + "\")}";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			if (!rs.hasNext()) {
				person = m.createResource();
				m.add(person, RDF.type, FOAF.Person);
				m.add(person, FOAF.name, name);
				m.add(person, FOAF.mbox, email);
			} else {
				for (; rs.hasNext();) {
					QuerySolution rb = rs.nextSolution();
					RDFNode y = rb.get("person");
					person = y.as(Resource.class);
				}
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return person;
	}

	public Resource getPerson(String name) {
		// TODO Auto-generated method stub
		Resource person = null;
		String prolog2 = "PREFIX rdf: <" + RDF.getURI() + ">";
		String prolog3 = "PREFIX foaf: <" + FOAF.getURI() + ">";
		// Query string.
		String queryString = prolog2 + ThesauformConfiguration.NL + prolog3 + ThesauformConfiguration.NL
				+ "SELECT ?person ?name ?mail WHERE {" + "?person rdf:type foaf:Person ."
				+ "?person foaf:name ?name . FILTER regex(?name,\"" + name + "\")." + "?person foaf:mbox ?mail }";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			if (!rs.hasNext()) {
			} else {
				for (; rs.hasNext();) {
					QuerySolution rb = rs.nextSolution();
					RDFNode y = rb.get("person");
					person = y.as(Resource.class);
				}
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return person;
	}

	public boolean getPersonB(String name, String email) {
		// TODO Auto-generated method stub
		boolean person = false;
		// test if it is not the generic user
		if (!name.equals("public")) {
			String prolog1 = "PREFIX ns: <" + ThesauformConfiguration.term_uri + ">";
			String prolog2 = "PREFIX rdf: <" + RDF.getURI() + ">";
			String prolog3 = "PREFIX foaf: <" + FOAF.getURI() + ">";
			// Query string.
			String queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
					+ ThesauformConfiguration.NL + "SELECT ?person ?name ?mail WHERE {"
					+ "?person rdf:type foaf:Person ." + "?person foaf:name ?name . FILTER regex(?name,\"" + name
					+ "\")." + "?person foaf:mbox ?mail . FILTER regex(?mail,\"" + email + "\")}";
			Query query = QueryFactory.create(queryString);
			QueryExecution qexec = QueryExecutionFactory.create(query, m);
			try {
				ResultSet rs = qexec.execSelect();
				if (!rs.hasNext()) {
					person = false;
				} else {
					person = true;
				}
			} finally {
				// QueryExecution objects should be closed to free any system
				// resources
				qexec.close();
			}
		}
		return person;
	}
	
	/**
	 * Get all 
	 * @param typeCollection Should be in delete, insert, update
	 * @return
	 * @throws Exception 
	 */
	public List<String> getInCollection(String typeCollection) throws Exception {
		List<String> inCollectionList = new ArrayList<String>();
		String prolog1 = "PREFIX trait: <" + ThesauformConfiguration.term_uri + "#>";
		String prolog2 = "PREFIX rdf: <" + ThesauformConfiguration.rdf + ">";
		String prolog3 = "PREFIX skos: <" + ThesauformConfiguration.skos + ">";
		String prolog4 = "PREFIX rdfs: <" + ThesauformConfiguration.rdfs + ">";
		String prolog5 = "PREFIX change: <" + ThesauformConfiguration.term_uri + ThesauformConfiguration.uriChange + ">";
		String queryString = "";
		switch (typeCollection) {
			case "insert":
				queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
					+ ThesauformConfiguration.NL + prolog4 + ThesauformConfiguration.NL + prolog5 + ThesauformConfiguration.NL 
					+ "SELECT DISTINCT ?name WHERE {" + "?trait rdf:type skos:Concept ."
					+ "?trait rdfs:label ?name ." + "?trait change:insert ?ins }";
				break;
			case "update":
				queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
					+ ThesauformConfiguration.NL + prolog4 + ThesauformConfiguration.NL + prolog5 + ThesauformConfiguration.NL 
					+ "SELECT DISTINCT ?name WHERE {" + "?trait rdf:type skos:Concept ."
					+ "?trait rdfs:label ?name ." + "?trait change:update ?upt }";
				break;
			case "delete":
				queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
					+ ThesauformConfiguration.NL + prolog4 + ThesauformConfiguration.NL + prolog5 + ThesauformConfiguration.NL 
					+ "SELECT DISTINCT ?name WHERE {" + "?trait rdf:type skos:Concept ."
					+ "?trait rdfs:label ?name ." + "?trait change:delete ?del }";
				break;
			default:
				throw new Exception("Collection type "+ typeCollection +"not managed.");
		}
		// Query string.
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			for (; rs.hasNext();) {
				QuerySolution rb = rs.nextSolution();
				inCollectionList.add(rb.get("?name").asNode().getLiteralLexicalForm());
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return inCollectionList;
	}
	
	/**
	 * Return list in collection
	 * @return
	 */
	public Map<String, List<String>> getAllTraitWithAnn() {
		Map<String, List<String>> map = new TreeMap<>();
		NodeIterator insert = this.getAllMember(m.getResource(ThesauformConfiguration.term_uri + "#Insert"));
		NodeIterator update = this.getAllMember(m.getResource(ThesauformConfiguration.term_uri + "#Update"));
		NodeIterator delete = this.getAllMember(m.getResource(ThesauformConfiguration.term_uri + "#Delete"));
		List<String> valInsert = new ArrayList<String>();
		while (insert.hasNext()) {
			String name = insert.next().as(Resource.class).getLocalName();
			valInsert.add(name);
		}
		map.put("Insert", valInsert);
		List<String> valUpdate = new ArrayList<String>();
		while (update.hasNext()) {
			String name = update.next().as(Resource.class).getLocalName();
			valUpdate.add(name);
		}
		map.put("Update", valUpdate);
		List<String> valDelete = new ArrayList<String>();
		while (delete.hasNext()) {
			String name = delete.next().as(Resource.class).getLocalName();
			valDelete.add(name);
		}
		map.put("Delete", valDelete);
		return map;
	}
	
	public int countVote(Resource c, Property p, String person, String value) {
		int cpt = 0;
		//replace value special character
		value = value.replaceAll("[\r\n]", "\\r");
		String prolog1 = "PREFIX skos: <" + ThesauformConfiguration.skos + ">";
		String prolog2 = "PREFIX change: <" + ThesauformConfiguration.term_uri + ThesauformConfiguration.uriChange
				+ ">";
		String prolog3 = "PREFIX trait: <" + ThesauformConfiguration.term_uri + "#>";
		String prolog4 = "PREFIX dc: <" + ThesauformConfiguration.dc + ">";
		String prolog5 = "PREFIX foaf: <" + ThesauformConfiguration.foaf + ">";
		// Query string
		String queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
				+ ThesauformConfiguration.NL + prolog4 + ThesauformConfiguration.NL + prolog5
				+ ThesauformConfiguration.NL + "SELECT ?vote_val WHERE { " + "<" + c + "> change:vote ?vote . "
				+ "?vote change:hasProperty <" + p + "> . " + "?vote change:hasValue ?val . "
				+ "?vote change:hasVote ?vote_val . "
				+ "?vote change:contribution ?person . " + "?person dc:creator  ?creator . "
				+ "?creator foaf:name ?cname ." + "FILTER (?val=\"" + value
				+ "\" && NOT EXISTS { ?vote trait:reference ?ref } "
				+ "&& (REPLACE(LCASE(?cname), \" \", \"_\", \"i\")=\"" + person.toLowerCase().replace(" ", "_")
				+ "\"))." + " }";
		if (p == SkosVoc.definition && value.contains("__")) {
			String prolog6 = "PREFIX rdf: <" + ThesauformConfiguration.rdf + ">";
			String[] refDef = value.split("__",-1);
			// change queryString
			queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
					+ ThesauformConfiguration.NL + prolog4 + ThesauformConfiguration.NL + prolog5
					+ ThesauformConfiguration.NL + prolog6 + ThesauformConfiguration.NL + "SELECT ?vote_val WHERE { " + "<"
					+ c + "> change:vote ?vote . " + "?vote change:hasProperty <" + p + "> . "
					+ "?vote change:hasVote ?vote_val . " + "?vote change:hasValue ?val . " 
					+ "?vote trait:reference ?ref . " + "?ref rdf:value ?refval . "
					+ "?vote change:contribution ?person . " + "?person dc:creator  ?creator . "
					+ "?creator foaf:name ?cname ." + "FILTER (?val=\"" + refDef[0] + "\" && ?refval=\""
					+ refDef[1] + "\" && (REPLACE(LCASE(?cname), \" \", \"_\", \"i\")=\""
					+ person.toLowerCase().replace(" ", "_") + "\"))." + " }";
		}
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			for (; rs.hasNext();) {
				QuerySolution rb = rs.nextSolution();
				RDFNode y = rb.get("vote_val");
				cpt = Integer.parseInt(y.asNode().getLiteralLexicalForm());
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return cpt;
	}

	/**
	 * Count vote number for a concept
	 * @param c
	 * @return
	 */
	public int countVote(Resource c) {
		int cpt = 0;
		String prolog1 = "PREFIX skos: <" + ThesauformConfiguration.skos + ">";
		String prolog2 = "PREFIX change: <" + ThesauformConfiguration.term_uri + ThesauformConfiguration.uriChange
				+ ">";
		String prolog3 = "PREFIX trait: <" + ThesauformConfiguration.term_uri + "#>";
		// Query string
		String queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
				+ ThesauformConfiguration.NL + "SELECT  (COUNT(DISTINCT ?vote) AS ?count)  WHERE { " + "<" + c
				+ "> change:vote ?vote . " + "?vote change:hasValue ?val }";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			for (; rs.hasNext();) {
				QuerySolution rb = rs.nextSolution();
				RDFNode y = rb.get("count");
				cpt = Integer.parseInt(y.asNode().getLiteralLexicalForm());
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return cpt;
	}

	/**
	 * Count vote number for a concept for a person
	 * @param c
	 * @param person,
	 * @return
	 */
	public int countVotePerson(Resource c, String person) {
		int cpt = 0;
		String prolog1 = "PREFIX skos: <" + ThesauformConfiguration.skos + ">";
		String prolog2 = "PREFIX change: <" + ThesauformConfiguration.term_uri + ThesauformConfiguration.uriChange
				+ ">";
		String prolog3 = "PREFIX trait: <" + ThesauformConfiguration.term_uri + "#>";
		String prolog4 = "PREFIX dc: <" + ThesauformConfiguration.dc + ">";
		String prolog5 = "PREFIX foaf: <" + ThesauformConfiguration.foaf + ">";
		// Query string
		String queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
				+ ThesauformConfiguration.NL + prolog4 + ThesauformConfiguration.NL + prolog5 + ThesauformConfiguration.NL
				+ "SELECT  (COUNT(DISTINCT ?vote) AS ?count)  WHERE { " + "<" + c
				+ "> change:vote ?vote . "
				+ "?vote change:contribution ?person . " + "?person dc:creator  ?creator . "
				+ "?creator foaf:name ?cname ." + "FILTER (REPLACE(LCASE(?cname), \" \", \"_\", \"i\")=\"" + person.toLowerCase().replace(" ", "_")
				+ "\")." + " }";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			for (; rs.hasNext();) {
				QuerySolution rb = rs.nextSolution();
				RDFNode y = rb.get("count");
				cpt = Integer.parseInt(y.asNode().getLiteralLexicalForm());
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return cpt;
	}

	/**
	 * Count vote number for a concept for a property (as definition)
	 * @param c
	 * @param p,
	 * @return
	 */
	public int countUserVoteProperty(Resource c, Property p) {
		int cpt = 0;
		String prolog1 = "PREFIX skos: <" + ThesauformConfiguration.skos + ">";
		String prolog2 = "PREFIX change: <" + ThesauformConfiguration.term_uri + ThesauformConfiguration.uriChange
				+ ">";
		String prolog3 = "PREFIX trait: <" + ThesauformConfiguration.term_uri + "#>";
		String prolog4 = "PREFIX dc: <" + ThesauformConfiguration.dc + ">";
		String prolog5 = "PREFIX foaf: <" + ThesauformConfiguration.foaf + ">";
		// Query string
		String queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
				+ ThesauformConfiguration.NL + prolog4 + ThesauformConfiguration.NL + prolog5 + ThesauformConfiguration.NL
				+ "SELECT  (COUNT(DISTINCT ?creator) AS ?count)  WHERE { " + "<" + c
				+ "> change:vote ?vote . "
				+ "?vote change:hasProperty <" + p + "> . "
				+ "?vote change:contribution ?person . " + "?person dc:creator  ?creator . "
				+ "}";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			for (; rs.hasNext();) {
				QuerySolution rb = rs.nextSolution();
				RDFNode y = rb.get("count");
				cpt = Integer.parseInt(y.asNode().getLiteralLexicalForm());
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return cpt;
	}
	
	/**
	 * Count number of concept with at least one vote for a person
	 * @param c
	 * @param person
	 * @return
	 */
	public int countConceptVotedPerson(String person) {
		int cpt = 0;
		String prolog1 = "PREFIX skos: <" + ThesauformConfiguration.skos + ">";
		String prolog2 = "PREFIX change: <" + ThesauformConfiguration.term_uri + ThesauformConfiguration.uriChange
				+ ">";
		String prolog3 = "PREFIX trait: <" + ThesauformConfiguration.term_uri + "#>";
		String prolog4 = "PREFIX dc: <" + ThesauformConfiguration.dc + ">";
		String prolog5 = "PREFIX foaf: <" + ThesauformConfiguration.foaf + ">";
		// Query string
		String queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
				+ ThesauformConfiguration.NL + prolog4 + ThesauformConfiguration.NL + prolog5 + ThesauformConfiguration.NL
				+ "SELECT  (COUNT(DISTINCT ?concept) AS ?count)  WHERE { ?concept change:vote ?vote . "
				+ "?vote change:contribution ?person . " + "?person dc:creator  ?creator . "
				+ "?creator foaf:name ?cname ." + "FILTER (REPLACE(LCASE(?cname), \" \", \"_\", \"i\")=\"" + person.toLowerCase().replace(" ", "_")
				+ "\")." + " }";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			for (; rs.hasNext();) {
				QuerySolution rb = rs.nextSolution();
				RDFNode y = rb.get("count");
				cpt = Integer.parseInt(y.asNode().getLiteralLexicalForm());
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return cpt;
	}
	
	public int countVote(String traitName, String property, String value) throws Exception {
		int cpt = 0;
		Resource c = this.getResource(Format.formatName(traitName));
		Property p = null;
		if (property.equalsIgnoreCase("prefLabel")) {
			p = SkosXLVoc.prefLabel;
		} else if (property.equalsIgnoreCase("altLabel")) {
			p = SkosXLVoc.altLabel;
		} else if (property.equalsIgnoreCase("abbreviation")) {
			p = TraitVocTemp.abbreviation;
		} else if (property.equalsIgnoreCase("prefUnit")) {
			p = TraitVocTemp.prefUnit;
		} else if (property.equalsIgnoreCase("reference")) {
			p = TraitVocTemp.reference;
		} else if (property.equalsIgnoreCase("definition")) {
			p = SkosVoc.definition;
		} else if (property.equalsIgnoreCase("update")) {
			p = ChangeVoc.update;
		} else if (property.equalsIgnoreCase("delete")) {
			p = ChangeVoc.delete;
		} else if (property.equalsIgnoreCase("comment")) {
			p = ChangeVoc.comment;
		} else if (property.equalsIgnoreCase("insert")) {
			p = ChangeVoc.insert;
		} else if (property.equalsIgnoreCase("broaderTransitive")) {
			p = SkosVoc.broaderTransitive;
		} else if (property.equalsIgnoreCase("related")) {
			p = SkosVoc.related;
		} else {
			throw new Exception("Vocabularie " + property + " not managed");
		}
		String prolog1 = "PREFIX skos: <" + ThesauformConfiguration.skos + ">";
		String prolog2 = "PREFIX change: <" + ThesauformConfiguration.term_uri + ThesauformConfiguration.uriChange
				+ ">";
		String prolog3 = "PREFIX trait: <" + ThesauformConfiguration.term_uri + "#>";
		// Query string.
		String queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
				+ ThesauformConfiguration.NL + "SELECT  (COUNT(DISTINCT ?vote) AS ?count)  WHERE { " + "<" + c
				+ "> change:vote ?vote . " + "?vote change:hasProperty <" + p + "> . "
				+ "?vote change:hasValue ?val . FILTER (NOT EXISTS { ?vote trait:reference ?ref } && ?val=\"" + value + "\" ) }";
		//@Patch1: count differently for definition with reference
		if (p == SkosVoc.definition && value.contains("__")) {
			String prolog4 = "PREFIX rdf: <" + ThesauformConfiguration.rdf + ">";
			String[] refDef = value.split("__");
			queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3 + ThesauformConfiguration.NL + prolog4
				+ ThesauformConfiguration.NL + "SELECT  (COUNT(DISTINCT ?vote) AS ?count)  WHERE { " + "<" + c
				+ "> change:vote ?vote . " + "?vote change:hasProperty <" + p + "> . "
				+ "?vote change:hasValue ?val . " + "?vote trait:reference ?ref . " + "?ref rdf:value ?refval . "
				+ "FILTER (?val=\"" + refDef[0] + "\" && ?refval=\"" + refDef[1] + "\" ) }";
		}
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			for (; rs.hasNext();) {
				QuerySolution rb = rs.nextSolution();
				RDFNode y = rb.get("count");
				cpt = Integer.parseInt(y.asNode().getLiteralLexicalForm());
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return cpt;
	}
	
	//get all vote link to a concept annotation
	public List<String> getVote(Resource c, Property p, String value) {
		List<String> voteList =  new ArrayList<String>();
		String prolog1 = "PREFIX skos: <" + ThesauformConfiguration.skos + ">";
		String prolog2 = "PREFIX change: <" + ThesauformConfiguration.term_uri + ThesauformConfiguration.uriChange
				+ ">";
		String prolog3 = "PREFIX trait: <" + ThesauformConfiguration.term_uri + "#>";
		// Query string
		String queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
				+ ThesauformConfiguration.NL + "SELECT ?cpt WHERE { " + "<" + c
				+ "> change:vote ?vote . " + "?vote change:hasVote ?cpt . " + "?vote change:hasProperty <" + p + "> . "
				+ "?vote change:hasValue ?val . FILTER (NOT EXISTS { ?vote trait:reference ?ref } && ?val=\"" + value + "\" ) }";
		//@Patch1: count differently for definition with reference
		if (p == SkosVoc.definition && value.contains("__")) {
			String prolog4 = "PREFIX rdf: <" + ThesauformConfiguration.rdf + ">";
			String[] refDef = value.split("__");
			queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3 + ThesauformConfiguration.NL + prolog4
				+ ThesauformConfiguration.NL + "SELECT ?cpt WHERE { " + "<" + c
				+ "> change:vote ?vote . " + "?vote change:hasProperty <" + p + "> . "
				+ "?vote change:hasValue ?val . " + "?vote change:hasVote ?cpt . " + "?vote trait:reference ?ref . " + "?ref rdf:value ?refval . "
				+ "FILTER (?val=\"" + refDef[0] + "\" && ?refval=\"" + refDef[1] + "\" ) }";
		}
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			for (; rs.hasNext();) {
				QuerySolution rb = rs.nextSolution();
				RDFNode y = rb.get("cpt");
				voteList.add(y.asNode().getLiteralLexicalForm());
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return voteList;
	}

	/**
	 * Test existence of vote and return the resource
	 * 
	 * @param traitName
	 * @param property
	 * @param person
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public Resource existVote(String traitName, String property, String person, String value) throws Exception {
		Resource returnValue = null;
		Resource c = this.getResource(Format.formatName(traitName));
		Property p = null;
		if (property.equalsIgnoreCase("prefLabel")) {
			p = SkosXLVoc.prefLabel;
		} else if (property.equalsIgnoreCase("altLabel")) {
			p = SkosXLVoc.altLabel;
		} else if (property.equalsIgnoreCase("abbreviation")) {
			p = TraitVocTemp.abbreviation;
		} else if (property.equalsIgnoreCase("prefUnit")) {
			p = TraitVocTemp.prefUnit;
		} else if (property.equalsIgnoreCase("reference")) {
			p = TraitVocTemp.reference;
		} else if (property.equalsIgnoreCase("definition")) {
			p = SkosVoc.definition;
		} else if (property.equalsIgnoreCase("update")) {
			p = ChangeVoc.update;
		} else if (property.equalsIgnoreCase("delete")) {
			p = ChangeVoc.delete;
		} else if (property.equalsIgnoreCase("comment")) {
			p = ChangeVoc.comment;
		} else if (property.equalsIgnoreCase("insert")) {
			p = ChangeVoc.insert;
		} else if (property.equalsIgnoreCase("broaderTransitive")) {
			p = SkosVoc.broaderTransitive;
		} else if (property.equalsIgnoreCase("related")) {
			p = SkosVoc.related;
		} else {
			throw new Exception("Vocabularie " + property + " not managed");
		}
		String prolog1 = "PREFIX skos: <" + ThesauformConfiguration.skos + ">";
		String prolog2 = "PREFIX change: <" + ThesauformConfiguration.term_uri + ThesauformConfiguration.uriChange
				+ ">";
		String prolog3 = "PREFIX trait: <" + ThesauformConfiguration.term_uri + "#>";
		String prolog4 = "PREFIX dc: <" + ThesauformConfiguration.dc + ">";
		String prolog5 = "PREFIX foaf: <" + ThesauformConfiguration.foaf + ">";
		// Query string
		// TODO @Patch1 if def__ref annotation should be selected on a special way
		String queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
				+ ThesauformConfiguration.NL + prolog4 + ThesauformConfiguration.NL + prolog5
				+ ThesauformConfiguration.NL + "SELECT ?vote WHERE { " + "<" + c + "> change:vote ?vote . "
				+ "?vote change:hasProperty <" + p + "> . " + "?vote change:hasValue ?val . "
				+ "?vote change:contribution ?person . " + "?person dc:creator  ?creator . "
				+ "?creator foaf:name ?cname ." + "FILTER (?val = \"" + value
				+ "\" && NOT EXISTS { ?vote trait:reference ?ref } "
				+ "&& (REPLACE(LCASE(?cname), \" \", \"_\", \"i\")=\"" + person.toLowerCase().replace(" ", "_")
				+ "\"))." + " }";
		if (p == SkosVoc.definition && value.contains("__")) {
			String prolog6 = "PREFIX rdf: <" + ThesauformConfiguration.rdf + ">";
			String[] refDef = value.split("__");
			// change queryString
			queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
					+ ThesauformConfiguration.NL + prolog4 + ThesauformConfiguration.NL + prolog5
					+ ThesauformConfiguration.NL + prolog6 + ThesauformConfiguration.NL + "SELECT ?vote WHERE { " + "<"
					+ c + "> change:vote ?vote . " + "?vote change:hasProperty <" + p + "> . "
					+ "?vote change:hasValue ?val . " + "?vote trait:reference ?ref . " + "?ref rdf:value ?refval . "
					+ "?vote change:contribution ?person . " + "?person dc:creator  ?creator . "
					+ "?creator foaf:name ?cname ." + "FILTER (?val=\"" + refDef[0].trim() + "\" && ?refval = \""
					+ refDef[1].trim() + "\" && (REPLACE(LCASE(?cname), \" \", \"_\", \"i\")=\""
					+ person.toLowerCase().replace(" ", "_") + "\"))." + " }";
		}
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			if (rs.hasNext()) {
				QuerySolution rb = rs.nextSolution();
				RDFNode y = rb.get("vote");
				returnValue = y.as(Resource.class);
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return (returnValue);
	}

	/**
	 * Add a personal vote for annotation
	 * 
	 * @param traitName
	 * @param property
	 * @param person
	 * @param voteValue
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public Boolean addVote(String traitName, String property, String person, String value, Integer voteValue) throws Exception {
		Boolean returnValue = false;
		Resource c = this.getResource(Format.formatName(traitName));
		Property p = null;
		Resource pe = this.getPerson(person);
		//escape value #
		value = value.replace("#", "\\#");
		if (property.equalsIgnoreCase("prefLabel")) {
			p = SkosXLVoc.prefLabel;
		} else if (property.equalsIgnoreCase("altLabel")) {
			p = SkosXLVoc.altLabel;
		} else if (property.equalsIgnoreCase("abbreviation")) {
			p = TraitVocTemp.abbreviation;
		} else if (property.equalsIgnoreCase("prefUnit")) {
			p = TraitVocTemp.prefUnit;
		} else if (property.equalsIgnoreCase("reference")) {
			p = TraitVocTemp.reference;
		} else if (property.equalsIgnoreCase("definition")) {
			p = SkosVoc.definition;
		} else if (property.equalsIgnoreCase("update")) {
			p = ChangeVoc.update;
		} else if (property.equalsIgnoreCase("delete")) {
			p = ChangeVoc.delete;
		} else if (property.equalsIgnoreCase("comment")) {
			p = ChangeVoc.comment;
		} else if (property.equalsIgnoreCase("insert")) {
			p = ChangeVoc.insert;
		} else if (property.equalsIgnoreCase("broaderTransitive")) {
			p = SkosVoc.broaderTransitive;
		} else if (property.equalsIgnoreCase("related")) {
			p = SkosVoc.related;
		} else {
			throw new Exception("Vocabularie " + property + " not managed");
		}
		if (this.existVote(traitName, property, person, value) == null) {
			// TODO @Patch1 if def__ref annotation should be inserted in a special way
			Resource vote = m.createResource();
			m.add(vote, ChangeVoc.hasVote, m.createTypedLiteral(voteValue));
			m.add(vote, ChangeVoc.hasProperty, p);
			if (p == SkosVoc.definition && value.contains("__")) {
				String[] refDef = value.split("__");
				m.add(vote, ChangeVoc.hasValue, refDef[0].trim());
				Resource refR = createResource();
				m.add(refR,RDF.value,refDef[1]);
				m.add(refR,RDF.type,RefVoc.Reference);
				m.add(vote, TraitVocTemp.reference, refR);
			} else {
				m.add(vote, ChangeVoc.hasValue, value);
			}
			m.add(c, ChangeVoc.vote, vote);
			Resource contribution = m.createResource();
			m.add(contribution, DC.creator, pe);
			this.setResource(contribution, DCTerms.created, Calendar.getInstance());
			m.add(vote, ChangeVoc.contribution, contribution);
			returnValue = true;
		} else {
			throw new Exception("Vote already exists");
		}
		return (returnValue);
	}

	/**
	 * Delete a personal vote for annotation
	 * 
	 * @param traitName
	 * @param property
	 * @param person
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public Boolean delVote(String traitName, String property, String person, String value) throws Exception {
		Boolean returnValue = false;
		if (property.equalsIgnoreCase("prefLabel")) {
		} else if (property.equalsIgnoreCase("altLabel")) {
		} else if (property.equalsIgnoreCase("abbreviation")) {
		} else if (property.equalsIgnoreCase("prefUnit")) {
		} else if (property.equalsIgnoreCase("reference")) {
		} else if (property.equalsIgnoreCase("definition")) {
		} else if (property.equalsIgnoreCase("update")) {
		} else if (property.equalsIgnoreCase("delete")) {
		} else if (property.equalsIgnoreCase("comment")) {
		} else if (property.equalsIgnoreCase("insert")) {
		} else if (property.equalsIgnoreCase("broaderTransitive")) {
		} else if (property.equalsIgnoreCase("related")) {
		} else {
			throw new Exception("Vocabularie " + property + " not managed");
		}
		// TODO @Patch1 if def__ref annotation should be removed in a special way
		Resource vote = this.existVote(traitName, property, person, value);
		if (vote != null) {
			//TODO should be saved to the model
			// remove statements where resource is subject
		    m.removeAll(vote, null, (RDFNode) null);
		    // remove statements where resource is object
		    m.removeAll(null, null, vote);
			returnValue = true;
		} else {
			throw new Exception("Vote not existing");
		}
		return (returnValue);
	}

	/**
	 * Add note to a personal vote for annotation
	 * 
	 * @param traitName
	 * @param property
	 * @param person
	 * @param voteValue
	 * @param value
	 * @param comment
	 * @return
	 * @throws Exception
	 */
	public Boolean changeVote(String traitName, String property, String person, String value, Integer voteValue, String comment) throws Exception {
		Boolean returnValue = false;
		//test voteValue not 0
		if(voteValue!=0) {
			Resource vote = this.existVote(traitName, property, person, value);
			if (vote == null) {
				throw new Exception("Vote should be inserted before comment.");
			} else {
				//remove comment if already exists
			    m.removeAll(vote, RDFS.comment, (RDFNode) null);
				//add comment
				m.add(vote, RDFS.comment, comment);
				returnValue = true;
			}
		}
		else {
			throw new Exception("Could not comment without vote.");
		}
		return (returnValue);
	}
	
	/**
	 * Get vote comment if exists
	 * @param traitName
	 * @param property
	 * @param person
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public String getVoteComment(String traitName, String property, String person, String value) throws Exception {
		String comment = null;
		Resource vote = this.existVote(traitName, property, person, value);
		try {
				comment = vote.getProperty(RDFS.comment).getObject().toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return (comment);
	}
	
	public List<String> getAllDelete(String nameTrait) {
		List<String> deleteList = new ArrayList<String>();
		String prolog1 = "PREFIX trait: <" + ThesauformConfiguration.term_uri + "#>";
		String prolog2 = "PREFIX rdf: <" + ThesauformConfiguration.rdf + ">";
		String prolog3 = "PREFIX skos: <" + ThesauformConfiguration.skos + ">";
		String prolog4 = "PREFIX rdfs: <" + ThesauformConfiguration.rdfs + ">";
		String prolog5 = "PREFIX change: <" + ThesauformConfiguration.term_uri + ThesauformConfiguration.uriChange
				+ ">";
		String prolog6 = "PREFIX dc: <" + ThesauformConfiguration.dc + ">";
		String prolog7 = "PREFIX foaf: <" + ThesauformConfiguration.foaf + ">"; // Query
																				// string.
		String queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
				+ ThesauformConfiguration.NL + prolog4 + ThesauformConfiguration.NL + prolog5
				+ ThesauformConfiguration.NL + prolog6 + ThesauformConfiguration.NL + prolog7
				+ ThesauformConfiguration.NL + "SELECT DISTINCT ?cname WHERE {" + "?trait rdf:type skos:Concept ."
				+ "?trait rdfs:label ?name ." + "?trait change:delete ?del ." + "?del dc:creator ?creator ."
				+ "?creator foaf:name ?cname ." + "FILTER (REPLACE(LCASE(?name), \" \", \"_\", \"i\")=\""
				+ nameTrait.toLowerCase().replace(" ", "_") + "\")." + "}";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			for (; rs.hasNext();) {
				QuerySolution rb = rs.nextSolution();
				deleteList.add(rb.get("?cname").asNode().getLiteralLexicalForm());
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return deleteList;
	}

	/// does not return the value of the properties that were set up before the
	/// annotation process
	public Map<String, List<String>> getAnnotation(String trait, String type) {
		Resource concept = this.getResource(Format.formatName(trait));
		Map<String, List<String>> map = new TreeMap<>();
		if (type.equalsIgnoreCase("insert")) {
			StmtIterator iterator = concept.listProperties(ChangeVoc.insert);
			List<String> list = new ArrayList<String>();
			if (iterator.hasNext()) {
				list.add("insert");
				map.put("insert", list);
			}
		} else if (type.equalsIgnoreCase("delete")) {
			StmtIterator iterator = concept.listProperties(ChangeVoc.delete);
			List<String> list = new ArrayList<String>();
			if (iterator.hasNext()) {
				String contributor = iterator.next().getObject().as(Resource.class).getProperty(DC.creator).getObject()
						.as(Resource.class).getProperty(FOAF.name).getObject().asNode().getLiteralValue().toString();
				list.add(contributor);
			}
			map.put("delete", list);
		} else if (type.equalsIgnoreCase("update")) {
			StmtIterator iterator = concept.listProperties(ChangeVoc.update);
			while (iterator.hasNext()) {
				Resource annotation = iterator.next().getObject().as(Resource.class);
				// get the couple prop/value
				String value = getAnnotationInfo(annotation, ChangeVoc.hasValue).asNode().getLiteralLexicalForm();
				Property prop = getAnnotationInfo(annotation, ChangeVoc.hasProperty).as(Property.class);
				// build the map
				if (prop.getLocalName().equalsIgnoreCase("prefLabel")) {
					if (map.get("name") == null || map.get("name").isEmpty()) {
						List<String> list = new ArrayList<String>();
						list.add(value);
						map.put("name", list);
					} else {
						map.get("name").add(value);
					}
				} else if (prop.getLocalName().equalsIgnoreCase("definition")) {
					// @Patch1: get the reference if exists
					String refStr = "";
					try {
						if (annotation.getProperty(TraitVocTemp.reference).getResource() != null) {
							// reference exists
							Resource ref = annotation.getProperty(TraitVocTemp.reference).getResource();
							refStr = ref.listProperties(RDF.value).next().getObject().asNode()
									.getLiteralLexicalForm();
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					if (refStr != "") {
						value = value + "__" + refStr;
					}
					if (map.get("definition") == null || map.get("definition").isEmpty()) {
						List<String> list = new ArrayList<String>();
						list.add(value);
						map.put("definition", list);
					} else {
						map.get("definition").add(value);
					}
				} else if (prop.getLocalName().equalsIgnoreCase("reference")) {
					if (map.get("reference") == null || map.get("reference").isEmpty()) {
						List<String> list = new ArrayList<String>();
						list.add(value);
						map.put("reference", list);
					} else {
						map.get("reference").add(value);
					}
				} else if (prop.getLocalName().equalsIgnoreCase("abbreviation")) {
					if (map.get("abbreviation") == null || map.get("abbreviation").isEmpty()) {
						List<String> list = new ArrayList<String>();
						list.add(value);
						map.put("abbreviation", list);
					} else {
						map.get("abbreviation").add(value);
					}
				} else if (prop.getLocalName().equalsIgnoreCase("altLabel")) {
					if (map.get("synonym") == null || map.get("synonym").isEmpty()) {
						List<String> list = new ArrayList<String>();
						list.add(value);
						map.put("synonym", list);
					} else {
						map.get("synonym").add(value);
					}
				} else if (prop.getLocalName().equalsIgnoreCase("broaderTransitive")) {
					if (map.get("category") == null || map.get("category").isEmpty()) {
						List<String> list = new ArrayList<String>();
						list.add(value);
						map.put("category", list);
					} else {
						map.get("category").add(value);
					}
				} else if (prop.getLocalName().equalsIgnoreCase("prefUnit")) {
					if (map.get("unit") == null || map.get("unit").isEmpty()) {
						List<String> list = new ArrayList<String>();
						list.add(value);
						map.put("unit", list);
					} else {
						map.get("unit").add(value);
					}
				} else {

				}
			}
		}
		return map;
	}

	/*
	 * TODO test if annotation exists before to insert. Not working with reference definition.
	 * Not important as vote are linked to concept
	 */
	public boolean existsAnnotation(String trait, Property property, String value) {
		boolean exists = false;
		String prolog1 = "PREFIX trait: <" + ThesauformConfiguration.term_uri + "#>";
		String prolog2 = "PREFIX rdf: <" + ThesauformConfiguration.rdf + ">";
		String prolog3 = "PREFIX skos: <" + ThesauformConfiguration.skos + ">";
		String prolog4 = "PREFIX rdfs: <" + ThesauformConfiguration.rdfs + ">";
		String prolog5 = "PREFIX change: <" + ThesauformConfiguration.term_uri + ThesauformConfiguration.uriChange
				+ ">";
		String queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
				+ ThesauformConfiguration.NL + prolog4 + ThesauformConfiguration.NL + prolog5
				+ ThesauformConfiguration.NL + "SELECT ?name WHERE {" + "?trait rdf:type skos:Concept ."
				+ "?trait rdfs:label ?name ." + "?trait change:update ?upd ." + "?upd change:hasValue ?val ."
				+ "?upd change:hasProperty <" + property + "> ." + "FILTER (regex(?val,\"" + value
				+ "\") && NOT EXISTS { ?upd trait:reference ?ref } && REPLACE(LCASE(?name), \" \", \"_\", \"i\")=\""
				+ trait.toLowerCase().replace(" ", "_") + "\")." + "}";
		// @Patch1: if referenced definition
		if (property == SkosVoc.definition && value.contains("__")) {
			String[] refDef = value.split("__");
			// change queryString
			queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
					+ ThesauformConfiguration.NL + prolog4 + ThesauformConfiguration.NL + prolog5
					+ ThesauformConfiguration.NL + "SELECT ?name WHERE {" + "?trait rdf:type skos:Concept ."
					+ "?trait rdfs:label ?name ." + "?trait change:update ?upd ." + "?upd change:hasValue ?val ."
					+ "?upd change:hasProperty <" + property + "> ." + "?upd trait:reference ?ref . "
					+ "?ref rdf:value ?refval . " + "FILTER (regex(?val,\"" + refDef[0]
					+ "\") && REPLACE(LCASE(?name), \" \", \"_\", \"i\")=\" && regex(?val,\"" + refDef[1] + "\")"
					+ trait.toLowerCase().replace(" ", "_") + "\")." + "}";
		}
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			if (rs.hasNext()) {
				exists = true;
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return exists;
	}

	public Resource setConcept(String cl, Resource scheme, Resource person, Calendar date) {
		// Concepts
		Resource concept = this.createResource(cl);
		this.setResource(concept, RDF.type, SkosVoc.Concept); // OWL.Class
		this.setResource(concept, SkosVoc.inScheme, scheme);
		Resource note = this.createInsert(concept);
		this.setResource(note, DCTerms.created, date);
		this.setResource(note, DC.creator, person);
		this.setResource(concept, StatusVoc.term_status, "testing");
		// XL Label
		Resource ConceptLabel = this.createResource();
		this.setResource(ConceptLabel, RDF.type, SkosXLVoc.Label);
		this.setResource(ConceptLabel, SkosXLVoc.literalForm, cl); // RDFS.label
		this.setResource(concept, SkosXLVoc.prefLabel, ConceptLabel);
		// JOWL
		this.setResource(concept, RDF.type, OWL.Class);
		this.setResource(concept, RDFS.label, cl);
		return concept;
	}

	public Resource setConcept(String cl, Resource scheme, Resource father, Resource person, Calendar date) {
		// Concepts
		Resource concept = this.createResource(cl);
		this.setResource(concept, RDF.type, SkosVoc.Concept); // OWL.Class
		this.setResource(concept, SkosVoc.inScheme, scheme);
		Resource note = this.createInsert(concept);
		this.setResource(note, DCTerms.created, date);
		this.setResource(note, DC.creator, person);
		this.setResource(concept, StatusVoc.term_status, "testing");
		this.setResource(concept, SkosVoc.broaderTransitive, father); // RDFS.subClass
		// XL Label
		Resource Label = this.setPrefLabel(concept, cl);
		note = this.createInsert(Label);
		this.setResource(note, DCTerms.created, date);
		this.setResource(note, DC.creator, person);
		// JOWL
		this.setResource(concept, RDF.type, OWL.Class);
		this.setResource(concept, RDFS.subClassOf, father);
		this.setResource(concept, RDFS.label, cl);
		return concept;
	}

	public Resource setConcept(String cl, Resource scheme, Resource father, Resource person, Calendar date,
			String status) {
		// Concepts
		Resource concept = this.createResource(cl);
		this.setResource(concept, RDF.type, SkosVoc.Concept); // OWL.Class
		this.setResource(concept, SkosVoc.inScheme, scheme);
		Resource note = this.createInsert(concept);
		this.setResource(note, DCTerms.created, date);
		this.setResource(note, DC.creator, person);
		this.setResource(concept, StatusVoc.term_status, status);
		this.setResource(concept, SkosVoc.broaderTransitive, father); // RDFS.subClass
		// XL Label
		this.setPrefLabel(concept, cl);
		Resource Label = this.getPrefLabel(concept);
		note = this.createInsert(Label);
		this.setResource(note, DCTerms.created, date);
		this.setResource(note, DC.creator, person);
		// JOWL
		this.setResource(concept, RDF.type, OWL.Class);
		this.setResource(concept, RDFS.subClassOf, father);
		this.setResource(concept, RDFS.label, cl);
		return concept;
	}

	public Resource setConcept(String cl, Resource scheme, Resource person, Calendar date, String status) {
		// Concepts
		Resource concept = this.createResource(cl);
		this.setResource(concept, RDF.type, SkosVoc.Concept); // OWL.Class
		this.setResource(concept, SkosVoc.inScheme, scheme);
		Resource note = this.createInsert(concept);
		this.setResource(note, DCTerms.created, date);
		this.setResource(note, DC.creator, person);
		this.setResource(concept, StatusVoc.term_status, status);
		// XL Label
		this.setPrefLabel(concept, cl);
		Resource Label = this.getPrefLabel(concept);
		note = this.createInsert(Label);
		this.setResource(note, DCTerms.created, date);
		this.setResource(note, DC.creator, person);
		// JOWL
		this.setResource(concept, RDF.type, OWL.Class);
		this.setResource(concept, RDFS.label, cl);
		return concept;
	}

	public Resource setScheme(String sch, Resource person, Calendar date) {
		// Scheme
		Resource scheme = this.createResource(sch);
		this.setResource(scheme, RDF.type, SkosVoc.ConceptScheme);
		this.setResource(scheme, RDFS.label, sch);
		Resource note = this.createInsert(scheme);
		this.setResource(note, DCTerms.created, date);
		this.setResource(note, DC.creator, person);
		return scheme;
	}

	public Resource createLabel(String labelX) {
		Resource label = this.createResource();
		this.setResource(label, RDF.type, SkosXLVoc.Label);
		this.setResource(label, SkosXLVoc.literalForm, labelX); //
		return label;
	}

	public Resource createReference(String value) {
		Resource reference = this.createResource();
		this.setResource(reference, RDF.type, RefVoc.Reference);
		this.setResource(reference, RDF.value, value);
		return reference;
	}

	public Resource createUnit(String value) {
		Resource unit = this.createResource();
		this.setResource(unit, RDF.type, UnitVoc.Unit);
		this.setResource(unit, RDF.value, value);
		return unit;
	}

	public Resource setPrefLabel(Resource concept, String labelX) {
		Resource prefLabel = this.createLabel(labelX);
		this.setResource(concept, SkosXLVoc.prefLabel, prefLabel);
		return prefLabel;
	}

	public Resource setAltLabel(Resource concept, String labelX) {
		Resource altLabel = this.createLabel(labelX);
		this.setResource(concept, SkosXLVoc.altLabel, altLabel);
		return altLabel;
	}

	public Resource setFormalName(Resource concept, String labelX) {
		Resource altLabel = this.createLabel(labelX);
		this.setResource(concept, TraitVocTemp.formalName, altLabel);
		return altLabel;
	}

	public Resource setAbbreviation(Resource label, String labelX) {
		Resource abbr = this.createLabel(labelX);
		this.setResource(label, TraitVocTemp.abbreviation, abbr);
		return abbr;
	}

	public Resource setDefinition(Resource concept, String defi) {
		Resource def = this.createResource();
		this.setResource(concept, SkosVoc.definition, def);
		this.setResource(def, RDF.value, defi);
		return def;
	}

	public Resource setComment(Resource concept, String comment) {
		this.setResource(concept, RDF.value, comment);
		return concept;
	}

	public StmtIterator getComment(Resource concept) {
		return this.SimpleSelector(concept, ChangeVoc.comment, null);
	}

	public Resource setReference(Resource def, String ref) {
		Resource abbr = createReference(ref);
		this.setResource(def, TraitVocTemp.reference, abbr);
		return abbr;
	}

	public Resource setUnit(Resource concept, String unit) {
		Resource abbr = this.createUnit(unit);
		this.setResource(concept, TraitVocTemp.prefUnit, abbr);
		return abbr;
	}

	public void setRelated(Resource concept, Resource rel) {
		this.setResource(concept, SkosVoc.related, rel);
		this.setResource(rel, SkosVoc.related, concept);
	}

	public String getLabelLiteralForm(Resource label) {
		String Label = "";
		try {
			Label = label.getProperty(SkosXLVoc.literalForm).getObject().asNode().getLiteralLexicalForm();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return Label;
	}

	public Resource getPrefLabel(Resource concept) {
		Resource prefLabel = null;
		// get concept
		// get ?o depending on concept skosXL ?o
		try {
			prefLabel = concept.getProperty(SkosXLVoc.prefLabel).getObject().as(Resource.class);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return prefLabel;
	}

	public Resource getAltLabel(Resource concept) {
		Resource AltLabel = null;
		try {
			// many alternative labels
			AltLabel = concept.getProperty(SkosXLVoc.altLabel).getObject().as(Resource.class);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return AltLabel;
	}

	public StmtIterator getAllFormalName(Resource concept) {
		StmtIterator it = concept.listProperties(TraitVocTemp.formalName);
		return it;
	}

	public StmtIterator getAllAltLabel(Resource concept) {
		StmtIterator it = concept.listProperties(SkosXLVoc.altLabel);
		return it;
	}

	public StmtIterator getAllRelated(Resource concept) {
		StmtIterator it = concept.listProperties(SkosVoc.related);
		return it;
	}

	public StmtIterator getAllRelatedExtern(Resource concept) {
		StmtIterator it = concept.listProperties(SkosVoc.relatedMatch);
		return it;
	}

	public Resource getAbbreviation(Resource prefLabel) {
		Resource Abbr = null;
		// many abbreviation, unit, related
		try {
			Abbr = prefLabel.getProperty(TraitVocTemp.abbreviation).getObject().as(Resource.class);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return Abbr;
	}

	public StmtIterator getAllAbbrev(Resource preflabel) {
		StmtIterator it = preflabel.listProperties(TraitVocTemp.abbreviation);
		return it;
	}

	public Resource getDefinition(Resource concept) {
		Resource Def = null;
		try {
			Def = concept.getProperty(SkosVoc.definition).getObject().as(Resource.class);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return Def;
	}

	public String getValue(Resource def) {
		String value = "";
		try {
			value = def.getProperty(RDF.value).getObject().asNode().getLiteralLexicalForm();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return value;
	}

	public Resource getReference(Resource definition) {
		Resource Ref = null;
		try {
			Ref = definition.getProperty(TraitVocTemp.reference).getObject().as(Resource.class);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return Ref;
	}

	public StmtIterator getAllReference(Resource def) {
		StmtIterator it = def.listProperties(TraitVocTemp.reference);
		return it;
	}

	public Resource getUnit(Resource concept) {
		Resource unit = null;
		try {
			unit = concept.getProperty(TraitVocTemp.prefUnit).getObject().as(Resource.class);
			if (!unit.hasProperty(RDF.type, UnitVoc.Unit)) {
				unit = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return unit;
	}

	public String getCategory(String concept) {
		String s = "";
		if (this.getResource(concept).getProperty(SkosVoc.broaderTransitive) != null) {
			s = this.getResource(concept).getProperty(SkosVoc.broaderTransitive).getObject().as(Resource.class)
					.getLocalName();
		}
		return s;
	}

	public Resource getCategory(Resource concept) {
		Resource cat = null;
		try {
			cat = concept.getProperty(SkosVoc.broaderTransitive).getObject().as(Resource.class);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return cat;
	}

	/**
	 * get all parent of a concept
	 * 
	 * @param concept
	 *            Resource
	 * @return StmtIterator
	 */
	public StmtIterator getAllParent(Resource concept) {
		StmtIterator it = concept.listProperties(SkosVoc.broaderTransitive);
		return it;
	}

	public boolean containsConcept(Resource concept) {
		boolean bool = false;
		try {
			bool = m.containsResource(concept);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bool;
	}

	// TODO better solution
	public Resource getScheme() {
		// TODO Auto-generated method stub
		StmtIterator it = this.SimpleSelector(null, RDF.type, SkosVoc.ConceptScheme);
		return it.next().getSubject();
	}

	public StmtIterator getListStatement(String concept) {
		return this.getResource(concept).listProperties();
	}

	public Map<String, Map<String, String>> getAnnotation(String trait) {
		Resource concept = this.getResource(trait);
		Map<String, Map<String, String>> map = new TreeMap<>();
		StmtIterator iterator = concept.listProperties(ChangeVoc.update);
		while (iterator.hasNext()) {
			RDFNode conceptAnnote = iterator.next().getObject();
			if (conceptAnnote.isResource()) {
				Resource scN = conceptAnnote.as(Resource.class);
				Map<String, String> tab = new TreeMap<>();
				RDFNode date = getAnnotationInfo(scN, DCTerms.created);
				RDFNode contrib = getAnnotationInfo(scN, DC.creator);
				RDFNode name = null;
				if (contrib != null) {
					name = getAnnotationInfo(contrib.as(Resource.class), FOAF.name);
				} else {

				}
				RDFNode anno = null;
				Property prop = null;
				String refStr = "";
				anno = getAnnotationInfo(scN, ChangeVoc.hasValue);
				prop = getAnnotationInfo(scN, ChangeVoc.hasProperty).as(Property.class);
				// get the reference if exists
				try {
					if (scN.getProperty(TraitVocTemp.reference).getResource() != null) {
						// reference exists
						Resource ref = scN.getProperty(TraitVocTemp.reference).getResource();
						refStr = ref.listProperties(RDF.value).next().getObject().asNode().getLiteralLexicalForm();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				if (name != null) {
					tab.put("date", date.asNode().getLiteralValue().toString());
					tab.put("contributor", name.asNode().getLiteralValue().toString());
					if (refStr != "") {
						tab.put("reference", refStr);
					}
				}
				if (prop != null && anno != null) {
					if (prop.getLocalName().equalsIgnoreCase("prefLabel")) {
						tab.put("pref name", anno.asNode().getLiteralValue().toString());
					} else if (prop.getLocalName().equalsIgnoreCase("altLabel")) {
						tab.put("synonym", anno.asNode().getLiteralValue().toString());
					} else if (prop.getLocalName().equalsIgnoreCase("broaderTransitive")) {
						tab.put("term category", anno.asNode().getLiteralValue().toString());
					} else {
						tab.put(prop.getLocalName(), anno.asNode().getLiteralValue().toString());
					}
				}
				if (!tab.isEmpty()) {
					map.put(scN.toString(), tab);
				}
			}
		}
		if (concept.getProperty(ChangeVoc.delete) != null) {
			if (concept.getProperty(ChangeVoc.delete).getObject() != null) {// &&
				Map<String, String> tab = new TreeMap<>();
				tab.put("deleted",
						concept.getProperty(ChangeVoc.delete).getObject().as(Resource.class).getProperty(DC.creator)
								.getObject().as(Resource.class).getProperty(FOAF.name).getObject().asNode()
								.getLiteralValue().toString() + " proposed to delete this trait");
				map.put("deleted", tab);
			}
		}
		return map;
	}

	public boolean isSynonym(String trait) {
		boolean bool = false;
		return bool;
	}

	public Resource IsprefLabel(String trait) {
		Resource concept = this.getResource(trait).listProperties(SkosXLVoc.prefLabel).next().getObject()
				.as(Resource.class);
		Resource sujet = null;
		StmtIterator iter = m.listStatements(new SimpleSelector((Resource) null, SkosXLVoc.altLabel, (RDFNode) null) {
			public boolean selects(Statement s) {
				return (subject == null || s.getSubject().equals(subject))
						&& (predicate == null || s.getPredicate().equals(predicate))
						&& (object == null || s.getObject().equals(object));
			}
		});
		while (iter.hasNext()) {
			Statement st = iter.next();
			if (st.getObject().as(Resource.class).equals(concept)) {
				sujet = st.getSubject();
			}
		}
		return sujet;
	}

	public Resource getLiteral(Property p, String def) {
		Resource sujet = null;
		StmtIterator iter = m.listStatements(new SimpleSelector((Resource) null, p, (RDFNode) null) {
			public boolean selects(Statement s) {
				return (subject == null || s.getSubject().equals(subject))
						&& (predicate == null || s.getPredicate().equals(predicate))
						&& (object == null || s.getObject().equals(object));
			}
		});
		while (iter.hasNext()) {
			Statement st = iter.next();
			if (st.getObject().asNode().getLiteralValue().toString().trim().equalsIgnoreCase(def.trim())) {
				sujet = st.getSubject();
			}
		}
		return sujet;
	}

	@Override
	public Resource createComment(Resource concept) {
		// TODO Auto-generated method stub
		Resource note = this.createResource();
		this.setResource(concept, ChangeVoc.comment, note);
		return note;
	}

	@Override
	public Resource createDelete(Resource concept) {
		// TODO Auto-generated method stub
		Resource note = this.createResource();
		this.setResource(concept, ChangeVoc.delete, note);
		return note;
	}

	@Override
	public Resource createInsert(Resource concept) {
		// TODO Auto-generated method stub
		Resource note = this.createResource();
		this.setResource(concept, ChangeVoc.insert, note);
		return note;
	}

	@Override
	public Resource createUpdate(Resource concept) {
		// TODO Auto-generated method stub
		Resource note = this.createResource();
		this.setResource(concept, ChangeVoc.update, note);
		return note;
	}

	@Override
	public void sethasProperty(Resource note, Property p) {
		// TODO Auto-generated method stub
		this.setResource(note, ChangeVoc.hasProperty, p);

	}

	@Override
	public void sethasValue(Resource note, String value) {
		// TODO Auto-generated method stub
		this.setResource(note, ChangeVoc.hasValue, value);
	}

	@Override
	public void sethasValue(Resource note, Resource value) {
		// TODO Auto-generated method stub
		this.setResource(note, ChangeVoc.hasValue, value);
	}

	public StmtIterator getConceptUpdate() {
		StmtIterator iter = m.listStatements(new SimpleSelector(null, ChangeVoc.update, (RDFNode) null) {
			public boolean selects(Statement s) {
				return (subject == null || s.getSubject().equals(subject))
						&& (predicate == null || s.getPredicate().equals(predicate))
						&& (object == null || s.getObject().equals(object));
			}
		});
		return iter;
	}

	public StmtIterator getConceptUpdate(Resource sujet) {
		StmtIterator iter = m.listStatements(new SimpleSelector(sujet, ChangeVoc.update, (RDFNode) null) {
			public boolean selects(Statement s) {
				return (subject == null || s.getSubject().equals(subject))
						&& (predicate == null || s.getPredicate().equals(predicate))
						&& (object == null || s.getObject().equals(object));
			}
		});
		return iter;
	}

	public StmtIterator SimpleSelector(Resource s, Property p, RDFNode o) {
		StmtIterator iter = m.listStatements(new SimpleSelector(s, p, o) {
			public boolean selects(Statement s) {
				return (subject == null || s.getSubject().equals(subject))
						&& (predicate == null || s.getPredicate().equals(predicate))
						&& (object == null || s.getObject().equals(object));
			}
		});
		return iter;
	}

	public RDFNode getAnnotationInfo(Resource sujet, Property prop) {
		RDFNode objet = null;
		StmtIterator iter = m.listStatements(new SimpleSelector(sujet, prop, (RDFNode) null) {
			public boolean selects(Statement s) {
				return (subject == null || s.getSubject().equals(subject))
						&& (predicate == null || s.getPredicate().equals(predicate))
						&& (object == null || s.getObject().equals(object));
			}
		});
		while (iter.hasNext()) {
			Statement st = iter.next();
			objet = st.getObject();
		}
		return objet;
	}

	public StmtIterator getResourceByPerson(Resource person) {
		return this.SimpleSelector(null, DC.creator, person);
	}

	public Resource annoteStmt(Resource s, Property p, RDFNode o) {
		StmtIterator iter = this.SimpleSelector(s, p, o);
		Resource st = m.createReifiedStatement(iter.next());
		return st;
	}

	public NodeIterator getSubclass(Resource concept) {
		StmtIterator i = this.SimpleSelector(null, RDF.type, SkosVoc.Concept);
		while (i.hasNext()) {
			Resource s = i.next().getSubject();
			StmtIterator stmtIt = null;
			Statement stmt = null;
			try {
				stmtIt = s.listProperties(SkosVoc.broaderTransitive);
				while (stmtIt.hasNext()) {
					stmt = stmtIt.next();
					if (stmt != null) {
						m.add(stmt.getObject().as(Resource.class), SkosVoc.narrowerTransitive, stmt.getSubject());
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		NodeIterator it = null;
		it = m.listObjectsOfProperty(concept, SkosVoc.narrowerTransitive);
		return it;
	}

	public Resource getSuperclass(Resource r) {
		Resource pere = null;
		try {
			if (!r.getLocalName().equalsIgnoreCase(ThesauformConfiguration.super_root)) {
				pere = r.listProperties(SkosVoc.broaderTransitive).next().getObject().as(Resource.class);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return pere;
	}

	public Resource createCollection(String t) {
		Resource collection = this.createResource(t);
		this.setResource(collection, RDF.type, SkosVoc.Collection);
		return collection;
	}

	public void addMember(Resource collection, Resource r) {
		if (!m.contains(collection, SkosVoc.member, r)) {
			this.setResource(collection, SkosVoc.member, r);
		}
	}

	// when validation
	public void removeMember(Resource collection, Resource r) {
		m.remove(collection, SkosVoc.member, r);
	}

	public NodeIterator getAllMember(Resource collection) {
		NodeIterator it = m.listObjectsOfProperty(collection, SkosVoc.member);
		return it;
	}

	public StmtIterator getCollection(Resource concept) {
		StmtIterator it = m.listStatements(new SimpleSelector(null, SkosVoc.member, concept) {
			public boolean selects(Statement s) {
				return (subject == null || s.getSubject().equals(subject))
						&& (predicate == null || s.getPredicate().equals(predicate))
						&& (object == null || s.getObject().equals(object));
			}
		});
		return it;
	}

	public Resource compteur(Resource c, Property p, Resource person, String value) {
		String prolog1 = "PREFIX skos: <" + ThesauformConfiguration.skos + ">";
		String prolog2 = "PREFIX change: <" + ThesauformConfiguration.term_uri + ThesauformConfiguration.uriChange
				+ ">";
		String prolog3 = "PREFIX trait: <" + ThesauformConfiguration.term_uri + "#>";
		// Query string.
		String queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL + prolog3
				+ ThesauformConfiguration.NL + "SELECT ?voteNum ?vote ?val WHERE { " + "<" + c
				+ "> change:vote ?vote . " + "?vote change:hasProperty <" + p + "> . "
				+ "?vote change:hasVote ?voteNum . " + "?vote change:hasValue ?val . FILTER regex(?val,\"" + value
				+ "\") . " + " }";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		Resource vote = null;
		String voteNum = "";
		try {
			ResultSet rs = qexec.execSelect();
			if (!rs.hasNext()) {
				vote = m.createResource();
				m.add(vote, ChangeVoc.hasVote, m.createTypedLiteral(0));
				m.add(vote, ChangeVoc.hasProperty, p);
				m.add(vote, ChangeVoc.hasValue, value);
				m.add(c, ChangeVoc.vote, vote);
				voteNum = "0";
			} else {
				for (; rs.hasNext();) {
					QuerySolution rb = rs.nextSolution();
					RDFNode y = rb.get("vote");
					vote = y.as(Resource.class);
					RDFNode x = rb.get("voteNum");
					voteNum = x.asNode().getLiteralLexicalForm();
				}
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		int num = Integer.parseInt(voteNum);
		num++;
		vote.listProperties(ChangeVoc.hasVote).next().changeObject(m.createTypedLiteral(num));
		Resource contribution = m.createResource();
		m.add(contribution, DC.creator, person);
		this.setResource(contribution, DCTerms.created, Calendar.getInstance());
		m.add(vote, ChangeVoc.contribution, contribution);
		return vote;
	}

	public void removeSubject(Resource r) {
		m.remove(r, null, (RDFNode) null);
	}

	public void removeObject(Resource r) {
		m.remove(null, null, r);
	}

	public void remove(StmtIterator arg0) {
		m.remove(arg0);
	}

	public void remove(Resource s, Property p) {
		m.remove(s, p, (RDFNode) null);
	}

	public Resource getConceptFromAbbrev(String o) {
		Resource concept = null;
		String prolog1 = "PREFIX trait: <" + ThesauformConfiguration.term_uri + "#>";
		String prolog2 = "PREFIX skosxl: <" + "" + ThesauformConfiguration.skosXL + "" + ">";
		// Query string.
		String queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL
				+ "SELECT ?concept WHERE {" + "?abbrev skosxl:literalForm ?name . FILTER regex(?name,\"" + o + "\")."
				+ "?label trait:abbreviation ?abbrev . " + "?concept skosxl:prefLabel ?label}";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			for (; rs.hasNext();) {
				QuerySolution rb = rs.nextSolution();
				RDFNode y = rb.get("concept");
				concept = y.asResource();
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return concept;
	}

	public Resource getConceptFromSyn(String o) {
		Resource concept = null;
		String prolog1 = "PREFIX trait: <" + ThesauformConfiguration.term_uri + "#>";
		String prolog2 = "PREFIX skosxl: <" + "" + ThesauformConfiguration.skosXL + "" + ">";
		// Query string
		String queryString = prolog1 + ThesauformConfiguration.NL + prolog2 + ThesauformConfiguration.NL
				+ "SELECT ?concept WHERE {" + "?label skosxl:literalForm ?name . FILTER regex(?name,\"^" + o + "$\")."
				+ "?concept skosxl:altLabel ?label}";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet rs = qexec.execSelect();
			for (; rs.hasNext();) {
				QuerySolution rb = rs.nextSolution();
				RDFNode y = rb.get("concept");
				concept = y.asResource();
			}
		} finally {
			// QueryExecution objects should be closed to free any system
			// resources
			qexec.close();
		}
		return concept;
	}

	public QueryExecution getQexec() {
		String prolog2 = "PREFIX rdf: <" + RDF.getURI() + ">";
		String prolog3 = "PREFIX skos: <" + ThesauformConfiguration.skos + ">";
		String prolog4 = "PREFIX rdfs: <" + RDFS.getURI() + ">";
		String prolog5 = "PREFIX skosxl: <" + ThesauformConfiguration.skosXL + ">";
		String prolog6 = "PREFIX xsd: <" + ThesauformConfiguration.xsd + ">";
		String prolog7 = "PREFIX dc: <" + ThesauformConfiguration.dc + ">";
		String prolog8 = "PREFIX foaf: <" + ThesauformConfiguration.foaf + ">";
		String prolog9 = "PREFIX owl: <" + ThesauformConfiguration.owl + ">";
		String prolog10 = "PREFIX dct: <" + ThesauformConfiguration.dct + ">";
		String prolog11 = "PREFIX vs: <" + ThesauformConfiguration.vs + ">";
		// // Query string.
		String prolog1 = "PREFIX trait: <" + ThesauformConfiguration.term_uri + "#>";
		// Query string.
		String queryString = prolog1 + "\n" + prolog2 + "\n" + prolog3 + "\n" + prolog4 + "\n" + prolog5 + "\n"
				+ prolog6 + "\n" + prolog7 + "\n" + prolog8 + "\n" + prolog9 + "\n" + prolog10 + "\n" + prolog11 + "\n"
				+ "PREFIX obo: <http://purl.obolibrary.org/obo/>\nPREFIX oboInOwl: <http://www.geneontology.org/formats/oboInOwl#>\n CONSTRUCT { "
				+ "?uri2 a owl:Class;" + "    rdfs:label ?label;" + "    obo:IAO_0000115 ?skosDefinition;"
				+ "    rdfs:subClassOf ?subClassOf2;" + "    oboInOwl:hasExactSynonym ?synonym;"
				+ "    oboInOwl:hasRelatedSynonym ?relatedSyonym ;" + "    oboInOwl:hasDbXref ?po ;"
				+ "    oboInOwl:hasDbXref ?ref ." + "" + "}" + "WHERE {" + " ?uri a skos:Concept, owl:Class ." + "    "
				+ "?uri rdfs:label ?label. ?uri vs:term_status 'stable' " + "    "
				+ "OPTIONAL { ?uri rdfs:subClassOf ?subClassOf . }"
				+ "OPTIONAL { ?uri skosxl:altLabel ?altLabelNode . ?altLabelNode skosxl:literalForm ?synonym . }"
				+ "OPTIONAL { ?uri skosxl:prefLabel ?prefLabelNode . ?prefLabelNode trait:abbreviation ?abbrevNode . ?abbrevNode skosxl:literalForm ?synonym}"
				+ "" + "" + "OPTIONAL { ?uri skos:altLabel ?synonym .}"
				+ "OPTIONAL { ?uri skos:definition ?skosDefinitionNode . ?skosDefinitionNode rdf:value ?skosDefinition . }"
				+ "OPTIONAL { ?uri skos:definition ?skosDefinitionNode .?skosDefinitionNode trait:reference ?refNode . ?refNode rdf:value ?ref . }"
				+ "OPTIONAL { ?uri skos:relatedMatch ?po . }" + "" + "BIND ( iri(" + "    replace("
				+ "        replace(str(?uri), '(.*:.*?):(.*)', '$1_$2')," + "        '"
				+ ThesauformConfiguration.term_uri + "#'," + "        'http://purl.obolibrary.org/obo/'" + "    )"
				+ ") AS ?uri2 )" + "BIND ( iri(" + "    replace("
				+ "        replace(str(?subClassOf), '(.*:.*?):(.*)', '$1_$2')," + "        '"
				+ ThesauformConfiguration.term_uri + "#'," + "        'http://purl.obolibrary.org/obo/'" + "    )"
				+ ") AS ?subClassOf2 )" + "}";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		return qexec;
	}
}
