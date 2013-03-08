/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.doctor;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.collect.ImmutableMap;
import com.medselect.common.BaseManager;
import com.medselect.common.ReturnMessage;
import com.medselect.util.Constants;
import com.medselect.util.ValidationException;
import com.medselect.billing.BillingOfficeManager;
import com.medselect.staticlist.StaticlistManager;
import com.medselect.user.UserManager;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchException;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.SortExpression;
import com.google.appengine.api.search.SortOptions;

import java.util.List;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.HashMap;

import java.util.Map;
import org.json.JSONException;
/**
 * Class to upload Doctor values.
 * @author karlo.smid@gmail.com
 */
public class DoctorManager extends BaseManager {
  public static final String DOCTOR_ENTITY_NAME = "Doctor";
  public static final String DOCTOR_DISPLAY_NAME = "Doctor";
  public static final Map<String, BaseManager.FieldType> DOCTOR_STRUCTURE =
      new ImmutableMap.Builder<String,BaseManager.FieldType>()
          .put("billingOffice", BaseManager.FieldType.STRING)
          .put("firstName", BaseManager.FieldType.STRING)
          .put("lastName", BaseManager.FieldType.STRING)
          .put("email", BaseManager.FieldType.STRING)
          .put("title", BaseManager.FieldType.STRING_LIST)
          .put("speciality", BaseManager.FieldType.STRING_LIST)
          .put("education", BaseManager.FieldType.STRING)
          .put("photo", BaseManager.FieldType.STRING)
          .put("publicDescription", BaseManager.FieldType.STRING)
          .put("notes", BaseManager.FieldType.TEXT)
          .put("searchId", BaseManager.FieldType.STRING)
          .put("gaeKey", BaseManager.FieldType.STRING)

          .build();

  public DoctorManager() {
    super(DOCTOR_STRUCTURE, DOCTOR_ENTITY_NAME, DOCTOR_DISPLAY_NAME);
  }
/**
   * Inserts Doctor value into the database.
   * @param params Map request parameters in form parameter name:parameter value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage insertDoctorValue(Map<String, String> params) {
    String insertValueStatus = "SUCCESS";
    String message = "";

    //***Read the Doctor value information from the request
    String billingOffice = params.get("billingOffice");
    String firstName = params.get("firstName");
    String lastName = params.get("lastName");
    String email = params.get("email");
    String education = params.get("education");
    String publicDescription = params.get("publicDescription");
    String notes = params.get("notes");
    String speciality1 = params.get("speciality1");
    params.put("primarySpeciality", speciality1 );

    if( billingOffice == null || billingOffice.isEmpty() ){
      message = "billingOffice is mandatory parameter!";
      insertValueStatus = "FAILURE";
      return createReturnMessage( message, insertValueStatus );
    }
    else{
      Map<String,String> billingOfficeParams= new HashMap<String,String>();
      billingOfficeParams.put( "officeName", billingOffice );
      BillingOfficeManager billingOfficeManager = new BillingOfficeManager();
      ReturnMessage response = billingOfficeManager.readBillingOfficeValues( billingOfficeParams, billingOffice );
      message = response.getMessage();
      if ( message.equals( "FAILURE" ) ){
        insertValueStatus = "FAILURE";
        message = "billingOffice value: "+billingOffice+" does not exist in the system!";
        return createReturnMessage( message, insertValueStatus );
      }
    }
    if( firstName == null || firstName.isEmpty() ){
      message = "firstName is mandatory parameter!";
      insertValueStatus = "FAILURE";
      return createReturnMessage( message, insertValueStatus );
    }
    else if( lastName == null || lastName.isEmpty() ){
      message = "lastName is mandatory parameter!";
      insertValueStatus = "FAILURE";
      return createReturnMessage( message, insertValueStatus );
    }
    else if( education == null || education.isEmpty() ){
      message = "education is mandatory parameter!";
      insertValueStatus = "FAILURE";
      return createReturnMessage( message, insertValueStatus );
    }
    else if( email == null || email.isEmpty() ){
      message = "email is mandatory parameter!";
      insertValueStatus = "FAILURE";
      return createReturnMessage( message, insertValueStatus );
    }
    if( !this.dataValidator.isEmail( email ) ){
        message = "email is not valid!";
        insertValueStatus = "FAILURE";
        return createReturnMessage( message, insertValueStatus );
    }
    else{
      UserManager userManager = new UserManager(Constants.COMMON_APP);
      if (!userManager.doesUserExist(email)){
        message = "email value: " + email + "added to users!";
        Map<String, String> userParams = new HashMap<String,String>();
        userParams.put("email", email );
        userParams.put("firstName", firstName );
        userParams.put("lastName", lastName );
        userParams.put("key", email);
        ReturnMessage createUserResult = userManager.createUser(userParams);
        if (createUserResult.getStatus().equals("FAILURE")){
          return createUserResult;
        }
      }
    } 
    Key dsKey = KeyFactory.createKey(entityName, firstName+lastName+email);
    this.value = new Entity(dsKey);

    try{
      parseAndSetTitleAndSpecialityValues( params, this.value, true, true, false, "" );
    }
    catch( ValidationException validEx  ){
      message = validEx.getMessageList().get(0);
      insertValueStatus = "FAILURE";
      ReturnMessage.Builder builder = new ReturnMessage.Builder();
      ReturnMessage response = builder.status(insertValueStatus).message(message).value(null).build();
      return response;
    }
    String searchId = java.util.UUID.randomUUID().toString();
    params.put( "searchId", searchId );
    Map<String, String> keepParams = new HashMap<String,String>(params);
    ReturnMessage createResponse = this.doCreate( params, false, null );
    keepParams.put( "gaeKey", createResponse.getKey() );
    ReturnMessage updateResponse = updateDoctorValue(keepParams, createResponse.getKey() );
    LOGGER.info(updateResponse.getStatus());
    LOGGER.info(updateResponse.getMessage());
    Document doc = Document.newBuilder().setId( searchId )
          .addField(Field.newBuilder().setName("q").setText( firstName+" "+lastName+" "+speciality1 ))
          .addField(Field.newBuilder().setName("firstName").setText(firstName))
          .addField(Field.newBuilder().setName("lastName").setText(lastName))
          .addField(Field.newBuilder().setName("speciality").setText(speciality1))
          .addField(Field.newBuilder().setName("entityId").setText( createResponse.getKey() ))
          .build();
    Index doctorIndex = getIndex();
    doctorIndex.add( doc );
    return createResponse;
  }

/**
   * Parse and set title and speciality values
   * @param params Map of submited form parameters.
   * @param newValue is Entity object
   * @param replaceTitles boolean true is for repleace list elements, false is for add list elements
   * @param replaceSpeciality boolean true is for repleace list elements, false is for add list elements
   * @param update boolean true if update of data is in progress.
   * @param entityPrefix String perfix for Entity properties
   * @throws ValidationException
   * @return
   */
  public void parseAndSetTitleAndSpecialityValues(Map<String, String> params, Entity newValue, boolean replaceTitle, boolean replaceSpeciality, boolean update, String entityPrefix ) throws ValidationException
  {
    List<String> listTitles = new ArrayList<String>();
    List<String> listSpeciality = new ArrayList<String>();
    SortedSet<String> sortedParamKeys = new TreeSet<String>(params.keySet());
    for (String vKey : sortedParamKeys){
      if ( vKey.matches("^"+entityPrefix+"title[1-9][0-9]*") ){
        String keyValue = params.get( vKey );
        params.remove( vKey );
        if ( !isListValue( "TITLES", "COMMON", keyValue ) && !keyValue.equals("delete value") ){
          ValidationException validationException = new ValidationException();
          validationException.addMessage( "Unknown "+entityPrefix+"title value: " + keyValue );
          throw validationException;
        }else{
          if ( !keyValue.isEmpty() && !keyValue.equals("delete value") ){
            listTitles.add( keyValue );
          }
        }
      }
      else if( vKey.matches("^"+entityPrefix+"speciality[1-9][0-9]*") ){
        String keyValue = params.get( vKey );
        params.remove( vKey );
        if ( !isListValue( "SPECIALTIES", "COMMON", keyValue ) && !keyValue.equals("delete value") ){
          ValidationException validationException = new ValidationException();
          validationException.addMessage( "Unknown "+entityPrefix+"speciality value: " + keyValue );
          throw validationException;
        } else {
          if ( !keyValue.isEmpty() && !keyValue.equals("delete value") ){
            listSpeciality.add( keyValue );
          }
        }
      }
    }
    if ( !listTitles.isEmpty() ){
      if ( replaceTitle ){
        try{
          newValue.setProperty(entityPrefix+"title", listTitles);
        }
        catch( java.lang.IllegalArgumentException iae ){
          ValidationException validationException = new ValidationException();
          validationException.addMessage( iae.getMessage() );
          throw validationException;
        }
      }
      else{
        List<String> currentTitle = ( List<String> ) newValue.getProperty( entityPrefix+"title" );
        if ( currentTitle != null ){
          List<String> current = new ArrayList<String>( currentTitle );
          current.addAll( listTitles );
          try{
            newValue.setProperty(entityPrefix+"title", current );
          }
          catch( java.lang.IllegalArgumentException iae ){
            ValidationException validationException = new ValidationException();
            validationException.addMessage( iae.getMessage() );
            throw validationException; 
          }
        }
        else{
          try{
            newValue.setProperty(entityPrefix+"title", listTitles );
          }
          catch( java.lang.IllegalArgumentException iae ){
            ValidationException validationException = new ValidationException();
            validationException.addMessage( iae.getMessage() );
            throw validationException; 
          }
        }
      }
    }else{
      if ( update && replaceTitle ){
        try{
          newValue.setProperty( entityPrefix+"title", listTitles );
        }
        catch( java.lang.IllegalArgumentException iae ){
          ValidationException validationException = new ValidationException();
          validationException.addMessage( iae.getMessage() );
          throw validationException; 
        }
      }
    }
    if ( !listSpeciality.isEmpty() ){
      if ( replaceSpeciality ){
        params.put("primarySpeciality", listSpeciality.get(0) );
        try{
          newValue.setProperty(entityPrefix+"speciality", listSpeciality);
        }
        catch( java.lang.IllegalArgumentException iae ){
          ValidationException validationException = new ValidationException();
          validationException.addMessage( iae.getMessage() );
          throw validationException; 
        }
      }
      else{
        List<String> currentSpeciality = ( List<String> ) newValue.getProperty( entityPrefix+"speciality" );
        if ( currentSpeciality != null ){
          List<String> current = new ArrayList<String>( currentSpeciality );
          current.addAll( listSpeciality );
          try{
            newValue.setProperty( entityPrefix+"speciality", current );
          }
          catch( java.lang.IllegalArgumentException iae ){
            ValidationException validationException = new ValidationException();
            validationException.addMessage( iae.getMessage() );
            throw validationException; 
          }
        }
        else{
          try{
            newValue.setProperty( entityPrefix+"speciality", listSpeciality );
          }
          catch( java.lang.IllegalArgumentException iae ){
            ValidationException validationException = new ValidationException();
            validationException.addMessage( iae.getMessage() );
            throw validationException; 
          }
        }
      }
    }
    else{
      if ( params.get("gaeKey") == null ){
        ValidationException validationException = new ValidationException();
        validationException.addMessage( "At least one speciality is required!" );
        throw validationException;
      }
    }
  }
  
/**
   * Checks if value is present in static list 
   * @param forList String is code for static list.
   * @param forApp String is code for application to which static list belongs
   * @param value String value that we are looking for.
   * @return
   */
  private boolean isListValue( String forList, String forApp, String value ){
    Map<String,String> staticlistParams= new HashMap<String,String>();
    staticlistParams.put( "listCode", forList );
    staticlistParams.put( "listApp", forApp );
    StaticlistManager staticlistManager = new StaticlistManager();
    ReturnMessage response = staticlistManager.readStaticlistValues( staticlistParams, null );
    List<Map> values = response.getValueList();
    if ( values.size() != 1 ){
      return false;
    }
    else{
      List<String> listValue = ( List<String> ) values.get(0).get( "listValue" );
      return listValue.contains( value );
    }
  }
/**
   * Update Doctor value from the database based on value GAE key.
   * @param params Map request parameters in form parameter name:parameter value.
   * @param doctorKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage updateDoctorValue(Map<String, String> params, String doctorKey) {
    String UpdateStatus = "SUCCESS";
    String message = "";
    Key dsKey;
    
    String billingOffice = params.get("billingOffice");
    String firstName = params.get("firstName");
    String lastName = params.get("lastName");
    String email = params.get("email");
    String speciality1 = params.get("speciality1");
    
    Entity updateValue = null;

    if (doctorKey != null) {
      dsKey = KeyFactory.stringToKey(doctorKey);
      try {
        updateValue = ds.get(dsKey);
        params.put( "key", doctorKey );
      } catch (EntityNotFoundException ex) {
        LOGGER.warning("Doctor value identified by " + doctorKey + " does not exist.");
        message = "Doctor value identified by " + doctorKey + " does not exist.";
        UpdateStatus = "FAILURE";
      }
    }
    if( billingOffice == null || billingOffice.isEmpty() ){
      message = "billingOffice is mandatory parameter!";
      UpdateStatus = "FAILURE";
    }
    else{
      ReturnMessage response = this.doGet( billingOffice );
      this.value = updateValue;//because doGet sets this.value to retrieved entity
      UpdateStatus = response.getStatus();
      if ( UpdateStatus.equals( "FAILURE" ) ){
        message = response.getMessage();
      }
    }
    if( firstName == null || firstName.isEmpty() ){
      message = "firstName is mandatory parameter!";
      UpdateStatus = "FAILURE";
    }
    else if( lastName == null || lastName.isEmpty() ){
      message = "lastName is mandatory parameter!";
      UpdateStatus = "FAILURE";
    }
    else if( email == null || email.isEmpty() ){
      message = "email is mandatory parameter!";
      UpdateStatus = "FAILURE";
    }
    if( !this.dataValidator.isEmail( email ) ){
        message = "email is not valid!";
        String insertValueStatus = "FAILURE";
        return createReturnMessage( message, insertValueStatus );
    } else {
      UserManager userManager = new UserManager( "dummy" );
      if (!userManager.doesUserExist(email)){
        message = "email  value: "+email+"added to users!";
        Map<String, String> userParams = new HashMap<String,String>();
        userParams.put("email", email );
        userParams.put("firstName", firstName );
        userParams.put("lastName", lastName );
        userParams.put("key", email);
        userParams.put("password", email);
        ReturnMessage createUserResult = userManager.createUser( userParams );
        if ( createUserResult.getStatus().equals("FAILURE") ){
          return createUserResult;
        }
      }
    }
    if( UpdateStatus.equals( "FAILURE" ) ){
      ReturnMessage.Builder builder = new ReturnMessage.Builder();
      ReturnMessage response = builder.status(UpdateStatus).message(message).value(null).build();
      return response;
    }
    //***Set the updated values.
    if (UpdateStatus.equals("SUCCESS")) {
      //***Read the Doctor value information from the request
      String clearTitles = params.get("clearTitles");
      params.remove("clearTitles");
      String clearSpecialities = params.get("clearSpecialities");
      params.remove("clearSpecialities");
      
      boolean repleaceTitles = true; 
      if (clearTitles != null) {
        if ( clearTitles.toUpperCase().equals( "TRUE" ) ){
          repleaceTitles = true;
        }
        else{
          repleaceTitles = false;
        }
      }
      else{
        repleaceTitles = false;
      }
      boolean repleaceSpecialities = true; 
      if (clearSpecialities != null) {
        if ( clearSpecialities.toUpperCase().equals( "TRUE" ) ){
          repleaceSpecialities = true;
        }
        else{
          repleaceSpecialities = false;
        }
      }
      else{
        repleaceSpecialities = false;
      }
      try{
        parseAndSetTitleAndSpecialityValues( params, this.value, repleaceSpecialities, repleaceSpecialities, true, "" );
      }catch( ValidationException validEx ){
        return createReturnMessage( validEx.getMessageList().get(0), "FAILURE" );
      }
      boolean justGaeKey = false;
      if ( params.get("gaeKey") != null ){
        justGaeKey = true;
      }
      ReturnMessage updateResponse = this.doUpdate( params );
      if ( justGaeKey ){
        return updateResponse;
      }
      Document doc = Document.newBuilder().setId( (String) this.value.getProperty("searchId") )
          .addField(Field.newBuilder().setName("q").setText( firstName+" "+lastName+" "+speciality1 ))
          .addField(Field.newBuilder().setName("firstName").setText(firstName))
          .addField(Field.newBuilder().setName("lastName").setText(lastName))
          .addField(Field.newBuilder().setName("speciality").setText(speciality1))
          .addField(Field.newBuilder().setName("entityId").setText( doctorKey ))
          .build();
      Index doctorIndex = getIndex();
      doctorIndex.add( doc );
      return updateResponse;
    }
    return createReturnMessage( message, UpdateStatus );
  }
  
  /**
   * Update/create Doctor image from the database based on value GAE key.
   * @param photoKey String doctor image blob key.
   * @param doctorKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage updateCreateDoctorImage(String photoKey, String doctorKey) {
    String UpdateStatus = "SUCCESS";
    String message = "";
    Key dsKey;
    Entity updateValue = null;

    if (doctorKey != null) {
      dsKey = KeyFactory.stringToKey(doctorKey);
      try {
        updateValue = ds.get(dsKey);
      } catch (EntityNotFoundException ex) {
        LOGGER.warning("Doctor value identified by " + doctorKey + " does not exist.");
        message = "Doctor value identified by " + doctorKey + " does not exist.";
        UpdateStatus = "FAILURE";
        return createReturnMessage( message, UpdateStatus );
      }
    }
    if( photoKey == null || photoKey.isEmpty() ){
      message = "photo database key is mandatory parameter!";
      UpdateStatus = "FAILURE";
      return createReturnMessage( message, UpdateStatus );
    }
    String currentImage = (String) updateValue.getProperty( "photo" );
    ReturnMessage operationResult = deleteImageBy( currentImage );
    if ( operationResult.getStatus().equals( "FAILURE" ) ){
      return operationResult;
    }
    Map<String,String> params= new HashMap<String,String>();
    params.put( "photo", photoKey );
    params.put( "key", doctorKey );
    return this.doUpdate( params );
  }

/**
   * Deletes Doctor image based on image blob key.
   * @param  String image blob key.
   * @return ReturnMessage JSON format message with operation status, info message and Doctor data.
   */
  protected ReturnMessage deleteImageBy( String imageKey ){
    if( imageKey != null ){
      BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
      BlobKey blobKey = new BlobKey( imageKey );
      try{
        blobstoreService.delete( blobKey );
      }
      catch( com.google.appengine.api.blobstore.BlobstoreFailureException bsf ){
        String message = bsf.getMessage();
        String UpdateStatus = "FAILURE";
        return createReturnMessage( message, UpdateStatus );
      }
    }
    return createReturnMessage( "", "SUCCESS" );
  }
/**
   * Returns Doctor values from the database based on value GAE key.
   * @param params Map request parameters in form parameter name:parameter value.
   * @param itemKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status, info message and Doctor data.
   */
  public ReturnMessage readDoctorValues(Map<String, String> params, String itemKey) {
    //*** If this request was for a single value, return that request.
    String directionParam = params.get("direction");
    String sortBy = params.get("sortBy");
    String limit = params.get("length");
    int maxResults = 0;
    if (itemKey != null) {
      return this.doGet(itemKey);
    }
    try{
      maxResults = checkLengthParameter( limit );
    } catch(NumberFormatException ex){
        return createReturnMessage(ex.getMessage(), "FAILURE");
    }
    if ( sortBy != null ){
      if ( !sortBy.equals("speciality") && !sortBy.equals("firstName") && !sortBy.equals("lastName") ){
        return createReturnMessage( sortBy + " is not supported as sortBy parameter!", "FAILURE" ); 
      }
    } else {
      sortBy = "lastName";
    }
    if ( directionParam != null && !directionParam.isEmpty() ){
      if ( !directionParam.toUpperCase().equals( "ASC" ) && !directionParam.toUpperCase().equals( "DESC" ) ){
        return createReturnMessage( directionParam + " is wrong value for sort order!", "FAILURE" );
      }
    } else {
      directionParam = "ASC";
    }
    String fuzzyQ = params.get("q");
    List<Filter> doctorFilter = new ArrayList<Filter>();
    if ( fuzzyQ != null && !fuzzyQ.isEmpty() ){
      Results<ScoredDocument> fuzzyResults = findDocuments( fuzzyQ, maxResults );
      if (fuzzyResults != null) {
        for (ScoredDocument scoredDocument : fuzzyResults) {
          Filter fuzzyResultsFilter = this.createFilterForFormParameter( "gaeKey", scoredDocument.getOnlyField("entityId").getText() );
          if ( fuzzyResultsFilter != null ){
            doctorFilter.add( fuzzyResultsFilter );
          }
        }
      }
    }
    //** lets parse the filter parameters
    String billingOfficeKey = params.get("billingOffice");
    if ( billingOfficeKey != null && !billingOfficeKey.isEmpty() ){
      Filter billingOfficeFilter = this.createFilterForFormParameter( "billingOffice", billingOfficeKey );
      if ( billingOfficeFilter != null ){
        doctorFilter.add( billingOfficeFilter );
      }
    }
    //*** Assemble the query.
    if ( doctorFilter.size() == 1  ){
      q = new com.google.appengine.api.datastore.Query(entityName).setFilter( doctorFilter.get( 0 ) );
    } else if ( doctorFilter.size() > 1 ){
      Filter doctorCompositeFilter =
        CompositeFilterOperator.or( doctorFilter );
      q = new com.google.appengine.api.datastore.Query(entityName).setFilter( doctorCompositeFilter );
    }
    else{
      q = new com.google.appengine.api.datastore.Query( entityName );
    }
    directionParam = params.get("direction");
    //this direction is only for billingOffice filter.
    if ( directionParam != null && !directionParam.isEmpty() ){
      if (directionParam.equalsIgnoreCase("DESC")) {
        q.addSort(sortBy, SortDirection.DESCENDING);
      } else {
        q.addSort(sortBy, SortDirection.ASCENDING);
      }
    }
    return this.doRead(params, itemKey);
  }
/**
   * Delets Doctor values from the database based on value GAE key.
   * @param itemKey String is unique GAE entity key value.
   * @return ReturnMessage JSON format message with operation status and info message.
*/
  public ReturnMessage deleteDoctorValue(String itemKey) {
    Key dsKey = null;
    Entity deleteValue = null;
    ReturnMessage imageDeleteResult = updateCreateDoctorImage( "just_to_delete_current_image_blob", itemKey );
    if ( imageDeleteResult.getStatus().equals( "FAILURE" ) ){
      return imageDeleteResult;
    } else {
      if (itemKey != null) {
        dsKey = KeyFactory.stringToKey(itemKey);
        try {
          deleteValue = ds.get(dsKey);
        } catch (EntityNotFoundException ex) {
          LOGGER.warning("Doctor value identified by " + itemKey + " does not exist.");
        }
        getIndex().remove( (String) deleteValue.getProperty("searchId") );
      }
      return this.doDelete(itemKey, "email");
    }
  }
  /**
   * Returns Doctor search index.
   * @return Index is search API index.
   */
  protected Index getIndex() {
    IndexSpec indexSpec = IndexSpec.newBuilder().setName("doctor").build();
    return SearchServiceFactory.getSearchService().getIndex(indexSpec);
  }
  /**
   * Returns BillingOffice GAE key based on its namea
   * @param String officeName billingOffice name.
   * @return String is GAE billingOffice key.
   */
  protected String getOfficeGAEkeyFor( String officeName ) throws JSONException {
    Map<String,String> officeParams= new HashMap<String,String>();
    officeParams.put( "officeName", officeName );
    officeParams.put( "format", "list" );
    BillingOfficeManager officeManager = new BillingOfficeManager();
    ReturnMessage response = officeManager.readBillingOfficeValues( officeParams, null );
    if ( response.getMessage().equals( "Returned 1 Billing Offices." ) ){
      JSONArray jsonArray = null;
      String officeKey = null; 
      jsonArray = response.getValue().getJSONArray( "values" );
      for ( int i = 0; i < jsonArray.length(); i++)
      {
        JSONArray json = jsonArray.getJSONArray( i );
        officeKey = json.getString( json.length() - 1 );
      }
      return officeKey;
    }
    return null;
  }
/**
   * Returns list of ScoredDocument resulsts for search API query string.
   * @param String queryString is fuzzy string that contains firstName, lastName and first speciality.
   * @param int limit is limit for number of results.
   * @return Results<ScoredDocument> search API socred document results according to query string.
*/
  public Results<ScoredDocument> findDocuments(String queryString, int limit) {
    SortExpression.SortDirection direction = SortExpression.SortDirection.DESCENDING;

    if ( limit == 0 ){
      limit = 1000;
    }
    try {
      SortOptions sortOptions = SortOptions.newBuilder()
          .setLimit( limit )
          .build();
      QueryOptions options = QueryOptions.newBuilder()
          .setLimit(limit)
          .setFieldsToReturn("entityId")
          .setSortOptions(sortOptions)
          .build();
      com.google.appengine.api.search.Query query = com.google.appengine.api.search.Query.newBuilder().setOptions(options).build(queryString);
      return getIndex().search(query);
    } catch (SearchException e) {
      LOGGER.info( "Search request with query " + queryString + " failed: "+ e.getMessage() );
      return null;
    }
  }
}
