public class City {
    private int id;
    private double x, y;
    private int demand;
    private String priority;  // "High", "Medium", "Low"

    public City(int id, double x, double y, int demand, String priority) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.demand = demand;
        this.priority = priority;
    }

    public int getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getDemand() { return demand; }
    public void setDemand(int demand) { this.demand = demand; }
    public String getPriority() { return priority; }

    @Override
    public String toString() {
        return "City{" +
               "id=" + id +
               ", x=" + x +
               ", y=" + y +
               ", demand=" + demand +
               ", priority='" + priority + '\'' +
               '}';
    }
}
