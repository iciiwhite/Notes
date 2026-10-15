package io.iciitestapps.note

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.iciitestapps.note.databinding.ActivityEditNoteBinding
import kotlinx.coroutines.launch

class EditNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditNoteBinding
    private var noteId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        binding.toolbar.setNavigationOnClickListener { finish() }

        noteId = intent.getLongExtra(EXTRA_ID, 0L)
        if (noteId != 0L) {
            lifecycleScope.launch {
                NoteDatabase.get(this@EditNoteActivity).noteDao().findById(noteId)?.let { n ->
                    binding.noteTitle.setText(n.title)
                    binding.noteContent.setText(n.content)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> { save(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun save() {
        val title = binding.noteTitle.text.toString().trim()
        val content = binding.noteContent.text.toString().trim()
        if (title.isEmpty() && content.isEmpty()) { finish(); return }

        lifecycleScope.launch {
            val dao = NoteDatabase.get(this@EditNoteActivity).noteDao()
            val now = System.currentTimeMillis()
            val finalTitle = title.ifEmpty { getString(R.string.untitled) }
            val note = if (noteId != 0L) {
                val existing = dao.findById(noteId)
                existing?.copy(title = finalTitle, content = content, updatedAt = now)
                    ?: Note(title = finalTitle, content = content, updatedAt = now)
            } else {
                Note(title = finalTitle, content = content, updatedAt = now)
            }
            dao.upsert(note)
            finish()
        }
    }

    companion object {
        const val EXTRA_ID = "note_id"
    }
}