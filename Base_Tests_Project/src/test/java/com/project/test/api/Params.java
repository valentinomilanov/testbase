package com.project.test.api;

/**
 * 
 * @author Valentino Milanov
 *
 */
public class Params {
	
	//FIXME fix this class to your needs
	 private String archiveId;
	    private String model;
	    
	    public Params() {
	        super();
	    }

	    public String getArchiveId() {
	        return archiveId;
	    }

	    public void setArchiveId(String archiveId) {
	        this.archiveId = archiveId;
	    }

	    public String getModel() {
	        return model;
	    }

	    public void setModel(String model) {
	        this.model = model;
	    }

	    @Override
	    public int hashCode() {
	        final int prime = 31;
	        int result = 1;
	        result = prime * result + ((archiveId == null) ? 0 : archiveId.hashCode());
	        result = prime * result + ((model == null) ? 0 : model.hashCode());
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
	        Params other = (Params) obj;
	        if (archiveId == null) {
	            if (other.archiveId != null)
	                return false;
	        } else if (!archiveId.equals(other.archiveId))
	            return false;
	        if (model == null) {
	            if (other.model != null)
	                return false;
	        } else if (!model.equals(other.model))
	            return false;
	        return true;
	    }

	    @Override
	    public String toString() {
	        return "Params [archiveId=" + archiveId + ", model=" + model + "]";
	    }
	    
}
