package magazoo.magazine.langa.tine.model;

public class ReportShop {

    private String shopId;
    private String reportedBy;
    private long reportedAt;
    private boolean location;
    private boolean nonstop;
    private boolean pos;
    private boolean tickets;

    public ReportShop(String shopId, String reportedBy, long reportedAt, boolean nonstop) {
        this.shopId = shopId;
        this.reportedBy = reportedBy;
        this.reportedAt = reportedAt;
        this.nonstop = nonstop;
    }
}
