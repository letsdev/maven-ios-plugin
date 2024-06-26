/**
 * ios-maven-plugin
 * <p/>
 * User: fkoebel
 * Date: 2016-06-23
 * <p/>
 * This code is copyright (c) 2016 let's dev.
 * URL: https://www.letsdev.de
 * e-Mail: contact@letsdev.de
 */

package de.letsdev.maven.plugins.ios;

import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.Map;

import de.letsdev.maven.plugins.ios.mojo.IOSException;

public class ProjectTester {

    public static void test(final Map<String, String> properties,
                            MavenProject mavenProject) throws IOSException, IOException {

        if (Utils.shouldSkipTests(properties)) {
            return;
        }

        String projectName = Utils.buildProjectName(properties, mavenProject);
        File workDirectory = Utils.getWorkDirectory(properties, mavenProject, projectName);
        File targetDirectory = Utils.getTargetDirectory(mavenProject);

        copyArchitecturesToDirectory(targetDirectory);
        copyArchitecturesToDirectory(workDirectory);

        if (Utils.shouldResetIphoneSimulators(properties)) {
            resetSimulators(workDirectory);
        }

        String scheme = properties.get(Utils.PLUGIN_PROPERTIES.XCTEST_SCHEME.toString());
        String configuration = properties.get(Utils.PLUGIN_PROPERTIES.XCTEST_CONFIGURATION.toString());
        String sdk = properties.get(Utils.PLUGIN_PROPERTIES.XCTEST_SDK.toString());
        String sdkArchs = Utils.getArchitecturesForSdk(properties, sdk);
        String destination = properties.get(Utils.PLUGIN_PROPERTIES.XCTEST_DESTINATION.toString());
        StringBuilder otherArguments = new StringBuilder();
        otherArguments.append(properties.get(Utils.PLUGIN_PROPERTIES.XCTEST_BUILD_ARGUMENTS.toString()));
        otherArguments.append(" ");

        if (Utils.shouldUseWorkspaceFile(properties)) {
            otherArguments.append("-workspace");
            otherArguments.append(" ");
            otherArguments.append(projectName).append(".xcworkspace");
        }

        if (properties.containsKey(Utils.PLUGIN_PROPERTIES.XCTEST_DERIVED_DATA_PATH.toString())) {
            otherArguments.append(" " + "-derivedDataPath ")
                    .append(properties.get(Utils.PLUGIN_PROPERTIES.XCTEST_DERIVED_DATA_PATH.toString()));
        } else if (properties.containsKey(Utils.PLUGIN_PROPERTIES.DERIVED_DATA_PATH.toString())) {
            otherArguments.append(" " + "-derivedDataPath ")
                    .append(properties.get(Utils.PLUGIN_PROPERTIES.DERIVED_DATA_PATH.toString()));
        }

        if (sdk != null) {
            otherArguments.append(" -sdk ").append(sdk);
            otherArguments.append(" ARCHS=").append(sdkArchs);
            otherArguments.append(" VALID_ARCHS=").append(sdkArchs);
        }

        String jsonOutputFile = Utils.createJsonOutputFilePath("test", properties);
        String xcPrettyCommand = Utils.getXcprettyCommand("testResults.txt", jsonOutputFile);
        String buildCommandWrapperParameter = Utils.buildCommandWrapperParameter(properties, workDirectory, "test");

        final String scriptName = "run-xctests.sh";

        File tempFile = Utils.createTempFile(scriptName);

        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", tempFile.getAbsoluteFile().toString(), scheme,
                configuration, destination, otherArguments.toString(), xcPrettyCommand,
                buildCommandWrapperParameter);

        processBuilder.directory(workDirectory);
        CommandHelper.performCommand(processBuilder);
    }

    private static void resetSimulators(File workDirectory) throws IOSException, IOException {

        final String scriptName = "reset-simulators.sh";

        File tempFile = Utils.createTempFile(scriptName);

        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", tempFile.getAbsoluteFile().toString());
        processBuilder.directory(workDirectory);
        CommandHelper.performCommand(processBuilder);
    }

    private static void copyArchitecturesToDirectory(File rootDirectory) {
        // Run shell-script from resource-folder.
        try {
            final String scriptName = "copy-dependencies-back-to-target.sh";
            File tempFile = Utils.createTempFile(scriptName);
            ProcessBuilder processBuilder = new ProcessBuilder("sh", tempFile.getAbsoluteFile().toString(),
                    rootDirectory.getAbsolutePath());

            processBuilder.directory(rootDirectory);
            CommandHelper.performCommand(processBuilder);
        } catch (IOException | IOSException e) {
            e.printStackTrace();
            //throw new IOSException(e);
        }
    }
}
