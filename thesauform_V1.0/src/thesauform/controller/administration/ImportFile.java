package thesauform.controller.administration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;

import thesauform.beans.TraitConcept;
import thesauform.model.Format;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;
import thesauform.model.vocabularies.ChangeVoc;
import thesauform.model.vocabularies.SkosVoc;
import thesauform.model.vocabularies.SkosXLVoc;
import thesauform.model.vocabularies.TraitVocTemp;

/**
 * Servlet implementation class ExportFile
 */
@WebServlet("/administration/importFile")
@MultipartConfig
public class ImportFile extends HttpServlet {
	/**
	 * import csv template (defined in export) 
	 * load template row into model 
	 * write model into temp file 
	 * test temp file 
	 * save old file 
	 * write real file
	 */
	private static final long serialVersionUID = 1L;

	public static final String VUE_SUCCESS = "/WEB-INF/scripts/importFile.jsp";
	public static final String VUE_FAILED = "/WEB-INF/scripts/importFile.jsp";
	private static final String TYPE_ERROR = "type";
	private static final String USER_ERROR_PARAMETER = "my_errors";
	private static final String EMPTY_ROOT_MESSAGE = "No root defined";
	private static final String MULTI_ROOTS_MESSAGE = "More than one root defined";
	private static final String INFINITE_LOOP_MESSAGE = "Infinite loop detected";
	private static final String MERGE_PARAMETER = "merge";

	/**
	 * Check file is not a security problem
	 * @return true if no problem
	 * @throws IOException 
	 */
	protected boolean checkFile(File myFile) {
		Boolean return_value = false;
		//check extension
		try {
			String fileName = myFile.getName();
			if(fileName.endsWith(".csv")) {
				//read first line
				BufferedReader reader;
				reader = new BufferedReader(new FileReader(myFile));
				String header = reader.readLine();
			    if(header.equalsIgnoreCase(ThesauformConfiguration.TEMPLATE_HEADER)){
			    	return_value = true;
			    }
			    reader.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return(return_value);
	}


	//add concept
	protected boolean addConcept(SkosTraitModel model, TraitConcept concept, Resource scheme, Calendar date, Resource person) {
		Boolean return_value = false;
		try {
			Resource newConcept = null;
			//test if already existing
			if(!model.existsProperty(Format.formatName(concept.getName()), SkosXLVoc.prefLabel)) {
				newConcept = model.setConcept(Format.formatName(concept.getName()), scheme, person, date,"stable");
			}
			else {
				newConcept = model.getResource(Format.formatName(concept.getName()));

			}
			//test if already existing
			Resource insertlist = model.createCollection("Insert");
			if(!model.existsProperty(Format.formatName(concept.getName()), ChangeVoc.insert)) {
				model.addMember(insertlist, newConcept);
			}
			if(concept.getDefinition()!=null) {
				String defRef = concept.getDefinition();
				String definition = "";
				String reference = "";
				Pattern pattern = Pattern.compile("^(.*)\\(([^(]*)\\)$");
				Matcher matcher = pattern.matcher(defRef);
				if (matcher.matches()){
					definition = matcher.group(1);
					reference = matcher.group(2);
				} else {
					definition = defRef;
				}
				//test if already existing
				if(!model.existsProperty(concept.getName(), SkosVoc.definition)) {
					if (!definition.trim().isEmpty() || !definition.trim().equalsIgnoreCase("")) {
						// definition
						Resource def = model.setDefinition(newConcept, definition.trim());
						Resource note = model.createInsert(def);
						model.setResource(note, DCTerms.created, date);
						model.setResource(note, DC.creator, person);
					}
					if (!reference.trim().isEmpty() || !reference.trim().equalsIgnoreCase("")) {
						Resource ref = model.setReference(model.getDefinition(newConcept), reference.trim());
						Resource note = model.createInsert(ref);
						model.setResource(note, DCTerms.created, date);
						model.setResource(note, DC.creator, person);
					}
				}
				else {
					//do nothing
				}
			}
			if(concept.getAbbreviation()!=null) {
				//test if already existing
				if(!model.existsProperty(concept.getName(), TraitVocTemp.abbreviation)) {
					if (!concept.getAbbreviation().trim().isEmpty() || !concept.getAbbreviation().trim().equalsIgnoreCase("")) {
						Resource Label = model.setAbbreviation(model.getPrefLabel(newConcept), concept.getAbbreviation().trim());
						Resource note = model.createInsert(Label);
						model.setResource(note, DCTerms.created, date);
						model.setResource(note, DC.creator, person);
					}
				}
			}
			if(concept.getSynonymsList()!=null) {
				if (!concept.getSynonymsList().isEmpty()) {
					for (TraitConcept syn : concept.getSynonymsList()) {
						model.setResource(newConcept, SkosXLVoc.altLabel, syn.getName());
					}
				}
			}
			if(concept.getUnit()!=null) {
				//test if already existing
				if(!model.existsProperty(concept.getName(), TraitVocTemp.prefUnit)) {
					if (concept.getUnit()!=null && (!concept.getUnit().trim().isEmpty() || !concept.getUnit().trim().equalsIgnoreCase(""))) {
						Resource unit = model.setUnit(newConcept, concept.getUnit().trim());
						Resource note = model.createInsert(unit);
						model.setResource(note, DCTerms.created, date);
						model.setResource(note, DC.creator, person);
					}
				}
			}
			//non transitive
			if(concept.getRelatedsList()!=null) {
				if (!concept.getRelatedsList().isEmpty()) {
					for (TraitConcept rel : concept.getRelatedsList()) {
						model.setResource(newConcept, SkosVoc.related, model.createResource(rel.getName()));
					}
				}
			}
			return_value = true;
		}
		catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return return_value;
	}
	
	//add parent
	protected boolean addParent(SkosTraitModel model, TraitConcept concept, Resource scheme, Calendar date, Resource person) {
		Boolean return_value = false;
		try {
			Resource existingConcept = model.getResource(Format.formatName(concept.getName()));
			if(concept.getParent()!= null && !concept.getParent().isEmpty()) {
				for (TraitConcept parent : concept.getParent()) {
					Resource fatherConcept = model.getResource(Format.formatName(parent.getName()));
					model.setFather(existingConcept, fatherConcept);
				}
			}
			return_value = true;
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();

		}
		return return_value;
	}
	
	/**
	 * insert the uploaded file
	 * @return true if success
	 */
	protected boolean insertFile(File myFile, boolean merge){
		Boolean return_value = false;
		//store hierarchy for cycle detection
		HashMap<String, List<String>> hierarchyMap = new HashMap<String, List<String>>();
		//store the concepts
		HashMap<String, TraitConcept> conceptMap = new HashMap<String, TraitConcept>();
		//store the roots
		List<String> rootList = new ArrayList<String>();
		//read csv file line by line
		try {
			BufferedReader in = new BufferedReader(new FileReader(myFile.getAbsolutePath()));
			//header already checked, read first line and do nothing
			in.readLine();
			String myLine = "";
			String[] myArrayLine;
			String name = "";
			String unit = "";
			String definition = "";
			String abbreviation = "";
			String parents = "";
			String synonyms = "";
			String relateds = "";
			while ((myLine = in.readLine()) != null){
				TraitConcept myTrait = new TraitConcept();
				//split the line
				myArrayLine = myLine.split(",",-1);
				//link cells to objects
				name = myArrayLine[0].trim();
				parents = myArrayLine[1].trim();
				unit = myArrayLine[3].trim();
				definition = myArrayLine[2].trim();
				abbreviation = myArrayLine[4].trim();
				synonyms = myArrayLine[5].trim();
				relateds = myArrayLine[6].trim();
				List<TraitConcept> parentsList = new ArrayList<TraitConcept>();
				if(parents.contains("/")) {
					//multi parents
					String[] parentsArray = parents.split("/");
					for (int i = 0; i < parentsArray.length; i++) {
						TraitConcept myParentTrait = new TraitConcept();
						String parentName = parentsArray[i].trim();
						myParentTrait.setName(parentName);
						parentsList.add(myParentTrait);
						parentName = Format.formatName(parentName);
						//test if parent exists
						if(hierarchyMap.containsKey(parentName)) {
							//test if relation parent/son is already set
							List<String> mySonList = hierarchyMap.get(parentName);
							String sonName = Format.formatName(name);
							if(!mySonList.contains(sonName)) {
								mySonList.add(sonName);
								hierarchyMap.put(parentName, mySonList);
							}
						}
						else {
							List<String> mySonList = new ArrayList<String>();
							mySonList.add(Format.formatName(name));
							hierarchyMap.put(parentName, mySonList);
						}
					}
				}
				else {
					if(!parents.isEmpty()) {
						//only one parent
						TraitConcept myParentTrait = new TraitConcept();
						myParentTrait.setName(parents.trim());
						parentsList.add(myParentTrait);
						//test if parent exists
						String parentName = Format.formatName(parents.trim());
						if(hierarchyMap.containsKey(parentName)) {
							//test if relation parent/son is already set
							List<String> mySonList = hierarchyMap.get(parentName);
							String sonName = Format.formatName(name);
							if(!mySonList.contains(sonName)) {
								mySonList.add(sonName);
								hierarchyMap.put(parentName, mySonList);
							}
						}
						else {
							List<String> mySonList = new ArrayList<String>();
							mySonList.add(Format.formatName(name));
							hierarchyMap.put(parentName, mySonList);
						}
					}
					else {
						//it is a root
						rootList.add(name);
					}
				}
				if(!parentsList.isEmpty()) {
					myTrait.setParent(parentsList);
				}
				List<TraitConcept> synonymsList = new ArrayList<TraitConcept>();
				if(synonyms.contains("/")) {
					//multi synonyms
					String[] synonymsArray = synonyms.split("/");
					for (int i = 0; i < synonymsArray.length; i++) {
						TraitConcept mySynonymTrait = new TraitConcept();
						mySynonymTrait.setName(synonymsArray[i].trim());
						synonymsList.add(mySynonymTrait);
					}
				}
				else {
					if(!synonyms.isEmpty()) {
						//only one synonym
						TraitConcept mySynonymTrait = new TraitConcept();
						mySynonymTrait.setName(synonyms.trim());
						synonymsList.add(mySynonymTrait);
					}
					else {
						//no synonym, do nothing
					}
				}
				if(!synonymsList.isEmpty()) {
					myTrait.setSynonymsList(synonymsList);
				}
				List<TraitConcept> relatedsList = new ArrayList<TraitConcept>();
				if(relateds.contains("/")) {
					//multi relateds
					String[] relatedsArray = relateds.split("/");
					for (int i = 0; i < relatedsArray.length; i++) {
						TraitConcept myRelatedTrait = new TraitConcept();
						myRelatedTrait.setName(relatedsArray[i].trim());
						relatedsList.add(myRelatedTrait);
					}
				}
				else {
					if(!relateds.isEmpty()) {
						//only one related
						TraitConcept myRelatedTrait = new TraitConcept();
						myRelatedTrait.setName(relateds.trim());
						relatedsList.add(myRelatedTrait);
					}
					else {
						//no related, do nothing
					}
				}
				if(!relatedsList.isEmpty()) {
					myTrait.setRelatedsList(relatedsList);
				}
				myTrait.setName(name);
				if(!definition.isEmpty()) {
					myTrait.setDefinition(definition);
				}
				if(!unit.isEmpty()) {
					myTrait.setUnit(unit);
				}
				if(!abbreviation.isEmpty()){
					myTrait.setAbbreviation(abbreviation);
				}
				conceptMap.put(name,myTrait);
			}
			//for each root
			if(rootList.size()==1) {
				for (String root : rootList) {
					//go through hierarchy to test infinite loop
					List<String> visitedList = new ArrayList<String>();
					if(infinite_loop(hierarchyMap,visitedList,Format.formatName(root),null)) {
						in.close();
						throw new Exception(INFINITE_LOOP_MESSAGE);
					}
					SkosTraitModel m = null;
					if(merge) {
						//read model
						if(ThesauformConfiguration.database) {
							m = new SkosTraitModel(ThesauformConfiguration.data_file);
						}
						else {
							m = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.data_file));
						}						
					}
					else {
						//create model
						m = new SkosTraitModel();
					}
					Calendar myDate = Calendar.getInstance();
					Resource myCreator = m.getPerson("admin", "admin@thesauform.org");
					Resource myScheme = m.setScheme(ThesauformConfiguration.super_root, myCreator, myDate);
					//recursively go through hierarchy
				    Iterator<Entry<String, TraitConcept>> it = conceptMap.entrySet().iterator();
				    while (it.hasNext()) {
				        Map.Entry<String, TraitConcept> pair = (Map.Entry<String, TraitConcept>)it.next();
						addConcept(m, (TraitConcept)pair.getValue(), myScheme, myDate, myCreator);
				    }
				    //add parent
				    Iterator<Entry<String, TraitConcept>> it2 = conceptMap.entrySet().iterator();
				    while (it2.hasNext()) {
				        Map.Entry<String, TraitConcept> pair = (Map.Entry<String, TraitConcept>)it2.next();
						addParent(m, (TraitConcept)pair.getValue(), myScheme, myDate, myCreator);
				        it2.remove(); // avoids a ConcurrentModificationException
				    }
					//create file
					Model model = ModelFactory.createDefaultModel();
					model.setNsPrefix("skos", ThesauformConfiguration.skos);
					model.setNsPrefix("skosxl", ThesauformConfiguration.skosXL);
					model.setNsPrefix("dc", ThesauformConfiguration.dc);
					model.setNsPrefix("owl", ThesauformConfiguration.owl);
					model.setNsPrefix("rdfs", ThesauformConfiguration.rdfs);
					model.setNsPrefix("rdf", ThesauformConfiguration.rdf);
					model.setNsPrefix("dct", ThesauformConfiguration.dct);
					model.setNsPrefix("foaf", ThesauformConfiguration.foaf);
					model.setNsPrefix("xsd", ThesauformConfiguration.xsd);
					model.setNsPrefix("vs", ThesauformConfiguration.vs);
					model.setNsPrefix("trait", ThesauformConfiguration.term_uri + ThesauformConfiguration.uriTraitTmp);
					model.setNsPrefix("change", ThesauformConfiguration.term_uri + ThesauformConfiguration.uriChange);
					model.setNsPrefix("ref", ThesauformConfiguration.term_uri + ThesauformConfiguration.uriRef);
					model.setNsPrefix("unit", ThesauformConfiguration.term_uri + ThesauformConfiguration.uriUnit);
					model.add(m.getModel());
					try {
						String file = "";
						if(ThesauformConfiguration.database) {
							file = ThesauformConfiguration.data_file;
						}
						else {
							file = getServletContext().getRealPath(ThesauformConfiguration.data_file);
						}
						FileOutputStream ost = new FileOutputStream(file);
						model.write(ost, "RDF/XML" ); 
					}
					catch (FileNotFoundException e) {
							System.err.println("file not found : "+e);
					}
					model.close();
					m.close();
				}
			}
			else {
				//if no root or more than one root, throw exception
				if(rootList.size()==0) {
					in.close();
					throw new Exception(EMPTY_ROOT_MESSAGE);
				}
				else {
					in.close();
					throw new Exception(MULTI_ROOTS_MESSAGE);
				}
			}
			in.close();
		}
		catch (Exception e) {
			//TODO better management of exceptions
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return(return_value);		
	}
	
	/**
	 * Test infinite loop
	 * @param hierarchyMap
	 * @return boolean
	 * @throws Exception 
	 */
	private boolean infinite_loop(HashMap<String, List<String>> hierarchyMap,List<String> visitedList,String root, String last_root) throws Exception {
		boolean return_value = false;
		//route through the tree, check if node are visited twice
		try {
			if(visitedList.contains(root)) {
				//test if real cycle for this node
				List<String> visitedNodeList = new ArrayList<String>();
				isRealCycle(last_root,hierarchyMap,visitedNodeList);
			}
			else {
				//test if root belong to the hierarchy
				visitedList.add(root);
				List<String> sonsList = hierarchyMap.get(root);
				if(sonsList!=null) {
					for (String son : sonsList) {
						return_value = infinite_loop(hierarchyMap, visitedList, son, root);
					}
				}
				else {
					//it is a leaf do nothing
				}
			}
			
		} catch (Exception e) {
			if(e.getMessage().equals("cycle"))
			{
				return_value = true;
			}
			else {
				System.err.println(e.getLocalizedMessage());
				e.printStackTrace();

			}
		}
		return return_value;
	}

	private List<String> isRealCycle(String root, HashMap<String, List<String>> hierarchyMap, List<String> visitedList) throws Exception {
		visitedList.add(root);
		if(hierarchyMap.containsKey(root)) {
			for (String son : hierarchyMap.get(root)) {
				if(!visitedList.contains(son))
				{
					visitedList = isRealCycle(son, hierarchyMap, visitedList);
				}
				else {
					throw new Exception("cycle");
				}
			}
		}
		return(visitedList);
	}

	/**
	 * delete file from system
	 * @return true if success
	 */
	protected boolean deleteFile(Path filePath) {
		Boolean return_value = false;
		try {
		    Files.delete(filePath);
		    return_value = true;
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", filePath);
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", filePath);
		} catch (IOException x) {
		    // File permission problems are caught here.
		    System.err.println(x);
		}
		return(return_value);	
	}
	
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Create path components to save the file
		String path = "";
		if(!ThesauformConfiguration.database) {
			//copy in war deployment data directory
			String internalDataPath = File.separator +"WEB-INF"+ File.separator + "data";
			String absoluteDataPath = getServletContext().getRealPath(internalDataPath);
			path = absoluteDataPath;
		} else {
			//copy in custom repository
			path = ThesauformConfiguration.database_path;
		}
		Part filePart = request.getPart("my_file");
		String fileName = getFileName(filePart);
		OutputStream out = null;
		InputStream filecontent = null;
		response.setContentType("text/html;charset=UTF-8");
		try {
			//get parameters
			boolean merge = false;
			if(request.getParameterMap().containsKey(MERGE_PARAMETER) && !request.getParameter(MERGE_PARAMETER).isEmpty()) {
				merge = true;
			}
			//create file on server
			File myFile = new File(path + File.separator + fileName);
			out = new FileOutputStream(myFile);
			filecontent = filePart.getInputStream();
			int read = 0;
			final byte[] bytes = new byte[1024];
			while ((read = filecontent.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			//file successfully copied
			//check uploaded content
			if(checkFile(myFile)) {
				//insert the new file
				insertFile(myFile,merge);
			}
			else {
				//delete file
				if(!deleteFile(myFile.toPath())){
					throw new Exception("Delete file failed");
				}
			}
		} catch (FileNotFoundException fne) {
			System.err.println(fne.getMessage());


		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();


		} finally {
			if (out != null) {
				out.close();
			}
			if (filecontent != null) {
				filecontent.close();
			}
		}
	}

	private String getFileName(final Part part) {
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//do treatment
		this.getServletContext().getRequestDispatcher(VUE_SUCCESS).forward(request, response);
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// manage errors messages
		Map<String, String> errors = new HashMap<>();
		errors.put(TYPE_ERROR, "");
		try {
			// do treatment
			processRequest(request, response);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			request.setAttribute(USER_ERROR_PARAMETER, errors);
			this.getServletContext().getRequestDispatcher(VUE_FAILED).forward(request, response);
		}
	}
}
