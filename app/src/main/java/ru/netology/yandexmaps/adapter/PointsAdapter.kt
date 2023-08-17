package ru.netology.yandexmaps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.databinding.PointItemBinding
import ru.netology.yandexmaps.dto.Point
import ru.netology.yandexmaps.listener.OnInteractionListener
import ru.netology.yandexmaps.types.PointType

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
                val currentPoint = getItem(holder.adapterPosition)
                onInteractionListener.onClick(currentPoint)
            }

            menu.setOnClickListener {
                PopupMenu(root.context, it).apply {
                    inflate(R.menu.point_menu)

                    setOnMenuItemClickListener { item ->
                        val currentPoint = getItem(holder.adapterPosition)
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(currentPoint)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(currentPoint)
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
                desc.text = point.description
                type.setImageResource(
                    when (point.pointType) {
                        PointType.DEFAULT -> R.drawable.location_24
                        PointType.ATM -> R.drawable.atm_24
                        PointType.BUS -> R.drawable.bus_24
                        PointType.CAR -> R.drawable.car_24
                        PointType.DINING -> R.drawable.dining_24
                        PointType.HOME -> R.drawable.home_24
                        PointType.SLEEP -> R.drawable.sleep_24
                        PointType.STORE -> R.drawable.store_24
                    }
                )
                descButton.setOnClickListener {
                    desc.isVisible = !desc.isVisible
                }
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
