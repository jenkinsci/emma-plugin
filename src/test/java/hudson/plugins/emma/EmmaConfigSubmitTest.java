package hudson.plugins.emma;

import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import hudson.model.FreeStyleProject;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 * Test for Project configuration.
 * @author Seiji Sogabe
 */
public class EmmaConfigSubmitTest extends HudsonTestCase {

    public void testIncludeIsEmpty() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-emma-EmmaPublisher").setChecked(true);
        // includes is empty
        submit(f);

        EmmaPublisher publisher = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);

        assertEquals("", publisher.includes);
    }
    
    public void testIncludeIsSet() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-emma-EmmaPublisher").setChecked(true);
        f.getInputByName("emma.includes").setValue("**/*");
        submit(f);

        EmmaPublisher publisher = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);

        assertEquals("**/*", publisher.includes);
    }

    public void testHealthReportDefaultMaxValue() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-emma-EmmaPublisher").setChecked(true);
        f.getInputByName("emmaHealthReports.maxClass").setValue("");
        f.getInputByName("emmaHealthReports.maxMethod").setValue("");
        f.getInputByName("emmaHealthReports.maxBlock").setValue("");
        f.getInputByName("emmaHealthReports.maxLine").setValue("");
        f.getInputByName("emmaHealthReports.maxCondition").setValue("");
        submit(f);

        EmmaPublisher publisher = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);
        EmmaHealthReportThresholds thresholds = publisher.healthReports;
        
        assertEquals(100, thresholds.getMaxClass());
        assertEquals(70, thresholds.getMaxMethod());
        assertEquals(80, thresholds.getMaxBlock());
        assertEquals(80, thresholds.getMaxLine());
        assertEquals(80, thresholds.getMaxCondition());
    }    

    public void testHealthReportDefaultMinValue() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-emma-EmmaPublisher").setChecked(true);
        f.getInputByName("emmaHealthReports.minClass").setValue("");
        f.getInputByName("emmaHealthReports.minMethod").setValue("");
        f.getInputByName("emmaHealthReports.minBlock").setValue("");
        f.getInputByName("emmaHealthReports.minLine").setValue("");
        f.getInputByName("emmaHealthReports.minCondition").setValue("");
        submit(f);

        EmmaPublisher publisher = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);
        EmmaHealthReportThresholds thresholds = publisher.healthReports;
        
        assertEquals(0, thresholds.getMinClass());
        assertEquals(0, thresholds.getMinMethod());
        assertEquals(0, thresholds.getMinBlock());
        assertEquals(0, thresholds.getMinLine());
        assertEquals(0, thresholds.getMinCondition());
    }    

    public void testHealthReportMaxValue() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-emma-EmmaPublisher").setChecked(true);
        f.getInputByName("emmaHealthReports.maxClass").setValue("10");
        f.getInputByName("emmaHealthReports.maxMethod").setValue("10");
        f.getInputByName("emmaHealthReports.maxBlock").setValue("10");
        f.getInputByName("emmaHealthReports.maxLine").setValue("10");
        f.getInputByName("emmaHealthReports.maxCondition").setValue("10");
        submit(f);

        EmmaPublisher publisher = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);
        EmmaHealthReportThresholds thresholds = publisher.healthReports;
        
        assertEquals(10, thresholds.getMaxClass());
        assertEquals(10, thresholds.getMaxMethod());
        assertEquals(10, thresholds.getMaxBlock());
        assertEquals(10, thresholds.getMaxLine());
        assertEquals(10, thresholds.getMaxCondition());
    }    

    public void testHealthReportMinValue() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-emma-EmmaPublisher").setChecked(true);
        f.getInputByName("emmaHealthReports.minClass").setValue("10");
        f.getInputByName("emmaHealthReports.minMethod").setValue("10");
        f.getInputByName("emmaHealthReports.minBlock").setValue("10");
        f.getInputByName("emmaHealthReports.minLine").setValue("10");
        f.getInputByName("emmaHealthReports.minCondition").setValue("10");
        submit(f);

        EmmaPublisher publisher = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);
        EmmaHealthReportThresholds thresholds = publisher.healthReports;
        
        assertEquals(10, thresholds.getMinClass());
        assertEquals(10, thresholds.getMinMethod());
        assertEquals(10, thresholds.getMinBlock());
        assertEquals(10, thresholds.getMinLine());
        assertEquals(10, thresholds.getMinCondition());
    }    
    
    public void test_new_no_tests_required_flag() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-emma-EmmaPublisher").setChecked(true);

        f.getInputByName("emmaAdvancedSettings.testNotMandatory").setChecked(true);
        submit(f);
        EmmaPublisher publisher = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);
        assertEquals(true, publisher.advancedSettings.getTestNotMandatory());
        EmmaPublisher publisher2 = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);
        assertEquals(true, publisher2.advancedSettings.getTestNotMandatory());

        f.getInputByName("emmaAdvancedSettings.testNotMandatory").setChecked(false);
        submit(f);
        EmmaPublisher publisher3 = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);
        assertEquals(false, publisher3.advancedSettings.getTestNotMandatory());
        EmmaPublisher publisher4 = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);
        assertEquals(false, publisher4.advancedSettings.getTestNotMandatory());
    }

    public void testDataColumnMapping() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);

        FreeStyleProject fp = createFreeStyleProject();
        HtmlPage p = client.goTo(fp.getUrl() + "/configure");
        HtmlForm f = p.getFormByName("config");

        f.getInputByName("hudson-plugins-emma-EmmaPublisher").setChecked(true);
        f.getInputByName("emmaAdvancedSettings.firstDataColumnDescriptor").setValue("test_class");
        f.getInputByName("emmaAdvancedSettings.secondDataColumnDescriptor").setValue("test_method");
        f.getInputByName("emmaAdvancedSettings.thirdDataColumnDescriptor").setValue("test_block");
        f.getInputByName("emmaAdvancedSettings.fourthDataColumnDescriptor").setValue("test_line");
        f.getInputByName("emmaAdvancedSettings.fifthDataColumnDescriptor").setValue("test_condition");
        submit(f);

        EmmaPublisher publisher = (EmmaPublisher) fp.getPublisher(EmmaPublisher.DESCRIPTOR);
        AdvancedSettings advancedSettings = publisher.advancedSettings;
        
        assertEquals("test_class", advancedSettings.getFirstDataColumnDescriptor());
        assertEquals("test_class", advancedSettings.getFirstDataColumnDescriptor());
        assertEquals("test_method", advancedSettings.getSecondDataColumnDescriptor());
        assertEquals("test_block", advancedSettings.getThirdDataColumnDescriptor());
        assertEquals("test_line", advancedSettings.getFourthDataColumnDescriptor());
        assertEquals("test_condition", advancedSettings.getFifthDataColumnDescriptor());
    }
}
