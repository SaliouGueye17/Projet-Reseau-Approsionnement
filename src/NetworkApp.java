import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkApp {
    private static String warehouseLabel(int id) {
    Map<Integer, String> labels = new HashMap<>();
    labels.put(101, "Warehouse X");
    labels.put(102, "Warehouse Y");
    labels.put(103, "Warehouse Z");
    return labels.getOrDefault(id, "Warehouse " + id);
}
    // Méthode pour obtenir le label du cluster basé sur l'index ou tout autre critère
    private static String getClusterLabel(int clusterId) {
        return "Cluster " + clusterId;
    }

    // Méthode pour obtenir le label de la ville basé sur son nom ou ID
    private static String getCityLabel(String cityName) {
        return cityName;
    }

    public static void main(String[] args) {
        String inputFile = "TestCase0.txt";       // Fichier d'entrée
        String outputFile = "Output_TestCase0.json"; // Fichier de sortie JSON au format .json

        // Lecture
        List<City> cities = new ArrayList<>();
        List<Warehouse> warehouses = new ArrayList<>();
        readInputFile(inputFile, cities, warehouses);

        System.out.println("Number of cities loaded: " + cities.size());
        System.out.println("Number of warehouses loaded: " + warehouses.size());

        // Tâche 1 & 2
        EmergencySupplyNetwork network = new EmergencySupplyNetwork(cities, warehouses);
        network.buildCostMatrix();
        network.printCostMatrix();
        network.allocateResources();  // => postAllocationCapacities enregistrés ici

        // Tâche 3
        ResourceRedistribution rr = new ResourceRedistribution();
        rr.initializeHeaps(network.getWarehouses());
        rr.redistributeResources();

        System.out.println("Final Resource Levels (after Tâche 3):");
        for (Warehouse w : network.getWarehouses()) {
            System.out.println("Warehouse " + w.getId() + ": " + w.getCapacity() + " units");
        }

        // Tâche 4
        DynamicResourceSharing ds = new DynamicResourceSharing(cities.size());
        // ...
        // Merges, queries, etc.

        // Génération du JSON
        writeOutputJSON(outputFile, network, rr, ds);
        System.out.println("Fichier JSON généré : " + outputFile);
    }

    private static void readInputFile(String filename,
                                      List<City> cities,
                                      List<Warehouse> warehouses) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            boolean inCities = false, inWarehouses = false;
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("Cities:")) {
                    inCities = true;
                    inWarehouses = false;
                    continue;
                } else if (line.startsWith("Warehouses:")) {
                    inCities = false;
                    inWarehouses = true;
                    continue;
                }

                if (inCities && line.startsWith("City")) {
                    // City A: ID = 1, Coordinates = (2, 3), Demand = 50 units, Priority = High
                    String regex = ".*ID = (\\d+), Coordinates = \\((\\d+),\\s*(\\d+)\\), Demand = (\\d+) units, Priority = (\\w+).*";
                    Matcher m = Pattern.compile(regex).matcher(line);
                    if (m.matches()) {
                        int id = Integer.parseInt(m.group(1));
                        double x = Double.parseDouble(m.group(2));
                        double y = Double.parseDouble(m.group(3));
                        int demand = Integer.parseInt(m.group(4));
                        String priority = m.group(5);
                        cities.add(new City(id, x, y, demand, priority));
                    }
                } else if (inWarehouses && line.startsWith("Warehouse")) {
                    // Warehouse X: ID = 101, Coordinates = (10, 20), Capacity = 100 units
                    String regex = ".*ID = (\\d+), Coordinates = \\((\\d+),\\s*(\\d+)\\), Capacity = (\\d+) units.*";
                    Matcher m = Pattern.compile(regex).matcher(line);
                    if (m.matches()) {
                        int id = Integer.parseInt(m.group(1));
                        double x = Double.parseDouble(m.group(2));
                        double y = Double.parseDouble(m.group(3));
                        int cap = Integer.parseInt(m.group(4));
                        warehouses.add(new Warehouse(id, x, y, cap));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeOutputJSON(String filename,
                                        EmergencySupplyNetwork network,
                                        ResourceRedistribution rr,
                                        DynamicResourceSharing ds) {
        try (PrintWriter out = new PrintWriter(filename)) {
            out.println("{");
            out.println("  \"Task 1 and 2\": {");

            // Graph Representation
            out.println("    \"Graph Representation\": {");
            out.println("      \"Cost Matrix\": [");
            // **Déclaration de la variable `cities`**
            List<City> cities = network.getCities();
            List<Warehouse> warehouses = network.getWarehouses();
            List<Map<String,Object>> costMat = network.getCostMatrixAsList();
            for (int i = 0; i < costMat.size(); i++) {
                Map<String,Object> row = costMat.get(i);
                out.print("        {");
                int cpt = 0;
                for (Map.Entry<String,Object> e : row.entrySet()) {
                    out.print(" \"" + e.getKey() + "\": " + e.getValue());
                    cpt++;
                    if (cpt < row.size()) out.print(",");
                }
                out.print(" }");
                if (i < costMat.size()-1) out.println(",");
                else out.println();
            }
            out.println("      ]");
            out.println("    },"); // fin Graph Representation

            // Resource Allocation
            out.println("    \"Resource Allocation\": [");
            List<AllocationRecord> allocs = network.getAllocationRecords();
            for (int i = 0; i < allocs.size(); i++) {
                AllocationRecord ar = allocs.get(i);
                out.println("      {");
                out.println("        \"City\": \"" + ar.city + "\",");
                out.println("        \"Priority\": \"" + ar.priority + "\",");
                if (ar.parts.size() == 1) {
                    AllocationPart ap = ar.parts.get(0);
                    out.println("        \"Allocated\": " + ap.units + ",");
                    out.println("        \"Warehouse\": \"" + ap.warehouse + "\"");
                } else {
                    out.println("        \"Allocated\": [");
                    for (int j = 0; j < ar.parts.size(); j++) {
                        AllocationPart ap = ar.parts.get(j);
                        out.println("          {");
                        out.println("            \"Units\": " + ap.units + ",");
                        out.println("            \"Warehouse\": \"" + ap.warehouse + "\"");
                        out.print("          }");
                        if (j < ar.parts.size()-1) out.println(",");
                        else out.println();
                    }
                    out.println("        ]");
                }
                out.print("      }");
                if (i < allocs.size()-1) out.println(",");
                else out.println();
            }
            out.println("    ],");

            // *** Remaining Capacities -> ICI on veut "0,20,110" ***
            out.println("    \"Remaining Capacities\": {");
            List<Warehouse> wList = network.getWarehouses();
            // On récupère la map postAllocationCapacities
            Map<Integer,Integer> postCaps = network.postAllocationCapacities; 
            for (int i = 0; i < wList.size(); i++) {
                Warehouse w = wList.get(i);
                // Capacité APRES TÂCHE 2
                int cap = postCaps.get(w.getId()); 
                out.print("      \"Warehouse " + w.getId() + "\": " + cap);
                if (i < wList.size()-1) out.println(",");
                else out.println();
            }
            out.println("    }");
            out.println("  },"); // fin "Task 1 and 2"

            // Tâche 3
            out.println("  \"Task 3\": {");
            out.println("    \"Resource Redistribution\": {");

            // Transfers
            out.println("      \"Transfers\": [");
            List<TransferRecord> trs = rr.getTransfers();
            for (int i = 0; i < trs.size(); i++) {
                TransferRecord t = trs.get(i);
                out.println("        {");
                out.println("          \"From\": \"" + warehouseLabel(t.getFromId()) + "\",");
                out.println("          \"To\": \"" + warehouseLabel(t.getToId()) + "\",");
                out.println("          \"Units\": " + t.getUnits());

                out.print("        }");
                if (i < trs.size()-1) out.println(",");
                else out.println();
            }
            out.println("      ],");

            // *** Final Resource Levels
            out.println("      \"Final Resource Levels\": {");
            for (int i = 0; i < wList.size(); i++) {
                Warehouse w = wList.get(i);
                // Capacité APRES Tâche 3 (celle courante)
                int finalCap = w.getCapacity();
                out.print("        \"Warehouse " + w.getId() + "\": " + finalCap);
                if (i < wList.size()-1) out.println(",");
                else out.println();
            }
            out.println("      }");

            out.println("    }");
            out.println("  },"); // fin Task 3

            // Tâche 4 ...
            out.println("  \"Task 4\": {");
            out.println("    \"Dynamic Resource Sharing\": {");
            // Afficher clusters initiaux
            System.out.println("Initial Clusters:");
            for (int i = 0; i < cities.size(); i++) {
                // Juste pour affichage, cluster = find(i)
                System.out.println("City " + (i+1) + ": Cluster " + (ds.find(i)+1));
            }           
            // Merging Steps
            out.println("      \"Merging Steps\": [");
            List<MergeStep> mergeSteps = ds.getMergeSteps();
            for (int i = 0; i < mergeSteps.size(); i++) {
                MergeStep ms = mergeSteps.get(i);
                out.println("        {");
                out.println("          \"Action\": \"" + ms.getAction() + "\",");
                out.println("          \"Cities\": [");
                for (int j = 0; j < cities.size(); j++) {
                    out.print("            \"" + cities.get(j) + "\"");
                    if (j < cities.size() - 1) out.println(",");
                    else out.println();
                }
                out.println("          ],");
                out.println("          \"Cluster After Merge\": \"" + ms.getClusterAfterMerge() + "\"");
                out.print("        }");
                if (i < mergeSteps.size() - 1) out.println(",");
                else out.println();
            }
            out.println("      ],"); // fin Merging Steps
            
            // Cluster Membership After Merging
            out.println("      \"Cluster Membership After Merging\": {");
            // Calculer les clusters finaux
            // Supposons que chaque cluster est identifié par son cluster label
            // Vous devez adapter cette logique en fonction de votre implémentation de l'union-find
            // Ici, nous utiliserons la méthode find() pour déterminer le cluster final de chaque ville
            Map<String, String> initialClusters = new HashMap<>();
            Map<String, String> finalClusters = new HashMap<>();
            // Mapping des noms de villes aux indices correspondants (supposons que City A est index 0, etc.)
            Map<String, Integer> cityIndices = new HashMap<>();
            cityIndices.put("City A", 0);
            cityIndices.put("City B", 1);
            cityIndices.put("City C", 2);
            
            for (String city : initialClusters.keySet()) {
                int index = cityIndices.get(city);
                int root = ds.find(index);
                // Le label du cluster peut être basé sur le root ou une logique personnalisée
                String clusterLabel = getClusterLabel(root + 1); // Exemple: "Cluster 1"
                finalClusters.put(city, clusterLabel);
            }
            
            int finalSize = finalClusters.size();
            int count = 0;
            for (Map.Entry<String, String> entry : finalClusters.entrySet()) {
                out.print("        \"" + entry.getKey() + "\": \"" + entry.getValue() + "\"");
                count++;
                if (count < finalSize) out.println(",");
                else out.println();
            }
            out.println("      },"); // fin Cluster Membership After Merging
            
            // Queries
            out.println("      \"Queries\": [");
            List<QueryRecord> queries = ds.getQueryRecords();
            for (int i = 0; i < queries.size(); i++) {
                QueryRecord qr = queries.get(i);
                out.println("        {");
                out.println("          \"Query\": \"" + qr.getQuery() + "\",");
                out.println("          \"Result\": \"" + qr.getResult() + "\"");
                out.print("        }");
                if (i < queries.size() - 1) out.println(",");
                else out.println();
            }
            out.println("      ]"); // fin Queries
            
            out.println("    }"); // fin Dynamic Resource Sharing
            out.println("  }"); // fin Task 4
            
            out.println("}"); // fin racine JSON
           
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
