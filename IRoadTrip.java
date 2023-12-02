import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.exit;

public class IRoadTrip {


    public IRoadTrip (String [] args) {
        HashMap<String, HashMap<String, Integer>> boarderMap = new HashMap<>();
        HashMap<String, String> idMap = new HashMap<>();
        idMap = getIDs();
        boarderMap = getInfoBorders(idMap);
        HashMap<String, String> idMapFinal = new HashMap<>();


        //for (String key : getInfoBorders().keySet()) {

        //}


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

    public HashMap<String, HashMap<String, Integer>> getInfoBorders(HashMap<String, String> IDs) {

        HashMap<String, HashMap<String, Integer>> map = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("borders.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] origin = line.split(" = ");
                if (origin.length > 1) {
                    String[] destinationsArr = origin[1].split(" km; ");
                    HashMap<String, Integer> destMap = new HashMap<>();
                    for (int i = 0; i < destinationsArr.length; i++) {
                        String[] dest = destinationsArr[i].split(" ");
                        //System.out.println(Arrays.toString(dest));
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
                    String check = IDs.get(origin[0].toLowerCase());

                    if (check == null) {
                        System.out.println("BAD " + origin[0]);
                    } else {
                        System.out.println("Worked!!");
                    }
                    map.put(origin[0].toLowerCase(), destMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            exit(0);
        }

        //FOR PRINTING THE MAP:
        for (Map.Entry<String, HashMap<String, Integer>> outerEntry : map.entrySet()) {
            System.out.println(outerEntry.getKey());

            // Iterate over the inner map
            for (Map.Entry<String, Integer> innerEntry : outerEntry.getValue().entrySet()) {
                System.out.println("    " + innerEntry.getKey() + ", " + innerEntry.getValue());
            }
        }

        return map;
    }

    public void getDistances() {
        try (BufferedReader reader = new BufferedReader(new FileReader("capdist.csv"))) {
            String line;
            HashMap<String, String[]> distanceMap = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                String[] capInfo = line.split(",");
                String country1 = capInfo[1];
                String country2 = capInfo[3];

                String kilom = capInfo[4];
                String[] arr = {country2, kilom};

                distanceMap.put(country1, arr);
            }

        } catch (Exception e) {
            e.printStackTrace();
            exit(0);
        }
    }

    public HashMap<String, String> getIDs() {
        HashMap<String, String> map = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("state_name.tsv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("2020-12-31")) {
                    String[] lineArr = line.split("\t");
                    map.put(lineArr[2].toLowerCase(), lineArr[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            exit(0);
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }


        return map;
    }




}

