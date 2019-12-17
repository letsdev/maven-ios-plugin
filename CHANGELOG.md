# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.18.11] - 2019-12-17
### Added
- xcframeworks will be generated per default (iOSXcFrameworkBuild), when building a iOS/macOS library

## [1.18.10] - 2019-11-26
### Changed
- added space to some parameters in ProjectTester
- provisioning profiles get set correctly now

### Added
- kills simulator application when resetting devices

## [1.18.9] - 2019-11-04
### Added
- added sdk argument for xcodebuild clean

## [1.18.8] - 2019-11-04
### Changed
- fixed xcpretty log file argument 

## [1.18.7] - 2019-10-31
### Added
- reading name from provisioning profile, setting provisioningProfileSpecifier 

### Changed
- adjusted integration of altool, supporting app store uploads with Xcode 11 

## [1.18.6] - 2019-10-28
### Added
- added xcpretty for test execution
- added support for Carthage dependencies (carthageEnabled)
- added configuration for xcode build directory (derivedDataPath)
- added configuration for xcode build directory while executing tests (xcTestsDerivedDataPath)
- integrated xcpretty-json-formatter (installation: gem install xcpretty-json-formatter)
- added configuration for provisioningProfileName
- supporting OCLint with xcpretty

### Changed
- erasing xcode simulator contents before running tests
- setting x86_64 as default simulator architecture
- setting iPhone X,OS=latest as default xc test destination
- xctest execution can be done with xcode workspace files
- stripping simulator architectures for app-store builds