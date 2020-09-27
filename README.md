
<div align="center">
  <img src="https://github.com/Ryukyo/team-manager-app/blob/master/Screenshots/Login.PNG?raw=true" alt="login-screen" width="225" height="475"/>
</div>

<!-- ![Homepage](/Screenshots/Login.PNG?raw=true "TeamD_Login") -->

# TeamD - Keeping Track of Team Tasks

This was created during my time as a student at Code Chrysalis.

The project is built with Kotlin in Android studio. It uses the following Firebase sercices:

- Authentication
- Storage
- Cloud Firestore

## Features

Further screenshots can be found in the Screenshots folder. The following features have been added at the time of writing:

- User authentication
- User profile
- Create multiple boards and invite users to share the board with
- Create lists and tasks that can be assigned to users with access to the board
- Tasks consist of name and an optional due date (datepicker) and color label
- Drag and drop items within a list to change their order

## Installation

Dependencies are listed in the build.gradle files

To run the application, please use the emulator of Android Studio.
A virtual device running Android 10 (Q) is recommended.

### Database Setup

All database related functionality is handled in:
app/src/main/java/com/example/teamd/firebase/FirestoreClass.kt

Since Firebase was used for handling database, storage and authentication, those features can only be handled wihtin the project in Firebase.
For the settings of Firebase, a google-services.json file is necessary. This file can be generated in your Firebase project

## Tests

Not yet implemented

## Planned Features and Known Bugs

see Issues

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
