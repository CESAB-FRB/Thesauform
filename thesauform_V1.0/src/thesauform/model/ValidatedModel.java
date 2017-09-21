package thesauform.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Manage validation
 * 
 * @author Baptiste
 */
public class ValidatedModel {

	protected static String NL = System.getProperty("line.separator");
	protected ThesauformConfiguration conf = new ThesauformConfiguration();
	public static final String ERROR_TRAIT_EMPTY = "No trait given";
	public static final String ERROR_PROP_EMPTY = "No propriety given";
	public static final String ERROR_VALUE_EMPTY = "No value given";
	public static final String ERROR_DATABASE_EMPTY = "No trait dabatabase given";
	public static final String ERROR_TMP_DATABASE_EMPTY = "No trait temporary dabatabase given";
	public static final String ERROR_MODEL_EMPTY = "No model given";
	public static final String ERROR_TRAIT_FILE = "problem writing in trait database";
	public static final String ERROR_TRAIT_TMP_FILE = "problem writing in trait temporary database";
	public static final String ERROR_TRAIT_FILE_NOT_EMPTY = "the file cache is not empty";
	public static final String ERROR_ADD_VAL = "adding validated failed";
	public static final String ERROR_ADD_INVAL = "adding invalidated failed";
	public static final String ERROR_DEL_VAL = "deleting validated failed";
	public static final String ERROR_DEL_INVAL = "deleting invalidated failed";
	private SkosModel myModel;
	private String traitName;
	private String property;
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
	public ValidatedModel(SkosModel myModel, String traitFile, String traitTmpFile, String traitName, String property, String value) {
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
	 * Add validated tag
	 * 
	 * @return
	 * @throws java.lang.Exception
	 */
	public boolean addValidated() throws Exception {
		boolean returnVal = false;
		if (myModel.addValidated(traitName, property, value)) {
			this.saveModel();
			returnVal = true;
		} else {
			//throw new Exception(ERROR_ADD_VAL);
		}
		return (returnVal);
	}

	/**
	 * Add invalidated tag
	 * 
	 * @return
	 * @throws java.lang.Exception
	 */
	public boolean addInvalidated() throws Exception {
		boolean returnVal = false;
		if (myModel.addInvalidated(traitName, property, value)) {
			this.saveModel();
			returnVal = true;
		} else {
			throw new Exception(ERROR_ADD_INVAL);
		}
		return (returnVal);
	}

	/**
	 * Remove validated tag
	 */
	public boolean deleteValidated() throws Exception {
		boolean returnVal = false;
		if (myModel.deleteValidated(traitName, property, value)) {
			this.saveModel();
			returnVal = true;
		} else {
			throw new Exception(ERROR_DEL_VAL);
		}
		return (returnVal);
	}	

	/**
	 * Remove invalidated tag
	 */
	public boolean deleteInvalidated() throws Exception {
		boolean returnVal = false;
		if (myModel.deleteInvalidated(traitName, property, value)) {
			this.saveModel();
			returnVal = true;
		} else {
			throw new Exception(ERROR_DEL_INVAL);
		}
		return (returnVal);
	}	

	/**
	 * Save the model
	 * 
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
