package util;

import org.mongodb.morphia.query.Query;

import java.util.List;

/**
 * Pagination
 *
 * @author maasdianto
 *         create on 12/13/2014
 */
public class Pagination<T> {

    private long totalRows = 0;    // Total number of items (database results)
    private int pageSize = 10;   // Max number of items you want shown per page
    private int numLinks = 3;    // Number of "digit" links to show before/after the currently viewed page
    private int page = 1;    // The current page being viewed
    private List<T> items;

    /**
     * Create instance of pagination with given query
     *
     * @param page The page no to retrieve
     * @param query The morphia query object
     */
    public Pagination(int page, Query<T> query) {
        this.page = page;
        this.totalRows = query.countAll();
        this.items = query.offset(offset()).limit(pageSize).asList();
    }

    /**
     * Constructor overloading to customize row per page
     *
     * @param page The page no
     * @param pageSize The page size
     * @param query The morphia query object
     */
    public Pagination(int page, int pageSize, Query<T> query) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalRows = query.countAll();
        this.items = query.offset(offset()).limit(pageSize).asList();
    }

    /**
     * Set num links
     *
     * @param numLinks The num link
     */
    public void setNumLinks(int numLinks) {
        this.numLinks = numLinks;
    }

    // GETTER METHOD

    public int offset() {
        return (page - 1) * pageSize;
    }

    public int itemSize() {
        return items.size();
    }

    public long totalRows() {
        return totalRows;
    }

    public int page() {
        return page;
    }

    public int start() {
        return page > numLinks ? (page - numLinks) : 1;
    }

    public int end() {
        return (page + numLinks) < totalPage() ? page + numLinks : totalPage();
    }

    public List<T> items() {
        return items;
    }

    public int totalPage() {
        return (int) (totalRows - 1) / pageSize + 1;
    }

    public boolean first() {
        return page <= 1;
    }

    public boolean last() {
        return page >= totalPage();
    }

    public int prev() {
        return page > 1 ? page - 1 : 1;
    }

    public int next() {
        return page < totalPage() ? page + 1 : totalPage();
    }
}