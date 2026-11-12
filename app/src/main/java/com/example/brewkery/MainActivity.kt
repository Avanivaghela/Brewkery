package com.example.brewkery

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.brewkery.ui.cart.CartFragment
import com.example.brewkery.ui.detail.ItemDetailFragment
import com.example.brewkery.ui.menu.MenuFragment
import com.example.brewkery.ui.status.OrderStatusFragment

class MainActivity : AppCompatActivity(),
    MenuFragment.MenuNavigationListener,
    CartFragment.CartNavigationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, MenuFragment())
                .commit()
        }
    }

    override fun navigateToDetail() {
        replaceFragment(ItemDetailFragment(), "detail")
    }

    override fun navigateToCart() {
        replaceFragment(CartFragment(), "cart")
    }

    override fun navigateToOrderStatus() {
        replaceFragment(OrderStatusFragment(), "order_status")
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.nav_host_fragment, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }
}
