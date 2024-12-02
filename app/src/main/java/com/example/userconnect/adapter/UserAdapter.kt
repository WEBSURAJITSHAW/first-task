package com.example.userconnect.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.userconnect.R
import com.example.userconnect.databinding.ItemUserBinding
import com.example.userconnect.models.User

class UserAdapter(private val users: MutableList<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            // Bind main data
            binding.nameTextView.text = "${user.name.title} ${user.name.first} ${user.name.last}"
            binding.emailTextView.text = user.email

            // Load profile image using Glide
            Glide.with(itemView.context)
                .load(user.picture.medium)
                .circleCrop()
                .into(binding.profileImageView)



            // Bind extra data
            binding.tvStreet.text = "Street: ${user.location?.street?.name}"
            binding.tvCity.text = "City: ${user.location?.city}"
            binding.tvState.text = "State: ${user.location?.state}"
            binding.tvCountry.text = "Country: ${user.location?.country}"

            binding.extraDataLayout.visibility = if (user.isExtraVisible) View.VISIBLE else View.GONE

            // GestureDetector for double-tap
            val gestureDetector = GestureDetector(itemView.context, object :
                GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    user.isExtraVisible = !user.isExtraVisible
                    Log.d("DoubleClick", "Visibility toggled for ${user.name.first}")
                    notifyItemChanged(adapterPosition)
                    return true
                }
            })

            itemView.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size


    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newUsers: List<User>) {
        val startPosition = users.size
        users.addAll(newUsers)
        notifyItemRangeInserted(startPosition, newUsers.size)
    }
}
