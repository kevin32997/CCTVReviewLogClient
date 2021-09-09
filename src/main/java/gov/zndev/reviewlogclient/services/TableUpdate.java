package gov.zndev.reviewlogclient.services;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TableUpdate {

    private String tableName;
    private String lastUpdate;

}
