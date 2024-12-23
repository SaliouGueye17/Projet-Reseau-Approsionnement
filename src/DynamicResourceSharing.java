import java.util.ArrayList;
import java.util.List;

public class DynamicResourceSharing {
    private int[] parent;
    private int[] rank;
    private List<MergeStep> mergeSteps;
    private List<QueryRecord> queryRecords;

    public DynamicResourceSharing(int n) {
        parent = new int[n];
        rank = new int[n];
        mergeSteps = new ArrayList<>();
        queryRecords = new ArrayList<>();

        // Initialisation : chaque élément est son propre parent
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    // Trouve le parent de l'élément x
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Compression de chemin
        }
        return parent[x];
    }

    // Fusionne les clusters contenant x et y
    public void union(int x, int y, String labelX, String labelY) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX != rootY) {
            if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }

            // Enregistrer l'étape de fusion
            mergeSteps.add(new MergeStep(
                "Merge",
                List.of(labelX, labelY),
                "Cluster " + (find(x) + 1)
            ));
        }
    }

    // Enregistre une requête
    public void addQuery(String query, boolean result) {
        queryRecords.add(new QueryRecord(query, result ? "Yes" : "No"));
    }

    // Récupère les étapes de fusion
    public List<MergeStep> getMergeSteps() {
        return mergeSteps;
    }

    // Récupère les requêtes et leurs résultats
    public List<QueryRecord> getQueryRecords() {
        return queryRecords;
    }
}
