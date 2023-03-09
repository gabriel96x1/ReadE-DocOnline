# ReadE-DocOnline AKA ElectronicDocs

Is an Android application that utilizes the Near Field Communication (NFC) chip to read electronic documents such as passports and ID cards that comply with the International Civil Aviation Organization (ICAO) standards.

This app comes with features that allow for pre-filling input fields, such as passport number, date of expiry, and date of birth by taking a photograph of the ID where MRZ is visible. Additionally, the app supports returning the photo as a Base64-encoded image.

Available data keys:
- `firstName` - String
- `lastName` - String
- `gender` - String
- `state` - String
- `nationality` - String
- `photo` - Bitmap

## Usage
To use the ReadE-DocOnline/ElectronicDocs app, you will need to have an electronic document that is ICAO compliant and a device capable of use NFC. You can then follow these steps:

- Open the app on your Android device.
- Take a photograph of your e-passport or e-id where MRZ is visible.
- Hold the electronic document against the NFC chip on your device.
- The app will automatically scan the document and display the information contained therein.

## Contacts

Author - Gabriel Rodriguez ([gabriel96x1@gmail.com](mailto:gabriel96x1@gmail.com))

Credits to:

- Anton Tananaev ([anton.tananaev@gmail.com](mailto:anton.tananaev@gmail.com))

## Dependencies

Note that the app includes following third party dependencies:

- JMRTD - [LGPL 3.0 License](https://www.gnu.org/licenses/lgpl-3.0.en.html)
- SCUBA (Smart Card Utils) - [LGPL 3.0 License](https://www.gnu.org/licenses/lgpl-3.0.en.html)
- Spongy Castle - MIT-based [Bouncy Castle Licence](https://www.bouncycastle.org/licence.html)
- JP2 for Android - [BSD 2-Clause License](https://opensource.org/licenses/BSD-2-Clause)
- JNBIS - [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0)

## License

    Apache License, Version 2.0

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
