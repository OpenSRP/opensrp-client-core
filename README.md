[![Build Status](https://travis-ci.org/OpenSRP/opensrp-client-core.svg?branch=master)](https://travis-ci.org/OpenSRP/opensrp-client-core) [![Coverage Status](https://coveralls.io/repos/github/OpenSRP/opensrp-client-core/badge.svg?branch=master)](https://coveralls.io/github/OpenSRP/opensrp-client-core?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4a58cd4e1748432780ac66a9fbee0394)](https://www.codacy.com/app/OpenSRP/opensrp-client-core?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=OpenSRP/opensrp-client-core&amp;utm_campaign=Badge_Grade)

[![Dristhi](opensrp-app/res/drawable-mdpi/login_logo.png)](https://smartregister.atlassian.net/wiki/dashboard.action)


# Table of Contents

* [Introduction](#introduction)
* [Features](#features)
* [Why OpenSRP?](#why-opensrp)
* [Website](#website)
* [Developer Documentation](#developer-documentation)
   * [Pre-requisites](#pre-requisites)
   * [Installation Devices](#installation-devices)
   * [How to install](#how-to-install)
   * [Developer Guides](#developer-guides)
   * [Wiki](#wiki)
   * [Uses](#uses)

# Introduction

OpenSRP Client Core App/Module basically provides basic functionality such as networking, security, database access, common widgets, utilities, domain objects, service layer, broadcast receivers and syncing.

# Features

It provides:

1. Domain objects for forms and database.
2. Mappers between the different domains
3. Basic and advanced networking capabilities to securely and efficiently connect to an OpenSRP backend
4. Sync abilities that handle and maintain data consistency between the client and backend
5. Data creation, edit, retrieval and deletion capabilities on the client device
6. Security services that maintain global data and application security
7. Utilities used for file storage, caching, image rendering, logging, session management and number conversions
8. Access to tailored android views/widgets for OpenSRP
9. Device-to-device sharing of application data - This includes events, clients and profile images

# Why OpenSRP?

1. It provides client access on Android phones which are easily available and acquirable
2. It can work with minimal or no internet connection
3. It provides enhanced security
4. It primarily integrates with OpenMRS
5. It is tailored to be used by health workers who regularly provide outreach services
6. It generates custom reports eg. HIA 2
7. It manages stock levels for stock provided to the health workers
8. It implements the WHO-recommended **z-score** for child growth monitoring
9. It provides device-to-device sharing of medical records in areas without an internet connection.


# Website

If you are looking for more information regarding OpenSRP as a platform checkout the [OpenSRP Site](http://smartregister.org/)


# Developer Documentation

This section will provide a brief description on how to build and install the application from the repository source code.


## Pre-requisites

1. Make sure you have Java 1.7 to 1.8 installed
2. Make sure you have Android Studio installed or [download it from here](https://developer.android.com/studio/index.html)


## Installation Devices

1. Use a physical Android device to run the app
2. Use the Android Emulator that comes with the Android Studio installation (Slow & not advisable)
3. Use Genymotion Android Emulator
    * Go [here](https://www.genymotion.com/) and register for genymotion account if none. Free accounts have limitations which are not counter-productive
    * Download your OS Version of VirtualBox at [here](https://www.virtualbox.org/wiki/Downloads)
    * Install VirtualBox
    * Download Genymotion & Install it
    * Sign in to the genymotion app
    * Create a new Genymotion Virtual Device
        * **Preferrable & Stable Choice** - API 22(Android 5.1.0), Screen size of around 800 X 1280, 1024 MB Memory --> eg. Google Nexus 7, Google Nexus 5

## How to install

1. Import the project into Android Studio by: **Import a gradle project** option
   _All the plugins required are explicitly stated, therefore it can work with any Android Studio version - Just enable it to download any packages not available offline_
1. Open Genymotion and Run the Virtual Device created previously.
1. Run the app on Android Studio and chose the Genymotion Emulator as the ` Deployment Target`


### Developer Guides

If you want to contribute please refer to these resources:

   * [Getting started with OpenSRP](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/6619148/Getting+started+with+OpenSRP)
   * [Setup Instructions](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/6619255/Setup+Instructions)
   * [Complete OpenSRP Developer's Guide](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/6619193/OpenSRP+Developer%27s+Guide)
   * [Peer-to-Peer Library Guide](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/1139212418/Android+Peer-to-peer+sync+library?atlOrigin=eyJpIjoiYWE5NmM1ZTk3MGQ2NGU4OWE0ZTdmM2U2YTFjODg2YTAiLCJwIjoiYyJ9)

### Wiki

If you are looking for detailed guides on how to install, configure, contribute and extend OpenSRP visit [OpenSRP Wiki](https://smartregister.atlassian.net/wiki)


# Uses

OpenSRP Client core has been used in several modules and applications:

   * [OpenSRP Path application](https://github.com/OpenSRP/opensrp-client-path)
   * [OpenSRP KIP application](https://github.com/OpenSRP/opensrp-client-kip)
   * [OpenSRP Growth monitoring library](https://github.com/OpenSRP/opensrp-client-growth-monitoring)
   * [OpenSRP Immunization library](https://github.com/OpenSRP/opensrp-client-immunization)
   * [OpenSRP Native Form library](https://github.com/OpenSRP/opensrp-client-native-form)


# Main Functions

## 1. Security

Security is provided in the following:
   * Network - It supports SSL certificates from **[Let's Encrypt](https://letsencrypt.org/)** CA
   * Data access - Only registered providers are able to view and manipulate records
   * Data encryption - The database on the android client is encrypted with 256-bit AES encryption using [SQLCipher](https://guardianproject.info/code/sqlcipher/).

The security classes can be found in `org.smartregister.ssl`


Under the cryptography package we have CryptographicHelper class whose instance exposes methods

   *__byte[] encrypt(byte[] input, String keyAlias)__* For encryption of a byte array input with key

   *__byte[] decrypt(byte[] encrypted, String keyAlias)__* For decryption of encrypted byte array with key

   *__Key getKey(String keyAlias)__* For retrieving a generated key stored in the Android keystore

   *__void generateKey(String keyAlias)__* For key generation using a Key Alias parameter for use by Android keystore

   * NB: *  This class depends on `AndroidLegacyCryptography` class and the `AndroidMCryptography` class which both implement the above in different ways depending on the SDK version.
              `AndroidLegacyCryptography` has method implementation that are used when the SDK version is less than API level 23


## 2. Data management

This app provides data management.

It implements both plain and secure data storage. Classes implementing secure storage extend `SQLiteOpenHelper`, `Repository` or `BaseRepository`.

The rest use the SQLite helpers provided in the Android SDK.

For this reason, there are multiple implementations for storing the same model(s).

Class | Represents
----- | --------------
`EventClientRepository` | Events
`AlertRepository` | Alerts
`ChildRepository` | Children
`ClientRepository` | Clients/Patients
`DetailsRepository` | Details
`EligibleCoupleRepository` | Eligible couples
`EventRepository` | Events
`FormDataRepository` | Form data
`FormsVersionRepository` | Form version
`ImageRepository` | Image locations
`MotherRepository` | Mothers
`ServiceProvidedRepository` | Provided service to the patient
`SettingsRepository` | App settings eg. connection configurations
`TimelineEventRepository` | Timeline events

The data management classes can be found in `org.smartregister.repository`

## 3. Networking

This app provides the following networking capabilities:

Class | Represents
----- | --------------
`OpensrpSSLHelper` | SSL Connection helper
`OpenSRPImageLoader` | Asynchronous image downloader
`HttpAgent` | Synchronous networking class with username\password ([Basic Auth](https://tools.ietf.org/html/rfc2617)) access support
`ConnectivityChangeReceiver` | Network status detection by a broadcast receiver
`GZipEncodingHttpClient` | GZip encoding and decoding capabilities
`Session` | Session management
`UserService` | User authentication & client-server time synchronization

The networking classes can be found in:
   * `org.smartregister.service`
   * `org.smartregister.util`
   * `org.smartregister.client`
   * `org.smartregister.ssl`
   * `org.smartregister.view.receiver`


## 4. Domain Objects

This app provides the following domain objects:

Class | Represents
----- | ----------
`Address` | Location address containing map coordinates
`BaseDataObject` | Data object with datestamps, void flag, related void details and server version
`BaseEntity` | Extends `BaseDataObject` to include the `baseEntityId`, `identifiers`, `addresses` and `attributes` that are common in OpenSRP models
`Client` | Represents a patient in OpenSRP eg. a child. It contains relevant patient details and extends `BaseEntity`
`ColumnAttribute` | Represents a column using the type, is-primary-key and is-index properties. It is used in the `EventClientRepository` class to define and access columns in the appropriate table
`Event` | It represents an event in OpenSRP which are mainly [encounters](#https://github.com/OpenSRP/opensrp-client-native-form#encounter-types) eg. Birth Registration, Death. It extends the `BaseDataObject` and provides other properties
`Obs` | It represents an observation in an `Event` _above_
`Query` | It represents a data query and enables creation of queries using an OOP approach
`FormData` | It represents form fields, their inputs and any sub-forms
`FormField` | It represents a single form question/field with a name, value and source
`FormInstance` | It represents a `FormData` of a specific definition version
`FormSubmission` | It represents the status of a form before or after submission. It therefore contains other metadata such as client version and server verion.
`SubForm` | It represents a form inside another form
`Alert` | It represents a notification about an encounter which is due or overdue the expected time
`ANM` | It represents a health services provider
`Child` | It represents a child
`EligibleCouple` | It represents an eligible couple
`FormDefinitionVersion` | It represents a form version
`Mother` | It represents a mother
`Photo` | It represents a photo by storing the file path & resource id
`ProfileImage` | It represents the photo of an entity
`Report` | It represents a report
`MontlyReport` | It represents a monthly report
`Response` | It represents an HTTP response with status & payload
`ServiceProvided` | It represents a service that was provided to a patient
`TimelineEvent` | It represents an event within a patient's life eg. birth

The domain object classes can be found in `org.smartregister.domain`. There are several domains namely: global domain, form and database domain.


## 5. Sync

This app provides the following sync capabilities:

   * Periodic syncing based on network connection
   * Updating views with updated information

The sync classes can be found in `org.smartregister.sync`

## 6. Utilities

This app provides the following utilities:

Class | Provides
----- | --------
`BitmapImageCache` | Improved image caching
`Cache` | Data caching and modifications listener
`FileUtilities` | File storage utility
`FloatUtil` | Float conversion utility
`FormSubmissionBuilder` | Form submission builders
`FormUtils` | Form generation and manipulation utility
`IntegerUtil` | Integer conversion utility
`JsonFormUtils` | JSON form data extractor and injector
`OpenSRPImageLoader` | Asynchronous image downloader with thread-safe image caching
`Session` | Session manager
`StringUtil` | String manipulation utility
`TimelineEventComparator` | Timeline event comparator
`Utils` | Date conversion, android preference manipulator, view generator, metrics humanizer among other basic utility functions.
`AppExecutors` | Provides implementation of the executor interface that allows grouping request. Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind webservice requests)


The utility classes can be found under `org.smartregister.util`

## 7. Services

This app provides business logic for operations as follows:

Class | Business logic related to
----- | ----------
`ActionService` | Actions
`AlertService` | Alerts
`AllFormVersionSyncService` | Form versions
`ANMService` | Health service providers
`BeneficiaryService` | Beneficiaries
`ChildService` | Children
`Drishti` | Form submissions
`MotherService` | Mothers
`ServiceProvidedService` | Services provided

The service classes can be found in `org.smartregister.service`

## 8. App Localization

This app provides capability to support multiple languages.

Check out the sample app to see how to implement language switching.

Ensure each class in your app extends (directly or indirectly) a class in client-core. If it doesn't then extend MultiLanguageActivity instead of AppCompatActivity.

## 9. Data Compression

The package `compression` contains an interface `ICompression` whose methods are Implemented using the GZIPCompression class (which uses a GZIP implementation)
Other compression Algorithms can be used by adding a new class implementing the interface methods.

Methods in the ICompression interface are

*__byte[] compress(String rawString)__* Compress the given string input

*__String decompress(byte[] compressedBytes)__* Decompress a byte array of compressed data

*__void compress(String inputFilePath, String compressedOutputFilepath)__* Compress file in file path `inputFilePath` and output to location `compressedOutputFilepath`

*__void decompress(String compressedInputFilePath, String decompressedOutputFilePath)__* Decompress file in file path `compressedInputFilePath` and output to location `decompressedOutputFilePath`

## 10. Bootstrap View Generation

You can quickly bootstrap view generation that take the format of a `Register` or `Profile` using the package `org.smartregister.view`. Views that render a Register can extend the `BaseRegisterFragment`
that implements basic register functionality such as searching, listing, sorting and counting number of records on the generic base view whilst reading a `cursor` object.

For views that display a generic List of items but require heavier customization, using the `org.smartregister.view.fragment.BaseListFragment<T>` allows you to render any generic list while providing a context
aware background executor and error handling that only requires provisioning or `Callable` function to act as a data source.
Check the Sample app's `ReportFragment` that consumes a `Retrofit` response and renders a list of objects.

## Configurability

By placing a file named `app.properties` in your implementation assets folder (See sample app) , one can configure certain aspects of the app

### Configurable Settings

| Configuration                       | Type    | Default  | Description                                                              |
| ----------------------------------- | ------- | -------- | -------------------------------------------------------------------------|
| `system.toaster.centered`           | Boolean | false    | Position toaster(s) at the center of the view(s)                         |
| `disable.location.picker.view`      | Boolean | false    | Disables LocationPicker View                                             |
| `location.picker.tag.shown`         | Boolean | false    | Hides/Shows the location tag in the location picker tree view            |
| `encrypt.shared.preferences`        | Boolean | false    | Enable/disables encrypting SharedPreferences                          |
