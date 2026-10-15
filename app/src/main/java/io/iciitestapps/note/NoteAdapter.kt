package io.iciitestapps.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.iciitestapps.note.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAdapter(
    private val onClick: (Note) -> Unit,
    private val onLongClick: (Note) -> Unit
) : ListAdapter<Note, NoteAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val note = getItem(position)
        holder.binding.noteTitle.text = note.title
        holder.binding.noteContent.text = note.content
        holder.binding.noteDate.text = DATE.format(Date(note.updatedAt))
        holder.binding.root.setOnClickListener { onClick(note) }
        holder.binding.root.setOnLongClickListener { onLongClick(note); true }
    }

    fun submit(notes: List<Note>) = submitList(notes)

    companion object {
        private val DATE = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())

        private val DIFF = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(a: Note, b: Note) = a.id == b.id
            override fun areContentsTheSame(a: Note, b: Note) = a == b
        }
    }
}