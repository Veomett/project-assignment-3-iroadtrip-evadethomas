import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IRoadTrip {


    public IRoadTrip (String [] args) {
        getInfoBorders();
        getDistances();
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

    public HashMap<String, HashMap<String, Double>> getInfoBorders() {

        HashMap<String, HashMap<String, Double>> map = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("borders.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                String[] origin = line.split(" = ");
                //System.out.println(origin[0]);
                if (origin.length > 1) {
                    String[] destinationsArr = origin[1].split(" km; ");
                    //System.out.println(Arrays.toString(destinationsArr));
                    HashMap<String, Double> destMap = new HashMap<>();
                    for (int i = 0; i < destinationsArr.length; i++) {
                        String[] dest = destinationsArr[i].split(" ");
                        //System.out.println(Arrays.toString(dest));
                        String finalDest = "";
                        Double distance = 0.0;
                        for (int j = 0; j < dest.length; j++) {
                            if (Character.isDigit(dest[j].charAt(0)) == false) {
                                finalDest += dest[j];
                                finalDest += " ";
                            } else {
                                String num = dest[j].replace(",", "");
                                distance = Double.parseDouble(num);
                                //System.out.println(distance);
                                break;
                            }
                        }
                        destMap.put(finalDest, distance);
                        finalDest = finalDest.trim();
                        //System.out.println(finalDest);
                    }
                    map.put(origin[0], destMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //FOR PRINTING THE MAP:
        for (Map.Entry<String, HashMap<String, Double>> outerEntry : map.entrySet()) {
            System.out.println(outerEntry.getKey());

            // Iterate over the inner map
            for (Map.Entry<String, Double> innerEntry : outerEntry.getValue().entrySet()) {
                System.out.println("    " + innerEntry.getKey() + ", " + innerEntry.getValue());
            }
        }

        return map;
    }

    public void getDistances() {

    }



}

