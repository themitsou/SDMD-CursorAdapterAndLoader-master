package gr.academic.city.sdmd.cursoradapterandloader;

/**
 * Created by Theofanis on 16/7/2017.
 */

public class Issues {
    private Issue[] issues;
    private Long total_count;
    private Long offset;
    private Long limit;


    public Issue[] getIssues() {
        return issues;
    }

    public void setIssues(Issue[] issues) {
        this.issues = issues;
    }

    public Long getTotal_count() {
        return total_count;
    }

    public void setTotal_count(Long total_count) {
        this.total_count = total_count;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Issue getIssue(Integer i) {
        return getIssues()[i];
    }

}
