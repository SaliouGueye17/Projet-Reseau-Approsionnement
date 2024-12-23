public class Warehouse {
    private int id;
    private double x, y;
    private int capacity;

    public Warehouse(int id, double x, double y, int capacity) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.capacity = capacity;
    }

    public int getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    @Override
    public String toString() {
        return "Warehouse{" +
               "id=" + id +
               ", x=" + x +
               ", y=" + y +
               ", capacity=" + capacity +
               '}';
    }
}
