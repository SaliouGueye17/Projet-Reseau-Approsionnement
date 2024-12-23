public class QueryRecord {
    private String query;
    private String result;

    public QueryRecord(String query, String result) {
        this.query = query;
        this.result = result;
    }

    public String getQuery() {
        return query;
    }

    public String getResult() {
        return result;
    }
}
