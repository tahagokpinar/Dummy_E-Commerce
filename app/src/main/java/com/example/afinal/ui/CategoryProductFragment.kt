package com.example.afinal.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
 * Use the [CategoryProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryProductFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var categorySlug: String? = null
    private lateinit var categoryProductList: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var iDummyService: IDummyService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        categorySlug = arguments?.getString("categorySlug")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryProductList = view.findViewById(R.id.categoryProductList)
        categoryProductList.layoutManager = GridLayoutManager(context, 2)

        iDummyService = ApiClient.getClient().create(IDummyService::class.java)
        iDummyService.getProductsByCategory(categorySlug!!).enqueue(object : Callback<Products> {
            override fun onResponse(p0: Call<Products>, p1: Response<Products>) {
                if (p1.isSuccessful) {
                    val products = p1.body()
                    productAdapter = ProductAdapter(products!!.products, ::onProductClick)
                    categoryProductList.adapter = productAdapter
                }
            }

            override fun onFailure(p0: Call<Products>, p1: Throwable) {
                Log.e("fail", p1.message.toString())
            }
        })
    }

    private fun onProductClick(product: Product) {
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
         * @return A new instance of fragment CategoryProductFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CategoryProductFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}