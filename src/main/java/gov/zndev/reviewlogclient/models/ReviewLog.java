package gov.zndev.reviewlogclient.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLog {
    private int id;
    private String reviewId;
    private int personnelId;
    private String personnelName;
    private String incidentDescription;
    private String inclusiveDates;
    private Date reviewDate;
    private String formattedReviewDate;
    private int reviewerId;
    private String reviewerName;
    private String reviewerCodeName;

    private Personnel personnel;

    private IntegerProperty incidentCount= new SimpleIntegerProperty();

    public int getIncidentCount() {
        return incidentCount.get();
    }

    public IntegerProperty incidentCountProperty() {
        return incidentCount;
    }

    public void setIncidentCount(int incidentCount) {
        this.incidentCount.set(incidentCount);
    }
}
