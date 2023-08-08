package com.project.test.api.pojo;

import java.util.List;

/**
 * 
 * @author Valentino Milanov
 *
 * An example for a POJO(Plain Old Java Object)
 */
public class ProjectsStudyPojo {
	
	private List<String> archiveCreatedMessage = null;
    private List<String> model = null;
    private List<String> successHandler = null;
    private List<String> failureHandler = null;
    private String archiveCreatedHeader;
    private String archiveListLink;
    private String archiveType;
    private Boolean buildInChain;
    private Boolean cloneDocumentMilestones;
    private Boolean cloneDocumentNaming;
    private Boolean cloneMetrics;
    private String contentModelDate;
    private String contentModelName;
    private String contentModelVersion;
    private String description;
    private String duplicationPolicy;
    private Integer duration;
    private String id;
    private String name;
    private String sourceStudyId;
    private String startDate;
    private Boolean studyBased;
    private Integer targetSites;
    
    public ProjectsStudyPojo(String name, String id, String description, String startDate, Integer duration, Integer targetSites, String duplicationPolicy,String contentModelName, String contentModelDate, String contentModelVersion, List<String> model, Boolean buildInChain, Boolean studyBased, String sourceStudyId, Boolean cloneDocumentNaming, Boolean cloneMetrics, Boolean cloneDocumentMilestones, List<String> successHandler, List<String> failureHandler, String archiveType, String archiveCreatedHeader, List<String> archiveCreatedMessage, String archiveListLink) {

        this.name = name;
        this.id = id;
        this.description = description;
        this.startDate = startDate;
        this.duration = duration;
        this.targetSites = targetSites;
        this.duplicationPolicy = duplicationPolicy;
        this.contentModelName = contentModelName;
        this.contentModelDate = contentModelDate;
        this.contentModelVersion = contentModelVersion;
        this.model = model;
        this.buildInChain = buildInChain;
        this.studyBased = studyBased;
        this.sourceStudyId = sourceStudyId;
        this.cloneDocumentNaming = cloneDocumentNaming;
        this.cloneMetrics = cloneMetrics;
        this.cloneDocumentMilestones = cloneDocumentMilestones;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.archiveType = archiveType;
        this.archiveCreatedHeader = archiveCreatedHeader;
        this.archiveCreatedMessage = archiveCreatedMessage;
        this.archiveListLink = archiveListLink;


    }
    public List<String> getFailureHandler() {
        return failureHandler;
    }

    public void setFailureHandler(List<String> failureHandler) {
        this.failureHandler = failureHandler;
    }
    public List<String> getArchiveCreatedMessage() {
        return archiveCreatedMessage;
    }

    public void setArchiveCreatedMessage(List<String> archiveCreatedMessage) {
        this.archiveCreatedMessage = archiveCreatedMessage;
    }

    public List<String> getModel() {
        return model;
    }

    public void setModel(List<String> model) {
        this.model = model;
    }

    public List<String> getSuccessHandler() {
        return successHandler;
    }

    public void setSuccessHandler(List<String> successHandler) {
        this.successHandler = successHandler;
    }
    public String getArchiveCreatedHeader() {
        return archiveCreatedHeader;
    }

    public void setArchiveCreatedHeader(String archiveCreatedHeader) {
        this.archiveCreatedHeader = archiveCreatedHeader;
    }

    public String getArchiveListLink() {
        return archiveListLink;
    }

    public void setArchiveListLink(String archiveListLink) {
        this.archiveListLink = archiveListLink;
    }

    public String getArchiveType() {
        return archiveType;
    }

    public void setArchiveType(String archiveType) {
        this.archiveType = archiveType;
    }

    public Boolean getBuildInChain() {
        return buildInChain;
    }

    public void setBuildInChain(Boolean buildInChain) {
        this.buildInChain = buildInChain;
    }

    public Boolean getCloneDocumentMilestones() {
        return cloneDocumentMilestones;
    }

    public void setCloneDocumentMilestones(Boolean cloneDocumentMilestones) {
        this.cloneDocumentMilestones = cloneDocumentMilestones;
    }

    public Boolean getCloneDocumentNaming() {
        return cloneDocumentNaming;
    }

    public void setCloneDocumentNaming(Boolean cloneDocumentNaming) {
        this.cloneDocumentNaming = cloneDocumentNaming;
    }

    public Boolean getCloneMetrics() {
        return cloneMetrics;
    }

    public void setCloneMetrics(Boolean cloneMetrics) {
        this.cloneMetrics = cloneMetrics;
    }

    public String getContentModelDate() {
        return contentModelDate;
    }

    public void setContentModelDate(String contentModelDate) {
        this.contentModelDate = contentModelDate;
    }

    public String getContentModelName() {
        return contentModelName;
    }

    public void setContentModelName(String contentModelName) {
        this.contentModelName = contentModelName;
    }

    public String getContentModelVersion() {
        return contentModelVersion;
    }

    public void setContentModelVersion(String contentModelVersion) {
        this.contentModelVersion = contentModelVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuplicationPolicy() {
        return duplicationPolicy;
    }

    public void setDuplicationPolicy(String duplicationPolicy) {
        this.duplicationPolicy = duplicationPolicy;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceStudyId() {
        return sourceStudyId;
    }

    public void setSourceStudyId(String sourceStudyId) {
        this.sourceStudyId = sourceStudyId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Boolean getStudyBased() {
        return studyBased;
    }

    public void setStudyBased(Boolean studyBased) {
        this.studyBased = studyBased;
    }

    public Integer getTargetSites() {
        return targetSites;
    }

    public void setTargetSites(Integer targetSites) {
        this.targetSites = targetSites;
    }

}
