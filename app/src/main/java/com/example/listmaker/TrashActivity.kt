package com.example.listmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listmaker.adapter.ItemListTrashAdapter
import com.example.listmaker.databinding.ActivityTrashBinding
import com.example.listmaker.viewmodel.ItemListTrashViewModel
import com.example.listmaker.viewmodel.ItemListViewModel
import com.google.android.material.navigation.NavigationView
class TrashActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrashBinding
    private lateinit var itemListTrashAdapter: ItemListTrashAdapter
    private lateinit var itemListTrashViewModel: ItemListTrashViewModel
    private lateinit var itemListViewModel: ItemListViewModel
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navDrawer: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set activity title
        setSupportActionBar(binding.toolbarTrash)

        // Initialize Adapter
        itemListTrashAdapter = ItemListTrashAdapter(
            onRecoverItemList = { itemListWithItems ->
                // Send itemList to HomeActivity
                itemListViewModel.upsertItemList(itemListWithItems.itemList!!)
                itemListWithItems.items?.forEach { item ->
                    itemListViewModel.upsertItem(item)
                }
                // Delete itemList from Trash
                itemListTrashViewModel.deleteItemList(itemListWithItems.itemList)
                itemListWithItems.items?.forEach { item ->
                    itemListTrashViewModel.deleteItem(item)
                }
                // Show No List In Trash Image
                showNoListInTrashImage()
            },
            onDeleteItemList = { itemListWithItems ->
                // Delete itemList from Trash
                itemListTrashViewModel.deleteItemList(itemListWithItems.itemList!!)
                // Show No List In Trash Image
                showNoListInTrashImage()
            }
        )

        // RecyclerView
        val rvLayoutManager = binding.rvTrash.layoutManager as LinearLayoutManager?
        val position = rvLayoutManager?.findFirstVisibleItemPosition() // So that the screen doesn't scroll up, everytime i delete something.
        binding.rvTrash.apply {
            adapter = itemListTrashAdapter
            layoutManager = LinearLayoutManager(this@TrashActivity)
        }
        rvLayoutManager?.scrollToPosition(position ?: 0)

        // ViewModel
        itemListViewModel = ViewModelProvider(this)[ItemListViewModel::class.java]
        itemListTrashViewModel = ViewModelProvider(this)[ItemListTrashViewModel::class.java]
        itemListTrashViewModel.itemListsWithItems.observe(this) { itemListsWithItems ->
            itemListTrashAdapter.updateItemListsWithItems(itemListsWithItems)
        }

        // Navigation Drawer
        navigationDrawer()
    }

    // Navigation Drawer
    private fun navigationDrawer() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayoutTrash)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        // when toggle is opened and back is pressed, the navigation bar will close
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navDrawer = findViewById<NavigationView>(R.id.navDrawer)
        navDrawer.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navTrash -> startActivity(Intent(this, TrashActivity::class.java))
                R.id.navLists -> startActivity(Intent(this, HomeActivity::class.java))
            }
            this.finish()
            true
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        // Empty Trash
        return when (item.itemId) {
            R.id.menuTrashEmptyTrash -> {
                itemListTrashViewModel.itemListsWithItems.observe(this) { itemListsWithItems ->
                    itemListsWithItems.forEach { itemListWithItem ->
                        itemListTrashViewModel.deleteItemList(itemListWithItem.itemList!!)
                        itemListWithItem.items?.forEach { item ->
                            itemListTrashViewModel.deleteItem(item)
                        }
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Empty Trash menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.trash_menu, menu)
        return true
    }

    // Navigation Drawer closes onBackPressed
    override fun onBackPressed() {
        if (binding.drawerLayoutTrash.isDrawerOpen(navDrawer)) {
            binding.drawerLayoutTrash.closeDrawer(navDrawer)
        } else {
            super.onBackPressed()
        }
    }

    // Show No List In Trash Image
    private fun showNoListInTrashImage() {
        if(itemListTrashAdapter.itemCount == 0) {
            binding.ivNoListInTrash.visibility = View.VISIBLE
        }
        else {
            binding.ivNoListInTrash.visibility = View.GONE
        }
    }

}

