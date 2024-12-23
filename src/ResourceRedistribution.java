import java.util.*;

public class ResourceRedistribution {

    private List<TransferRecord> transfers;
    private PriorityQueue<Warehouse> maxHeap; // Entrepôts excédentaires
    private PriorityQueue<Warehouse> minHeap; // Entrepôts en manque

    public ResourceRedistribution() {
        transfers = new ArrayList<>();
        // Capacité décroissante
        maxHeap = new PriorityQueue<>((w1, w2) -> w2.getCapacity() - w1.getCapacity());
        // Capacité croissante
        minHeap = new PriorityQueue<>(Comparator.comparingInt(Warehouse::getCapacity));
    }

    public void initializeHeaps(List<Warehouse> wList) {
        for (Warehouse w : wList) {
            if (w.getCapacity() > 50) {
                maxHeap.add(w);
            } else if (w.getCapacity() < 50) {
                minHeap.add(w);
            }
        }
    }

    public void redistributeResources() {
        System.out.println("Resource Redistribution:");
        while (!maxHeap.isEmpty() && !minHeap.isEmpty()) {
            Warehouse from = maxHeap.poll();
            Warehouse to = minHeap.poll();

            int fromExcess = from.getCapacity() - 50;
            int toNeed = 50 - to.getCapacity();
            int transfer = Math.min(fromExcess, toNeed);

            from.setCapacity(from.getCapacity() - transfer);
            to.setCapacity(to.getCapacity() + transfer);

            System.out.println("Transferred " + transfer + " units from Warehouse "
                               + from.getId() + " to Warehouse " + to.getId());

           transfers.add(new TransferRecord(from.getId(), to.getId(), transfer));

            if (from.getCapacity() > 50) {
                maxHeap.add(from);
            } else if (from.getCapacity() < 50) {
                minHeap.add(from);
            }
            if (to.getCapacity() > 50) {
                maxHeap.add(to);
            } else if (to.getCapacity() < 50) {
                minHeap.add(to);
            }
        }
    }

    public List<TransferRecord> getTransfers() {
        return transfers;
    }
}
