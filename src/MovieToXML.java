import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This java class is meant to work with the XMLElement class 
 * which has also been submitted with ps4
 * 
 * @author Michael Patterson
 *
 */
public class MovieToXML {

	public static void main(String[] args) {
		
		Scanner userDB = new Scanner(System.in); 

		try {
			/* Get db name from user */
			System.out.print("(q to quit) Enter the name of the database file : ");
			String dbName = userDB.nextLine();
			
			 if (dbName.equalsIgnoreCase("q"))
                 System.exit(0);
					
			/* Connect to sqlite database */
			Class.forName("org.sqlite.JDBC");
			Connection db = DriverManager.getConnection("jdbc:sqlite:" + dbName); 

			// Create movie.xml
		
			System.out.println(writeMovieXML(db));
			
						
			// Create people.xml
			System.out.println(writePeopleXML(db));
			
			// Create oscars.sml
			System.out.println(writeOscarsXML(db));
			
			userDB.close();			
			db.close();
		
		} catch (ClassNotFoundException e) {
	
			String errMsg = e.getMessage();
			System.err.println(errMsg); 

		} catch (SQLException e) {
		
			String errMsg = e.getMessage();
			System.err.println(errMsg); 
			main(null);

		} catch (Exception e) {
			
			String errMsg = e.getMessage();
			System.err.println(errMsg); 
			main(null);
		}

	}
	
	/**
	 * Private class that opens the xmL file, performs
	 * the queries, create the elements and 
	 * write them to the movies.xml file
	 * @param db The database that is being queried
	 * @return a success message
	 * @throws Exception
	 */
	private static String writeMovieXML(Connection db) throws Exception{
		
		/*Create and open new movie.xml file*/
		PrintStream movieFile = new PrintStream("movies.XML");
		movieFile.println("<?xml version=\"1.0\"?>");
		movieFile.println("<movies>");
	
	
		/* Get movies */
		Statement stmt = db.createStatement();
		ResultSet results = stmt.executeQuery("SELECT * FROM Movie;");
		ResultSetMetaData rsmd = results.getMetaData();
		
		/* Create movie XMLElements */
		while (results.next()){
			XMLElement movie = new XMLElement ("movie", null , false, 1);
			movie.addAttribute("id", "M" + results.getString(1));
			
			ArrayList<XMLElement> children = new ArrayList<XMLElement>();
			/* Create children elements if needed */
			for (int i=2; i < 8; i++){
				if (results.getString(i) != null){
					XMLElement child = new XMLElement (rsmd.getColumnLabel(i), results.getString(i), false, 2);
					children.add(child);
				}
			}
			
			/*Add Directors Attribute*/
			Statement dir = db.createStatement();
			ResultSet directors = dir.executeQuery("SELECT director_id FROM Director "
					+ "WHERE movie_id = \"" + results.getString(1) + "\"" );
			
			writePersonAtt("director", movie, directors);
				
			/*Add Actors Attribute */
			Statement  act= db.createStatement();
			ResultSet actors = act.executeQuery("SELECT actor_id FROM Actor "
					+ "WHERE movie_id = \"" + results.getString(1) + "\"");
			writePersonAtt("actors", movie, actors);
			
			/*Add Oscars Attribute */
			Statement osc = db.createStatement();
			ResultSet oscars = osc.executeQuery("SELECT year, person_id FROM Oscar"
					+ " WHERE movie_id = \"" + results.getString(1)+ "\"");
			writeOscarAtt("oscars", movie, oscars);
						
			/*write movie and it's children to the file*/
			movieFile.println(movie.getOpenTag());
			for (XMLElement child : children){
				movieFile.println(child.inlineElement());
			}
			movieFile.println(movie.getCloseTag());
		}
		movieFile.println("</movies>");
		
		movieFile.close();
		
		return "movies.xml has been written.";
	}
	
	private static String writePeopleXML(Connection db) throws Exception{
		
		/*Create and open new xml file*/
		PrintStream peopleFile = new PrintStream("people.XML");
		peopleFile.println("<?xml version=\"1.0\"?>");
		peopleFile.println("<people>");
		
		/* Get People results */
		Statement stmt = db.createStatement();
		ResultSet results = stmt.executeQuery("SELECT * FROM Person;");
		ResultSetMetaData rsmd = results.getMetaData();
		
		/* Create person XMLElements */
		while (results.next()){
			XMLElement person = new XMLElement ("person", null , false, 1);
			person.addAttribute("id", "P" + results.getString(1));
			
			ArrayList<XMLElement> children = new ArrayList<XMLElement>();
			/* Create children elements if needed */
			for (int i=2; i < 5; i++){
				if (results.getString(i) != null){
					XMLElement child = new XMLElement (rsmd.getColumnLabel(i), results.getString(i), false, 2);
					children.add(child);
				}
			}
			
			/*Add directed Attribute*/
			Statement dir = db.createStatement();
			ResultSet directed = dir.executeQuery("SELECT movie_id FROM Director "
					+ "WHERE director_id = \"" + results.getString(1) + "\"" );
			
			writeMovieAtt("directed", person, directed);
				
			/*Add actedIn Attribute */
			Statement  mov= db.createStatement();
			ResultSet movie = mov.executeQuery("SELECT movie_id FROM Actor "
					+ "WHERE actor_id = \"" + results.getString(1) + "\"");
			writeMovieAtt("actedIn", person, movie);
			
			/*Add Oscars Attribute */
			Statement osc = db.createStatement();
			ResultSet oscars = osc.executeQuery("SELECT year, person_id FROM Oscar"
					+ " WHERE person_id = \"" + results.getString(1)+ "\"");
			writeOscarAtt("oscars", person, oscars);
						
			/*write people element and it's children to the file*/
			peopleFile.println(person.getOpenTag());
			for (XMLElement child : children){
				peopleFile.println(child.inlineElement());
			}
			peopleFile.println(person.getCloseTag());
		}
		
		peopleFile.println("</people>");
		
		peopleFile.close();
		
		
		return "people.xml has been written";
	}
	
	private static String writeOscarsXML(Connection db) throws Exception{
		
		/*Create and open new oscars.xml file*/
		PrintStream oscarFile = new PrintStream("oscars.XML");
		oscarFile.println("<?xml version=\"1.0\"?>");
		oscarFile.println("<oscars>");
	
	
		/* Get oscars */
		Statement stmt = db.createStatement();
		ResultSet results = stmt.executeQuery("SELECT * FROM Oscar;");
		ResultSetMetaData rsmd = results.getMetaData();
		
		/* Create oscar XMLElements */
		while (results.next()){
			XMLElement oscar = new XMLElement ("oscar", null , false, 1);
			String oscarID;
			if (results.getString(2) == null ){
				oscarID = "O" + results.getString(4) + "0000000"; 
			}else{
				oscarID = "O" + results.getString(4) + results.getString(2);
			}
			oscar.addAttribute("id", oscarID);
			
			ArrayList<XMLElement> children = new ArrayList<XMLElement>();
			/* Create children elements if needed */
			for (int i=3; i < 5; i++){
				if (results.getString(i) != null){
					XMLElement child = new XMLElement (rsmd.getColumnLabel(i), results.getString(i), false, 2);
					children.add(child);
				}
			}
			
			/*Add movie Attribute*/
			oscar.addAttribute("movie_id", "M"+results.getString(1));
				
			/*Add actor Attribute */
			if (results.getString(2) != null){
				oscar.addAttribute("person_id", "P"+results.getString(2));
			}
						
			/*write oscar element and it's children to the file*/
			oscarFile.println(oscar.getOpenTag());
			for (XMLElement child : children){
				oscarFile.println(child.inlineElement());
			}
			oscarFile.println(oscar.getCloseTag());
		}
		oscarFile.println("</oscars>");
		
		oscarFile.close();
		
		return "oscar.xml has been written";
	}
	
	/**
	 * Helper class to write an person attribute if needed
	 * @param name name of the attribute
	 * @param element element that the attribute will be added to
	 * @param rs ResultSet than includes the values of the attribute
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void writePersonAtt (String name, XMLElement element, ResultSet rs) throws SQLException, Exception{
		if (rs.next()){
			element.addAttribute(name, "P"+rs.getString(1));
			while (rs.next()){
				element.appendAttribute(name, "P"+rs.getString(1));
			}
		}
	}
	
	/**
	 Helper class to write an person attribute if needed
	 * @param name name of the attribute
	 * @param element element that the attribute will be added to
	 * @param rs ResultSet than includes the values of the attribute
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void writeMovieAtt (String name, XMLElement element, ResultSet rs) throws SQLException, Exception{
		if (rs.next()){
			element.addAttribute(name, "M"+rs.getString(1));
			while (rs.next()){
				element.appendAttribute(name, "M"+rs.getString(1));
			}
		}
	}
	
	/**
	 * Helper class to write an Oscar attribute if needed
	 * @param name  Name of the attribute
	 * @param element  the element that the attribute will be added to
	 * @param rs  the result set that includes the values of the attributes
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void writeOscarAtt (String name, XMLElement element, ResultSet rs) throws SQLException, Exception{
		String oscAtt = "";
		
		if (rs.next()){
			if (rs.getString(2) == null){
				oscAtt = "O" + rs.getString(1) + "0000000"; 
			}else{
				oscAtt = "O" + rs.getString(1) + rs.getString(2);
			}
			element.addAttribute(name, oscAtt);
			while (rs.next()){
				if (rs.getString(2) == null){
					oscAtt = "O" + rs.getString(1) + "0000000"; 
				}else{
					oscAtt = "O" + rs.getString(1) + rs.getString(2);
				}
				element.appendAttribute(name,oscAtt);
			}
		}
	}
	
	

}
