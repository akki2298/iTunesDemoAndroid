package com.encora.topsongsapp.data.model


data class Feed(
    val entries: List<FeedEntry> = emptyList()
)

data class FeedEntry(
    var id: String? = null,
    var title: String? = null,
    var name: String? = null,
    var artist: Artist? = null,
    var links: MutableList<PlaybackLink> = mutableListOf(),
    var category: Category? = null,
    var price: Price? = null,
    var images: MutableList<PlaybackImage> = mutableListOf(),
    var collection: Collection? = null,
    var rights: String? = null,
    var releaseDate: String? = null,
    var duration: Long? = null
)

data class Category(
    val term: String? = null,
    val scheme: String? = null,
    val label: String? = null
)

data class Collection(
    var name: String? = null,
    var link: String? = null,
    var contentType: ContentType? = null
)

data class Link(
    val rel: String? = null,
    val type: String? = null,
    val href: String? = null
)

data class ContentType(
    var term: String? = null,
    var label: String? = null
)

data class Price(
    val amount: String? = null,
    val currency: String? = null
)

data class Artist(
    val href: String? = null,
    var name: String? = null
)

data class PlaybackLink(
    val url: String? = null,
    val type: String? = null,
    val duration: Int? = null
)

data class PlaybackImage(
    val height: Int? = null,
    val url: String? = null
)

