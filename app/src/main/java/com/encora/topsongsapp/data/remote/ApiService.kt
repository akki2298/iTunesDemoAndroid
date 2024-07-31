package com.encora.topsongsapp.data.remote

import android.util.Xml
import com.encora.topsongsapp.data.model.Artist
import com.encora.topsongsapp.data.model.Category
import com.encora.topsongsapp.data.model.Collection
import com.encora.topsongsapp.data.model.ContentType
import com.encora.topsongsapp.data.model.Feed
import com.encora.topsongsapp.data.model.FeedEntry
import com.encora.topsongsapp.data.model.PlaybackImage
import com.encora.topsongsapp.data.model.PlaybackLink
import com.encora.topsongsapp.data.model.Price
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class ApiService @Inject constructor() {

    suspend fun getSongsFromApi(): Feed {
        val urlConnection = withContext(Dispatchers.IO) {
            URL(BASE_URL + END_POINTS).openConnection()
        } as HttpURLConnection

        try {
            urlConnection.inputStream.use { inputStream ->
                return parseXml(inputStream)
            }
        } finally {
            urlConnection.disconnect()
        }
    }

    private fun parseXml(inputStream: InputStream): Feed {
        val parser = Xml.newPullParser()
        parser.setInput(inputStream, null)

        var eventType = parser.eventType
        var currentFeed: Feed? = null
        var currentEntry: FeedEntry? = null
        val entries = mutableListOf<FeedEntry>()
        var currentArtist: Artist? = null
        var currentPrice: Price? = null
        var collection: Collection? = null
        var currentCategory: Category? = null
        var currentContentType: ContentType? = null
        var currentImage: PlaybackImage? = null
        var currentDuration: Long? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        TAG_ENTRY -> currentEntry = FeedEntry()
                        TAG_ID -> currentEntry?.id = parser.nextText()
                        TAG_TITLE -> currentEntry?.title = parser.nextText()
                        TAG_ARTIST -> {
                            currentArtist = Artist()
                            currentArtist?.name = parser.nextText()
                            currentEntry?.artist = currentArtist
                        }

                        TAG_LINK -> {
                            val url = parser.getAttributeValue(null, ATTRIBUTE_HREF)
                            val type = parser.getAttributeValue(null, ATTRIBUTE_TYPE)
                            val duration = parser.getAttributeValue(null, ATTRIBUTE_DURATION)?.toIntOrNull()
                            val link = PlaybackLink(url, type, duration)
                            currentEntry?.links =
                                (currentEntry?.links ?: mutableListOf()).apply { add(link) }
                        }

                        TAG_CATEGORY -> {
                            val term = parser.getAttributeValue(null, ATTRIBUTE_TERM)
                            val scheme = parser.getAttributeValue(null, ATTRIBUTE_SCHEME)
                            val label = parser.getAttributeValue(null, ATTRIBUTE_LABEL)
                            currentCategory = Category(term, scheme, label)
                            currentEntry?.category = currentCategory
                        }

                        TAG_COLLECTION -> {
                            collection = Collection()
                        }

                        TAG_CONTENT_TYPE -> {
                            currentContentType = ContentType(
                                term = parser.getAttributeValue(null, ATTRIBUTE_TERM),
                                label = parser.getAttributeValue(null, ATTRIBUTE_LABEL)
                            )
                            collection?.contentType = currentContentType
                        }

                        TAG_PRICE -> {
                            val amount = parser.getAttributeValue(null, ATTRIBUTE_AMOUNT)
                            val currency = parser.getAttributeValue(null, ATTRIBUTE_CURRENCY)
                            currentPrice = Price(amount, currency)
                            currentEntry?.price = currentPrice
                        }

                        TAG_IMAGE -> {
                            val height = parser.getAttributeValue(null, ATTRIBUTE_HEIGHT)?.toIntOrNull()
                            val url = parser.nextText()
                            currentImage = PlaybackImage(height, url)
                            currentEntry?.images = (currentEntry?.images
                                ?: mutableListOf()).apply { add(currentImage) }
                        }

                        TAG_NAME -> {
                            if (collection != null) {
                                collection.name = parser.nextText()
                            } else {
                                currentEntry?.name = parser.nextText()
                            }
                        }

                        TAG_RIGHTS -> currentEntry?.rights = parser.nextText()
                        TAG_RELEASE_DATE -> {
                            val label = parser.getAttributeValue(null, ATTRIBUTE_LABEL)
                            currentEntry?.releaseDate = label
                        }

                        TAG_DURATION -> {
                            currentDuration = parser.nextText()?.toLongOrNull()
                            currentEntry?.duration = currentDuration
                        }
                    }
                }

                XmlPullParser.END_TAG -> {
                    when (parser.name) {
                        TAG_ENTRY -> currentEntry?.let { entries.add(it) }
                        TAG_COLLECTION -> {
                            currentEntry?.collection = collection
                            collection = null
                        }
                    }
                }
            }
            eventType = parser.next()
        }

        currentFeed = Feed(entries)
        return currentFeed
    }
    companion object {
        const val BASE_URL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/"
        const val END_POINTS = "topsongs/limit=20/xml"
        const val TAG_ENTRY = "entry"
        const val TAG_ID = "id"
        const val TAG_TITLE = "title"
        const val TAG_ARTIST = "artist"
        const val TAG_LINK = "link"
        const val TAG_CATEGORY = "category"
        const val TAG_COLLECTION = "collection"
        const val TAG_CONTENT_TYPE = "contentType"
        const val TAG_PRICE = "price"
        const val TAG_IMAGE = "image"
        const val TAG_NAME = "name"
        const val TAG_RIGHTS = "rights"
        const val TAG_RELEASE_DATE = "releaseDate"
        const val TAG_DURATION = "duration"

        const val ATTRIBUTE_HREF = "href"
        const val ATTRIBUTE_TYPE = "type"
        const val ATTRIBUTE_DURATION = "duration"
        const val ATTRIBUTE_TERM = "term"
        const val ATTRIBUTE_SCHEME = "scheme"
        const val ATTRIBUTE_LABEL = "label"
        const val ATTRIBUTE_AMOUNT = "amount"
        const val ATTRIBUTE_CURRENCY = "currency"
        const val ATTRIBUTE_HEIGHT = "height"
    }
}