package gm.mobi.android.db.model.objects;

/**
 * Created by InmaculadaAlcon on 14/08/2014.
 */
import java.util.Date;
public class AppAdvice extends Synchronized{

    protected Long mIdAppAdvice;
    protected String mPath;
    protected Long mIdMessage;
    protected Long mPlatform;
    protected Long mStatus;
    protected Long mVisibleButton;
    protected String mButtonAction;
    protected Long mButtonTextId;
    protected String mButtonData;
    protected Long mStartVersion;
    protected Long mEndVersion;
    protected Date mStartDate;
    protected Date mEndDate;
    protected Long mWeight;

    public AppAdvice(){}

    public Long getIdAppAdvice() {
        return mIdAppAdvice;
    }

    public void setIdAppAdvice(Long idAppAdvice) {
        mIdAppAdvice = idAppAdvice;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public Long getIdMessage() {
        return mIdMessage;
    }

    public void setIdMessage(Long idMessage) {
        mIdMessage = idMessage;
    }

    public Long getPlatform() {
        return mPlatform;
    }

    public void setPlatform(Long platform) {
        mPlatform = platform;
    }

    public Long getStatus() {
        return mStatus;
    }

    public void setStatus(Long status) {
        mStatus = status;
    }

    public Long getVisibleButton() {
        return mVisibleButton;
    }

    public void setVisibleButton(Long visibleButton) {
        mVisibleButton = visibleButton;
    }

    public String getButtonAction() {
        return mButtonAction;
    }

    public void setButtonAction(String buttonAction) {
        mButtonAction = buttonAction;
    }

    public Long getButtonTextId() {
        return mButtonTextId;
    }

    public void setButtonTextId(Long buttonTextId) {
        mButtonTextId = buttonTextId;
    }

    public Long getStartVersion() {
        return mStartVersion;
    }

    public void setStartVersion(Long startVersion) {
        mStartVersion = startVersion;
    }

    public Long getEndVersion() {
        return mEndVersion;
    }

    public void setEndVersion(Long endVersion) {
        mEndVersion = endVersion;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public void setEndDate(Date endDate) {
        mEndDate = endDate;
    }

    public Long getWeight() {
        return mWeight;
    }

    public void setWeight(Long weight) {
        mWeight = weight;
    }

    public String getButtonData() {
        return mButtonData;
    }

    public void setButtonData(String buttonData) {
        mButtonData = buttonData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppAdvice)) return false;
        if (!super.equals(o)) return false;

        AppAdvice appAdvice = (AppAdvice) o;

        if (mButtonAction != null ? !mButtonAction.equals(appAdvice.mButtonAction) : appAdvice.mButtonAction != null)
            return false;
        if (mButtonData != null ? !mButtonData.equals(appAdvice.mButtonData) : appAdvice.mButtonData != null)
            return false;
        if (mButtonTextId != null ? !mButtonTextId.equals(appAdvice.mButtonTextId) : appAdvice.mButtonTextId != null)
            return false;
        if (mEndDate != null ? !mEndDate.equals(appAdvice.mEndDate) : appAdvice.mEndDate != null)
            return false;
        if (mEndVersion != null ? !mEndVersion.equals(appAdvice.mEndVersion) : appAdvice.mEndVersion != null)
            return false;
        if (mIdAppAdvice != null ? !mIdAppAdvice.equals(appAdvice.mIdAppAdvice) : appAdvice.mIdAppAdvice != null)
            return false;
        if (mIdMessage != null ? !mIdMessage.equals(appAdvice.mIdMessage) : appAdvice.mIdMessage != null)
            return false;
        if (mPath != null ? !mPath.equals(appAdvice.mPath) : appAdvice.mPath != null) return false;
        if (mPlatform != null ? !mPlatform.equals(appAdvice.mPlatform) : appAdvice.mPlatform != null)
            return false;
        if (mStartDate != null ? !mStartDate.equals(appAdvice.mStartDate) : appAdvice.mStartDate != null)
            return false;
        if (mStartVersion != null ? !mStartVersion.equals(appAdvice.mStartVersion) : appAdvice.mStartVersion != null)
            return false;
        if (mStatus != null ? !mStatus.equals(appAdvice.mStatus) : appAdvice.mStatus != null)
            return false;
        if (mVisibleButton != null ? !mVisibleButton.equals(appAdvice.mVisibleButton) : appAdvice.mVisibleButton != null)
            return false;
        if (mWeight != null ? !mWeight.equals(appAdvice.mWeight) : appAdvice.mWeight != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIdAppAdvice != null ? mIdAppAdvice.hashCode() : 0);
        result = 31 * result + (mPath != null ? mPath.hashCode() : 0);
        result = 31 * result + (mIdMessage != null ? mIdMessage.hashCode() : 0);
        result = 31 * result + (mPlatform != null ? mPlatform.hashCode() : 0);
        result = 31 * result + (mStatus != null ? mStatus.hashCode() : 0);
        result = 31 * result + (mVisibleButton != null ? mVisibleButton.hashCode() : 0);
        result = 31 * result + (mButtonAction != null ? mButtonAction.hashCode() : 0);
        result = 31 * result + (mButtonTextId != null ? mButtonTextId.hashCode() : 0);
        result = 31 * result + (mButtonData != null ? mButtonData.hashCode() : 0);
        result = 31 * result + (mStartVersion != null ? mStartVersion.hashCode() : 0);
        result = 31 * result + (mEndVersion != null ? mEndVersion.hashCode() : 0);
        result = 31 * result + (mStartDate != null ? mStartDate.hashCode() : 0);
        result = 31 * result + (mEndDate != null ? mEndDate.hashCode() : 0);
        result = 31 * result + (mWeight != null ? mWeight.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AppAdvice{" +
                "mIdAppAdvice=" + mIdAppAdvice +
                ", mPath='" + mPath + '\'' +
                ", mIdMessage=" + mIdMessage +
                ", mPlatform=" + mPlatform +
                ", mStatus=" + mStatus +
                ", mVisibleButton=" + mVisibleButton +
                ", mButtonAction='" + mButtonAction + '\'' +
                ", mButtonTextId=" + mButtonTextId +
                ", mButtonData='" + mButtonData + '\'' +
                ", mStartVersion=" + mStartVersion +
                ", mEndVersion=" + mEndVersion +
                ", mStartDate=" + mStartDate +
                ", mEndDate=" + mEndDate +
                ", mWeight=" + mWeight +
                '}';
    }
}
