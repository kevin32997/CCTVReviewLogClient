package gov.zndev.reviewlogclient.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
public class Incident {

    private int id;
    private int reviewLogId;
    private Date incidentDate;
    private String day;
    private String time;
    private String description;

}
