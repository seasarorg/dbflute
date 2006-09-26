package org.apache.torque.task;

import org.apache.tools.ant.Task;
import org.apache.velocity.util.StringUtils;

public class PackageAsPathTask extends Task {

    protected String pckg;

    protected String name;

    public PackageAsPathTask() {
    }

    public void execute() {
        super.getProject().setUserProperty(name, StringUtils.getPackageAsPath(pckg));
    }

    public void setPackage(String pckg) {
        this.pckg = pckg;
    }

    public void setName(String name) {
        this.name = name;
    }

}