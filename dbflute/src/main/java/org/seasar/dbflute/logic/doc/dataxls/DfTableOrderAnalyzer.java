package org.seasar.dbflute.logic.doc.dataxls;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.ForeignKey;
import org.apache.torque.engine.database.model.Table;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/23 Saturday)
 */
public class DfTableOrderAnalyzer {

    public List<List<Table>> analyzeOrder(Database database) {
        final Set<String> alreadyRegisteredSet = new HashSet<String>();
        final List<List<Table>> outputOrderedList = new ArrayList<List<Table>>();

        List<Table> tableList = database.getTableList();
        while (true) {
            final int beforeSize = tableList.size();
            tableList = doAnalyzeOrder(tableList, alreadyRegisteredSet, outputOrderedList);
            if (tableList.isEmpty()) {
                break;
            }
            final int afterSize = tableList.size();
            if (beforeSize == afterSize) { // means it cannot analyze more
                outputOrderedList.add(tableList);
                break;
            }
        }
        return outputOrderedList;
    }

    /**
     * @param tableList The list of table, which may be registered. (NotNull)
     * @param alreadyRegisteredSet The name set of already registered table. (NotNull)
     * @param outputOrderedList The ordered list of table for output. (NotNull)
     * @return The list of unregistered table. (NotNull)
     */
    protected List<Table> doAnalyzeOrder(List<Table> tableList, Set<String> alreadyRegisteredSet,
            List<List<Table>> outputOrderedList) {
        final List<Table> unregisteredTableList = new ArrayList<Table>();
        final List<Table> elementList = new ArrayList<Table>();
        for (Table table : tableList) {
            if (table.isTypeView() || table.isAdditionalSchema()) {
                // view object - view is not an object which has own data
                // additional schema - tables on main schema only are target
                continue;
            }
            final List<ForeignKey> foreignKeyList = table.getForeignKeyList();
            boolean independent = true;
            for (ForeignKey fk : foreignKeyList) {
                final String foreignTableName = fk.getForeignTableName();
                if (!fk.isSelfReference() && !alreadyRegisteredSet.contains(foreignTableName)) {
                    // found parent non-registered
                    independent = false;
                    break;
                }
            }
            if (independent) {
                elementList.add(table);
                alreadyRegisteredSet.add(table.getName());
            } else {
                unregisteredTableList.add(table);
            }
        }
        if (!elementList.isEmpty()) {
            outputOrderedList.add(elementList);
        }
        return unregisteredTableList;
    }
}
