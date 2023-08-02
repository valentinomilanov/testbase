package com.project.test.api;

public enum Modules {
//TODO add your own modules, here are a couple exaples from some other projects
	REVIEW_FORM_AND_SIGN("REVIEW_FORM_AND_SIGN"),
    SIGN_DOCUMENT("SIGN_DOC"),
    SIGN_FORM("SIGN_FORM"),
    UPLOAD_DOCUMENT("UPLOAD_DOC"),
    UPLOAD_DOCUMENT_AND_SIGN("UPLOAD_DOC_AND_SIGN"),
	SUREWORKFLOW("project.workflow"),
	GOOGLE_G_SUITE("project.pro-interface.google"),
    ONLINE_HELP("project.help"),
    LABORATORY_FEATURES("project.laboratory"),
    ;
	
	 private final String moduleId;

	    private Modules(String moduleId) {
	        this.moduleId = moduleId;
	    }

	    public String getModuleId() {
	        return moduleId;
	    }
}
