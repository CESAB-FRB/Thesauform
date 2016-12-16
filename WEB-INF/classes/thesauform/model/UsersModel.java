package thesauform.model;

import thesauform.beans.Person;
import thesauform.model.vocabularies.PersonVoc;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Get users database in memory (map)
 * 
 * @author baptiste
 */
public class UsersModel {

	private Map<String, Person> usersMap;
	private List<Person> usersList;
	public static final String ERROR_USER_EXIST = "user already exists";
	public static final String ERROR_USER_NOT_EXIST = "user does not exist";
	public static final String ERROR_USER_RANK_NOT_MATCH = "user authentification failed";
	public static final String ERROR_USER_EMPTY = "users database is empty";
	public static final String ERROR_PERSON_FILE = "problem writing in person database";
	public static final String ERROR_PERSON_TMP_FILE = "problem writing in person temporary database";
	public static final String ERROR_PERSON_FILE_NOT_EMPTY = "the file cache is not empty";
	private String database;
	private String tmp_database;

	/**
	 * Constructor get all users in a list
	 * 
	 * @param personsFile
	 */
	public UsersModel(String personsFile, String personsTmpFile) {
		this.usersMap = new HashMap<String, Person>();
		this.usersList = new ArrayList<Person>();
		this.database = personsFile;
		this.tmp_database = personsTmpFile;
		try {
			// set model
			SkosPersonModel personModel = new SkosPersonModel(personsFile);
			Map<String, Map<String, String>> personMap = personModel.getAllPersons();
			Iterator<Entry<String, Map<String, String>>> personIterator = personMap.entrySet().iterator();
			if (personIterator.hasNext()) {
				while (personIterator.hasNext()) {
					Entry<String, Map<String, String>> personPair = personIterator.next();
					Map<String, String> personMapTmp = personPair.getValue();
					String name = personPair.getKey();
					String mail = personMapTmp.get("mail");
					String password = personMapTmp.get("password");
					String right = personMapTmp.get("right");
					Person user = new Person();
					user.setName(name);
					user.setMail(mail);
					user.setPassword(password);
					user.setRight(right);
					this.usersMap.put(name, user);
					this.usersList.add(user);
				}
			} else {
				throw new Exception(ERROR_USER_EMPTY);
			}
			personModel.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return all users
	 * 
	 * @return
	 */
	public List<Person> getAllUsers() {
		return (this.usersList);
	}

	/**
	 * Test if a user exists
	 * 
	 * @param user
	 * @return
	 */
	public boolean existUser(Person user) {
		boolean returnVal = false;
		if (this.usersMap.containsKey(user.getName())) {
			returnVal = true;
		}
		return (returnVal);
	}

	/**
	 * Add user
	 * 
	 * @param user
	 * @return
	 * @throws java.lang.Exception
	 */
	public boolean addUser(Person user) throws Exception {
		boolean returnVal = false;
		if (!this.existUser(user)) {
			this.usersMap.put(user.getName(), user);
			SkosPersonModel personModel = new SkosPersonModel(this.database);
			Resource userResource = personModel.createResource();
			userResource.addProperty(RDF.type, FOAF.Person);
			userResource.addProperty(FOAF.name, user.getName());
			userResource.addProperty(FOAF.mbox, user.getMail());
			userResource.addProperty(PersonVoc.password, user.getPassword());
			userResource.addProperty(PersonVoc.right, user.getRight());
			// export result in a file
			this.saveModel(personModel);
			// close model
			personModel.close();
		} else {
			throw new Exception(user.getName() + " " + ERROR_USER_EXIST);
		}
		return (returnVal);
	}

	/**
	 * Delete user
	 * 
	 * @param userName
	 * @return
	 * @throws java.lang.Exception
	 */
	public boolean deleteUser(String userName) throws Exception {
		boolean return_value = false;
		if (this.existUser(this.getUser(userName))) {
			this.usersMap.remove(userName);
			SkosPersonModel personModel = new SkosPersonModel(this.database);
			if (personModel.deleteUser(userName)) {
				// export result in a file
				this.saveModel(personModel);
				return_value = true;
			}
			// close model
			personModel.close();
		} else {
			throw new Exception(userName + " " + ERROR_USER_NOT_EXIST);
		}
		return (return_value);
	}

	/**
	 * Modify user property
	 * 
	 * @param user
	 * @return
	 * @throws java.lang.Exception
	 */
	public boolean modifyUser(Person user) throws Exception {
		boolean returnVal = false;
		if (this.existUser(user)) {
			this.deleteUser(user.getName());
			this.addUser(user);
		} else {
			throw new Exception(user.getName() + " " + ERROR_USER_NOT_EXIST);
		}
		return (returnVal);
	}

	/**
	 * Test the user password
	 * 
	 * @param user
	 * @return
	 * @throws java.lang.Exception
	 */
	public boolean authentifyUser(Person user) throws Exception {
		boolean returnVal = false;
		if (this.existUser(user)) {
			Person db_user = this.usersMap.get(user.getName());
			if ((db_user.getPassword()).equals(user.getPassword())) {
				returnVal = true;
			}
		} else {
			throw new Exception(user.getName() + " " + ERROR_USER_NOT_EXIST);
		}
		return (returnVal);
	}

	/**
	 * Test the user password and rank
	 * 
	 * @param user
	 * @param rank
	 * @return
	 * @throws java.lang.Exception
	 */
	public boolean authentifyUser(Person user, String rank) throws Exception {
		boolean returnVal = false;
		if (this.existUser(user)) {
			Person db_user = this.usersMap.get(user.getName());
			if ((db_user.getPassword()).equals(user.getPassword())) {
				if ((db_user.getRight()).equals(rank)) {
					returnVal = true;
				}
			}
		} else {
			throw new Exception(user.getName() + " " + ERROR_USER_RANK_NOT_MATCH);
		}
		return (returnVal);
	}

	/**
	 * Return db user
	 * 
	 * @param user_name
	 * @return
	 * @throws java.lang.Exception
	 */
	public Person getUser(String user_name) throws Exception {
		Person db_user = null;
		if (this.usersMap.containsKey(user_name)) {
			db_user = this.usersMap.get(user_name);
		} else {
			throw new Exception(user_name + " " + ERROR_USER_NOT_EXIST);
		}
		return (db_user);
	}

	/**
	 * Save the model
	 * 
	 * @param personModel
	 * @return
	 */
	public boolean saveModel(SkosPersonModel personModel) throws Exception {
		boolean success = false;
		// export result in temporary file if empty (simulate transaction)
		BufferedReader br = new BufferedReader(new FileReader(this.tmp_database));
		if (br.readLine() == null) {
			personModel.save(this.tmp_database);
			// test that file is working
			SkosPersonModel testModel = new SkosPersonModel(this.tmp_database);
			try {
				testModel.getAllPersons();
			} catch (Exception e) {
				br.close();
				throw new Exception(ERROR_PERSON_TMP_FILE);
			}
			testModel.close();
			// save it in real data file
			try {
				personModel.save(this.database);
				// clean temporary file
				FileWriter fstream = new FileWriter(this.tmp_database);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write("");
				out.close();
				fstream.close();
				br.close();
			} catch (Exception e) {
				br.close();
				personModel.close();
				throw new Exception(ERROR_PERSON_FILE);
			}
		} else {
			br.close();
			throw new Exception(ERROR_PERSON_FILE_NOT_EMPTY);
		}
		return success;
	}

}
