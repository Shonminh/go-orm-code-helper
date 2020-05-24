package org.shonminh.helper.sql;

import org.junit.Test;
import org.shonminh.helper.util.HeaderUtil;

import java.util.List;

import static org.junit.Assert.*;

public class HeaderTest {

    @Test
    public void appendDependencyPackage() {
        Header header = new Header();
        header.appendDependencyPackage("demo.b");
        header.appendDependencyPackage("demo.a");
        List<String> dependencyPackageList = header.getDependencyPackageList();
        assertEquals(2, dependencyPackageList.size());
        assertEquals("demo.a", dependencyPackageList.get(0));
        assertEquals("demo.b", dependencyPackageList.get(1));
    }

    @Test
    public void getHeaderCodes() {
        Header header = new Header();
        header.appendDependencyPackage("demo.a");
        header.appendDependencyPackage("demo.b");
        String headerCodes = HeaderUtil.getHeaderCodes(header.getDependencyPackageList());
        assertEquals("package model\n" +
                "\n" +
                "import (\n" +
                "\t\"demo.a\"\n" +
                "\t\"demo.b\"\n" +
                ")\n" +
                "\n", headerCodes);
    }
}