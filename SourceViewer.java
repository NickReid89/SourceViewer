
 Title: SourceViewer.java
 Description: This program takes in an ip address or url and returns a web page's
 source based on the options entered. 

 Date: October 30 2015
 @author Nickolas Reid
 @version 1.0
 @copyright 2015 Nickolas Reid.

 Design:
 This application is designed to go through multiple safe guards before returning
 a result to the user. The first safe guard is to check whether the ip address or 
 url is valid. After this, if an IP address is entered, a http:// prefix is added
 to it. Once this is done, the program evalulates how many arguments a user
 has entered. The application calls CheckProtocol() method which then goes through
 another safeguard check. The application returns the first line of the URL a 
 user has requested. If the result is null, then it is assumed the user chose the
 wrong protocol. Once the protocol is changed, all validation tests should have 
 been passed. The application only now looks at the option and search term arguements
 given and returns the users requested results.
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.System.out;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceViewer {
    /* 	
     User has three options to user the program:    
     1. IP Address
     2. IP Address and Search line
     3. IP Address, Option, Search Line
     Anything else will result in giving a user an error message.
    
     */

    public static void main(String[] args) {

        try {
            //Check if a user entered a valid IP address or url.  
            if (validateURL(args[0]) || validateIP(args[0])) {
                /*
                 If the user entered a url, then a http:// prefix must be added.
                 */
                if (validateIP(args[0])) {
                    args[0] = "http://" + args[0];
                }

                //Switch case for what arguements the user chose.
                switch (args.length) {
                    //Only URL
                    case 1:
                        checkProtocol(args[0], "", "");
                        break;
                    //Only URL and Search Line
                    case 2:
                        checkProtocol(args[0], "", args[1]);
                        break;
                    //URL, Option, Search Line
                    case 3:
                        checkProtocol(args[0], args[1], args[2]);
                        break;
                    //Default for users putting in too many terms or anything else caught.
                    default:
                        out.println("Usage: <IP Address> | <IP Address> <Search Line> | <IP Address> <Option (-n,-c)> <Search Line>)");
                        break;
                }

            } else {
                // The user places an invalid ip address or url and is warned against it.
                System.out.println("Please check the IP address or URL you have provided. \nIf you are using a URL be sure to have http:// or https:// before the url.");
            }
            //Catches the case there is no arguements.
        } catch (ArrayIndexOutOfBoundsException ai) {
            System.out.println("Please enter at least a URL");
            out.println("Usage: <IP Address> | <IP Address> <Search Line> | <IP Address> <Option (-n,-c)> <Search Line>)");
        }
    }

    //Prints results to user.
    private static void printFromStream(InputStream raw, String option, String searchTerm) throws IOException {
        // If this flag is triggered then a user result was found.
        boolean foundResult = false;
        // Counts the number of times a result is found.
        int lineCounter = 0;
        //Counts the line a result is on.
        int lineNumber = 0;
        // Try to read the HTML stream.
        try (InputStream buffer = new BufferedInputStream(raw)) {
            /* I put the Input Stream inside of a BufferedReader because I found the InputStreamReader could only read by character
             whereas the BufferedReader can read the HTML line by line which is what I was assigned to do.
             */
            BufferedReader reader = new BufferedReader(new InputStreamReader(buffer));
            // This string will hold each line in the HTML document.
            String markupLine;
            //While the document still has lines.
            while ((markupLine = reader.readLine()) != null) {
                // Count the line number we're on.
                lineNumber++;
                /*Trim the white space off the HTML lines. Normally in an HTML document indentation
                 Would be important. However, in our case we're only returning lines to a user.                 
                 */
                markupLine = markupLine.trim();

                // Convert the HTML line and the search time to uppercase to compare and check for an empty string.
                if (markupLine.toUpperCase().contains(searchTerm.toUpperCase()) && !searchTerm.isEmpty()) {
                    //If true there is a match.
                    foundResult = true;
                    // Count the occurances of the match.
                    lineCounter++;
                    // If the user wants the lines numbers we print it like this.
                    if (option.equals("-n")) {
                        //This gets rid of white space
                        if (!markupLine.isEmpty()) {
                            out.println("Line number: " + lineNumber + ": " + markupLine + "\n");
                        }

                        //Else we print the default way.
                    } else if (!option.equals("-c")) {
                        //Gets rid of white space.
                        if (!markupLine.isEmpty()) {
                            out.println(markupLine + "\n");
                        }
                    }

                } // If the user didn't put in a search term print out the page minus white space.
                else if (searchTerm.isEmpty()) {
                    foundResult = true;
                    if (!markupLine.isEmpty()) {
                        out.println(markupLine);
                    }
                }

            }/* If the user specified the total count of their search term or 
             The user searched for a result and the application is now returning
             The total number of times their search result appeared.
             */

            if (option.equals("-c") || !searchTerm.isEmpty()) {
                out.println("The line '" + searchTerm + "' has been found: " + lineCounter + " times in this document");
                //If their result is not found.
            } else if (foundResult == false) {
                System.out.println("No results found.");

            }
        }
    } //End of printFromStream

    //This checks whether a URL is valid. It does not check if it connects though.
    private static boolean validateURL(String url) {
        try {
            //If the URL can be transformed to a URI it is valid.
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException ex) {
            //The URL isn't formed correctly.
            return false;
        }
    } //End of validateURL

    //This method utilizes regex to see if an IP address is functionally valid.
    //This method does not check connectivity though.

    private static boolean validateIP(String IP) {
        String ipPattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern p = Pattern.compile(ipPattern);
        Matcher m = p.matcher(IP);
        if (m.find()) {
            //Formed correctly.
            return true;
        }
        //Fromed incorrectly.
        return false;
    } // End of validateIP

    //ChangeProtocol looks at a url and switches the protocol. if it is 
    //http it changes to https, if it is https it changes to http.

    private static String changeProtocol(String URL) {
        //Utilizes regex to see if there is an http:// in the string.
        String protocolPattern = "http://";
        Pattern http = Pattern.compile(protocolPattern);
        Matcher m = http.matcher(URL);
        //If it has a result, change the protocol.
        if (m.find()) {
            return "https://" + URL.substring(7, URL.length());
        }
        return "http://" + URL.substring(8, URL.length());

    } // End of changeProtocol

    /*
     CheckProtocol takes in three string arguements(url, option search term) and 
     first opens a connection and tries to read from it. If it can, it calls
     printFromStream ()for further evaluation. If it returns null however, it 
     switches the protocol and then moves to printFromStream().
     */
    private static void checkProtocol(String URL, String option, String searchTerm) {
        try {
            //Create URL
            URL u = new URL(URL);
            //Open coonection
            HttpURLConnection uc = (HttpURLConnection) u.openConnection();
            try {
                //create stream to read object.
                InputStream raw = uc.getInputStream();
                try (InputStream buffer = new BufferedInputStream(raw)) {
                    //Create a BufferedReader to read the html file line by line.
                    BufferedReader reader = new BufferedReader(new InputStreamReader(buffer));
                    //If the results return null, switch protocl.
                    if (reader.readLine() == null) {
                        u = new URL(changeProtocol(URL));
                        uc = (HttpURLConnection) u.openConnection();
                        raw = uc.getInputStream();
                        //print results.
                        printFromStream(raw, option, searchTerm);
                    } else {
                        uc = (HttpURLConnection) u.openConnection();
                        raw = uc.getInputStream();
                        //Print results.
                        printFromStream(raw, option, searchTerm);
                    }
                }
            } catch (MalformedURLException ex) {
                out.println("url is malformed.");
            } catch (IOException ex) {
                out.println("IO Error");
            }
        } catch (IOException ex) {
            out.println(URL + " is not correct. Please try again.");
        }

    } //End of checkProtocol

} // End of public class SourceViewer
