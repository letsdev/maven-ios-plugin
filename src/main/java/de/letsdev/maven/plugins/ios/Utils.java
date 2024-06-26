package de.letsdev.maven.plugins.ios;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.Map;

import de.letsdev.maven.plugins.ios.mojo.IOSException;
import de.letsdev.maven.plugins.ios.mojo.container.XcodeArchiveProductType;
import de.letsdev.maven.plugins.ios.mojo.container.XcodeExportOptions;

/**
 * Maven iOS Plugin
 * <p/>
 * User: cwack
 * Date: 09.10.2012
 * Time: 19:54:44
 * <p/>
 * This code is copyright (c) 2012 let's dev.
 * URL: https://www.letsdev.de
 * e-Mail: contact@letsdev.de
 */

public class Utils {

    private static String BUNDLE_VERSION_SNAPSHOT_ID = "-SNAPSHOT";

    static String SDK_IPHONE_OS = "iphoneos";
    static String SDK_IPHONE_SIMULATOR = "iphonesimulator";

    private static String RELEASE_TASK = "releaseTask";
    private static String RELEASE_TASK_TESTFLIGHT = "Testflight";
    private static String RELEASE_TASK_APP_STORE_UPLOAD = "AppStoreUpload";

    private static String EXPORT_PRODUCT_PATH_BASE = "Products/Library";
    private static String EXPORT_PRODUCT_PATH_FRAMEWORK = EXPORT_PRODUCT_PATH_BASE + "/Frameworks";
    private static String EXPORT_PRODUCT_PATH_BUNDLE = EXPORT_PRODUCT_PATH_BASE + "/Bundles";

    public static String XCODE_BUILD_PARAM_MARKETING_VERSION = "MARKETING_VERSION";

    public static String XCODE_BUILD_PARAM_PROJECT_VERSION = "CURRENT_PROJECT_VERSION";

    public enum PLUGIN_PROPERTIES {

        APP_NAME("appName"),
        APP_ICON_NAME("appIconName"),
        BUILD_ID("buildId"),
        BUNDLE_IDENTIFIER("bundleIdentifier"),
        DISPLAY_NAME("displayName"),
        DEPLOY_IPA_PATH("deployIpaPath"),
        DEPLOY_ICON_PATH("deployIconPath"),
        RELEASE_TASK("releaseTask"),
        CLASSIFIER("classifier"),
        IOS_FRAMEWORK_BUILD("iOSFrameworkBuild"),
        IOS_XC_FRAMEWORK_BUILD("iOSXcFrameworkBuild"),
        MACOSX_FRAMEWORK_BUILD("macOSFrameworkBuild"),
        IPHONEOS_ARCHITECTURES("iphoneosArchitectures"),
        IPHONESIMULATOR_ARCHITECTURES("iphonesimulatorArchitectures"),
        IPHONESIMULATOR_BITCODE_ENABLED("iphonesimulatorBitcodeEnabled"),
        CODE_SIGNING_ENABLED("codeSigningEnabled"),
        CODE_SIGN_WITH_RESOURCE_RULES_ENABLED("codeSignWithResourceRulesEnabled"),
        CODE_SIGN_IDENTITY("codeSignIdentity"),
        CODE_SIGN_ENTITLEMENTS("codeSignEntitlements"),
        CONFIGURATION("configuration"),
        HOCKEY_APP_TOKEN("hockeyAppToken"),
        INFO_PLIST("infoPlist"),
        IPA_VERSION("ipaVersion"),
        KEYCHAIN_PASSWORD("keychainPassword"),
        KEYCHAIN_PATH("keychainPath"),
        RELEASE_NOTES("releaseNotes"),
        SCHEME("scheme"),
        SDK("sdk"),
        SOURCE_DIRECTORY("sourceDir"),
        PROJECT_NAME("projectName"),
        GCC_PREPROCESSOR_DEFINITIONS("gccPreprocessorDefinitions"),
        PROVISIONING_PROFILE_UUID("provisioningProfileUUID"),
        PROVISIONING_PROFILE_SPECIFIER("provisioningProfileSpecifier"),
        DEVELOPMENT_TEAM("developmentTeam"),
        TARGET("target"),
        TARGET_DIR("targetDir"),
        BUILD_TO_XCARCHIVE_ENABLED("build-xcarchive"),
        COCOA_PODS_ENABLED("cocoa-pods-enabled"),
        CARTHAGE_ENABLED("carthage-enabled"),
        CARTHAGE_COMMAND_ARGUMENTS("carthage-command-arguments"),
        ITUNES_CONNECT_USERNAME("iTunesConnectUsername"),
        ITUNES_CONNECT_PASSWORD("iTunesConnectPassword"),
        XCODE_VERSION("xcodeVersion"),
        XCTEST_SCHEME("xcTestsScheme"),
        XCTEST_CONFIGURATION("xcTestsConfiguration"),
        XCTEST_DESTINATION("xcTestsDestination"),
        XCTEST_SDK("xcTestsSdk"),
        SKIP_TESTS("skipTests"),
        XCTEST_BUILD_ARGUMENTS("xcTestsBuildArguments"),
        RESET_SIMULATORS("resetSimulators"),
        DERIVED_DATA_PATH("derivedDataPath"),
        XCTEST_DERIVED_DATA_PATH("xcTestsDerivedDataPath"),
        PROVISIONING_PROFILE_NAME("provisioningProfileName"),
        XCODE_BUILD_COMMAND_WRAPPER_EXECUTABLE("xcodeBuildCommandWrapperExecutable"),
        XCODE_BUILD_COMMAND_WRAPPER_OUTPUT_BASE_DIRECTORY("xcodeBuildCommandWrapperOutputBaseDirectory"),
        SWIFT_PM_DISABLE_AUTOMATIC_PACKAGE_RESOLUTION("swiftPmDisableAutomaticPackageResolution");

        PLUGIN_PROPERTIES(String name) {

            this.name = name;
        }

        private final String name;

        public String toString() {

            return name;
        }
    }

    public enum PLUGIN_SUFFIX {
        APP("app"),
        XCARCHIVE("xcarchive"),
        IPA("ipa"),
        APP_DSYM("app.dSYM"),
        DSYMS("dSYMs"),
        FRAMEWORK("framework"),
        XCFRAMEWORK("xcframework"),
        FRAMEWORK_ZIP("zip"),
        PLIST("plist");

        PLUGIN_SUFFIX(String name) {

            this.name = name;
        }

        private final String name;

        public String toString() {

            return name;
        }
    }

    public enum PLUGIN_PACKAGING {
        IPA("ipa"),
        IOS_FRAMEWORK("ios-framework"),
        FRAMEWORK_ZIP("zip");

        PLUGIN_PACKAGING(String name) {

            this.name = name;
        }

        private final String name;

        public String toString() {

            return name;
        }
    }

    public static boolean isMacOSFramework(Map<String, String> properties) {

        return "true".equals(properties.get(PLUGIN_PROPERTIES.MACOSX_FRAMEWORK_BUILD.toString()));
    }

    public static boolean isiOSFramework(MavenProject mavenProject, Map<String, String> properties) {

        return isiOSFramework(mavenProject,
                "true".equals(properties.get(PLUGIN_PROPERTIES.IOS_FRAMEWORK_BUILD.toString())));
    }

    static boolean isiOSXcFramework(Map<String, String> properties) {

        return "true".equals(properties.get(PLUGIN_PROPERTIES.IOS_XC_FRAMEWORK_BUILD.toString()));
    }

    public static boolean isiOSFramework(MavenProject mavenProject, boolean isFrameworkBuild) {

        return mavenProject.getPackaging().equals(Utils.PLUGIN_PACKAGING.IOS_FRAMEWORK.toString()) || isFrameworkBuild;
    }

    static boolean shouldCodeSign(MavenProject mavenProject, Map<String, String> properties) {

        return !isiOSFramework(mavenProject, properties) && !isMacOSFramework(properties);
    }

    static boolean shouldCodeSignWithResourceRules(Map<String, String> properties) {

        return "true".equals(properties.get(PLUGIN_PROPERTIES.CODE_SIGN_WITH_RESOURCE_RULES_ENABLED.toString()));
    }

    static boolean shouldBuildXCArchive(Map<String, String> properties) {

        return "true".equals(properties.get(PLUGIN_PROPERTIES.BUILD_TO_XCARCHIVE_ENABLED.toString()));
    }

    static boolean shouldBuildXCArchiveWithExportOptionsPlist(XcodeExportOptions xcodeExportOptions) {

        return xcodeExportOptions.method != null;
    }

    static String getArchiveName(final String appName, MavenProject mavenProject) {

        return getTargetDirectory(mavenProject).getAbsolutePath() + File.separator + appName + "."
                + PLUGIN_SUFFIX.XCARCHIVE;
    }

    static String getIpaName(final String schemeName) {

        return schemeName + "." + PLUGIN_SUFFIX.IPA;
    }

    static File getTargetDirectory(MavenProject mavenProject) {

        return new File(mavenProject.getBuild().getDirectory());
    }

    public static String buildProjectName(Map<String, String> buildProperties, MavenProject mavenProject) {

        String projectName = mavenProject.getArtifactId();
        if (buildProperties.get(Utils.PLUGIN_PROPERTIES.PROJECT_NAME.toString()) != null) {
            projectName = buildProperties.get(Utils.PLUGIN_PROPERTIES.PROJECT_NAME.toString());
        }
        return projectName;
    }

    static boolean cocoaPodsEnabled(Map<String, String> buildProperties) {

        return "true".equals(buildProperties.get(PLUGIN_PROPERTIES.COCOA_PODS_ENABLED.toString()));
    }

    static boolean carthageEnebled(Map<String, String> buildProperties) {

        return "true".equals(buildProperties.get(PLUGIN_PROPERTIES.CARTHAGE_ENABLED.toString()));
    }

    static boolean isIphoneSimulatorBitcodeEnabled(Map<String, String> buildProperties) {

        return "true".equals(buildProperties.get(PLUGIN_PROPERTIES.IPHONESIMULATOR_BITCODE_ENABLED.toString()));
    }

    static boolean shouldResetIphoneSimulators(Map<String, String> buildProperties) {

        return "true".equals(buildProperties.get(PLUGIN_PROPERTIES.RESET_SIMULATORS.toString()));
    }

    static boolean disableAutomaticPackageResolutionEnabled(Map<String, String> buildProperties) {

        return "true".equals(buildProperties.get(PLUGIN_PROPERTIES.SWIFT_PM_DISABLE_AUTOMATIC_PACKAGE_RESOLUTION.toString()));
    }

    public static File getWorkDirectory(Map<String, String> buildProperties, MavenProject mavenProject,
                                        String projectName) throws IOSException {

        File workDirectory = new File(mavenProject.getBasedir().toString() + File.separator + buildProperties.get(
                Utils.PLUGIN_PROPERTIES.SOURCE_DIRECTORY.toString()) + File.separator + projectName);

        if (!workDirectory.exists()) {
            throw new IOSException("Invalid sourceDirectory specified: " + workDirectory.getAbsolutePath());
        }
        return workDirectory;
    }

    private static String getProjectVersion(MavenProject mavenProject, Map<String, String> properties) {

        String projectVersion = mavenProject.getVersion();

        if (properties.get(Utils.PLUGIN_PROPERTIES.IPA_VERSION.toString()) != null) {
            projectVersion = properties.get(Utils.PLUGIN_PROPERTIES.IPA_VERSION.toString());
        }

        return projectVersion;
    }

    static boolean isTestflightBuild(Map<String, String> buildProperties) {

        String valueReleaseTask = buildProperties.get(Utils.RELEASE_TASK);
        return Utils.RELEASE_TASK_TESTFLIGHT.equalsIgnoreCase(valueReleaseTask);
    }

    static boolean isAppStoreBuild(Map<String, String> buildProperties) {

        String valueReleaseTask = buildProperties.get(Utils.RELEASE_TASK);
        return Utils.RELEASE_TASK_APP_STORE_UPLOAD.equalsIgnoreCase(valueReleaseTask);
    }

    public static String getAdjustedVersion(MavenProject mavenProject, Map<String, String> properties) {

        String result = getProjectVersion(mavenProject, properties);

        //remove -SNAPSHOT in version number in order to prevent malformed version numbers in framework builds
        //FK 02.12.: in the meantime -SNAPSHOT version suffixes are also invalid for normal xcodebuilds, so we remove -SNAPSHOT in every case
        result = result.replace(Utils.BUNDLE_VERSION_SNAPSHOT_ID, "");

        return result;
    }

    public static String getAppName(Map<String, String> properties) {

        return properties.get(PLUGIN_PROPERTIES.APP_NAME.toString());
    }

    public static String getConfiguration(Map<String, String> properties) {

        return properties.get(PLUGIN_PROPERTIES.CONFIGURATION.toString());
    }

    public static String getClassifier(Map<String, String> properties) {

        return properties.get(PLUGIN_PROPERTIES.CLASSIFIER.toString());
    }

    public static String getBuildId(Map<String, String> properties) {

        return properties.get(PLUGIN_PROPERTIES.BUILD_ID.toString());
    }

    public static String getCurrentXcodeVersion(File workDirectory) {

        String xcodeVersion = "";

        // Run shell-script from resource-folder.
        try {
            final String scriptName = "get-xcode-version.sh";
            File tempFile = createTempFile(scriptName);
            ProcessBuilder processBuilder = new ProcessBuilder("sh", tempFile.getAbsoluteFile().toString());

            processBuilder.directory(workDirectory);
            xcodeVersion = CommandHelper.performCommand(processBuilder);
            System.out.println("############################################################################");
            System.out.println("################################ " + xcodeVersion
                    + " is current xcode version ############################################");
            System.out.println("############################################################################");
        } catch (IOException | IOSException e) {
            e.printStackTrace();
        }

        return xcodeVersion;
    }

    static String getSdk(Map<String, String> properties, boolean shouldUseIphoneSimulatorSDK) {
        String sdk = properties.get(Utils.PLUGIN_PROPERTIES.SDK.toString());
        if (shouldUseIphoneSimulatorSDK) {
            sdk = Utils.SDK_IPHONE_SIMULATOR;
        }

        return sdk;
    }

    static String getArchitecturesForSdk(Map<String, String> properties, String sdk) {

        String architectures = properties.get(PLUGIN_PROPERTIES.IPHONEOS_ARCHITECTURES.toString());

        if (SDK_IPHONE_SIMULATOR.equals(sdk)) {
            architectures = properties.get(Utils.PLUGIN_PROPERTIES.IPHONESIMULATOR_ARCHITECTURES.toString());
        }

        return architectures;
    }

    static String getExportProductPath(XcodeArchiveProductType productType) {

        String productPath;

        switch (productType) {
            case XCODE_ARCHIVE_PRODUCT_TYPE_BUNDLE:
                productPath = Utils.EXPORT_PRODUCT_PATH_BUNDLE;
                break;
            case XCODE_ARCHIVE_PRODUCT_TYPE_FRAMEWORK:
                productPath = Utils.EXPORT_PRODUCT_PATH_FRAMEWORK;
                break;
            default:
                productPath = "";
                break;
        }

        return productPath;
    }

    static XcodeArchiveProductType getExportProductType(String productName) {

        XcodeArchiveProductType type = XcodeArchiveProductType.XCODE_ARCHIVE_PRODUCT_TYPE_UNKNOWN;

        String EXPORT_PRODUCT_TYPE_EXTENSION_BUNDLE = "bundle";
        String EXPORT_PRODUCT_TYPE_EXTENSION_FRAMEWORK = "framework";
        if (EXPORT_PRODUCT_TYPE_EXTENSION_FRAMEWORK.equals(FilenameUtils.getExtension(productName))) {
            type = XcodeArchiveProductType.XCODE_ARCHIVE_PRODUCT_TYPE_FRAMEWORK;
        } else if (EXPORT_PRODUCT_TYPE_EXTENSION_BUNDLE.equals(FilenameUtils.getExtension(productName))) {
            type = XcodeArchiveProductType.XCODE_ARCHIVE_PRODUCT_TYPE_BUNDLE;
        }

        return type;
    }

    static boolean shouldUseWorkspaceFile(Map<String, String> properties) {

        if (properties.containsKey(PLUGIN_PROPERTIES.COCOA_PODS_ENABLED.toString())) {
            return properties.get(PLUGIN_PROPERTIES.COCOA_PODS_ENABLED.toString()).equals("true");
        }

        return false;
    }

    static String getXcprettyCommand(String logFileName, String jsonOutputFileIdentifier) {
        String xcprettyOutputFile = jsonOutputFileIdentifier + "-result.json";
        String xcprettyCompilationDatabaseFile = jsonOutputFileIdentifier + "-compile-commands.json";

        String xcPrettyStatement = " | tee " + logFileName + " |";
        if (jsonOutputFileIdentifier != null && !jsonOutputFileIdentifier.isEmpty()) {
            xcPrettyStatement += " XCPRETTY_JSON_FILE_OUTPUT=" + xcprettyOutputFile;
        }

        xcPrettyStatement += " xcpretty";

        if (jsonOutputFileIdentifier != null && !jsonOutputFileIdentifier.isEmpty()) {
            xcPrettyStatement += " -f `xcpretty-json-formatter` -r json-compilation-database -o " + xcprettyCompilationDatabaseFile;
        }

        xcPrettyStatement += " && exit ${PIPESTATUS[0]}";
        return xcPrettyStatement;
    }

    public static File createTempFile(String shellScriptFileName) throws IOException {
        File tempFile = File.createTempFile(shellScriptFileName, "sh");
        InputStream iStream = ProjectBuilder.class.getResourceAsStream("/META-INF/" + shellScriptFileName);
        OutputStream oStream = new FileOutputStream(tempFile);

        try {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = iStream.read(buffer)) != -1) {
                oStream.write(buffer, 0, bytesRead);
            }
        } finally {
            oStream.flush();
            oStream.close();

        }

        return tempFile;
    }

    static void executeShellScript(String scriptName, String value1, String value2,
                                   File workDirectory) throws IOSException {

        // Run shell-script from resource-folder.
        try {
            File tempFile = createTempFile(scriptName);

            if (value1 == null) {
                value1 = "";
            }

            if (value2 == null) {
                value2 = "";
            }

            ProcessBuilder processBuilder = new ProcessBuilder("sh", tempFile.getAbsoluteFile().toString(), value1,
                    value2);

            processBuilder.directory(workDirectory);
            CommandHelper.performCommand(processBuilder);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOSException(e);
        }
    }

    static String createJsonOutputFilePath(String relativeFilePath, Map<String, String> properties) {

        String jsonOutputFile = "";
        if (properties.containsKey(Utils.PLUGIN_PROPERTIES.DERIVED_DATA_PATH.toString())) {
            jsonOutputFile += properties.get(Utils.PLUGIN_PROPERTIES.DERIVED_DATA_PATH.toString()) + "/";
        }
        jsonOutputFile += "Build/reports/";
        jsonOutputFile += relativeFilePath;
        return jsonOutputFile;
    }

    static String getFrameworkBuildName(String appName) {
        return appName + "." + PLUGIN_SUFFIX.FRAMEWORK.toString();
    }

    static String getFrameworkTargetName(String appName, Map<String, String> properties) {
        String frameworkTargetName = getFrameworkBuildName(appName);
        if (isiOSXcFramework(properties)) {
            frameworkTargetName = appName + "." + PLUGIN_SUFFIX.XCFRAMEWORK.toString();
        }

        return frameworkTargetName;
    }

    static String buildCommandWrapperParameter(final Map<String, String> properties, File projectDirectory,
                                               String outputDirectoryExtension) throws IOSException {

        String parameter = "";

        if (properties.containsKey(Utils.PLUGIN_PROPERTIES.XCODE_BUILD_COMMAND_WRAPPER_EXECUTABLE.toString())) {
            parameter += properties.get(Utils.PLUGIN_PROPERTIES.XCODE_BUILD_COMMAND_WRAPPER_EXECUTABLE.toString());

            if (properties.containsKey(
                    Utils.PLUGIN_PROPERTIES.XCODE_BUILD_COMMAND_WRAPPER_OUTPUT_BASE_DIRECTORY.toString())) {
                String directoryPath = properties.get(
                        Utils.PLUGIN_PROPERTIES.XCODE_BUILD_COMMAND_WRAPPER_OUTPUT_BASE_DIRECTORY.toString()) + "/"
                        + outputDirectoryExtension;
                parameter += " --out-dir "  + projectDirectory.toString() + "/" + directoryPath;

                Utils.executeShellScript("create-directory.sh", directoryPath, null, projectDirectory);
            }
        }

        return parameter;
    }

    static boolean shouldSkipTests(Map<String, String> buildProperties) {

        return "true".equals(buildProperties.get(PLUGIN_PROPERTIES.SKIP_TESTS.toString()));
    }
}
