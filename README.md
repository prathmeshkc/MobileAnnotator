# MobileAnnotator
The MobileAnnotator app is designed to simplify the task of activity recognition by allowing users to verbally label their activities as they go about their daily lives. 

#Working

The app uses a beep to prompt the user to speak and then listens for a short period of time to capture audio input, let’s say 30 seconds. This raw audio is sent to the Google Cloud Platform’s Speech-to-Text API using GCP’s client libraries. The API then sends back the text extracted from the audio. After that, the app processes the text for keywords, checks those keywords against a set of acceptable words, and saves the time of the prompt, all the words recognized, and the known keywords recognized, as well as the path to the raw audio clip in a CSV file.

#Usage
With this app, researchers no longer have to rely on multiple-choice surveys, which can be burdensome and disruptive at times. Instead, they can collect data in a more natural and efficient way, which can help improve the accuracy of their models and insights into human behavior.

#Possible Extensions

1. Bluetooth Headset Support


