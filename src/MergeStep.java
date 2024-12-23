import java.util.List;

public class MergeStep {
    private String action;
    private List<String> cities;
    private String clusterAfterMerge;

    public MergeStep(String action, List<String> cities, String clusterAfterMerge) {
        this.action = action;
        this.cities = cities;
        this.clusterAfterMerge = clusterAfterMerge;
    }

    public String getAction() {
        return action;
    }

    public List<String> getCities() {
        return cities;
    }

    public String getClusterAfterMerge() {
        return clusterAfterMerge;
    }
}

