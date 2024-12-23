package pt.noticias.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient

/**
 * This object class provides configuration and access to the Firestore database.
 */
object FirestoreConfig {
    private var firestore: Firestore? = null

    /**
     * Initializes the Firestore connection using the provided credentials file path.
     *
     * @param credentialsFile Path to the Google Cloud credentials file (JSON format) in the resources directory.
     * @throws IllegalArgumentException If the credentials file is not found in the resources directory.
     */
    fun initialize(credentialsFile: String) {
        if (FirebaseApp.getApps().isEmpty()) {
            val inputStream = this::class.java.classLoader.getResourceAsStream(credentialsFile)
                ?: throw IllegalArgumentException("Could not find $credentialsFile in resources")

            val credentials = GoogleCredentials.fromStream(inputStream)
            val options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build()
            FirebaseApp.initializeApp(options)
        }

        firestore = FirestoreClient.getFirestore()
    }

    /**
     * Returns the initialized Firestore instance.
     *
     * @throws IllegalStateException If Firestore is not initialized yet.
     */
    fun getInstance(): Firestore = firestore
        ?: throw IllegalStateException("Firestore must be initialized first")
}