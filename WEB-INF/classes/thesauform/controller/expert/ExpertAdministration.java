package thesauform.controller.expert;

import java.io.IOException;

import thesauform.beans.Person;
import thesauform.model.SkosTraitModel;
import thesauform.model.ThesauformConfiguration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class servletExpertAdministration
 */
@WebServlet("/expert")
public class ExpertAdministration extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8930352223539579557L;

	public static final String VUE_SUCCESS = "/WEB-INF/scripts/expertAdministration.jsp";
	private static final String PERSON_FILE = "person_file";
	private static final String TYPE_INSERT = "Insert";
	private static final String TYPE_DELETE = "Delete";
	private static final String TYPE_UPDATE = "Update";
	private static final String FORM_INSERTED = "my_inserted_list";
	private static final String FORM_DELETED = "my_deleted_list";
	private static final String FORM_UPDATED = "my_updated_list";
	private static final String ERROR = "error";
	private static final String ERROR_MESSAGE_MODIF_TYPE = "Unknow modification type";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// manage errors messages
		Map<String, String> errors = new HashMap<>();
		// trait model
		SkosTraitModel traitModel = null;
		// test if a session is initialized
		HttpSession session = request.getSession(false);
		if (session != null) {
			if (session.getAttribute(ThesauformConfiguration.USR_SESSION) instanceof Person) {
				Person user = (Person) session.getAttribute(ThesauformConfiguration.USR_SESSION);
				boolean authentificationStatus = user.getAuthenticated();
				if (authentificationStatus) {
					if(ThesauformConfiguration.database) {
						session.setAttribute(PERSON_FILE, ThesauformConfiguration.person_file);
					}
					else {
						session.setAttribute(PERSON_FILE, getServletContext().getRealPath(ThesauformConfiguration.person_file));
					}
					// do treatment
					try {
						// set file
						if(ThesauformConfiguration.database) {
							traitModel = new SkosTraitModel(ThesauformConfiguration.data_file);
						}
						else {
							traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.data_file));
						}
						// pass list of all name of modified trait to the view
						Map<String, List<String>> traitMap = traitModel.getAllTraitWithAnn();
						List<String> insertedTraitList;
						List<String> modifiedTraitList;
						List<String> deletedTraitList;
						Iterator<Entry<String, List<String>>> allTraitIterator = traitMap.entrySet().iterator();
						if (allTraitIterator.hasNext()) {
							while (allTraitIterator.hasNext()) {
								Entry<String, List<String>> traitPair = allTraitIterator.next();
								List<String> traitList = traitPair.getValue();
								String typeModif = traitPair.getKey();
								Iterator<String> traitIterator = traitList.iterator();
								List<String> tmpList;
								String namePara;
								if (typeModif.equals(TYPE_INSERT)) {
									insertedTraitList = traitList;
									tmpList = insertedTraitList;
									namePara = FORM_INSERTED;
								} else {
									if (typeModif.equals(TYPE_DELETE)) {
										deletedTraitList = traitList;
										tmpList = deletedTraitList;
										namePara = FORM_DELETED;
									} else {
										if (typeModif.equals(TYPE_UPDATE)) {
											modifiedTraitList = traitList;
											tmpList = modifiedTraitList;
											namePara = FORM_UPDATED;
										} else {
											throw new Exception(ERROR_MESSAGE_MODIF_TYPE + " " + typeModif);
										}
									}
								}
								if (traitIterator.hasNext()) {
									Collections.sort(tmpList);
									request.setAttribute(namePara, tmpList);
								} else {
									// do nothing view will check if empty
								}
							}
						} else {
							throw new Exception();
						}
					} catch (Exception e) {
						// set errors messages
						errors.put(ERROR, e.getMessage());
					}
					this.getServletContext().getRequestDispatcher(VUE_SUCCESS).forward(request, response);
				} else {
					// re-authenticate
					this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
				}
			} else {
				// re-authenticate
				this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
			}
		} else {
			// re-authenticate
			this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// manage errors messages
		Map<String, String> errors = new HashMap<>();
		// trait model
		SkosTraitModel traitModel = null;
		// test if a session is initialized
		HttpSession session = request.getSession(false);
		if (session != null) {
			if (session.getAttribute(ThesauformConfiguration.USR_SESSION) instanceof Person) {
				Person user = (Person) session.getAttribute(ThesauformConfiguration.USR_SESSION);
				boolean authentificationStatus = user.getAuthenticated();
				if (authentificationStatus) {
					if(ThesauformConfiguration.database) {
						session.setAttribute(PERSON_FILE, ThesauformConfiguration.person_file);
					}
					else {
						session.setAttribute(PERSON_FILE, getServletContext().getRealPath(ThesauformConfiguration.person_file));
					}
					// do treatment
					try {
						// set protected file
						if(ThesauformConfiguration.database) {
							traitModel = new SkosTraitModel(ThesauformConfiguration.data_file);
						}
						else {
							traitModel = new SkosTraitModel(getServletContext().getRealPath(ThesauformConfiguration.data_file));
						}
						// pass list of all name of modified trait to the view
						Map<String, List<String>> traitMap = traitModel.getAllTraitWithAnn();
						List<String> insertedTraitList;
						List<String> modifiedTraitList;
						List<String> deletedTraitList;
						Iterator<Entry<String, List<String>>> allTraitIterator = traitMap.entrySet().iterator();
						if (allTraitIterator.hasNext()) {
							while (allTraitIterator.hasNext()) {
								Entry<String, List<String>> traitPair = allTraitIterator.next();
								List<String> traitList = traitPair.getValue();
								String typeModif = traitPair.getKey();
								Iterator<String> traitIterator = traitList.iterator();
								List<String> tmpList;
								String namePara;
								if (typeModif.equals(TYPE_INSERT)) {
									insertedTraitList = traitList;
									tmpList = insertedTraitList;
									namePara = FORM_INSERTED;
								} else {
									if (typeModif.equals(TYPE_DELETE)) {
										deletedTraitList = traitList;
										tmpList = deletedTraitList;
										namePara = FORM_DELETED;
									} else {
										if (typeModif.equals(TYPE_UPDATE)) {
											modifiedTraitList = traitList;
											tmpList = modifiedTraitList;
											namePara = FORM_UPDATED;
										} else {
											throw new Exception(ERROR_MESSAGE_MODIF_TYPE + " " + typeModif);
										}
									}
								}
								if (traitIterator.hasNext()) {
									request.setAttribute(namePara, tmpList);
								} else {
									// do nothing view will check if empty
								}
							}
						} else {
							throw new Exception();
						}
					} catch (Exception e) {
						// set errors messages
						errors.put(ERROR, e.getMessage());
					}
					this.getServletContext().getRequestDispatcher(VUE_SUCCESS).forward(request, response);
				} else {
					// re-authenticate
					this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
				}
			} else {
				// re-authenticate
				this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
			}
		} else {
			// re-authenticate
			this.getServletContext().getRequestDispatcher(ThesauformConfiguration.VUE_FAILED).forward(request, response);
		}
	}
}
