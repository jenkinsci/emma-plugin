package hudson.plugins.emma;

import hudson.model.AbstractBuild;
import hudson.model.ModelObject;
import hudson.model.Run;

import java.io.IOException;

/**
 * Base class of the coverage report tree,
 * which maintains the details of the coverage report.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class AbstractReport<
    PARENT extends AggregatedReport<?,PARENT,?>,
    SELF extends CoverageObject<SELF>> extends CoverageObject<SELF> implements ModelObject {

    private String name;

    private PARENT parent;

    public void addCoverage(CoverageElement cv) throws IOException {
        cv.addTo(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return name;
    }

    /**
     * Called at the last stage of the tree construction,
     * to set the back pointer.
     */
    protected void setParent(PARENT p) {
        this.parent = p;
    }

    /**
     * Gets the back pointer to the parent coverage object.
     */
    public PARENT getParent() {
        return parent;
    }

    @Override
    public SELF getPreviousResult() {
        PARENT p = parent;
        while(true) {
            p = p.getPreviousResult();
            if(p==null)
                return null;
            SELF prev = (SELF)p.getChildren().get(name);
            if(prev!=null)
                return prev;
        }
    }

    @Override
    public Run<?,?> getBuild() {
        return parent.getBuild();
    }

////////////////////////////////////////////////////////////////////////////////
// default interface implementation for the advanced setup support
    
//    @Override
    public boolean getTestNotMandatory(){
        boolean ret_val = false;
        PARENT p = getParent();
        
        if(p != null){
            ret_val = p.getTestNotMandatory();
        }
        
        return ret_val;
    }
    
    @Override
    public String getFirstDataColumnDescriptor()
    {
        String ret_val = "";
        PARENT p = getParent();
        
        if(p != null){
            ret_val = p.getFirstDataColumnDescriptor();
        }
        
        return ret_val;
    }
    
    @Override
    public String getSecondDataColumnDescriptor()
    {
        String ret_val = "";
        PARENT p = getParent();
        
        if(p != null){
            ret_val = p.getSecondDataColumnDescriptor();
        }
        
        return ret_val;
    }
    
    @Override
    public String getThirdDataColumnDescriptor()
    {
        String ret_val = "";
        PARENT p = getParent();
        
        if(p != null){
            ret_val = p.getThirdDataColumnDescriptor();
        }
        
        return ret_val;
    }
    
    @Override
    public String getFourthDataColumnDescriptor()
    {
        String ret_val = "";
        PARENT p = getParent();
        
        if(p != null){
            ret_val = p.getFourthDataColumnDescriptor();
        }
        
        return ret_val;
    }
    
    @Override
    public String getFifthDataColumnDescriptor()
    {
        String ret_val = "";
        PARENT p = getParent();
        
        if(p != null){
            ret_val = p.getFifthDataColumnDescriptor();
        }
        
        return ret_val;
    }
//
////////////////////////////////////////////////////////////////////////////////

}
