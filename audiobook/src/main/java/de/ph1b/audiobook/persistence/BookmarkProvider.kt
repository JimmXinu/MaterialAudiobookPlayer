package de.ph1b.audiobook.persistence

import de.ph1b.audiobook.Book
import de.ph1b.audiobook.Bookmark
import de.ph1b.audiobook.persistence.internals.SqlBookmarkStore
import io.reactivex.Observable
import v
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides access to bookmarks.
 *
 * @author: Paul Woitaschek
 */
@Singleton
class BookmarkProvider
@Inject constructor(private val store: SqlBookmarkStore) {

  fun deleteBookmark(id: Long) = store.deleteBookmark(id)

  fun addBookmark(bookmark: Bookmark) = store.addBookmark(bookmark)

  fun addBookmarkAtBookPosition(book: Book, title: String) {
    val addedBookmark = Bookmark(book.currentChapter().file, title, book.time)
    addBookmark(addedBookmark)
    v { "Added bookmark=$addedBookmark" }
  }

  fun get_bookmarks(book: Book) = store.bookmarks(book)

  fun bookmarks(book: Book): Observable<List<Bookmark>> = Observable.fromCallable { store.bookmarks(book) }
}
