package magazoo.magazine.langa.tine.model;

public class StoreReport {

    private String shopId;
    private boolean pos;
    private boolean nonstop;
    private boolean tickets;
    private boolean location;
    private long reportedAt;
    private String reportedBy;

    public StoreReport() {
    }

    public StoreReport(String shopId, boolean location, boolean pos, boolean nonstop, boolean tickets, long reportedAt, String reportedBy) {
        this.shopId = shopId;
        this.location = location;
        this.pos = pos;
        this.nonstop = nonstop;
        this.tickets = tickets;
        this.reportedAt = reportedAt;

        this.reportedBy = reportedBy;

    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public boolean isPos() {
        return pos;
    }

    public void setPos(boolean pos) {
        this.pos = pos;
    }

    public boolean isNonstop() {
        return nonstop;
    }

    public void setNonstop(boolean nonstop) {
        this.nonstop = nonstop;
    }

    public boolean isTickets() {
        return tickets;
    }

    public void setTickets(boolean tickets) {
        this.tickets = tickets;
    }

    public boolean isLocation() {
        return location;
    }

    public void setLocation(boolean location) {
        this.location = location;
    }

    public long getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(long reportedAt) {
        this.reportedAt = reportedAt;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }
}
