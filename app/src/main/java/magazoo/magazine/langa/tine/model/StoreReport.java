package magazoo.magazine.langa.tine.model;

public class StoreReport {

    private String shopId;
    private String regards;
    private boolean howisit;
    private String reportedBy;
    private long reportedAt;

    public StoreReport() {
    }

    public StoreReport(String shopId, String regards, boolean howisit, String reportedBy, long reportedAt) {
        this.shopId = shopId;
        this.regards = regards;
        this.howisit = howisit;
        this.reportedBy = reportedBy;
        this.reportedAt = reportedAt;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getRegards() {
        return regards;
    }

    public void setRegards(String regards) {
        this.regards = regards;
    }

    public boolean isHowisit() {
        return howisit;
    }

    public void setHowisit(boolean howisit) {
        this.howisit = howisit;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public long getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(long reportedAt) {
        this.reportedAt = reportedAt;
    }

    @Override
    public boolean equals(Object o){
      if(o == this) return true;
        if(!(o instanceof StoreReport)){
            return false;
        }

        StoreReport report = (StoreReport) o;

        return report.getShopId().equals(shopId) && report.getReportedBy().equals(reportedBy)
                && report.getRegards().equals(regards) && report.isHowisit() == howisit;
    }
}

