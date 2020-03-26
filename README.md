# Forex
Forex is an application that updates in real-time the exchange rates of
different currencies based on the Euro

## Installation
The debug version can easily be built for installation on device(or emulator) or for an .apk which can then be installed manually 
### On Emulator or Device
From the terminal, run
```
gradlew installDebug
```
### APK
From the terminal, run
```
gradlew assembleDebug
```
This creates an APK in 
```
Revolut/app/build/outputs/apk/
```

## Architecture
Clean architecture was used to architect the application. The main
reasons for this choice was for future maintenance, as well as
abstraction (the different layers are abstracted away from each other).
There are three main layers namely:
### data
Which contains the models to hold data, remote source with api for
network calls to retrieve real-time data, cache source which is the data
source of truth and repository to pass the data onto the domain layer
### domain
Which contains usecase specific business logic for constantly updating
the currencies with their rates, moving currency to top of list on click
and updating the corresponding rates when the top rate changes . The
domain exposes the business logic to the ui or presentation layer
through interface abstractions
### ui
This is the layer for the data visualization component of the
application. The presentation layer was setup with MVVM where the views
have little or no view logic, the viewmodels hold livedata for the views
to observe, manage rxjava disposables and contain view logic without
referencing view components and are testable. The View component used to
visualize the data is a ConstraintLayout with a recyclerView to scroll
through the data.

## Caveats
As long as the rate amount at the top of the list is being changed by
the user, the real-time update is suspended and reinstated afterwards
