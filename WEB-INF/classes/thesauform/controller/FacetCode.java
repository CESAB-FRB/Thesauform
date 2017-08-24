package thesauform.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thesauform.model.ThesauformConfiguration;

/**
 * Servlet implementation class facetCode
 */
@WebServlet("/facetCode")
public class FacetCode extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6487314769459745582L;

	protected static final ThesauformConfiguration conf = new ThesauformConfiguration();

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/javascript");
		PrintWriter pw = response.getWriter();
		String js_code = "";
		String[] facet_list_array = ThesauformConfiguration.facet_list.split(",");
		if (facet_list_array.length != 0) {
			js_code += "var item_template = '<div class=\"item\">' + '<h4><%= obj.name %></h4>' + '<p class=\"tags\">' + ";
			for (String facet_tmp : facet_list_array) {
				js_code += "'<% if (obj." + facet_tmp.split(":")[0].replace("'", "") + ") {  %> <%= obj."
						+ facet_tmp.split(":")[0].replace("'", "") + " %><% } %>' +";
			}
			js_code += "'</p>' + '</div>';";
		}
		// create JS function
		js_code += "$(function my_facet(){";
		// create the facet JSON array from file
		String json_facet = new String(
				Files.readAllBytes(Paths.get(getServletContext().getRealPath(ThesauformConfiguration.facet_file))));
		js_code += ("var item =[" + json_facet + "];");
		//// create the plug-in settings
		js_code += "var settings = {items: item,facets: {";
		// read facet from configuration file
		js_code += ThesauformConfiguration.facet_list;
		js_code += "}, resultSelector  : '#results', facetSelector   : '#facets', resultTemplate  : item_template, orderByOptions  : {";
		// set order here it is define in configuration file by apparition order
		js_code += ("'name': 'Name'," + ThesauformConfiguration.facet_list);
		// call the facet plugin0
		js_code += "}};$.facetelize(settings);});";
		js_code += "window.onload = function () {my_facet();};";
		js_code += "$(document).ready(function () {$('#results').on('click', '.item h4', function () {var productId = $(this).html();window.location = \"annotationInfo?viz=1&&trait=\" + productId.replace(\" \", \"_\");});$('.facetitem').on('click', function () {$(this).toggleClass('green');$(\".facetitem\").each(function () {if ($(this).children('span').html().replace(\"(\", \"\").replace(\")\", \"\") == 0) {$(this).removeClass(\"white\");$(this).addClass(\"gris\");} else {$(this).removeClass(\"gris\");$(this).addClass(\"white\");}});});";
		js_code += "$('.deselectstartover').on('click', function () {$(\".facetitem\").each(function () {$(this).addClass(\"white\");$(this).removeClass(\"gris\");});});});";
		pw.println(js_code);
	}
}
