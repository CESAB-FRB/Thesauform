package thesauform.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Manage personal vote
 * 
 * @author Baptiste
 */
public class VotesModel {

	protected static String NL = System.getProperty("line.separator");
	protected ThesauformConfiguration conf = new ThesauformConfiguration();
	public static final String ERROR_TRAIT_EMPTY = "No trait given";
	public static final String ERROR_PROP_EMPTY = "No propriety given";
	public static final String ERROR_PERSON_EMPTY = "No person given";
	public static final String ERROR_VALUE_EMPTY = "No value given";
	public static final String ERROR_DATABASE_EMPTY = "No trait dabatabase given";
	public static final String ERROR_TMP_DATABASE_EMPTY = "No trait temporary dabatabase given";
	public static final String ERROR_MODEL_EMPTY = "No model given";
	public static final String ERROR_TRAIT_FILE = "problem writing in trait database";
	public static final String ERROR_TRAIT_TMP_FILE = "problem writing in trait temporary database";
	public static final String ERROR_TRAIT_FILE_NOT_EMPTY = "the file cache is not empty";
	public static final String ERROR_ADD_VOTE = "adding vote failed";
	public static final String ERROR_DEL_VOTE = "deleting vote failed";
	private SkosModel myModel;
	private String traitName;
	private String property;
	private String person;
	private String value;
	private String database;
	private String tmp_database;

	/**
	 * Constructor get all votes in a list for a trait
	 * 
	 * @param myModel
	 * @param traitFile
	 * @param traitTmpFile
	 * @param traitName
	 * @param property
	 * @param person
	 * @param value
	 */
	public VotesModel(SkosModel myModel, String traitFile, String traitTmpFile, String traitName, String property,
			String person, String value) {
		try {
			// initialize properties
			if (!(traitName == null || traitName.isEmpty())) {
				this.traitName = traitName;
			} else {
				throw new Exception(ERROR_TRAIT_EMPTY);
			}
			if (!(property == null || property.isEmpty())) {
				this.property = property;
			} else {
				throw new Exception(ERROR_PROP_EMPTY);
			}
			if (!(person == null || person.isEmpty())) {
				this.person = person;
			} else {
				throw new Exception(ERROR_PERSON_EMPTY);
			}
			if (!(value == null || value.isEmpty())) {
				this.value = value;
			} else {
				throw new Exception(ERROR_VALUE_EMPTY);
			}
			if (!(traitFile == null || traitFile.isEmpty())) {
				this.database = traitFile;
			} else {
				throw new Exception(ERROR_DATABASE_EMPTY);
			}
			if (!(traitTmpFile == null || traitTmpFile.isEmpty())) {
				this.tmp_database = traitTmpFile;
			} else {
				throw new Exception(ERROR_TMP_DATABASE_EMPTY);
			}
			if (!(myModel == null)) {
				this.myModel = myModel;
			} else {
				throw new Exception(ERROR_MODEL_EMPTY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test existence of a personal vote
	 * 
	 * @return
	 * @throws java.lang.Exception
	 */
	public boolean existsVote() throws Exception {
		boolean returnVal = false;
		if (myModel.existVote(this.traitName, this.property, this.person, this.value) != null) {
			returnVal = true;
		}
		return (returnVal);
	}

	/**
	 * Add vote
	 * 
	 * @return
	 * @throws java.lang.Exception
	 */
	public Integer addVote() throws Exception {
		Integer cptVote = null;
		if (myModel.addVote(this.traitName, this.property, this.person, this.value)) {
			this.saveModel();
			cptVote = this.countVote();
		} else {
			throw new Exception(ERROR_ADD_VOTE);
		}
		return (cptVote);
	}

	/**
	 * Delete vote
	 */
	public Integer deleteVote() throws Exception {
		Integer cptVote = null;
		if (myModel.delVote(this.traitName, this.property, this.person, this.value)) {
			this.saveModel();
			cptVote = this.countVote();
		} else {
			throw new Exception(ERROR_ADD_VOTE);
		}
		return (cptVote);
	}

	/**
	 * Get vote number for a propriety
	 */
	public Integer countVote() throws Exception {
		Integer cptVote = null;
		cptVote = myModel.countVote(this.traitName, this.property, this.value);
		return (cptVote);
	}

	/**
	 * Save the model
	 * 
	 * @param personModel
	 * @return
	 */
	public boolean saveModel() throws Exception {
		boolean success = false;
		// export result in temporary file if empty (simulate transaction)
		BufferedReader br = new BufferedReader(new FileReader(this.tmp_database));
		if (br.readLine() == null) {
			// test if model not null
			if (this.myModel != null) {
				this.myModel.save(this.tmp_database);
				// test that file is working
				SkosModel testModel = new SkosModel(this.tmp_database);
				try {
					testModel.getAllTraitWithAnn();
				} catch (Exception e) {
					br.close();
					throw new Exception(ERROR_TRAIT_TMP_FILE);
				}
				testModel.close();
				// save it in real data file
				try {
					this.myModel.save(this.database);
					// clean temporary file
					BufferedWriter fstream = new BufferedWriter(new FileWriter(this.tmp_database));
					BufferedWriter out = new BufferedWriter(fstream);
					out.write("");
					out.close();
					fstream.close();
				} catch (Exception e) {
					br.close();
					throw new Exception(ERROR_TRAIT_FILE);
				}
			}
		} else {
			br.close();
			throw new Exception(ERROR_TRAIT_FILE_NOT_EMPTY);
		}
		br.close();
		return success;
	}

}
