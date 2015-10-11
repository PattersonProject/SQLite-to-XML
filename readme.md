#Java SQLite to XML converter

## Project Description
This set of Java classes prompts the user for the name of a database that contains information on oscar winning movies and converts it to a set of well formed XML files which make up an XML database.  The sqlite database was built by the teaching staff of Harvard Extension CSCI E 66 Database Systems class in the spring of 2015.  It contains 5 tables - Actor, Director, Movie, Oscar and Person. The resulting XML database links the people, oscars and movies using the id parameter of the XML tags. 

##Instructions for Running the program
Clone this repository and from the root directory run the following command:
```java -cp ".bin;../sqlite-jdbc-3.8.11.2.jar" MovieToXML```
	
The source files are in the /scr directory, the movie database is in the root directory.  The XML files will be generated in the root directory. 

##Approach
My approach was to have the main method in MovieToXML build each XML file by instantiating XMLElement objects based on query results. Once an XMLElement was built, including its chiled elements the attributes are queired and appended.  It is then written to the approriate XML file.  When there are no more records in the query the file is completed and closed. 

The XMLElement class is written to be as versatile as possible, and can be used to write XML files in diffent applications as needed.  The XMLElement class is responsible for approprite indentations of the tags, while the main class keeps track of inline elements vs. open elements. 