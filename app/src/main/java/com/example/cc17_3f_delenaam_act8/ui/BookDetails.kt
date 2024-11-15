package com.example.cc17_3f_delenaam_act8.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.cc17_3f_delenaam_act8.R
import com.example.cc17_3f_delenaam_act8.data.model.Book
import com.example.cc17_3f_delenaam_act8.databinding.BottomViewBinding
import com.example.cc17_3f_delenaam_act8.utility.ensureHttpsImageUrl
import com.example.cc17_3f_delenaam_act8.utility.fromHtml
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import coil.load

class BookDetails : BottomSheetDialogFragment() {

    private var _binding: BottomViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Book>(ARG_BOOK)?.let { book ->
        }
    }

    private fun setupBookDetails(book: Book) {
        val secureBook = book.ensureHttpsImageUrl()
        with(binding) {
            bookDetailTitleTextView.text = book.volumeInfo.title
            bookDetailAuthorsTextView.text = book.volumeInfo.authors?.joinToString(", ")

            // Publisher and date
            val publisherText = buildString {
                book.volumeInfo.publisher?.let { append(it) }
                book.volumeInfo.publishedDate?.let {
                    if (isNotEmpty()) append(" • ")
                    append(it.split("-")[0]) // Show only the year
                }
            }
            bookDetailPublisherTextView.text = publisherText
            bookDetailPublisherTextView.isVisible = publisherText.isNotEmpty()

            // Description with HTML formatting
            book.volumeInfo.description?.let { description ->
                bookDetailDescriptionTextView.text = description.fromHtml()
            }
            bookDetailDescriptionLabel.isVisible = !book.volumeInfo.description.isNullOrEmpty()
            bookDetailDescriptionTextView.isVisible = !book.volumeInfo.description.isNullOrEmpty()

            // Categories as chips with improved styling
            categoriesChipGroup.removeAllViews()
            book.volumeInfo.categories?.forEach { category ->
                val chip = Chip(requireContext()).apply {
                    text = category
                    setChipBackgroundColorResource(R.color.surface_variant)
                    setTextColor(resources.getColor(R.color.on_surface_variant, null))
                    textSize = 14f
                    isClickable = false
                    setEnsureMinTouchTargetSize(false)
                    chipMinHeight = resources.getDimensionPixelSize(R.dimen.chip_min_height).toFloat()
                    chipStartPadding = 12f
                    chipEndPadding = 12f
                }
                categoriesChipGroup.addView(chip)
            }
            categoriesScrollView.isVisible = !book.volumeInfo.categories.isNullOrEmpty()

            // Book cover
            bookDetailCoverImageView.load(secureBook.volumeInfo.imageLinks?.thumbnail) {
                crossfade(true)
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_BOOK = "book"

        fun newInstance(book: Book) = BookDetails().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_BOOK, book)
            }
        }
    }
}