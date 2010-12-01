package org.seasar.dbflute.friends.velocity;

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.engine.database.model.AppData;
import org.apache.velocity.VelocityContext;

/**
 * @author jflute
 * @since 0.9.5.1 (2009/06/26 Friday)
 */
public class DfVelocityContextFactory {

    public VelocityContext create(AppData appData) {
        final VelocityContext context = new VelocityContext();
        final List<AppData> dataModels = new ArrayList<AppData>();
        dataModels.add(appData);
        context.put("dataModels", dataModels); // for compatibility
        context.put("schemaData", appData);
        return context;
    }
}
