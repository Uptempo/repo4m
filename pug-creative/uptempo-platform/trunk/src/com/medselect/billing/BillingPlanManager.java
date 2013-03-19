/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.billing;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.common.collect.ImmutableMap;
import com.medselect.common.BaseManager;
import com.medselect.common.ReturnMessage;
import com.medselect.util.ValidationException;
import com.medselect.util.ValidatorUtil;
import com.medselect.billing.BillingOfficeManager;
import com.medselect.staticlist.StaticlistManager;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilter;
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

import java.util.Date;
import java.text.DateFormat;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.HashMap;
import java.lang.Double;
import java.lang.Math;
import java.lang.Integer;
import java.lang.NumberFormatException;
import java.util.Map;
import org.json.JSONException;
import org.restlet.ext.json.JsonRepresentation;
import java.io.UnsupportedEncodingException;
/**
 * Class to upload BillingPlan values.
 * @author karlo.smid@gmail.com
 */
public class BillingPlanManager extends BaseManager {
  public static final String BILLINGPLAN_ENTITY_NAME = "BillingPlan";
  public static final String BILLINGPLAN_DISPLAY_NAME = "Billing Plan";
  public static final Map<String, BaseManager.FieldType> BILLINGPLAN_STRUCTURE =
      new ImmutableMap.Builder<String,BaseManager.FieldType>()
          .put("billingOffice", BaseManager.FieldType.STRING)
          .put("planDescription", BaseManager.FieldType.STRING)
          .put("frequency", BaseManager.FieldType.STRING)
          .put("costPerPeriod", BaseManager.FieldType.DECIMAL)
          .put("weeklyCycleDay", BaseManager.FieldType.STRING)
          .put("monthlyCycleDay", BaseManager.FieldType.INTEGER)
          .put("billingMethod", BaseManager.FieldType.STRING)
          .put("paypalToken", BaseManager.FieldType.STRING)

          .build();

  public BillingPlanManager() {
    super(BILLINGPLAN_STRUCTURE, BILLINGPLAN_ENTITY_NAME, BILLINGPLAN_DISPLAY_NAME);
  }
/**
   * Inserts Billing plan value into the database.
   * @param params Map request parameters in form parameter name:parameter value.
   * @return ReturnMessage JSON format message with operation status and info message.
   */
  public ReturnMessage insertBillingPlanValue(Map<String, String> params) {
    String insertValueStatus = "SUCCESS";
    String message = "";

    //***Read the Billing Plan value information from the request
    String billingOffice = params.get("billingOffice");
    String planDescription = params.get("planDescription");
    String frequency = params.get("frequency");
    String costPerPeriod = params.get("costPerPeriod");
    String weeklyCycleDay = params.get("weeklyCycleDay");
    String monthlyCyclePeriod = params.get("monthlyCyclePeriod");
    String billingMethod = params.get("billingMethod");
    Map<String, String> paypalParams = new HashMap<String,String>();

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
    if( frequency == null || frequency.isEmpty() ){
      message = "frequency is mandatory parameter!";
      insertValueStatus = "FAILURE";
      return createReturnMessage( message, insertValueStatus );
    }
    if ( !isListValue( "FREQUENCY", frequency ) ) {
      message = "frequency value must be one of FREQUENCY static list values!";
      insertValueStatus = "FAILURE";
      return createReturnMessage( message, insertValueStatus );
    }
    
    if( costPerPeriod == null || costPerPeriod.isEmpty() ){
      message = "costPerPeriod is mandatory parameter!";
      insertValueStatus = "FAILURE";
      return createReturnMessage( message, insertValueStatus );
    }
    String costPerPeriodTwoDecimals = null;
    try{
      double cost = Double.parseDouble( costPerPeriod );
      cost = (double)Math.round(cost * 100) / 100;
      costPerPeriodTwoDecimals = Double.toString( cost );
    } catch ( NumberFormatException nfe ){
      return createReturnMessage( nfe.getMessage(), "FAILURE" );
    }
    if( billingMethod == null || billingMethod.isEmpty() ){
      message = "billingMethod is mandatory parameter!";
      insertValueStatus = "FAILURE";
      return createReturnMessage( message, insertValueStatus );
    }
    if ( !isListValue( "BILLINGMETHOD", billingMethod ) ) {
      message = "billingMethod value must be one of BILLINGMETHOD static list values!";
      insertValueStatus = "FAILURE";
      return createReturnMessage( message, insertValueStatus );
    }
    if ( frequency.equals("weekly") ){
      if( weeklyCycleDay == null || weeklyCycleDay.isEmpty() ){
        message = "weeklyCycleDay is mandatory parameter when frequency is weekly!";
        insertValueStatus = "FAILURE";
        return createReturnMessage( message, insertValueStatus );
      } else {
        try{
          int cycle = Integer.parseInt( weeklyCycleDay );//calculate from current date first following cycle day
          if ( cycle > 7 || cycle < 1 ){
            return createReturnMessage( "weeklyCycleDay is out of [1,7] interval!", "FAILURE" );
          }
        } catch ( NumberFormatException nfe ){
          return createReturnMessage( nfe.getMessage()+ " for weeklyCycleDay!", "FAILURE" );
        }
      }
    } else if ( frequency.equals("monthly") ){
      if( monthlyCyclePeriod == null || monthlyCyclePeriod.isEmpty() ){
        message = "monthlyCyclePeriod is mandatory parameter when frequency is monthly!";
        insertValueStatus = "FAILURE";
        return createReturnMessage( message, insertValueStatus );
      } else {
        try{
          int cycle = Integer.parseInt( monthlyCyclePeriod );
          if ( cycle > 31 || cycle < 1 ){
            return createReturnMessage( "monthlyCycleDay is out of [1,31] interval!", "FAILURE" );
          }
        } catch ( NumberFormatException nfe ){
          return createReturnMessage( nfe.getMessage()+ " for monthlyCyclePeriod!", "FAILURE" );
        }
      }
    
    }
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date date = new Date();
    paypalParams.put( "billingStartDate", dateFormat.format(date) );
    paypalParams.put( "planDescription", planDescription );
    paypalParams.put( "costPerPeriod", costPerPeriod );
    paypalParams.put( "frequency", frequency );
    Key dsKey = KeyFactory.createKey(entityName, java.util.UUID.randomUUID().toString());
    this.value = new Entity(dsKey);

    ReturnMessage createResponse = this.doCreate( params, false, null );
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
        if ( !isListValue( "TITLES", keyValue ) && !keyValue.equals("delete value") ){
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
        if ( !isListValue( "SPECIALITIES", keyValue ) && !keyValue.equals("delete value") ){
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
   * @param value String value that we are looking for.
   * @return
   */
  private boolean isListValue( String forList, String value ){
    Map<String,String> staticlistParams= new HashMap<String,String>();
    staticlistParams.put( "listCode", forList );
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
      /*UserManager userManager = new UserManager( "dummy" );
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
      }*/
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
      doctorIndex.put(doc);
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
    String billingOfficeValue = params.get("billingOffice");
    String billingOfficeKey = null;
    if ( fuzzyQ == null || fuzzyQ.isEmpty() ){
      try{
        billingOfficeKey = getOfficeGAEkeyFor( billingOfficeValue );
      } catch( org.json.JSONException jsonEx ){
          return createReturnMessage( jsonEx.getMessage(), "BUG" );
      }
    }
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
        getIndex().delete( (String) deleteValue.getProperty("searchId") );
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
