package com.project.testrail.enums;

/**
 * You can add here the id and name of TestRail projects
 */

public enum TestRailProjects {

	//Example of TestRail projects
	WEB(1, "Project - Web app 1"),
	WEB2(21, "Project - Web app 2")
	;

    private int id;
    private String name;

    private TestRailProjects(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public static TestRailProjects getById(int id) {
    	for (TestRailProjects project: TestRailProjects.values()) {
    		if (project.getId() == id) {
    			return project;
    		}
    	}
    	return null;
    }
}
