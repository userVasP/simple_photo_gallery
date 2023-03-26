# Simple photo gallery
This is my pet project to learning general libraries of Android and architecture of Android applications. Architecture of project bases on MVVM pattern and 
Android architecture components are used for implementation this pattern.
Aplication can display images from sd card. User can see every image in separate screen.
User can marked image as favorite an it will display in separate screen. Every image can be delated. User can login or logout and load/send image
to/from server which working on the localhost.
## Libraries
In this project the following libraries are used:
* Jetpack Compose / view + DataBinding for creation UI
* Navigation for moving between screens
* Coil for loading image
* Room for working with local db
* Retrofit for networking requests
* Coroutine and Flow or Rx Java for implementation asynchronous programming and working with multithreding
## History and branches
At the beginning, the project used views and DataBinding to create a UI and dagger 2 for implementation DI ([view_branch](https://github.com/userVasP/simple_photo_gallery/tree/view_branch)).
Later, the project was migrated from views to Jetpack Compose and from dagger 2 to Hilt. In [main_branch](https://github.com/userVasP/simple_photo_gallery/tree/main) uses
Kotlin Coroutine and Flow to implement asynchronous programming in [using_rx_java_branc](https://github.com/userVasP/simple_photo_gallery/tree/using_rx_java_branch)
uses RX Java.
## Screens and screenshots
### Screen gallery
This screen displays images saved on sd card
![screen_gallery](https://user-images.githubusercontent.com/119778104/227793813-b297aded-88cf-498a-b0d3-e9faa834273d.png)
### Screen favorite
This screen displays images saved on sd card which marked as favorite by user
![screen_favorite](/assets/images/screen_favorite.png)
### Screen detail
This screen displays choosen by user image
![screen_detail](/assets/images/screen_detail.png)
### Screen login
This screen displays login's form

![screen_login](/assets/images/screen_login.png)
### Screen registration
This screen displays registration's form
![screen_registration](/assets/images/screen_registration.png)
### Screen server
This screen displays user's interaction with server
![screen_server](/assets/images/screen_server.png)
