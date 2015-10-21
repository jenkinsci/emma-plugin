
package hudson.plugins.emma;

import java.io.Serializable;

////////////////////////////////////////////////////////////////////////////////
//[KB]  implementation for the advanced setup support
public class AdvancedSettings implements Serializable {
    
    private boolean testNotMandatory = false;

    private String firstDataColumnDescriptor = "";
    private String secondDataColumnDescriptor = "";
    private String thirdDataColumnDescriptor = "";
    private String fourthDataColumnDescriptor = "";
    private String fifthDataColumnDescriptor = "";


    private String getValue(String value, String defaultValue){   
        
        if ((value == null)||("".equals(value)))
        {
            value = defaultValue;
        }
        return value;
    }

    
    public void setTestNotMandatory(boolean state){
        
        testNotMandatory = state;
    }

    public boolean getTestNotMandatory(){
        
        return testNotMandatory;
    }
    
    public String getFirstDataColumnDescriptor() {   
        
        return getValue(firstDataColumnDescriptor, Messages.CoverageObject_Legend_Class());
    }
    
    public void setFirstDataColumnDescriptor(String name) {
        
        firstDataColumnDescriptor = name;
    }

    public String getSecondDataColumnDescriptor() {
        
        return getValue(secondDataColumnDescriptor, Messages.CoverageObject_Legend_Block());
    }
    
    public void setSecondDataColumnDescriptor(String name) {
        
        secondDataColumnDescriptor = name;
    }

    public String getThirdDataColumnDescriptor() {
        
        return getValue(thirdDataColumnDescriptor, Messages.CoverageObject_Legend_Method());
    }
    
    public void setThirdDataColumnDescriptor(String name) {
        
        thirdDataColumnDescriptor = name;
    }

    public String getFourthDataColumnDescriptor() {
        
        return getValue(fourthDataColumnDescriptor, Messages.CoverageObject_Legend_Line());
    }
    
    public void setFourthDataColumnDescriptor(String name) {
        
        fourthDataColumnDescriptor = name;
    }

    public String getFifthDataColumnDescriptor() {
        
        return getValue(fifthDataColumnDescriptor, Messages.CoverageObject_Legend_Condition());
    }
    
    public void setFifthDataColumnDescriptor(String name) {
        
        fifthDataColumnDescriptor = name;
    }
    
    public void applySettings(AdvancedSettings settings){
        
        if(settings != null){
            setTestNotMandatory(settings.getTestNotMandatory());
            setFirstDataColumnDescriptor(settings.getFirstDataColumnDescriptor());
            setSecondDataColumnDescriptor(settings.getSecondDataColumnDescriptor());
            setThirdDataColumnDescriptor(settings.getThirdDataColumnDescriptor());
            setFourthDataColumnDescriptor(settings.getFourthDataColumnDescriptor());
            setFifthDataColumnDescriptor(settings.getFifthDataColumnDescriptor());
        }
    }    
}
//
////////////////////////////////////////////////////////////////////////////////
