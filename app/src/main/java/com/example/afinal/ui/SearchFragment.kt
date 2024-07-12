package com.example.afinal.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.DetailActivity
import com.example.afinal.R
import com.example.afinal.adapters.ProductAdapter
import com.example.afinal.configs.ApiClient
import com.example.afinal.models.Product
import com.example.afinal.models.Products
import com.example.afinal.services.IDummyService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var iDummyService: IDummyService
    lateinit var searchList : RecyclerView
    lateinit var searchText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchList = view.findViewById(R.id.searchList)
        searchText = view.findViewById(R.id.editTextSearch)

        searchList.layoutManager = GridLayoutManager(context, 2)

        iDummyService = ApiClient.getClient().create(IDummyService::class.java)

        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    searchProduct(query)
                } else {
                    searchList.adapter = null
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        searchText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val query = searchText.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchProduct(query)
                    hideKeyboard()
                }
                true
            }else
                false
        }
    }

    private fun searchProduct(query: String) {
        iDummyService.getProductsBySearch(query).enqueue(object : Callback<Products> {
            override fun onResponse(p0: Call<Products>, p1: Response<Products>) {
                if (p1.isSuccessful) {
                    val products = p1.body()
                    val productAdapter = ProductAdapter(products!!.products, ::onProductClick)
                    searchList.adapter = productAdapter
                    Log.d("search", products.products.toString())
                }
            }

            override fun onFailure(p0: Call<Products>, p1: Throwable) {
                Log.e("fail", p1.message.toString())
            }

        })
    }

    private fun hideKeyboard(){
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun onProductClick(product: Product){
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}