package thesauform.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public final class ThesauformConfiguration {

	private static Properties prop = null;
	public static Boolean database;
	public static String database_path;
	public static String tab_title;
	public static String data_file;
	public static String data_file_tmp;
	public static String public_data_file;
	public static String person_file;
	public static String person_file_tmp;
	public static String term_uri;
	public static String person_uri;
	public static String super_root;
	public static String term_root;
	public static String software_title;
	public static String explanation_title;
	public static String contact_information;
	public static String contact_mail;
	public static String trait_display;
	public static String logos;
	public static String logo;
	public static String facet_file;
	public static String facet_list;
	
	//exception
	public static final String WRONG_DATABASE_CONF_PARAM = "Wrong database configuration parameter (true/false)";
	public static final String WRONG_OPTION_CONF_PARAM = "Wrong display option configuration parameter (unit,realName,categoriesList)";

	// global constant
	public static final String NL = System.getProperty("line.separator");
	
	public static final String USR_SESSION = "userSession";
	public static final String VOTE_RANK = "expert";
	public static final String ADMIN_RANK = "admin";
	public static final String TRAIT_PFX = "trait";
	public static final String CHANGE_PFX = "change";
	public static final String REF_PFX = "ref";
	public static final String UNIT_PFX = "unit";
	public static final String PERSON_PFX = "user";

	//query prefix
	public static final String skos = "http://www.w3.org/2004/02/skos/core#";
	public static final String skosXL = "http://www.w3.org/2008/05/skos-xl#";
	public static final String vs = "http://www.w3.org/2003/06/sw-vocab-status/ns#";
	public static final String dc = "http://purl.org/dc/elements/1.1/";
	public static final String foaf = "http://xmlns.com/foaf/0.1/";
	public static final String xsd = "http://www.w3.org/2001/XMLSchema#";
	public static final String owl = "http://www.w3.org/2002/07/owl#";
	public static final String dct = "http://purl.org/dc/terms/";
	public static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";


	public static final String uriTrait = "/elements/1.0/";
	public static final String uriTraitTmp = "#";
	public static final String uriUnit = "/unit#";

	//changeVoc
	public static final String uriChange = "/change#";
	//refVoc
	public static final String uriRef = "/reference#";
	// authentication constant
	public static final String VUE_FAILED = "/WEB-INF/scripts/authenticationThesauform.jsp";
	public static final String PARAMETER_MODE = "action";
	public static final String MODE_ANNOTATION = "annotation";
	public static final String MODE_VOTE = "expert";
	public static final String MODE_ADMINISTRATION = "administration";
	public static final String VUE_SUCCESS_ANNOTATION = "/annotationModification";
	public static final String VUE_SUCCESS_VOTE = "/expert";
	public static final String VUE_SUCCESS_ADMINISTRATION = "/administration";
	public static final String ERROR_MSG = "Connection failed : ";
	public static final String FORM_ERROR = "errors";
	public static final String FORM_NAME = "name";
	public static final String FORM_PASS = "pw";
	public static final String FORM_STATUS = "status";
	public static final String ERROR_STATUS = "identification failed";
	// AnnotationForm
	public static final String ERROR_MESSAGE_USER = "User empty";
	public static final String ERROR_MESSAGE_SESSION = "Session empty";
	public static final String DATABASE = "_database_";
	public static final String TRAIT_FILE = "_data_file_";
	public static final String PERSON_FILE = "_person_file_";
	public static final String PERSON_FILE_TMP = "person_file_tmp";
	// hierarchyVisualization
	public static final String GET_VIZ = "viz";
	// cacheClear
	public static final String CACHE_ERROR = "cache_error";
	public static final String CACHE_ERROR_PARAMETER = "errors";
	public static final String CACHE_MESSAGE = "cache flushing failed";

	private static String filename = "thesauform.properties";

	public static Logger thesauform_logger;

	/**
	 * Constructor
	 */
	public ThesauformConfiguration() {
		thesauform_logger = LogManager.getLogger(getClass());
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = getClass().getClassLoader().getResourceAsStream(ThesauformConfiguration.filename);
			if (input == null) {
				thesauform_logger.error("No file " + ThesauformConfiguration.filename);
			}
			prop.load(input);
			String databaseString = prop.getProperty("database");
			if(databaseString!=null&&databaseString!="") {
				database = true;
				database_path = databaseString;
			}
			else {
				database = false;
				input.close();
				throw new Exception( WRONG_DATABASE_CONF_PARAM );
			}
			tab_title = prop.getProperty("tab_title");
			if(database) {
				data_file = database_path + prop.getProperty("data_file");
			}
			else {
				data_file = prop.getProperty("data_file");
			}
			if(database) {
				data_file_tmp = database_path + prop.getProperty("data_file_tmp");
			}
			else {
				data_file_tmp = prop.getProperty("data_file_tmp");
			}
			if(database) {
				if(!prop.getProperty("public_data_file").isEmpty()) {
					public_data_file = database_path + prop.getProperty("public_data_file");
				}
				else {
					public_data_file = "";
				}
			}
			else {
				public_data_file = prop.getProperty("public_data_file");
			}
			if(database) {
				person_file = database_path + prop.getProperty("person_file");
			}
			else {
				person_file = prop.getProperty("person_file");
			}
			if(database) {
				person_file_tmp = database_path + prop.getProperty("person_file_tmp");
			}
			else {
				person_file_tmp = prop.getProperty("person_file_tmp");
			}
			term_uri = prop.getProperty("term_uri");
			person_uri = prop.getProperty("person_uri");
			super_root = prop.getProperty("super_root");
			term_root = prop.getProperty("term_root");
			software_title = prop.getProperty("software_title");
			explanation_title = prop.getProperty("explanation_title");
			contact_information = prop.getProperty("contact_information");
			contact_mail = prop.getProperty("contact_mail");
			String traitDisplayString = prop.getProperty("trait_display");
			if(traitDisplayString == null||traitDisplayString == "") {
				//do nothing
			}
			else {
				String[] traitDisplayArray = traitDisplayString.split(",");
				for (String display : traitDisplayArray) {
					if(display.equals("unit")||display.equals("realName")||display.equals("categoriesList")) {

					}else {
						throw new Exception( WRONG_OPTION_CONF_PARAM );
					}
				}
			}
			trait_display = prop.getProperty("trait_display");
			logos = prop.getProperty("logos");
			logo = prop.getProperty("logo_header");
			if(database) {
				facet_file = database_path + prop.getProperty("facet_file");
			}
			else {
				facet_file = prop.getProperty("facet_file");
			}
			if(prop.getProperty("facet_list")!="") {
				facet_list = prop.getProperty("facet_list");
			}
			else {
				facet_list = "";
			}
			ThesauformConfiguration.prop = prop;
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getMyProperty(String key) {
		return (ThesauformConfiguration.prop.getProperty(key));
	}
	
	/**
	 * Update property
	 */
	public boolean setMyProperty(String key, String value) {
		boolean success = false;
		try {
			ThesauformConfiguration.prop.setProperty(key, value);
			URL resource = getClass().getClassLoader().getResource(ThesauformConfiguration.filename);
			BufferedWriter out = new BufferedWriter(new FileWriter(Paths.get(resource.toURI()).toFile()));
			ThesauformConfiguration.prop.store(out, null);
			out.close();
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}
	
}
