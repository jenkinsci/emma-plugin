package hudson.plugins.emma;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.maven.ExecutedMojo;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModuleSetBuild;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import jenkins.tasks.SimpleBuildStep;

/**
 * {@link Publisher} that captures Emma coverage reports.
 *
 * @author Kohsuke Kawaguchi
 */
public class EmmaPublisher extends Recorder implements SimpleBuildStep {

    /**
     * Relative path to the Emma XML file inside the workspace.
     */
    public String includes;
    
    public boolean useThreshold;

    /**
     * Rule to be enforced. Can be null.
     *
     * TODO: define a configuration mechanism.
     */
    public Rule rule;

    /**
     * {@link hudson.model.HealthReport} thresholds to apply.
     */
    public EmmaHealthReportThresholds healthReports = new EmmaHealthReportThresholds();

    /**
     * get instance of AdvancedSettings object 
     */
    public AdvancedSettings advancedSettings = new AdvancedSettings();   
    
    /**
     * look for emma reports based in the configured parameter includes. 'includes' is - an Ant-style pattern - a list
     * of files and folders separated by the characters ;:,
     */
    protected static FilePath[] locateCoverageReports(FilePath workspace, String includes) throws IOException, InterruptedException {

        // First use ant-style pattern
        try {
            FilePath[] ret = workspace.list(includes);
            if (ret.length > 0) {
                return ret;
            }
        } catch (Exception e) {
        }

        // If it fails, do a legacy search
        ArrayList<FilePath> files = new ArrayList<FilePath>();
        String parts[] = includes.split("\\s*[;:,]+\\s*");
        for (String path : parts) {
            FilePath src = workspace.child(path);
            if (src.exists()) {
                if (src.isDirectory()) {
                    files.addAll(Arrays.asList(src.list("**/coverage*.xml")));
                } else {
                    files.add(src);
                }
            }
        }
        return files.toArray(new FilePath[files.size()]);
    }

    /**
     * save emma reports from the workspace to build folder
     */
    protected static void saveCoverageReports(FilePath folder, FilePath[] files) throws IOException, InterruptedException {
        folder.mkdirs();
        for (int i = 0; i < files.length; i++) {
            String name = "coverage" + (i > 0 ? i : "") + ".xml";
            FilePath src = files[i];
            FilePath dst = folder.child(name);
            src.copyTo(dst);
        }
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        try{
            perform(build, build.getWorkspace(), launcher, listener);
            return true;
        } catch(Exception ex){
            return false;
        }
    }

	private void checkThreshold(Run<?, ?> build,
			final PrintStream logger, EnvVars env, final EmmaBuildAction action) {
		if (useThreshold) {
        	if (isMethodCoverageOk(action) 
        			|| isClassCoverageOk(action) 
        			|| isBlockCoverageOk(action)
        			|| isLineCoverageOk(action)){
        		logger.println("Emma: Build failed due to coverage percentage threshold exceeds ");
        		build.setResult(Result.FAILURE);
        	} 
        	if (isClassCoverageOk(action)) {
        		logger.println("Emma: Classes coverage "+action.getClassCoverage().getPercentage(advancedSettings.getTestNotMandatory())+"% < "+healthReports.getMinClass()+"%.");
        	}
        	if (isMethodCoverageOk(action)) {
        		logger.println("Emma: Methods coverage "+action.getMethodCoverage().getPercentage(advancedSettings.getTestNotMandatory())+"% < "+healthReports.getMinMethod()+"%.");
			}
        	if (isBlockCoverageOk(action)) {
        		logger.println("Emma: Blocks coverage "+action.getBlockCoverage().getPercentage(advancedSettings.getTestNotMandatory())+"% < "+healthReports.getMinBlock()+"%.");
			}
        	if (isLineCoverageOk(action)) {
        		logger.println("Emma: Line coverage "+action.getLineCoverage().getPercentage(advancedSettings.getTestNotMandatory())+"% < "+healthReports.getMinLine()+"%.");
			}
		}
	}

	private boolean isLineCoverageOk(final EmmaBuildAction action) {
		return action.getLineCoverage().getPercentage(advancedSettings.getTestNotMandatory()) < healthReports.getMinLine();
	}

	private boolean isBlockCoverageOk(final EmmaBuildAction action) {
		return action.getBlockCoverage().getPercentage(advancedSettings.getTestNotMandatory()) < healthReports.getMinBlock();
	}

	private boolean isClassCoverageOk(final EmmaBuildAction action) {
		return action.getClassCoverage().getPercentage(advancedSettings.getTestNotMandatory()) < healthReports.getMinClass();
	}

	private boolean isMethodCoverageOk(final EmmaBuildAction action) {
		return action.getMethodCoverage().getPercentage(advancedSettings.getTestNotMandatory()) < healthReports.getMinMethod();
	}

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new EmmaProjectAction(project);
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    /**
     * Gets the directory to store report files
     */
    static File getEmmaReport(Run<?, ?> build) {
        return new File(build.getRootDir(), "emma");
    }

    @Override
    public BuildStepDescriptor<Publisher> getDescriptor() {
        return DESCRIPTOR;
    }

    private boolean didEmmaRun(Iterable<MavenBuild> mavenBuilds) {
        for (MavenBuild build : mavenBuilds) {
            if (didEmmaRun(build)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean didEmmaRun(MavenBuild mavenBuild) {
        for (ExecutedMojo mojo : mavenBuild.getExecutedMojos()) {
            if ("org.codehaus.mojo".equals(mojo.groupId) && "emma-maven-plugin".equals(mojo.artifactId)) {
                return true;
            }
        }
        return false;
    }
    
    @Extension
    public static final BuildStepDescriptor<Publisher> DESCRIPTOR = new DescriptorImpl();

    @Override
    public void perform(Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        final PrintStream logger = listener.getLogger();
        
        // Make sure Emma actually ran
        if (build instanceof MavenBuild) {
            MavenBuild mavenBuild = (MavenBuild) build;
            if (!didEmmaRun(mavenBuild)) {
                listener.getLogger().println("Skipping Emma coverage report as mojo did not run.");
                return;
            }
        } else if (build instanceof MavenModuleSetBuild) {
            MavenModuleSetBuild moduleSetBuild = (MavenModuleSetBuild) build;
            if (!didEmmaRun(moduleSetBuild.getModuleLastBuilds().values())) {
                listener.getLogger().println("Skipping Emma coverage report as mojo did not run.");
                return;
            }
        }       
        EnvVars env = build.getEnvironment(listener);
        
        //env.overrideAll(build.getBuildVariables());
        includes = env.expand(includes);

        FilePath[] reports;
        if (includes == null || includes.trim().length() == 0) {
            logger.println("Emma: looking for coverage reports in the entire workspace: " + workspace.getRemote());
            reports = locateCoverageReports(workspace, "**/emma/coverage.xml");
        } else {
            logger.println("Emma: looking for coverage reports in the provided path: " + includes);
            reports = locateCoverageReports(workspace, includes);
        }

        if (reports.length == 0) {
            if (build.getResult().isWorseThan(Result.UNSTABLE)) {
                return;
            }

            logger.println("Emma: no coverage files found in workspace. Was any report generated?");
            build.setResult(Result.FAILURE);
            return;
        } else {
            String found = "";
            for (FilePath f : reports) {
                found += "\n          " + f.getRemote();
            }
            logger.println("Emma: found " + reports.length + " report files: " + found);
        }

        FilePath emmafolder = new FilePath(getEmmaReport(build));
        saveCoverageReports(emmafolder, reports);
        logger.println("Emma: stored " + reports.length + " report files in the build folder: " + emmafolder);

        final EmmaBuildAction action = EmmaBuildAction.load(build, rule, healthReports, reports);

        action.applySettings(advancedSettings);
        
        logger.println("Emma: " + action.getBuildHealth().getDescription());
        
        build.getActions().add(action);

        final CoverageReport result = action.getResult();
        if (result == null) {
            logger.println("Emma: Could not parse coverage results. Setting Build to failure.");
            build.setResult(Result.FAILURE);
        } else if (result.isFailed()) {
            logger.println("Emma: code coverage enforcement failed. Setting Build to unstable.");
            build.setResult(Result.UNSTABLE);
        }
        
        checkThreshold(build, logger, env, action);
    }

    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public DescriptorImpl() {
            super(EmmaPublisher.class);
        }

        @Override
        public String getDisplayName() {
            return Messages.EmmaPublisher_DisplayName();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public Publisher newInstance(StaplerRequest req, JSONObject json) throws FormException {
            EmmaPublisher pub = new EmmaPublisher();
            req.bindParameters(pub, "emma.");
            req.bindParameters(pub.healthReports, "emmaHealthReports.");
            // start ugly hack
            //@TODO remove ugly hack
            // the default converter for integer values used by req.bindParameters
            // defaults an empty value to 0. This happens even if the type is Integer
            // and not int.  We want to change the default values, so we use this hack.
            //
            // If you know a better way, please fix.
            if ("".equals(req.getParameter("emmaHealthReports.maxClass"))) {
                pub.healthReports.setMaxClass(100);
            }
            if ("".equals(req.getParameter("emmaHealthReports.maxMethod"))) {
                pub.healthReports.setMaxMethod(70);
            }
            if ("".equals(req.getParameter("emmaHealthReports.maxBlock"))) {
                pub.healthReports.setMaxBlock(80);
            }
            if ("".equals(req.getParameter("emmaHealthReports.maxLine"))) {
                pub.healthReports.setMaxLine(80);
            }
            if ("".equals(req.getParameter("emmaHealthReports.maxCondition"))) {
                pub.healthReports.setMaxCondition(80);
            }
            // end ugly hack

            /**
             * bind AdvancedSettings object to config UI elements
             */
            req.bindParameters(pub.advancedSettings, "emmaAdvancedSettings.");
        
            return pub;
        }
    }
}
