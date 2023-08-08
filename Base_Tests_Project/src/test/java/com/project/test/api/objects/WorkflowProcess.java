package com.project.test.api.objects;

import java.util.List;

import org.joda.time.LocalDateTime;

/**
 * 
 * @author Valentino Milanov
 *
 * Example of an object
 */
public class WorkflowProcess {

	private String id;
    private String name;
    private String processType;
    private String executionType;
    private String definitionId;
    private boolean isTemplate;
    private String formKey;
    private String status;
    private String initiator;
    private String executor;
    private List<String> processComments;
    private String model;
    private String initialModel;
    private List<String> userTasks;
    private List<String> pendingUserTasks;
    private List<String> activities;
    private LocalDateTime createTime;
    private LocalDateTime startTime;
    private LocalDateTime lastModifiedTime;
    private LocalDateTime endTime;
    private long duration;
    
    public WorkflowProcess() {
        super();
    }
    
    public WorkflowProcess(String id, String name, String processType, String executionType, String definitionId,
            boolean isTemplate, String formKey, String status, String initiator, String executor,
            List<String> processComments, String model, String initialModel, List<String> userTasks,
            List<String> pendingUserTasks, List<String> activities, LocalDateTime createTime,
            LocalDateTime startTime, LocalDateTime lastModifiedTime, LocalDateTime endTime, long duration) {
        super();
        this.id = id;
        this.name = name;
        this.processType = processType;
        this.executionType = executionType;
        this.definitionId = definitionId;
        this.isTemplate = isTemplate;
        this.formKey = formKey;
        this.status = status;
        this.initiator = initiator;
        this.executor = executor;
        this.processComments = processComments;
        this.model = model;
        this.initialModel = initialModel;
        this.userTasks = userTasks;
        this.pendingUserTasks = pendingUserTasks;
        this.activities = activities;
        this.createTime = createTime;
        this.startTime = startTime;
        this.lastModifiedTime = lastModifiedTime;
        this.endTime = endTime;
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
    
    public String getProcessType() {
        return processType;
    }
    
    public void setProcessType(String processType) {
        this.processType = processType;
    }
    
    public String getExecutionType() {
        return executionType;
    }
    
    public void setExecutionType(String executionType) {
        this.executionType = executionType;
    }
    
    public String getDefinitionId() {
        return definitionId;
    }
    
    public void setDefinitionId(String definitionId) {
        this.definitionId = definitionId;
    }
    
    public boolean isTemplate() {
        return isTemplate;
    }
    
    public void setTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
    }
    
    public String getFormKey() {
        return formKey;
    }
    
    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getInitiator() {
        return initiator;
    }
    
    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }
    
    public String getExecutor() {
        return executor;
    }
    
    public void setExecutor(String executor) {
        this.executor = executor;
    }
    
    public List<String> getProcessComments() {
        return processComments;
    }
    
    public void setProcessComments(List<String> processComments) {
        this.processComments = processComments;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getInitialModel() {
        return initialModel;
    }

    public void setInitialModel(String initialModel) {
        this.initialModel = initialModel;
    }

    public List<String> getUserTasks() {
        return userTasks;
    }
    
    public void setUserTasks(List<String> userTasks) {
        this.userTasks = userTasks;
    }
    
    public List<String> getPendingUserTasks() {
        return pendingUserTasks;
    }
    
    public void setPendingUserTasks(List<String> pendingUserTasks) {
        this.pendingUserTasks = pendingUserTasks;
    }
    
    public List<String> getActivities() {
        return activities;
    }
    
    public void setActivities(List<String> activities) {
        this.activities = activities;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getLastModifiedTime() {
        return lastModifiedTime;
    }
    
    public void setLastModifiedTime(LocalDateTime lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((activities == null) ? 0 : activities.hashCode());
        result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
        result = prime * result + ((definitionId == null) ? 0 : definitionId.hashCode());
        result = prime * result + (int) (duration ^ (duration >>> 32));
        result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
        result = prime * result + ((executionType == null) ? 0 : executionType.hashCode());
        result = prime * result + ((executor == null) ? 0 : executor.hashCode());
        result = prime * result + ((formKey == null) ? 0 : formKey.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((initialModel == null) ? 0 : initialModel.hashCode());
        result = prime * result + ((initiator == null) ? 0 : initiator.hashCode());
        result = prime * result + (isTemplate ? 1231 : 1237);
        result = prime * result + ((lastModifiedTime == null) ? 0 : lastModifiedTime.hashCode());
        result = prime * result + ((model == null) ? 0 : model.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((pendingUserTasks == null) ? 0 : pendingUserTasks.hashCode());
        result = prime * result + ((processComments == null) ? 0 : processComments.hashCode());
        result = prime * result + ((processType == null) ? 0 : processType.hashCode());
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((userTasks == null) ? 0 : userTasks.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkflowProcess other = (WorkflowProcess) obj;
        if (activities == null) {
            if (other.activities != null)
                return false;
        } else if (!activities.equals(other.activities))
            return false;
        if (createTime == null) {
            if (other.createTime != null)
                return false;
        } else if (!createTime.equals(other.createTime))
            return false;
        if (definitionId == null) {
            if (other.definitionId != null)
                return false;
        } else if (!definitionId.equals(other.definitionId))
            return false;
        if (duration != other.duration)
            return false;
        if (endTime == null) {
            if (other.endTime != null)
                return false;
        } else if (!endTime.equals(other.endTime))
            return false;
        if (executionType == null) {
            if (other.executionType != null)
                return false;
        } else if (!executionType.equals(other.executionType))
            return false;
        if (executor == null) {
            if (other.executor != null)
                return false;
        } else if (!executor.equals(other.executor))
            return false;
        if (formKey == null) {
            if (other.formKey != null)
                return false;
        } else if (!formKey.equals(other.formKey))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (initialModel == null) {
            if (other.initialModel != null)
                return false;
        } else if (!initialModel.equals(other.initialModel))
            return false;
        if (initiator == null) {
            if (other.initiator != null)
                return false;
        } else if (!initiator.equals(other.initiator))
            return false;
        if (isTemplate != other.isTemplate)
            return false;
        if (lastModifiedTime == null) {
            if (other.lastModifiedTime != null)
                return false;
        } else if (!lastModifiedTime.equals(other.lastModifiedTime))
            return false;
        if (model == null) {
            if (other.model != null)
                return false;
        } else if (!model.equals(other.model))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (pendingUserTasks == null) {
            if (other.pendingUserTasks != null)
                return false;
        } else if (!pendingUserTasks.equals(other.pendingUserTasks))
            return false;
        if (processComments == null) {
            if (other.processComments != null)
                return false;
        } else if (!processComments.equals(other.processComments))
            return false;
        if (processType != other.processType)
            return false;
        if (startTime == null) {
            if (other.startTime != null)
                return false;
        } else if (!startTime.equals(other.startTime))
            return false;
        if (status != other.status)
            return false;
        if (userTasks == null) {
            if (other.userTasks != null)
                return false;
        } else if (!userTasks.equals(other.userTasks))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WorkflowProcess [id=" + id + ", name=" + name + ", processType=" + processType + ", executionType="
                + executionType + ", definitionId=" + definitionId + ", isTemplate=" + isTemplate + ", formKey="
                + formKey + ", status=" + status + ", initiator=" + initiator + ", executor=" + executor
                + ", processComments=" + processComments + ", model=" + model + ", initialModel=" + initialModel
                + ", userTasks=" + userTasks + ", pendingUserTasks=" + pendingUserTasks + ", activities=" + activities
                + ", createTime=" + createTime + ", startTime=" + startTime + ", lastModifiedTime=" + lastModifiedTime
                + ", endTime=" + endTime + ", duration=" + duration + "]";
    }
    
}
