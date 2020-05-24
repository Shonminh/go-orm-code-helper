package org.shonminh.helper.sql;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Header {
    private List<String> dependencyPackageList;
    private Set<String> existDependencySet;

    public Header() {
        this.dependencyPackageList = new ArrayList<>();
        this.existDependencySet = new HashSet<>();
    }

    public void appendDependencyPackage(String dependencyPackageName) {
        if (this.existDependencySet.contains(dependencyPackageName)) {
            return;
        }
        this.existDependencySet.add(dependencyPackageName);

        List<String> arrayList = this.getDependencyPackageList();
        arrayList.add(dependencyPackageName);
        arrayList.sort(String::compareTo);
        this.setDependencyPackageList(arrayList);

    }

    public void appendDependencyPackageList(List<String> dependencyPackageList) {
        List<String> arrayList = new ArrayList<>(this.getDependencyPackageList());
        for (String packageName : dependencyPackageList) {
            if (this.existDependencySet.contains(packageName)) {
                continue;
            }
            arrayList.add(packageName);
        }
        arrayList.sort(String::compareTo);
        this.setDependencyPackageList(arrayList);
    }


    public List<String> getDependencyPackageList() {
        return dependencyPackageList;
    }

    public void setDependencyPackageList(List<String> dependencyPackageList) {
        this.dependencyPackageList = dependencyPackageList;
    }
}
