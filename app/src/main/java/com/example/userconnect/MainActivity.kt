package com.example.userconnect

import android.os.Bundle
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
        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(mutableListOf())
        recyclerView.adapter = userAdapter

        progressBar = findViewById(R.id.progress)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        observeViewModel()
        userViewModel.getUsers(currentPage, resultsPerPage)

        // Add scroll listener for pagination
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    currentPage++
                    userViewModel.getUsers(currentPage, resultsPerPage)
                }
            }
        })
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            // Observe loading state
            launch {
                userViewModel.loadingStateFlow.collect { isLoading ->
                    progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }

            // Observe user data
            userViewModel.userStateFlow.collect { users ->
                if (users.isNotEmpty()) {
                    userAdapter.updateData(users)
                } else {
                    Toast.makeText(this@MainActivity, "No more data to load", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
