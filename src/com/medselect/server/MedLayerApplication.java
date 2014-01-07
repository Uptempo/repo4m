/*
 * Copyright 2012 Rentolution.com
 */

package com.medselect.server;

import com.medselect.medlayer.MedLayerAppServerResource;
import com.medselect.medlayer.MedLayerDatasetServerResource;
import com.medselect.medlayer.MedLayerPOIServerResource;
import com.medselect.medlayer.MedLayerServerResource;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 *
 * @author Antonio Sala
 */
public class MedLayerApplication extends Application {
  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());
    router.attach("/app", MedLayerAppServerResource.class);
    router.attach("/app/{key}", MedLayerAppServerResource.class);
    router.attach("/app/detailed/{key}", MedLayerServerResource.class);
    router.attach("/dataset", MedLayerDatasetServerResource.class);
    router.attach("/dataset/{key}", MedLayerDatasetServerResource.class);
    router.attach("/poi", MedLayerPOIServerResource.class);
    router.attach("/poi/{key}", MedLayerPOIServerResource.class);
    

    return router;
  }
}
