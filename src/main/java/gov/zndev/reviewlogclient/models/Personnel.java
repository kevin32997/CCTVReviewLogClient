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
public class Personnel {

    private int id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String office;
    private String position;
    private int addedBy;

    private IntegerProperty reviews = new SimpleIntegerProperty();

    public int getReviews() {
        return reviews.get();
    }

    public IntegerProperty reviewsProperty() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews.set(reviews);
    }

    private Date dateCreated;

    public String getFullName() {
        if(middleName.equals("")){
            return lastName.toUpperCase() + ", " + firstName.toUpperCase();
        }else {
            return lastName.toUpperCase() + ", " + firstName.toUpperCase() + " " + middleName.toUpperCase().charAt(0) + ".";
        }
    }

}
