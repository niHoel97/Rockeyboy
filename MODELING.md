# **MODELING**
This document is created for developers who will continue working on the app, get familiar with it, and contribute to its development. It shows the app's architecture through diagrams and descriptions. This document includes the following diagrams:
- Class diagram
- Sequence diagram for the introScreens
- Sequence diagram for changing coordinates in settings
- Flowchart for navigation through the app
- Architectural sketch
- UseCase diagram for showing weatherdata and updating coordinates

## **CLASSDIAGRAM**
```mermaid 
    classDiagram 
        class MainActivity{
            httpClient : HttpClientProvider
    
            weatherViewModel : WeatherViewModel
            weatherViewModelFactory : weatherViewModelFactory
    
            locationDataSource : LocationforecastDataSource
            locationRepository : LocationforecastRepository 
    
            gribdataSource : GribDataSource
            gribRepository : GribRepository 
    
            factory : WeatherViewModelFactory
            calendarFactory : CalendarViewModelFactory
            calendarViewModel : CalendarViewModelFactory = ViewModelProvider
    
            IntroScreens()
            MainScreen()
        }
    
        class LocationforecastDataSource{
            getWeatherForecast(url: String) WeatherResponse?
        }
    
         class LocationforecastRepository{
            url : String
            fetchWeather(lat: Double, lon: Double, alt: Double) WeatherResponse?
        }
        
        class HttpClientProvider {
            client : HttpClient
        }
    
        class WeatherViewModel {
           _weatherState : WeatherRespone?
           weatherResponse : _weatherState
           _gribResponseState : GribResponse?
           gribResponseState : _gribResponseState
           _errorMessage : String?
           errorMessage : _errorMessage
           _instantAirPressure : String?
           instantAirPressure : _instantAirPressure
    
           fetchWeatherAndGribData()
        }
    
        class WeatherViewModelFactory {
            OverrideFunCreate() WeatherViewModel
        }
    
    %% Calendar
        class CalendarViewModelFactory {
            OverrideFunCreate() CalendarViewModel
        }
    
        class CalendarViewModel {
            _selectedDate : LocalDate.now
            selectedDate : _selectedDate
            _displayMonth : LocalDate.now.withDayOfMonth
            displayedMonth : _displayedMonth
            _selectedForecasts : List<TimeSeries>(epmtylist)
            selectedForecasts : _selectedForecasts
            _coordiantes : CoordinatesData
            coordinates _  _Coordinates
            latestWeather: WeatherResponse? = null 
    
            selectDate(date: LocalDate)
            changeMonth(by: Long)
            fetchTemperature()
            ObserveWeather(weatherFlow : weatherRespone?)
            UpdateDatesWithDate()
            UpdateForecastsForDate()
        }
    
    %%Locationforecast
    
        class WeatherResponse {
            Geometry ... Details
        }
    
    %%Grib
    
        class GribDataSource {
            gribFileChache : LinkedHashMap
            result : Gribresponse
        }
    
        class GribRepository {
            fetchGrib() GribResponse
        }
    
        class GribResponse {
            Domain ... Unit
        }
    
        class GribData{
            isobar : Int
            moh : Int
            temp : Double
            windFromDirection : Double 
            windSpeed : Double 
        }
    
        class WindShearData{
            lowIsobar : Int 
            upperIsobar : Int 
            shearMagnitude : Double
            lowerWindDirection : Double 
            lowerWindSpeed : Double 
            upperWindDirection : Double
            upperWindSpeed : Double 
        }
    
        class GribTab {
            <<Enum>>
    
        }
    
    %% Home 
        class BottomNavItem{
            route : String
            title : String 
            icon : Int 
        }
    
    %% Settings
        class SettingsViewModel{
            _rocketDataList : RocketData
            rocketDataList : _rocketDataList
            _weatherSettings : WeatherSettingsData? = null
            weatherSettings : _weatherSettings
    
            insertRocketData()
            fetchRocketData()
            fetchWeatherSettings()
            resetWeatherSettingsToDeafult()
            defaultWeatherSettings()
            updateWeatherSettings(newSettings : WeatherSettingsData)
        }
    
        class SettingsViewModelFactory{
            overrideFunCreate() SettingsViewModel
        }
    
        class WeatherSettingsRepository {
            insertWeatherSettings(Settings : WeatherSettingData)
            getWeatherSettings() WeatherSettingsdata? 
        }
    
        class CoordinatesData{
            lat : Double 
            lon : Double 
            alt : Int
        }
    
    %% Database
        class WeatherSettingsDao {
            <<Interface>>
            getLatest() WeatherSettingsData? 
        }
    
        class WeatherSettingsData {
            <<DataClass>>
            tempMin: Double
            tempMax: Double
            humidityMin: Double
            humidityMax: Double
            windMin: Double
            windMax: Double
            precipitationMin: Double
            precipitationMax: Double
            fogmin: Double
            fogMax: Double
            dewMin: Double
            dewMax: Double
            cloudLowMin: Double
            cloudLowMax: Double
            cloudMediumMin: Double
            cloudMediumMax: Double
            cloudHighMin: Double
            cloudHighMax: Double
            shearMin: Double
            shearMax: Double
        }
    
    
    %% MAIN
        MainActivity "1" --> "1" WeatherViewModelFactory
        WeatherViewModelFactory "1" --> "1" WeatherViewModel
        WeatherViewModel "1" --> "1" LocationforecastRepository
        WeatherViewModel "1" --> "1" GribRepository
        MainActivity "1" --> "1" SettingsViewModelFactory
        SettingsViewModelFactory "1" --> "1" SettingsViewModel
        SettingsViewModel "1" --> "1" WeatherSettingsRepository 
        WeatherSettingsRepository "1" --> "1" WeatherSettingsDao
        WeatherSettingsDao "1" --> "1" WeatherSettingsData
        MainActivity "1" --> "1" CalendarViewModelFactory
    
        MainActivity "1" --> "1" BottomNavItem
    
        LocationforecastRepository "1" --> "1" LocationforecastDataSource
        LocationforecastDataSource "1" --> "1" HttpClientProvider
    
        GribRepository "1" --> "1" GribDataSource
        GribDataSource "1" --> "1" HttpClientProvider
        HttpClientProvider "1" --> "1..*" HttpClient
        LocationforecastDataSource "1" --> "1" WeatherResponse
        GribDataSource "1" --> "1" GribResponse
        MainActivity "1" --> "1" GribTab
    
    %% LOCATIONFORECAST
        WeatherResponse "1" --> "1" Geometry 
        WeatherResponse "1" --> "1" Properties
    
    %% GRIB
        GribResponse "1" --> "1" Domain 
        GribResponse "1" --> "1" Parameters
        GribResponse "1" --> "1" Ranges
        GribDataSource "1" --> "1" GribData
        GribDataSource "1" --> "1" WindShearData
    
    %% Settings
        SettingsViewModel "1" --> "1" CoordinatesData
    
    %% Calendar
        CalendarViewModelFactory "1" --> "1" CalendarViewModel
```

### **CLASS DIAGRAM DESCRIPTION**
- #### MainActivity
  - This diagram shows a simplified version of the class structure of the app. It contains a MainActivity that creates objects of DataSources, Repositories, ViewModelFactories, ViewModels, and the database.
- #### DataSources
  - Generates a response from the API using HttpClient.
- #### Repositories
  - Contains functions for fetching data from data sources.
- #### ViewModelFactories
  - Allows us to generate ViewModels that take arguments as parameters.
- #### ViewModels
  - Used to handle logic and some data storage.
- #### DataClasses (most data classes are excluded from this diagram due to redundancy)
  - Used to hold data when parsing from the API.
- #### Database
  - Used for storing data such as weather settings

## **EXTENSION OF CLASS DIAGRAM (The excluded data-classes)**
```mermaid
classDiagram

%% Weatherforecast


    class Geometry {
        type : String 
        coordiantes : List<Double>    
    }

    class Properties {
        meta : Meta
        timeseres : List<Timeseries>
    }

    class Meta {
        updatedAt : String
        units = Units
    }

    class Timeseries {
        time : String
        data : Data
    }

    class Data {
        instant : Instant? = null
        next12Hours : Next12Hours? = null
        next1Hours = Next1Hours? = null
        next6Hours = Next6Hours? = null
    }

    class Instant {
        details : Details
    }

    class Next12Hours {
        summary : Summary
        details : Details
    }

    class Next1Hours {
        summary : Summary 
        details : Details 
    }

    class Next6Hours {
        summary : Summary
        details : Details 
    }

    class Units {
        airPressureAtSeaLevel : String 
        airTemperature : String 
        airtemperatureMax : String 
        airTemperatureMin : String
        airTemperaturePercentile90 : String 
        airTemperaturePercentile10 : String 
        cloudAreaFraction : String 
        cloudAreaFractionHigh : String 
        cloudAreaFractionLow : String
        cloudAreaFractionMedium : String
        dewPointTemperature : String 
        fogAreaFraaction : String 
        precipitationAmmount : String
        precipitationAmmountMax : String
        precipitationAmmountMin : String
        probabilityOfPrecipitation : String
        probabilityOfThunder : String
        relativeHumidity : String 
        ultravioletIndexClearSky : Int 
        windFromDirection : String
        windSpeed : String 
        windSpeedOfGust : String 
        windSpeedPercentile10 : String 
        windSpeedPercentile90 : String 
    }

    class Summary {
        symbolCode : String 
    }

    class Details {
        airPressureAtSeaLevel : Double? = null
        airTemperature : Double? = null
        airTemperatureMax : Double? = null
        airTemperatureMin : Double? = null
        airTemperaturePercentile90 : Double? = null
        airTemperaturePercentile10 : Double? = null
        cloudAreaFraction : Double? = null
        cloudAreaFractionHigh : Double? = null
        cloudAreaFractionLow : Double? = null
        cloudAreaFractionMedium : Double? = null
        dewPointTemperature : Double? = null
        fogAreaFraction : Double? = null
        precipitationAmount : Double? = null
        precipitationAmountMax: Double? = null
        precipitationAmountMin: Double? = null
        probabilityOfPrecipitation: Double? = null
        probabilityOfThunder: Double? = null
        relativeHumidity : Double? = null
        ultravioletIndexClearSky : Double? = null
        windFromDirection : Double? = null  
        windSpeed : Double? = null
        windSpeedOfGust : Double? = null
        windSpeedPercentile10 : Double? = null
        windSpeedPercentile90 : Double? = null
    }

%% grib

class Domain {
        %% Data class
        type : String
        domainType : String
        axes : Axes
        referencing : List<Referencing>
    }

    class Axes {
        %% Data class
        x : X
        y : Y
        z : Z
        t : T
    }

    class X {
        %% Data class
        values : List<Double>
    }

    class Y {
        %% Data class
        values : List<Double>
    }

    class Z {
        %% Data class
        values : List<Int>
    }

    class T {
        %% Data class
        values : List<String>
    }

    class Referencing {
        %% Data class
        coordinates : List<String>
        system : System
    }

     class System {
        %% Data class
        type : String
        id : String
        cs : Cs? = null
        calendar = String? = null
    }

    class Cs {
        csAxes : List<CsAxes>
    }

    class CsAxes {
        name : Name
        direction : String 
        unit : UnitCs
    }

    class Name {
        en : String
    }

    class UnitCs {
        symbol : String
    }

     class Parameters {
        %% Data class
        temperature : ParamsTemperature
        wind_from_direction : ParamsWindFromDirection
        wind_speed : ParamsWindSpeed
    }

    class ParamsTemperature {
        type : String
        id : String
        label : Label
        observedProperty : ObservedProperty
        unit : Unit
    }

    class Temperature {
        type : String
        dataType : String
        axisName : List<String>
        shape : List<Int>
        values : List<Double> 
    }

    class ParamsWindFromDirection {
        type : String 
        id : Sting 
        label : Label 
        observedProperty : ObservedProperty
        unit : Unit
    }

    class WindFromDirection {
        type : String 
        dataType : Sting 
        axisNames : List<String>
        shape : List<Int>
        values : List<Double>
    }

    class ParamsWindSpeed {
        type : String
        id : String 
        label : Label 
        ovservedProperty : ObservedProperty 
        unit : Unit
    }

    class WindSpeed {
        type : String 
        dataType : String
        axisNames : List<String>
        shape : List<Int>
        values : List<Double>
    }

    class Ranges {
        temperature : Temperature 
        wind_from_direction : WindFromDirection
        wind_speed : WindsSpeed
    }

    class Label {
        en : String
    }

    class ObservedProperty {
        id : String
        label : Label 
    }

    class Unit {
        id : String
        label : Label
        symbol : String
    }

%% WEATHER
    Domain --> Axes
    Domain --> Referencing
    
    Axes --> X
    Axes --> Y
    Axes --> Z
    Axes --> T

    Referencing --> System 

    System --> Cs

    Cs --> CsAxes

    CsAxes --> Name
    CsAxes --> UnitCs

    Parameters --> ParamsTemperature
    Parameters --> ParamsWindFromDirection
    Parameters --> ParamsWindSpeed

    ParamsTemperature --> Label 
    ParamsTemperature --> ObservedProperty
    ParamsTemperature --> Unit

    ParamsWindSpeed --> Label
    ParamsWindSpeed --> ObservedProperty
    ParamsWindSpeed --> Unit 

    Ranges --> Temperature 
    Ranges --> WindFromDirection
    Ranges --> WindSpeed 

    ObservedProperty --> Label

    Unit --> Label 

%% GRIB
    Properties --> Meta
    Properties --> Timeseries

    Meta --> Units

    Timeseries --> Data
    
    Data --> Instant
    Data --> Next12Hours 
    Data --> Next1Hours
    Data --> Next6Hours 

    Instant --> Details 

    Next12Hours --> Summary 
    Next12Hours --> Details
    
    Next1Hours --> Summary
    Next1Hours --> Details
    
    Next6Hours --> Summary
    Next6Hours --> Details
```
### **EXTENDED CLASS DIAGRAM DESCRIPTION**
All the data classes were removed from the class diagram due to redundancy. The diagram is included here to help future developers working on this project understand how the classes work together.

## **SEQUENSE DIAGRAMS**
We made sequence diagrams for two cases:
- IntroScreens
- SettingsScreen

### **INTROSCREENS SEQUENCE DIAGRAM**
```mermaid
    sequenceDiagram
        actor User
        participant App
        participant FirstIntroScreen
        participant SecondIntroScreen
        participant ThirdIntroScreen
        participant FinalIntroScreen
        participant MainScreen

        User->>App: "User opens the app for the first time"
        App->>FirstIntroScreen: "App displays the first introScreen"

        User->>FirstIntroScreen: "User navigates to the next screen using a button"
        App->>SecondIntroScreen: "App displays the second introScreen"

        SecondIntroScreen->>User: "Asks for coordinates and choice of submit/default"

        alt "User enters valid coordinates and presses submit"
            User->>App: "Sends valid coordinates"
            App->>ThirdIntroScreen: "App displays the third introScreen"

        else "User selects default values and presses submit"
            User->>App: "Selects default and presses submit"
            App->>ThirdIntroScreen: "App displays the third introScreen"

        else "User enters invalid coordinates"
            User->>App: "Sends invalid coordinates"
            App->>User: "Error message: invalid coordinates"

            alt "User manually enters coordinates"
                User->>App: "Sends valid coordinates"
                App->>ThirdIntroScreen: "App displays the third introScreen"

            else "User presses default and submit"
                User->>App: "Selects default and presses submit"
                App->>ThirdIntroScreen: "App displays the third introScreen"
            end
        end

        ThirdIntroScreen->>User: "Edit weather preferences or press sibmit and continue"

        alt "User presses submit and continue"
            User->>App: "Presses submit and continue"
            App->>FinalIntroScreen: "App displays the final introScreen"

        else "User edits preferences and presses submit and continue"
            User->>App: "Sends weather preferences"
            App->>FinalIntroScreen: "App displays the final introScreen"
        end

        FinalIntroScreen->>User: "Asks for navigation to MainScreen"
        
        User->>App: "Presses the navigate button"

        App->>User: "Displays rocket animation"

        App->>MainScreen: "Displays the mainScreen"
```

### **INTROSCREENS SEQUENCE DIAGRAM DESCRIPTION**
This diagram describes the scenario when a user opens the app for the first time and goes through the **IntroScreens**. It includes various alternative routes for completing the intro, such as entering coordinates manually or using the default button before submitting. This diagram was included because it covers a small part of the app, but involves many screens and several decisions to be made.

## **SETTINGSSCREEN SEQUENCE DIAGRAM**
```mermaid 
sequenceDiagram
    actor User
    participant App
    participant SettingsScreen
    participant WeatherViewModel
    participant WeatherScreen
    participant CoordinatesData

    User->>App: Open the app
    App->>SettingsScreen: Show SettingsScreen
    User->>SettingsScreen: Enter coordinates and press "Submit"
    SettingsScreen->>WeatherViewModel: submitChanges() with coordinates
    WeatherViewModel->>CoordinatesData: Save coordinates
    WeatherViewModel->>WeatherViewModel: updateCoordinates() 
    WeatherViewModel->>WeatherViewModel: fetchWeatherAndGribData()
    WeatherViewModel->>WeatherScreen: Update weather and grib data
    WeatherScreen->>User: Show updated weather forecast and grib data
```
### **SETTINGSSCREEN SEQUENCE DIAGRAM DESCRIPTION**
This diagram shows the case when a user changes coordinates in the SettingsScreen. This diagram was included because it is informative and shows how **WeatherViewModel** saves the coordinates as **CoordinatesData** within itself. This is due to complications caused by some spaghetti code.


## **FLOWCHART**
```mermaid

flowchart TD
    %% MainScreen
    A[Start]
    B[WeatherScreen]
    C[WeatherData]
    D[LocationforecastDataSource]
    E[LocationforecastRepository]
    F[WeatherViewModel]

    %% WeatherScreen
    G[WeatherScreen]
    H[GribData]
    I[GribDataSource]
    J[GribRepository]

    %% SettingsScreen
    K[SettingsScreen]
    L[SettingsViewModelFactory]
    M[SettingsViewModel]
    N[CoordinatesData]
    O[WeatherSettings]
    P[AppDatabase]

    %% DataBase
    K --"Navigate to weatherSettings using singleChoiceSegmentButton"-->O
    O --"Updating and Storing updates"-->P

    %% MainScreen
    A --"Open app"--> B
    C --> D
    D --> E
    E --> F
    F --"Displays WeatherData"--> B
    
    %% MainScreen to WeatherScreen
    B --"Using navigationBar"--> G
    H --> I
    I --> J
    J --> F
    F --"Displays GribData"--> G

    %% MainScreen to SettingsScreen 
    B --"Using navigationBar"--> K
    L --> M
    M --> K
    K --"Updating coordinates"-->N
    N --"Pressing submitChanges"-->F

    %% WeatherScreen til MainScreen
    G --"Using navigationBar"--> B

    %% SettingsScreen to MainScreen 
    K --"Using navigationBar"--> B

    %% SettingsScreen to WeatherScreen 
    K --"Using navigationBar"-->G

    %% WeatherScreen to settingsScreen 
    G --"Using navigationBar"-->K
```

### **FLOWCHART DESCRIPTION**
The flowchart shows how the user can navigate through the app. This diagram was included because it shows every navigation move through the app and displays it more simply than a sequence diagram. The navigation is controlled using a bottom navigation bar.

## **ARCHITECTURAL SKETCH**
<img width="627" alt="Screenshot 2025-05-16 at 11 21 52" src="https://github.uio.no/IN2000-V25/team-47/assets/11854/1551ac39-1e27-4af0-89c1-5cd6a18a9cd0">

### **ARCHITECTURAL SKETCH DESCRIPTION**
The architectural sketch shows the combined structure, including the data, model, and UI. It is helpful to get a visual representation of how the three parts are connected in the code.

## **USECASE DIAGRAM**
<img width="1072" alt="Screenshot 2025-05-16 at 10 55 31" src="https://github.uio.no/IN2000-V25/team-47/assets/11854/d66d26fa-63bb-45a4-b37c-a22fbfcd5ad1">

### **USECASE DESCRIPTION**
This diagram show how the user request weatherdata and recieves either error message or valid weatherdata, including weather rating. User also has the opportunity to change coordinates.

