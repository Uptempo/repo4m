/*
 * Copyright 2012 Uptempo Group Inc.
 */
package com.medselect.paypal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import urn.ebay.api.PayPalAPI.BAUpdateRequestType;
import urn.ebay.api.PayPalAPI.BAUpdateResponseType;
import urn.ebay.api.PayPalAPI.BillAgreementUpdateReq;
import urn.ebay.api.PayPalAPI.BillOutstandingAmountReq;
import urn.ebay.api.PayPalAPI.BillOutstandingAmountRequestType;
import urn.ebay.api.PayPalAPI.BillOutstandingAmountResponseType;
import urn.ebay.api.PayPalAPI.BillUserReq;
import urn.ebay.api.PayPalAPI.BillUserRequestType;
import urn.ebay.api.PayPalAPI.BillUserResponseType;
import urn.ebay.api.PayPalAPI.CreateRecurringPaymentsProfileReq;
import urn.ebay.api.PayPalAPI.CreateRecurringPaymentsProfileRequestType;
import urn.ebay.api.PayPalAPI.CreateRecurringPaymentsProfileResponseType;
import urn.ebay.api.PayPalAPI.DoReferenceTransactionReq;
import urn.ebay.api.PayPalAPI.DoReferenceTransactionRequestType;
import urn.ebay.api.PayPalAPI.DoReferenceTransactionResponseType;
import urn.ebay.api.PayPalAPI.GetBillingAgreementCustomerDetailsReq;
import urn.ebay.api.PayPalAPI.GetBillingAgreementCustomerDetailsRequestType;
import urn.ebay.api.PayPalAPI.GetBillingAgreementCustomerDetailsResponseType;
import urn.ebay.api.PayPalAPI.GetRecurringPaymentsProfileDetailsReq;
import urn.ebay.api.PayPalAPI.GetRecurringPaymentsProfileDetailsRequestType;
import urn.ebay.api.PayPalAPI.GetRecurringPaymentsProfileDetailsResponseType;
import urn.ebay.api.PayPalAPI.ManageRecurringPaymentsProfileStatusReq;
import urn.ebay.api.PayPalAPI.ManageRecurringPaymentsProfileStatusRequestType;
import urn.ebay.api.PayPalAPI.ManageRecurringPaymentsProfileStatusResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.UpdateRecurringPaymentsProfileReq;
import urn.ebay.api.PayPalAPI.UpdateRecurringPaymentsProfileRequestType;
import urn.ebay.api.PayPalAPI.UpdateRecurringPaymentsProfileResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.ActivationDetailsType;
import urn.ebay.apis.eBLBaseComponents.AddressType;
import urn.ebay.apis.eBLBaseComponents.AutoBillType;
import urn.ebay.apis.eBLBaseComponents.BillOutstandingAmountRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.BillingPeriodDetailsType;
import urn.ebay.apis.eBLBaseComponents.BillingPeriodDetailsType_Update;
import urn.ebay.apis.eBLBaseComponents.BillingPeriodType;
import urn.ebay.apis.eBLBaseComponents.CountryCodeType;
import urn.ebay.apis.eBLBaseComponents.CreateRecurringPaymentsProfileRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.CreditCardDetailsType;
import urn.ebay.apis.eBLBaseComponents.CreditCardNumberTypeType;
import urn.ebay.apis.eBLBaseComponents.CreditCardTypeType;
import urn.ebay.apis.eBLBaseComponents.CurrencyCodeType;
import urn.ebay.apis.eBLBaseComponents.DoReferenceTransactionRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.FailedPaymentActionType;
import urn.ebay.apis.eBLBaseComponents.ManageRecurringPaymentsProfileStatusRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.MerchantPullPaymentCodeType;
import urn.ebay.apis.eBLBaseComponents.MerchantPullPaymentType;
import urn.ebay.apis.eBLBaseComponents.MerchantPullStatusCodeType;
import urn.ebay.apis.eBLBaseComponents.PayerInfoType;
import urn.ebay.apis.eBLBaseComponents.PaymentActionCodeType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsType;
import urn.ebay.apis.eBLBaseComponents.PersonNameType;
import urn.ebay.apis.eBLBaseComponents.RecurringPaymentsProfileDetailsType;
import urn.ebay.apis.eBLBaseComponents.ReferenceCreditCardDetailsType;
import urn.ebay.apis.eBLBaseComponents.ScheduleDetailsType;
import urn.ebay.apis.eBLBaseComponents.StatusChangeActionType;
import urn.ebay.apis.eBLBaseComponents.UpdateRecurringPaymentsProfileRequestDetailsType;

import com.paypal.exception.ClientActionRequiredException;
import com.paypal.exception.HttpErrorException;
import com.paypal.exception.InvalidCredentialException;
import com.paypal.exception.InvalidResponseDataException;
import com.paypal.exception.MissingCredentialException;
import com.paypal.exception.SSLConfigurationException;
import com.paypal.sdk.exceptions.OAuthException;
/**
 * Class to connect with PayPal Recurring Payments.
 * @author karlo.smid@gmail.com
 */
public class PayPalRecurringPaymentsConnector {

  public PayPalRecurringPaymentsConnector(){
  
  }
/**
   * Create in PayPal recurrent billing using customer creditcard number.
   * @param params Map request parameters in form parameter name:parameter value.
   * @return ReturnMessage JSON format message with operation status and info message.

  public void CreateRecurringPaymentsProfile( Map<String, String> params ){
    CreateRecurringPaymentsProfileReq req = new CreateRecurringPaymentsProfileReq();
    CreateRecurringPaymentsProfileRequestType reqType = new CreateRecurringPaymentsProfileRequestType();
    //Populate Recurring Payments Profile Details
    RecurringPaymentsProfileDetailsType profileDetails = new RecurringPaymentsProfileDetailsType( params.get("billingStartDate") + "T00:00:00:000Z" );
    // Populate schedule details
    ScheduleDetailsType scheduleDetails = new ScheduleDetailsType();
    scheduleDetails.setDescription( params.get( "planDescription" ) );
    int frequency = Integer.parseInt( params.get( "frequency" ) );
    BasicAmountType paymentAmount = new BasicAmountType( currency, params.get( "costPerPeriod") );
    BillingPeriodType period = BillingPeriodType.fromValue( params.get("frequency") );
    int numCycles = Integer.parseInt( "0" );
    BillingPeriodDetailsType paymentPeriod = new BillingPeriodDetailsType( period, frequency, paymentAmount );
    paymentPeriod.setTotalBillingCycles( numCycles );
    paymentPeriod.setShippingAmount( new BasicAmountType( currency, "0.0" ) );
    paymentPeriod.setTaxAmount(new BasicAmountType( currency, "0.0" ) );
    scheduleDetails.setPaymentPeriod( paymentPeriod );
    CreateRecurringPaymentsProfileRequestDetailsType reqDetails = new CreateRecurringPaymentsProfileRequestDetailsType(
                                                                  profileDetails, scheduleDetails);
    // Set EC-Token or Credit card details
    CreditCardDetailsType cc = new CreditCardDetailsType();
    cc.setCreditCardNumber(params.get("creditCardNumber"));
    cc.setCVV2(params.get("cvv"));
    cc.setExpMonth(Integer.parseInt(params.get("expMonth")));
    cc.setExpYear(Integer.parseInt(params.get("expYear")));
    PayerInfoType payerInfo= new PayerInfoType();
    payerInfo.setPayer(params.get("BuyerEmailId"));
    cc.setCardOwner(payerInfo);
    CreditCardTypeType type = CreditCardTypeType.fromValue(params.get("creditCardType"));
    switch(type){
      case AMEX:
        cc.setCreditCardType(CreditCardTypeType.AMEX);
        break;
      case VISA:
        cc.setCreditCardType(CreditCardTypeType.VISA);
        break;
      case DISCOVER:
        cc.setCreditCardType(CreditCardTypeType.DISCOVER);
        break;
      case MASTERCARD:
        cc.setCreditCardType(CreditCardTypeType.MASTERCARD);
        break;
    }

    reqDetails.setCreditCard(cc);
    reqType.setCreateRecurringPaymentsProfileRequestDetails(reqDetails);
    req.setCreateRecurringPaymentsProfileRequest(reqType);
    CreateRecurringPaymentsProfileResponseType resp = service.createRecurringPaymentsProfile(req);
    if (resp != null) {
      session.setAttribute("lastReq", service.getLastRequest());
      session.setAttribute("lastResp", service.getLastResponse());
    }
    if (resp.getAck().toString().equalsIgnoreCase("SUCCESS")) {
      Map<Object, Object> map = new LinkedHashMap<Object, Object>();
      map.put("Ack", resp.getAck());
      map.put("Profile ID",
      resp.getCreateRecurringPaymentsProfileResponseDetails().getProfileID());
      map.put("Transaction ID",
      resp.getCreateRecurringPaymentsProfileResponseDetails().getTransactionID());
      map.put("Profile Status",
      resp.getCreateRecurringPaymentsProfileResponseDetails().getProfileStatus());
      session.setAttribute("map", map);
      response.sendRedirect(this.getServletContext().getContextPath()+"/Response.jsp");
    } else {
      session.setAttribute("Error", resp.getErrors());
      response.sendRedirect(this.getServletContext().getContextPath()+"/Error.jsp");
    }
  }*/
}
