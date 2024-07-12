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
import com.example.afinal.adapters.CartAdapter
import com.example.afinal.configs.ApiClient
import com.example.afinal.models.Carts
import com.example.afinal.services.IDummyService
import com.example.afinal.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrdersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrdersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerView: RecyclerView
    lateinit var cartsAdapter: CartAdapter
    lateinit var iDummyService: IDummyService
    lateinit var sharedPreferences: SharedPref


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
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = SharedPref(requireContext())

        recyclerView = view.findViewById(R.id.cartList)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        val user = sharedPreferences.getUser()
        val userId = user?.id

        iDummyService = ApiClient.getClient().create(IDummyService::class.java)
        if (userId != null) {
            iDummyService.getCarts(userId).enqueue(object : Callback<Carts> {
                override fun onResponse(p0: Call<Carts>, p1: Response<Carts>) {
                    if (p1.isSuccessful) {
                        val carts = p1.body()
                        if (carts != null) {
                            cartsAdapter = CartAdapter(carts.carts)
                            recyclerView.adapter = cartsAdapter
                            Log.d("Carts", "Carts: $carts")
                        }
                    }
                }

                override fun onFailure(p0: Call<Carts>, p1: Throwable) {
                    Log.e("Carts", "Failed to get carts", p1)
                }
            })
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrdersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrdersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}