package gov.zndev.reviewlogclient.models.other;

public class Sort {

    public static final String ASCENDING = "ascending";
    public static final String DESCENDING = "descending";

    private String sortType;
    private String sortBy;

    public Sort() {

    }

    public Sort(String sortType, String sortBy) {
        this.sortType = sortType;
        this.sortBy = sortBy;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
