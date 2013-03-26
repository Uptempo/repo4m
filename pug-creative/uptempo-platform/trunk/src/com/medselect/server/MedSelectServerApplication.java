/*
 * Copyright 2012 Rentolution.com
 */

package com.medselect.server;

import com.medselect.application.ApplicationKeyResource;
import com.medselect.application.ApplicationServerResource;
import com.medselect.appointment.AppointmentCleanupServerResource;
import com.medselect.appointment.AppointmentServerResource;
import com.medselect.appointment.AppointmentUtilServerResource;
import com.medselect.audit.AuditServerResource;
import com.medselect.audit.AuditLogServerResource;
import com.medselect.config.ConfigServerResource;
import com.medselect.drawing.DrawingServerResource;
import com.medselect.user.UserAuthServerResource;
import com.medselect.user.UserModServerResource;
import com.medselect.user.UserServerResource;
import com.medselect.staticlist.StaticlistServerResource;
import com.medselect.billing.BillingGroupServerResource;
import com.medselect.billing.BillingOfficeServerResource;
import com.medselect.doctor.DoctorServerResource;
import com.medselect.billing.BillingPlanServerResource;


import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 *
 * @author Mike Gordon
 */
public class MedSelectServerApplication extends Application {
  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());
    router.attach("/config", ConfigServerResource.class);
    router.attach("/config/{key}", ConfigServerResource.class);
    router.attach("/drawing", DrawingServerResource.class);
    router.attach("/user", UserServerResource.class);
    router.attach("/user/{userEmail}", UserModServerResource.class);
    router.attach("/userauth", UserAuthServerResource.class);
    router.attach("/app", ApplicationServerResource.class);
    router.attach("/app/{key}", ApplicationServerResource.class);
    router.attach("/appkey/{key}", ApplicationKeyResource.class);
    router.attach("/user/{key}", UserServerResource.class);
    router.attach("/appointment", AppointmentServerResource.class);
    router.attach("/appointment/{key}", AppointmentServerResource.class);
    router.attach("/appointmentutil", AppointmentUtilServerResource.class);
    router.attach("/appointmentcleanup", AppointmentCleanupServerResource.class);
    router.attach("/audit", AuditServerResource.class);
    router.attach("/audit/{auditKey}", AuditServerResource.class);
    router.attach("/auditlog", AuditLogServerResource.class);
    router.attach("/auditlog/{auditlogKey}", AuditLogServerResource.class);
    router.attach("/staticlist", StaticlistServerResource.class);
    router.attach("/staticlist/{key}", StaticlistServerResource.class);
    router.attach("/billinggroup", BillingGroupServerResource.class);
    router.attach("/billinggroup/{key}", BillingGroupServerResource.class);
    router.attach("/billingoffice", BillingOfficeServerResource.class);
    router.attach("/billingoffice/{key}", BillingOfficeServerResource.class);
    router.attach("/doctor", DoctorServerResource.class);
    router.attach("/doctor/{key}", DoctorServerResource.class);
    router.attach("/billingplan", BillingPlanServerResource.class);
    router.attach("/billingplan/{key}", BillingPlanServerResource.class);

    return router;
  }
}
