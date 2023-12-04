import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.exit;

public class IRoadTrip {

    static HashMap<String, String> stateNameMap;
    static HashMap<String, String> finalNameMap;
    static HashMap<String, HashMap<String, Integer>> nameBorderDistance;
    static HashMap<String, HashMap<String, Integer>> allDistancesCap;

    static HashMap<String, String> notFound;



    public IRoadTrip (String [] args) {
        /*
        HashMap<String, HashMap<String, Integer>> boarderMap = new HashMap<>();
        HashMap<String, String> idMap = new HashMap<>();
        idMap = getIDs();
        boarderMap = getInfoBorders(idMap);
        HashMap<String, String> idMapFinal = new HashMap<>();
         */
        stateNameMap = new HashMap<>();
        finalNameMap = new HashMap<>();
        nameBorderDistance = new HashMap<>();
        allDistancesCap = new HashMap<>();
        notFound = new HashMap<>();



        getStateName();
        getBoardersName();
        getDistances();
        addOtherPossibleNames();
        /*setIDsForFinalNameMap adds all similar/differently formatted names to the stateNameMap, so if the user
        * searches on of these, it will find the proper key. */
        setIDsForfinalNameMap();
        handleNotFoundHashMap();


    }
    private static void printHashMap(Map<?, ?> map, String indent) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map<?, ?>) {
                System.out.println(indent + key + " -> ");
                printHashMap((Map<?, ?>) value, indent + "    ");
            } else {
                System.out.println(indent + key + " -> " + value);
            }
        }
    }
    public String findLikelyMatch(String name) {

        String[] words = name.split(" ");
        String ID = null;
        for (String key : stateNameMap.keySet()) {
            int sameWordCount = 0;
            for (int i = 0; i < words.length; i++) {
                if (key.contains(words[i])) {
                    sameWordCount += 1;
                }
            }
            if (sameWordCount == words.length) {
                ID = stateNameMap.get(key);
                break;
            } else {
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
        System.out.println("New ones");
        printHashMap(notFound, "    ");
        //dhekelia is not found
        notFound.put("french guiana", "PNG");
        notFound.put("eswatini", "SWA");
        notFound.put("south korea", "ROK");
        notFound.put("korea, south", "ROK");
        //san marino is landlocked, so not in boarders



        // will add to finalNameMap


    }
    public void addOtherPossibleNames() {
        HashMap<String, String> tempMap = new HashMap<>();

        for (String key : stateNameMap.keySet()) {
            String name = key;
            //The case a name contains parenthesis
            boolean commaHandled = false;
            if (name.contains("(")) {

                String[] parens = name.split("\\(");
                String left = parens[0].trim().toLowerCase();
                tempMap.put(left, stateNameMap.get(name));
                //The case either side of the parenthesis contains a comma
                if (left.contains(",")) {
                    String[] comArr = left.split(",");
                    left = comArr[1].trim() + comArr[0];
                    tempMap.put(left, stateNameMap.get(name));
                    commaHandled = true;
                }

                String right = parens[1].substring(0, parens[1].length() - 1).toLowerCase();
                tempMap.put(right, stateNameMap.get(name));

                if (right.contains(",")) {
                    String[] comArr = right.split(",");
                    right = comArr[1].trim() + comArr[0];
                    tempMap.put(right, stateNameMap.get(name));
                    commaHandled = true;
                }

            }
            //The case it contains a comma AND paren hasn't been handled yet
            if (name.contains(",") && commaHandled == false) {
                String[] comArr = name.split(",");
                String newName = comArr[1].trim() + comArr[0];
                tempMap.put(newName, stateNameMap.get(name));
            }

            if (name.contains("’")) {
                String newName = name.replace("’", "'");
                tempMap.put(newName, stateNameMap.get(name));
            }

            if (name.contains("/")) {
                String[] slashArr = name.split("/");
                tempMap.put(slashArr[0].trim(), stateNameMap.get(name));
                tempMap.put(slashArr[1].trim(), stateNameMap.get(name));
            }

        }

        stateNameMap.putAll(tempMap);
    }

    public void setIDsForfinalNameMap() {

        for (String key : finalNameMap.keySet()) {
            HashMap<String, String> tempMap = new HashMap<>();

            String name = key;
            String ID = stateNameMap.get(name);
            tempMap.put(name, null);
            boolean commaHandled = false;
            //Check for parenthesis


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

            String newName = name;
            if (name.contains(",") && ID == null) {
                String[] comArr = name.split(",");
                newName = comArr[1].trim() + comArr[0];
                ID = stateNameMap.get(newName);
                tempMap.put(newName, null);
            }

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

            if (ID != null) {

                for (String theKey : tempMap.keySet()) {
                    tempMap.put(theKey, ID);
                }

                stateNameMap.putAll(tempMap);
                finalNameMap.put(name, ID);
            } else {
                notFound.put(name, ID);
            }

        }

        //printHashMap(finalNameMap, "    ");
        printHashMap(notFound, "    ");
    }

    public void getBoardersName() {
        try (BufferedReader reader = new BufferedReader(new FileReader("borders.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] origin = line.split(" = ");
                if (origin.length > 1) {
                    String[] destinationsArr = origin[1].split(" km; ");
                    HashMap<String, Integer> destMap = new HashMap<>();
                    for (int i = 0; i < destinationsArr.length; i++) {
                        String[] dest = destinationsArr[i].split(" ");
                        String finalDest = "";
                        for (int j = 0; j < dest.length; j++) {
                            if (Character.isDigit(dest[j].charAt(0)) == false) {
                                finalDest += dest[j];
                                finalDest += " ";
                            } else {
                                finalDest = finalDest.trim();
                                break;
                            }
                        }
                        destMap.put(finalDest.toLowerCase(), 0);
                    }
                    nameBorderDistance.put(origin[0].toLowerCase(), destMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            exit(0);
        }
        //printHashMap(nameBorderDistance, "  ");

        for (Map.Entry<String, HashMap<String, Integer>> outerEntry : nameBorderDistance.entrySet()) {
            String outerKey = outerEntry.getKey();
            HashMap<String, Integer> innerMap = outerEntry.getValue();
            finalNameMap.put(outerKey, null);

            for (String innerKey : innerMap.keySet()) {
                finalNameMap.put(innerKey, null);
            }
        }

        //printHashMap(finalNameMap, "  ");

    }

    public void getDistances() {
        try (BufferedReader reader = new BufferedReader(new FileReader("capdist.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("kmdist")) {
                    continue;
                }
                //System.out.println(line);
                String[] capInfo = line.split(",");
                String country1 = capInfo[1];
                String country2 = capInfo[3];

                Integer kilom = Integer.parseInt(capInfo[4]);

                if (allDistancesCap.get(country1) == null) {
                    HashMap<String, Integer> innerMap = new HashMap<>();
                    innerMap.put(country2, kilom);
                    allDistancesCap.put(country1, innerMap);
                } else {
                    Map<String, Integer> innerMap = allDistancesCap.get(country1);
                    innerMap.put(country2, kilom);
                }
            }

            //printHashMap(allDistancesCap, "  ");

        } catch (Exception e) {
            e.printStackTrace();
            exit(0);
        }

    }
    public void getStateName() {
        try (BufferedReader reader = new BufferedReader(new FileReader("state_name.tsv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("2020-12-31")) {
                    String[] lineArr = line.split("\t");
                    stateNameMap.put(lineArr[2].toLowerCase(), lineArr[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            exit(0);
        }
        //printHashMap(stateNameMap, "    ");
    }


    public int getDistance (String country1, String country2) {
        // Replace with your code
        return -1;
    }


    public List<String> findPath (String country1, String country2) {
        // Replace with your code


        return null;
    }


    public void acceptUserInput() {
        // Replace with your code


        System.out.println("IRoadTrip - skeleton");
    }


    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);

        a3.acceptUserInput();
    }

}


