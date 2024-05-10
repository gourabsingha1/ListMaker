package com.example.listmaker

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listmaker.adapter.ItemListAdapter
import com.example.listmaker.databinding.ActivityHomeBinding
import com.example.listmaker.model.ItemList
import com.example.listmaker.model.ItemListWithItems
import com.example.listmaker.viewmodel.ItemListTrashViewModel
import com.example.listmaker.viewmodel.ItemListViewModel
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity(), ItemListAdapter.ItemListClickUpdateInterface {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var itemListAdapter: ItemListAdapter
    private lateinit var itemListViewModel: ItemListViewModel
    private lateinit var itemListTrashViewModel: ItemListTrashViewModel
    private lateinit var navDrawer : NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigationDrawer()
        setAdapter()
        setRecyclerView()
        setViewModel()
        searchQuery()
        setSearchBarMenu()

        // Make a list
        binding.btnMakeList.setOnClickListener {
            startActivity(Intent(this, MakeListActivity::class.java))
        }
    }

    // Set Adapter
    private fun setAdapter() {
        itemListAdapter = ItemListAdapter(
            this,
            onDeleteItemList = { itemListWithItems ->
                // Move to Trash
                itemListTrashViewModel.upsertItemList(itemListWithItems.itemList!!)
                itemListWithItems.items?.forEach { item ->
                    itemListTrashViewModel.upsertItem(item)
                }
                // Delete from HomeActivity
                itemListViewModel.deleteItemList(itemListWithItems.itemList)
                itemListWithItems.items?.forEach { item ->
                    itemListViewModel.deleteItem(item)
                }
            },
            showMenuDeleteSelected = { show ->
                showMenuDeleteSelected(show)
            })
    }

    // Set RecyclerView
    private fun setRecyclerView() {
        binding.rvHome.apply {
            adapter = itemListAdapter
            layoutManager = LinearLayoutManager(this@HomeActivity)
        }
    }

    // Set ViewModel
    private fun setViewModel() {
        itemListTrashViewModel = ViewModelProvider(this)[ItemListTrashViewModel::class.java]
        itemListViewModel = ViewModelProvider(this)[ItemListViewModel::class.java]
        itemListViewModel.itemListsWithItems.observe(this) { itemListsWithItems ->
            // Update ItemListsWithItems
            itemListAdapter.updateItemListsWithItems(itemListsWithItems)

            // Search query
            itemListViewModel.searchQuery.observe(this) { query ->
                val filteredItems = itemListsWithItems.filter { it.itemList?.name?.contains(query, ignoreCase = true)!! }
                itemListAdapter.updateItemListsWithItems(filteredItems)

                // Filter based on item name
//                val filteredItemLists = itemListsWithItems.mapNotNull { itemListWithItems ->
//                    // Filter the items based on the query
//                    val filteredItems =
//                        itemListWithItems.items?.filter {it.name?.contains(query, ignoreCase = true)!!}
//                    // If the itemList name matches the query or there are any matching items, include the itemList in the results
//                    if (itemListWithItems.itemList?.name?.contains(query, ignoreCase = true)!!
//                        || filteredItems?.isNotEmpty()!!) {
//                        val x = arrayListOf<Item>()
//                        filteredItems?.forEach {
//                            x.add(it)
//                        }
//                        ItemListWithItems(itemListWithItems.itemList, x)
//                    } else {
//                        null
//                    }
//                }
//                itemListAdapter.updateItemListsWithItems(filteredItemLists)
            }
        }
    }

    // Navigation Drawer
    private fun navigationDrawer() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navDrawer = findViewById<NavigationView>(R.id.navDrawer)
        navDrawer.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.navLists -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.navTrash -> startActivity(Intent(this, TrashActivity::class.java))
            }
            this.finish()
            true
        }

        // Highlight Menu Item (Lists)
//        navDrawer.menu.findItem(R.id.navLists)?.let { menuItem ->
//            menuItem.actionView?.background = ColorDrawable(ContextCompat.getColor(this, R.color.black))
//        }

        // Open navigation on clicking navigation icon in search bar
        binding.sbHomeSearch.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(navDrawer)
        }
    }
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(navDrawer)) {
            binding.drawerLayout.closeDrawer(navDrawer)
        }
        else {
            super.onBackPressed()
        }
    }

    // Searchbar Menu
    private fun setSearchBarMenu() {
        binding.sbHomeSearch.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                // Empty Lists
                R.id.menuHomeEmptyLists -> {
                    itemListViewModel.itemListsWithItems.observe(this) { itemListsWithItems ->
                        itemListsWithItems.forEach { itemListWithItems ->
                            // Move to Trash
                            itemListTrashViewModel.upsertItemList(itemListWithItems.itemList!!)
                            itemListWithItems.items?.forEach { item ->
                                itemListTrashViewModel.upsertItem(item)
                            }
                            // Delete from HomeActivity
                            itemListViewModel.deleteItemList(itemListWithItems.itemList)
                            itemListWithItems.items?.forEach { item ->
                                itemListViewModel.deleteItem(item)
                            }
                        }
                    }
                    true
                }
                // Delete selected
                R.id.menuHomeDeleteSelected -> {
                    itemListAdapter.deleteSelected()
                    showMenuDeleteSelected(false)
                    true
                }
                // Account
                R.id.menuHomeAccount -> {
                    Toast.makeText(this, "Account", Toast.LENGTH_SHORT).show()
                    true                }
                else -> super.onOptionsItemSelected(item)
            }
        }
    }

    // Search query
    private fun searchQuery() {
        binding.svHomeSearch.editText.setOnEditorActionListener { v, _, _ ->
            val text = v.text.toString()
            binding.sbHomeSearch.setText(text)
            itemListViewModel.setSearchQuery(text)
            binding.svHomeSearch.hide()
            true
        }
    }

    // Show delete menu
    private fun showMenuDeleteSelected(show: Boolean) {
        binding.sbHomeSearch.menu.findItem(R.id.menuHomeDeleteSelected).isVisible = show
    }

    override fun onItemListClick(position: Int) {
        Intent(this, ItemListActivity::class.java).also {
            it.putExtra("EXTRA_POSITION", position)
            val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_bottom, R.anim.slide_out_top).toBundle()
            startActivity(it, options)
        }
    }
}