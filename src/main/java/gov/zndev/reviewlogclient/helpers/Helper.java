package gov.zndev.reviewlogclient.helpers;


import com.google.gson.internal.LinkedTreeMap;
import gov.zndev.reviewlogclient.models.Incident;
import gov.zndev.reviewlogclient.models.Personnel;
import gov.zndev.reviewlogclient.models.ReviewLog;
import gov.zndev.reviewlogclient.models.User;
import gov.zndev.reviewlogclient.models.other.RepoInterface;
import gov.zndev.reviewlogclient.repositories.incidents.IncidentRepository;
import gov.zndev.reviewlogclient.repositories.reviewlogs.ReviewLogsRepository;
import gov.zndev.reviewlogclient.services.TableUpdate;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;
import org.springframework.context.annotation.Bean;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Helper {


    public static final int SORTTYPE_ASCENDING = 0;
    public static final int SORTTYPE_DESCENDING = 1;

    public static final FXMLLoader CreateLoader(String src, Class<?> object) {
        return new FXMLLoader(object.getClassLoader().getResource(src));
    }

    public static final ValidationSupport CreateValidationSupport() {
        return new ValidationSupport();
    }


    public static final List<Personnel> CastToPersonnelList(List<?> list) {
        List<Personnel> convertedList = new ArrayList<>();
        for (Object object : list) {
            LinkedTreeMap map = (LinkedTreeMap) object;
            Personnel personnel = new Personnel();
            personnel.setId((map.get("id") != null) ? (int) Double.parseDouble(map.get("id").toString()) : 0);
            personnel.setFirstName((map.get("firstName") != null) ? map.get("firstName").toString() : "");
            personnel.setLastName((map.get("lastName") != null) ? map.get("lastName").toString() : "");

            personnel.setMiddleName((map.get("middleName") != null) ? map.get("middleName").toString() : "");

            personnel.setOffice((map.get("office") != null) ? map.get("office").toString() : "");
            personnel.setPosition((map.get("position") != null) ? map.get("position").toString() : "");
            personnel.setAddedBy((map.get("addedBy") != null) ? (int) Double.parseDouble(map.get("addedBy").toString()) : 0);

            try {
                DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                personnel.setDateCreated(utcFormat.parse(map.get("dateCreated").toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            convertedList.add(personnel);
        }

        return convertedList;
    }

    public static final List<Incident> CastToIncidentList(List<?> list) {
        List<Incident> convertedList = new ArrayList<>();
        for (Object object : list) {
            LinkedTreeMap map = (LinkedTreeMap) object;
            Incident incident = new Incident();
            incident.setId((map.get("id") != null) ? (int) Double.parseDouble(map.get("id").toString()) : 0);
            incident.setReviewLogId((map.get("reviewLogId") != null) ? (int) Double.parseDouble(map.get("reviewLogId").toString()) : 0);
            incident.setDay((map.get("day") != null) ? map.get("day").toString() : "");
            incident.setTime((map.get("time") != null) ? map.get("time").toString() : "");
            incident.setDescription((map.get("incidentDescription") != null) ? map.get("incidentDescription").toString() : "");

            try {
                incident.setIncidentDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(map.get("incidentDate").toString()));

            } catch (ParseException e) {
                e.printStackTrace();
            }


            convertedList.add(incident);
        }

        return convertedList;
    }


    public static final List<ReviewLog> CastToReviewLogList(List<?> list) {
        List<ReviewLog> convertedList = new ArrayList<>();
        for (Object object : list) {
            LinkedTreeMap map = (LinkedTreeMap) object;
            ReviewLog reviewLog = new ReviewLog();
            reviewLog.setId((map.get("id") != null) ? (int) Double.parseDouble(map.get("id").toString()) : 0);
            reviewLog.setIncidentDescription((map.get("incidentDescription") != null) ? map.get("incidentDescription").toString() : "");
            reviewLog.setInclusiveDates((map.get("inclusiveDates") != null) ? map.get("inclusiveDates").toString() : "");
            reviewLog.setReviewerId((map.get("reviewerId") != null) ? (int) Double.parseDouble(map.get("reviewerId").toString()) : 0);
            reviewLog.setReviewerName((map.get("reviewerName") != null) ? map.get("reviewerName").toString() : "");
            try {
                DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                reviewLog.setReviewDate(utcFormat.parse(map.get("reviewDate").toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            LinkedTreeMap per_map = (LinkedTreeMap) map.get("personnel");
            if (per_map != null) {
                Personnel personnel = new Personnel();
                personnel.setId((per_map.get("id") != null) ? (int) Double.parseDouble(per_map.get("id").toString()) : 0);
                personnel.setFirstName((per_map.get("firstName") != null) ? per_map.get("firstName").toString() : "");
                personnel.setLastName((per_map.get("lastName") != null) ? per_map.get("lastName").toString() : "");
                personnel.setMiddleName((per_map.get("middleName") != null) ? per_map.get("middleName").toString() : "");
                personnel.setOffice((per_map.get("office") != null) ? per_map.get("office").toString() : "");
                personnel.setPosition((per_map.get("position") != null) ? per_map.get("position").toString() : "");
                personnel.setAddedBy((per_map.get("addedBy") != null) ? (int) Double.parseDouble(per_map.get("addedBy").toString()) : 0);
                try {
                    DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                    personnel.setDateCreated((per_map.get("addedBy") != null) ? utcFormat.parse(per_map.get("dateCreated").toString()) : null);


                } catch (ParseException e) {
                    e.printStackTrace();
                }

                reviewLog.setPersonnel(personnel);
            }
            convertedList.add(reviewLog);
        }

        return convertedList;
    }


    public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc),
                new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }

    public static final List<User> CastToUserList(List<?> list) {
        List<User> convertedList = new ArrayList<>();
        for (Object object : list) {
            LinkedTreeMap map = (LinkedTreeMap) object;
            User user = new User();
            user.setId((map.get("id") != null) ? (int) Double.parseDouble(map.get("id").toString()) : 0);
            user.setFullname((map.get("fullname") != null) ? map.get("fullname").toString() : "");
            user.setUsername((map.get("username") != null) ? map.get("username").toString() : "");
            user.setPassword((map.get("password") != null) ? map.get("password").toString() : "");
            user.setUserRole((map.get("userRole") != null) ? map.get("userRole").toString() : "");
            try {
                DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                user.setDateCreated(utcFormat.parse(map.get("dateCreated").toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            convertedList.add(user);
        }

        return convertedList;
    }

    public static final List<TableUpdate> CastToUpdatesList(List<?> list) {
        List<TableUpdate> convertedList = new ArrayList<>();
        for (Object object : list) {
            LinkedTreeMap map = (LinkedTreeMap) object;
            TableUpdate tableUpdate = new TableUpdate();


            tableUpdate.setTableName((map.get("tableName") != null) ? map.get("tableName").toString() : "");


            tableUpdate.setLastUpdate((map.get("tableName") != null) ? map.get("lastUpdate").toString() : "");


            convertedList.add(tableUpdate);
        }

        return convertedList;
    }

    public static final void UpdateConfigurations() throws IOException {
        File configFile = new File("config.properties");
        Properties props = new Properties();
        props.setProperty("base_url", ResourceHelper.BASE_URL);
        FileWriter writer = new FileWriter(configFile);
        props.store(writer, "Server Settings");
        writer.close();
    }


//
//    public static final List<Item> CastToItemList(List<?> list) {
//        List<Item> items = new ArrayList<>();
//        for (Object object : list) {
//            LinkedTreeMap map = (LinkedTreeMap) object;
//            Item item = new Item();
//            item.setId((int) Double.parseDouble(map.get("id").toString()));
//            item.setName(map.get("name").toString());
//            item.setBrandId((int) Double.parseDouble(map.get("brandId").toString()));
//            item.setBrandName((map.get("brandName") != null) ? map.get("brandName").toString() : "");
//            item.setTypeId((int) Double.parseDouble(map.get("typeId").toString()));
//            item.setTypeName((map.get("typeName") != null) ? map.get("typeName").toString() : "");
//            item.setModel((map.get("model") != null) ? map.get("model").toString() : "");
//            item.setDescription((map.get("description") != null) ? map.get("description").toString() : "");
//            item.setCreatedBy((map.get("createdBy") != null) ? (int) Double.parseDouble(map.get("createdBy").toString()) : 0);
//            try {
//                item.setDateCreated(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(map.get("dateCreated").toString()));
//                item.setDateUpdated(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(map.get("dateUpdated").toString()));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//
//            try {
//                item.setDateCreated(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(map.get("dateCreated").toString()));
//                item.setDateUpdated(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(map.get("dateUpdated").toString()));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            items.add(item);
//        }
//
//        return items;
//    }
//
//    public static final List<Brand> CastToBrandList(List<?> list) {
//        List<Brand> brands = new ArrayList<>();
//        for (Object object : list) {
//            LinkedTreeMap map = (LinkedTreeMap) object;
//
//            Brand brand = new Brand();
//            brand.setId((int) Double.parseDouble(map.get("id").toString()));
//            brand.setName(map.get("name").toString());
//            brand.setDescription(map.get("description").toString());
//            try {
//                brand.setDateCreated(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(map.get("dateCreated").toString()));
//                brand.setDateUpdated(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(map.get("dateUpdated").toString()));
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            brands.add(brand);
//        }
//
//        return brands;
//    }
//
//    public static final List<Type> CastToTypeList(List<?> list) {
//        List<Type> types = new ArrayList<>();
//        for (Object object : list) {
//            LinkedTreeMap map = (LinkedTreeMap) object;
//            Type type = new Type();
//            type.setId((int) Double.parseDouble(map.get("id").toString()));
//            type.setName(map.get("name").toString());
//            type.setDescription(map.get("description").toString());
//            try {
//                type.setDateCreated(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(map.get("dateCreated").toString()));
//                type.setDateUpdated(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(map.get("dateUpdated").toString()));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            types.add(type);
//        }
//        return types;
//    }
//
//
//    public static final List<Location> CastToLocationList(List<?> list) {
//        List<Location> locations = new ArrayList<>();
//        for (Object object : list) {
//            LinkedTreeMap map = (LinkedTreeMap) object;
//            Location location = new Location();
//            location.setId((int) Double.parseDouble(map.get("id").toString()));
//            location.setName(map.get("name").toString());
//            location.setDescription(map.get("description").toString());
//            location.setCreatedBy((int) Double.parseDouble(map.get("id").toString()));
//            try {
//                location.setDateCreated(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(map.get("dateCreated").toString()));
//                location.setDateUpdated(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(map.get("dateUpdated").toString()));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            locations.add(location);
//        }
//        return locations;
//    }
//
//    public static final List<InventoryActivityReference> CastToInvActivityReference(List<?> list) {
//        List<InventoryActivityReference> references = new ArrayList<>();
//        for (Object object : list) {
//            LinkedTreeMap map = (LinkedTreeMap) object;
//            InventoryActivityReference reference = new InventoryActivityReference();
//            reference.setId((int) Double.parseDouble(map.get("id").toString()));
//            reference.setUserId((map.get("userId") != null) ? (int) Double.parseDouble(map.get("userId").toString()) : 0);
//            reference.setAction((map.get("action") != null) ? map.get("action").toString() : "");
//            reference.setConsignee((map.get("consignee") != null) ? map.get("consignee").toString() : "");
//            reference.setLocationId((map.get("locationId") != null) ? (int) Double.parseDouble(map.get("locationId").toString()) : 0);
//            reference.setLocationFromId((map.get("locationFromId") != null) ? (int) Double.parseDouble(map.get("locationFromId").toString()) : 0);
//
//            reference.setReference((map.get("reference") != null) ? map.get("reference").toString() : "");
//
//            reference.setRemarks((map.get("remarks") != null) ? map.get("remarks").toString() : "");
//            try {
//                reference.setActivityDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(map.get("activityDate").toString()));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            references.add(reference);
//        }
//        return references;
//    }
//
//    public static final List<InventoryActivity> CastToInventoryActivity(List<?> list) {
//        List<InventoryActivity> activities = new ArrayList<>();
//        for (Object object : list) {
//            LinkedTreeMap map = (LinkedTreeMap) object;
//            InventoryActivity activity = new InventoryActivity();
//            activity.setId((int) Double.parseDouble(map.get("id").toString()));
//            activity.setReferenceId((map.get("referenceId") != null) ? (int) Double.parseDouble(map.get("referenceId").toString()) : 0);
//            activity.setTotal((map.get("total") != null) ? (int) Double.parseDouble(map.get("total").toString()) : 0);
//            activity.setItemId((map.get("itemId") != null) ? (int) Double.parseDouble(map.get("itemId").toString()) : 0);
//
//            activity.setInventoryId((map.get("inventoryId") != null) ? (int) Double.parseDouble(map.get("inventoryId").toString()) : 0);
//
//
//            activity.setLocationId((map.get("locationId") != null) ? (int) Double.parseDouble(map.get("locationId").toString()) : 0);
//            activity.setOtherLocationId((map.get("otherLocationId") != null) ? (int) Double.parseDouble(map.get("otherLocationId").toString()) : 0);
//
//            activity.setAction((map.get("action") != null) ? map.get("action").toString() : "");
//            try {
//                activity.setActivityDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(map.get("activityDate").toString()));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            activities.add(activity);
//        }
//        return activities;
//    }
//
//    public static final List<Inventory> CastToInventoryList(List<?> list) {
//        List<Inventory> inventories = new ArrayList<>();
//        for (Object object : list) {
//            LinkedTreeMap map = (LinkedTreeMap) object;
//            Inventory inventory = new Inventory();
//            inventory.setId((int) Double.parseDouble(map.get("id").toString()));
//            inventory.setItemId((map.get("itemId") != null) ? (int) Double.parseDouble(map.get("itemId").toString()) : 0);
//            inventory.setLocationId((map.get("locationId") != null) ? (int) Double.parseDouble(map.get("locationId").toString()) : 0);
//            inventory.setPropertyNumber((map.get("propertyNumber") != null) ? map.get("propertyNumber").toString() : "");
//            inventory.setSerialNumber((map.get("serialNumber") != null) ? map.get("serialNumber").toString() : "");
//            inventory.setCode((map.get("code") != null) ? map.get("code").toString() : "");
//            inventory.setCreatedBy((map.get("createdBy") != null) ? (int) Double.parseDouble(map.get("createdBy").toString()) : 0);
//            try {
//                inventory.setDateCreated(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(map.get("dateCreated").toString()));
//                inventory.setDateUpdated(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(map.get("dateUpdated").toString()));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            LinkedTreeMap item_map = (LinkedTreeMap) map.get("item");
//            if(item_map!=null) {
//                Item item = new Item();
//                item.setId((item_map.get("id") != null) ? (int) Double.parseDouble(item_map.get("id").toString()) : 0);
//                item.setName((item_map.get("name") != null) ? item_map.get("name").toString() : "");
//                item.setBrandId((item_map.get("brandId") != null) ? (int) Double.parseDouble(item_map.get("brandId").toString()) : 0);
//                item.setBrandName((item_map.get("brandName") != null) ? item_map.get("brandName").toString() : "");
//                item.setTypeId((item_map.get("typeId") != null) ? (int) Double.parseDouble(item_map.get("typeId").toString()) : 0);
//                item.setTypeName((item_map.get("typeName") != null) ? item_map.get("typeName").toString() : "");
//                item.setModel((item_map.get("model") != null) ? item_map.get("model").toString() : "");
//                item.setCreatedBy((item_map.get("createdBy") != null) ? (int) Double.parseDouble(map.get("createdBy").toString()) : 0);
//
//
//                try {
//                    item.setDateCreated((map.get("dateCreated") != null) ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(item_map.get("dateCreated").toString()) : new Date());
//                    item.setDateUpdated((map.get("dateUpdated") != null) ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(item_map.get("dateUpdated").toString()) : new Date());
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                inventory.setItem(item);
//            }
//            inventories.add(inventory);
//        }
//        return inventories;
//    }

    public static FXMLLoader LoadLayoutResource(String resource) {
        return new FXMLLoader(Helper.class.getClassLoader().getResource(resource));
    }


    public static void UpdateReviewLogInclusiveDates(ReviewLog reviewLog,HelperListener listener ) {
        // Get Inclusive dates
        new Thread(() -> {

            IncidentRepository incidentRepo = new IncidentRepository();
            ReviewLogsRepository reviewRepo = new ReviewLogsRepository();
            incidentRepo.getIncidentByReviewLog(reviewLog.getId(), (success, message, object) -> {
                if (success) {
                    List<Incident> incidents = (List<Incident>) object;
                    if(!incidents.isEmpty()){
                        List<Date> dates = new ArrayList<>();
                        for (Incident incident : incidents) {
                            dates.add(incident.getIncidentDate());
                        }

                        Collections.sort(dates);
                        DateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy");
                        reviewLog.setInclusiveDates(dateFormat.format(dates.get(0)) + " - " + dateFormat.format(dates.get(dates.size() - 1)));

                        reviewRepo.updateReviewLogInclusiveDates(reviewLog.getId(), reviewLog, new RepoInterface() {
                            @Override
                            public void activityDone(Boolean success, String message, Object object) {
                                if(listener!=null) {
                                    listener.UpdateReviewLogInclusiveDatesResult(true, message, object);
                                }
                            }
                        });
                    }
                }
            });
        }).start();
    }

    @Bean
    public static final Stage CreateStage(String title) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.getIcons().add(new Image("icon.png"));
        return stage;
    }

    public interface HelperListener{
        void UpdateReviewLogInclusiveDatesResult(boolean success,String message, Object obl);
    }

}
