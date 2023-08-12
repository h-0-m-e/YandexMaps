package ru.netology.yandexmaps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.databinding.PointItemBinding
import ru.netology.yandexmaps.dto.Point
import ru.netology.yandexmaps.listener.OnInteractionListener

class PointsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Point, PointsAdapter.PointsViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointsViewHolder {
        val binding = PointItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val holder = PointsViewHolder(binding)

        with(binding) {
            root.setOnClickListener {
                val point = getItem(holder.adapterPosition)
                onInteractionListener.onClick(point)
            }
            menu.setOnClickListener {
                PopupMenu(root.context, it).apply {
                    inflate(R.menu.point_menu)

                    setOnMenuItemClickListener { item ->
                        val point = getItem(holder.adapterPosition)
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(point)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(point)
                                true
                            }
                            else -> false
                        }
                    }

                    show()
                }
            }
        }

        return holder
    }


    override fun onBindViewHolder(holder: PointsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PointsViewHolder(
        private val binding: PointItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(point: Point) {
            with(binding) {
                title.text = point.title
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Point>() {
        override fun areItemsTheSame(oldItem: Point, newItem: Point): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Point, newItem: Point): Boolean =
            oldItem == newItem
    }
}
