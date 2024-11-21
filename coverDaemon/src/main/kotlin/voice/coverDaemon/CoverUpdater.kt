package voice.coverDaemon

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import voice.app.scanner.CoverSaver
import voice.common.BookId
import voice.logging.core.Logger
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoverUpdater
@Inject constructor(
  private val coverSaver: CoverSaver,
) {

  /**
   * Updates the book cover by sending a POST request to a service
   * that returns a new book cover.
   *
   * @param bookId The ID of the book to update the cover for.
   */
  suspend fun updateCover(bookId: BookId) {
    try {
      // Fetch the updated cover bitmap from the service
      val bitmap = postForBitmap()

      if (bitmap != null) {
        // Save the new cover using CoverSaver
        coverSaver.save(bookId, bitmap)

        Logger.d("Successfully updated the cover for bookId=$bookId")
      } else {
        Logger.w("Failed to fetch a valid bitmap for bookId=$bookId")
      }
    } catch (e: Exception) {
      Logger.e(e, "Error updating cover for bookId=$bookId")
    }
  }

  /**
   * Makes a POST request to the given URL and returns the response as a Bitmap.
   *
   * @param url The URL of the service.
   * @param requestBody The request body to send with the POST request.
   * @return The bitmap image received in the response, or null if the request fails.
   */
  private suspend fun postForBitmap(): Bitmap? {
    return withContext(Dispatchers.IO) {
      var connection: HttpURLConnection? = null
      try {
        // Create a URL connection
        val endpoint = URL("https://picsum.photos/1024")
        connection = endpoint.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        // Check the response code
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
          // Parse the response into a Bitmap
          connection.inputStream.use { input ->
            return@withContext decodeBitmapFromStream(input)
          }
        } else {
          Logger.e("POST request failed with response code=${connection.responseCode}")
        }
      } catch (e: Exception) {
        Logger.e(e, "Error making POST request")
      } finally {
        connection?.disconnect()
      }
      null
    }
  }

  /**
   * Decodes a bitmap from an InputStream.
   *
   * @param inputStream The input stream containing the image data.
   * @return The decoded Bitmap, or null if decoding fails.
   */
  private fun decodeBitmapFromStream(inputStream: InputStream): Bitmap? {
    return try {
      BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
      Logger.e(e, "Error decoding bitmap from InputStream")
      null
    }
  }
}
