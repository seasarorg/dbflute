package org.seasar.dbflute.friends.velocity;

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.engine.database.model.AppData;
import org.apache.velocity.VelocityContext;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenManager;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenRequest;
import org.seasar.dbflute.task.bs.assistant.DfDocumentSelector;

/**
 * @author jflute
 * @since 0.9.5.1 (2009/06/26 Friday)
 */
public class DfVelocityContextFactory {

    public VelocityContext createAsCore(AppData appData, DfDocumentSelector selector) {
        final VelocityContext context = new VelocityContext();
        final List<AppData> dataModels = new ArrayList<AppData>();
        dataModels.add(appData);
        context.put("dataModels", dataModels); // for compatibility
        context.put("schemaData", appData);
        context.put("selector", selector); // basically for Doc task
        return context;
    }

    public VelocityContext createAsFreeGen(DfFreeGenManager freeGenManager, List<DfFreeGenRequest> freeGenRequestList) {
        final VelocityContext context = new VelocityContext();
        context.put("manager", freeGenManager);
        context.put("requestList", freeGenRequestList);
        return context;
    }
}
