package gov.zndev.reviewlogclient.services;

import java.util.List;

public interface UpdateServiceResponseInterface {

    void updates(String message,boolean status,List<TableUpdate> list);

}
