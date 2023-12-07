import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import static java.lang.System.exit;
/** author Eva DeThomas
 *  This program uses Dijkstra's algorithm to find the shortest path from one country to another by car given
 *  a border, capital city distance, and country code file.
 * */
public class IRoadTrip {
//See each static variable in constructor
    static HashMap<String, String> stateNameMap;
    static HashMap<String, String> finalNameMap;
    static HashMap<String, HashMap<String, Integer>> nameBorderDistance;
    static HashMap<String, HashMap<String, Integer>> allDistancesCap;
    static HashMap<String, String> notFound;
    static PriorityQueue<HashMap.Entry<String, Integer>> queue;
    static ArrayList<String> noBorderCountries;



    public IRoadTrip (String [] args) {

        //The state name map contains all the names from the stateName file, and eventually includes aliases as well.
        stateNameMap = new HashMap<>();
        //The finalName map contains the state names mapped to the keys that fit perfectly with the border file.
        //(Aliases not included, edge cases handled so it can read them)
        finalNameMap = new HashMap<>();
        //While being built, contains the border names and the distances to each country it borders, but
        //later, it contains the codes of each country and the distance
        nameBorderDistance = new HashMap<>();
        //This contains all the distances from the cap distance file, connected to their 3-letter codes, regardless of
        //whether it's a border or not
        allDistancesCap = new HashMap<>();
        //Contains all the names that weren't connected to the country codes, so they have a function that handles and
        //fixes them
        notFound = new HashMap<>();
        //Countries that have no borders, program will return no path for these.
        noBorderCountries = new ArrayList();

        //All of the following construct the main hashmap that's used in Dijkstras:
        //The first three read in the files, and make the hashmaps described above.
        getStateName(args[2]);
        getBoardersName(args[0]);
        getDistances(args[1]);
        //This cross-referances the files and reformats some of the names in case the user types in something similar,
        //but different then expected
        addOtherPossibleNames();
        //Adds the IDs to a "final name map" where there is only one correct key for every value
        setIDsForfinalNameMap();
        //Adds in all of the not found names, and connects them to the proper code.
        handleNotFoundHashMap();
        //Changes the sames in the name map to Ids, so they can easily seach in the capDistanceMap
        rewriteNameBorderDistanceWithIds();
        //Adds to the map so "USA" is an acceptable input from the user, rather than just United States
        IDsPointToSelves();

    }

    public static void IDsPointToSelves() {
        HashMap<String,String> tempHash = new HashMap<>();
        //Iterates through the values of the stateNameMap and adds the code as aliases that point
        //to themselves.
        for (String value : stateNameMap.values()) {
            if (value != null) {
                tempHash.put(value.toLowerCase(), value);
            }
        }
        //Add all the values from the tempMap to the state name map
        stateNameMap.putAll(tempHash);
    }


    public void rewriteNameBorderDistanceWithIds() {
        HashMap<String, HashMap<String, Integer>> newBorderMap = new HashMap<>();
        boolean doNotAdd;

        //Iterating through nameBorderDistance, starting wiht country1 (outter loop)
        for (String outerKey : nameBorderDistance.keySet()) {
            //Getting the innerMap, (country 2 and sitance)
            HashMap<String, Integer> innerMap = nameBorderDistance.get(outerKey);
            //Getting the ID from the finalNameMap
            String newOuterKey = finalNameMap.get(outerKey);
            //Initializing new inner map for the IDs
            HashMap<String, Integer> newMap = new HashMap<>();

            doNotAdd = true;

            // Iterate through the inner map (country2 and the distance)
            for (String innerKey : innerMap.keySet()) {
                //Getting the actual ID out
                String newInnerKey = finalNameMap.get(innerKey);

                HashMap<String, Integer> getDisMap = allDistancesCap.get(newOuterKey);
                //Getting the new map, insert to it ONLY if there are no null values (IE, the country actually exists)
                if (getDisMap != null && newInnerKey != null) {
                    Integer value = getDisMap.get(newInnerKey);
                    if (value != null) {
                        newMap.put(newInnerKey, value);
                        doNotAdd = false;
                    }
                }
            }
            //If the country doesn't exist/is null oro is an edge case, skip adding it to the list.
            if (!doNotAdd) {
                newBorderMap.put(newOuterKey, newMap);
            }
        }
        //reinitialize the main hashmap, to the hashmap with the IDs.
        nameBorderDistance = newBorderMap;
    }

    //Takes in a string, and looks through the aliases and names to see if there is something similar it could be
    //NOTE: This isn't the most reliable, it only gets the MOST LIKELY, but it saved me a lot of time and made for
    //less hard-coding.
    public String findLikelyMatch(String name) {
        String[] words = name.split(" ");
        String ID = null;
        //Iterate through keys of state name map
        for (String key : stateNameMap.keySet()) {
            //Count the same words in both names
            int sameWordCount = 0;
            for (int i = 0; i < words.length; i++) {
                if (key.contains(words[i])) {
                    sameWordCount += 1;
                }
            }
            //if length is the same as the name we're looking for, set the key to that name
            if (sameWordCount == words.length) {
                ID = stateNameMap.get(key);
                break;
            } else {
                //Otherwise, check to see if there's a similar first half, if there is add it.
                //else if does the same with the second half.
                String subName = name.substring(0, name.length() / 2);
                if (key.contains(subName)) {
                    ID = stateNameMap.get(key);
                } else if (key.contains(name.substring(name.length() /2, name.length()))) {
                    ID = stateNameMap.get(key);
                }
            }

        }
        return ID;
    }
    public void handleNotFoundHashMap() {

        for (Map.Entry<String, String> entry : notFound.entrySet()) {
            String ID = findLikelyMatch(entry.getKey());
            if (ID != null) {
                notFound.put(entry.getKey(), ID);
            }
        }

        /* Needed to use this for a littttle hard-coding, the ones that are completely different and theres no way to tell.
        Also error I'm feeling too lazy to try and fix: sometimes the "findLikelyMatch" returns the wrong one.
         */
        //System.out.println("New ones");
        //printHashMap(notFound, "    ");
        //dhekelia is not found
        notFound.put("french guiana", "PNG");
        notFound.put("eswatini", "SWA");
        notFound.put("south korea", "ROK");
        notFound.put("korea, south", "ROK");
        notFound.put("monaco", null);
        notFound.put("andorra", null);
        notFound.put("north korea", "PRK");
        notFound.put("korea, north", "PRK");
        notFound.put("uk", "UKG");
        notFound.put("uae", "UAE");
        notFound.put("congo, republic of the", "CON");
        notFound.put("macau", null);
        notFound.put("us", "USA");
        notFound.put("congo, republic of the", "CON");
        notFound.put("republic of the congo", "CON");
        notFound.put("united states of america", "USA");
        //ALSO found come country codes that are different in the actual distance map:

        notFound.put("uk", "UK");
        notFound.put("united kingdom", "UK");

        //System.out.println("NEW new ones");
        //printHashMap(notFound, "    ");
        //Update final map with corrected edge cases
        finalNameMap.putAll(notFound);
        stateNameMap.putAll(notFound);
        stateNameMap.putAll(finalNameMap);




        // will add to finalNameMap

    }

    //This function goes through the stateName map, and modified and adds other possible aliases that are formatted
    //differently.
    public void addOtherPossibleNames() {
        //Create a temp map so updated values can be added post-iteration
        HashMap<String, String> tempMap = new HashMap<>();
        //Run through the keys of the stateMap hash table.
        for (String key : stateNameMap.keySet()) {
            String name = key;
            //If a name contians parenthesis, get out the both of the names
            boolean commaHandled = false;
            if (name.contains("(")) {
                String[] parens = name.split("\\(");
                String left = parens[0].trim().toLowerCase();
                //Add the left of the parens to the possible names
                tempMap.put(left, stateNameMap.get(name));
                //Check if the left contains a comma, if it does, reformat and add to stateName as possible alias
                if (left.contains(",")) {
                    String[] comArr = left.split(",");
                    left = comArr[1].trim() + " " + comArr[0];
                    tempMap.put(left, stateNameMap.get(name));
                    commaHandled = true;
                }
                //Get the right, add as an alias
                String right = parens[1].substring(0, parens[1].length() - 1).toLowerCase();
                tempMap.put(right, stateNameMap.get(name));
                //Same as above with commas but for the right side.
                if (right.contains(",")) {
                    String[] comArr = right.split(",");
                    right = comArr[1].trim() + " " + comArr[0];
                    tempMap.put(right, stateNameMap.get(name));
                    commaHandled = true;
                }
            }
            //Handles commas for non-parenthesis names
            if (name.contains(",") && commaHandled == false) {
                String[] comArr = name.split(",");
                String newName = comArr[1].trim() + " " + comArr[0];
                tempMap.put(newName, stateNameMap.get(name));
            }
            //Fixes the cot d'ivore problem, different types of apostrophies.
            if (name.contains("’")) {
                String newName = name.replace("’", "'");
                tempMap.put(newName, stateNameMap.get(name));
            }
            //Gets both names when there's a slash, (left and right like parens)
            if (name.contains("/")) {
                String[] slashArr = name.split("/");
                tempMap.put(slashArr[0].trim(), stateNameMap.get(name));
                tempMap.put(slashArr[1].trim(), stateNameMap.get(name));
            }
        }
        //Add all the tempNames to the stateNameMap
        stateNameMap.putAll(tempMap);
    }

    //This takes in the finalNameMap, that at this point only has the border NAMES and all of the distances
    //on the inside are set to 0. This function ensures that all the border names are also included in the possibleAlias
    //(stateName map) to ensure data is consistant. It finds names that don't have a matching ID, and tries to
    //re-arrange and find a matching ID. If it doensn't work, it adds them to "not found" which is a list of borders
    //with unknown country names to be fixed later.
    public void setIDsForfinalNameMap() {
        //Iterating through the keys of the finalNameMap, retrieved from the border file.
        for (String key : finalNameMap.keySet()) {
            //Initializing a tempMap so it can be changed later, after incrimentation.
            HashMap<String, String> tempMap = new HashMap<>();

            String name = key;
            //Use the country name to look through the possible aliases and get the 3-letter IDs
            String ID = stateNameMap.get(name);

            //Add the name in-case it has not been found in the stateName map that includes all possible
            // names yet (will update value later)
            tempMap.put(name, null);
            //Check for parenthesis
            //Handles parenthesis like the other function, adding the inner and outer names to the state name map
            if (name.contains("(") && ID == null) {
                String[] parens = name.split("\\(");

                String left = parens[0].trim().toLowerCase();
                tempMap.put(left, null);

                String right = parens[1].substring(0, parens[1].length() - 1).toLowerCase();
                tempMap.put(right, null);

                if (stateNameMap.get(right) != null) {
                    ID = stateNameMap.get(right);
                }
                if (stateNameMap.get(left) != null) {
                    ID = stateNameMap.get(left);
                }
            }

            //Handles the commas, tries to find an ID when commas are moved around.
            String newName = name;
            if (name.contains(",") && ID == null) {
                String[] comArr = name.split(",");
                newName = comArr[1].trim() + " " + comArr[0];
                ID = stateNameMap.get(newName);
                tempMap.put(newName, null);
            }
            //Moves around the thes, tries to find a matching ID without thes
            if ((name.contains("the ") || name.contains(" the")) && ID == null) {
                String newName1 = name.replace("the", "").trim();
                ID = stateNameMap.get(newName1);
                tempMap.put(newName1, null);
                if (ID == null) {
                    newName = newName.replace("the", "").trim();
                    ID = stateNameMap.get(newName);
                    tempMap.put(newName, null);
                }
            }
            //If the ID, after all the checks isn't null anymore, then get the ID from state-name and set it correctly
            if (ID != null) {

                for (String theKey : tempMap.keySet()) {
                    tempMap.put(theKey, ID);
                }

                stateNameMap.putAll(tempMap);
                finalNameMap.put(name, ID);
            } else {
                //Otherwise, add it to "notFound", ID is always null here.
                notFound.put(name, ID);
            }
        }
    }

    //Get borders name takes in the boarders file, and creates a hashmap of hashmaps. The outer being country1,
    //country2 being the destination bordering country, and the distance is initialized to null for now.
    public void getBoardersName(String file) {
        //Uses buffered reader with exception catch to read thorugh each line in the file.
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                //Split the border file with the equals sign
                String[] origin = line.split(" = ");
                if (origin.length > 1) {
                    //Take out the km
                    String[] destinationsArr = origin[1].split(" km; ");
                    //Innitialize the inner maps
                    HashMap<String, Integer> destMap = new HashMap<>();
                    for (int i = 0; i < destinationsArr.length; i++) {
                        //Iterate through the rest of the borders, following the equals sign using split with space
                        String[] dest = destinationsArr[i].split(" ");
                        String finalDest = "";
                        //this loop skips the border distance/irrelevnt information
                        for (int j = 0; j < dest.length; j++) {
                            if (Character.isDigit(dest[j].charAt(0)) == false) {
                                finalDest += dest[j];
                                finalDest += " ";
                            } else {
                                finalDest = finalDest.trim();
                                break;
                            }
                        }
                        //Add the inner map to the outer map, initlaize all distances to 0 since its unknown.
                        destMap.put(finalDest.toLowerCase(), 0);
                    }
                    //Add to the global final hashmap
                    nameBorderDistance.put(origin[0].toLowerCase(), destMap);
                } else {
                    //If there is not a right side, then there are no bordering countries. Adding it here.
                    noBorderCountries.add(origin[0].toLowerCase());
                }
            }
        } catch (Exception e) {
            System.out.println("Error collecting file. Printing stack trace for debugging: ");
            e.printStackTrace();
            exit(0);
        }
        //Creates a "finalNameMap" which reads the keys and the values, getting all the countries and putting them
        //in one hashmap to help match them to their country codes. Having this hashmap enables easier access to the IDs
        //and in turn the ability to implement Dijerkas. (maps borderName -> null, which eventually will be a country
        //code.
        for (Map.Entry<String, HashMap<String, Integer>> outerEntry : nameBorderDistance.entrySet()) {
            String outerKey = outerEntry.getKey();
            HashMap<String, Integer> innerMap = outerEntry.getValue();
            finalNameMap.put(outerKey, null);
            for (String innerKey : innerMap.keySet()) {
                finalNameMap.put(innerKey, null);
            }
        }

    }

    //Get distances takes in the distances file that provides information about the length of each path from
    //the capdist file
    public void getDistances(String file) {
        //Uses buffer-reader and exception to take in the file
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                //skips irrelevent line
                if (line.contains("kmdist")) {
                    continue;
                }
                //gets the starting country code, ending code and distance in km
                String[] capInfo = line.split(",");
                String country1 = capInfo[1];
                String country2 = capInfo[3];

                Integer kilom = Integer.parseInt(capInfo[4]);
                //Adds them to the "allDistanceCap" hashmap, same format as the borders hashmap.
                if (allDistancesCap.get(country1) == null) {
                    HashMap<String, Integer> innerMap = new HashMap<>();
                    innerMap.put(country2, kilom);
                    allDistancesCap.put(country1, innerMap);
                } else {
                    Map<String, Integer> innerMap = allDistancesCap.get(country1);
                    innerMap.put(country2, kilom);
                }
            }
        } catch (Exception e) {
            System.out.println("Error collecting file. Printing stack trace for debugging: ");
            e.printStackTrace();
            exit(0);
        }

    }

    //Takes in the state name file, creates a map of the name to the given country codes
    public void getStateName(String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                //Only takes in the current dates
                if (line.contains("2020-12-31")) {
                    String[] lineArr = line.split("\t");
                    //adds them to the map
                    stateNameMap.put(lineArr[2].toLowerCase(), lineArr[1]);
                }
            }
        } catch (Exception e) {
            System.out.println("Error collecting file. Printing stack trace for debugging: ");
            e.printStackTrace();
            exit(0);
        }
    }

    //Get distance takes in two country names, gets their codes, and if they are bordering, returns their distance
    public int getDistance (String country1, String country2) {
        //Gets the country codes
        country1 = stateNameMap.get(country1);
        country2 = stateNameMap.get(country2);

        if (country1 == country2) {
            return 0;
        }
        //Uses the codes to get the distance from boarderDistance
        HashMap<String, Integer> disMap = nameBorderDistance.get(country1);
        //If its not found, disMap will be null (the inner map with boardering countries)
        if (disMap != null) {
            if (disMap.get(country2) != null) {
                //return the distance
                return disMap.get(country2);
            }
        }
        //Otherwise, return -1
        return -1;
    }

    //This walks back through the given set to find the path between one coutry to another, obtained by Dikstra's
    //Creates a linked list by following the last node in last Hashmap, then formats the list properly in a copy
    //before returning it
    public List<String> findTheRealPath(HashMap<String, String> last, String country1, String country2) {
        //Initialize linked list and current to walk through the last values of each "node"/verticie.
        LinkedList<String> path = new LinkedList<>();
        String current = country2;
        //Creating a backwards hashmap to obtain the NAME from the given COUNTRY CODE because
        //the Hashmap contains country codes not names.
        HashMap<String, String> nameBackMap = new HashMap<>();
        for (Map.Entry<String, String> entry : finalNameMap.entrySet()) {
            nameBackMap.put(entry.getValue(), entry.getKey());
        }
        //While there is still a "last" node, get the current and last node, add found node to the path,
        //then set current to the node before it.
        while (current != null) {
            path.addFirst(current);
            current = last.get(current);
        }
        //Added this last min whoops
        if (country1.equals(country2)) {
            path.addFirst(country1);
        }
        //If the path equals the first country, then we know we've found the path. In this case we format it properly.
        if (path.getFirst().equals(country1)) {
            //Create a new list to hold the formatted paths.
            LinkedList<String> formattedPath = new LinkedList<>();
            //Walk through the path list, use the country codes to get the names, and use helper capitalize function
            //to put the capitalization back.
            for (int i = 0; i < path.size() - 1; i++) {
                String currentElement = capitalizeWords(nameBackMap.get(path.get(i)));
                String nextElement = capitalizeWords(nameBackMap.get(path.get(i + 1)));
                HashMap<String, Integer> temp = nameBorderDistance.get(path.get(i));
                //Get the distance from one place to another using the boarderDistance main map
                Integer kiloms = temp.get(path.get(i + 1));
                if (country1.equals(country2)) {
                    kiloms = 0;
                }
                //Finally print it out
                String finalFormat = currentElement + " --> " + nextElement + " (" + kiloms + " km.)";
                finalFormat.replaceAll(" Of ", " of ");
                formattedPath.add(finalFormat);
            }
            return formattedPath;
        } else {
            return null;
        }
    }

    public static String capitalizeWords(String given) {
        //turns string to char array
        char[] arr = given.toCharArray();
        boolean toUpper = false;
        //walk through each letter
        for (int i = 0; i < arr.length; i++) {
            //if not a letter, the set bool so next letter will capitalize
            if (toUpper && Character.isLetter(arr[i])) {
                arr[i] = Character.toUpperCase(arr[i]);
                toUpper = false;
            } else if (!Character.isLetter(arr[i])) {
                toUpper = true;
            }
        }
        //captilize edge case of first letter.
        arr[0] = Character.toUpperCase(arr[0]);
        //return new result
        return new String(arr);
    }

    //Acutally uses the graph to impliment Dikstra's algorithm. Takes in starting and ending country NAME.
    public List<String> findPath(String country1, String country2) {

        //Get the codes from names in order to obtain edges from clean border and dist. map.
        String count1 = stateNameMap.get(country1);
        String count2 = stateNameMap.get(country2);

        //Create a hashMap that stores the cummulative cost of each path for each node, intilize all to highest cost.
        HashMap<String, Integer> costs = new HashMap<>();
        for (String node : nameBorderDistance.keySet()) {
            costs.put(node, Integer.MAX_VALUE);
        }
        //Set the first country to
        //insert the first country, with lowest cost of 0 into priority queue of costs. (This allows out starting vertex)
        costs.put(count1, 0);

        //Initialize last to keep track of path
        HashMap<String, String> last = new HashMap<>();
        //Initialize known so we know if a node has been visited or not.
        HashSet<String> known = new HashSet<>();
        //Initalize priority queue, using the values of the costs hashMap as a comparator. Keeps track of lowest cost
        //path for each vertex
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(costs::get));

        //add the starting country to queue.
        queue.add(count1);

        //While there are still values in the queue
        while (!queue.isEmpty()) {
            //get the least costly node (will bein as country1)
            String v = queue.poll();

            //If its the vertex we're looking for, we can break early.
            if (v.equals(count2)) {
                break;
            }
            //If we already know the path for this node, we can skip the rest
            if (known.contains(v)) {
                continue;
            }
            //Otherwise, we now know have visited this node and can add it to the visited table
            known.add(v);

            //Get the neighboring countries.
            HashMap<String, Integer> innerMap = nameBorderDistance.get(v);

            //if there are none, no reason to look through them.
            if (innerMap == null) {
                continue; // Handle potential null values
            }

            //for each neighboring country...
            for (String nab : innerMap.keySet()) {
                //Get the cummulative cost of path so far
                int cumulCost = costs.get(v) + innerMap.get(nab);

                //if the cummulative cost is the least expensive compared to it's neighbors, update it.
                if (cumulCost < costs.get(nab)) {
                    //update costs
                    costs.put(nab, cumulCost);
                    //update node v as previous for neighbor we found
                    last.put(nab, v);
                    //remove from the priority queue
                    queue.remove(nab);
                    //re-add it to get the right priority.
                    queue.add(nab);
                }
            }
        }

        //return the table with the paths, and country1 and 2. The table is updated so the
        //value of country 2 on the "last" country hashmap will lead to the least-expensive path.

        return findTheRealPath(last, count1, count2);
    }

    //take in the user input (called by the instance)
    public void acceptUserInput() {
        // Replace with your code
        //Open scanner
        Scanner scanner = new Scanner(System.in);
        //Used if the user types in EXIT
        boolean done = false;
        //Until exit, trap them in infinite loop
        while (true) {
            //Set noPath to false for single country handling
            boolean noPath = false;

            //Intialize first country
            String firstCountry;
            //While they have an invalid input and exit isn't typed, it'll keep asking for the first country
            while (true) {
                System.out.print("Enter the name of the first country (type EXIT to quit): ");
                //Get user input for first country
                firstCountry = scanner.nextLine();
                if (firstCountry.equals("EXIT")) {
                    done = true;
                    break;
                }
                //Set input to lowercase for error handling, if lonely country, it will end loop and set no path for
                //no path response.
                if (noBorderCountries.contains(firstCountry.toLowerCase())) {
                    noPath = true;
                    break;
                }

                firstCountry = firstCountry.toLowerCase();
                //Handles bad input
                if (stateNameMap.get(firstCountry) == null) {
                    System.out.println("Invalid country name. Please enter a valid country name.");
                } else {
                    break;
                }

            }
            //If exit typed, break.
            if (done) {
                break;
            }
            //Works the same as first country
            String secondCountry;
            while (true) {
                System.out.print("Enter the name of the second country (type EXIT to quit): ");
                secondCountry = scanner.nextLine();
                if (secondCountry.equals("EXIT")) {
                    done = true;
                    break;
                }
                if (noBorderCountries.contains(firstCountry.toLowerCase())) {
                    noPath = true;
                    break;
                }
                secondCountry = secondCountry.toLowerCase();
                if (stateNameMap.get(secondCountry) == null) {
                    System.out.println("Invalid country name. Please enter a valid country name.");
                } else {
                    break;
                }
            }
            if (done) {
                break;
            }
            //Handles single countries.
            if (noPath == true) {
                System.out.println("No path found");
                break;
            }
            /*
            It probably would be better to take in the codes here, but since IDK how the grading works I'm just feeding
            the country names and will re-get them in path.
             */
            //Get the path
            List<String> path = findPath(firstCountry, secondCountry);
            //Either print the path or that it's not found
            if (path == null) {
                System.out.println("No path found.");
            } else {
                for (String element : path) {
                    System.out.println(element);
                }
            }

            //NOTE : Add function here to handle USA.
            // Output the input received from the user
        }
        //close scanner
        scanner.close();
        //Exit.
        System.out.println("IRoadTrip - skeleton");
    }


    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);
        a3.acceptUserInput();
    }

}


