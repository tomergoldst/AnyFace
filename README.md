# AnyFace
Detect photos with faces

When clicking 'run detection' all photos will processed on the background and you will be notified when done

Check logs to see ongoing photos processing progress 

## Instructions
1. Connect to Firebase

    This sample app uses Firebase ML Kit and thus you have to connect this project to your Firebase account in order to run it.
    If you have your `google-services.json` file simply copy it to the 'app' folder

    See https://firebase.google.com/docs/ml-kit/android/detect-faces for more information and help on how to add firebase

2. Create a new folder named `anyface` in your's downloads folder and put the images there 


## Tech stack
* Kotlin
* MVVM
* Repository
* Android X
* Android Architecture Components
    * Room
    * ViewModel
    * LiveData
    * WorkManager
* Glide
* ML Kit: Face Detection Moudle for Firebase

### License
```
Copyright 2019 Tomer Goldstein

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


