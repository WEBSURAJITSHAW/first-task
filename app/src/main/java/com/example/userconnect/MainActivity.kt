package com.example.userconnect

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.userconnect.adapter.UserAdapter
import com.example.userconnect.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var progressBar: View

    private var currentPage = 1
    private val resultsPerPage = 10
    private var isLoading = false // To track loading state
    private var hasMoreData = true // To check if more data is available

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpUserRv()
    }
    private fun setUpUserRv() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(mutableListOf())
        recyclerView.adapter = userAdapter

        progressBar = findViewById(R.id.progress)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        observeViewModel()
        loadUsers() // Initial load

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // Trigger pagination when nearing the end of the list
                if (!isLoading && hasMoreData &&
                    (visibleItemCount + firstVisibleItemPosition >= totalItemCount - 2)) {
                    loadUsers() // Fetch more data
                }
            }
        })
    }

    private fun loadUsers() {
        isLoading = true
        userViewModel.fetchUsers()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            launch {
                userViewModel.loadingStateFlow.collect { loading ->
                    progressBar.visibility = if (loading) View.VISIBLE else View.GONE
                }
            }

            launch {
                userViewModel.userStateFlow.collect { users ->
                    if (users.size > userAdapter.itemCount) {
                        // Only append new data
                        val newUsers = users.subList(userAdapter.itemCount, users.size)
                        userAdapter.appendData(newUsers)

                        currentPage++ // Increment page only after successfully appending data
                        isLoading = false // Reset loading state
                    } else {
                        // No more data to load
                        hasMoreData = false
                    }
                }
            }
        }
    }


}
