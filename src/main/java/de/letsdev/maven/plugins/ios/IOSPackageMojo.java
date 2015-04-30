/**
 * Maven iOS Plugin
 *
 * User: sbott
 * Date: 19.07.2012
 * Time: 19:54:44
 *
 * This code is copyright (c) 2012 let's dev.
 * URL: http://www.letsdev.de
 * e-Mail: contact@letsdev.de
 */

package de.letsdev.maven.plugins.ios;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.*;


/**
 * @author let's dev
 * @goal package
 * @phase package
 */
public class IOSPackageMojo extends AbstractMojo {

    /**
     * iOS app name
     *
     * @parameter property="ios.appName"
     * @required
     */
    private String appName;

    /**
     * iOS classifier
     *
     * @parameter property="ios.classifier"
     */
    private String classifier;

    /**
     * build id
     * @parameter
     * 		property="ios.buildId"
     */
    private String buildId;

    /**
     * iOS configuration
     *
     * @parameter property="ios.configuration"
     * default-value="Release"
     */
    private String configuration;

    /**
     * ipaVersion
     * @parameter
     * 		property="ios.ipaVersion"
     */
    private String ipaVersion;

    /**
     * flag for iOS framework builds
     * @parameter
     * 		property="ios.iOSFrameworkBuild"
     * 		default-value="false"
     */
    private boolean iOSFrameworkBuild;

    /**
     * The maven project.
     *
     * @parameter property="project"
     * @required
     * @readonly
     */
    protected MavenProject mavenProject;

    /**
     * @component
     */
    private MavenProjectHelper projectHelper;

    /**
     *
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        Artifact currentArtifact = mavenProject.getArtifact();
        final String targetDir = mavenProject.getBuild().getDirectory();
        String destinationDirectory = null;
        String artifactName = null;
        String projectVersion = mavenProject.getVersion();

        String classifierString = (this.classifier != null? "-" + this.classifier + "-" : "-");

        String artifactType = (currentArtifact.getType() == null || "pom".equals(currentArtifact.getType() ) ? Utils.PLUGIN_PACKAGING.IPA.toString() : currentArtifact.getType());
        if (iOSFrameworkBuild) {
            artifactType = Utils.PLUGIN_PACKAGING.FRAMEWORK_ZIP.toString();
        }

        if (ipaVersion != null) {
            projectVersion = ipaVersion;
        }
        if (buildId != null) {
            projectVersion += "-b" + buildId;
        }

        if (Utils.isiOSFramework(mavenProject, iOSFrameworkBuild)) {
            artifactName = appName + "." + Utils.PLUGIN_SUFFIX.FRAMEWORK_ZIP;
            destinationDirectory = targetDir;
//            this.projectHelper.attachArtifact( mavenProject, Utils.PLUGIN_SUFFIX.IOS_FRAMEWORK.toString(), null, new File(destinationDirectory + File.separator + artifactName));
        }
        else {
            mavenProject.setPackaging(Utils.PLUGIN_PACKAGING.IPA.toString());
            artifactName = appName + "-" + projectVersion + "." + Utils.PLUGIN_SUFFIX.IPA;
            destinationDirectory = targetDir + File.separator + configuration + "-iphoneos" + File.separator;
        }

        File destinationFile = new File(destinationDirectory + File.separator + artifactName);

        File artifactFile =  new File(targetDir + File.separator + appName + classifierString + projectVersion + "." + (Utils.isiOSFramework(mavenProject, iOSFrameworkBuild) ? Utils.PLUGIN_SUFFIX.FRAMEWORK_ZIP : Utils.PLUGIN_SUFFIX.IPA));

        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(destinationFile);
            out = new FileOutputStream(artifactFile, true);
            IOUtils.copy(in, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

        currentArtifact.setFile(artifactFile);

        projectHelper.attachArtifact(mavenProject, artifactType, this.classifier, artifactFile);
    }
}
