import java.util.ArrayList;
import java.util.List;

public class AllocationRecord {
    public String city;        // ex: "City 1"
    public String priority;    // "High", "Medium", "Low"
    public List<AllocationPart> parts; // ex: [ {units=20,warehouse="W101"}, {units=30,warehouse="W102"} ]

    public AllocationRecord(String city, String priority) {
        this.city = city;
        this.priority = priority;
        this.parts = new ArrayList<>();
    }
}
