package io.iciitestapps.note

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.iciitestapps.note.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NoteAdapter
    private var allNotes: List<Note> = emptyList()
    private var query: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        adapter = NoteAdapter(::openNote, ::confirmDelete)
        binding.recycler.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recycler.adapter = adapter

        binding.fab.setOnClickListener { openNote(null) }

        lifecycleScope.launch {
            NoteDatabase.get(this@MainActivity).noteDao().observeAll().collectLatest {
                allNotes = it
                applyFilter()
            }
        }
    }

    private fun applyFilter() {
        val filtered = if (query.isBlank()) allNotes else allNotes.filter {
            it.title.contains(query, true) || it.content.contains(query, true)
        }
        adapter.submit(filtered)
        val empty = filtered.isEmpty()
        binding.empty.visibility = if (empty) View.VISIBLE else View.GONE
        binding.recycler.visibility = if (empty) View.GONE else View.VISIBLE
    }

    private fun openNote(note: Note?) {
        val intent = Intent(this, EditNoteActivity::class.java)
        if (note != null) intent.putExtra(EditNoteActivity.EXTRA_ID, note.id)
        startActivity(intent)
    }

    private fun confirmDelete(note: Note) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(R.string.delete_title)
            .setMessage(getString(R.string.delete_message, note.title))
            .setPositiveButton(R.string.delete) { _, _ ->
                lifecycleScope.launch {
                    NoteDatabase.get(this@MainActivity).noteDao().delete(note)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val item = menu.findItem(R.id.action_search)
        val view = item.actionView as SearchView
        view.queryHint = getString(R.string.search_hint)
        view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = false
            override fun onQueryTextChange(q: String?): Boolean {
                query = q.orEmpty()
                applyFilter()
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = super.onOptionsItemSelected(item)
}