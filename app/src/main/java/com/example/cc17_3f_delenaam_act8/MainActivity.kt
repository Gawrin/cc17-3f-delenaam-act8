package com.example.cc17_3f_delenaam_act8

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cc17_3f_delenaam_act8.data.model.Book
import com.example.cc17_3f_delenaam_act8.data.repository.BooksRepository
import com.example.cc17_3f_delenaam_act8.databinding.ActivityMainBinding
import com.example.cc17_3f_delenaam_act8.ui.BookAdapter
import com.example.cc17_3f_delenaam_act8.ui.BookDetails
import com.example.cc17_3f_delenaam_act8.ui.BookViewModel
import com.example.cc17_3f_delenaam_act8.ui.BookViewModelFactory
import com.google.android.material.snackbar.Snackbar
import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: BookViewModel by viewModels {
        BookViewModelFactory(BooksRepository())
    }
    private val bookAdapter = BookAdapter { book ->
        showBookDetails(book)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.booksRecyclerView.apply {
            // Adjust grid columns based on screen size and orientation
            val spanCount = when {
                resources.configuration.screenWidthDp >= 840 -> 4
                resources.configuration.screenWidthDp >= 600 -> 3
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE -> 3
                else -> 3 // Phones portrait
            }

            layoutManager = GridLayoutManager(this@MainActivity, spanCount)
            adapter = bookAdapter

            // Add item decoration for consistent spacing
            if (itemDecorationCount == 0) {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
                        val position = parent.getChildAdapterPosition(view)
                        val column = position % spanCount

                        outRect.left = spacing - column * spacing / spanCount
                        outRect.right = (column + 1) * spacing / spanCount
                        if (position < spanCount) outRect.top = spacing
                        outRect.bottom = spacing
                    }
                })
            }
        }
    }


    private fun observeViewModel() {
        viewModel.books.observe(this) { books ->
            bookAdapter.submitList(books)
            if (books.isEmpty() && !viewModel.isLoading.value!!) {
                showMessage(getString(R.string.no_results))
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.networkStatusCard?.apply {
                isVisible = isLoading
                if (isLoading) {
                    animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(300)
                        .start()
                } else {
                    animate()
                        .alpha(0f)
                        .translationY(-50f)
                        .setDuration(200)
                        .start()
                }
            }


        }

    }

    private fun showBookDetails(book: Book) {
        BookDetails.newInstance(book)
            .show(supportFragmentManager, "BookDetailsBottomSheet")
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setupRecyclerView()
    }
}