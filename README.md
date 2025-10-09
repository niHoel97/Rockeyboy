### The RocketBoy app, the source code and all associated literature is part of a group project at the Department of Informatics, University of Oslo.

# RocketBoy

## Group members:

    Anniken Ditlefsen
    Nikolai Hoel
    Nils Josef Löfgren
    Felix Berntsen
    Aleksander Rangøy Bless
    August Birkeland Brøyn

# How to run:

    1. Clone the Github repo
    2. Open and build the app in Android Studio
    3. Run the app
    4. NB! The app uses atmospheric wind data from an API
       managed by the group. If the API is down or is
       otherwise not working, please contact:
       augustbb@ifi.uio.no

# Demo
https://github.uio.no/IN2000-V25/team-47/assets/10917/d3f8d2f1-1671-4bbb-9440-33fa09633110

# Where to find more documentation:

## ARCHITECTURE.MD

    ARCHITECTURE.MD contains useful information for further
    development.
    It describes:

    - the apps architecture (i.e. class diagrams, flow charts
      architectural sketches, etc.)
    - use of central programming principals (which should be continued in
      further development)
    - general descriptions intended for further operation, maintainance
      and development of the app

## README.MD:

    README.MD contains useful information for first time users.
    This includes:

    - How to run the app
    - Where to find more documentation
    - Descriptions of the apps dependencies
    - List of libraries used

## SOURCE CODE:

    The source code is found in the app directory. Analysing the code can
    give a good overview of the implementation of the apps functionality
    as the components that make up the app is described within the code,
    and as the code is commented.

# Dependencies:

## Jetpack Compose and Material3:

    Jetpack Compose and Material3 for the UI design. Kotlin,
    Serialization, Ktor and Retrofit to fetch and parse the JSON files
    from the APIs (these are well documented in the course).

## Lifecycle and ViewModel:

    Lifecycle and ViewModel to structure our data cleanly with clear
    layering in accordance with MVVM principles. Dependencies for logging
    during code debugging, and dependencies for testing the code.

# Libraries:

## RoomDB:

    About RoomDB: Room Database is an Android library that makes it
    easier to work with SQLite databases. The database is stored locally
    on the Android system to save user information and any app data we
    need to persist between app launches.
    Link → https://developer.android.com/training/data-storage/room

## Mapbox:

    About Mapbox: Mapbox is a platform for mapping services and
    geolocation that offers a range of tools and APIs for building
    map-based applications and geographic solutions. We use their SDK to
    implement map features in the app, including allowing users to save
    their location.
    Link → https://docs.mapbox.com/android/maps/guides/
