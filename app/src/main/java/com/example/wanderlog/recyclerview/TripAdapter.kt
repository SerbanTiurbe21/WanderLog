package com.example.wanderlog.recyclerview

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlog.R
import com.example.wanderlog.database.models.Trip
import com.example.wanderlog.utils.DoubleClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException

class TripAdapter(private var tripList: Set<Trip>, private val tripUpdateListener: TripUpdateListener) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {
    interface TripUpdateListener {
        fun onTripUpdate(trip: Trip)
    }
    class TripViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewTrip: ImageView = view.findViewById(R.id.imageViewTrip)
        val textViewTripName: TextView = view.findViewById(R.id.textViewTripName)
        val textViewRatingScore: TextView = view.findViewById(R.id.textViewRatingScore)
        val linearLayoutStarRating: LinearLayout = view.findViewById(R.id.linearlayoutStarRating)
        val textViewOriginalPrice: TextView = view.findViewById(R.id.textViewOriginalPrice)
        val textViewDiscountedPrice: TextView = view.findViewById(R.id.textViewDiscountedPrice)
        val imageViewBookmark: ImageView = view.findViewById(R.id.imageViewBookmark)
        val frameLayoutBookmark: FrameLayout = view.findViewById(R.id.frameLayoutBookmark)
        val textViewInfoDetails: TextView = view.findViewById(R.id.textViewInfoDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = tripList.elementAt(position)

        holder.textViewTripName.text = trip.tripName
        holder.textViewRatingScore.text = trip.rating.toString()


        bindStars(holder.linearLayoutStarRating, trip.rating)


        holder.textViewInfoDetails.apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }

        holder.imageViewTrip.loadImageAsync(holder.imageViewTrip.context, trip.photoUri)

        holder.textViewOriginalPrice.apply {
            text = "${trip.price.toInt()}$"
            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        holder.textViewDiscountedPrice.text = "${(trip.price * 0.8).toInt()}$"

        holder.imageViewBookmark.setImageResource(
            if (trip.isFavourite) R.drawable.favoriteicon
            else R.drawable.bookmark
        )
        val doubleClickListener = DoubleClickListener {
            trip.isFavourite = !trip.isFavourite
            if (trip.isFavourite) {
                holder.imageViewBookmark.setImageResource(R.drawable.favoriteicon)
            } else {
                holder.imageViewBookmark.setImageResource(R.drawable.bookmark)
            }
            tripUpdateListener.onTripUpdate(trip)
        }
        holder.frameLayoutBookmark.setOnClickListener(doubleClickListener)
        holder.textViewInfoDetails.setOnClickListener{
            val bundle = bundleOf("tripId" to trip.id)
            it.findNavController().navigate(R.id.action_nav_home_to_nav_tripDetails, bundle)
        }

        holder.itemView.setOnLongClickListener{
            val bundle = bundleOf("tripId" to trip.id)
            it.findNavController().navigate(R.id.action_nav_home_to_nav_editTrip, bundle)
            true
        }
    }

    override fun getItemCount() = tripList.size

    private fun bindStars(starsLayout: LinearLayout, rating: Float) {
        starsLayout.removeAllViews()

        val fullStars = rating.toInt()
        val halfStars = if (rating % 1 >= 0.5) 1 else 0
        val emptyStars = 5 - (fullStars + halfStars)

        for (i in 1..fullStars) {
            starsLayout.addView(createStarImageView(starsLayout.context, R.drawable.ic_star_filled))
        }

        if (halfStars == 1) {
            starsLayout.addView(createStarImageView(starsLayout.context, R.drawable.ic_star_half))
        }

        for (i in 1..emptyStars) {
            starsLayout.addView(createStarImageView(starsLayout.context, R.drawable.ic_star_outline))
        }
    }

    private fun createStarImageView(context: Context, drawableId: Int): ImageView {
        return ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                val margin = context.resources.getDimensionPixelSize(R.dimen.star_margin)
                setMargins(margin, margin, margin, margin)
            }
            setImageResource(drawableId)

            val size = context.resources.getDimensionPixelSize(R.dimen.star_size)
            layoutParams.width = size
            layoutParams.height = size
        }
    }

    private fun ImageView.loadImageAsync(context: Context, imageUriString: String) {
        val imageView = this
        CoroutineScope(Dispatchers.IO).launch {
            val contentUri = Uri.parse(imageUriString)

            val bitmap = try {
                val imageStream = context.contentResolver.openInputStream(contentUri)
                BitmapFactory.decodeStream(imageStream)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                null
            }

            withContext(Dispatchers.Main) {
                bitmap?.let {
                    imageView.setImageBitmap(it)
                }
            }
        }
    }

    fun updateTrips(newTrips: Set<Trip>) {
        val diffCallback = TripDiffCallback(tripList, newTrips)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        tripList = newTrips
        diffResult.dispatchUpdatesTo(this)
    }

    class TripDiffCallback(private val oldList: Set<Trip>, private val newList: Set<Trip>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList.elementAt(oldItemPosition).id == newList.elementAt(newItemPosition).id
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList.elementAt(oldItemPosition) == newList.elementAt(newItemPosition)
        }
    }
}
