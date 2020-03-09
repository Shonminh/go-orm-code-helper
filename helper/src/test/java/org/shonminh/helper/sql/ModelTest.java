package org.shonminh.helper.sql;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ModelTest {

    private Model model = null;
    @Before
    public void setUp() throws Exception {
        Model model = new Model("test_tab");
        model.setPrimaryKey("id");
        model.setStructModelName("TestTab");
        model.setColumns(new ArrayList<>());

        List<Column> columnList = new ArrayList<>();
        Column column = new Column();
        column.setStructName("Id");
        column.setStructType("uint64");
        columnList.add(column);

        Column column1 = new Column();
        column1.setStructName("UserId");
        column1.setStructType("uint32");
        columnList.add(column1);

        Column column2 = new Column();
        column2.setStructName("Name");
        column2.setStructType("string");
        columnList.add(column2);

        Column column3 = new Column();
        column3.setStructName("CreateTime");
        column3.setStructType("uint32");
        columnList.add(column3);

        Column column4 = new Column();
        column4.setStructName("UpdateTime");
        column4.setStructType("uint32");
        columnList.add(column4);

        model.setColumns(columnList);

        this.setModel(model);
    }

    @Test
    public void TestGenerateGoCreateFunction() {
        System.out.println(this.getModel());
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }
}