package de.ph1b.audiobook.features.bookmarks

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.PopupMenu
import de.ph1b.audiobook.Bookmark
import de.ph1b.audiobook.Chapter
import de.ph1b.audiobook.R
import de.ph1b.audiobook.databinding.BookmarkBinding
import de.ph1b.audiobook.features.bookmarks.dialogs.AddBookmarkDialog
import de.ph1b.audiobook.features.bookmarks.dialogs.DeleteBookmarkDialog
import de.ph1b.audiobook.features.bookmarks.dialogs.EditBookmarkDialog
import de.ph1b.audiobook.features.bookmarks.list.BookmarkAdapter
import de.ph1b.audiobook.features.bookmarks.list.BookmarkClickListener
import de.ph1b.audiobook.injection.App
import de.ph1b.audiobook.mvp.MvpController

/**
 * Dialog for creating a bookmark
 *
 * @author Paul Woitaschek
 */
class BookmarkController(args: Bundle) : MvpController<BookmarkView, BookmarkPresenter, BookmarkBinding>(args), BookmarkView, BookmarkClickListener, AddBookmarkDialog.Callback, DeleteBookmarkDialog.Callback, EditBookmarkDialog.Callback {

  private val bookId = args.getLong(NI_BOOK_ID)
  private lateinit var adapter: BookmarkAdapter

  override val layoutRes = R.layout.bookmark
  override fun createPresenter() = App.component.bookmarkPresenter.apply {
    bookId = this@BookmarkController.bookId
  }

  override fun render(bookmarks: List<Bookmark>) {
    adapter.newData(bookmarks)
  }

  override fun showBookmarkAdded(bookmark: Bookmark) {
    val index = adapter.indexOf(bookmark)
    binding.recycler.smoothScrollToPosition(index)
    Snackbar.make(binding.root, R.string.bookmark_added, Snackbar.LENGTH_SHORT)
        .show()
  }

  override fun onDeleteBookmarkConfirmed(id: Long) {
    presenter.deleteBookmark(id)
  }

  override fun onBookmarkClicked(bookmark: Bookmark) {
    presenter.selectBookmark(bookmark.id)
    router.popController(this)
  }

  override fun onEditBookmark(id: Long, title: String) {
    presenter.editBookmark(id, title)
  }

  override fun onBookmarkNameChosen(name: String) {
    presenter.addBookmark(name)
  }

  override fun finish() {
    router.popController(this)
  }

  override fun onBindingCreated(binding: BookmarkBinding) {
    setupToolbar()
    setupList()

    binding.addBookmarkFab.setOnClickListener {
      showAddBookmarkDialog()
    }
  }

  override fun init(chapters: List<Chapter>) {
    adapter = BookmarkAdapter(chapters, this)
    binding.recycler.adapter = adapter
  }

  private fun setupToolbar() {
    binding.toolbar.setTitle(R.string.bookmark)
    binding.toolbar.setNavigationIcon(R.drawable.close)
    binding.toolbar.setNavigationOnClickListener {
      router.popController(this)
    }
  }

  private fun setupList() {
    val layoutManager = LinearLayoutManager(activity)
    binding.recycler.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    binding.recycler.layoutManager = layoutManager
    val itemAnimator = binding.recycler.itemAnimator as DefaultItemAnimator
    itemAnimator.supportsChangeAnimations = false
  }

  override fun onOptionsMenuClicked(bookmark: Bookmark, v: View) {
    val popup = PopupMenu(activity, v)
    popup.menuInflater.inflate(R.menu.bookmark_popup, popup.menu)
    popup.setOnMenuItemClickListener {
      when (it.itemId) {
        R.id.edit -> {
          showEditBookmarkDialog(bookmark)
          true
        }
        R.id.delete -> {
          showDeleteBookmarkDialog(bookmark)
          true
        }
        else -> false
      }
    }
    popup.show()
  }

  private fun showEditBookmarkDialog(bookmark: Bookmark) {
    EditBookmarkDialog(this, bookmark).showDialog(router)
  }

  private fun showAddBookmarkDialog() {
    AddBookmarkDialog(this).showDialog(router)
  }

  private fun showDeleteBookmarkDialog(bookmark: Bookmark) {
    DeleteBookmarkDialog(this, bookmark).showDialog(router)
  }

  companion object {

    private const val NI_BOOK_ID = "ni#bookId"

    fun newInstance(bookId: Long) = BookmarkController(Bundle().apply {
      putLong(NI_BOOK_ID, bookId)
    })
  }
}
