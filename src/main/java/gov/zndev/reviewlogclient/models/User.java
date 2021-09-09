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
@AllArgsConstructor
@NoArgsConstructor
public class User {

    public static final String ROLE_ADMIN="admin";
    public static final String ROLE_ENCODER="encoder";

    private int id;
    private String fullname;

    private String username;

    private String password;

    private String userRole;

    private Date dateCreated;

    private IntegerProperty reviewCount=new SimpleIntegerProperty();

    public int getReviewCount() {
        return reviewCount.get();
    }

    public IntegerProperty reviewCountProperty() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount.set(reviewCount);
    }
}
