class TransferRecord {
    private int from; // ID de l'entrepôt source
    private int to;   // ID de l'entrepôt destination
    private int units; // Nombre d'unités transférées

    public TransferRecord(int from, int to, int units) {
        this.from = from;
        this.to = to;
        this.units = units;
    }

    public int getFromId() {
        return from;
    }

    public int getToId() {
        return to;
    }

    public int getUnits() {
        return units;
    }
}
