package com.example.afinal.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.R
import com.example.afinal.adapters.CategoryAdapter
import com.example.afinal.configs.ApiClient
import com.example.afinal.models.Categories
import com.example.afinal.models.CategoryList
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
 * Use the [CategoriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoriesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var iDummyService: IDummyService
    lateinit var categoryList : RecyclerView

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
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryList = view.findViewById(R.id.categoryList)
        categoryList.layoutManager = GridLayoutManager(context, 2)

        iDummyService = ApiClient.getClient().create(IDummyService::class.java)
        iDummyService.getCategories().enqueue(object : Callback<CategoryList> {
            override fun onResponse(
                p0: Call<CategoryList>,
                p1: Response<CategoryList>
            ) {
                if (p1.isSuccessful) {
                    val category = p1.body()
                    val categoryAdapter = CategoryAdapter(category!!, ::onCategoryClick)
                    categoryList.adapter = categoryAdapter
                }
            }

            override fun onFailure(p0: Call<CategoryList>, p1: Throwable) {
                Log.e("fail", p1.message.toString())
            }

        })
    }

    fun onCategoryClick(category: Categories) {
        val categorySlug = category.slug
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val categoryProductFragment = CategoryProductFragment.newInstance("", "")
        val bundle = Bundle()
        bundle.putString("categorySlug", categorySlug)
        categoryProductFragment.arguments = bundle
        fragmentTransaction.replace(R.id.nav_content, categoryProductFragment)
        fragmentTransaction.commit()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CategoriesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CategoriesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}