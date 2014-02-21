
package org.bml.util.rt.telemetry;

import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Brian M. Lima
 */
public class RTTelemetryConfig extends Properties {
  /** standard commons Logging */
  private static final Log LOG = LogFactory.getLog(RTTelemetryConfig.class);

  static enum KEYS {
    SYNC_RATE,
  }
  
}
