package denis.musicplayer.ui.playlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import denis.musicplayer.R
import denis.musicplayer.data.media.model.Track
import denis.musicplayer.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_playlist.*
import org.jetbrains.anko.toast
import javax.inject.Inject

/**
 * Created by denis on 04/01/2018.
 */
class PlaylistActivity : BaseActivity(), PlaylistMvpView, PlaylistAdapter.Callback {
    companion object {
        val CODE_PLAYLIST_DELETED = 48
        val KEY_DELETED = "key_deleted"

        val KEY_ID = "key_id"
        val KEY_TITLE = "key_title"


        fun getStartIntent(context: Context): Intent = Intent(context, PlaylistActivity::class.java)
    }

    @Inject lateinit var presenter: PlaylistMvpPresenter<PlaylistMvpView>

    @Inject lateinit var layoutManager: LinearLayoutManager

    @Inject lateinit var adapter: PlaylistAdapter

    var id = 0L
    var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        activityComponent.inject(this)
        presenter.onAttach(this)

        setUp()
    }

    override fun setUp() {
        getArguments()
        setButtons()
        setRecyclerView()
    }

    private fun setButtons() {
        back.setOnClickListener {
            finish()
        }

        delete.setOnClickListener {
            presenter.deletePlaylist(id)
        }
    }

    private fun getArguments() {
        id = intent.extras?.getLong(KEY_ID) ?: 0
        title = intent.extras?.getString(KEY_TITLE) ?: ""

        categoryTitle.text = title

        presenter.getTracks(id)
    }

    private fun setRecyclerView() {
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        adapter.callback = this
        adapter.setItemTouchHelper(recyclerView)
    }

    override fun onPlaylistDeleted() {
        toast(getString(R.string.playlist_deleted, title))

        val intent = Intent()
        intent.putExtra(KEY_DELETED, true)
        setResult(48, intent)

        finish()
    }

    override fun updateArray(array: ArrayList<Track>) {
        adapter.updateArray(array)
    }

    override fun onItemMove(oldPos: Int, newPos: Int) {
        presenter.onItemMove(id, oldPos, newPos)
    }

    override fun onItemSwipe(trackId: Long) {
        presenter.onItemSwipe(id, trackId)
    }
}

