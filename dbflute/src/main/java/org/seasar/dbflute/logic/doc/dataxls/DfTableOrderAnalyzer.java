package org.seasar.dbflute.logic.doc.dataxls;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.ForeignKey;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/23 Saturday)
 */
public class DfTableOrderAnalyzer {

    public List<List<Table>> analyzeOrder(Database database) {
        final Set<String> alreadyRegisteredSet = new HashSet<String>();
        final List<List<Table>> outputOrderedList = new ArrayList<List<Table>>();

        List<Table> workTableList;
        {
            final List<Table> allTableList = database.getTableList();
            final TreeSet<Table> allTableSet = new TreeSet<Table>(new Comparator<Table>() {
                public int compare(Table o1, Table o2) {
                    // e.g. order, order_detail, order_detail_more, ...
                    return o1.getName().compareTo(o2.getName());
                }
            });
            allTableSet.addAll(allTableList);
            workTableList = new ArrayList<Table>(allTableSet);
        }
        int level = 1;
        while (true) {
            final int beforeSize = workTableList.size();
            workTableList = doAnalyzeOrder(workTableList, alreadyRegisteredSet, outputOrderedList, level);
            if (workTableList.isEmpty()) {
                break;
            }
            final int afterSize = workTableList.size();
            if (beforeSize == afterSize) { // means it cannot analyze more
                if (level == 1) {
                    ++level; // next: ignores additional foreign key
                } else {
                    outputOrderedList.add(workTableList);
                    break;
                }
            }
        }
        return groupingLargeSection(outputOrderedList);
    }

    protected List<List<Table>> groupingLargeSection(List<List<Table>> outputOrderedList) {
        final List<List<Table>> groupedList = new ArrayList<List<Table>>();
        for (List<Table> tableList : outputOrderedList) {
            if (tableList.size() < 10) {
                groupedList.add(tableList);
                continue;
            }
            List<Table> workTableList = new ArrayList<Table>(); // as initial instance
            String currentPrefix = null;
            for (Table table : tableList) {
                final String name = table.getName();
                if (currentPrefix != null) {
                    if (name.startsWith(currentPrefix)) { // grouped
                        workTableList.add(table);
                    } else {
                        if (workTableList.size() > 1) { // switched
                            groupedList.add(workTableList);
                            workTableList = new ArrayList<Table>();
                        }
                        currentPrefix = null;
                    }
                }
                if (currentPrefix == null) {
                    currentPrefix = Srl.substringFirstFront(name, "_");
                    workTableList.add(table);
                }
            }
            if (!workTableList.isEmpty()) {
                groupedList.add(workTableList);
            }
        }

        // assert here
        int resourceCount = 0;
        for (List<Table> tableList : outputOrderedList) {
            resourceCount = resourceCount + tableList.size();
        }
        int groupedCount = 0;
        for (List<Table> tableList : groupedList) {
            groupedCount = groupedCount + tableList.size();
        }
        if (resourceCount != groupedCount) {
            String msg = "The grouping process had the loss:";
            msg = msg + " resourceCount=" + resourceCount + " groupedCount=" + groupedCount;
            throw new IllegalStateException(msg);
        }

        return groupedList;
    }

    /**
     * @param tableList The list of table, which may be registered. (NotNull)
     * @param alreadyRegisteredSet The name set of already registered table. (NotNull)
     * @param outputOrderedList The ordered list of table for output. (NotNull)
     * @return The list of unregistered table. (NotNull)
     */
    protected List<Table> doAnalyzeOrder(List<Table> tableList, Set<String> alreadyRegisteredSet,
            List<List<Table>> outputOrderedList, final int level) {
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
                if (level == 1 && fk.hasFixedCondition()) {
                    continue;
                }
                if (level == 2 && fk.isAdditionalForeignKey()) {
                    continue;
                }
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
