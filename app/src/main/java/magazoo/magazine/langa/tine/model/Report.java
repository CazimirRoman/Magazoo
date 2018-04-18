package magazoo.magazine.langa.tine.model;

public class Report {

    private String shopId;
    private String regards;
    private boolean howIsIt;
    private String reportedBy;
    private long reportedAt;

    public Report(String shopId, String regards, boolean howIsIt, String reportedBy, long reportedAt) {
        this.shopId = shopId;
        this.regards = regards;
        this.howIsIt = howIsIt;
        this.reportedBy = reportedBy;
        this.reportedAt = reportedAt;
    }

    public Report(){

    }

    public String getRegards() {
        return regards;
    }

    public String getShopId() { return shopId; }

    public boolean getHowIsIt() {
        return howIsIt;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public long getReportedAt() {
        return reportedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Report)) {
            return false;
        }

        Report report = (Report) o;

        return report.getReportedBy().equals(reportedBy)
                && report.getRegards().equals(regards) && report.getHowIsIt() == howIsIt && report.getShopId().equals(shopId);
    }
}

