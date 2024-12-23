import java.util.*;

public class EmergencySupplyNetwork {

    private List<City> cities;
    private List<Warehouse> warehouses;
    private double[][] costMatrix;

    // Permet de stocker les capacités APRES Tâche 2 (avant Tâche 3)
    public Map<Integer, Integer> postAllocationCapacities;

    // Stockage des allocations (si nécessaire pour la section JSON "Resource Allocation")
    private List<AllocationRecord> allocationRecords;

    public EmergencySupplyNetwork(List<City> cities, List<Warehouse> warehouses) {
        this.cities = cities;
        this.warehouses = warehouses;
        this.allocationRecords = new ArrayList<>();
        this.postAllocationCapacities = new HashMap<>();
    }

    public List<City> getCities() { return cities; }
    public List<Warehouse> getWarehouses() { return warehouses; }
    public List<AllocationRecord> getAllocationRecords() { return allocationRecords; }

    // Construction de la matrice de coûts
    public void buildCostMatrix() {
        int nC = cities.size();
        int nW = warehouses.size();
        costMatrix = new double[nC][nW];

        for (int i = 0; i < nC; i++) {
            City c = cities.get(i);
            for (int j = 0; j < nW; j++) {
                Warehouse w = warehouses.get(j);
                double dist = distance(c.getX(), c.getY(), w.getX(), w.getY());
                int coeff = getTransportCoefficient(dist);
                costMatrix[i][j] = dist * coeff;
            }
        }
    }
    
    public void printCostMatrix() {
    System.out.println("Cost Matrix:");
    for (int i = 0; i < costMatrix.length; i++) {
        System.out.print("City " + (i + 1) + " -> ");
        for (int j = 0; j < costMatrix[i].length; j++) {
            System.out.printf("%10.2f", costMatrix[i][j]); // Format numérique
        }
        System.out.println();
    }
}


    // Méthode d’aide (pour JSON) : costMatrix -> liste d'objets
    public List<Map<String,Object>> getCostMatrixAsList() {
        List<Map<String,Object>> result = new ArrayList<>();
        for (int i = 0; i < cities.size(); i++) {
            City c = cities.get(i);
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("City", "City " + c.getId());
            for (int j = 0; j < warehouses.size(); j++) {
                Warehouse w = warehouses.get(j);
                double cost = costMatrix[i][j];
                // On peut formater en . ou , selon besoin, ex:
                // row.put("Warehouse " + w.getId(), String.format("%.2f", cost));
                row.put("Warehouse " + w.getId(), String.format("%.2f", cost));
            }
            result.add(row);
        }
        return result;
    }

    // Allocation multi-entrepôts (Tâche 2)
    public void allocateResources() {
        PriorityQueue<City> pq = new PriorityQueue<>((c1, c2) ->
            Integer.compare(priorityValue(c2.getPriority()), priorityValue(c1.getPriority()))
        );
        pq.addAll(cities);

        while (!pq.isEmpty()) {
            City city = pq.poll();
            int cityIndex = cities.indexOf(city);
            int demandRem = city.getDemand();

            System.out.println("Allocating resources for City " + city.getId()
                               + " (Priority: " + city.getPriority() + ")");
            // On crée un record pour loguer
            AllocationRecord rec = new AllocationRecord("City " + city.getId(), city.getPriority());

            while (demandRem > 0) {
                int bestWhIndex = findBestWarehouse(cityIndex);
                if (bestWhIndex == -1) {
                    System.out.println("No available warehouse for City " + city.getId() 
                                       + " (remaining demand: " + demandRem + ")");
                    break;
                }
                Warehouse wh = warehouses.get(bestWhIndex);
                int canAlloc = Math.min(wh.getCapacity(), demandRem);
                wh.setCapacity(wh.getCapacity() - canAlloc);
                demandRem -= canAlloc;
                
                System.out.println("Allocated " + canAlloc + " units from Warehouse " + wh.getId());
                rec.parts.add(new AllocationPart(canAlloc, "Warehouse " + wh.getId()));
            }

            city.setDemand(demandRem);
            if (!rec.parts.isEmpty()) {
                allocationRecords.add(rec);
            }
        }

        System.out.println("Remaining Warehouse Capacities (just after Tâche 2):");
        for (Warehouse w : warehouses) {
            System.out.println("Warehouse " + w.getId() + ": " + w.getCapacity() + " units");
        }

        // --- On enregistre ces capacités dans la Map postAllocationCapacities ---
        postAllocationCapacities.clear();
        for (Warehouse w : warehouses) {
            postAllocationCapacities.put(w.getId(), w.getCapacity());
        }
    }

    // Cherche l'entrepôt avec le coût minimal pour cityIndex et capacity>0
    private int findBestWarehouse(int cityIndex) {
        double minCost = Double.MAX_VALUE;
        int bestIndex = -1;
        for (int j = 0; j < warehouses.size(); j++) {
            if (warehouses.get(j).getCapacity() <= 0) continue;
            double cst = costMatrix[cityIndex][j];
            if (cst < minCost) {
                minCost = cst;
                bestIndex = j;
            }
        }
        return bestIndex;
    }

    private int priorityValue(String p) {
        switch (p.toLowerCase()) {
            case "high":   return 3;
            case "medium": return 2;
            case "low":    return 1;
        }
        return 0;
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private static int getTransportCoefficient(double d) {
        if (d <= 10) return 1;
        if (d <= 20) return 2;
        return 3;
    }
}
