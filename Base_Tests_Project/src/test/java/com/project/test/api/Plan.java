package com.project.test.api;

public enum Plan {
//TODO If needed add your enums for license plans, here are some examples
	
	PROJECTPLATFORM_ENTERPRICE("PROJECT_PLATFORM_ENTERPRISE"),
    PROJECT_COLLABORATION_PLATFORM_BASE("COLLABORATION_PLATFORM_BASE"),
    PROJECT_COLLABORATION_PLATFORM_STANDARD("COLLABORATION_PLATFORM_STANDARD"),
    PROJECT_COLLABORATION_PLATFORM_CORPORATE("COLLABORATION_PLATFORM_CORPORATE"),
    PROJECT_COLLABORATION_PLATFORM_ENTERPRICE("COLLABORATION_PLATFORM_ENTERPRISE"),
    ;
    
    String name;

    private Plan(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
}