package hudson.plugins.emma;

/**
 * @author Manuel Carrasco
 */
public class CoverageObjectTest extends AbstractEmmaTestBase {
	
    public void testPrintRatioTable() throws Exception {

    	Ratio r = null;
    	StringBuilder b = new StringBuilder();
        boolean no_tests_required = false;

    	r = new Ratio(0,100);
    	b = new StringBuilder();
    	CoverageObject.printRatioTable(r, b, no_tests_required);
    	assertEquals("<table class='percentgraph' cellpadding='0px' cellspacing='0px'><tr class='percentgraph'><td width='64px' class='data'>0.0%</td><td class='percentgraph'><div class='percentgraph'><div class='greenbar' style='width: 0.0px;'><span class='text'>0/100</span></div></div></td></tr></table>", b.toString());
//[KB-]    	assertEquals("<table class='percentgraph' cellpadding='0px' cellspacing='0px'><tr class='percentgraph'><td width='64px' class='data'>0,0%</td><td class='percentgraph'><div class='percentgraph'><div class='greenbar' style='width: 0.0px;'><span class='text'>0/100</span></div></div></td></tr></table>", b.toString());

    	r = new Ratio(51,200);
    	b = new StringBuilder();
    	CoverageObject.printRatioTable(r, b, no_tests_required);
    	assertEquals("<table class='percentgraph' cellpadding='0px' cellspacing='0px'><tr class='percentgraph'><td width='64px' class='data'>25.5%</td><td class='percentgraph'><div class='percentgraph'><div class='greenbar' style='width: 25.5px;'><span class='text'>51/200</span></div></div></td></tr></table>", b.toString());
//[KB-]    	assertEquals("<table class='percentgraph' cellpadding='0px' cellspacing='0px'><tr class='percentgraph'><td width='64px' class='data'>25,5%</td><td class='percentgraph'><div class='percentgraph'><div class='greenbar' style='width: 25.5px;'><span class='text'>51/200</span></div></div></td></tr></table>", b.toString());

    }


    public void testPrintColumnt() throws Exception {

    	Ratio r = null;
    	StringBuilder b = new StringBuilder();
        boolean no_tests_required = false;

    	CoverageObject.printRatioCell(true, null, b, no_tests_required);
    	assertEquals("", b.toString());

    	r = new Ratio(0,100);
    	b = new StringBuilder();
    	CoverageObject.printRatioCell(true, r, b, no_tests_required);
    	assertTrue(b.toString().contains("'nowrap red'"));

    	r = new Ratio(0,100);
    	b = new StringBuilder();
    	CoverageObject.printRatioCell(false, r, b, no_tests_required);
    	assertTrue(b.toString().contains("'nowrap'"));

    	r = new Ratio(51,200);
    	b = new StringBuilder();
    	CoverageObject.printRatioCell(false, r, b, no_tests_required);
    	assertEquals("<td class='nowrap' data='025.50'>\n" +
    			"<table class='percentgraph' cellpadding='0px' cellspacing='0px'><tr class='percentgraph'><td width='64px' class='data'>25.5%</td><td class='percentgraph'><div class='percentgraph'><div class='greenbar' style='width: 25.5px;'><span class='text'>51/200</span></div></div></td></tr></table></td>\n", b.toString());
//[KB-]    	assertEquals("<td class='nowrap' data='025,50'>\n" +
//[KB-]    			"<table class='percentgraph' cellpadding='0px' cellspacing='0px'><tr class='percentgraph'><td width='64px' class='data'>25,5%</td><td class='percentgraph'><div class='percentgraph'><div class='greenbar' style='width: 25.5px;'><span class='text'>51/200</span></div></div></td></tr></table></td>\n", b.toString());


    	r = new Ratio(0,0);
    	b = new StringBuilder();
    	CoverageObject.printRatioCell(true, r, b, no_tests_required);
    	assertTrue(b.toString().contains("'nowrap red'"));

    	r = new Ratio(0,0);
    	b = new StringBuilder();
    	CoverageObject.printRatioCell(false, r, b, no_tests_required);
    	assertTrue(b.toString().contains("'nowrap'"));
    }
 
}
